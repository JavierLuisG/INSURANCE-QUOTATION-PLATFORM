---
id: SPEC-002
status: IMPLEMENTED
feature: ep-003-ft-019-core-folios
created: 2026-04-25
updated: 2026-04-25
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-001
---

# Spec: FT-019 — Generación de Folios de Cotización (`plataforma-core-ohs`)

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature expone el endpoint `GET /v1/folios` en el mock server `plataforma-core-ohs`, el cual genera folios únicos y secuenciales para cotizaciones de seguros de daños. Cada llamada produce un nuevo folio con formato `COT-AAAA-NNNNNN`, garantizando atomicidad mediante un contador MongoDB con `$inc`. La secuencia se reinicia por año.

### Requerimiento de Negocio

El cotizador de seguros de daños requiere folios únicos e irrepetibles para identificar cada cotización. El folio debe seguir el formato estándar `COT-AAAA-NNNNNN` (ej. `COT-2026-000001`), donde `AAAA` es el año de emisión y `NNNNNN` es un número secuencial con cero padding. El servicio debe garantizar que dos cotizaciones simultáneas nunca reciban el mismo folio.

### Historias de Usuario

#### HU-01: Generación atómica de folio de cotización (HU-095)

```
Como:        Sistema cotizador de seguros de daños
Quiero:      Obtener un folio único y secuencial al iniciar una cotización
Para:        Identificar de forma inequívoca cada cotización en el sistema

Prioridad:   Alta
Estimación:  XS (1 story point)
Dependencias: SPEC-001 (mock server base operativo)
Capa:        Backend (Node.js/Express — plataforma-core-ohs)
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Generación exitosa del primer folio del año
  Dado que:  el mock server está activo y no existen folios para el año actual
  Cuando:    el cotizador hace GET /v1/folios
  Entonces:  recibe HTTP 200 con body { "folio": "COT-2026-000001" }
             y el contador en MongoDB se inicializa en seq=1
```

**Happy Path**
```gherkin
CRITERIO-1.2: Folio incrementa en cada llamada sucesiva
  Dado que:  ya existe un contador con seq=N para el año actual
  Cuando:    el cotizador hace GET /v1/folios
  Entonces:  recibe HTTP 200 con body { "folio": "COT-2026-0000NN+1" }
             y el número de secuencia es estrictamente mayor que el anterior
```

**Happy Path**
```gherkin
CRITERIO-1.3: Formato de folio siempre válido
  Dado que:  el mock server está activo
  Cuando:    el cotizador hace GET /v1/folios en cualquier momento del año
  Entonces:  el campo "folio" cumple el patrón /^COT-\d{4}-\d{6}$/
```

**Error Path**
```gherkin
CRITERIO-1.4: Error interno de base de datos
  Dado que:  la base de datos MongoDB no está disponible
  Cuando:    el cotizador hace GET /v1/folios
  Entonces:  recibe HTTP 500 con body { "message": "Error interno del servidor", "code": "INTERNAL_ERROR" }
```

**Edge Case**
```gherkin
CRITERIO-1.5: Padding de secuencia con ceros
  Dado que:  el contador tiene seq=1
  Cuando:    el cotizador hace GET /v1/folios
  Entonces:  el folio tiene exactamente 6 dígitos en la parte numérica: "000001"
```

### Reglas de Negocio

1. **Formato obligatorio**: El folio DEBE seguir el patrón `COT-AAAA-NNNNNN` donde `AAAA` = año actual (4 dígitos) y `NNNNNN` = secuencia con zero-padding a 6 dígitos.
2. **Atomicidad**: La generación usa `findOneAndUpdate` con `$inc` y `upsert: true` para garantizar que dos peticiones concurrentes nunca obtengan el mismo número de secuencia.
3. **Secuencia anual**: El contador se segmenta por año (`cotizacion-AAAA`), reiniciando la secuencia cada año calendario.
4. **Sin autenticación**: El endpoint es de uso interno entre servicios del sistema y no requiere token JWT.
5. **Idempotencia de upsert**: Si no existe contador para el año actual, se crea automáticamente con `seq=1` en la primera llamada.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `FolioCounter` | colección `folioCounters` | nueva | Contador atómico de folios por año |

#### Campos del modelo — `FolioCounter`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `_id` | string | sí | formato `cotizacion-AAAA` | Clave primaria compuesta (tipo cotización + año) |
| `seq` | number | sí | entero ≥ 1, default 0 | Valor del contador secuencial |

#### Índices / Constraints

- `_id` es la clave primaria: garantiza unicidad del contador por año y tipo de cotización.
- No se requieren índices adicionales — el acceso es siempre por `_id` exacto.

### API Endpoints

#### GET /v1/folios

- **Descripción**: Genera y retorna un folio único y secuencial para una nueva cotización
- **Auth requerida**: no
- **Request Body**: ninguno
- **Query params**: ninguno
- **Response 200**:
  ```json
  {
    "folio": "COT-2026-000001"
  }
  ```
- **Response 500**: error al acceder a MongoDB
  ```json
  {
    "message": "Error interno del servidor",
    "code": "INTERNAL_ERROR"
  }
  ```

#### Operación MongoDB subyacente

```js
FolioCounter.findOneAndUpdate(
  { _id: `cotizacion-${year}` },   // filtro por año
  { $inc: { seq: 1 } },            // incremento atómico
  { new: true, upsert: true }      // retorna doc actualizado; crea si no existe
)
```

### Diseño Frontend

No aplica — este feature es exclusivamente de backend (API interna entre servicios).

### Arquitectura y Dependencias

- **Módulo**: `plataforma-core-ohs` (mock server Node.js/Express)
- **Archivo**: `src/routes/folios.js`
- **Registro en punto de entrada**: `app.use('/v1', foliosRouter)` en `src/index.js`
- **Dependencias**: `mongoose ^8.23.0` (ya presente en `package.json`)
- **Servicios externos**: MongoDB (`folioCounters` collection)
- **No requiere** el interceptor `mockScenarioInterceptor` — la generación de folio es determinista y no se simula con escenarios de error controlados por base de datos.

### Notas de Implementación

- El modelo `FolioCounter` se define inline en `src/routes/folios.js` usando `mongoose.models.FolioCounter || mongoose.model(...)` para evitar errores de re-registro del modelo al reiniciar con `--watch`.
- La función `padStart(6, '0')` garantiza el formato de 6 dígitos independientemente del valor de `seq`.
- El año se toma de `new Date().getFullYear()` en cada petición — no se almacena en el modelo.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación

- [x] Crear esquema y modelo `FolioCounter` — `_id` (string), `seq` (number)
- [x] Implementar `GET /v1/folios` — operación atómica `findOneAndUpdate` con `$inc` y `upsert`
- [x] Formatear folio con `padStart(6, '0')` y patrón `COT-${year}-${sequence}`
- [x] Manejar error 500 con body `{ message, code: 'INTERNAL_ERROR' }`
- [x] Registrar router en `src/index.js` bajo `/v1`

#### Tests Backend

- [x] `test_folio_generation_sequential` — happy path, formato válido `/^COT-\d{4}-\d{6}$/`
- [x] `test_folio_increments_on_second_call` — folios distintos en llamadas consecutivas, valores `COT-YEAR-000001` y `COT-YEAR-000002`
- [x] `test_folio_db_error_returns_500` — cuando MongoDB falla, responde 500 con `INTERNAL_ERROR`
- [x] `test_folio_padding_single_digit` — seq=5 produce `000005`
- [x] `test_folio_padding_large_number` — seq=123456 produce `123456`

### Frontend

No aplica.

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 al 1.5
- [ ] Ejecutar skill `/risk-identifier` → clasificación de riesgo por concurrencia en generación de folios
- [ ] Verificar cobertura de tests ≥ 80% en `src/routes/folios.js`
- [ ] Prueba de concurrencia manual: 10 peticiones simultáneas → 10 folios distintos
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
