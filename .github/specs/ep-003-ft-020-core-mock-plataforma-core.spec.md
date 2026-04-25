---
id: SPEC-001
status: IN_PROGRESS
feature: ep-003-ft-020-core-mock-plataforma-core
created: 2026-04-24
updated: 2026-04-24
author: spec-generator
version: "1.0"
related-specs: []
---

# Spec: FT-020 — Simulación de Servicio `Plataforma-core-ohs` (Mock Server)

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa un mock server en Node.js/Express que simula el servicio externo `Plataforma-core-ohs`, replicando sus contratos de API REST para catálogos, códigos postales, tarifas y factores técnicos. Permite al equipo de desarrollo y pruebas trabajar de forma independiente del servicio real, habilitando el desarrollo paralelo de las features EP-003 FT-015 a FT-019 y EP-001/EP-002 que dependen de datos maestros.

### Requerimiento de Negocio

Implementar un mock server robusto que simule el servicio `Plataforma-core-ohs` API REST, replicando sus contratos y soportado por una base de datos MongoDB poblada con migraciones (`migrate-mongo`), con soporte para respuestas dinámicas y escenarios de error controlados para pruebas de resiliencia.

### Historias de Usuario

#### HU-01: Configuración de Mock Server Base (HU-092)

```
Como:        Desarrollador
Quiero:      Un mock server operativo y accesible en el puerto configurado
Para:        Simular las respuestas del servicio `Plataforma-core-ohs` y desarrollar el cotizador de forma independiente

Prioridad:   Crítica
Estimación:  S (3 story points)
Dependencias: Ninguna
Capa:        Backend (Node.js/Express)
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Mock server se inicia correctamente
  Dado que:  he iniciado el mock server con configuración válida
  Cuando:    accedo a GET http://localhost:{PORT}/health
  Entonces:  recibo HTTP 200 con body { "status": "UP", "service": "plataforma-core-ohs-mock" }
```

**Happy Path**
```gherkin
CRITERIO-1.2: El cotizador se conecta al mock server
  Dado que:  el mock server está activo y el cotizador apunta a su URL
  Cuando:    el cotizador realiza una petición a cualquier endpoint del mock
  Entonces:  recibe una respuesta simulada con la estructura esperada del servicio real
```

**Error Path**
```gherkin
CRITERIO-1.3: Puerto ocupado impide inicio
  Dado que:  el puerto configurado ya está en uso
  Cuando:    se intenta iniciar el mock server
  Entonces:  el proceso termina con exit code 1 y un mensaje claro en logs: "Puerto {PORT} ya está en uso"
```

**Edge Case**
```gherkin
CRITERIO-1.4: Configuración via variables de entorno
  Dado que:  el archivo .env define PORT=9090 y MONGODB_URI=mongodb://localhost:27017/mock-ohs
  Cuando:    se inicia el mock server
  Entonces:  el servidor escucha en el puerto 9090 y conecta al MongoDB configurado
```

---

#### HU-02: Simulación de Catálogos Básicos (HU-093)

```
Como:        Desarrollador
Quiero:      Que el mock server simule los endpoints de suscriptores, agentes y giros
Para:        Probar la funcionalidad de selección de catálogos en el cotizador (FT-015)

Prioridad:   Alta
Estimación:  M (4 story points)
Dependencias: HU-01 (HU-092)
Capa:        Backend (Node.js/Express)
```

#### Criterios de Aceptación — HU-02

**Happy Path**
```gherkin
CRITERIO-2.1: Catálogo de suscriptores disponible
  Dado que:  el mock server está activo con datos de migración cargados
  Cuando:    el cotizador realiza GET /v1/subscribers
  Entonces:  recibe HTTP 200 con array JSON de suscriptores con campos { id, nombre, clave }
```

**Happy Path**
```gherkin
CRITERIO-2.2: Catálogo de agentes disponible
  Dado que:  el mock server está activo
  Cuando:    el cotizador realiza GET /v1/agents
  Entonces:  recibe HTTP 200 con array JSON de agentes con campos { id, nombre, clave }
```

**Happy Path**
```gherkin
CRITERIO-2.3: Catálogo de giros disponible
  Dado que:  el mock server está activo
  Cuando:    el cotizador realiza GET /v1/business-lines
  Entonces:  recibe HTTP 200 con array JSON de giros con campos { id, descripcion, claveIncendio }
```

**Error Path**
```gherkin
CRITERIO-2.4: Catálogo configurado para error retorna 500
  Dado que:  el mock server está configurado con escenario de error para /v1/subscribers
  Cuando:    el cotizador realiza GET /v1/subscribers
  Entonces:  recibe HTTP 500 con body { "message": "Error interno simulado", "code": "MOCK_ERROR" }
```

**Edge Case**
```gherkin
CRITERIO-2.5: Catálogo vacío retorna array vacío
  Dado que:  la colección de agentes en MongoDB está vacía
  Cuando:    el cotizador realiza GET /v1/agents
  Entonces:  recibe HTTP 200 con body []
```

---

#### HU-03: Simulación de Códigos Postales y Zonas (HU-094)

```
Como:        Desarrollador
Quiero:      Que el mock server simule los endpoints de consulta y validación de códigos postales
Para:        Probar la lógica de tarifas por ubicación en el cotizador (FT-016)

Prioridad:   Alta
Estimación:  M (4 story points)
Dependencias: HU-01 (HU-092)
Capa:        Backend (Node.js/Express)
```

#### Criterios de Aceptación — HU-03

**Happy Path**
```gherkin
CRITERIO-3.1: Consulta de CP válido retorna zona
  Dado que:  el CP "06600" existe en la base de datos del mock
  Cuando:    el cotizador realiza GET /v1/zip-codes/06600
  Entonces:  recibe HTTP 200 con body { "codigoPostal": "06600", "zonaCAT": "ZONA_A", "nivelTecnico": "ALTO", "estado": "Ciudad de México", "municipio": "Cuauhtémoc" }
```

**Happy Path**
```gherkin
CRITERIO-3.2: Validación batch de CPs
  Dado que:  se envía un array de CPs para validar
  Cuando:    el cotizador realiza POST /v1/zip-codes/validate con body { "zipCodes": ["06600", "44100", "99999"] }
  Entonces:  recibe HTTP 200 con body { "results": [{ "codigoPostal": "06600", "valido": true, ... }, { "codigoPostal": "99999", "valido": false }] }
```

**Error Path**
```gherkin
CRITERIO-3.3: CP no existente retorna 404
  Dado que:  el CP "99999" no existe en la base de datos del mock
  Cuando:    el cotizador realiza GET /v1/zip-codes/99999
  Entonces:  recibe HTTP 404 con body { "message": "Código postal no encontrado", "code": "ZIP_NOT_FOUND" }
```

**Error Path**
```gherkin
CRITERIO-3.4: CP con formato inválido retorna 400
  Dado que:  se envía un CP con formato no numérico
  Cuando:    el cotizador realiza GET /v1/zip-codes/ABCDE
  Entonces:  recibe HTTP 400 con body { "message": "Formato de código postal inválido", "code": "INVALID_ZIP_FORMAT" }
```

---

#### HU-04: Simulación de Catálogos de Riesgo y Garantías (HU-095)

```
Como:        Desarrollador
Quiero:      Que el mock server simule los endpoints de clasificación de riesgo y garantías
Para:        Probar la configuración de coberturas y evaluación de riesgo en el cotizador (FT-017)

Prioridad:   Media
Estimación:  S (3 story points)
Dependencias: HU-01 (HU-092)
Capa:        Backend (Node.js/Express)
```

#### Criterios de Aceptación — HU-04

**Happy Path**
```gherkin
CRITERIO-4.1: Catálogo de clasificación de riesgo disponible
  Dado que:  el mock server está activo con datos de migración
  Cuando:    el cotizador realiza GET /v1/catalogs/risk-classification
  Entonces:  recibe HTTP 200 con array de objetos { id, nombre, descripcion }
```

**Happy Path**
```gherkin
CRITERIO-4.2: Catálogo de garantías disponible
  Dado que:  el mock server está activo con datos de migración
  Cuando:    el cotizador realiza GET /v1/catalogs/guarantees
  Entonces:  recibe HTTP 200 con array de objetos { id, nombre, claveIncendio, tarifable }
```

**Edge Case**
```gherkin
CRITERIO-4.3: Catálogo configurado como vacío
  Dado que:  el escenario de mock para garantías está configurado para devolver lista vacía
  Cuando:    el cotizador realiza GET /v1/catalogs/guarantees
  Entonces:  recibe HTTP 200 con body []
```

---

#### HU-05: Simulación de Tarifas y Factores Técnicos (HU-096)

```
Como:        Desarrollador
Quiero:      Que el mock server simule los endpoints de tarifas (incendio, CAT, FHM) y factores técnicos
Para:        Probar la lógica de cálculo de primas con los 14 componentes técnicos (FT-018)

Prioridad:   Alta
Estimación:  L (5 story points)
Dependencias: HU-01 (HU-092)
Capa:        Backend (Node.js/Express)
```

#### Criterios de Aceptación — HU-05

**Happy Path**
```gherkin
CRITERIO-5.1: Tarifas de incendio disponibles
  Dado que:  el mock server está activo con datos de migración
  Cuando:    el cotizador realiza GET /v1/tariffs/fire
  Entonces:  recibe HTTP 200 con array de objetos de tarifa { zonaRiesgo, tipoConstructivo, tasaBase, factorRecargo }
```

**Happy Path**
```gherkin
CRITERIO-5.2: Factor CAT para zona específica
  Dado que:  la "ZONA_A" tiene factor CAT configurado
  Cuando:    el cotizador realiza GET /v1/tariffs/cat?zona=ZONA_A
  Entonces:  recibe HTTP 200 con { zona: "ZONA_A", factorTEV: 0.0015, factorFHM: 0.0008 }
```

**Happy Path**
```gherkin
CRITERIO-5.3: Factores de equipo electrónico disponibles
  Dado que:  existen factores técnicos de EE configurados
  Cuando:    el cotizador realiza GET /v1/tariffs/electronic-equipment
  Entonces:  recibe HTTP 200 con array de factores { clase, nivelZona, factor }
```

**Error Path**
```gherkin
CRITERIO-5.4: Tarifa no encontrada retorna 404
  Dado que:  se consulta una tarifa para una zona/tipo no configurado
  Cuando:    el cotizador realiza GET /v1/tariffs/cat?zona=ZONA_INEXISTENTE
  Entonces:  recibe HTTP 404 con { "message": "Tarifa no encontrada para los parámetros indicados", "code": "TARIFF_NOT_FOUND" }
```

**Happy Path**
```gherkin
CRITERIO-5.5: Endpoint GET y PUT de tarifas
  Dado que:  el mock soporta operaciones de lectura y actualización
  Cuando:    el cotizador realiza PUT /v1/tariffs/fire con body válido
  Entonces:  recibe HTTP 200 con la tarifa actualizada en el mock
```

---

#### HU-06: Migraciones de Datos con migrate-mongo (HU-097)

```
Como:        Desarrollador
Quiero:      Que la base de datos MongoDB del mock se pueble y actualice mediante migraciones versionadas
Para:        Mantener datos de prueba consistentes y reproducibles en todos los entornos

Prioridad:   Alta
Estimación:  M (5 story points)
Dependencias: HU-01 (HU-092)
Capa:        Backend (Node.js/Express + MongoDB)
```

#### Criterios de Aceptación — HU-06

**Happy Path**
```gherkin
CRITERIO-6.1: Migraciones iniciales se aplican al arranque
  Dado que:  la base de datos MongoDB está vacía
  Cuando:    el mock server se inicia
  Entonces:  migrate-mongo aplica todas las migraciones pendientes y los datos iniciales están disponibles
```

**Happy Path**
```gherkin
CRITERIO-6.2: Nuevas migraciones se detectan y aplican
  Dado que:  la base de datos está en versión N y existe una migración N+1
  Cuando:    el mock server se reinicia
  Entonces:  la migración N+1 se aplica y la base de datos queda en versión N+1
```

**Error Path**
```gherkin
CRITERIO-6.3: Migración con error aborta el inicio
  Dado que:  un script de migración contiene un error de sintaxis
  Cuando:    el mock server intenta iniciar
  Entonces:  el proceso registra el error detallado en logs y termina con exit code 1
```

**Edge Case**
```gherkin
CRITERIO-6.4: Sin migraciones pendientes no aplica nada
  Dado que:  la base de datos ya está en la última versión
  Cuando:    el mock server se inicia
  Entonces:  migrate-mongo registra "No pending migrations" y el servidor arranca normalmente
```

---

#### HU-07: Respuestas Dinámicas y Escenarios de Error (HU-098)

```
Como:        Desarrollador
Quiero:      Poder configurar respuestas dinámicas, retrasos y escenarios de error en el mock
Para:        Realizar pruebas de resiliencia del cotizador contra fallos del servicio externo

Prioridad:   Alta
Estimación:  M (4 story points)
Dependencias: HU-01 (HU-092)
Capa:        Backend (Node.js/Express)
```

#### Criterios de Aceptación — HU-07

**Happy Path**
```gherkin
CRITERIO-7.1: Configurar retraso artificial en endpoint
  Dado que:  el mock está configurado con delay de 2000ms para /v1/subscribers
  Cuando:    el cotizador realiza GET /v1/subscribers
  Entonces:  recibe la respuesta después de ~2000ms (±100ms de tolerancia)
```

**Happy Path**
```gherkin
CRITERIO-7.2: Forzar respuesta HTTP 500 en endpoint
  Dado que:  el mock está configurado para devolver 500 en /v1/agents
  Cuando:    el cotizador realiza GET /v1/agents
  Entonces:  recibe HTTP 500 con body { "message": "Servicio no disponible (simulado)", "code": "SERVICE_ERROR" }
```

**Happy Path**
```gherkin
CRITERIO-7.3: Devolver datos malformados para prueba de resiliencia
  Dado que:  el mock está configurado para devolver JSON inválido en /v1/catalogs/guarantees
  Cuando:    el cotizador realiza GET /v1/catalogs/guarantees
  Entonces:  recibe HTTP 200 con un body que no es JSON válido, y el cotizador lo maneja con error de deserialización
```

**Edge Case**
```gherkin
CRITERIO-7.4: Valor de delay inválido es rechazado
  Dado que:  se intenta configurar un delay con valor negativo
  Cuando:    se envía POST /_mock/scenarios con { "delay_ms": -1000 }
  Entonces:  el mock retorna HTTP 400 con mensaje de validación
```

---

#### HU-08: Pruebas de Estabilidad bajo Carga (HU-099)

```
Como:        Desarrollador
Quiero:      Validar la estabilidad del mock server bajo carga concurrente
Para:        Asegurar que es un reemplazo fiable del servicio real en entornos de CI/CD

Prioridad:   Media
Estimación:  M (5 story points)
Dependencias: HU-01 a HU-06 (HU-092 a HU-096)
Capa:        Backend (Node.js/Express) + QA
```

#### Criterios de Aceptación — HU-08

**Happy Path**
```gherkin
CRITERIO-8.1: Estabilidad bajo carga de 100 RPS
  Dado que:  el mock server está operativo con datos de migración
  Cuando:    se ejecuta una prueba de carga con 100 solicitudes/segundo durante 60 segundos
  Entonces:  el mock responde consistentemente sin caídas, tasa de error < 0.1%
```

**Happy Path**
```gherkin
CRITERIO-8.2: Tiempo de respuesta aceptable bajo carga
  Dado que:  el mock server está bajo carga de 100 RPS
  Cuando:    se mide el percentil 95 de latencia
  Entonces:  P95 < 150ms para endpoints de catálogos y P95 < 50ms para health check
```

**Edge Case**
```gherkin
CRITERIO-8.3: Degradación gradual identificada
  Dado que:  la carga supera el umbral configurado (>200 RPS)
  Cuando:    se analiza el reporte de la prueba de carga
  Entonces:  se identifican los cuellos de botella y se documentan en el reporte de estabilidad
```

---

### Reglas de Negocio

1. El mock server NO requiere autenticación para sus endpoints — es exclusivo de entornos dev/test.
2. Los endpoints deben replicar exactamente el contrato de `Plataforma-core-ohs`: mismas rutas `/v1/...`, mismos campos en request/response, mismos códigos HTTP.
3. Los datos de prueba se cargan exclusivamente vía migraciones de `migrate-mongo` — no se permiten inserciones manuales en el código de la aplicación.
4. El formato de error estándar del mock es `{ "message": "<texto>", "code": "<CODIGO_ERROR>" }`.
5. El mock server debe soportar CORS para permitir peticiones desde el frontend Next.js en desarrollo local.
6. El puerto por defecto es `3001` para no colisionar con el backend Spring Boot (8080) ni el frontend Next.js (3000).
7. Los escenarios dinámicos (delays, errores forzados) se configuran via archivo `.env` o endpoint `POST /_mock/scenarios` — no requieren reinicio del servidor.
8. Cada migración de `migrate-mongo` debe tener la estructura `V{N}__{descripcion}.js` y contener funciones `up` y `down`.
9. El folio generado por `/v1/folios` debe seguir el patrón `COT-AAAA-NNNNNN` con año actual y secuencia de 6 dígitos.
10. Los campos `zonaCAT` válidos son: `ZONA_A`, `ZONA_B`, `ZONA_C`, `ZONA_D`. Los `nivelTecnico` válidos son: `ALTO`, `MEDIO`, `BAJO`.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `subscribers` | MongoDB colección `subscribers` | nueva | Catálogo de suscriptores |
| `agents` | MongoDB colección `agents` | nueva | Catálogo de agentes |
| `businessLines` | MongoDB colección `businessLines` | nueva | Catálogo de giros (líneas de negocio) |
| `zipCodes` | MongoDB colección `zipCodes` | nueva | Catálogo de CP con zona CAT y nivel técnico |
| `riskClassifications` | MongoDB colección `riskClassifications` | nueva | Catálogo de clasificación de riesgo |
| `guarantees` | MongoDB colección `guarantees` | nueva | Catálogo de garantías tarifables |
| `tariffsFire` | MongoDB colección `tariffsFire` | nueva | Tarifas de incendio por zona y tipo constructivo |
| `tariffsCat` | MongoDB colección `tariffsCat` | nueva | Factores CAT TEV y FHM por zona |
| `tariffsElectronicEquipment` | MongoDB colección `tariffsElectronicEquipment` | nueva | Factores técnicos de equipo electrónico |
| `mockScenarios` | MongoDB colección `mockScenarios` | nueva | Escenarios dinámicos configurados (delays, errores) |

#### Campos del modelo — `subscribers`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `_id` | ObjectId | sí | auto | ID interno MongoDB |
| `id` | String | sí | alfanumérico, único | ID de negocio (ej. SUB-001) |
| `nombre` | String | sí | max 200 chars | Nombre del suscriptor |
| `clave` | String | sí | max 50 chars | Clave del suscriptor |
| `activo` | Boolean | sí | — | Estado activo |

#### Campos del modelo — `zipCodes`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `_id` | ObjectId | sí | auto | ID interno MongoDB |
| `codigoPostal` | String | sí | `^\d{5}$`, único | CP de 5 dígitos |
| `zonaCAT` | String | sí | enum: `ZONA_A`\|`ZONA_B`\|`ZONA_C`\|`ZONA_D` | Zona catastrófica |
| `nivelTecnico` | String | sí | enum: `ALTO`\|`MEDIO`\|`BAJO` | Nivel técnico |
| `estado` | String | sí | max 100 chars | Estado de la república |
| `municipio` | String | sí | max 200 chars | Municipio o alcaldía |
| `ciudad` | String | no | max 200 chars | Ciudad |

#### Campos del modelo — `businessLines`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `_id` | ObjectId | sí | auto | ID interno MongoDB |
| `id` | String | sí | alfanumérico, único | ID de negocio |
| `descripcion` | String | sí | max 300 chars | Descripción del giro |
| `claveIncendio` | String | sí | max 20 chars | Clave para cálculo incendio |
| `activo` | Boolean | sí | — | Estado activo |

#### Campos del modelo — `tariffsFire`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `_id` | ObjectId | sí | auto | ID interno MongoDB |
| `zonaRiesgo` | String | sí | enum: `ZONA_A`\|`ZONA_B`\|`ZONA_C`\|`ZONA_D` | Zona de riesgo |
| `tipoConstructivo` | String | sí | max 50 chars | Tipo constructivo |
| `tasaBase` | Number | sí | > 0 | Tasa base de incendio |
| `factorRecargo` | Number | sí | >= 1 | Factor de recargo |
| `vigenciaDesde` | Date | sí | ISO8601 | Vigencia de inicio |
| `vigenciaHasta` | Date | no | ISO8601 | Vigencia de fin |

#### Campos del modelo — `mockScenarios`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `_id` | ObjectId | sí | auto | ID interno MongoDB |
| `endpointPath` | String | sí | URL path, único | Endpoint afectado |
| `scenarioType` | String | sí | enum: `DELAY`\|`HTTP_ERROR`\|`MALFORMED_DATA`\|`NORMAL` | Tipo de escenario |
| `delayMs` | Number | no | >= 0, solo si `scenarioType=DELAY` | Retraso en ms |
| `httpStatusCode` | Number | no | 400-599, solo si `scenarioType=HTTP_ERROR` | Código HTTP forzado |
| `responseBody` | String | no | — | Cuerpo de respuesta forzado |
| `activo` | Boolean | sí | — | Escenario activo |

#### Índices / Constraints

- `zipCodes.codigoPostal`: índice único — búsqueda frecuente por CP
- `subscribers.id`: índice único — ID de negocio
- `agents.id`: índice único — ID de negocio
- `businessLines.id`: índice único — ID de negocio
- `mockScenarios.endpointPath`: índice único — un escenario por endpoint

---

### API Endpoints

#### GET /health
- **Descripción**: Health check del mock server
- **Auth requerida**: no
- **Response 200**:
  ```json
  { "status": "UP", "service": "plataforma-core-ohs-mock", "timestamp": "2026-04-24T00:00:00.000Z" }
  ```

---

#### GET /v1/subscribers
- **Descripción**: Lista todos los suscriptores del catálogo
- **Auth requerida**: no
- **Response 200**:
  ```json
  [{ "id": "SUB-001", "nombre": "Grupo Asegurador XYZ", "clave": "GAX", "activo": true }]
  ```
- **Response 500**: escenario de error configurado

#### GET /v1/agents
- **Descripción**: Lista todos los agentes del catálogo
- **Auth requerida**: no
- **Response 200**:
  ```json
  [{ "id": "AGT-001", "nombre": "Juan Pérez García", "clave": "JPG", "activo": true }]
  ```

#### GET /v1/business-lines
- **Descripción**: Lista todos los giros (líneas de negocio) del catálogo
- **Auth requerida**: no
- **Response 200**:
  ```json
  [{ "id": "GIR-001", "descripcion": "Manufactura ligera", "claveIncendio": "MAN-L", "activo": true }]
  ```

---

#### GET /v1/zip-codes/:zipCode
- **Descripción**: Consulta un código postal y retorna su zona CAT y datos geográficos
- **Auth requerida**: no
- **Path Param**: `zipCode` — 5 dígitos numéricos
- **Response 200**:
  ```json
  {
    "codigoPostal": "06600",
    "zonaCAT": "ZONA_A",
    "nivelTecnico": "ALTO",
    "estado": "Ciudad de México",
    "municipio": "Cuauhtémoc",
    "ciudad": "Ciudad de México"
  }
  ```
- **Response 400**: formato de CP inválido (`{ "message": "Formato de código postal inválido", "code": "INVALID_ZIP_FORMAT" }`)
- **Response 404**: CP no encontrado (`{ "message": "Código postal no encontrado", "code": "ZIP_NOT_FOUND" }`)

#### POST /v1/zip-codes/validate
- **Descripción**: Valida múltiples CPs en una sola petición
- **Auth requerida**: no
- **Request Body**:
  ```json
  { "zipCodes": ["06600", "44100", "99999"] }
  ```
- **Response 200**:
  ```json
  {
    "results": [
      { "codigoPostal": "06600", "valido": true, "zonaCAT": "ZONA_A", "nivelTecnico": "ALTO" },
      { "codigoPostal": "99999", "valido": false, "error": "Código postal no encontrado" }
    ]
  }
  ```
- **Response 400**: body malformado o array vacío

---

#### GET /v1/catalogs/risk-classification
- **Descripción**: Lista las clasificaciones de riesgo disponibles
- **Auth requerida**: no
- **Response 200**:
  ```json
  [{ "id": "CR-001", "nombre": "Riesgo Bajo", "descripcion": "Edificaciones de concreto con bajo riesgo operacional" }]
  ```

#### GET /v1/catalogs/guarantees
- **Descripción**: Lista las garantías tarifables disponibles
- **Auth requerida**: no
- **Response 200**:
  ```json
  [{ "id": "G-001", "nombre": "Incendio Edificios", "claveIncendio": "INC-EDI", "tarifable": true }]
  ```

---

#### GET /v1/tariffs/fire
- **Descripción**: Lista las tarifas de incendio por zona y tipo constructivo
- **Auth requerida**: no
- **Query Params opcionales**: `zona`, `tipoConstructivo`
- **Response 200**:
  ```json
  [{ "zonaRiesgo": "ZONA_A", "tipoConstructivo": "Concreto", "tasaBase": 0.0012, "factorRecargo": 1.15 }]
  ```

#### GET /v1/tariffs/cat
- **Descripción**: Retorna factores CAT TEV y FHM para una zona
- **Auth requerida**: no
- **Query Params**: `zona` (requerido)
- **Response 200**:
  ```json
  { "zona": "ZONA_A", "factorTEV": 0.0015, "factorFHM": 0.0008 }
  ```
- **Response 404**: zona no encontrada

#### PUT /v1/tariffs/fire
- **Descripción**: Actualiza una tarifa de incendio en el mock (para pruebas dinámicas)
- **Auth requerida**: no
- **Request Body**: objeto de tarifa con campos a actualizar
- **Response 200**: tarifa actualizada

#### GET /v1/tariffs/electronic-equipment
- **Descripción**: Lista los factores técnicos de equipo electrónico
- **Auth requerida**: no
- **Response 200**:
  ```json
  [{ "clase": "A", "nivelZona": "ALTO", "factor": 0.0025 }]
  ```

---

#### GET /v1/folios
- **Descripción**: Genera un nuevo folio alfanumérico único
- **Auth requerida**: no
- **Response 200**:
  ```json
  { "folio": "COT-2026-000001" }
  ```

---

#### POST /_mock/scenarios
- **Descripción**: Configura un escenario dinámico para un endpoint (delay, error, datos malformados)
- **Auth requerida**: no (interno dev/test)
- **Request Body**:
  ```json
  {
    "endpointPath": "/v1/subscribers",
    "scenarioType": "HTTP_ERROR",
    "httpStatusCode": 500,
    "responseBody": "{\"message\": \"Servicio no disponible (simulado)\", \"code\": \"SERVICE_ERROR\"}"
  }
  ```
- **Response 200**: escenario configurado
- **Response 400**: datos de configuración inválidos

#### DELETE /_mock/scenarios/:endpointPath
- **Descripción**: Elimina el escenario configurado para un endpoint, restaurando comportamiento normal
- **Auth requerida**: no
- **Response 204**: escenario eliminado

---

### Arquitectura y Dependencias

El módulo `plataforma-core-ohs` ya existe como proyecto Node.js/Express con las siguientes dependencias instaladas:

| Dependencia | Versión | Uso |
|-------------|---------|-----|
| `express` | ^4.22.1 | Servidor HTTP |
| `mongoose` | ^8.23.0 | ODM para MongoDB |
| `migrate-mongo` | ^11.0.0 | Migraciones de datos |
| `cors` | ^2.8.6 | CORS para dev local |
| `express-validator` | ^7.3.2 | Validación de request params |
| `dotenv` | ^16.6.1 | Variables de entorno |

**Estructura de archivos propuesta:**

```
plataforma-core-ohs/
├── src/
│   ├── index.js                    # Entry point — Express app + migrate-mongo
│   ├── config/
│   │   └── database.js             # Conexión MongoDB via mongoose
│   ├── routes/
│   │   ├── subscribers.js          # GET /v1/subscribers
│   │   ├── agents.js               # GET /v1/agents
│   │   ├── businessLines.js        # GET /v1/business-lines
│   │   ├── zipCodes.js             # GET /v1/zip-codes/:cp, POST /v1/zip-codes/validate
│   │   ├── catalogs.js             # GET /v1/catalogs/risk-classification, /guarantees
│   │   ├── tariffs.js              # GET|PUT /v1/tariffs/...
│   │   ├── folios.js               # GET /v1/folios
│   │   └── mockScenarios.js        # POST|DELETE /_mock/scenarios
│   ├── models/
│   │   ├── Subscriber.js
│   │   ├── Agent.js
│   │   ├── BusinessLine.js
│   │   ├── ZipCode.js
│   │   ├── RiskClassification.js
│   │   ├── Guarantee.js
│   │   ├── TariffFire.js
│   │   ├── TariffCat.js
│   │   ├── TariffElectronicEquipment.js
│   │   └── MockScenario.js
│   ├── middleware/
│   │   └── mockScenarioInterceptor.js  # Middleware que aplica escenarios dinámicos
│   └── migrations/                 # migrate-mongo migrations
│       ├── migrate-mongo-config.js
│       ├── V1__initial_subscribers.js
│       ├── V2__initial_agents.js
│       ├── V3__initial_business_lines.js
│       ├── V4__initial_zip_codes.js
│       ├── V5__initial_risk_classifications.js
│       ├── V6__initial_guarantees.js
│       ├── V7__initial_tariffs_fire.js
│       ├── V8__initial_tariffs_cat.js
│       └── V9__initial_tariffs_electronic_equipment.js
├── .env.example
└── package.json
```

**Variables de entorno requeridas:**

```env
PORT=3001
MONGODB_URI=mongodb://localhost:27017/plataforma-core-ohs-mock
NODE_ENV=development
```

**Notas de Implementación:**

- El middleware `mockScenarioInterceptor.js` se ejecuta ANTES de los handlers de ruta. Al recibir una petición, consulta la colección `mockScenarios` para el path actual. Si existe un escenario activo:
  - `DELAY`: usa `setTimeout` antes de continuar con el handler normal
  - `HTTP_ERROR`: retorna inmediatamente con el código y body configurados
  - `MALFORMED_DATA`: retorna el string de `responseBody` sin parsear como JSON
  - `NORMAL`: sin efecto (pasa al handler)
- La generación de folios en `/v1/folios` usa un contador atómico con `findOneAndUpdate` y `$inc` para garantizar secuencialidad sin race conditions.
- `migrate-mongo` se ejecuta al inicio del servidor en `src/index.js` antes de levantar Express, usando `await migrateMongoUp()`.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.

### Backend

#### Implementación — Infraestructura base (HU-092)

- [ ] Crear `src/config/database.js` con conexión Mongoose a MongoDB
- [ ] Crear `src/index.js` con Express app, CORS, body-parser, ejecución de migraciones y arranque del servidor
- [ ] Crear `.env.example` con variables `PORT`, `MONGODB_URI`, `NODE_ENV`
- [ ] Configurar `src/migrations/migrate-mongo-config.js` apuntando a `MONGODB_URI`
- [ ] Implementar `GET /health` con respuesta `{ status: "UP", service: "plataforma-core-ohs-mock" }`
- [ ] Implementar `src/middleware/mockScenarioInterceptor.js` con lógica DELAY/HTTP_ERROR/MALFORMED_DATA

#### Implementación — Modelos MongoDB

- [ ] Crear modelo `Subscriber.js` con campos: id, nombre, clave, activo
- [ ] Crear modelo `Agent.js` con campos: id, nombre, clave, activo
- [ ] Crear modelo `BusinessLine.js` con campos: id, descripcion, claveIncendio, activo
- [ ] Crear modelo `ZipCode.js` con campos: codigoPostal, zonaCAT, nivelTecnico, estado, municipio, ciudad
- [ ] Crear modelo `RiskClassification.js` con campos: id, nombre, descripcion
- [ ] Crear modelo `Guarantee.js` con campos: id, nombre, claveIncendio, tarifable
- [ ] Crear modelo `TariffFire.js` con campos: zonaRiesgo, tipoConstructivo, tasaBase, factorRecargo, vigenciaDesde, vigenciaHasta
- [ ] Crear modelo `TariffCat.js` con campos: zona, factorTEV, factorFHM
- [ ] Crear modelo `TariffElectronicEquipment.js` con campos: clase, nivelZona, factor
- [ ] Crear modelo `MockScenario.js` con campos: endpointPath, scenarioType, delayMs, httpStatusCode, responseBody, activo

#### Implementación — Rutas

- [ ] Crear `src/routes/subscribers.js` con `GET /v1/subscribers`
- [ ] Crear `src/routes/agents.js` con `GET /v1/agents`
- [ ] Crear `src/routes/businessLines.js` con `GET /v1/business-lines`
- [ ] Crear `src/routes/zipCodes.js` con `GET /v1/zip-codes/:zipCode` y `POST /v1/zip-codes/validate` (con validación express-validator para formato de CP)
- [ ] Crear `src/routes/catalogs.js` con `GET /v1/catalogs/risk-classification` y `GET /v1/catalogs/guarantees`
- [ ] Crear `src/routes/tariffs.js` con `GET /v1/tariffs/fire`, `PUT /v1/tariffs/fire`, `GET /v1/tariffs/cat`, `GET /v1/tariffs/electronic-equipment`
- [ ] Crear `src/routes/folios.js` con `GET /v1/folios` y lógica de secuencia atómica
- [ ] Crear `src/routes/mockScenarios.js` con `POST /_mock/scenarios` y `DELETE /_mock/scenarios/:endpointPath`
- [ ] Registrar todos los routers en `src/index.js`

#### Implementación — Migraciones de datos

- [ ] Crear migración `V1__initial_subscribers.js` con ≥5 suscriptores representativos (up + down)
- [ ] Crear migración `V2__initial_agents.js` con ≥5 agentes representativos (up + down)
- [ ] Crear migración `V3__initial_business_lines.js` con ≥10 giros con `claveIncendio` (up + down)
- [ ] Crear migración `V4__initial_zip_codes.js` con ≥20 CPs de diferentes zonas (A, B, C, D) y estados (up + down)
- [ ] Crear migración `V5__initial_risk_classifications.js` con ≥3 clasificaciones (up + down)
- [ ] Crear migración `V6__initial_guarantees.js` con las 14 garantías del motor de cálculo (up + down)
- [ ] Crear migración `V7__initial_tariffs_fire.js` con tarifas para todas las zonas y tipos constructivos (up + down)
- [ ] Crear migración `V8__initial_tariffs_cat.js` con factores CAT para zonas A, B, C, D (up + down)
- [ ] Crear migración `V9__initial_tariffs_electronic_equipment.js` con factores por clase y nivel (up + down)

#### Tests Backend

- [ ] `test_health_returns_200` — verifica que GET /health retorna 200
- [ ] `test_subscribers_returns_list` — happy path lista de suscriptores
- [ ] `test_agents_returns_empty_list` — catálogo vacío retorna 200 con []
- [ ] `test_zip_code_valid_returns_zona` — GET /v1/zip-codes/06600 retorna zona correcta
- [ ] `test_zip_code_not_found_returns_404` — GET /v1/zip-codes/99999 retorna 404
- [ ] `test_zip_code_invalid_format_returns_400` — GET /v1/zip-codes/ABCDE retorna 400
- [ ] `test_zip_codes_validate_batch` — POST /v1/zip-codes/validate con array mixto
- [ ] `test_tariffs_fire_returns_list` — GET /v1/tariffs/fire retorna tarifas
- [ ] `test_tariffs_cat_zone_not_found_returns_404` — zona inexistente retorna 404
- [ ] `test_folio_generation_sequential` — GET /v1/folios retorna folio con patrón COT-AAAA-NNNNNN
- [ ] `test_mock_scenario_delay_applied` — escenario DELAY retrasa la respuesta
- [ ] `test_mock_scenario_http_error_applied` — escenario HTTP_ERROR retorna el código configurado
- [ ] `test_mock_scenario_invalid_delay_returns_400` — delay negativo es rechazado
- [ ] `test_migration_applies_on_empty_db` — base de datos vacía recibe migraciones correctamente

### Frontend

_No aplica — esta feature es exclusivamente de backend/infraestructura. No genera componentes, páginas ni hooks en el frontend Next.js._

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 a CRITERIO-8.3
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD de riesgos (desactualización del contrato, performance, datos no representativos)
- [ ] Validar prueba de carga con JMeter/Artillery a 100 RPS durante 60s → P95 < 150ms, error rate < 0.1%
- [ ] Verificar que cada migración tiene función `up` y `down` funcionales
- [ ] Validar que todos los campos de respuesta coinciden con los contratos esperados por el backend Spring Boot (FT-015 a FT-018)
- [ ] Smoke test de integración: arrancar mock server y ejecutar una solicitud a cada endpoint principal
- [ ] Revisar cobertura de tests contra criterios de aceptación
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
