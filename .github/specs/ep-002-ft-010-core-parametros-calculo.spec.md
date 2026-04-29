---
id: SPEC-010
status: APPROVED
feature: ep-002-ft-010-core-parametros-calculo
created: 2026-04-28
updated: 2026-04-28
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-009
  - SPEC-001
---

# Spec: EP-002 FT-010 — Configuración y Gestión de Parámetros de Cálculo

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa la ingestión, almacenamiento y disponibilidad centralizada de parámetros técnicos (tarifas de incendio, CAT, FHM, factores de equipo electrónico, catálogos de códigos postales y zonas) necesarios para que el motor de cálculo (FT-012) y el motor de validación de reglas (FT-011) operen con información técnica correcta, vigente y consistente. Los parámetros se ingestan desde el servicio externo `Plataforma-core-ohs` o mediante simulación, se almacenan en MongoDB, se cachean para optimizar latencia, y se exponen a través de una fachada centralizada que garantiza consistencia y actualización.

### Requerimiento de Negocio

El motor de cálculo del EP-002 requiere acceso centralizado y optimizado a múltiples conjuntos de datos técnicos (tarifas, factores, catálogos). Sin una capa de gestión de parámetros robusta, los cálculos serían imprecisos, desactualizados o inconsistentes, impactando directamente la tarificación y conformidad regulatoria. Esta feature resuelve la necesidad de:

1. Ingestar datos de tarifas y factores desde una fuente centralizada (`Plataforma-core-ohs`)
2. Mantener vigencias de parámetros (fechas de inicio/fin)
3. Mapear datos técnicos (códigos postales → zonas CAT y niveles de riesgo)
4. Exponer parámetros a los motores sin latencia significativa
5. Manejar fallos de integración con estrategias de reintentos y circuit breaker
6. Validar y sanejar datos ingeridos según reglas de negocio

### Historias de Usuario

#### HU-044: Ingestión de Tarifas de Incendio

```
Como:        Administrador de parámetros
Quiero:      Que el sistema ingeste o simule la consulta de tarifas de incendio desde el servicio `Plataforma-core-ohs`
Para:        Que el motor de cálculo tenga acceso a los datos más recientes y correctos de incendio

Prioridad:   Alta
Estimación:  M
Dependencias: Conexión con Plataforma-core-ohs (FT-007)
Capa:        Backend
```

#### Criterios de Aceptación — HU-044

**Happy Path**
```gherkin
CRITERIO-44.1: Obtención exitosa de tarifas de incendio
  Dado que: el servicio 'Plataforma-core-ohs' está disponible y responde con tarifas válidas
  Cuando:  se solicita la carga de tarifas de incendio por el administrador
  Entonces: el sistema obtiene las tarifas correctamente
           y las tarifas se almacenan en el repositorio de parámetros de cálculo
           y se registra un log de éxito con timestamp
```

**Error Path**
```gherkin
CRITERIO-44.2: Manejo de errores durante la obtención de tarifas
  Dado que: el servicio 'Plataforma-core-ohs' no está disponible o responde con un error (5xx)
  Cuando:  el sistema intenta cargar las tarifas de incendio
  Entonces: se ejecutan reintentos con backoff exponencial (máximo 3 intentos)
           y se registra el error detallado en los logs
           y se notifica al administrador de parámetros sobre el fallo
           y las tarifas previamente almacenadas (si las hay) permanecen sin cambios
           y se activa el Circuit Breaker después del umbral de fallos
```

**Edge Case**
```gherkin
CRITERIO-44.3: Validación de datos de tarifas inválidos
  Dado que: el servicio 'Plataforma-core-ohs' devuelve tarifas con valores inconsistentes (ej. tarifaBase negativa)
  Cuando:  el sistema intenta validar y almacenar las tarifas
  Entonces: el sistema rechaza las tarifas inválidas
           y se registra un error de validación de negocio
           y se notifica al administrador sobre los datos inválidos
           y las tarifas válidas previamente almacenadas permanecen activas
```

---

#### HU-045: Ingestión de Tarifas CAT

```
Como:        Administrador de parámetros
Quiero:      Que el sistema ingeste o simule la consulta de tarifas de catástrofe (CAT) desde el servicio `Plataforma-core-ohs`
Para:        Que el motor de cálculo aplique los factores de catástrofe correctos según la zona

Prioridad:   Alta
Estimación:  M
Dependencias: HU-044 (patrón de integración), Catálogo de Zonas CAT
Capa:        Backend
```

#### Criterios de Aceptación — HU-045

**Happy Path**
```gherkin
CRITERIO-45.1: Obtención de factores CAT desde Plataforma-core-ohs
  Dado que: el servicio `Plataforma-core-ohs` está disponible y contiene factores CAT válidos
  Cuando:  se solicita la carga de tarifas CAT
  Entonces: el sistema obtiene los factores correctamente
           y los factores son almacenados en el repositorio de parámetros de cálculo
           y se respetan las fechas de vigencia (fecha_vigencia_inicio ≤ fecha_vigencia_fin)
```

**Error Path**
```gherkin
CRITERIO-45.2: Manejo de inconsistencia de mapeo de zonas CAT
  Dado que: el servicio `Plataforma-core-ohs` devuelve una tarifa CAT para una 'zona_cat' que no existe en `catalogo_cp_zonas`
  Cuando:  el sistema intenta almacenar esta tarifa
  Entonces: el sistema registra un error de inconsistencia de zona
           y la tarifa CAT para esa zona específica no se almacena o se marca como inválida
           y se notifica al administrador sobre la discrepancia
           y se genera un log de advertencia con detalles de la zona rechazada
```

**Edge Case**
```gherkin
CRITERIO-45.3: Aplicación de fechas de vigencia de factores CAT
  Dado que: el servicio `Plataforma-core-ohs` proporciona factores CAT con fechas de vigencia (ej. '2026-01-01' - '2026-12-31')
  Cuando:  el sistema carga estos factores
  Entonces: los factores se marcan como vigentes solo dentro de su periodo especificado
           y el motor de cálculo solo aplica los factores que están vigentes en la fecha de la consulta
           y si fecha_vigencia_inicio > fecha_vigencia_fin se registra una advertencia y no se almacena
```

---

#### HU-046: Ingestión de Tarifa FHM y Factores de Equipo Electrónico

```
Como:        Administrador de parámetros
Quiero:      Que el sistema ingeste o simule la consulta de la tarifa FHM y factores de equipo electrónico desde `Plataforma-core-ohs`
Para:        Asegurar cálculos precisos en estas coberturas específicas

Prioridad:   Alta
Estimación:  S
Dependencias: Plataforma-core-ohs (FT-007)
Capa:        Backend
```

#### Criterios de Aceptación — HU-046

**Happy Path**
```gherkin
CRITERIO-46.1: Obtención de parámetros FHM y equipo electrónico
  Dado que: el servicio 'Plataforma-core-ohs' está disponible (o simulado)
  Cuando:  se solicita la carga de tarifa FHM y factores de equipo electrónico
  Entonces: el sistema los obtiene exitosamente
           y los parámetros se almacenan en el repositorio de parámetros de cálculo
           y quedan disponibles para el motor de cálculo sin retraso
```

**Error Path**
```gherkin
CRITERIO-46.2: Manejo de factores nulos o inválidos durante la ingestión
  Dado que: el servicio 'Plataforma-core-ohs' proporciona un factor nulo o inválido (ej. ≤ 0)
  Cuando:  el sistema ingesta dicho factor
  Entonces: el sistema aplica un valor por defecto configurable para ese factor
           y registra un evento de advertencia indicando el uso de valor por defecto
           o rechaza la ingestión y notifica al administrador
           y el cálculo puede proceder usando el último valor conocido válido
```

**Edge Case**
```gherkin
CRITERIO-46.3: Indisponibilidad de Plataforma-core-ohs
  Dado que: el servicio 'Plataforma-core-ohs' no está disponible
  Cuando:  el sistema intenta cargar los parámetros FHM y equipo electrónico
  Entonces: el sistema no actualiza los parámetros
           y el sistema utiliza los últimos parámetros conocidos o valores por defecto configurados
           y el Circuit Breaker se activa si se supera el umbral de fallos
           y se registra un error en logs y se notifica al administrador
```

---

#### HU-047: Ingestión y Mapeo de Catálogo de Códigos Postales y Zonas

```
Como:        Administrador de parámetros
Quiero:      Que el sistema ingeste o simule la consulta del `catalogo_cp_zonas` desde `Plataforma-core-ohs` y realice el mapeo de zonas
Para:        Que las ubicaciones de riesgo se clasifiquen correctamente según su código postal

Prioridad:   Alta
Estimación:  M
Dependencias: Plataforma-core-ohs (FT-007), HU-045 (para validación de zonas CAT)
Capa:        Backend
```

#### Criterios de Aceptación — HU-047

**Happy Path**
```gherkin
CRITERIO-47.1: Obtención de datos del catálogo CP-Zonas
  Dado que: el servicio "Plataforma-core-ohs" está disponible (o simulado)
  Cuando:  se solicita la carga del catálogo CP-Zonas
  Entonces: el sistema obtiene los datos del catálogo correctamente
           y almacena cada registro con campos: codigo_postal, zona_cat, nivel_tecnico, fecha_carga
           y se optimiza la estructura interna para búsquedas rápidas por código postal
```

**Error Path**
```gherkin
CRITERIO-47.2: Manejo de código postal no encontrado
  Dado que: el catálogo CP-Zonas ha sido cargado exitosamente
  Cuando:  se consulta un código postal que no se encuentra en el catálogo
  Entonces: el sistema devuelve un valor por defecto predefinido (ej. "ZONA-DESCONOCIDA", "NIVEL-BAJO")
           o devuelve un error explícito (HTTP 404) si está en una API de consulta
           y se registra un evento de advertencia con el código postal no encontrado
           y se notifica al administrador si excede un umbral de consultas fallidas
```

**Edge Case**
```gherkin
CRITERIO-47.3: Validación de datos inconsistentes o inválidos en el catálogo externo
  Dado que: el servicio "Plataforma-core-ohs" devuelve un catálogo con códigos postales mal formados (ej. no numéricos)
  Cuando:  se solicita la carga del catálogo CP-Zonas
  Entonces: el sistema rechaza las entradas inválidas
           y se registran errores detallados sobre los datos inconsistentes
           y se carga el resto de entradas válidas o falla la carga completamente (según política)
           y se notifica al administrador sobre los registros rechazados
```

---

#### HU-048: Disponibilidad Centralizada de Parámetros para Motores

```
Como:        Desarrollador del motor de cálculo
Quiero:      Que todos los parámetros, tarifas y catálogos ingeridos sean accesibles y estén actualizados
Para:        Que el motor de validación y cálculo opere con información consistente y correcta

Prioridad:   Alta
Estimación:  M
Dependencias: HU-044, HU-045, HU-046, HU-047 (todas las ingestiones)
Capa:        Backend
```

#### Criterios de Aceptación — HU-048

**Happy Path**
```gherkin
CRITERIO-48.1: Consulta de Parámetros Actualizados sin latencia significativa
  Dado que: los parámetros, tarifas y catálogos han sido cargados exitosamente (HU-044 a HU-047)
  Cuando:  el Motor de Validación o el Motor Central de Cálculo solicitan los datos
  Entonces: los motores reciben los datos sin latencia significativa (< 50 ms)
           y los datos recibidos son los cargados más recientemente
           y se utiliza caché en memoria para optimizar el acceso
```

**Error Path**
```gherkin
CRITERIO-48.2: Manejo de error por parámetros no disponibles
  Dado que: los parámetros, tarifas o catálogos no están disponibles en el sistema
  Cuando:  los motores intentan acceder a ellos para realizar un cálculo
  Entonces: el sistema maneja la excepción adecuadamente (lanza un error específico)
           y el cálculo es evitado o marcado como inválido
           y se genera un registro de error detallado en los logs
           y se notifica al administrador de la indisponibilidad
```

**Edge Case**
```gherkin
CRITERIO-48.3: Actualización de Parámetros en Tiempo Real
  Dado que: un parámetro ha sido actualizado en su fuente original (Plataforma-core-ohs)
  Cuando:  el sistema ha refrescado y cargado la nueva versión del parámetro
  Entonces: los motores reciben y utilizan la nueva versión del parámetro
           y la caché se invalida o se refresca automáticamente
           y no hay latencia significativa en la disponibilidad del nuevo valor
           y se registra un evento de auditoría sobre la actualización
```

### Reglas de Negocio

**BR-001: Mecanismo de Caché para Tarifas y Parámetros**
- Descripción: Para reducir la latencia y la carga en `Plataforma-core-ohs`, los parámetros obtenidos deben ser almacenados en un mecanismo de caché local (Caffeine con TTL configurable, ej. 24 horas).
- Trigger: Después de una ingestión exitosa de parámetros.
- Logic: Los parámetros se almacenan en caché. Las consultas deben priorizar caché antes de acceder a BD. Si caché está vacía o expirada, se realiza una consulta a BD o refresco a fuente.
- Implementation: Servicio de parámetros de cálculo con anotación `@Cacheable(value = "parameters", key = "#paramType")`.
- Exception: Si caché y BD están vacías, se retorna error de parámetro no disponible.

**BR-002: Notificación y Registro de Errores Críticos**
- Descripción: Cualquier fallo durante la conexión o recuperación de datos debe ser registrado con nivel CRITICAL y notificado al administrador.
- Trigger: Fallo en llamada a `Plataforma-core-ohs` o en procesamiento de respuesta.
- Logic: 1) Registrar error con CRITICAL, timestamp, mensaje, stack trace y detalles de respuesta. 2) Enviar notificación (correo o alerta en UI) al rol 'Administrador de parámetros'.
- Implementation: Adaptador de integración `Plataforma-core-ohs`, servicio de notificaciones.
- Exception: No aplica.

**BR-003: Validación de Vigencias de Parámetros**
- Descripción: Tarifas y factores solo son válidos dentro de su rango de fechas de vigencia. Un parámetro con `fecha_vigencia_fin < fecha_vigencia_inicio` es rechazado.
- Trigger: Al cargar y al consultar parámetros.
- Logic: Si `fecha_vigencia_inicio > fecha_vigencia_fin`, rechazar y registrar advertencia. Al consultar, comparar fecha actual con rango de vigencia.
- Implementation: Validador en repositorio o servicio de parámetros.
- Exception: Parámetros con vigencia inválida no se almacenan.

**BR-004: Mapeo Consistente de Zonas CAT a Catálogo Postal**
- Descripción: Los códigos de zona CAT recibidos deben corresponderse con zonas definidas en `catalogo_cp_zonas`.
- Trigger: Al recibir datos de tarifas CAT o al validar códigos postales.
- Logic: Validar que `zona_cat` exista en catálogo. Si no, descartar tarifa CAT y registrar error.
- Implementation: Validador en servicio de ingestión CAT.
- Exception: Tarifas CAT con zonas no mapeadas se rechazan y se notifica al administrador.

**BR-005: Valores por Defecto para Parámetros Inválidos o Nulos**
- Descripción: Si un parámetro se recibe nulo o inválido (ej. factor ≤ 0), aplicar un valor por defecto configurable.
- Trigger: Durante ingestión de factores FHM o equipo electrónico.
- Logic: Si `factor IS NULL OR factor <= 0` THEN USE `default_value_configurable` ELSE USE `received_factor`.
- Implementation: Lógica en adaptador de integración y repositorio de parámetros.
- Exception: Si no hay valor por defecto, registrar error y opcionalmente rechazar la ingestión.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `TarifaIncendio` | colección `tarifas_incendio` | nueva | Tarifas de incendio con zona, tipo inmueble, base, vigencia |
| `TarifaCAT` | colección `tarifas_cat` | nueva | Factores de catástrofe por zona con fechas de vigencia |
| `TarifaFHM` | colección `tarifas_fhm` | nueva | Tarifa FHM única y factor de equipo electrónico |
| `CatalogoCPZonas` | colección `catalogo_cp_zonas` | nueva | Mapeo código postal → zona CAT → nivel técnico |
| `ParametroCalculoFachada` | cache en memoria (Caffeine) | nueva | Caché de acceso centralizado a parámetros |

#### Campos del modelo — TarifaIncendio

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String (UUID) | sí | UUID válido | Identificador único |
| `zonaGeografica` | String | sí | no vacío, maxLength: 50 | Zona de aplicación |
| `tipoInmueble` | String | sí | no vacío, maxLength: 100 | Tipo de inmueble (ej. Residencial Urbano) |
| `tarifaBase` | BigDecimal | sí | positivo, max_precision: 4 decimales | Tarifa base |
| `fechaVigenciaInicio` | LocalDate | sí | formato YYYY-MM-DD | Fecha de inicio de vigencia |
| `fechaVigenciaFin` | LocalDate | no | formato YYYY-MM-DD, posterior a inicio | Fecha de fin de vigencia |
| `createdAt` | Instant | sí | auto-generado | Timestamp de creación (UTC) |
| `updatedAt` | Instant | sí | auto-generado | Timestamp de actualización (UTC) |
| `origen` | String | sí | "Plataforma-core-ohs" o "Simulación" | Fuente de los datos |

#### Campos del modelo — TarifaCAT

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String (UUID) | sí | UUID válido | Identificador único |
| `zonaCAT` | String | sí | debe existir en `catalogo_cp_zonas` | Zona CAT (ej. ZONA_NORTE_MEX) |
| `factorCAT` | BigDecimal | sí | positivo | Factor multiplicador para catástrofe |
| `fechaVigenciaInicio` | LocalDate | sí | formato YYYY-MM-DD | Fecha de inicio |
| `fechaVigenciaFin` | LocalDate | sí | formato YYYY-MM-DD, ≥ inicio | Fecha de fin |
| `createdAt` | Instant | sí | auto-generado | Timestamp de creación |
| `updatedAt` | Instant | sí | auto-generado | Timestamp de actualización |
| `origen` | String | sí | "Plataforma-core-ohs" o "Simulación" | Fuente de los datos |

#### Campos del modelo — TarifaFHM

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String (UUID) | sí | UUID válido | Identificador único |
| `tarifaFHM` | BigDecimal | sí | positivo | Tarifa FHM |
| `factorEquipoElectronico` | BigDecimal | sí | positivo | Factor de equipo electrónico |
| `fechaVigenciaInicio` | LocalDate | sí | formato YYYY-MM-DD | Fecha de inicio |
| `fechaVigenciaFin` | LocalDate | no | formato YYYY-MM-DD | Fecha de fin (opcional, se asume indefinida si no aplica) |
| `createdAt` | Instant | sí | auto-generado | Timestamp de creación |
| `updatedAt` | Instant | sí | auto-generado | Timestamp de actualización |
| `origen` | String | sí | "Plataforma-core-ohs" o "Simulación" | Fuente de los datos |

#### Campos del modelo — CatalogoCPZonas

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | auto-generado | Identificador único |
| `codigoPostal` | String | sí | formato numérico (ej. ^\d{5}$) | Código postal |
| `zonaCAT` | String | sí | debe existir en catálogo de zonas | Zona CAT asociada |
| `nivelTecnico` | String | sí | enum: ALTO, MEDIO, BAJO | Nivel técnico de riesgo |
| `fechaCarga` | Instant | sí | auto-generado (UTC) | Timestamp de carga |
| `origen` | String | sí | "Plataforma-core-ohs" o "Simulación" | Fuente de los datos |

#### Índices / Constraints

| Colección | Campo(s) | Tipo | Justificación |
|-----------|----------|------|-------------|
| `tarifas_incendio` | `zonaGeografica`, `tipoInmueble`, `fechaVigenciaInicio` | Compound Index | Búsqueda frecuente por zona, tipo y vigencia |
| `tarifas_cat` | `zonaCAT`, `fechaVigenciaInicio` | Compound Index | Búsqueda por zona y vigencia |
| `catalogo_cp_zonas` | `codigoPostal` | Unique Index | Cada CP es único en el catálogo |
| `catalogo_cp_zonas` | `zonaCAT` | Index | Búsqueda y validación de zonas |

### API Endpoints

#### POST /api/v1/parameters/tarifas-incendio/load

- **Descripción**: Inicia la ingestión de tarifas de incendio desde Plataforma-core-ohs o simulación (HU-044)
- **Auth requerida**: sí (JWT + rol "ADMIN_PARAMETROS")
- **Request Body**:
  ```json
  {
    "origenForzado": "SIMULACION"  // opcional, si no se especifica usa config
  }
  ```
- **Response 202**:
  ```json
  {
    "requestId": "uuid",
    "status": "INICIADA",
    "mensaje": "Carga de tarifas de incendio iniciada",
    "timestamp": "2026-04-28T10:30:00Z"
  }
  ```
- **Response 400**: parámetros inválidos
- **Response 401**: token ausente o expirado
- **Response 409**: ingestión ya en progreso
- **Response 503**: Plataforma-core-ohs no disponible y circuit breaker abierto

#### POST /api/v1/parameters/tarifas-cat/load

- **Descripción**: Inicia la ingestión de tarifas CAT desde Plataforma-core-ohs (HU-045)
- **Auth requerida**: sí (JWT + rol "ADMIN_PARAMETROS")
- **Request Body**:
  ```json
  {
    "origenForzado": "SIMULACION"  // opcional
  }
  ```
- **Response 202**: ingestión iniciada
- **Response 400**: parámetros inválidos
- **Response 401**: no autenticado
- **Response 409**: ingestión en progreso
- **Response 503**: servicio no disponible

#### POST /api/v1/parameters/tarifas-fhm/load

- **Descripción**: Inicia la ingestión de tarifas FHM y equipo electrónico (HU-046)
- **Auth requerida**: sí (JWT + rol "ADMIN_PARAMETROS")
- **Request Body**:
  ```json
  {
    "origenForzado": "SIMULACION"
  }
  ```
- **Response 202**: ingestión iniciada
- **Response 400**: parámetros inválidos
- **Response 401**: no autenticado
- **Response 409**: ingestión en progreso
- **Response 503**: servicio no disponible

#### POST /api/v1/parameters/catalogo-cp-zonas/load

- **Descripción**: Inicia la ingestión y mapeo del catálogo CP-Zonas (HU-047)
- **Auth requerida**: sí (JWT + rol "ADMIN_PARAMETROS")
- **Request Body**:
  ```json
  {
    "origenForzado": "SIMULACION"
  }
  ```
- **Response 202**: ingestión iniciada
- **Response 400**: parámetros inválidos
- **Response 401**: no autenticado
- **Response 409**: ingestión en progreso
- **Response 503**: servicio no disponible

#### GET /api/v1/parameters/tarifas-incendio

- **Descripción**: Obtiene todas las tarifas de incendio vigentes (caché-backed)
- **Auth requerida**: sí (JWT, cualquier rol autenticado)
- **Response 200**:
  ```json
  [
    {
      "id": "uuid",
      "zonaGeografica": "Zona_A",
      "tipoInmueble": "Residencial Urbano",
      "tarifaBase": 0.0050,
      "fechaVigenciaInicio": "2026-04-21",
      "fechaVigenciaFin": "2027-04-21",
      "origen": "Plataforma-core-ohs"
    }
  ]
  ```
- **Response 401**: no autenticado
- **Response 503**: parámetros no disponibles

#### GET /api/v1/parameters/tarifas-cat

- **Descripción**: Obtiene todas las tarifas CAT vigentes por zona
- **Auth requerida**: sí
- **Query params**: `zona` (opcional, filtra por zona)
- **Response 200**: array de tarifas CAT
- **Response 401**: no autenticado
- **Response 503**: parámetros no disponibles

#### GET /api/v1/parameters/tarifas-fhm

- **Descripción**: Obtiene la tarifa FHM y factor de equipo electrónico vigente (HU-046)
- **Auth requerida**: sí
- **Response 200**:
  ```json
  {
    "id": "uuid",
    "tarifaFHM": 0.05,
    "factorEquipoElectronico": 1.25,
    "fechaVigenciaInicio": "2026-01-01",
    "fechaVigenciaFin": "2026-12-31",
    "origen": "Plataforma-core-ohs"
  }
  ```
- **Response 401**: no autenticado
- **Response 503**: parámetros no disponibles

#### GET /api/v1/parameters/catalogo-cp-zonas/{codigoPostal}

- **Descripción**: Consulta la zona CAT y nivel técnico de un código postal (HU-047)
- **Auth requerida**: sí
- **Response 200**:
  ```json
  {
    "codigoPostal": "28001",
    "zonaCAT": "ZONA-A",
    "nivelTecnico": "ALTO",
    "fechaCarga": "2026-04-21T10:30:00Z"
  }
  ```
- **Response 401**: no autenticado
- **Response 404**: código postal no mapeado (o retorna valor por defecto según BR-002)
- **Response 503**: catálogo no disponible

#### GET /api/v1/parameters/status

- **Descripción**: Obtiene el estado de disponibilidad de todos los parámetros y última fecha de actualización (HU-048)
- **Auth requerida**: sí
- **Response 200**:
  ```json
  {
    "tarifasIncendio": {
      "disponible": true,
      "ultimaActualizacion": "2026-04-28T10:30:00Z",
      "cantidad": 125
    },
    "tarifasCAT": {
      "disponible": true,
      "ultimaActualizacion": "2026-04-28T10:25:00Z",
      "cantidad": 32
    },
    "tarifasFHM": {
      "disponible": true,
      "ultimaActualizacion": "2026-04-28T10:20:00Z"
    },
    "catalogoCPZonas": {
      "disponible": true,
      "ultimaActualizacion": "2026-04-28T09:00:00Z",
      "cantidad": 52000
    },
    "circuitBreakerStatus": "CLOSED"
  }
  ```
- **Response 401**: no autenticado

### Diseño Frontend

#### Páginas nuevas
No hay UI de frontend en esta feature. Es un backend puro (API administrativa).

#### Notas de Integración Frontend
- Si existe un panel de administración (FT-XXX), se puede agregar una sección "Parámetros → Cargar Tarifas" con botones que llamen a los endpoints POST anteriores.
- El endpoint `GET /api/v1/parameters/status` puede usarse para mostrar un dashboard de estado de parámetros.

### Arquitectura y Dependencias

#### Paquetes nuevos requeridos

```
plataformas-danos-back/src/main/java/com/sofka/
├── parameters/
│   ├── adapter/                      # Adaptadores hexagonales
│   │   └── out/
│   │       ├── persistence/
│   │       │   ├── MongoTarifaIncendioRepository.java
│   │       │   ├── MongoTarifaCATRepository.java
│   │       │   ├── MongoTarifaFHMRepository.java
│   │       │   └── MongoCatalogoCPZonasRepository.java
│   │       └── external/
│   │           └── PlataformaCoreOhsAdapter.java
│   ├── domain/
│   │   ├── model/
│   │   │   ├── TarifaIncendio.java
│   │   │   ├── TarifaCAT.java
│   │   │   ├── TarifaFHM.java
│   │   │   ├── CatalogoCPZonas.java
│   │   │   ├── ParametroCalculoFachada.java
│   │   │   └── events/
│   │   │       └── ParametrosActualizadosEvent.java
│   │   ├── ports/
│   │   │   ├── in/
│   │   │   │   ├── IngestTarifasIncendioUseCase.java
│   │   │   │   ├── IngestTarifasCATUseCase.java
│   │   │   │   ├── IngestTarifasFHMUseCase.java
│   │   │   │   ├── IngestCatalogoCPZonasUseCase.java
│   │   │   │   └── ConsultarParametrosUseCase.java
│   │   │   └── out/
│   │   │       ├── TarifaIncendioRepository.java
│   │   │       ├── TarifaCATRepository.java
│   │   │       ├── TarifaFHMRepository.java
│   │   │       ├── CatalogoCPZonasRepository.java
│   │   │       ├── PlataformaCoreOhsPort.java
│   │   │       └── NotificacionAdministradorPort.java
│   │   └── service/
│   │       ├── ParametroCalculoService.java (interfaz)
│   │       ├── ParametroCalculoServiceImpl.java
│   │       ├── IngestorTarifasService.java
│   │       └── CacheParametrosService.java
│   ├── controller/
│   │   └── ParametroCalculoController.java
│   ├── dto/
│   │   ├── request/
│   │   │   ├── CargarTarifasRequest.java
│   │   │   └── CargarCatalogoRequest.java
│   │   └── response/
│   │       ├── TarifaIncendioResponse.java
│   │       ├── TarifaCATResponse.java
│   │       ├── TarifaFHMResponse.java
│   │       ├── CatalogoCPZonasResponse.java
│   │       ├── IngestStatusResponse.java
│   │       └── ParametrosStatusResponse.java
│   └── config/
│       ├── ParametrosCacheConfig.java
│       └── ResilienceConfig.java
```

#### Dependencias técnicas

| Dependencia | Versión | Propósito |
|-------------|---------|----------|
| Spring Data MongoDB | 4.0.5 | Persistencia en MongoDB |
| Caffeine | 3.2.0 | Cache en memoria con TTL |
| Resilience4j | 2.3.0 | Circuit Breaker, Retry |
| JJWT | 0.12.6 | Autenticación JWT |
| Lombok | 1.18.30 | Reducción de boilerplate |
| Spring AOP | 6.1.x | Auditoría y observabilidad |

#### Integraciones externas

| Sistema | Contrato | Timeout | Reintentos |
|---------|----------|---------|-----------|
| `Plataforma-core-ohs` | REST HTTP | 10 seg (configurable) | 3 + backoff exponencial |
| Sistema de notificaciones (futuro) | Interfaz de puerto | N/A | N/A |

#### Punto de entrada de la app

Registrar el módulo en la configuración de Spring Boot:
- Crear clase `ParametrosCoreConfig.class` (o integrar en `CotizadorCoreConfig.java` existente)
- Registrar beans: `ParametroCalculoService`, `ParametrosCacheConfig`, `ResilienceConfig`
- Escanear `@ComponentScan(basePackages = {"com.sofka.parameters"})`

### Notas de Implementación

1. **Caché con Caffeine**: La fachada de parámetros debe usar `@Cacheable` con TTL de 24 horas. Invalidar manualmente cuando se completa una ingestión exitosa con `@CacheEvict`.

2. **Circuit Breaker en Resilience4j**: Configurar con umbral de fallos (ej. 5 fallos consecutivos) y wait duration (ej. 30 segundos) antes de permitir reintentos.

3. **Validación de Vigencias**: Implementar un validador reutilizable que compruebe `fecha_vigencia_inicio ≤ fecha_vigencia_fin` antes de persistir.

4. **Mapeo de Zonas**: Antes de guardar tarifas CAT, validar que la zona existe en el catálogo de CP-Zonas. Si no existe, registrar error y rechazar.

5. **Auditoría**: Registrar cada operación de carga/actualización de parámetros con level=CRITICAL, usuario, timestamp y resultado (éxito/fallo).

6. **Modo Simulación**: Implementar datos simulados (_fixtures_) para cada tipo de tarifa, cargables desde archivos JSON o hardcodeados. Permitir al usuario elegir modo via parámetro `origenForzado` en request.

7. **Testcontainers para integración**: Los tests de integración deben usar MongoDB real via Testcontainers, no H2 o mocks de BD.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación — Modelos y Persistencia

- [ ] Crear entidad `TarifaIncendio.java` en `domain/model/` con anotación `@Document(collection = "tarifas_incendio")`
- [ ] Crear entidad `TarifaCAT.java` con `@Document(collection = "tarifas_cat")`
- [ ] Crear entidad `TarifaFHM.java` con `@Document(collection = "tarifas_fhm")`
- [ ] Crear entidad `CatalogoCPZonas.java` con `@Document(collection = "catalogo_cp_zonas")`
- [ ] Crear `TarifaIncendioRepository` extends `MongoRepository<TarifaIncendio, String>` con métodos: `findByZonaGeograficaAndFechaVigencia()`, `findAllVigentes(LocalDate)`
- [ ] Crear `TarifaCATRepository` extends `MongoRepository<TarifaCAT, String>` con métodos: `findByZonaCATAndFechaVigencia()`, `findByZonaCAT(String)`
- [ ] Crear `TarifaFHMRepository` extends `MongoRepository<TarifaFHM, String>` con método: `findVigentFHM(LocalDate)`
- [ ] Crear `CatalogoCPZonasRepository` extends `MongoRepository<CatalogoCPZonas, String>` con métodos: `findByCodigoPostal(String)`, `findByZonaCAT(String)`
- [ ] Crear índices en MongoDB: compound index `(zonaGeografica, tipoInmueble, fechaVigenciaInicio)` en `tarifas_incendio`, similar para CAT
- [ ] Crear índice único en `codigoPostal` en colección `catalogo_cp_zonas`

#### Implementación — Puertos (Interfaces)

- [ ] Crear `TarifaIncendioRepository.java` (interfaz) en `domain/ports/out/`
- [ ] Crear `TarifaCATRepository.java` (interfaz)
- [ ] Crear `TarifaFHMRepository.java` (interfaz)
- [ ] Crear `CatalogoCPZonasRepository.java` (interfaz)
- [ ] Crear `PlataformaCoreOhsPort.java` — interfaz para comunicación con servicio externo (métodos: `obtenerTarifasIncendio()`, `obtenerTarifasCAT()`, `obtenerTarifasFHM()`, `obtenerCatalogoCPZonas()`)
- [ ] Crear `NotificacionAdministradorPort.java` — interfaz para notificaciones (método: `notificarError(String titulo, String detalles)`)
- [ ] Crear use cases: `IngestTarifasIncendioUseCase.java`, `IngestTarifasCATUseCase.java`, `IngestTarifasFHMUseCase.java`, `IngestCatalogoCPZonasUseCase.java`, `ConsultarParametrosUseCase.java`

#### Implementación — Servicios y Lógica de Negocio

- [ ] Crear `ParametroCalculoService.java` (interfaz) con métodos: `obtenerTarifasIncendioVigentes()`, `obtenerTarifasCATVigentes()`, `obtenerTarifaFHMVigente()`, `obtenerZonaYNivelPorCP(String)`, `refrescarParametros(String tipoParametro)`
- [ ] Crear `ParametroCalculoServiceImpl.java` con implementación de servicios, incluyendo lógica de caché (Caffeine, `@Cacheable`)
- [ ] Crear `IngestorTarifasService.java` — orquesta la ingestión: llamadas a Plataforma-core-ohs con Resilience4j (retry + circuit breaker), validación, almacenamiento, notificaciones
- [ ] Crear `CacheParametrosService.java` — gestiona invalidación y refresco de caché tras ingestión exitosa (usar `@CacheEvict`)
- [ ] Crear `ValidadorVigenciasParametros.java` — valida que `fecha_inicio ≤ fecha_fin`
- [ ] Crear `ValidadorMapeoZonas.java` — valida que zona CAT existe en catálogo
- [ ] Implementar `PlataformaCoreOhsAdapter.java` — adaptador hexagonal que consume API de Plataforma-core-ohs con Resilience4j
- [ ] Implementar manejo de modo simulación: si `origen="SIMULACION"`, cargar datos de fixtures JSON o constantes Java

#### Implementación — Controladores REST

- [ ] Crear `ParametroCalculoController.java` con endpoints:
  - `POST /api/v1/parameters/tarifas-incendio/load` — HU-044
  - `POST /api/v1/parameters/tarifas-cat/load` — HU-045
  - `POST /api/v1/parameters/tarifas-fhm/load` — HU-046
  - `POST /api/v1/parameters/catalogo-cp-zonas/load` — HU-047
  - `GET /api/v1/parameters/tarifas-incendio`
  - `GET /api/v1/parameters/tarifas-cat` (con query param opcional `zona`)
  - `GET /api/v1/parameters/tarifas-fhm`
  - `GET /api/v1/parameters/catalogo-cp-zonas/{codigoPostal}`
  - `GET /api/v1/parameters/status` — HU-048
- [ ] Implementar autenticación JWT en controlador (@PreAuthorize para endpoints administrativos)
- [ ] Implementar manejo de errores y respuestas HTTP correctas (202, 400, 401, 409, 503)
- [ ] Registrar logging detallado de todas las operaciones

#### Implementación — Configuración y Resiliencia

- [ ] Crear `ParametrosCacheConfig.java` — configurar Caffeine cache: `CacheManager`, `CacheBuilder`, TTL = 24 horas
- [ ] Crear `ResilienceConfig.java` — configurar Resilience4j: `@CircuitBreaker`, `@Retry` (3 intentos, backoff exponencial 100ms, multiplicador 2)
- [ ] Configurar propiedades en `application.yml`: `plataforma-core-ohs.url`, `plataforma-core-ohs.timeout`, `resiliencia.circuit-breaker.failure-threshold`, `cache.ttl`
- [ ] Crear `NotificacionesAdapter.java` (implementación stub de `NotificacionAdministradorPort`) — por ahora, registrar en logs; futuro: integración con email/alertas

#### Implementación — DTOs

- [ ] Crear request DTOs: `CargarTarifasRequest.java` (con campo opcional `origenForzado`)
- [ ] Crear response DTOs: `TarifaIncendioResponse.java`, `TarifaCATResponse.java`, `TarifaFHMResponse.java`, `CatalogoCPZonasResponse.java`
- [ ] Crear `IngestStatusResponse.java` (estado de ingestión en progreso)
- [ ] Crear `ParametrosStatusResponse.java` (estado global de disponibilidad de parámetros)
- [ ] Aplicar validaciones Bean Validation (`@NotNull`, `@Min`, etc.) en DTOs

### Backend — Tests

#### Tests Unitarios

- [ ] `test_tarifaIncendioService_obtenerTarifasVigentes_success` — happy path HU-044
- [ ] `test_tarifaIncendioService_obtenerTarifasVigentes_retornaVacio_sineNingunaTarifaVigente` — edge case
- [ ] `test_ingestorTarifasService_ingestTarifasIncendio_exitoso_almacenaYNotifica` — HU-044 happy path con caché
- [ ] `test_ingestorTarifasService_ingestTarifasIncendio_plataformaNoDisponible_reintentaYNotifica` — HU-044 error path
- [ ] `test_ingestorTarifasService_ingestTarifasCAT_validaZonas_rechazaTarifasConZonasInvalidas` — HU-045 edge case BR-004
- [ ] `test_validadorVigencias_valida_fechasInconsistentes_rechaza` — BR-003
- [ ] `test_tarifaFHMService_obtenerVigente_retornaValorPorDefecto_siFHMNuloOInvalido` — HU-046 error path BR-005
- [ ] `test_catalogoService_consultarCP_devuelveZonaYNivel_siCPExiste` — HU-047 happy path
- [ ] `test_catalogoService_consultarCP_devuelveDefault_siCPNoExiste` — HU-047 edge case BR-002
- [ ] `test_parametrosFachada_consultarParametros_devuelveDatos_sinLatenciaSignificativa` — HU-048 happy path
- [ ] `test_parametrosFachada_consultarParametros_lanzaError_siParametrosNoDisponibles` — HU-048 error path
- [ ] `test_parametrosCacheService_refresca_parametrosActualizados_despuesDeIngestExitosa` — HU-048 edge case

#### Tests de Integración (Testcontainers + MongoDB real)

- [ ] `test_tarifaIncendioRepository_guardaYRecupera_documentoValido` — persistence
- [ ] `test_tarifaCATRepository_filtraPorZonaYVigencia_correctamente`
- [ ] `test_catalogoCPZonasRepository_buscaPorCPconIndiceUnico_eficiente`
- [ ] `test_ingestorTarifasService_cargaDesdeAdaptador_almacenaEnMongoDB_exitoso` — integración E2E de ingestión
- [ ] `test_ingestorTarifasService_circuitBreakerAbre_despuesDe5FallosConsecutivos` — Resilience4j
- [ ] `test_ingestorTarifasService_reintentos_conBackoffExponencial_entre100y400ms`
- [ ] `test_parametrosCacheService_cachInvalida_alCompletarIngestExitosa`

#### Tests de Controlador (MockMvc)

- [ ] `test_parametroController_postCargarTarifasIncendio_returns202_withValidRequest` — HU-044
- [ ] `test_parametroController_postCargarTarifasIncendio_returns401_noAuth` — autenticación
- [ ] `test_parametroController_postCargarTarifasIncendio_returns409_ingestEnProgreso` — concurrencia
- [ ] `test_parametroController_getTarifasIncendio_returns200_withCachedData` — caché
- [ ] `test_parametroController_getTarifasIncendio_returns503_parametrosNoDisponibles`
- [ ] `test_parametroController_getConsultarCP_returns200_conZonaYNivel` — HU-047
- [ ] `test_parametroController_getConsultarCP_returns404_oCPNoEncontrado_segunPolitica`
- [ ] `test_parametroController_getStatus_returns200_conEstadoGlobalParametros` — HU-048

### Frontend

#### Implementación
- [ ] No aplica para esta feature (backend puro)
- [ ] Opcional: Crear componente `ParameterLoaderPanel.tsx` en futuro panel administrativo que llame a endpoints POST

### QA

#### Ejecución de Gherkin

- [ ] Ejecutar skill `/gherkin-case-generator` sobre HU-044 a HU-048
- [ ] Generar suite de pruebas E2E con Postman/REST Assured para cada escenario Gherkin
- [ ] Validar happy paths, error paths, edge cases

#### Validación de Criterios de Aceptación

- [ ] CRITERIO-44.1: Obtención exitosa y almacenamiento de tarifas de incendio ✓
- [ ] CRITERIO-44.2: Manejo de errores, reintentos, notificaciones ✓
- [ ] CRITERIO-44.3: Rechazo de datos inválidos ✓
- [ ] CRITERIO-45.1: Obtención de CAT, validación de vigencia ✓
- [ ] CRITERIO-45.2: Validación de mapeo de zonas ✓
- [ ] CRITERIO-45.3: Aplicación de vigencias ✓
- [ ] CRITERIO-46.1: Obtención de FHM y equipo electrónico ✓
- [ ] CRITERIO-46.2: Manejo de factores nulos/inválidos ✓
- [ ] CRITERIO-46.3: Fallback a valores previos o por defecto ✓
- [ ] CRITERIO-47.1: Obtención y mapeo de catálogo ✓
- [ ] CRITERIO-47.2: Manejo de CP no encontrado ✓
- [ ] CRITERIO-47.3: Rechazo de datos inconsistentes ✓
- [ ] CRITERIO-48.1: Consulta sin latencia significativa, caché ✓
- [ ] CRITERIO-48.2: Manejo de error parámetros no disponibles ✓
- [ ] CRITERIO-48.3: Actualización en tiempo real, invalidación de caché ✓

#### Validación de Reglas de Negocio

- [ ] BR-001: Caché con TTL 24 horas, refresco tras ingestión ✓
- [ ] BR-002: Logging CRITICAL, notificaciones a administrador ✓
- [ ] BR-003: Vigencias validadas, rechazadas si inválidas ✓
- [ ] BR-004: Mapeo de zonas validado contra catálogo ✓
- [ ] BR-005: Valores por defecto aplicados para parámetros inválidos ✓

#### Cobertura de Tests

- [ ] Cobertura global ≥ 80% (métrica: JaCoCo)
- [ ] Cobertura de servicios de parámetros ≥ 90% (módulo crítico)
- [ ] Todos los paths de error cubiertos (error path, edge case)
- [ ] Tests de integración con Testcontainers (MongoDB real)

#### Actualización de Documentación

- [ ] Actualizar Swagger/OpenAPI con nuevos endpoints (`/api/v1/parameters/**`)
- [ ] Documentar propiedades de configuración en `application.yml`
- [ ] Crear README de la feature con instrucciones de uso de endpoints administrativos
- [ ] Actualizar diagramas C4 (Nivel 2: Contenedores, Nivel 3: Componentes) para incluir el módulo de parámetros

#### Cierre de la Feature

- [ ] Revisar y aprobar todos los items de checklist
- [ ] Ejecutar suite completa de tests (unitarios + integración + controlador)
- [ ] Validar cobertura mínima ≥ 80%
- [ ] Recopilar métricas de performance (latencia caché, circuit breaker, reintentos)
- [ ] Actualizar estado de la spec: `status: IMPLEMENTED` en `.github/specs/`
