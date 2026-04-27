---
id: SPEC-005
status: APPROVED
feature: ep-003-ft-017-core-clasificacion-riesgo
created: 2026-04-27
updated: 2026-04-27
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-001
  - SPEC-003
---

# Spec: FT-017 — Integración de Catálogos de Clasificación de Riesgo y Garantías

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature extiende en `plataformas-danos-back` (Java 21 / Spring Boot) la capa de catálogos existente (SPEC-003) para integrar dos nuevos catálogos provenientes de `plataforma-core-ohs`: clasificación de riesgo y garantías. Sigue el mismo patrón de cliente HTTP, filtrado de registros inválidos, resiliencia Resilience4j y exposición REST ya establecido en FT-015, con una única diferencia: la adición de métodos a los artefactos existentes en lugar de crear nuevos.

### Requerimiento de Negocio

El cotizador de seguros de daños necesita catálogos de clasificación de riesgo y garantías para que el usuario pueda configurar coberturas con opciones actualizadas y consistentes con las políticas de suscripción. Esta información proviene exclusivamente de `plataforma-core-ohs`. La disponibilidad de ambos catálogos es prerequisito para la configuración de coberturas (HU-011) y el cálculo de primas (HU-015).

### Historias de Usuario

#### HU-01: Recuperar catálogo de clasificación de riesgo (HU-077)

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Consultar el catálogo de clasificación de riesgo desde plataforma-core-ohs
Para:        Ofrecer opciones actualizadas en la definición de coberturas de la cotización

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: SPEC-001 (mock server operativo), SPEC-003 (patrón cliente HTTP establecido)
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Consulta exitosa del catálogo de clasificación de riesgo
  Dado que:  plataforma-core-ohs está activo y tiene clasificaciones de riesgo en el catálogo
  Cuando:    el cotizador llama a GET /api/v1/catalogs/risk-classifications
  Entonces:  recibe HTTP 200 con una lista de objetos
             [{ "id": "RC-001", "nombre": "Riesgo Bajo", "descripcion": "..." }, ...]
```

**Edge Case**
```gherkin
CRITERIO-1.2: Catálogo de clasificación de riesgo vacío
  Dado que:  plataforma-core-ohs devuelve lista vacía
  Cuando:    el cotizador llama a GET /api/v1/catalogs/risk-classifications
  Entonces:  recibe HTTP 200 con lista vacía []
```

**Error Path**
```gherkin
CRITERIO-1.3: Servicio externo no disponible tras reintentos
  Dado que:  plataforma-core-ohs no responde después de 3 intentos
  Cuando:    el cotizador llama a GET /api/v1/catalogs/risk-classifications
  Entonces:  recibe HTTP 503 con { "message": "Servicio de catálogos no disponible",
             "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-02: Recuperar catálogo de garantías (HU-078)

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Consultar el catálogo de garantías desde plataforma-core-ohs
Para:        Ofrecer opciones de garantías completas y actualizadas en la configuración de coberturas

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: SPEC-001, SPEC-003, HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-02

**Happy Path**
```gherkin
CRITERIO-2.1: Consulta exitosa del catálogo de garantías
  Dado que:  plataforma-core-ohs está activo y tiene garantías en el catálogo
  Cuando:    el cotizador llama a GET /api/v1/catalogs/guarantees
  Entonces:  recibe HTTP 200 con una lista de objetos
             [{ "id": "GUA-001", "nombre": "Robo con Violencia",
                "claveIncendio": "RV", "tarifable": true }, ...]
```

**Edge Case**
```gherkin
CRITERIO-2.2: Catálogo de garantías vacío
  Dado que:  plataforma-core-ohs devuelve lista vacía
  Cuando:    el cotizador llama a GET /api/v1/catalogs/guarantees
  Entonces:  recibe HTTP 200 con lista vacía []
```

**Error Path**
```gherkin
CRITERIO-2.3: Servicio externo no disponible tras reintentos
  Dado que:  plataforma-core-ohs no responde después de 3 intentos
  Cuando:    el cotizador llama a GET /api/v1/catalogs/guarantees
  Entonces:  recibe HTTP 503 con { "message": "Servicio de catálogos no disponible",
             "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-03: Mapear y filtrar registros inválidos (HU-079)

```
Como:        Sistema (cotizador)
Quiero:      Filtrar registros con campos obligatorios ausentes y mapear la respuesta al modelo interno
Para:        Garantizar que solo datos íntegros llegan a la capa de presentación

Prioridad:   Alta
Estimación:  S (2 story points)
Dependencias: HU-01, HU-02
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-03

**Happy Path**
```gherkin
CRITERIO-3.1: Todos los campos de clasificación de riesgo se mapean correctamente
  Dado que:  plataforma-core-ohs devuelve una lista con registros completos
  Cuando:    el servicio procesa la respuesta de clasificación de riesgo
  Entonces:  todos los campos (id, nombre, descripcion) se preservan en el RiskClassificationDto
```

**Happy Path**
```gherkin
CRITERIO-3.2: Todos los campos de garantía se mapean correctamente
  Dado que:  plataforma-core-ohs devuelve una lista con registros completos
  Cuando:    el servicio procesa la respuesta de garantías
  Entonces:  todos los campos (id, nombre, claveIncendio, tarifable) se preservan en el GuaranteeDto
```

**Edge Case**
```gherkin
CRITERIO-3.3: Registro de clasificación de riesgo con id nulo es descartado
  Dado que:  la respuesta incluye un registro sin id
  Cuando:    el servicio filtra los registros
  Entonces:  el registro inválido es descartado y se registra un WARNING en los logs;
             los registros válidos se retornan normalmente
```

**Edge Case**
```gherkin
CRITERIO-3.4: Registro de garantía con nombre vacío es descartado
  Dado que:  la respuesta incluye un registro con nombre vacío
  Cuando:    el servicio filtra los registros
  Entonces:  el registro inválido es descartado y se registra un WARNING en los logs
```

---

### Reglas de Negocio

1. **Filtrado de registros inválidos (Clasificación de Riesgo)**: Se descartan registros donde `id` sea null o blank, o donde `nombre` sea null o blank. Se registra un WARNING por cada descarte.
2. **Filtrado de registros inválidos (Garantías)**: Se descartan registros donde `id` sea null o blank, o donde `nombre` sea null o blank. Se registra un WARNING por cada descarte.
3. **Fuente única**: Los catálogos de clasificación de riesgo y garantías solo se obtienen de `plataforma-core-ohs`. No se persisten en MongoDB.
4. **Reintentos**: Máximo 3 intentos con backoff exponencial (1000ms base, ×2). Solo para errores 5xx y de red; errores 4xx no se reintentan. Reutiliza la instancia `plataforma-core-ohs` ya configurada en `application.yaml`.
5. **Sin autenticación en mock**: La URL base se configura con `PLATAFORMA_CORE_OHS_URL`. El mock no requiere token.
6. **Extensión de artefactos existentes**: Los nuevos métodos se agregan a `CatalogsClient`, `CatalogsClientImpl`, `CatalogsService` y `CatalogsServiceImpl` existentes. No se crean nuevas clases de servicio/cliente.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `RiskClassificationDto` | ninguno (solo memoria) | nueva | DTO de clasificación de riesgo |
| `GuaranteeDto` | ninguno (solo memoria) | nueva | DTO de garantía |

> No se crea ninguna colección MongoDB. Los datos se leen del servicio externo y se retornan directamente.

#### Campos del modelo — `RiskClassificationDto`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | non-blank | Identificador de la clasificación |
| `nombre` | String | sí | non-blank | Nombre descriptivo |
| `descripcion` | String | sí | max 500 chars | Descripción de la clasificación |

#### Campos del modelo — `GuaranteeDto`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | non-blank | Identificador de la garantía |
| `nombre` | String | sí | max 200 chars | Nombre de la garantía |
| `claveIncendio` | String | sí | max 50 chars | Clave de incendio asociada |
| `tarifable` | Boolean | sí | — | Indica si la garantía es tarifable |

#### Índices / Constraints

No aplica — no hay persistencia en MongoDB para estos modelos.

### API Endpoints

#### GET /api/v1/catalogs/risk-classifications

- **Descripción**: Retorna el catálogo completo de clasificaciones de riesgo
- **Auth requerida**: no (pendiente feature auth — `SecurityConfig` actual es `permitAll()`)
- **Request Body**: ninguno

- **Response 200**:
  ```json
  [
    { "id": "RC-001", "nombre": "Riesgo Bajo", "descripcion": "Clasificación para riesgos con baja probabilidad de siniestro." },
    { "id": "RC-002", "nombre": "Riesgo Medio", "descripcion": "Clasificación para riesgos con probabilidad media de siniestro." }
  ]
  ```

- **Response 503**: servicio externo no disponible tras reintentos
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

---

#### GET /api/v1/catalogs/guarantees

- **Descripción**: Retorna el catálogo completo de garantías
- **Auth requerida**: no (pendiente feature auth — `SecurityConfig` actual es `permitAll()`)
- **Request Body**: ninguno

- **Response 200**:
  ```json
  [
    { "id": "GUA-001", "nombre": "Robo con Violencia", "claveIncendio": "RV", "tarifable": true },
    { "id": "GUA-002", "nombre": "Daños por Agua", "claveIncendio": "DA", "tarifable": false }
  ]
  ```

- **Response 503**: servicio externo no disponible tras reintentos
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

### Servicios Externos Consumidos

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `{plataforma-core-ohs.url}/v1/catalogs/risk-classification` | GET | Lista de clasificaciones de riesgo |
| `{plataforma-core-ohs.url}/v1/catalogs/guarantees` | GET | Lista de garantías |

**Respuesta del mock `risk-classification` (200 OK):**
```json
[
  { "id": "RC-001", "nombre": "Riesgo Bajo", "descripcion": "Clasificación para riesgos con baja probabilidad de siniestro." }
]
```

**Respuesta del mock `guarantees` (200 OK):**
```json
[
  { "id": "GUA-001", "nombre": "Robo con Violencia", "claveIncendio": "RV", "tarifable": true }
]
```

### Diseño Frontend

No aplica a esta spec — la integración con el frontend de Next.js es responsabilidad de FT-007.

### Arquitectura y Dependencias

- **Módulo**: `plataformas-danos-back` (Java 21 / Spring Boot 4.0.5)
- **Archivos nuevos**:
  - `model/dto/RiskClassificationDto.java`
  - `model/dto/GuaranteeDto.java`
- **Archivos modificados**:
  - `client/CatalogsClient.java` — agregar métodos `getRiskClassifications()` y `getGuarantees()`
  - `client/CatalogsClientImpl.java` — implementar llamadas a `/v1/catalogs/risk-classification` y `/v1/catalogs/guarantees`
  - `service/CatalogsService.java` — agregar métodos `getRiskClassifications()` y `getGuarantees()`
  - `service/CatalogsServiceImpl.java` — implementar con `@Retry`, filtros y fallbacks
  - `controller/CatalogsController.java` — agregar endpoints `GET /api/v1/catalogs/risk-classifications` y `GET /api/v1/catalogs/guarantees`
- **Reutilización**:
  - Bean `catalogsRestTemplate` (ya definido en `CatalogsClientConfig`) — misma instancia
  - Instancia retry `plataforma-core-ohs` en `application.yaml` — mismas reglas ya configuradas
  - `CatalogServiceUnavailableException` — misma excepción para el fallback
- **Dependencias ya presentes**: `resilience4j-spring-boot3`, `spring-boot-starter-web`, Lombok
- **Configuración externalizada**: Solo `plataforma-core-ohs.url` (ya en `application.yaml`)

### Notas de Implementación

- `CatalogsClientImpl` ya usa `@Qualifier("catalogsRestTemplate")` — los nuevos métodos de garantías y clasificaciones de riesgo usan `exchange()` con `ParameterizedTypeReference<List<...>>` igual que suscriptores, agentes y líneas de negocio.
- Los fallbacks se nombran `riskClassificationsFallback(Exception ex)` y `guaranteesFallback(Exception ex)` — misma firma y comportamiento que `subscribersFallback` existente.
- Las notas de URL externa: el mock expone `/v1/catalogs/risk-classification` (singular) para clasificaciones y `/v1/catalogs/guarantees` (plural) para garantías.
- El filtro de `RiskClassificationDto` descarta si `id` o `nombre` son null/blank (consistente con `SubscriberDto`). El filtro de `GuaranteeDto` descarta si `id` o `nombre` son null/blank.
- `application.yaml` no requiere cambios.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación

- [ ] Crear `model/dto/RiskClassificationDto.java` — campos `id`, `nombre`, `descripcion` (Lombok `@Data`)
- [ ] Crear `model/dto/GuaranteeDto.java` — campos `id`, `nombre`, `claveIncendio`, `tarifable` (Lombok `@Data`)
- [ ] Actualizar `client/CatalogsClient.java` — agregar `getRiskClassifications()` y `getGuarantees()`
- [ ] Actualizar `client/CatalogsClientImpl.java` — implementar llamadas a `/v1/catalogs/risk-classification` y `/v1/catalogs/guarantees` con `ParameterizedTypeReference`
- [ ] Actualizar `service/CatalogsService.java` — agregar `getRiskClassifications()` y `getGuarantees()`
- [ ] Actualizar `service/CatalogsServiceImpl.java` — implementar con `@Retry(name="plataforma-core-ohs", fallbackMethod=...)`, filtros de registros inválidos, y fallbacks `riskClassificationsFallback` y `guaranteesFallback`
- [ ] Actualizar `controller/CatalogsController.java` — agregar `GET /risk-classifications` y `GET /guarantees`
- [ ] Verificar que `application.yaml` ya tiene la instancia retry `plataforma-core-ohs` configurada (no requiere cambios)

#### Tests Backend

- [ ] `getRiskClassifications_validList_returns200WithList` — happy path, lista con datos
- [ ] `getRiskClassifications_emptyList_returnsEmptyList` — lista vacía retornada correctamente
- [ ] `riskClassificationsFallback_whenCalled_throwsCatalogServiceUnavailableException` — fallback directo
- [ ] `getRiskClassifications_recordMissingId_isDropped` — registro sin id descartado + log.warn
- [ ] `getRiskClassifications_mapsAllFields` — id, nombre, descripcion preservados
- [ ] `getGuarantees_validList_returns200WithList` — happy path, lista con datos
- [ ] `getGuarantees_emptyList_returnsEmptyList` — lista vacía retornada correctamente
- [ ] `guaranteesFallback_whenCalled_throwsCatalogServiceUnavailableException` — fallback directo
- [ ] `getGuarantees_recordMissingNombre_isDropped` — registro con nombre vacío descartado + log.warn
- [ ] `getGuarantees_mapsAllFields` — id, nombre, claveIncendio, tarifable preservados
- [ ] `getCatalogsController_getRiskClassifications_returns200WithList` — controller delega al service, retorna 200
- [ ] `getCatalogsController_getGuarantees_returns200WithList` — controller delega al service, retorna 200
- [ ] `getCatalogsController_getRiskClassifications_serviceUnavailable_propagatesException` — excepción propagada
- [ ] `getCatalogsController_getGuarantees_serviceUnavailable_propagatesException` — excepción propagada

### Frontend

No aplica a esta spec.

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 al 3.4
- [ ] Ejecutar skill `/risk-identifier` → clasificar riesgo de dependencia (catálogos críticos para cobertura)
- [ ] Verificar cobertura de tests ≥ 80% en `service/`, `client/`, `controller/`
- [ ] Prueba de integración manual: levantar `plataforma-core-ohs` y hacer `GET /api/v1/catalogs/risk-classifications` → lista no vacía
- [ ] Prueba de integración manual: `GET /api/v1/catalogs/guarantees` → lista no vacía con campos claveIncendio y tarifable
- [ ] Prueba de resiliencia manual: apagar mock → 503 + log CRITICAL en ambos endpoints
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
