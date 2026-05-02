---
id: SPEC-013
status: APPROVED
feature: ep-002-ft-011-core-validacion-reglas
created: 2026-05-02
updated: 2026-05-02
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-010  # ep-002-ft-010-core-parametros-calculo (rangos de suma asegurada y zona desde parámetros)
  - SPEC-011  # ep-001-ft-001-core-datos-generales (entidad Cotizacion, folio)
  - SPEC-004  # ep-003-ft-016-core-codigos-postales (CatalogoCPZonas, ParametroCalculoService.obtenerZonaPorCP)
---

# Spec: EP-002 FT-011 — Motor de Validación de Reglas de Negocio

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa el motor de validación de reglas de negocio del EP-002 (Motor de Cálculo). Actúa como pre-condición obligatoria antes de ejecutar cualquier cálculo de primas: valida que las sumas aseguradas de cada ubicación estén dentro de los rangos permitidos, verifica que los códigos postales existan en el catálogo y asigna la zona geográfica correspondiente, comprueba que cada ubicación tenga todos los campos mínimos requeridos, genera mensajes de error estructurados y claros para el usuario, y bloquea el cálculo cuando existen errores activos. El motor opera sobre una nueva entidad `UbicacionRiesgo` vinculada a la `Cotizacion`.

### Requerimiento de Negocio

El motor de cálculo del EP-002 requiere que todos los datos de entrada sean válidos, completos y coherentes con las políticas de suscripción antes de ejecutar cualquier cálculo de primas. Sin esta capa de validación, los cálculos producirían resultados incorrectos o ilegales, generando pérdidas financieras, incumplimiento normativo y cotizaciones inviables. La FT-011 resuelve la necesidad de:
1. Validar rangos de suma asegurada por tipo de riesgo y zona geográfica (HU-049).
2. Verificar y clasificar códigos postales contra el catálogo centralizado (HU-050).
3. Asegurar que todas las ubicaciones tienen los datos mínimos para el cálculo (HU-051).
4. Reportar errores de forma clara y accionable al usuario (HU-052).
5. Impedir la ejecución del cálculo cuando existen errores activos (HU-053).

---

### Historias de Usuario

#### HU-049: Validación de Rangos de Suma Asegurada

```
Como:        Usuario autenticado con rol agente_ventas, creador_cotizacion o editor_cotizaciones
Quiero:      Que el sistema valide que la suma asegurada de cada ubicación esté dentro
             de los rangos mínimo y máximo permitidos por tipo de riesgo y zona geográfica
Para:        Evitar errores en la cotización y asegurar coherencia con las políticas de suscripción

Prioridad:   Alta
Estimación:  M (3 SP)
Dependencias: SPEC-010 (ParametroCalculoService para obtener rangos por tipoRiesgo/zonaGeografica)
Capa:        Backend
```

#### Criterios de Aceptación — HU-049

**Happy Path**
```gherkin
CRITERIO-49.1: Suma asegurada dentro del rango permitido
  Dado que: el rango permitido para una ubicación con tipoRiesgo "Incendio" en zonaGeografica "A" es [50000, 500000]
  Y:        la ubicación registra sumaAsegurada = 250000
  Cuando:   se ejecuta la validación de reglas sobre la cotización
  Entonces: la ubicación se marca con hasValidationErrors = false para la regla de suma asegurada
  Y:        no se genera error para ese campo
```

**Error Path — Suma por debajo del mínimo**
```gherkin
CRITERIO-49.2: Suma asegurada inferior al mínimo
  Dado que: el rango mínimo para "Incendio" en "Zona A" es 50000
  Y:        la ubicación registra sumaAsegurada = 45000
  Cuando:   se ejecuta la validación de reglas
  Entonces: se genera un ValidationError con campo "sumaAsegurada", regla "RANGO_SUMA_ASEGURADA"
  Y:        el mensaje indica "La suma asegurada (45000) está por debajo del mínimo permitido de 50000 para Incendio en Zona A."
  Y:        la ubicación se marca con hasValidationErrors = true
  Y:        el estado de validación de la cotización pasa a INCOMPLETA
```

**Error Path — Suma por encima del máximo**
```gherkin
CRITERIO-49.3: Suma asegurada superior al máximo
  Dado que: el rango máximo para "Incendio" en "Zona A" es 500000
  Y:        la ubicación registra sumaAsegurada = 600000
  Cuando:   se ejecuta la validación de reglas
  Entonces: se genera un ValidationError con campo "sumaAsegurada", regla "RANGO_SUMA_ASEGURADA"
  Y:        el mensaje indica "La suma asegurada (600000) excede el máximo permitido de 500000 para Incendio en Zona A."
```

**Edge Case — Rango no configurado**
```gherkin
CRITERIO-49.4: Rangos no definidos para el tipo de riesgo o zona
  Dado que: no existen rangos configurados para tipoRiesgo "Terremoto" en zonaGeografica "C"
  Cuando:   se ejecuta la validación de reglas
  Entonces: se aplica un fallback con rangos por defecto (rangoMinimo = 1, rangoMaximo = MAX_LONG)
  Y:        se registra un log WARNING indicando que se usaron rangos por defecto
  Y:        la suma asegurada se acepta si es > 0
```

**Edge Case — Suma asegurada con valor negativo o cero**
```gherkin
CRITERIO-49.5: Suma asegurada inválida (≤ 0)
  Dado que: la ubicación registra sumaAsegurada = 0 o valor negativo
  Cuando:   se ejecuta la validación de reglas
  Entonces: se genera un ValidationError con mensaje "La suma asegurada debe ser un valor positivo mayor a cero."
  Y:        la validación de rango no se ejecuta para ese registro
```

---

#### HU-050: Validación de Código Postal y Asignación de Zona

```
Como:        Usuario autenticado con permisos de gestión de ubicaciones
Quiero:      Que el sistema valide el código postal de cada ubicación contra el catálogo de CP/Zonas
             y asigne automáticamente la zona geográfica correspondiente
Para:        Asegurar la correcta clasificación geográfica del riesgo y la aplicación de factores específicos

Prioridad:   Alta
Estimación:  M (3 SP)
Dependencias: SPEC-004 (CatalogoCPZonas, ParametroCalculoService.obtenerZonaPorCP)
Capa:        Backend
```

#### Criterios de Aceptación — HU-050

**Happy Path**
```gherkin
CRITERIO-50.1: Código postal válido y existente en catálogo
  Dado que: la ubicación registra codigoPostal = "06600"
  Y:        "06600" existe en el catalogo_cp_zonas con zonaCAT = "A"
  Cuando:   se ejecuta la validación de reglas
  Entonces: el campo zonaGeografica de la ubicación se asigna con valor "A"
  Y:        la ubicación se marca como válida para la regla de código postal
  Y:        no se genera error de validación para ese campo
```

**Error Path — CP no existente**
```gherkin
CRITERIO-50.2: Código postal no encontrado en catálogo
  Dado que: la ubicación registra codigoPostal = "99999"
  Y:        "99999" no existe en el catalogo_cp_zonas
  Cuando:   se ejecuta la validación de reglas
  Entonces: se genera un ValidationError con campo "codigoPostal", regla "CP_ZONA_EXISTENCIA"
  Y:        el mensaje indica "El código postal 99999 no se encontró en el catálogo. Verifique el dato ingresado."
  Y:        el campo zonaGeografica permanece nulo
```

**Error Path — Formato inválido de CP**
```gherkin
CRITERIO-50.3: Código postal con formato incorrecto
  Dado que: la ubicación registra codigoPostal = "ABC12" o valor con menos de 5 dígitos
  Cuando:   se ejecuta la validación de reglas
  Entonces: se genera un ValidationError con campo "codigoPostal", regla "CP_FORMATO"
  Y:        el mensaje indica "El código postal debe tener exactamente 5 dígitos numéricos."
  Y:        la consulta al catálogo no se ejecuta
```

**Error Path — Catálogo no disponible**
```gherkin
CRITERIO-50.4: Servicio de catálogo no disponible (Circuit Breaker abierto)
  Dado que: el ParametroCalculoService no está disponible
  Cuando:   se ejecuta la validación de reglas para el código postal
  Entonces: el Circuit Breaker actúa y se genera un ValidationError de sistema
  Y:        el mensaje indica "No se pudo validar el código postal en este momento. Intente más tarde."
  Y:        se registra un log ERROR con el detalle del fallo
```

**Edge Case — CP con datos inconsistentes en catálogo**
```gherkin
CRITERIO-50.5: Código postal existe pero sin zona asignada
  Dado que: el catálogo contiene el CP pero sin zona configurada
  Cuando:   se ejecuta la validación de reglas
  Entonces: zonaGeografica se asigna como "DESCONOCIDA"
  Y:        se registra un log WARNING con el detalle de inconsistencia
```

---

#### HU-051: Verificación de Datos Mínimos por Ubicación

```
Como:        Usuario autenticado con permisos de creación o edición de cotizaciones
Quiero:      Que el sistema verifique que cada ubicación de riesgo tenga todos los campos
             obligatorios para que el cálculo de prima pueda proceder
Para:        Evitar resultados incompletos en el cálculo y garantizar la integridad de los datos

Prioridad:   Alta
Estimación:  S (2 SP)
Dependencias: UbicacionRiesgo entity (definida en esta spec)
Capa:        Backend
```

#### Criterios de Aceptación — HU-051

**Happy Path**
```gherkin
CRITERIO-51.1: Ubicación con todos los datos mínimos completos
  Dado que: una ubicación tiene codigoPostal, tipoRiesgo, sumaAsegurada, zonaGeografica y descripcion completos
  Cuando:   se ejecuta la verificación de datos mínimos
  Entonces: la ubicación se marca como COMPLETA (datosMinimosCompletos = true)
  Y:        no se genera error de campo faltante
```

**Error Path — Campo obligatorio faltante**
```gherkin
CRITERIO-51.2: Campo obligatorio faltante en ubicación
  Dado que: una ubicación no tiene el campo tipoRiesgo
  Cuando:   se ejecuta la verificación de datos mínimos
  Entonces: se genera un ValidationError con campo "tipoRiesgo", regla "DATOS_MINIMOS"
  Y:        el mensaje indica "El campo 'Tipo de Riesgo' es obligatorio para calcular la prima."
  Y:        la ubicación se marca como INCOMPLETA
```

**Error Path — Múltiples campos faltantes**
```gherkin
CRITERIO-51.3: Múltiples campos obligatorios faltantes
  Dado que: una ubicación no tiene tipoRiesgo ni sumaAsegurada
  Cuando:   se ejecuta la verificación de datos mínimos
  Entonces: se generan dos ValidationErrors, uno por campo faltante
  Y:        cada error se lista individualmente con su campo y mensaje correspondiente
```

**Error Path — Bloqueo de cálculo por ubicación incompleta**
```gherkin
CRITERIO-51.4: Intento de cálculo con ubicación incompleta
  Dado que: al menos una ubicación tiene datosMinimosCompletos = false
  Cuando:   se intenta ejecutar el cálculo de prima
  Entonces: el cálculo es bloqueado (respondiendo HTTP 422)
  Y:        el mensaje indica los errores que impiden el cálculo
```

---

#### HU-052: Mensajes de Error Claros y Estructurados al Usuario

```
Como:        Usuario autenticado interactuando con el cotizador
Quiero:      Que los mensajes de error de validación sean descriptivos, identifiquen el campo
             o regla causante y sugieran una acción correctiva
Para:        Entender rápidamente qué corregir y cómo proceder sin necesitar soporte técnico

Prioridad:   Alta
Estimación:  S (2 SP)
Dependencias: HU-049, HU-050, HU-051 (generan los errores que esta HU debe estructurar)
Capa:        Backend + Frontend
```

#### Criterios de Aceptación — HU-052

**Happy Path**
```gherkin
CRITERIO-52.1: Mensaje de error descriptivo con acción correctiva
  Dado que: la validación de "sumaAsegurada" falla por exceder el máximo
  Cuando:   el motor de validación genera el error
  Entonces: el ValidationError incluye:
              errorCode = "VAL-049-002"
              field = "sumaAsegurada"
              message = "La suma asegurada (600000) excede el máximo permitido de 500000 para Incendio en Zona A. Ajuste el valor."
              severity = "ERROR"
              ruleName = "RANGO_SUMA_ASEGURADA"
```

**Happy Path — Múltiples errores listados individualmente**
```gherkin
CRITERIO-52.2: Múltiples errores presentados individualmente
  Dado que: una ubicación tiene errores en sumaAsegurada y codigoPostal simultáneamente
  Cuando:   el motor de validación ejecuta todas las reglas
  Entonces: el resultado contiene dos ValidationErrors, uno por cada regla fallida
  Y:        cada error incluye su errorCode, field, message y severity de forma independiente
```

**Happy Path — Identificación visual del campo en frontend**
```gherkin
CRITERIO-52.3: Error identificado y resaltado en la interfaz
  Dado que: el backend retorna ValidationErrors para "sumaAsegurada" de la ubicación "UBI-001"
  Cuando:   el frontend muestra el formulario de la ubicación
  Entonces: el campo "sumaAsegurada" aparece resaltado visualmente con el mensaje de error inline
  Y:        se muestra un panel de resumen de errores con todos los errores de la cotización
```

**Edge Case — Código de error no configurado**
```gherkin
CRITERIO-52.4: Error genérico si el código no está catalogado
  Dado que: ocurre un error de validación con código desconocido
  Cuando:   el sistema intenta construir el mensaje
  Entonces: se muestra un mensaje genérico: "Ha ocurrido un error de validación inesperado. Código: [errorCode]."
  Y:        se registra un log WARNING con el código no catalogado
```

---

#### HU-053: Bloqueo de Cálculo por Errores de Validación Activos

```
Como:        Usuario autenticado con permisos para iniciar el cálculo de prima
Quiero:      Que el sistema impida el cálculo de la prima si existen errores de validación
             activos en la cotización o en alguna de sus ubicaciones
Para:        Garantizar que solo se calculen primas con datos válidos y completos

Prioridad:   Alta
Estimación:  S (2 SP)
Dependencias: HU-049, HU-050, HU-051, HU-052 (estado de validación de cotización y ubicaciones)
Capa:        Backend + Frontend
```

#### Criterios de Aceptación — HU-053

**Happy Path**
```gherkin
CRITERIO-53.1: Cálculo permitido sin errores de validación
  Dado que: la cotización "COT-2026-000001" no tiene hasValidationErrors = false
  Y:        ninguna de sus ubicaciones tiene hasValidationErrors = true
  Cuando:   el usuario intenta ejecutar el cálculo de prima
  Entonces: el sistema permite la invocación del Motor Central de Cálculo (FT-012)
  Y:        el estado del cálculo transiciona a "EN_PROGRESO"
```

**Error Path — Errores en cotización**
```gherkin
CRITERIO-53.2: Cálculo abortado por errores activos en la cotización
  Dado que: la cotización "COT-2026-000002" tiene hasValidationErrors = true
  Cuando:   el usuario intenta ejecutar el cálculo
  Entonces: el cálculo es abortado con HTTP 422
  Y:        el Motor Central de Cálculo NO es invocado
  Y:        la respuesta incluye la lista de errores activos
  Y:        el mensaje sugiere al usuario resolver los errores antes de continuar
  Y:        se registra un log WARN con el folio y la razón del bloqueo
```

**Error Path — Errores en ubicaciones**
```gherkin
CRITERIO-53.3: Cálculo abortado por errores activos en ubicaciones
  Dado que: la cotización no tiene errores propios
  Y:        al menos una ubicación asociada tiene hasValidationErrors = true
  Cuando:   el usuario intenta ejecutar el cálculo
  Entonces: el cálculo es abortado con HTTP 422
  Y:        la respuesta identifica qué ubicaciones tienen errores activos
```

**Edge Case — Cotización sin ubicaciones**
```gherkin
CRITERIO-53.4: Cálculo abortado si no hay ubicaciones registradas
  Dado que: la cotización "COT-2026-000003" existe pero no tiene ubicaciones asociadas
  Cuando:   el usuario intenta ejecutar el cálculo
  Entonces: el cálculo es abortado con HTTP 422
  Y:        el mensaje indica "No se puede calcular la prima: la cotización no tiene ubicaciones de riesgo registradas."
```

---

### Reglas de Negocio

1. **BR-049-001 — Rango de suma asegurada por tipoRiesgo y zonaGeografica:** Cada ubicación debe tener `sumaAsegurada >= rangoMinimo` y `sumaAsegurada <= rangoMaximo`. Los rangos se obtienen desde `ParametroCalculoService` y dependen de `tipoRiesgo` y `zonaGeografica`. Si no existen rangos específicos, se aplica fallback (mínimo = 1, máximo = `Long.MAX_VALUE`) con log WARNING.
2. **BR-049-002 — Suma asegurada positiva:** `sumaAsegurada` debe ser un valor decimal positivo estrictamente mayor a cero. La validación de rango no se ejecuta si el valor es ≤ 0.
3. **BR-050-001 — Existencia de CP en catálogo:** El `codigoPostal` de cada ubicación debe existir en `catalogo_cp_zonas`. Si existe, `zonaGeografica` se asigna automáticamente desde el catálogo.
4. **BR-050-002 — Formato de CP:** El `codigoPostal` debe ser exactamente 5 dígitos numéricos (`^\d{5}$`). La validación de formato precede a la consulta al catálogo.
5. **BR-051-001 — Campos mínimos obligatorios de ubicación:** Para proceder al cálculo, cada `UbicacionRiesgo` debe tener: `codigoPostal`, `tipoRiesgo`, `sumaAsegurada`, `zonaGeografica` y `descripcion`. Un campo nulo o vacío genera un error con código `DATOS_MINIMOS`.
6. **BR-052-001 — Estructura estándar de errores:** Todo error de validación debe incluir: `errorCode` (formato `VAL-HU-SEQ`), `field`, `message`, `severity` (`ERROR` | `WARNING`), `ruleName`. Los mensajes deben ser generados por un `ValidationErrorBuilder` centralizado.
7. **BR-052-002 — No revelar información sensible en mensajes:** Los mensajes de error no exponen stack traces, IDs internos de MongoDB (`_id`), ni datos de configuración interna.
8. **BR-053-001 — Pre-condición obligatoria del cálculo:** El Motor Central de Cálculo (FT-012) no puede invocarse si `cotizacion.hasValidationErrors = true` o si cualquier ubicación asociada tiene `hasValidationErrors = true`. Esta restricción se aplica en `CalculationGuard` antes de delegar a FT-012.
9. **BR-053-002 — Sin ubicaciones bloquea cálculo:** Una cotización sin ubicaciones registradas no puede proceder al cálculo.
10. **BR-AUTH-001 — RBAC:** Los endpoints de validación de reglas son accesibles para roles `agente_ventas`, `creador_cotizacion`, `editor_cotizaciones` y `administrador_ventas`. La consulta de estado de validación también es accesible para `vendedor`.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas
| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `UbicacionRiesgo` | colección `ubicaciones-riesgo` | **nueva** | Ubicación de riesgo asociada a una cotización |
| `Cotizacion` | colección `cotizaciones` | **modificada** | Añadir `hasValidationErrors`, `validationErrorCount` |

#### Campos del modelo — `UbicacionRiesgo` (nueva)
| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | auto-generado | `@Id` interno de MongoDB |
| `folioCotizacion` | String | sí | formato `COT-\d{4}-\d{6}` | Referencia a la cotización padre |
| `descripcion` | String | sí* | max 255 chars | Descripción libre de la ubicación |
| `codigoPostal` | String | sí* | `^\d{5}$` | Código postal de 5 dígitos |
| `zonaGeografica` | String | no (asignada automáticamente) | asignada desde catálogo | Zona de riesgo asignada al validar CP |
| `tipoRiesgo` | String | sí* | valores: `Incendio`, `Robo`, `Terremoto`, `Cristales`, `Equipo Electronico` | Tipo de riesgo de la ubicación |
| `sumaAsegurada` | BigDecimal | sí* | > 0, formato monetario | Suma asegurada para esta ubicación |
| `hasValidationErrors` | Boolean | sí | default `false` | Indica si la ubicación tiene errores activos |
| `validationErrors` | List\<ValidationErrorDetail\> | no | puede estar vacío | Lista de errores activos de validación |
| `datosMinimosCompletos` | Boolean | sí | default `false` | Indica si todos los campos mínimos están presentes |
| `createdAt` | Instant | sí | auto-generado (UTC) | Timestamp de creación |
| `updatedAt` | Instant | sí | auto-gestionado (UTC) | Timestamp de última modificación |
| `version` | Long | sí | `@Version` | Control de concurrencia optimista |

> *Campos no obligatorios al crear la ubicación (puede quedar incompleta), pero todos requeridos para que `datosMinimosCompletos = true` y para habilitar el cálculo.

#### Campos nuevos en `Cotizacion` (modificación)
| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `hasValidationErrors` | Boolean | sí | default `false` | Indica errores activos en la cotización o sus ubicaciones |
| `validationErrorCount` | Integer | sí | default `0` | Total de errores activos (cotización + ubicaciones) |

#### Índices / Constraints
- `UbicacionRiesgo.folioCotizacion`: índice simple — búsqueda de todas las ubicaciones de una cotización.
- `UbicacionRiesgo.folioCotizacion + tipoRiesgo`: índice compuesto — filtrado por tipo de riesgo dentro de una cotización.
- `UbicacionRiesgo.hasValidationErrors`: índice simple — consultas de estado de validación.
- `UbicacionRiesgo.codigoPostal`: índice simple — búsquedas por CP.

---

### API Endpoints

#### POST /api/v1/cotizaciones/{folio}/validar-reglas
- **Descripción**: Ejecuta el motor de validación de reglas de negocio sobre la cotización y todas sus ubicaciones. Actualiza `hasValidationErrors` en cotización y en cada ubicación.
- **Auth requerida**: sí — JWT Bearer token
- **Roles permitidos**: `agente_ventas`, `creador_cotizacion`, `editor_cotizaciones`, `administrador_ventas`
- **Path param**: `folio` — formato `COT-YYYY-NNNNNN`
- **Request Body**: vacío
- **Response 200** (sin errores):
  ```json
  {
    "folio": "COT-2026-000001",
    "esValida": true,
    "totalErrores": 0,
    "ubicaciones": [
      {
        "ubicacionId": "ubi-abc123",
        "descripcion": "Almacén Planta Norte",
        "hasValidationErrors": false,
        "errores": []
      }
    ]
  }
  ```
- **Response 207** (cotización con errores de validación):
  ```json
  {
    "folio": "COT-2026-000002",
    "esValida": false,
    "totalErrores": 3,
    "ubicaciones": [
      {
        "ubicacionId": "ubi-xyz789",
        "descripcion": "Bodega Sur",
        "hasValidationErrors": true,
        "errores": [
          {
            "errorCode": "VAL-049-002",
            "field": "sumaAsegurada",
            "message": "La suma asegurada (600000) excede el máximo permitido de 500000 para Incendio en Zona A. Ajuste el valor.",
            "severity": "ERROR",
            "ruleName": "RANGO_SUMA_ASEGURADA"
          },
          {
            "errorCode": "VAL-050-001",
            "field": "codigoPostal",
            "message": "El código postal 99999 no se encontró en el catálogo. Verifique el dato ingresado.",
            "severity": "ERROR",
            "ruleName": "CP_ZONA_EXISTENCIA"
          }
        ]
      }
    ]
  }
  ```
- **Response 400**: folio con formato inválido
- **Response 401**: token ausente o expirado
- **Response 403**: rol sin permisos
- **Response 404**: cotización no encontrada

---

#### GET /api/v1/cotizaciones/{folio}/estado-validacion
- **Descripción**: Obtiene el estado de validación actual de la cotización y sus ubicaciones sin re-ejecutar las reglas.
- **Auth requerida**: sí — JWT Bearer token
- **Roles permitidos**: `agente_ventas`, `creador_cotizacion`, `editor_cotizaciones`, `administrador_ventas`, `vendedor`
- **Path param**: `folio` — formato `COT-YYYY-NNNNNN`
- **Response 200**: mismo esquema que POST /validar-reglas
- **Response 401**: token ausente o expirado
- **Response 403**: rol sin permisos
- **Response 404**: cotización no encontrada

---

#### POST /api/v1/cotizaciones/{folio}/ubicaciones
- **Descripción**: Crea una nueva ubicación de riesgo asociada a una cotización.
- **Auth requerida**: sí — JWT Bearer token
- **Roles permitidos**: `agente_ventas`, `creador_cotizacion`, `editor_cotizaciones`
- **Path param**: `folio` — formato `COT-YYYY-NNNNNN`
- **Request Body**:
  ```json
  {
    "descripcion": "Almacén Planta Norte",
    "codigoPostal": "06600",
    "tipoRiesgo": "Incendio",
    "sumaAsegurada": 250000.00
  }
  ```
- **Response 201**:
  ```json
  {
    "ubicacionId": "ubi-abc123",
    "folioCotizacion": "COT-2026-000001",
    "descripcion": "Almacén Planta Norte",
    "codigoPostal": "06600",
    "zonaGeografica": null,
    "tipoRiesgo": "Incendio",
    "sumaAsegurada": 250000.00,
    "hasValidationErrors": false,
    "validationErrors": [],
    "datosMinimosCompletos": true,
    "createdAt": "2026-05-02T10:00:00Z",
    "updatedAt": "2026-05-02T10:00:00Z",
    "version": 0
  }
  ```
- **Response 400**: campo inválido o folio mal formado
- **Response 401**: token ausente o expirado
- **Response 403**: rol sin permisos
- **Response 404**: cotización padre no encontrada

---

#### GET /api/v1/cotizaciones/{folio}/ubicaciones
- **Descripción**: Lista todas las ubicaciones de riesgo de una cotización.
- **Auth requerida**: sí
- **Roles permitidos**: todos los roles autorizados para ver cotizaciones
- **Response 200**: array de ubicaciones (mismo esquema que POST ubicaciones response)

---

#### GET /api/v1/cotizaciones/{folio}/ubicaciones/{ubicacionId}
- **Descripción**: Obtiene una ubicación específica por ID.
- **Auth requerida**: sí
- **Response 200**: ubicación completa
- **Response 404**: ubicación no encontrada

---

#### PUT /api/v1/cotizaciones/{folio}/ubicaciones/{ubicacionId}
- **Descripción**: Actualiza una ubicación de riesgo existente. Aplica versionado optimista.
- **Auth requerida**: sí
- **Roles permitidos**: `agente_ventas`, `creador_cotizacion`, `editor_cotizaciones`
- **Request Body**: campos opcionales a actualizar + `version` obligatorio
- **Response 200**: ubicación actualizada
- **Response 409**: conflicto de versión por edición concurrente

---

#### DELETE /api/v1/cotizaciones/{folio}/ubicaciones/{ubicacionId}
- **Descripción**: Elimina una ubicación de riesgo.
- **Auth requerida**: sí
- **Roles permitidos**: `agente_ventas`, `editor_cotizaciones`, `administrador_ventas`
- **Response 204**: eliminada exitosamente
- **Response 404**: no encontrada

---

### Diseño Frontend

#### Componentes nuevos
| Componente | Archivo | Props principales | Descripción |
|------------|---------|------------------|-------------|
| `ValidationErrorsPanel` | `components/Cotizador/ValidationErrorsPanel.tsx` | `errors: ValidationError[], ubicacionId?` | Panel colapsable con lista de errores de validación |
| `ValidationStatusBadge` | `components/Cotizador/ValidationStatusBadge.tsx` | `hasErrors: boolean, errorCount: number` | Badge visual de estado de validación (verde/rojo) |
| `UbicacionRiesgoForm` | `components/Cotizador/UbicacionRiesgoForm.tsx` | `folio, ubicacion?, onSave, onCancel` | Formulario de creación/edición de ubicación de riesgo |
| `UbicacionRiesgoCard` | `components/Cotizador/UbicacionRiesgoCard.tsx` | `ubicacion, onEdit, onDelete` | Tarjeta con resumen de ubicación y badge de validación |
| `UbicacionRiesgoList` | `components/Cotizador/UbicacionRiesgoList.tsx` | `folio, onValidate` | Lista de ubicaciones con botón "Validar Reglas" |
| `CalculoBlockedAlert` | `components/Cotizador/CalculoBlockedAlert.tsx` | `errors, onGoToErrors` | Alerta de bloqueo de cálculo con enlace a errores |

#### Páginas modificadas
| Página | Archivo | Cambio |
|--------|---------|--------|
| `EditarCotizacionPage` | `app/cotizaciones/[folio]/page.tsx` | Añadir sección de ubicaciones de riesgo y panel de validación |

#### Hooks y State
| Hook/Store | Archivo | Retorna | Descripción |
|------------|---------|---------|-------------|
| `useValidacionReglas` | `hooks/useValidacionReglas.ts` | `{ resultado, loading, error, ejecutarValidacion, getEstado }` | Orquesta llamadas de validación de reglas |
| `useUbicacionesRiesgo` | `hooks/useUbicacionesRiesgo.ts` | `{ ubicaciones, loading, error, crear, actualizar, eliminar }` | CRUD de ubicaciones de riesgo |

#### Services (llamadas API)
| Función | Archivo | Endpoint |
|---------|---------|---------|
| `ejecutarValidacionReglas(folio, token)` | `lib/services/validacionService.ts` | `POST /api/v1/cotizaciones/{folio}/validar-reglas` |
| `getEstadoValidacion(folio, token)` | `lib/services/validacionService.ts` | `GET /api/v1/cotizaciones/{folio}/estado-validacion` |
| `getUbicaciones(folio, token)` | `lib/services/ubicacionService.ts` | `GET /api/v1/cotizaciones/{folio}/ubicaciones` |
| `createUbicacion(folio, data, token)` | `lib/services/ubicacionService.ts` | `POST /api/v1/cotizaciones/{folio}/ubicaciones` |
| `updateUbicacion(folio, id, data, token)` | `lib/services/ubicacionService.ts` | `PUT /api/v1/cotizaciones/{folio}/ubicaciones/{id}` |
| `deleteUbicacion(folio, id, token)` | `lib/services/ubicacionService.ts` | `DELETE /api/v1/cotizaciones/{folio}/ubicaciones/{id}` |

---

### Arquitectura y Dependencias

**Backend — nuevos componentes:**

```
ValidationRulesController
  └── ValidationRulesService (interfaz)
        └── ValidationRulesServiceImpl
              ├── SumaAseguradaRangeValidator  (HU-049)
              ├── ZipCodeZoneValidator          (HU-050)
              ├── MinimumDataValidator          (HU-051)
              ├── ValidationErrorBuilder        (HU-052) — catálogo centralizado de mensajes
              └── CalculationGuard             (HU-053) — pre-condición para FT-012

UbicacionRiesgoController
  └── UbicacionRiesgoService (interfaz)
        └── UbicacionRiesgoServiceImpl
              └── UbicacionRiesgoRepository
```

**Dependencias externas y reutilización:**
- `ParametroCalculoService.obtenerZonaPorCP(cp)` — ya implementado, reutilizado por `ZipCodeZoneValidator`
- `ParametroCalculoService.obtenerTarifasIncendioVigentes()` — para obtener rangos de incendio
- `CatalogoCPZonasRepository` — ya existe, acceso directo si el servicio falla
- `ValidationRule` entity + `ValidationRuleRepository` — ya existen, pueden usarse para reglas configurables futuras
- `ValidationErrorDetail` DTO — ya existe, reutilizado en los errores de ubicación

**Circuit Breaker para validaciones externas:**
- `zipCodeValidationCircuitBreaker` — envuelve las llamadas a `ParametroCalculoService.obtenerZonaPorCP`
- Configuración: window 10 llamadas, threshold 50%, open 10s (igual que el resto del proyecto)

**Paquetes — sin cambios al pom.xml** (reutiliza toda la dependencia existente)

### Notas de Implementación
> - `UbicacionRiesgo` es una entidad independiente con referencia por `folioCotizacion` (no embebida en `Cotizacion`) para facilitar paginación y actualización individual.
> - El campo `hasValidationErrors` en `Cotizacion` es una bandera desnormalizada: se recalcula cada vez que se ejecuta `POST /validar-reglas`. Se hace `true` si la cotización propia o cualquier ubicación tienen errores.
> - `CalculationGuard` es un componente `@Component` que se inyecta en el futuro `MotorCalculoService` (FT-012) como pre-condición. **No** es un endpoint propio — es lógica transversal interna.
> - La validación de CP (`ZipCodeZoneValidator`) siempre actualiza `zonaGeografica` cuando el CP es válido, incluso si ya tenía zona asignada (para mantener consistencia con el catálogo).
> - El `ValidationErrorBuilder` usa un `Map<String, String>` interno para los templates de mensajes (no requiere BD). Futuras versiones pueden migrar a `ValidationRule` entity si se necesita parametrización dinámica.
> - Los rangos de suma asegurada (`rangoMinimo`, `rangoMaximo`) se obtienen en tiempo de validación con caché Caffeine (TTL configurado en `SPEC-010`). Si no hay rangos para un `tipoRiesgo/zonaGeografica` específico, se aplica fallback con `[1, Long.MAX_VALUE]`.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación — Modelo de Datos
- [ ] Crear `UbicacionRiesgo.java` en `model/entity/` con `@Document(collection = "ubicaciones-riesgo")`, todos los campos del modelo, `@Id`, `@Version`, `@Indexed` en `folioCotizacion` y `hasValidationErrors`
- [ ] Crear `UbicacionRiesgoRequest.java` DTO (campos opcionales, incluye `version`)
- [ ] Crear `UbicacionRiesgoResponse.java` DTO (excluye `_id` de MongoDB)
- [ ] Crear `ValidationRulesResult.java` DTO (folio, esValida, totalErrores, lista de UbicacionValidacionResult)
- [ ] Crear `UbicacionValidacionResult.java` DTO inner/separado (ubicacionId, descripcion, hasValidationErrors, errores)
- [ ] Modificar `Cotizacion.java` — añadir campos `hasValidationErrors` (Boolean, default false) y `validationErrorCount` (Integer, default 0)

#### Implementación — Repositorios
- [ ] Crear `UbicacionRiesgoRepository.java` extendiendo `MongoRepository<UbicacionRiesgo, String>` con:
  - `List<UbicacionRiesgo> findByFolioCotizacion(String folio)`
  - `List<UbicacionRiesgo> findByFolioCotizacionAndHasValidationErrors(String folio, boolean hasErrors)`
  - `long countByFolioCotizacionAndHasValidationErrors(String folio, boolean hasErrors)`

#### Implementación — Validadores
- [ ] Crear interfaz `BusinessRuleValidator.java` con método `List<ValidationErrorDetail> validate(UbicacionRiesgo ubicacion)`
- [ ] Implementar `SumaAseguradaRangeValidator.java` (`@Component`):
  - Obtiene rangos via `ParametroCalculoService` por `tipoRiesgo` y `zonaGeografica`
  - Fallback a `[1, Long.MAX_VALUE]` si no hay rangos configurados + log WARNING
  - Genera errorCodes `VAL-049-001` (≤0), `VAL-049-002` (< mínimo), `VAL-049-003` (> máximo)
- [ ] Implementar `ZipCodeZoneValidator.java` (`@Component`):
  - Valida formato `^\d{5}$` antes de consultar catálogo
  - Llama a `ParametroCalculoService.obtenerZonaPorCP` con Circuit Breaker
  - Asigna `zonaGeografica` cuando CP es válido
  - Genera errorCodes `VAL-050-001` (formato inválido), `VAL-050-002` (CP no encontrado), `VAL-050-003` (servicio no disponible)
- [ ] Implementar `MinimumDataValidator.java` (`@Component`):
  - Verifica presencia de: `codigoPostal`, `tipoRiesgo`, `sumaAsegurada`, `zonaGeografica`, `descripcion`
  - Genera errorCode `VAL-051-001` por cada campo faltante con nombre del campo en el mensaje

#### Implementación — ErrorBuilder y Guard
- [ ] Implementar `ValidationErrorBuilder.java` (`@Component`):
  - Mapa centralizado de templates: `errorCode → messageTemplate`
  - Método `buildError(errorCode, ...args)` que rellena el template con los valores
  - Incluye fallback genérico para códigos no catalogados (CRITERIO-52.4)
- [ ] Implementar `CalculationGuard.java` (`@Component`):
  - Método `assertCanCalculate(String folioCotizacion)` — lanza `ValidationBlockedException` si hay errores activos
  - Verifica `cotizacion.hasValidationErrors` y consulta ubicaciones con `hasValidationErrors = true`
  - Incluye verificación de que existen ubicaciones (`countByFolioCotizacion > 0`)

#### Implementación — Services
- [ ] Crear `ValidationRulesService.java` (interfaz) con métodos:
  - `ValidationRulesResult ejecutarValidacion(String folio)`
  - `ValidationRulesResult obtenerEstado(String folio)`
- [ ] Implementar `ValidationRulesServiceImpl.java`:
  - Orquesta los tres validators por ubicación (SumaAsegurada + ZipCode + MinimumData)
  - Persiste errores en cada `UbicacionRiesgo`
  - Actualiza `hasValidationErrors` y `validationErrorCount` en `Cotizacion`
- [ ] Crear `UbicacionRiesgoService.java` (interfaz) con métodos CRUD
- [ ] Implementar `UbicacionRiesgoServiceImpl.java`:
  - `crear(folio, request)`: verifica que la cotización existe, persiste ubicación con `datosMinimosCompletos` calculado
  - `obtenerPorFolio(folio)`: lista con paginación opcional
  - `obtenerPorId(folio, ubicacionId)`: verifica pertenencia al folio
  - `actualizar(folio, ubicacionId, request)`: versionado optimista
  - `eliminar(folio, ubicacionId)`: verifica pertenencia

#### Implementación — Controllers
- [ ] Implementar `ValidationRulesController.java` con:
  - `POST /api/v1/cotizaciones/{folio}/validar-reglas` → HTTP 200 o HTTP 207
  - `GET /api/v1/cotizaciones/{folio}/estado-validacion` → HTTP 200
- [ ] Implementar `UbicacionRiesgoController.java` con endpoints CRUD en `/api/v1/cotizaciones/{folio}/ubicaciones`
- [ ] Crear `ValidationBlockedException.java` (HTTP 422) con lista de errores
- [ ] Registrar los nuevos controllers en el contexto de Spring Security (autorización RBAC)

#### Tests Backend
- [ ] `SumaAseguradaRangeValidatorTest` — `validate_sumaEnRango_retornaListaVacia`
- [ ] `SumaAseguradaRangeValidatorTest` — `validate_sumaMenorAlMinimo_retornaErrorVAL049002`
- [ ] `SumaAseguradaRangeValidatorTest` — `validate_sumaMayorAlMaximo_retornaErrorVAL049003`
- [ ] `SumaAseguradaRangeValidatorTest` — `validate_sumaCeroONegativa_retornaErrorVAL049001`
- [ ] `SumaAseguradaRangeValidatorTest` — `validate_sinRangosConfigurados_aplicaFallbackYLogWarning`
- [ ] `ZipCodeZoneValidatorTest` — `validate_codigoPostalValido_asignaZona`
- [ ] `ZipCodeZoneValidatorTest` — `validate_codigoPostalInexistente_retornaErrorVAL050002`
- [ ] `ZipCodeZoneValidatorTest` — `validate_formatoInvalido_retornaErrorVAL050001SinConsultarCatalogo`
- [ ] `ZipCodeZoneValidatorTest` — `validate_servicioNoDisponible_retornaErrorVAL050003`
- [ ] `MinimumDataValidatorTest` — `validate_ubicacionCompleta_retornaListaVacia`
- [ ] `MinimumDataValidatorTest` — `validate_faltaTipoRiesgo_retornaErrorVAL051001`
- [ ] `MinimumDataValidatorTest` — `validate_variosFieldsFaltantes_retornaErrorPorCadaUno`
- [ ] `ValidationErrorBuilderTest` — `buildError_codigoConocido_retornaMensajeFormateado`
- [ ] `ValidationErrorBuilderTest` — `buildError_codigoDesconocido_retornaMensajeGenerico`
- [ ] `CalculationGuardTest` — `assertCanCalculate_sinErrores_noLanzaExcepcion`
- [ ] `CalculationGuardTest` — `assertCanCalculate_conErroresEnCotizacion_lanzaValidationBlockedException`
- [ ] `CalculationGuardTest` — `assertCanCalculate_conErroresEnUbicacion_lanzaValidationBlockedException`
- [ ] `CalculationGuardTest` — `assertCanCalculate_sinUbicaciones_lanzaValidationBlockedException`
- [ ] `ValidationRulesServiceImplTest` — `ejecutarValidacion_todasUbicacionesValidas_retornaEsValidaTrue`
- [ ] `ValidationRulesServiceImplTest` — `ejecutarValidacion_conErrores_actualizaHasValidationErrorsEnCotizacion`
- [ ] `UbicacionRiesgoRepositoryIT` (Testcontainers) — `findByFolioCotizacion_retornaUbicacionesDelFolio`
- [ ] `ValidationRulesControllerTest` — `POST_validar-reglas_sinErrores_retorna200`
- [ ] `ValidationRulesControllerTest` — `POST_validar-reglas_conErrores_retorna207`
- [ ] `ValidationRulesControllerTest` — `POST_validar-reglas_folioInexistente_retorna404`
- [ ] `ValidationRulesControllerTest` — `POST_validar-reglas_sinToken_retorna401`
- [ ] `UbicacionRiesgoControllerTest` — `POST_ubicaciones_datosValidos_retorna201`
- [ ] `UbicacionRiesgoControllerTest` — `PUT_ubicaciones_conflictoVersion_retorna409`
- [ ] `UbicacionRiesgoControllerTest` — `DELETE_ubicaciones_existente_retorna204`

---

### Frontend

#### Implementación
- [ ] Crear `lib/services/validacionService.ts` — `ejecutarValidacionReglas`, `getEstadoValidacion` con Axios + token Bearer
- [ ] Crear `lib/services/ubicacionService.ts` — CRUD completo de ubicaciones
- [ ] Crear `hooks/useValidacionReglas.ts` — orquesta llamadas de validación, expone `resultado`, `loading`, `error`, `ejecutarValidacion`, `getEstado`
- [ ] Crear `hooks/useUbicacionesRiesgo.ts` — CRUD ubicaciones con estado local y actualización optimista
- [ ] Implementar `components/Cotizador/ValidationErrorsPanel.tsx` — panel colapsable con lista de errores agrupados por regla, con errorCode visible
- [ ] Implementar `components/Cotizador/ValidationStatusBadge.tsx` — badge verde (sin errores) / rojo (con errores + count)
- [ ] Implementar `components/Cotizador/UbicacionRiesgoForm.tsx` — formulario con campos de ubicación, validación Zod inline (formato CP, sumaAsegurada > 0, tipoRiesgo obligatorio)
- [ ] Implementar `components/Cotizador/UbicacionRiesgoCard.tsx` — tarjeta con datos de ubicación, `ValidationStatusBadge`, botones editar/eliminar
- [ ] Implementar `components/Cotizador/UbicacionRiesgoList.tsx` — lista de tarjetas + botón "Validar Reglas" + botón "Nueva Ubicación"
- [ ] Implementar `components/Cotizador/CalculoBlockedAlert.tsx` — alerta con lista de errores y botón "Ver Errores" que hace scroll al panel de errores
- [ ] Integrar `UbicacionRiesgoList` y `ValidationErrorsPanel` en `app/cotizaciones/[folio]/page.tsx`

#### Tests Frontend
- [ ] `ValidationErrorsPanel muestra lista de errores correctamente`
- [ ] `ValidationErrorsPanel colapsa y expande al hacer clic`
- [ ] `ValidationStatusBadge muestra verde cuando hasErrors = false`
- [ ] `ValidationStatusBadge muestra rojo con count cuando hasErrors = true`
- [ ] `UbicacionRiesgoForm valida formato de CP (5 dígitos numéricos)`
- [ ] `UbicacionRiesgoForm rechaza sumaAsegurada = 0 o negativa`
- [ ] `UbicacionRiesgoForm requiere tipoRiesgo seleccionado`
- [ ] `UbicacionRiesgoForm envía datos correctos al servicio`
- [ ] `UbicacionRiesgoCard muestra descripcion y sumaAsegurada formateada`
- [ ] `UbicacionRiesgoCard llama onEdit al hacer clic en Editar`
- [ ] `UbicacionRiesgoCard llama onDelete al hacer clic en Eliminar`
- [ ] `UbicacionRiesgoList renderiza lista de ubicaciones`
- [ ] `UbicacionRiesgoList llama ejecutarValidacion al hacer clic en Validar Reglas`
- [ ] `CalculoBlockedAlert muestra errores y botón Ver Errores`
- [ ] `useValidacionReglas ejecutarValidacion actualiza resultado con errores`
- [ ] `useValidacionReglas maneja error 404 de folio inexistente`
- [ ] `useUbicacionesRiesgo crear añade ubicación a la lista`
- [ ] `useUbicacionesRiesgo eliminar quita ubicación de la lista`

---

### QA
- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-49.1 a CRITERIO-53.4
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD (HU-049 alta: rangos de tarificación; HU-050 alta: zona incorrecta impacta factores; HU-053 alta: bloqueo de cálculo)
- [ ] Verificar cobertura de tests backend ≥ 90% en módulo de validación con JaCoCo (CRITERIO técnico HU-049: >90%)
- [ ] Ejecutar pruebas de integración con Testcontainers MongoDB para `UbicacionRiesgoRepository`
- [ ] Validar Circuit Breaker en `ZipCodeZoneValidator` ante fallo de `ParametroCalculoService`
- [ ] Verificar que `CalculationGuard` bloquea correctamente en los tres escenarios (HU-053)
- [ ] Validar que el campo `_id` de MongoDB no se expone en ninguna respuesta de ubicaciones
- [ ] Validar que todos los errores incluyen `errorCode`, `field`, `message`, `severity`, `ruleName`
- [ ] Verificar comportamiento de fallback de rangos cuando `tipoRiesgo/zonaGeografica` no está configurado
- [ ] Actualizar estado spec: `status: IMPLEMENTED` al completar
