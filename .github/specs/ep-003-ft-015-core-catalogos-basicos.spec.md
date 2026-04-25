---
id: SPEC-003
status: APPROVED
feature: ep-003-ft-015-core-catalogos-basicos
created: 2026-04-25
updated: 2026-04-25
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-001
  - SPEC-002
---

# Spec: FT-015 — Conectividad y Consumo de Catálogos Básicos

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa en `plataformas-danos-back` (Java 21 / Spring Boot) la capa de integración con el servicio externo `plataforma-core-ohs` para consumir los catálogos básicos: suscriptores, agentes y giros (líneas de negocio). Incluye la configuración del cliente HTTP, la transformación de datos al modelo interno, y un mecanismo de resiliencia con reintentos exponenciales ante fallos del servicio externo. Los catálogos se exponen al frontend a través de endpoints REST propios.

### Requerimiento de Negocio

El cotizador de seguros de daños necesita catálogos actualizados de suscriptores, agentes y giros para poblar los formularios de cotización. Estos datos provienen de `plataforma-core-ohs` (o su mock en desarrollo). El sistema debe ser resiliente a fallos del servicio externo, transformar los datos al modelo interno y exponerlos de forma unificada al frontend sin acoplar directamente la UI al contrato de la API externa.

### Historias de Usuario

#### HU-01: Conexión al servicio de catálogos básicos (HU-068)

```
Como:        Sistema (cotizador — plataformas-danos-back)
Quiero:      Establecer y gestionar la conexión HTTP con plataforma-core-ohs
Para:        Consultar información de suscriptores, agentes y giros necesaria para cotizar

Prioridad:   Alta
Estimación:  S (2 story points)
Dependencias: SPEC-001 (mock server operativo)
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-01

**Happy Path**
```gherkin
CRITERIO-1.1: Conexión exitosa al arrancar el cotizador
  Dado que:  plataformas-danos-back inicia con PLATAFORMA_CORE_OHS_URL configurada
  Cuando:    el contexto de Spring se levanta correctamente
  Entonces:  el bean del cliente HTTP se registra sin errores y la URL base queda configurada
```

**Error Path**
```gherkin
CRITERIO-1.2: Fallo de conexión registrado en logs
  Dado que:  plataforma-core-ohs no responde
  Cuando:    el cotizador intenta consultar cualquier catálogo
  Entonces:  se registra un log de nivel ERROR con los detalles del fallo
             y se notifica al sistema que el catálogo no está disponible
```

**Edge Case**
```gherkin
CRITERIO-1.3: Configuración incorrecta detectada
  Dado que:  PLATAFORMA_CORE_OHS_URL es una URL inválida o vacía
  Cuando:    el cliente HTTP intenta realizar una llamada
  Entonces:  la llamada falla inmediatamente sin reintentos
             y se registra un ERROR de configuración
```

---

#### HU-02: Recuperar catálogo de suscriptores (HU-069)

```
Como:        Sistema (cotizador)
Quiero:      Recuperar el catálogo de suscriptores desde plataforma-core-ohs
Para:        Ofrecer la lista actualizada en el formulario de cotización

Prioridad:   Alta
Estimación:  S (3 story points)
Dependencias: HU-01 (conexión establecida)
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-02

**Happy Path**
```gherkin
CRITERIO-2.1: Lista de suscriptores recuperada exitosamente
  Dado que:  plataforma-core-ohs está activo y devuelve suscriptores
  Cuando:    el cotizador llama a GET /api/v1/catalogs/subscribers
  Entonces:  recibe HTTP 200 con un array de objetos { id, nombre, clave, activo }
             y la lista contiene al menos los campos id y nombre
```

**Happy Path**
```gherkin
CRITERIO-2.2: Lista vacía es respuesta válida
  Dado que:  plataforma-core-ohs devuelve una lista vacía []
  Cuando:    el cotizador llama a GET /api/v1/catalogs/subscribers
  Entonces:  recibe HTTP 200 con array vacío []
             y no se registran errores en los logs
```

**Error Path**
```gherkin
CRITERIO-2.3: Fallo del servicio externo → HTTP 503
  Dado que:  plataforma-core-ohs no está disponible después de agotar reintentos
  Cuando:    el cotizador llama a GET /api/v1/catalogs/subscribers
  Entonces:  recibe HTTP 503 con body { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-03: Recuperar catálogo de agentes (HU-070)

```
Como:        Sistema (cotizador)
Quiero:      Recuperar el catálogo de agentes desde plataforma-core-ohs
Para:        Ofrecer la lista actualizada en el formulario de cotización

Prioridad:   Alta
Estimación:  S (3 story points)
Dependencias: HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-03

**Happy Path**
```gherkin
CRITERIO-3.1: Lista de agentes recuperada exitosamente
  Dado que:  plataforma-core-ohs está activo y devuelve agentes
  Cuando:    el cotizador llama a GET /api/v1/catalogs/agents
  Entonces:  recibe HTTP 200 con un array de objetos { id, nombre, clave, activo }
```

**Error Path**
```gherkin
CRITERIO-3.2: Fallo del servicio externo → HTTP 503
  Dado que:  plataforma-core-ohs no está disponible después de agotar reintentos
  Cuando:    el cotizador llama a GET /api/v1/catalogs/agents
  Entonces:  recibe HTTP 503 con body { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-04: Recuperar catálogo de giros (HU-071)

```
Como:        Sistema (cotizador)
Quiero:      Recuperar el catálogo de giros (líneas de negocio) desde plataforma-core-ohs
Para:        Ofrecer la lista actualizada en el formulario de cotización

Prioridad:   Media
Estimación:  S (2 story points)
Dependencias: HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-04

**Happy Path**
```gherkin
CRITERIO-4.1: Lista de giros recuperada exitosamente
  Dado que:  plataforma-core-ohs está activo y devuelve giros
  Cuando:    el cotizador llama a GET /api/v1/catalogs/business-lines
  Entonces:  recibe HTTP 200 con un array de objetos { id, descripcion, claveIncendio, activo }
```

**Error Path**
```gherkin
CRITERIO-4.2: Fallo del servicio externo → HTTP 503
  Dado que:  plataforma-core-ohs no está disponible
  Cuando:    el cotizador llama a GET /api/v1/catalogs/business-lines
  Entonces:  recibe HTTP 503 con body { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
```

---

#### HU-05: Mapeo de datos al modelo interno (HU-072)

```
Como:        Sistema (cotizador)
Quiero:      Transformar la respuesta de plataforma-core-ohs al modelo de dominio interno
Para:        Desacoplar el contrato externo de la lógica de negocio del cotizador

Prioridad:   Alta
Estimación:  S (3 story points)
Dependencias: HU-02, HU-03, HU-04
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-05

**Happy Path**
```gherkin
CRITERIO-5.1: Datos transformados sin pérdida de información
  Dado que:  plataforma-core-ohs devuelve datos válidos para cualquier catálogo
  Cuando:    el servicio de catálogos mapea la respuesta al modelo interno
  Entonces:  todos los campos relevantes se preservan en el DTO de respuesta
             y los nombres de campo siguen la convención camelCase del modelo interno
```

**Error Path**
```gherkin
CRITERIO-5.2: Campo obligatorio faltante genera error de mapeo
  Dado que:  la respuesta de plataforma-core-ohs omite un campo obligatorio (id o nombre)
  Cuando:    el servicio intenta mapear el registro
  Entonces:  ese registro se descarta y se registra un log de WARNING con el detalle del campo faltante
             y los registros válidos restantes sí se retornan
```

---

#### HU-06: Manejo de errores y reintentos con backoff exponencial (HU-073)

```
Como:        Sistema (cotizador)
Quiero:      Aplicar reintentos con backoff exponencial ante fallos recuperables del servicio externo
Para:        Garantizar la resiliencia y disponibilidad del cotizador ante fallas transitorias

Prioridad:   Alta
Estimación:  M (4 story points)
Dependencias: HU-01
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-06

**Happy Path**
```gherkin
CRITERIO-6.1: Servicio se recupera durante reintentos
  Dado que:  plataforma-core-ohs falla en el primer intento pero responde en el segundo
  Cuando:    el cotizador llama a cualquier endpoint de catálogo
  Entonces:  el sistema reintenta la llamada
             y retorna la respuesta exitosa del segundo intento
             y se registra un log INFO indicando la recuperación
```

**Error Path**
```gherkin
CRITERIO-6.2: Fallo crítico tras agotar reintentos
  Dado que:  plataforma-core-ohs no responde en ninguno de los 3 intentos configurados
  Cuando:    el cotizador agota los reintentos
  Entonces:  se registra un log CRITICAL con los detalles del fallo y el servicio afectado
             y se retorna HTTP 503 al cliente
```

**Edge Case**
```gherkin
CRITERIO-6.3: Error HTTP 4xx no activa reintentos
  Dado que:  plataforma-core-ohs responde con HTTP 400 o 404
  Cuando:    el cotizador recibe la respuesta
  Entonces:  NO se activan reintentos
             y se registra un log ERROR con el código de estado recibido
```

### Reglas de Negocio

1. **Solo lectura**: Los endpoints de catálogos son exclusivamente `GET` — no se persisten datos en MongoDB local.
2. **Reintentos configurables**: Máximo 3 intentos, delay inicial 1000ms, multiplicador 2.0, delay máximo 8000ms. Todos los valores son externalizables via `application.yml`.
3. **Errores recuperables**: Códigos HTTP `500, 502, 503, 504` y excepciones de red activan reintentos. Códigos `4xx` son no recuperables.
4. **Mapeo tolerante**: Si un elemento del array externo carece de campo `id` o `nombre`, se descarta ese elemento (log WARNING). Los demás elementos se procesan normalmente.
5. **Sin autenticación**: El mock `plataforma-core-ohs` no requiere JWT. La URL base se configura con `PLATAFORMA_CORE_OHS_URL`.
6. **CORS**: Los endpoints `/api/v1/catalogs/*` están protegidos por el `SecurityFilterChain` existente.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `SubscriberDto` | ninguno (solo memoria) | nueva | DTO de suscriptor al modelo interno |
| `AgentDto` | ninguno (solo memoria) | nueva | DTO de agente al modelo interno |
| `BusinessLineDto` | ninguno (solo memoria) | nueva | DTO de giro/línea de negocio al modelo interno |

> No se crea ninguna colección MongoDB. Los datos se leen del servicio externo y se retornan directamente.

#### Campos del modelo — `SubscriberDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único del suscriptor |
| `nombre` | String | sí | max 200 chars | Nombre o razón social |
| `clave` | String | sí | max 50 chars | Clave interna del suscriptor |
| `activo` | Boolean | sí | — | Indica si está activo en el sistema origen |

#### Campos del modelo — `AgentDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único del agente |
| `nombre` | String | sí | max 200 chars | Nombre del agente |
| `clave` | String | sí | max 50 chars | Clave interna del agente |
| `activo` | Boolean | sí | — | Indica si está activo |

#### Campos del modelo — `BusinessLineDto` (response)

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | no vacío | Identificador único del giro |
| `descripcion` | String | sí | max 300 chars | Descripción del giro |
| `claveIncendio` | String | sí | max 20 chars | Clave de incendio asociada |
| `activo` | Boolean | sí | — | Indica si está activo |

#### Índices / Constraints

No aplica — no hay persistencia en MongoDB para estos modelos.

### API Endpoints

> Los endpoints de `plataformas-danos-back` siguen la convención `/api/v1/...`.

#### GET /api/v1/catalogs/subscribers

- **Descripción**: Retorna el catálogo de suscriptores obtenido de `plataforma-core-ohs`
- **Auth requerida**: sí (JWT)
- **Request Body**: ninguno
- **Response 200**:
  ```json
  [
    { "id": "SUB-001", "nombre": "Empresa ABC S.A. de C.V.", "clave": "ABC", "activo": true },
    { "id": "SUB-002", "nombre": "Corporativo XYZ", "clave": "XYZ", "activo": true }
  ]
  ```
- **Response 503**: servicio externo no disponible tras reintentos
  ```json
  { "message": "Servicio de catálogos no disponible", "code": "CATALOG_SERVICE_UNAVAILABLE" }
  ```

#### GET /api/v1/catalogs/agents

- **Descripción**: Retorna el catálogo de agentes obtenido de `plataforma-core-ohs`
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "AGT-001", "nombre": "Juan Pérez García", "clave": "JPG", "activo": true }
  ]
  ```
- **Response 503**: igual al anterior

#### GET /api/v1/catalogs/business-lines

- **Descripción**: Retorna el catálogo de giros/líneas de negocio obtenido de `plataforma-core-ohs`
- **Auth requerida**: sí (JWT)
- **Response 200**:
  ```json
  [
    { "id": "BL-001", "descripcion": "Comercio al por menor", "claveIncendio": "CM", "activo": true }
  ]
  ```
- **Response 503**: igual al anterior

### Diseño Frontend

No aplica a esta spec — la integración con el frontend de Next.js es responsabilidad de FT-007 (Integración de Servicios). Esta spec cubre únicamente el backend Java.

### Arquitectura y Dependencias

- **Módulo**: `plataformas-danos-back` (Java 21 / Spring Boot 4.0.5)
- **Archivos nuevos**:
  - `model/dto/SubscriberDto.java`
  - `model/dto/AgentDto.java`
  - `model/dto/BusinessLineDto.java`
  - `client/CatalogsClient.java` — interfaz del cliente HTTP
  - `client/CatalogsClientImpl.java` — implementación con WebClient/RestTemplate
  - `service/CatalogsService.java` — interfaz del servicio
  - `service/CatalogsServiceImpl.java` — orquesta cliente + mapeo + resiliencia
  - `controller/CatalogsController.java` — endpoints `/api/v1/catalogs/*`
  - `config/CatalogsClientConfig.java` — bean WebClient con URL base
  - `config/Resilience4jCatalogsConfig.java` — configuración de retry
- **Dependencias adicionales en `pom.xml`**:
  - `spring-boot-starter-webflux` (WebClient) o `spring-boot-starter-web` con `RestTemplate`
  - `resilience4j-spring-boot3` (ya debería estar disponible como parte del stack aprobado)
- **Configuración externalizada en `application.yml`**:
  ```yaml
  plataforma-core-ohs:
    url: ${PLATAFORMA_CORE_OHS_URL:http://localhost:3001}
    timeout-ms: 5000
    retry:
      max-attempts: 3
      initial-delay-ms: 1000
      multiplier: 2.0
      max-delay-ms: 8000
  ```
- **Servicios externos consumidos**:
  - `GET {plataforma-core-ohs.url}/v1/subscribers`
  - `GET {plataforma-core-ohs.url}/v1/agents`
  - `GET {plataforma-core-ohs.url}/v1/business-lines`

### Notas de Implementación

- Usar `RestTemplate` con `SimpleClientHttpRequestFactory` en lugar de WebFlux para mantener el stack blocking consistente con Spring MVC.
- La lógica de retry se implementa con `@Retryable` de `spring-retry` (alternativa simple) o con `Retry` de Resilience4j. Priorizar Resilience4j 2.3.0 (ya en el stack aprobado).
- El mapeo es directo (campos del DTO externo = campos del DTO interno en este caso) ya que el mock usa los mismos nombres. Si el servicio real cambiara, solo se modifica `CatalogsClientImpl`.
- Los errores HTTP `5xx` del servicio externo se traducen a `503 SERVICE_UNAVAILABLE` en la respuesta al frontend. Los `4xx` se propagan como `502 BAD_GATEWAY` con log de ERROR.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación

- [ ] Crear `model/dto/SubscriberDto.java` — campos `id`, `nombre`, `clave`, `activo` (Lombok `@Data`)
- [ ] Crear `model/dto/AgentDto.java` — campos `id`, `nombre`, `clave`, `activo`
- [ ] Crear `model/dto/BusinessLineDto.java` — campos `id`, `descripcion`, `claveIncendio`, `activo`
- [ ] Crear `config/CatalogsClientConfig.java` — bean `RestTemplate` con `baseUrl` y `connectionTimeout`
- [ ] Crear `config/Resilience4jCatalogsConfig.java` — configuración `RetryConfig` (3 intentos, backoff exponencial, solo errores recuperables)
- [ ] Crear `client/CatalogsClient.java` (interfaz) + `CatalogsClientImpl.java` — llamadas HTTP a `/v1/subscribers`, `/v1/agents`, `/v1/business-lines`
- [ ] Crear `service/CatalogsService.java` (interfaz) + `CatalogsServiceImpl.java` — orquesta cliente + mapeo + retry + manejo de errores
- [ ] Crear `controller/CatalogsController.java` — `GET /api/v1/catalogs/subscribers`, `GET /api/v1/catalogs/agents`, `GET /api/v1/catalogs/business-lines`
- [ ] Agregar `PLATAFORMA_CORE_OHS_URL` a `application.yml` y `.env.example`
- [ ] Registrar `CatalogsController` en el contexto de Spring (autowired vía `@RestController`)

#### Tests Backend

- [ ] `test_get_subscribers_returns_200_with_list` — happy path, lista no vacía
- [ ] `test_get_subscribers_empty_list_returns_200` — lista vacía es respuesta válida
- [ ] `test_get_subscribers_service_unavailable_returns_503` — fallo del servicio externo tras reintentos
- [ ] `test_get_agents_returns_200_with_list` — happy path agentes
- [ ] `test_get_agents_service_unavailable_returns_503` — fallo agentes
- [ ] `test_get_business_lines_returns_200_with_list` — happy path giros
- [ ] `test_get_business_lines_service_unavailable_returns_503` — fallo giros
- [ ] `test_retry_succeeds_on_second_attempt` — reintento exitoso en segundo intento
- [ ] `test_retry_exhausted_throws_service_exception` — reintentos agotados → excepción
- [ ] `test_4xx_error_not_retried` — HTTP 400/404 NO activan reintentos
- [ ] `test_mapping_drops_invalid_record_missing_id` — registro sin `id` se descarta (log WARNING)
- [ ] `test_catalogs_service_impl_maps_all_fields` — transformación completa de campos

### Frontend

No aplica a esta spec.

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 al 6.3
- [ ] Ejecutar skill `/risk-identifier` → clasificar riesgo de dependencia crítica con servicio externo
- [ ] Verificar cobertura de tests ≥ 80% en `service/`, `client/`, `controller/`
- [ ] Prueba de integración manual: levantar `plataforma-core-ohs` (mock) y hacer GET /api/v1/catalogs/subscribers → respuesta válida
- [ ] Prueba de resiliencia manual: apagar mock → llamar endpoint → verificar 503 + log CRITICAL
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
