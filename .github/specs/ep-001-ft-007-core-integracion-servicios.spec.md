---
id: SPEC-009
status: APPROVED
feature: ep-001-ft-007-core-integracion-servicios
created: 2026-04-28
updated: 2026-04-28
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-003
  - SPEC-004
  - SPEC-005
  - SPEC-006
  - SPEC-007
  - SPEC-008
---

# Spec: EP-001 FT-007 — Integración de Servicios con Plataforma-core-ohs

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa la capa de integración del cotizador (`plataformas-danos-back`) con el servicio de referencia `plataforma-core-ohs` para soportar el flujo completo de cotización del EP-001. Abarca seis dimensiones: consumo de catálogos básicos (suscriptores, agentes, giros), validación de códigos postales con zona de riesgo, obtención de catálogos de clasificación de riesgo y garantías, consulta de tarifas y factores técnicos, resiliencia mediante Circuit Breaker/Retry, y habilitación de un modo de simulación para desarrollo y pruebas. Los clientes HTTP, servicios con caché Caffeine y Resilience4j, y los controladores REST que exponen los datos al frontend se consideran parte de esta feature.

### Requerimiento de Negocio

El cotizador de seguros de daños requiere datos de referencia actualizados (catálogos, códigos postales, tarifas) provenientes de `plataforma-core-ohs` para que los usuarios puedan crear, editar y calcular cotizaciones con información válida. La integración debe ser resiliente ante fallos del servicio externo, almacenar los catálogos en caché para minimizar latencia, y permitir operar en modo de simulación cuando el servicio real no esté disponible (entornos de desarrollo y pruebas).

### Historias de Usuario

---

#### HU-029: Consumir catálogos de suscriptores, agentes y giros

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Consultar los catálogos de suscriptores, agentes y giros desde plataforma-core-ohs
Para:        Proveer listas de selección válidas y actualizadas en el formulario de cotización

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: plataforma-core-ohs operativo (real o mock)
Capa:        Backend
```

#### Criterios de Aceptación — HU-029

**Happy Path**
```gherkin
CRITERIO-29.1: Catálogos cargados exitosamente con caché
  Dado que:  plataforma-core-ohs está activo y devuelve datos para suscriptores, agentes y giros
  Cuando:    el cotizador invoca GET /api/v1/catalogs/subscribers (o /agents, /business-lines)
  Entonces:  recibe HTTP 200 con un array de objetos con al menos los campos id y nombre
             y la respuesta se almacena en caché Caffeine (TTL 12 horas)
             y el segundo llamado devuelve datos desde caché sin llamar al servicio externo
```

**Error Path**
```gherkin
CRITERIO-29.2: Servicio de catálogos no disponible tras reintentos → HTTP 503
  Dado que:  plataforma-core-ohs no responde en ninguno de los 3 reintentos configurados
  Cuando:    el cotizador invoca GET /api/v1/catalogs/subscribers (o /agents, /business-lines)
  Entonces:  recibe HTTP 503 con body { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
             y se registra log CRITICAL con detalles del fallo y nombre del catálogo afectado
```

**Edge Case**
```gherkin
CRITERIO-29.3: Registro sin campo id u nombre se descarta — resto se retorna
  Dado que:  plataforma-core-ohs devuelve un array donde un elemento omite el campo id
  Cuando:    el cotizador procesa la respuesta
  Entonces:  ese registro se descarta y se registra log WARNING con el detalle
             y los demás registros válidos se retornan normalmente con HTTP 200
```

---

#### HU-030: Validar código postal y obtener zona de riesgo

```
Como:        Sistema (cotizador)
Quiero:      Consultar y validar un código postal desde plataforma-core-ohs y obtener su zona de riesgo
Para:        Autocompletar municipio/estado y aplicar el factor de riesgo geográfico en el cálculo de primas

Prioridad:   Alta
Estimación:  M (4 story points)
Dependencias: HU-029 (conexión con plataforma-core-ohs establecida)
Capa:        Backend
```

#### Criterios de Aceptación — HU-030

**Happy Path**
```gherkin
CRITERIO-30.1: Código postal válido devuelve municipio, estado y zona de riesgo
  Dado que:  el usuario introduce un código postal de 5 dígitos válido (ej. "06600")
  Cuando:    el cotizador invoca GET /api/v1/zip-codes/06600
  Entonces:  recibe HTTP 200 con { zipCode, municipio, estado, zonaRiesgo }
             y la respuesta se almacena en caché por código postal (TTL 1 hora)
```

**Error Path**
```gherkin
CRITERIO-30.2: Código postal no encontrado en catálogo → HTTP 404
  Dado que:  el usuario introduce un código postal que no existe en plataforma-core-ohs (ej. "00000")
  Cuando:    el cotizador invoca GET /api/v1/zip-codes/00000
  Entonces:  recibe HTTP 404 con body { "message": "Código postal no encontrado", "code": "ZIP_CODE_NOT_FOUND" }
```

**Error Path**
```gherkin
CRITERIO-30.3: Formato inválido (no 5 dígitos) → HTTP 400
  Dado que:  el usuario envía un código postal con formato incorrecto (ej. "1234" o "ABCDE")
  Cuando:    el cotizador invoca GET /api/v1/zip-codes/1234
  Entonces:  recibe HTTP 400 con body { "message": "Formato de código postal inválido", "code": "INVALID_ZIP_CODE_FORMAT" }
             y NO se realiza llamada al servicio externo
```

**Error Path**
```gherkin
CRITERIO-30.4: Servicio de CP no disponible → HTTP 503
  Dado que:  plataforma-core-ohs no responde para la consulta de CP tras los reintentos
  Cuando:    el cotizador invoca GET /api/v1/zip-codes/{zipCode}
  Entonces:  recibe HTTP 503 con body { "message": "Servicio de validación de CP no disponible", "code": "ZIP_CODE_SERVICE_UNAVAILABLE" }
```

**Edge Case**
```gherkin
CRITERIO-30.5: Respuesta con datos incompletos aplica valores por defecto
  Dado que:  plataforma-core-ohs responde pero omite el campo zonaRiesgo
  Cuando:    el cotizador procesa la respuesta
  Entonces:  el campo zonaRiesgo toma el valor por defecto configurado (DEFAULT_ZONA)
             y se registra log WARNING indicando el dato faltante
```

---

#### HU-031: Obtener catálogos de clasificación de riesgo y garantías

```
Como:        Sistema (cotizador)
Quiero:      Consultar catálogos de clasificación de riesgo y garantías desde plataforma-core-ohs
Para:        Poblar las opciones de clasificación de ubicaciones y coberturas en el proceso de cotización

Prioridad:   Media
Estimación:  M (3 story points)
Dependencias: HU-029
Capa:        Backend
```

#### Criterios de Aceptación — HU-031

**Happy Path**
```gherkin
CRITERIO-31.1: Catálogos de clasificación de riesgo cargados exitosamente
  Dado que:  plataforma-core-ohs está activo y responde con clasificaciones de riesgo
  Cuando:    el cotizador invoca GET /api/v1/catalogs/risk-classifications
  Entonces:  recibe HTTP 200 con array de { id, nombre } de clasificaciones de riesgo
             y la respuesta se almacena en caché (TTL 12 horas)
```

**Happy Path**
```gherkin
CRITERIO-31.2: Catálogos de garantías cargados exitosamente
  Dado que:  plataforma-core-ohs está activo y responde con garantías
  Cuando:    el cotizador invoca GET /api/v1/catalogs/guarantees
  Entonces:  recibe HTTP 200 con array de { id, nombre } de garantías
             y la respuesta se almacena en caché (TTL 12 horas)
```

**Error Path**
```gherkin
CRITERIO-31.3: Servicio no disponible → fallback a datos predefinidos o HTTP 503
  Dado que:  plataforma-core-ohs no responde para clasificaciones o garantías
  Cuando:    el cotizador agota los reintentos
  Entonces:  si hay datos predefinidos configurados, se retornan con HTTP 200 y log WARNING indicando uso de fallback
             si no hay datos predefinidos, se retorna HTTP 503 con body { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-032: Consultar tarifas y factores técnicos

```
Como:        Motor de cálculo (parte del cotizador)
Quiero:      Consultar tarifas de incendio, CAT y equipo electrónico desde plataforma-core-ohs
Para:        Realizar cálculos de prima precisos con datos actualizados

Prioridad:   Alta
Estimación:  L (5 story points)
Dependencias: HU-029, FT-004 (motor de cálculo)
Capa:        Backend
```

#### Criterios de Aceptación — HU-032

**Happy Path**
```gherkin
CRITERIO-32.1: Tarifas de incendio consultadas exitosamente
  Dado que:  se inicia un cálculo de prima y plataforma-core-ohs está disponible
  Cuando:    el motor de cálculo solicita tarifas de incendio
  Entonces:  GET /api/v1/tariffs/fire retorna HTTP 200 con lista de tarifas { id, tipo, valor }
             y la respuesta se almacena en caché (TTL 6 horas)
```

**Happy Path**
```gherkin
CRITERIO-32.2: Tarifa CAT consultada con parámetro de zona geográfica
  Dado que:  el cálculo requiere tarifa CAT para una zona específica (ej. "Norte")
  Cuando:    el motor de cálculo invoca GET /api/v1/tariffs/cat/Norte
  Entonces:  recibe HTTP 200 con la tarifa CAT correspondiente a esa zona
```

**Happy Path**
```gherkin
CRITERIO-32.3: Tarifas de equipo electrónico consultadas exitosamente
  Dado que:  el cálculo requiere tarifas de equipo electrónico
  Cuando:    el motor de cálculo invoca GET /api/v1/tariffs/electronic-equipment
  Entonces:  recibe HTTP 200 con lista de tarifas { id, tipo, valor }
             y la respuesta se almacena en caché (TTL 6 horas)
```

**Error Path**
```gherkin
CRITERIO-32.4: Tarifa CAT no encontrada para zona → HTTP 404 sin reintento
  Dado que:  la zona solicitada no tiene tarifa CAT en el catálogo
  Cuando:    el motor de cálculo invoca GET /api/v1/tariffs/cat/{zona}
  Entonces:  recibe HTTP 404 con body { "message": "Tarifa CAT no encontrada para la zona indicada", "code": "TARIFF_NOT_FOUND" }
             y NO se realizan reintentos (error 4xx es no recuperable)
```

**Error Path**
```gherkin
CRITERIO-32.5: Servicio de tarifas no disponible → cálculo abortado → HTTP 503
  Dado que:  plataforma-core-ohs no responde para tarifas de incendio tras reintentos
  Cuando:    el motor de cálculo solicita tarifas
  Entonces:  recibe HTTP 503 con body { "message": "Servicio de tarifas no disponible", "code": "TARIFF_SERVICE_UNAVAILABLE" }
             y el cálculo de prima NO se continúa
             y se registra log CRITICAL con el tipo de tarifa afectada y número de intentos
```

---

#### HU-033: Resiliencia en integraciones con plataforma-core-ohs

```
Como:        Desarrollador
Quiero:      Que todas las integraciones con plataforma-core-ohs usen Circuit Breaker, Retry y timeouts
Para:        Garantizar la estabilidad del cotizador ante fallos transitorios o permanentes del servicio externo

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: HU-029, HU-030, HU-031, HU-032
Capa:        Backend
```

#### Criterios de Aceptación — HU-033

**Happy Path**
```gherkin
CRITERIO-33.1: Reintento exitoso recupera la operación
  Dado que:  plataforma-core-ohs falla en el primer intento pero responde en el segundo
  Cuando:    el cotizador realiza cualquier llamada a plataforma-core-ohs
  Entonces:  el sistema reintenta automáticamente con backoff exponencial
             y retorna la respuesta exitosa del segundo intento
             y registra log INFO indicando recuperación tras reintento
```

**Error Path**
```gherkin
CRITERIO-33.2: Reintentos agotados activan fallback / HTTP 503
  Dado que:  plataforma-core-ohs no responde en ninguno de los 3 intentos configurados
  Cuando:    el sistema agota los reintentos
  Entonces:  se activa el método de fallback configurado
             y se registra log CRITICAL con el servicio afectado, número de intentos y último error
             y el cliente recibe HTTP 503 con código de error canónico
```

**Edge Case**
```gherkin
CRITERIO-33.3: Errores HTTP 4xx no activan reintentos
  Dado que:  plataforma-core-ohs responde con HTTP 400 o 404
  Cuando:    el cotizador procesa la respuesta
  Entonces:  NO se realizan reintentos
             y se registra log ERROR con el código HTTP y el endpoint invocado
```

**Edge Case**
```gherkin
CRITERIO-33.4: Timeout configurado de 5 segundos finaliza la espera
  Dado que:  plataforma-core-ohs tarda más de 5000ms en responder
  Cuando:    el cotizador realiza una llamada
  Entonces:  la llamada se cancela por timeout
             y se registra log WARN con el tipo de operación y el tiempo transcurrido
             y se activa el mecanismo de reintento si corresponde
```

---

#### HU-034: Habilitación de modo de simulación para desarrollo y pruebas

```
Como:        Desarrollador
Quiero:      Poder configurar el sistema para usar plataforma-core-ohs (mock local) como sustituto del servicio real
Para:        Desarrollar y ejecutar pruebas sin depender del servicio de referencia real

Prioridad:   Media
Estimación:  S (2 story points)
Dependencias: Ninguna
Capa:        Backend
```

#### Criterios de Aceptación — HU-034

**Happy Path**
```gherkin
CRITERIO-34.1: Configuración de URL del mock mediante variable de entorno
  Dado que:  el desarrollador configura PLATAFORMA_CORE_OHS_URL=http://localhost:3001
  Cuando:    el cotizador arranca y realiza llamadas a plataforma-core-ohs
  Entonces:  todas las llamadas se dirigen al mock local (http://localhost:3001)
             y los logs indican la URL base configurada al inicio
```

**Happy Path**
```gherkin
CRITERIO-34.2: Datos predefinidos del mock son representativos y versionados
  Dado que:  el mock de plataforma-core-ohs está activo con datos de prueba v1.x
  Cuando:    el cotizador consulta suscriptores, agentes, giros, CPs y tarifas
  Entonces:  recibe respuestas coherentes con la estructura de datos del servicio real
             y los datos de prueba están versionados y gestionados en el repositorio del mock
```

**Edge Case**
```gherkin
CRITERIO-34.3: Alternancia entre mock y servicio real mediante configuración
  Dado que:  el sistema está configurado apuntando al mock
  Cuando:    se modifica PLATAFORMA_CORE_OHS_URL al endpoint real y se reinicia
  Entonces:  todas las llamadas se dirigen al servicio real sin cambios en el código
```

---

### Reglas de Negocio

1. **Caché por catálogo con TTL diferenciado**: catálogos básicos (suscriptores, agentes, giros, clasificación, garantías) → TTL 12 horas; tarifas de incendio y equipo electrónico → TTL 6 horas; tarifas CAT → TTL 1 hora; códigos postales → TTL 1 hora.
2. **Solo errores 5xx y errores de red activan reintentos**: errores 4xx (incluyendo 404 y 400) son no recuperables y no deben reintentar.
3. **Política de reintentos**: máximo 3 intentos, delay inicial 1000ms, multiplicador exponencial 2.0, delay máximo 8000ms.
4. **Circuit Breaker**: ventana deslizante de 10 llamadas, umbral de fallo 50%, estado abierto 10 segundos.
5. **Timeout universal**: 5000ms para todas las llamadas a plataforma-core-ohs.
6. **Fallback**: los métodos de fallback lanzan la excepción controlada correspondiente (`CatalogServiceUnavailableException`, `ZipCodeNotFoundException`, `TariffNotFoundException`) — no retornan datos parciales ni silencian el error salvo que haya datos predefinidos explícitamente configurados.
7. **Validación de formato de CP**: regex `^\d{5}$` debe aplicarse antes de invocar el servicio externo.
8. **Valores por defecto para CP**: si la respuesta del servicio omite `zonaRiesgo` o `nivelRiesgo`, se aplican DEFAULT_ZONA y DEFAULT_NIVEL definidos en `application.yml`.
9. **Solo lectura**: ninguno de estos endpoints persiste datos en MongoDB local.
10. **Modo de simulación** via `PLATAFORMA_CORE_OHS_URL`: apuntar al mock local (`http://localhost:3001`) activa el modo de desarrollo; apuntar al servicio real activa el modo producción.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `SubscriberDto` | ninguno (memoria) | existente | DTO de suscriptor desde plataforma-core-ohs |
| `AgentDto` | ninguno (memoria) | existente | DTO de agente desde plataforma-core-ohs |
| `BusinessLineDto` | ninguno (memoria) | existente | DTO de giro/línea de negocio |
| `ZipCodeDto` | ninguno (memoria) | existente | DTO de código postal con zona de riesgo |
| `RiskClassificationDto` | ninguno (memoria) | existente | DTO de clasificación de riesgo |
| `GuaranteeDto` | ninguno (memoria) | existente | DTO de garantía |
| `TariffDto` | ninguno (memoria) | existente | DTO de tarifa (incendio/CAT/equipo electrónico) |

> No se crean colecciones MongoDB. Todos los datos son transitorios (obtenidos del servicio externo y almacenados en caché Caffeine).

#### Campos — `SubscriberDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único del suscriptor |
| `nombre` | String | sí | max 255 chars | Nombre o razón social |
| `clave` | String | sí | max 50 chars | Clave interna |
| `activo` | Boolean | sí | — | Estado activo en origen |

#### Campos — `AgentDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único del agente |
| `nombre` | String | sí | max 255 chars | Nombre del agente |
| `clave` | String | sí | max 50 chars | Clave interna |
| `activo` | Boolean | sí | — | Estado activo en origen |

#### Campos — `BusinessLineDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único del giro |
| `descripcion` | String | sí | max 300 chars | Descripción del giro |
| `claveIncendio` | String | sí | max 20 chars | Clave de incendio asociada |
| `activo` | Boolean | sí | — | Estado activo en origen |

#### Campos — `ZipCodeDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `zipCode` | String | sí | regex `^\d{5}$` | Código postal de 5 dígitos |
| `municipio` | String | no | — | Municipio o delegación |
| `estado` | String | no | — | Estado/entidad federativa |
| `zonaRiesgo` | String | no | default DEFAULT_ZONA | Zona de riesgo geográfica |
| `nivelRiesgo` | String | no | default DEFAULT_NIVEL | Nivel de riesgo |

#### Campos — `RiskClassificationDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único |
| `nombre` | String | sí | — | Nombre de la clasificación (ej. "Riesgo Bajo") |

#### Campos — `GuaranteeDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único |
| `nombre` | String | sí | — | Nombre de la garantía (ej. "Incendio") |

#### Campos — `TariffDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador de la tarifa |
| `tipo` | String | sí | catálogo tipos tarifa | Tipo: `incendio`, `cat`, `fhm`, `equipo_electronico` |
| `valor` | BigDecimal | sí | decimal positivo | Valor de la tarifa |
| `parametros` | Map\<String,Object\> | no | {} | Parámetros adicionales de consulta |

#### Índices / Constraints

No aplica — sin persistencia MongoDB en esta feature.

---

### API Endpoints

> Todos los endpoints del cotizador siguen la convención `/api/v1/...` y requieren JWT Bearer.

#### GET /api/v1/catalogs/subscribers

- **Descripción**: Retorna el catálogo de suscriptores desde plataforma-core-ohs (con caché TTL 12h)
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "SUB-001", "nombre": "Empresa ABC S.A. de C.V.", "clave": "ABC", "activo": true },
    { "id": "SUB-002", "nombre": "Corporativo XYZ", "clave": "XYZ", "activo": true }
  ]
  ```
- **Response 503**:
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

#### GET /api/v1/catalogs/agents

- **Descripción**: Retorna el catálogo de agentes desde plataforma-core-ohs (con caché TTL 12h)
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "AGT-001", "nombre": "Juan Pérez García", "clave": "JPG", "activo": true }
  ]
  ```
- **Response 503**: igual al anterior

#### GET /api/v1/catalogs/business-lines

- **Descripción**: Retorna el catálogo de giros/líneas de negocio (con caché TTL 12h)
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "BL-001", "descripcion": "Comercio al por menor", "claveIncendio": "CM", "activo": true }
  ]
  ```
- **Response 503**: igual al anterior

#### GET /api/v1/catalogs/risk-classifications

- **Descripción**: Retorna el catálogo de clasificaciones de riesgo (con caché TTL 12h)
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "RISK-001", "nombre": "Riesgo Bajo" },
    { "id": "RISK-002", "nombre": "Riesgo Medio" },
    { "id": "RISK-003", "nombre": "Riesgo Alto" }
  ]
  ```
- **Response 503**: igual al anterior

#### GET /api/v1/catalogs/guarantees

- **Descripción**: Retorna el catálogo de garantías (con caché TTL 12h)
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "GAR-FIRE", "nombre": "Incendio" },
    { "id": "GAR-CAT", "nombre": "Terremoto/CAT" }
  ]
  ```
- **Response 503**: igual al anterior

#### GET /api/v1/zip-codes/{zipCode}

- **Descripción**: Valida un código postal y devuelve municipio, estado y zona de riesgo (con caché TTL 1h)
- **Auth requerida**: sí (JWT)
- **Path param**: `zipCode` — string de 5 dígitos
- **Response 200**:
  ```json
  {
    "zipCode": "06600",
    "municipio": "Cuauhtémoc",
    "estado": "Ciudad de México",
    "zonaRiesgo": "Alta",
    "nivelRiesgo": "3"
  }
  ```
- **Response 400**: formato inválido (no cumple `^\d{5}$`)
  ```json
  { "message": "Formato de código postal inválido", "code": "INVALID_ZIP_CODE_FORMAT" }
  ```
- **Response 404**: CP no existe en catálogo
  ```json
  { "message": "Código postal no encontrado", "code": "ZIP_CODE_NOT_FOUND" }
  ```
- **Response 503**: servicio externo no disponible
  ```json
  { "message": "Servicio de validación de CP no disponible", "code": "ZIP_CODE_SERVICE_UNAVAILABLE" }
  ```

#### GET /api/v1/tariffs/fire

- **Descripción**: Retorna las tarifas de incendio desde plataforma-core-ohs (con caché TTL 6h)
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "TIF-001", "tipo": "incendio", "valor": 0.0015, "parametros": {} }
  ]
  ```
- **Response 503**:
  ```json
  { "message": "Servicio de tarifas no disponible", "code": "TARIFF_SERVICE_UNAVAILABLE" }
  ```

#### GET /api/v1/tariffs/cat/{zona}

- **Descripción**: Retorna la tarifa CAT para la zona geográfica indicada (sin caché por variabilidad del parámetro)
- **Auth requerida**: sí (JWT)
- **Path param**: `zona` — nombre de la zona geográfica (ej. "Norte", "Sur", "Centro")
- **Response 200**:
  ```json
  { "id": "TIC-NORTE", "tipo": "cat", "valor": 0.0008, "parametros": { "zona": "Norte" } }
  ```
- **Response 404**: zona sin tarifa CAT definida
  ```json
  { "message": "Tarifa CAT no encontrada para la zona indicada", "code": "TARIFF_NOT_FOUND" }
  ```
- **Response 503**: igual al anterior

#### GET /api/v1/tariffs/electronic-equipment

- **Descripción**: Retorna las tarifas de equipo electrónico desde plataforma-core-ohs (con caché TTL 6h)
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "TIE-001", "tipo": "equipo_electronico", "valor": 0.0025, "parametros": {} }
  ]
  ```
- **Response 503**: igual al anterior

---

### Diseño Frontend

> Esta feature es predominantemente de backend. El frontend de Next.js 14 consume los endpoints del cotizador para poblar formularios de cotización. Los servicios y hooks se implementan en `cotizador-danos-web`.

#### Componentes nuevos

| Componente | Archivo | Props principales | Descripción |
|------------|---------|------------------|-------------|
| `CatalogSelect` | `components/shared/CatalogSelect` | `catalogType, value, onChange, disabled` | Dropdown genérico para catálogos (suscriptores, agentes, giros, clasificación, garantías) |
| `ZipCodeInput` | `components/shared/ZipCodeInput` | `value, onChange, onValidated` | Input de CP con validación en tiempo real y autocompletado de municipio/estado |

#### Hooks y State

| Hook | Archivo | Retorna | Descripción |
|------|---------|---------|-------------|
| `useCatalogs` | `hooks/useCatalogs` | `{ subscribers, agents, businessLines, riskClassifications, guarantees, loading, error }` | Carga todos los catálogos en paralelo |
| `useZipCode` | `hooks/useZipCode` | `{ zipCodeData, loading, error, validate }` | Valida un CP y retorna datos geográficos |
| `useTariffs` | `hooks/useTariffs` | `{ fireTariffs, catTariff, electronicTariffs, loading, error, fetchCatTariff }` | Consulta tarifas para el cálculo de prima |

#### Services (llamadas API)

| Función | Archivo | Endpoint |
|---------|---------|---------|
| `getSubscribers(token)` | `services/catalogsService` | `GET /api/v1/catalogs/subscribers` |
| `getAgents(token)` | `services/catalogsService` | `GET /api/v1/catalogs/agents` |
| `getBusinessLines(token)` | `services/catalogsService` | `GET /api/v1/catalogs/business-lines` |
| `getRiskClassifications(token)` | `services/catalogsService` | `GET /api/v1/catalogs/risk-classifications` |
| `getGuarantees(token)` | `services/catalogsService` | `GET /api/v1/catalogs/guarantees` |
| `validateZipCode(zipCode, token)` | `services/zipCodeService` | `GET /api/v1/zip-codes/{zipCode}` |
| `getFireTariffs(token)` | `services/tariffsService` | `GET /api/v1/tariffs/fire` |
| `getCatTariff(zona, token)` | `services/tariffsService` | `GET /api/v1/tariffs/cat/{zona}` |
| `getElectronicTariffs(token)` | `services/tariffsService` | `GET /api/v1/tariffs/electronic-equipment` |

---

### Arquitectura y Dependencias

**Módulo backend**: `plataformas-danos-back` (Java 21 / Spring Boot 4.0.5)

**Archivos backend existentes** (ya implementados en EP-003):
- `client/CatalogsClient.java` + `CatalogsClientImpl.java`
- `client/ZipCodeClient.java` + `ZipCodeClientImpl.java`
- `client/TariffsClient.java` + implementación
- `service/CatalogsService.java` + `CatalogsServiceImpl.java` (Retry + Caffeine)
- `service/ZipCodeService.java` + `ZipCodeServiceImpl.java`
- `service/TariffsService.java` + `TariffsServiceImpl.java`
- `config/CatalogsClientConfig.java` (RestTemplate con timeout)
- `exception/CatalogServiceUnavailableException.java`
- `exception/ZipCodeNotFoundException.java`
- `exception/InvalidZipCodeFormatException.java`
- `exception/TariffNotFoundException.java`

**Archivos backend a verificar/completar**:
- `controller/CatalogsController.java` — endpoints GET /api/v1/catalogs/* (ya cubierto en SPEC-003 y SPEC-005)
- `controller/ZipCodeController.java` — endpoint GET /api/v1/zip-codes/{zipCode} (cubierto en SPEC-004)
- `controller/TariffsController.java` — endpoints GET /api/v1/tariffs/* (cubierto en SPEC-006)

**Archivos frontend nuevos** (`cotizador-danos-web`):
- `services/catalogsService.ts`
- `services/zipCodeService.ts`
- `services/tariffsService.ts`
- `hooks/useCatalogs.ts`
- `hooks/useZipCode.ts`
- `hooks/useTariffs.ts`
- `components/shared/CatalogSelect.tsx`
- `components/shared/ZipCodeInput.tsx`

**Configuración externalizada** (`application.yml`):
```yaml
plataforma-core-ohs:
  url: ${PLATAFORMA_CORE_OHS_URL:http://localhost:3001}
  timeout-ms: 5000

resilience4j:
  circuitbreaker:
    instances:
      plataforma-core-ohs:
        sliding-window-size: 10
        failure-rate-threshold: 50
        wait-duration-in-open-state: 10s
  retry:
    instances:
      plataforma-core-ohs:
        max-attempts: 3
        wait-duration: 1000ms
        exponential-backoff-multiplier: 2.0
        retry-exceptions:
          - org.springframework.web.client.HttpServerErrorException
          - org.springframework.web.client.ResourceAccessException
        ignore-exceptions:
          - org.springframework.web.client.HttpClientErrorException

cache:
  ttl:
    catalogs-subscribers: 43200s
    catalogs-agents: 43200s
    catalogs-business-lines: 43200s
    catalogs-risk-classifications: 43200s
    catalogs-guarantees: 43200s
    tariffs-fire: 21600s
    tariffs-electronic-equipment: 21600s
    tariffs-cat: 3600s
    zip-codes: 3600s
```

**Servicios externos consumidos** (plataforma-core-ohs → backend cotizador):
- `GET {url}/v1/subscribers`
- `GET {url}/v1/agents`
- `GET {url}/v1/business-lines`
- `GET {url}/v1/catalogs/risk-classification`
- `GET {url}/v1/catalogs/guarantees`
- `GET {url}/v1/zip-codes/{zipCode}`
- `GET {url}/v1/tariffs/fire`
- `GET {url}/v1/tariffs/cat/{zona}`
- `GET {url}/v1/tariffs/electronic-equipment`

### Notas de Implementación

- La implementación del backend (clientes, servicios, resiliencia, caché) ya existe y está cubierta por las specs SPEC-003 (catálogos básicos), SPEC-004 (CP), SPEC-005 (clasificación/garantías), SPEC-006 (tarifas) y SPEC-008 (caché). Esta spec consolida los requerimientos de negocio del EP-001 y establece la capa frontend pendiente.
- El modo de simulación (HU-034) se activa apuntando `PLATAFORMA_CORE_OHS_URL` al mock Node.js en `plataforma-core-ohs/` — no requiere cambios de código en el backend.
- El frontend usa Axios para todas las llamadas HTTP; los errores 503 del backend deben traducirse a mensajes de usuario comprensibles (no exponer codes técnicos en la UI).
- Los hooks de catálogos deben cargar datos en paralelo (Promise.all) al montar los formularios de cotización para minimizar latencia percibida.
- `CatalogSelect` debe manejar el estado de loading (skeleton/spinner) y error (mensaje inline) de forma consistente para todos los catálogos.
- `ZipCodeInput` debe llamar a `validateZipCode` con debounce (500ms) al detectar 5 dígitos introducidos.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación

- [ ] Verificar que `CatalogsController` expone GET /api/v1/catalogs/subscribers, /agents, /business-lines, /risk-classifications, /guarantees (cubiertos en SPEC-003 y SPEC-005)
- [ ] Verificar que `ZipCodeController` expone GET /api/v1/zip-codes/{zipCode} con validación de formato (cubierto en SPEC-004)
- [ ] Verificar que `TariffsController` expone GET /api/v1/tariffs/fire, /tariffs/cat/{zona}, /tariffs/electronic-equipment (cubierto en SPEC-006)
- [ ] Verificar configuración Resilience4j en `application.yml` — Circuit Breaker + Retry con los valores de la RN-3 y RN-4
- [ ] Verificar configuración Caffeine en `application.yml` — TTLs de la RN-1
- [ ] Verificar que `GlobalExceptionHandler` mapea `CatalogServiceUnavailableException` → HTTP 503, `ZipCodeNotFoundException` → HTTP 404, `InvalidZipCodeFormatException` → HTTP 400, `TariffNotFoundException` → HTTP 404
- [ ] Verificar que fallback en `CatalogsServiceImpl` y `ZipCodeServiceImpl` lanza excepción controlada (no retorna null)
- [ ] Verificar que `TariffsServiceImpl` maneja HTTP 404 de plataforma-core-ohs como `TariffNotFoundException` sin reintentos

#### Tests Backend

- [ ] `test_get_subscribers_returns_200_with_cached_response` — segunda llamada devuelve datos de caché
- [ ] `test_get_subscribers_service_unavailable_returns_503` — fallback genera HTTP 503
- [ ] `test_get_agents_returns_200` — happy path agentes
- [ ] `test_get_business_lines_returns_200` — happy path giros
- [ ] `test_get_risk_classifications_returns_200` — happy path clasificaciones
- [ ] `test_get_guarantees_returns_200` — happy path garantías
- [ ] `test_get_zip_code_valid_returns_200_with_zone` — CP válido con zona de riesgo
- [ ] `test_get_zip_code_invalid_format_returns_400` — formato inválido → 400 sin llamada externa
- [ ] `test_get_zip_code_not_found_returns_404` — CP inexistente → 404
- [ ] `test_get_zip_code_missing_zone_applies_default` — zona faltante → DEFAULT_ZONA aplicado
- [ ] `test_get_fire_tariffs_returns_200` — happy path tarifas incendio
- [ ] `test_get_cat_tariff_valid_zone_returns_200` — tarifa CAT para zona existente
- [ ] `test_get_cat_tariff_not_found_returns_404_no_retry` — 404 en CAT no activa reintento
- [ ] `test_get_electronic_tariffs_returns_200` — happy path tarifas equipo electrónico
- [ ] `test_retry_succeeds_on_second_attempt_for_catalogs` — reintento exitoso en 2do intento
- [ ] `test_retry_exhausted_generates_503` — 3 intentos fallidos → 503 + log CRITICAL
- [ ] `test_4xx_error_not_retried_for_catalogs` — HTTP 4xx del externo → NO reintento
- [ ] `test_timeout_5s_triggers_retry` — timeout 5s activa mecanismo de reintento
- [ ] `test_mapping_drops_record_missing_id` — registro sin id descartado con log WARNING
- [ ] `test_mock_url_configurable_via_env` — PLATAFORMA_CORE_OHS_URL apunta al mock correctamente

### Frontend

#### Implementación

- [ ] Crear `services/catalogsService.ts` — funciones `getSubscribers`, `getAgents`, `getBusinessLines`, `getRiskClassifications`, `getGuarantees` con Axios + JWT header
- [ ] Crear `services/zipCodeService.ts` — función `validateZipCode(zipCode)` con manejo de 400/404/503
- [ ] Crear `services/tariffsService.ts` — funciones `getFireTariffs`, `getCatTariff(zona)`, `getElectronicTariffs`
- [ ] Crear `hooks/useCatalogs.ts` — carga en paralelo (Promise.all) con estado `{ loading, error, subscribers, agents, businessLines, riskClassifications, guarantees }`
- [ ] Crear `hooks/useZipCode.ts` — valida CP con debounce 500ms; estado `{ zipCodeData, loading, error, validate }`
- [ ] Crear `hooks/useTariffs.ts` — carga tarifas bajo demanda; estado `{ fireTariffs, catTariff, electronicTariffs, loading, error }`
- [ ] Implementar `components/shared/CatalogSelect.tsx` — dropdown genérico con skeleton/spinner en loading y mensaje de error inline
- [ ] Implementar `components/shared/ZipCodeInput.tsx` — input con debounce + autocompletado de municipio/estado + indicador de validación
- [ ] Integrar `useCatalogs` en formularios de cotización existentes (datos generales, ubicación, coberturas)
- [ ] Integrar `useZipCode` en campo de dirección del formulario de ubicación

#### Tests Frontend

- [ ] `CatalogSelect renders skeleton while loading`
- [ ] `CatalogSelect renders options from subscribers catalog`
- [ ] `CatalogSelect shows error message when service fails`
- [ ] `CatalogSelect disables field when loading or error`
- [ ] `ZipCodeInput calls validateZipCode after 500ms debounce on 5-digit input`
- [ ] `ZipCodeInput does not call service for input shorter than 5 digits`
- [ ] `ZipCodeInput autocompletes municipio and estado on success`
- [ ] `ZipCodeInput shows error on 404 response`
- [ ] `ZipCodeInput shows service error on 503 response`
- [ ] `useCatalogs loads all catalogs in parallel on mount`
- [ ] `useCatalogs sets error state when subscribers service returns 503`
- [ ] `useZipCode returns zip code data on valid response`
- [ ] `useZipCode returns error on invalid format (400)`
- [ ] `useTariffs fetches fire tariffs on mount`
- [ ] `useTariffs fetches cat tariff by zone when called`

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-29.1 al 34.3
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD (dependencia crítica con servicio externo, resiliencia, caché)
- [ ] Verificar cobertura JaCoCo ≥ 80% en `client/`, `service/`, `controller/` de integraciones
- [ ] Prueba de integración manual: mock activo → GET /api/v1/catalogs/subscribers → HTTP 200 con datos
- [ ] Prueba de resiliencia manual: apagar mock → llamar endpoint → verificar HTTP 503 + log CRITICAL
- [ ] Prueba de caché: llamar dos veces en < TTL → verificar hit de caché (segunda llamada sin log de cliente HTTP)
- [ ] Prueba de timeout: mock configurado para responder en 6s → verificar HTTP 503 y log WARN de timeout
- [ ] Prueba de CP inválido: GET /api/v1/zip-codes/1234 → HTTP 400 sin llamada externa
- [ ] Prueba de tarifa CAT not found: zona inexistente → HTTP 404 sin reintento
- [ ] Validar que `PLATAFORMA_CORE_OHS_URL` redirige correctamente (mock vs real) sin cambios de código
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
