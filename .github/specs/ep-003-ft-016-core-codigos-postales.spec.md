---
id: SPEC-004
status: IMPLEMENTED
feature: ep-003-ft-016-core-codigos-postales
created: 2026-04-27
updated: 2026-04-27
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-001
  - SPEC-003
---

# Spec: FT-016 — Consulta, Validación y Mapeo de Códigos Postales

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa en `plataformas-danos-back` (Java 21 / Spring Boot) la capa de integración con `plataforma-core-ohs` para consultar información de zona (zonaCAT y nivelTecnico) a partir de un código postal. Incluye validación de formato, llamada al servicio externo, mapeo al modelo interno con manejo de valores por defecto, y un endpoint REST propio que el cotizador expone al frontend. La resiliencia sigue el mismo patrón de Resilience4j ya establecido en SPEC-003.

### Requerimiento de Negocio

El cotizador de seguros de daños necesita clasificar cada ubicación de riesgo geográficamente (zonaCAT y nivelTecnico) para aplicar tarifas y factores técnicos correctos en el cálculo de primas. Esta información proviene exclusivamente del servicio `plataforma-core-ohs`. El sistema debe validar el formato del código postal antes de consultar el servicio, y ser resiliente ante fallos transitorios del mismo.

### Historias de Usuario

#### HU-01: Consultar información de zona por código postal (HU-074)

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Consultar un código postal en plataforma-core-ohs y obtener zonaCAT y nivelTecnico
Para:        Clasificar geográficamente la ubicación de riesgo y aplicar tarifas precisas

Prioridad:   Alta
Estimación:  M (3 story points)
Dependencias: SPEC-001 (mock server operativo), SPEC-003 (patrón cliente HTTP establecido)
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Consulta exitosa de código postal válido y existente
  Dado que:  plataforma-core-ohs está activo y el CP "06600" existe en el catálogo
  Cuando:    el cotizador llama a GET /api/v1/postal-codes/06600
  Entonces:  recibe HTTP 200 con { "codigoPostal": "06600", "zonaCAT": "ZONA_A",
             "nivelTecnico": "ALTO", "estado": "Ciudad de México",
             "municipio": "Cuauhtémoc", "ciudad": "Ciudad de México" }
```

**Error Path**
```gherkin
CRITERIO-1.2: Código postal no encontrado en el catálogo
  Dado que:  el CP "99999" no existe en plataforma-core-ohs
  Cuando:    el cotizador llama a GET /api/v1/postal-codes/99999
  Entonces:  recibe HTTP 404 con { "message": "Código postal no encontrado",
             "code": "ZIP_NOT_FOUND" }
```

**Error Path**
```gherkin
CRITERIO-1.3: Servicio externo no disponible tras reintentos
  Dado que:  plataforma-core-ohs no responde después de 3 intentos
  Cuando:    el cotizador llama a GET /api/v1/postal-codes/06600
  Entonces:  recibe HTTP 503 con { "message": "Servicio de catálogos no disponible",
             "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-02: Validar formato del código postal (HU-075)

```
Como:        Sistema (cotizador)
Quiero:      Validar que el código postal tenga formato de 5 dígitos numéricos
             antes de consultar el servicio externo
Para:        Evitar llamadas innecesarias al servicio externo y dar feedback preciso al usuario

Prioridad:   Alta
Estimación:  S (2 story points)
Dependencias: HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-02

**Happy Path**
```gherkin
CRITERIO-2.1: Código postal con formato válido pasa validación
  Dado que:  el usuario ingresa "06600" (5 dígitos numéricos)
  Cuando:    el sistema evalúa el formato
  Entonces:  el formato es válido y el sistema procede a consultar plataforma-core-ohs
```

**Error Path**
```gherkin
CRITERIO-2.2: Código postal con formato inválido rechazado sin consultar servicio
  Dado que:  el usuario ingresa "ABCDE" o "1234" o "123456"
  Cuando:    el sistema valida el formato
  Entonces:  recibe HTTP 400 con { "message": "Formato de código postal inválido. Debe ser exactamente 5 dígitos numéricos.", "code": "INVALID_ZIP_FORMAT" }
             y NO se realiza ninguna llamada a plataforma-core-ohs
```

**Edge Case**
```gherkin
CRITERIO-2.3: Código postal vacío es rechazado
  Dado que:  el path param codigoPostal está vacío
  Cuando:    el sistema evalúa la solicitud
  Entonces:  recibe HTTP 400 con { "message": "Formato de código postal inválido. Debe ser exactamente 5 dígitos numéricos.", "code": "INVALID_ZIP_FORMAT" }
```

---

#### HU-03: Mapear zonas al modelo interno (HU-076)

```
Como:        Sistema (cotizador)
Quiero:      Transformar la respuesta de plataforma-core-ohs al modelo de dominio interno,
             asignando valores por defecto cuando faltan campos de zona
Para:        Garantizar que los datos de zona estén siempre disponibles para el cálculo de primas

Prioridad:   Alta
Estimación:  S (2 story points)
Dependencias: HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-03

**Happy Path**
```gherkin
CRITERIO-3.1: Todos los campos de zona se mapean correctamente
  Dado que:  plataforma-core-ohs devuelve una respuesta completa con todos los campos
  Cuando:    el servicio procesa la respuesta
  Entonces:  todos los campos (codigoPostal, zonaCAT, nivelTecnico, estado, municipio, ciudad)
             se preservan en el ZipCodeDto de respuesta
```

**Edge Case**
```gherkin
CRITERIO-3.2: Campos de zona faltantes → valores por defecto
  Dado que:  plataforma-core-ohs devuelve respuesta sin zonaCAT o sin nivelTecnico
  Cuando:    el servicio intenta mapear el registro
  Entonces:  el campo faltante se asigna con un valor por defecto ("ZONA_INDEFINIDA" o "NIVEL_INDEFINIDO")
             y se registra un WARNING en los logs con el código postal afectado
```

---

### Reglas de Negocio

1. **Formato obligatorio**: El `codigoPostal` debe ser exactamente 5 dígitos numéricos (`^\d{5}$`). Validación en la capa de servicio antes de llamar al cliente HTTP.
2. **Fuente única**: La zona CAT y el nivel técnico solo se obtienen de `plataforma-core-ohs`. No se persisten en MongoDB.
3. **Valores por defecto**: Si la respuesta externa carece de `zonaCAT` o `nivelTecnico`, se asigna `"ZONA_INDEFINIDA"` y `"NIVEL_INDEFINIDO"` respectivamente, con log de WARNING.
4. **Reintentos**: Máximo 3 intentos con backoff exponencial (1000ms base, ×2). Solo para errores 5xx y de red; errores 4xx no se reintentan.
5. **Sin autenticación en mock**: La URL base se configura con `PLATAFORMA_CORE_OHS_URL`. El mock no requiere token.
6. **Propagación de 404**: Cuando el servicio externo retorna 404 (`ZIP_NOT_FOUND`), el backend Java retorna 404 al cliente con el mismo código de error.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `ZipCodeDto` | ninguno (solo memoria) | nueva | DTO de respuesta con zona geográfica |

> No se crea ninguna colección MongoDB. Los datos se leen del servicio externo y se retornan directamente.

#### Campos del modelo — `ZipCodeDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `codigoPostal` | String | sí | 5 dígitos | Código postal consultado |
| `zonaCAT` | String | sí | — | Zona catastrófica: `ZONA_A`, `ZONA_B`, `ZONA_C`, `ZONA_D` o `ZONA_INDEFINIDA` |
| `nivelTecnico` | String | sí | — | Nivel técnico: `ALTO`, `MEDIO`, `BAJO` o `NIVEL_INDEFINIDO` |
| `estado` | String | sí | max 100 chars | Estado/provincia de México |
| `municipio` | String | sí | max 200 chars | Municipio o delegación |
| `ciudad` | String | no | max 200 chars | Ciudad (puede ser null) |

#### Índices / Constraints

No aplica — no hay persistencia en MongoDB para este modelo.

### API Endpoints

#### GET /api/v1/postal-codes/{codigoPostal}

- **Descripción**: Retorna información de zona geográfica para el código postal consultado
- **Auth requerida**: no (pendiente feature auth — `SecurityConfig` actual es `permitAll()`)
- **Path Param**: `codigoPostal` — string de 5 dígitos numéricos
- **Request Body**: ninguno

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

- **Response 400**: formato inválido (`^\d{5}$` no se cumple)
  ```json
  { "message": "Formato de código postal inválido. Debe ser exactamente 5 dígitos numéricos.", "code": "INVALID_ZIP_FORMAT" }
  ```

- **Response 404**: código postal no existe en el catálogo externo
  ```json
  { "message": "Código postal no encontrado", "code": "ZIP_NOT_FOUND" }
  ```

- **Response 503**: servicio externo no disponible tras reintentos
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

### Servicio Externo Consumido

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `{plataforma-core-ohs.url}/v1/zip-codes/{zipCode}` | GET | Consulta código postal en mock |

**Respuesta del mock (200 OK):**
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

**Respuesta del mock (404):**
```json
{ "message": "Código postal no encontrado", "code": "ZIP_NOT_FOUND" }
```

**Respuesta del mock (400):**
```json
{ "message": "Formato de código postal inválido", "code": "INVALID_ZIP_FORMAT" }
```

### Diseño Frontend

No aplica a esta spec — la integración con el frontend de Next.js es responsabilidad de FT-007. Esta spec cubre únicamente el backend Java.

### Arquitectura y Dependencias

- **Módulo**: `plataformas-danos-back` (Java 21 / Spring Boot 4.0.5)
- **Archivos nuevos**:
  - `model/dto/ZipCodeDto.java`
  - `exception/ZipCodeNotFoundException.java`
  - `exception/InvalidZipCodeFormatException.java`
  - `client/ZipCodeClient.java` — interfaz del cliente HTTP
  - `client/ZipCodeClientImpl.java` — implementación con RestTemplate
  - `service/ZipCodeService.java` — interfaz del servicio
  - `service/ZipCodeServiceImpl.java` — validación + llamada + mapeo + resiliencia
  - `controller/ZipCodeController.java` — endpoint `GET /api/v1/postal-codes/{codigoPostal}`
- **Archivos modificados**:
  - `exception/GlobalExceptionHandler.java` — agregar handlers para `ZipCodeNotFoundException` (404) e `InvalidZipCodeFormatException` (400)
- **Reutilización**:
  - Bean `catalogsRestTemplate` (ya definido en `CatalogsClientConfig`) — misma instancia para `ZipCodeClientImpl`
  - Instancia retry `plataforma-core-ohs` en `application.yaml` — mismas reglas ya configuradas en SPEC-003
- **Dependencias ya presentes**: `resilience4j-spring-boot3`, `spring-boot-starter-web`, Lombok
- **Configuración externalizada**: Solo `plataforma-core-ohs.url` (ya en `application.yaml`)

### Notas de Implementación

- El `ZipCodeClientImpl` usa `@Qualifier("catalogsRestTemplate")` con constructor manual (misma técnica que `CatalogsClientImpl`).
- La validación de formato (`^\d{5}$`) ocurre en `ZipCodeServiceImpl` antes de llamar al cliente. Si falla, lanza `InvalidZipCodeFormatException` sin ninguna llamada externa.
- Cuando el cliente recibe `HttpClientErrorException(404)`, el servicio lanza `ZipCodeNotFoundException`. No aplica reintento (404 está en `ignore-exceptions` junto al resto de 4xx).
- El valor por defecto de `zonaCAT` si es null es `"ZONA_INDEFINIDA"`; para `nivelTecnico` es `"NIVEL_INDEFINIDO"`. Ambos se registran con `log.warn()`.
- `GlobalExceptionHandler` debe ordenar los handlers: `ZipCodeNotFoundException` → 404, `InvalidZipCodeFormatException` → 400. Los handlers existentes de SPEC-003 no se modifican.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación

- [x] Crear `model/dto/ZipCodeDto.java` — campos `codigoPostal`, `zonaCAT`, `nivelTecnico`, `estado`, `municipio`, `ciudad` (Lombok `@Data`)
- [x] Crear `exception/ZipCodeNotFoundException.java` — `extends RuntimeException`
- [x] Crear `exception/InvalidZipCodeFormatException.java` — `extends RuntimeException`
- [x] Actualizar `exception/GlobalExceptionHandler.java` — agregar `@ExceptionHandler` para `ZipCodeNotFoundException` → 404 y `InvalidZipCodeFormatException` → 400
- [x] Crear `client/ZipCodeClient.java` (interfaz) + `ZipCodeClientImpl.java` — llamada a `/v1/zip-codes/{zipCode}`, constructor manual con `@Qualifier("catalogsRestTemplate")`
- [x] Crear `service/ZipCodeService.java` (interfaz) + `ZipCodeServiceImpl.java` — validación de formato + `@Retry` + mapeo + valores por defecto + fallback
- [x] Crear `controller/ZipCodeController.java` — `GET /api/v1/postal-codes/{codigoPostal}`
- [x] Verificar que `application.yaml` ya tiene la instancia retry `plataforma-core-ohs` configurada (no requiere cambios)

#### Tests Backend

- [x] `test_get_postal_code_valid_returns_200` — happy path, CP existente
- [x] `test_get_postal_code_invalid_format_returns_400` — "ABCDE" → no llama cliente, lanza excepción
- [x] `test_get_postal_code_not_found_returns_404` — cliente lanza HttpClientErrorException 404 → ZipCodeNotFoundException
- [x] `test_get_postal_code_service_unavailable_returns_503` — fallback activa CatalogServiceUnavailableException
- [x] `test_zip_code_service_maps_all_fields` — todos los campos del DTO mapeados correctamente
- [x] `test_zip_code_service_applies_default_zona_when_null` — zonaCAT null → "ZONA_INDEFINIDA" + log.warn
- [x] `test_zip_code_service_applies_default_nivel_when_null` — nivelTecnico null → "NIVEL_INDEFINIDO" + log.warn
- [x] `test_zip_code_service_validates_format_before_calling_client` — formato inválido → cliente no es invocado
- [x] `test_zip_code_fallback_throws_catalog_service_unavailable` — zipCodeFallback → CatalogServiceUnavailableException
- [x] `test_zip_code_controller_returns_200_with_dto` — controller delega al service y retorna ResponseEntity OK
- [x] `test_zip_code_controller_propagates_not_found_exception` — service lanza ZipCodeNotFoundException → propagada
- [x] `test_zip_code_controller_propagates_invalid_format_exception` — service lanza InvalidZipCodeFormatException → propagada

### Frontend

No aplica a esta spec.

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 al 3.2
- [ ] Ejecutar skill `/risk-identifier` → clasificar riesgo de dependencia crítica (zona CAT para cálculo de primas)
- [ ] Verificar cobertura de tests ≥ 80% en `service/`, `client/`, `controller/`
- [ ] Prueba de integración manual: levantar `plataforma-core-ohs` y hacer `GET /api/v1/postal-codes/06600` → zonaCAT: ZONA_A
- [ ] Prueba manual de validación: `GET /api/v1/postal-codes/ABCDE` → 400 + no log de client call
- [ ] Prueba manual de 404: `GET /api/v1/postal-codes/00001` → 404 + code ZIP_NOT_FOUND
- [ ] Prueba de resiliencia manual: apagar mock → 503 + log CRITICAL
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
