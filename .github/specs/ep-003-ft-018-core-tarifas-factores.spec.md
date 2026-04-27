---
id: SPEC-006
status: APPROVED
feature: ep-003-ft-018-core-tarifas-factores
created: 2026-04-27
updated: 2026-04-27
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-001
  - SPEC-003
---

# Spec: FT-018 — Integración de Tarifas y Factores Técnicos

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa en `plataformas-danos-back` (Java 21 / Spring Boot) la integración con los endpoints de tarifas y factores técnicos de `plataforma-core-ohs`: tarifas de incendio, factores CAT (catastrófico TEV y FHM) por zona, y factores de equipo electrónico. A diferencia de FT-015/017 (que extienden `CatalogsClient`), FT-018 crea artefactos propios (`TariffsClient`, `TariffsService`, `TariffsController`) al tratarse de un dominio diferente con semántica de consulta distinta (query params, objetos únicos vs listas). Sigue el mismo patrón de resiliencia Resilience4j ya establecido.

### Requerimiento de Negocio

El motor de cálculo de primas del cotizador de seguros de daños requiere tarifas técnicas actualizadas — incendio, factores CAT y equipo electrónico — para calcular los 14 componentes de la prima. Estos datos provienen exclusivamente de `plataforma-core-ohs`. La disponibilidad de las tres familias de tarifas es prerequisito para las features de cálculo de primas (HU-015). Un fallo en el servicio externo debe retornar 503; una zona CAT inexistente debe retornar 404.

### Historias de Usuario

#### HU-01: Recuperar tarifas de incendio (HU-080)

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Consultar la lista de tarifas de incendio desde plataforma-core-ohs
Para:        Obtener la tasa base y el factor de recargo según zona y tipo constructivo del riesgo

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: SPEC-001 (mock server operativo), SPEC-003 (patrón cliente HTTP establecido)
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Consulta exitosa de tarifas de incendio
  Dado que:  plataforma-core-ohs está activo y tiene tarifas de incendio
  Cuando:    el cotizador llama a GET /api/v1/tariffs/fire
  Entonces:  recibe HTTP 200 con una lista de objetos
             [{ "zonaRiesgo": "ZONA_A", "tipoConstructivo": "Concreto", "tasaBase": 0.0012, "factorRecargo": 1.15 }, ...]
```

**Edge Case**
```gherkin
CRITERIO-1.2: Catálogo de tarifas de incendio vacío
  Dado que:  plataforma-core-ohs devuelve lista vacía
  Cuando:    el cotizador llama a GET /api/v1/tariffs/fire
  Entonces:  recibe HTTP 200 con lista vacía []
```

**Error Path**
```gherkin
CRITERIO-1.3: Servicio externo no disponible tras reintentos
  Dado que:  plataforma-core-ohs no responde después de 3 intentos
  Cuando:    el cotizador llama a GET /api/v1/tariffs/fire
  Entonces:  recibe HTTP 503 con { "message": "Servicio de catálogos no disponible",
             "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-02: Recuperar factor CAT por zona (HU-081)

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Consultar los factores CAT (TEV y FHM) para una zona de riesgo específica
Para:        Calcular el componente catastrófico de la prima según la ubicación del riesgo

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: SPEC-001, SPEC-003, HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-02

**Happy Path**
```gherkin
CRITERIO-2.1: Consulta exitosa de factor CAT para zona válida
  Dado que:  plataforma-core-ohs tiene configurado el factor CAT para ZONA_A
  Cuando:    el cotizador llama a GET /api/v1/tariffs/cat?zona=ZONA_A
  Entonces:  recibe HTTP 200 con { "zona": "ZONA_A", "factorTEV": 0.0015, "factorFHM": 0.0008 }
```

**Error Path**
```gherkin
CRITERIO-2.2: Zona no configurada retorna 404
  Dado que:  la zona "ZONA_INEXISTENTE" no tiene factor CAT en plataforma-core-ohs
  Cuando:    el cotizador llama a GET /api/v1/tariffs/cat?zona=ZONA_INEXISTENTE
  Entonces:  recibe HTTP 404 con { "message": "Tarifa CAT no encontrada para la zona indicada",
             "code": "TARIFF_NOT_FOUND" }
```

**Error Path**
```gherkin
CRITERIO-2.3: Servicio externo no disponible tras reintentos
  Dado que:  plataforma-core-ohs no responde después de 3 intentos
  Cuando:    el cotizador llama a GET /api/v1/tariffs/cat?zona=ZONA_A
  Entonces:  recibe HTTP 503 con { "message": "Servicio de catálogos no disponible",
             "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-03: Recuperar factores de equipo electrónico (HU-082)

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Consultar la lista de factores técnicos de equipo electrónico desde plataforma-core-ohs
Para:        Calcular el componente de equipo electrónico en la prima según clase y nivel de zona

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: SPEC-001, SPEC-003, HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-03

**Happy Path**
```gherkin
CRITERIO-3.1: Consulta exitosa de factores de equipo electrónico
  Dado que:  plataforma-core-ohs está activo y tiene factores de EE
  Cuando:    el cotizador llama a GET /api/v1/tariffs/electronic-equipment
  Entonces:  recibe HTTP 200 con una lista de objetos
             [{ "clase": "A", "nivelZona": "ALTO", "factor": 0.0025 }, ...]
```

**Edge Case**
```gherkin
CRITERIO-3.2: Catálogo de factores EE vacío
  Dado que:  plataforma-core-ohs devuelve lista vacía
  Cuando:    el cotizador llama a GET /api/v1/tariffs/electronic-equipment
  Entonces:  recibe HTTP 200 con lista vacía []
```

**Error Path**
```gherkin
CRITERIO-3.3: Servicio externo no disponible tras reintentos
  Dado que:  plataforma-core-ohs no responde después de 3 intentos
  Cuando:    el cotizador llama a GET /api/v1/tariffs/electronic-equipment
  Entonces:  recibe HTTP 503 con { "message": "Servicio de catálogos no disponible",
             "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-04: Mapear y filtrar registros inválidos (HU-083)

```
Como:        Sistema (cotizador)
Quiero:      Filtrar registros con campos obligatorios ausentes y mapear la respuesta al modelo interno
Para:        Garantizar que solo datos íntegros llegan a la capa de cálculo de primas

Prioridad:   Alta
Estimación:  S (2 story points)
Dependencias: HU-01, HU-02, HU-03
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-04

**Happy Path**
```gherkin
CRITERIO-4.1: Todos los campos de tarifa incendio se mapean correctamente
  Dado que:  plataforma-core-ohs devuelve una lista con registros completos
  Cuando:    el servicio procesa la respuesta de tarifas de incendio
  Entonces:  todos los campos (zonaRiesgo, tipoConstructivo, tasaBase, factorRecargo)
             se preservan en el TariffFireDto
```

**Happy Path**
```gherkin
CRITERIO-4.2: Todos los campos de factor CAT se mapean correctamente
  Dado que:  plataforma-core-ohs devuelve un objeto de factor CAT completo
  Cuando:    el servicio procesa la respuesta
  Entonces:  todos los campos (zona, factorTEV, factorFHM) se preservan en el TariffCatDto
```

**Happy Path**
```gherkin
CRITERIO-4.3: Todos los campos de factor EE se mapean correctamente
  Dado que:  plataforma-core-ohs devuelve una lista con registros completos
  Cuando:    el servicio procesa la respuesta de factores EE
  Entonces:  todos los campos (clase, nivelZona, factor) se preservan en el TariffElectronicEquipmentDto
```

**Edge Case**
```gherkin
CRITERIO-4.4: Registro de tarifa incendio con zonaRiesgo nula es descartado
  Dado que:  la respuesta incluye un registro de incendio sin zonaRiesgo
  Cuando:    el servicio filtra los registros
  Entonces:  el registro inválido es descartado y se registra un WARNING en los logs;
             los registros válidos se retornan normalmente
```

**Edge Case**
```gherkin
CRITERIO-4.5: Registro de factor EE con clase vacía es descartado
  Dado que:  la respuesta incluye un registro de EE con clase vacía
  Cuando:    el servicio filtra los registros
  Entonces:  el registro inválido es descartado y se registra un WARNING en los logs
```

---

### Reglas de Negocio

1. **Filtrado de registros inválidos (Incendio)**: Se descartan registros donde `zonaRiesgo` sea null o blank. Se registra un WARNING por cada descarte.
2. **Filtrado de registros inválidos (Equipo Electrónico)**: Se descartan registros donde `clase` sea null o blank. Se registra un WARNING por cada descarte.
3. **Factor CAT único**: El endpoint `/v1/tariffs/cat?zona={zona}` retorna un único objeto. Si el mock devuelve 404, el backend lanza `TariffNotFoundException` (→ HTTP 404 al cliente). No se aplica filtrado de lista.
4. **Parámetro zona obligatorio**: La consulta de factor CAT requiere el parámetro `zona`. Si está ausente → HTTP 400 (`@RequestParam(required = true)`).
5. **Fuente única**: Las tarifas solo se obtienen de `plataforma-core-ohs`. No se persisten en MongoDB.
6. **Reintentos**: Reutiliza la instancia retry `plataforma-core-ohs` ya configurada en `application.yaml` (máximo 3, backoff exponencial 1000ms × 2, solo 5xx y errores de red; 4xx no se reintentan).
7. **Separación de dominio**: Los artefactos de tarifas (`TariffsClient`, `TariffsService`, `TariffsController`) son independientes de `CatalogsClient`/`CatalogsService` — no se extienden los catálogos existentes.
8. **Reutilización del bean RestTemplate**: `TariffsClientImpl` usa `@Qualifier("catalogsRestTemplate")` — misma instancia ya definida en `CatalogsClientConfig`.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `TariffFireDto` | ninguno (solo memoria) | nueva | DTO de tarifa de incendio |
| `TariffCatDto` | ninguno (solo memoria) | nueva | DTO de factor CAT (TEV + FHM) por zona |
| `TariffElectronicEquipmentDto` | ninguno (solo memoria) | nueva | DTO de factor de equipo electrónico |

> No se crea ninguna colección MongoDB. Los datos se leen del servicio externo y se retornan directamente.

#### Campos del modelo — `TariffFireDto`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `zonaRiesgo` | String | sí | non-blank | Zona de riesgo (ej. ZONA_A) |
| `tipoConstructivo` | String | sí | max 50 chars | Tipo constructivo del inmueble |
| `tasaBase` | Double | sí | > 0 | Tasa base de incendio |
| `factorRecargo` | Double | sí | >= 1.0 | Factor de recargo aplicado |

#### Campos del modelo — `TariffCatDto`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `zona` | String | sí | non-blank | Zona de riesgo catastrófico |
| `factorTEV` | Double | sí | > 0 | Factor terremoto y erupción volcánica |
| `factorFHM` | Double | sí | > 0 | Factor ciclón, granizo y nevada (FHM) |

#### Campos del modelo — `TariffElectronicEquipmentDto`

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `clase` | String | sí | non-blank | Clase de equipo electrónico (A/B/C) |
| `nivelZona` | String | sí | non-blank | Nivel de zona (ALTO/MEDIO/BAJO) |
| `factor` | Double | sí | > 0 | Factor técnico de equipo electrónico |

#### Índices / Constraints

No aplica — no hay persistencia en MongoDB para estos modelos.

### API Endpoints

#### GET /api/v1/tariffs/fire

- **Descripción**: Retorna la lista completa de tarifas de incendio
- **Auth requerida**: no (SecurityConfig actual es `permitAll()`)
- **Request Body**: ninguno

- **Response 200**:
  ```json
  [
    { "zonaRiesgo": "ZONA_A", "tipoConstructivo": "Concreto", "tasaBase": 0.0012, "factorRecargo": 1.15 },
    { "zonaRiesgo": "ZONA_B", "tipoConstructivo": "Tabique", "tasaBase": 0.0018, "factorRecargo": 1.25 }
  ]
  ```

- **Response 503**: servicio externo no disponible tras reintentos
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

---

#### GET /api/v1/tariffs/cat

- **Descripción**: Retorna el factor CAT (TEV y FHM) para la zona indicada
- **Auth requerida**: no
- **Query Params**: `zona` (requerido — ej. `ZONA_A`)

- **Response 200**:
  ```json
  { "zona": "ZONA_A", "factorTEV": 0.0015, "factorFHM": 0.0008 }
  ```

- **Response 404**: zona no encontrada en el mock
  ```json
  { "message": "Tarifa CAT no encontrada para la zona indicada", "code": "TARIFF_NOT_FOUND" }
  ```

- **Response 400**: parámetro `zona` ausente
  ```json
  { "message": "El parámetro 'zona' es obligatorio", "code": "MISSING_PARAMETER" }
  ```

- **Response 503**: servicio externo no disponible tras reintentos
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

---

#### GET /api/v1/tariffs/electronic-equipment

- **Descripción**: Retorna la lista completa de factores técnicos de equipo electrónico
- **Auth requerida**: no
- **Request Body**: ninguno

- **Response 200**:
  ```json
  [
    { "clase": "A", "nivelZona": "ALTO", "factor": 0.0025 },
    { "clase": "B", "nivelZona": "MEDIO", "factor": 0.0018 }
  ]
  ```

- **Response 503**: servicio externo no disponible tras reintentos
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

### Servicios Externos Consumidos

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `{plataforma-core-ohs.url}/v1/tariffs/fire` | GET | Lista de tarifas de incendio |
| `{plataforma-core-ohs.url}/v1/tariffs/cat?zona={zona}` | GET | Factor CAT TEV+FHM para una zona |
| `{plataforma-core-ohs.url}/v1/tariffs/electronic-equipment` | GET | Lista de factores de equipo electrónico |

**Respuesta del mock `tariffs/fire` (200 OK):**
```json
[
  { "zonaRiesgo": "ZONA_A", "tipoConstructivo": "Concreto", "tasaBase": 0.0012, "factorRecargo": 1.15 }
]
```

**Respuesta del mock `tariffs/cat?zona=ZONA_A` (200 OK):**
```json
{ "zona": "ZONA_A", "factorTEV": 0.0015, "factorFHM": 0.0008 }
```

**Respuesta del mock `tariffs/cat?zona=ZONA_INEXISTENTE` (404 NOT FOUND):**
```json
{ "message": "Tarifa no encontrada para los parámetros indicados", "code": "TARIFF_NOT_FOUND" }
```

**Respuesta del mock `tariffs/electronic-equipment` (200 OK):**
```json
[
  { "clase": "A", "nivelZona": "ALTO", "factor": 0.0025 }
]
```

### Diseño Frontend

No aplica a esta spec — la integración con el frontend de Next.js es responsabilidad de FT-007.

### Arquitectura y Dependencias

- **Módulo**: `plataformas-danos-back` (Java 21 / Spring Boot 4.0.5)
- **Archivos nuevos**:
  - `model/dto/TariffFireDto.java`
  - `model/dto/TariffCatDto.java`
  - `model/dto/TariffElectronicEquipmentDto.java`
  - `exception/TariffNotFoundException.java`
  - `client/TariffsClient.java` (interfaz)
  - `client/TariffsClientImpl.java` (implementación)
  - `service/TariffsService.java` (interfaz)
  - `service/TariffsServiceImpl.java` (implementación)
  - `controller/TariffsController.java`
- **Archivos modificados**:
  - `exception/GlobalExceptionHandler.java` — agregar handler para `TariffNotFoundException` → 404
- **Reutilización**:
  - Bean `catalogsRestTemplate` (`@Qualifier("catalogsRestTemplate")`) — ya definido en `CatalogsClientConfig`
  - Instancia retry `plataforma-core-ohs` en `application.yaml` — mismas reglas ya configuradas
  - `CatalogServiceUnavailableException` — misma excepción para todos los fallbacks de tarifas
- **Dependencias ya presentes**: `resilience4j-spring-boot3`, `spring-boot-starter-web`, Lombok
- **Configuración externalizada**: Solo `plataforma-core-ohs.url` (ya en `application.yaml` — sin cambios)

### Notas de Implementación

- `TariffsClientImpl` usa `@Qualifier("catalogsRestTemplate")` e inyección por constructor — igual que `CatalogsClientImpl`. Los métodos `getTariffsFire()` y `getTariffsElectronicEquipment()` usan `exchange()` con `ParameterizedTypeReference<List<...>>`. El método `getTariffCat(String zona)` usa `getForObject()` con `TariffCatDto.class` y construye la URL con `UriComponentsBuilder` para el query param `zona`.
- `TariffsServiceImpl.getTariffCat(String zona)` maneja la excepción `HttpClientErrorException` con `HttpStatus.NOT_FOUND` → lanza `TariffNotFoundException`. Esto es consistente con `ZipCodeServiceImpl` (FT-016). Los errores 4xx no activan reintentos (ya configurado en `ignore-exceptions`).
- Los fallbacks se nombran `tariffFireFallback(Exception ex)`, `tariffCatFallback(String zona, Exception ex)` y `tariffElectronicEquipmentFallback(Exception ex)`. Nota: `tariffCatFallback` necesita el parámetro `zona` porque `getTariffCat` lo recibe — misma regla Resilience4j de FT-016.
- `TariffsController` usa `@RequestParam(required = true) String zona` en el endpoint CAT. Si `zona` es null/ausente, Spring retorna automáticamente 400 antes de llegar al service.
- `application.yaml` no requiere cambios — el retry `plataforma-core-ohs` ya está configurado.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación

- [ ] Crear `model/dto/TariffFireDto.java` — campos `zonaRiesgo`, `tipoConstructivo`, `tasaBase`, `factorRecargo` (Lombok `@Data`)
- [ ] Crear `model/dto/TariffCatDto.java` — campos `zona`, `factorTEV`, `factorFHM` (Lombok `@Data`)
- [ ] Crear `model/dto/TariffElectronicEquipmentDto.java` — campos `clase`, `nivelZona`, `factor` (Lombok `@Data`)
- [ ] Crear `exception/TariffNotFoundException.java` — excepción unchecked para zona CAT no encontrada
- [ ] Crear `client/TariffsClient.java` — interfaz con `getTariffsFire()`, `getTariffCat(String zona)`, `getTariffsElectronicEquipment()`
- [ ] Crear `client/TariffsClientImpl.java` — implementar las 3 llamadas con `@Qualifier("catalogsRestTemplate")`, `ParameterizedTypeReference` para listas y `UriComponentsBuilder` para query param de CAT
- [ ] Crear `service/TariffsService.java` — interfaz con `getTariffsFire()`, `getTariffCat(String zona)`, `getTariffsElectronicEquipment()`
- [ ] Crear `service/TariffsServiceImpl.java` — implementar con `@Retry(name="plataforma-core-ohs", fallbackMethod=...)`, filtros de registros inválidos (zonaRiesgo, clase), manejo de 404 para CAT, y fallbacks
- [ ] Crear `controller/TariffsController.java` — agregar `GET /api/v1/tariffs/fire`, `GET /api/v1/tariffs/cat`, `GET /api/v1/tariffs/electronic-equipment`
- [ ] Actualizar `exception/GlobalExceptionHandler.java` — agregar `@ExceptionHandler(TariffNotFoundException.class)` → 404 con `{ message, code: "TARIFF_NOT_FOUND" }`
- [ ] Verificar que `application.yaml` ya tiene retry `plataforma-core-ohs` configurado (no requiere cambios)

#### Tests Backend

- [ ] `getTariffsFire_validList_returns200WithList` — happy path, lista con datos
- [ ] `getTariffsFire_emptyList_returnsEmptyList` — lista vacía retornada correctamente
- [ ] `tariffFireFallback_whenCalled_throwsCatalogServiceUnavailableException` — fallback directo
- [ ] `getTariffsFire_recordMissingZonaRiesgo_isDropped` — registro sin zonaRiesgo descartado + log.warn
- [ ] `getTariffsFire_mapsAllFields` — zonaRiesgo, tipoConstructivo, tasaBase, factorRecargo preservados
- [ ] `getTariffCat_validZona_returnsDto` — happy path, objeto único con factorTEV y factorFHM
- [ ] `getTariffCat_zonaNotFound_throwsTariffNotFoundException` — mock devuelve 404 → TariffNotFoundException
- [ ] `tariffCatFallback_whenCalled_throwsCatalogServiceUnavailableException` — fallback directo con parámetro zona
- [ ] `getTariffCat_mapsAllFields` — zona, factorTEV, factorFHM preservados
- [ ] `getTariffsElectronicEquipment_validList_returns200WithList` — happy path, lista con datos
- [ ] `getTariffsElectronicEquipment_emptyList_returnsEmptyList` — lista vacía retornada correctamente
- [ ] `tariffElectronicEquipmentFallback_whenCalled_throwsCatalogServiceUnavailableException` — fallback directo
- [ ] `getTariffsElectronicEquipment_recordMissingClase_isDropped` — registro con clase vacía descartado + log.warn
- [ ] `getTariffsElectronicEquipment_mapsAllFields` — clase, nivelZona, factor preservados
- [ ] `getTariffsController_getFire_returns200WithList` — controller delega al service, retorna 200
- [ ] `getTariffsController_getCat_returns200WithDto` — controller delega al service, retorna 200 con objeto único
- [ ] `getTariffsController_getCat_tariffNotFound_propagatesException` — TariffNotFoundException propagada
- [ ] `getTariffsController_getElectronicEquipment_returns200WithList` — controller delega al service, retorna 200
- [ ] `getTariffsController_getFire_serviceUnavailable_propagatesException` — CatalogServiceUnavailableException propagada
- [ ] `getTariffsController_getCat_serviceUnavailable_propagatesException` — CatalogServiceUnavailableException propagada
- [ ] `getTariffsController_getElectronicEquipment_serviceUnavailable_propagatesException` — CatalogServiceUnavailableException propagada

### Frontend

No aplica a esta spec.

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 al 4.5
- [ ] Ejecutar skill `/risk-identifier` → clasificar riesgo de dependencia (tarifas críticas para cálculo de primas)
- [ ] Verificar cobertura de tests ≥ 80% en `service/`, `client/`, `controller/`
- [ ] Prueba de integración manual: levantar `plataforma-core-ohs` y hacer `GET /api/v1/tariffs/fire` → lista con datos
- [ ] Prueba de integración manual: `GET /api/v1/tariffs/cat?zona=ZONA_A` → objeto con factorTEV y factorFHM
- [ ] Prueba de integración manual: `GET /api/v1/tariffs/cat?zona=ZONA_INEXISTENTE` → 404
- [ ] Prueba de integración manual: `GET /api/v1/tariffs/electronic-equipment` → lista con clase, nivelZona, factor
- [ ] Prueba de resiliencia manual: apagar mock → 503 + log CRITICAL en los tres endpoints
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
