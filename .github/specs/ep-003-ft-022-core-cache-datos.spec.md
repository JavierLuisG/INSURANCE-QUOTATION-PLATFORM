---
id: SPEC-008
status: APPROVED
feature: ep-003-ft-022-core-cache-datos
created: 2026-04-28
updated: 2026-04-28
author: spec-generator
version: "1.1"
related-specs:
  - SPEC-001
  - SPEC-003
  - SPEC-004
  - SPEC-005
  - SPEC-006
  - SPEC-007
---

# Spec: FT-022 — Gestión de Caché y Estrategia de Actualización de Datos Maestros

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa una capa de caché Caffeine para datos maestros frecuentemente consultados (catálogos, tarifas, factores técnicos y reglas de validación) en `plataformas-danos-back`, junto con estrategias de invalidación por TTL, actualización programada mediante scheduler, invalidación bajo demanda y observabilidad del estado del caché via Micrometer/Prometheus. El objetivo es reducir la latencia de respuesta, disminuir la carga sobre `plataforma-core-ohs` y MongoDB, y mantener un equilibrio entre rendimiento y consistencia.

### Requerimiento de Negocio

El sistema de cotización realiza consultas repetidas a datos maestros (catálogos, tarifas, reglas) en cada operación de cotización, generando latencia acumulada y carga innecesaria en servicios dependientes. La dependencia directa de `plataforma-core-ohs` en cada request aumenta el riesgo ante indisponibilidad transitoria. Caffeine y `spring-boot-starter-cache` están declarados en `pom.xml` pero nunca fueron activados. Se requiere activar y configurar la caché con TTLs diferenciados por tipo de dato, un scheduler que refresque datos proactivamente, mecanismos de invalidación bajo demanda, y visibilidad operacional del estado del caché.

### Historias de Usuario

#### HU-105: Almacenar Datos Maestros en Caché

```
Como:        Sistema (plataformas-danos-back)
Quiero:      Almacenar los datos maestros clave (catálogos, tarifas) en caché
Para:        Optimizar el acceso, reducir los tiempos de respuesta y disminuir
             la carga sobre los servicios externos

Prioridad:   Alta
Estimación:  S (4 story points)
Dependencias: SPEC-001, SPEC-003, SPEC-004, SPEC-005, SPEC-006 (FT-015 a FT-018)
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-105

**Happy Path**

```gherkin
CRITERIO-105.1: Almacenamiento inicial en caché (cache miss → external call → store)
  Dado que:  un catálogo específico no está presente en la caché
  Cuando:    el sistema solicita el catálogo
  Entonces:  el sistema obtiene el catálogo del servicio externo
             y el sistema guarda el catálogo en caché con un TTL configurado

CRITERIO-105.2: Recuperación desde caché (cache hit → no external call)
  Dado que:  un catálogo ya está presente en la caché y no ha caducado
  Cuando:    el sistema solicita el mismo catálogo
  Entonces:  el sistema recupera el catálogo directamente de la caché
             y el sistema NO realiza una llamada al servicio externo

CRITERIO-105.3: Tiempo de respuesta optimizado
  Dado que:  un catálogo está presente en la caché
  Cuando:    el sistema consulta el catálogo
  Entonces:  el tiempo de respuesta es significativamente menor que una consulta directa
             al servicio externo (objetivo: < 5 ms latencia de caché)
```

**Error Path**

```gherkin
CRITERIO-105.4: Error del servicio externo no contamina la caché
  Dado que:  el servicio externo devuelve un error o no está disponible
  Cuando:    el sistema intenta obtener el catálogo (cache miss)
  Entonces:  el sistema registra el error
             y NO almacena datos inválidos o nulos en caché
             y devuelve un error apropiado al solicitante
```

**Edge Case**

```gherkin
CRITERIO-105.5: Fallo en el mecanismo de caché → fallback a servicio externo
  Dado que:  el mecanismo de caché falla al intentar almacenar o recuperar datos
  Cuando:    el sistema consulta el catálogo
  Entonces:  el sistema obtiene los datos del servicio externo (fallback)
             y registra el fallo del mecanismo de caché

CRITERIO-105.6: Acción de limpieza de caché registra auditoría
  Dado que:  un usuario con rol de administrador invoca la operación de limpieza de caché
  Cuando:    la caché de datos maestros es vaciada
  Entonces:  el sistema registra un evento de auditoría para la acción de limpieza
```

---

#### HU-106: Configurar Política de Invalidación por TTL

```
Como:        Sistema (plataformas-danos-back)
Quiero:      Configurar una política de invalidación de caché basada en TTL diferenciado
             por tipo de dato maestro
Para:        Asegurar que los datos en caché estén frescos y consistentes sin sobrecargar
             la fuente original, aplicando TTLs apropiados a cada tipo de catálogo

Prioridad:   Alta
Estimación:  XS (3 story points)
Dependencias: HU-105
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-106

**Happy Path**

```gherkin
CRITERIO-106.1: Asignación de TTL al guardar datos en caché
  Dado que:  un sistema con política de caché por TTL configurada para un catálogo
  Cuando:    el sistema guarda el dato maestro en caché
  Entonces:  se le asigna un TTL configurable al dato en caché

CRITERIO-106.2: Invalidación por expiración de TTL
  Dado que:  un dato maestro en caché con TTL expirado
  Cuando:    el sistema consulta ese dato
  Entonces:  el dato se considera inválido
             y el sistema consulta la fuente original para obtener el dato actualizado

CRITERIO-106.3: TTLs independientes por tipo de catálogo
  Dado que:  una configuración de TTL para catálogos estáticos de 12-24 horas
             y una configuración de TTL para tarifas/factores de 1-6 horas
  Cuando:    el sistema aplica estas configuraciones
  Entonces:  cada tipo de catálogo tiene su propia política de frescura independiente
```

**Edge Case**

```gherkin
CRITERIO-106.4: TTL=0 implica no cachear
  Dado que:  una política de TTL para un catálogo configurada a 0 segundos
  Cuando:    el sistema consulta ese catálogo
  Entonces:  el dato no se guarda en caché
             y el sistema siempre consulta la fuente original

CRITERIO-106.5: Fuente original no disponible al expirar TTL
  Dado que:  un dato en caché con TTL expirado
             y la fuente original no está disponible
  Cuando:    el sistema intenta consultar la fuente original
  Entonces:  el sistema maneja la indisponibilidad apropiadamente
             y puede devolver el dato obsoleto con advertencia o lanzar excepción
             según la política de tolerancia a fallos configurada
```

---

#### HU-107: Actualización Programada de Caché (Scheduler)

```
Como:        Sistema (plataformas-danos-back)
Quiero:      Implementar un mecanismo de actualización programada de la caché
             para datos maestros clave
Para:        Refrescar los datos periódicamente y asegurar su disponibilidad y frescura
             de forma proactiva, reduciendo los cache misses durante horas de alta demanda

Prioridad:   Media
Estimación:  S (4 story points)
Dependencias: HU-105, HU-106
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-107

**Happy Path**

```gherkin
CRITERIO-107.1: Inicio de actualización programada según configuración
  Dado que:  se ha configurado una tarea programada para la actualización de catálogos
  Cuando:    llega el momento de ejecución de la tarea programada (ej. cada 6 horas)
  Entonces:  el sistema inicia el proceso de actualización de los catálogos en caché

CRITERIO-107.2: Refresco de datos desde servicios externos
  Dado que:  la actualización programada se ha iniciado
  Cuando:    el sistema consulta los servicios externos para obtener datos maestros
  Entonces:  los datos en caché son refrescados con la información más reciente
             y el estado del proceso pasa a SUCCESS
             y se genera un log de finalización exitosa con detalles

CRITERIO-107.3: Notificación de fallo en actualización programada
  Dado que:  la actualización programada se está ejecutando
  Cuando:    ocurre un error durante la consulta a servicios externos o el refresco
  Entonces:  el sistema registra el error en los logs (nivel ERROR)
             y el estado del proceso pasa a FAILED
             y el sistema notifica a los administradores sobre el fallo
```

**Error Path**

```gherkin
CRITERIO-107.4: Servicios externos no disponibles durante actualización programada
  Dado que:  la actualización programada se ha iniciado
             y los servicios externos no están disponibles
  Cuando:    el sistema intenta consultar los datos
  Entonces:  el sistema registra el error de conexión
             y la caché NO se actualiza con nuevos datos (retiene los existentes)
             y el sistema notifica a los administradores

CRITERIO-107.5: Fallo al escribir en caché
  Dado que:  la actualización programada ha obtenido nuevos datos
             y ocurre un problema al intentar escribir en caché
  Cuando:    el sistema intenta refrescar los datos en caché
  Entonces:  el sistema registra el error de escritura
             y la caché retiene los datos anteriores
             y el sistema notifica a los administradores
```

---

#### HU-108: Invalidación de Caché Bajo Demanda

```
Como:        Sistema / Administrador
Quiero:      Poder invalidar la caché bajo demanda (por evento de actualización de
             plataforma-core-ohs o por acción manual de un administrador)
Para:        Reflejar cambios urgentes en los datos maestros de forma inmediata,
             asegurando la consistencia sin esperar el TTL

Prioridad:   Media
Estimación:  S (4 story points)
Dependencias: HU-105
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-108

**Happy Path**

```gherkin
CRITERIO-108.1: Invalidación manual de catálogo específico por administrador
  Dado que:  un administrador tiene permisos para invalidar la caché
             y la caché del catálogo solicitado es válida
  Cuando:    el administrador invoca DELETE /api/v1/cache/{cacheName} vía API interna
  Entonces:  la caché del catálogo indicado es invalidada (vaciada)
             y se registra un evento de auditoría con usuario, clave y timestamp
             y las siguientes consultas a ese catálogo irán a la fuente original

CRITERIO-108.2: Invalidación de todas las cachés
  Dado que:  un administrador invoca DELETE /api/v1/cache
  Cuando:    la operación se completa exitosamente
  Entonces:  todas las cachés registradas quedan vacías
             y se registra un evento de auditoría

CRITERIO-108.3: Comportamiento post-invalidación
  Dado que:  la caché de un catálogo ha sido invalidada
  Cuando:    se realiza una consulta al catálogo
  Entonces:  el sistema consulta la fuente original
             y el catálogo se vuelve a cachear con la información actualizada
```

**Error Path**

```gherkin
CRITERIO-108.4: Invalidación de caché con nombre inexistente → 404
  Dado que:  el administrador invoca DELETE /api/v1/cache/cache-inexistente
  Cuando:    el sistema procesa la solicitud
  Entonces:  la respuesta HTTP es 404
             y el body es { "message": "Caché 'cache-inexistente' no encontrada", "code": "CACHE_NOT_FOUND" }

CRITERIO-108.5: Endpoints de caché sin JWT → 401
  Dado que:  la petición no incluye Authorization header
  Cuando:    cualquier endpoint de /api/v1/cache es invocado
  Entonces:  la respuesta HTTP es 401
```

**Edge Case**

```gherkin
CRITERIO-108.6: Invalidación de clave inexistente no genera error
  Dado que:  se intenta invalidar una entrada de caché que no existe
  Cuando:    el sistema procesa la operación de invalidación
  Entonces:  la operación se completa sin error
             y se registra un log informativo indicando que la clave no existía
```

---

#### HU-109: Monitoreo de Rendimiento y Consistencia del Caché

```
Como:        Desarrollador
Quiero:      Monitorear el rendimiento del caché y la consistencia de los datos
Para:        Asegurar que cumple con los SLAs de tiempo de respuesta y optimizar
             su configuración mediante métricas observables

Prioridad:   Media
Estimación:  XS (3 story points)
Dependencias: HU-105, HU-106, HU-107, HU-108
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-109

**Happy Path**

```gherkin
CRITERIO-109.1: Métricas de caché accesibles via GET /api/v1/cache/stats
  Dado que:  el sistema de caché está activo y en uso
  Cuando:    se invoca GET /api/v1/cache/stats con JWT válido
  Entonces:  la respuesta HTTP es 200
             y el body contiene para cada caché: nombre, estimatedSize, hitCount,
             missCount y ttlSeconds configurado

CRITERIO-109.2: Cache hit ratio superior al 80% en uso normal
  Dado que:  el sistema lleva operando con catálogos cargados
  Cuando:    se consultan métricas de caché después de múltiples requests
  Entonces:  el hit ratio supera el 80% para catálogos estáticos

CRITERIO-109.3: Latencia de recuperación desde caché menor a 5 ms
  Dado que:  un catálogo está presente en la caché
  Cuando:    se realiza una consulta
  Entonces:  el tiempo de respuesta para esa consulta es menor a 5 ms
```

**Edge Case**

```gherkin
CRITERIO-109.4: Monitoreo con caché vacía no genera errores
  Dado que:  el sistema acaba de arrancar y las cachés están vacías
  Cuando:    se consultan las métricas de caché
  Entonces:  la respuesta muestra métricas en cero sin errores
             y no se generan falsas alertas
```

### Reglas de Negocio

1. **TTLs por tipo de dato** (valores por defecto, todos configurables via `application.yaml`):
   - Catálogos estáticos (subscribers, agents, businessLines, riskClassifications, guarantees): **12 horas**
   - Tarifas y factores (tariffsFire, tariffsElectronicEquipment): **6 horas**
   - Búsquedas por clave (tariffCat por zona, zipCode por CP): **1 hora**
   - Reglas de validación y corrección (ValidationRule, CorrectionRule): **1 hora**

2. Las excepciones nunca se almacenan en caché — solo resultados exitosos.

3. Cada caché tiene `maximumSize` configurado con política de desalojo **LRU** (estrategia nativa de Caffeine).

4. El scheduler de actualización se ejecuta por defecto cada **6 horas** (cron configurable).

5. La invalidación es **granular por caché nombrada** — no invalida toda la caché global.

6. Los endpoints de gestión de caché (`/api/v1/cache/**`) requieren JWT válido.

7. Toda operación de invalidación manual genera un log de auditoría (nivel INFO estructurado con `correlation-id`).

8. `recordStats()` en el builder de Caffeine debe estar habilitado para exponer métricas de hit/miss.

9. Si el servicio externo falla durante un cache miss, Resilience4j Retry (ya configurado) aplica antes de devolver error.

10. El scheduler no debe ejecutarse durante ventanas de alta carga — configurable por cron expression.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas (sin cambios en MongoDB)

| Capa | Archivo | Cambios | Descripción |
|------|---------|---------|-------------|
| `config/` | `CacheConfig.java` | nuevo | `@EnableCaching` + `CaffeineCacheManager` con 11 cachés nombradas y TTLs individuales |
| `config/` | `CacheProperties.java` | nuevo | `@ConfigurationProperties(prefix = "cache")` con TTLs configurables |
| `service/` | `CatalogsServiceImpl` | modificado | `@Cacheable` en 5 métodos de lectura |
| `service/` | `TariffsServiceImpl` | modificado | `@Cacheable` en 3 métodos de lectura |
| `service/` | `ZipCodeServiceImpl` | modificado | `@Cacheable` en `getByZipCode` con clave por CP |
| `service/` | `DataValidationEngineImpl` | modificado | `@Cacheable` en `getRulesForDataType` con clave por dataType |
| `service/` | `DataCorrectionServiceImpl` | modificado | `@Cacheable` en consulta de reglas con clave compuesta |
| `service/` | `CacheService.java` + `CacheServiceImpl.java` | nuevo | Operaciones de stats e invalidación sobre `CacheManager` |
| `service/` | `CacheRefreshService.java` | nuevo | Scheduler `@Scheduled` que refresca datos proactivamente |
| `controller/` | `CacheController.java` | nuevo | Endpoints de gestión de caché |
| `exception/` | `CacheNotFoundException.java` | nuevo | Excepción para caché con nombre no encontrado |
| `application.yaml` | — | modificado | Configuración de TTLs por caché nombrada |

#### Registro de cachés

| Nombre de Caché | Servicio | Clave | TTL por defecto | Max Entries |
|-----------------|----------|-------|-----------------|-------------|
| `catalogs-subscribers` | `CatalogsServiceImpl` | `'all'` | 12 h | 10 |
| `catalogs-agents` | `CatalogsServiceImpl` | `'all'` | 12 h | 10 |
| `catalogs-business-lines` | `CatalogsServiceImpl` | `'all'` | 12 h | 10 |
| `catalogs-risk-classifications` | `CatalogsServiceImpl` | `'all'` | 12 h | 10 |
| `catalogs-guarantees` | `CatalogsServiceImpl` | `'all'` | 12 h | 10 |
| `tariffs-fire` | `TariffsServiceImpl` | `'all'` | 6 h | 10 |
| `tariffs-electronic-equipment` | `TariffsServiceImpl` | `'all'` | 6 h | 10 |
| `tariffs-cat` | `TariffsServiceImpl` | `#zona` | 1 h | 100 |
| `zip-codes` | `ZipCodeServiceImpl` | `#zipCode` | 1 h | 500 |
| `validation-rules` | `DataValidationEngineImpl` | `#dataType` | 1 h | 50 |
| `correction-rules` | `DataCorrectionServiceImpl` | `#dataType + ':' + #fieldName` | 1 h | 200 |

### API Endpoints

#### GET /api/v1/cache/stats

- **Descripción**: Retorna métricas de las cachés activas (HU-109)
- **Auth requerida**: sí (JWT válido)
- **Response 200**:
  ```json
  [
    {
      "name": "catalogs-subscribers",
      "estimatedSize": 1,
      "hitCount": 245,
      "missCount": 3,
      "ttlSeconds": 43200
    },
    {
      "name": "tariffs-cat",
      "estimatedSize": 5,
      "hitCount": 1820,
      "missCount": 5,
      "ttlSeconds": 3600
    }
  ]
  ```
- **Response 401**: token ausente o expirado

#### DELETE /api/v1/cache/{cacheName}

- **Descripción**: Evacua todas las entradas de la caché nombrada (HU-108)
- **Auth requerida**: sí (JWT válido)
- **Path param**: `cacheName` — nombre exacto registrado (ej. `catalogs-subscribers`)
- **Response 204**: caché evacuada exitosamente
- **Response 401**: token ausente o expirado
- **Response 404**: caché no registrada
  ```json
  { "message": "Caché 'nombre' no encontrada", "code": "CACHE_NOT_FOUND" }
  ```

#### DELETE /api/v1/cache

- **Descripción**: Evacua todas las cachés registradas (HU-108)
- **Auth requerida**: sí (JWT válido)
- **Response 204**: todas las cachés evacuadas
- **Response 401**: token ausente o expirado

### Diseño Frontend

No aplica — feature exclusivamente backend.

### Arquitectura y Dependencias

- **Dependencias nuevas en `pom.xml`**: ninguna. `spring-boot-starter-cache`, `caffeine` y `spring-boot-starter-actuator` ya están disponibles en el stack aprobado. Verificar si `spring-boot-starter-actuator` está declarado; si no, agregarlo para la exposición de métricas Micrometer.
- **`@EnableCaching`**: agregar en `CacheConfig.java` (`@Configuration` + `@EnableCaching`).
- **`@EnableScheduling`**: agregar en `PlataformasDanosBackApplication.java` o en `CacheConfig.java` para habilitar el scheduler de HU-107.
- **`CacheConfig.java`** (nuevo): define `CaffeineCacheManager` con cachés nombradas usando `Caffeine.newBuilder().maximumSize(N).expireAfterWrite(Duration).recordStats()`.
- **`CacheProperties.java`** (nuevo): `@ConfigurationProperties(prefix = "cache.ttl")` — permite sobreescribir TTLs por entorno sin recompilar.
- **`CacheRefreshService.java`** (nuevo): `@Service` con métodos `@Scheduled(cron = "${cache.refresh.cron:0 0 */6 * * *}")` que llaman directamente a los clientes HTTP para refrescar cada caché estática.
- **`CacheController.java`** (nuevo): expone `/api/v1/cache/**`.
- **`application.yaml`**: agregar sección `cache.ttl.*` y `cache.refresh.cron`.
- **Impacto en `application.yaml`**: la spec global `spring.cache.caffeine.spec` queda supersedida por la configuración programática de `CacheConfig`. Se puede eliminar o dejar como fallback.

### Notas de Implementación

> **`@Cacheable` + `@Retry` (Resilience4j):** El proxy de caché se aplica antes del retry. Si el resultado está en caché, el retry nunca se activa. Si el método lanza excepción, el retry reintenta — pero la excepción no se cachea. Comportamiento correcto y deseado.

> **Scheduler y horarios de carga:** Configurar cron por defecto a madrugada/baja demanda. La expresión cron `0 0 */6 * * *` ejecuta cada 6 horas (00:00, 06:00, 12:00, 18:00). Sobreescribible via env var `CACHE_REFRESH_CRON`.

> **recordStats():** Habilitar en cada `Caffeine.newBuilder()` para que `CacheStats.hitCount()` y `missCount()` estén disponibles al construir la respuesta de `/stats`. Sin `recordStats()`, ambos valores serán siempre 0.

> **Cache stampede:** Caffeine maneja thundering herd nativamente mediante `refreshAfterWrite` (alternativa a `expireAfterWrite`). Si el volumen de requests concurrentes lo justifica, usar `refreshAfterWrite` + `expireAfterWrite` combinados para que el refresh ocurra en background antes de la expiración. Primera versión puede usar solo `expireAfterWrite`.

> **`CacheRefreshService` (HU-107):** El servicio llama directamente a los clientes HTTP (no a los servicios cacheados) para evitar que `@Cacheable` intercepte la llamada. Debe usar `@CacheEvict` + `@CachePut` o llamar al servicio con `sync = true`. Alternativa recomendada: usar `@CacheEvict(allEntries = true)` antes y dejar que el primer request post-eviction recargue la caché.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación (HU-105 + HU-106)

- [ ] Crear `config/CacheProperties.java` — `@ConfigurationProperties(prefix = "cache.ttl")` con campos configurables por caché
- [ ] Crear `config/CacheConfig.java` — `@Configuration` + `@EnableCaching` + `CaffeineCacheManager` con 11 cachés nombradas, TTLs individuales y `recordStats()`
- [ ] Agregar `@Cacheable(value = "catalogs-subscribers", key = "'all'")` en `CatalogsServiceImpl.getSubscribers()`
- [ ] Agregar `@Cacheable(value = "catalogs-agents", key = "'all'")` en `CatalogsServiceImpl.getAgents()`
- [ ] Agregar `@Cacheable(value = "catalogs-business-lines", key = "'all'")` en `CatalogsServiceImpl.getBusinessLines()`
- [ ] Agregar `@Cacheable(value = "catalogs-risk-classifications", key = "'all'")` en `CatalogsServiceImpl.getRiskClassifications()`
- [ ] Agregar `@Cacheable(value = "catalogs-guarantees", key = "'all'")` en `CatalogsServiceImpl.getGuarantees()`
- [ ] Agregar `@Cacheable(value = "tariffs-fire", key = "'all'")` en `TariffsServiceImpl.getTariffsFire()`
- [ ] Agregar `@Cacheable(value = "tariffs-cat", key = "#zona")` en `TariffsServiceImpl.getTariffCat(String zona)`
- [ ] Agregar `@Cacheable(value = "tariffs-electronic-equipment", key = "'all'")` en `TariffsServiceImpl.getTariffsElectronicEquipment()`
- [ ] Agregar `@Cacheable(value = "zip-codes", key = "#zipCode")` en `ZipCodeServiceImpl.getByZipCode(String zipCode)`
- [ ] Agregar `@Cacheable(value = "validation-rules", key = "#dataType")` en `DataValidationEngineImpl.getRulesForDataType(String dataType)`
- [ ] Agregar `@Cacheable(value = "correction-rules", key = "#dataType + ':' + #fieldName")` en `DataCorrectionServiceImpl` para consultas de reglas
- [ ] Actualizar `application.yaml` — agregar sección `cache.ttl.*` con TTLs por caché y `cache.refresh.cron`

#### Implementación (HU-107 — Scheduler)

- [ ] Agregar `@EnableScheduling` en `PlataformasDanosBackApplication.java` o `CacheConfig.java`
- [ ] Crear `service/CacheRefreshService.java` — `@Scheduled(cron = "${cache.refresh.cron:0 0 */6 * * *}")` que refresca catálogos estáticos via `@CacheEvict` + llamada a clientes HTTP; maneja excepciones sin propagar; notifica en fallo (log ERROR)
- [ ] Registrar en logs el estado final de cada ejecución (SUCCESS / FAILED) con detalles

#### Implementación (HU-108 — Invalidación bajo demanda)

- [ ] Crear `service/CacheService.java` (interfaz) — métodos: `getStats()`, `evict(String cacheName)`, `evictAll()`
- [ ] Crear `service/CacheServiceImpl.java` — implementación usando `CacheManager`; lanzar `CacheNotFoundException` si el nombre no existe; registrar auditoría en cada evicción
- [ ] Crear `exception/CacheNotFoundException.java` — extiende `RuntimeException`
- [ ] Crear `controller/CacheController.java` — endpoints: `GET /api/v1/cache/stats`, `DELETE /api/v1/cache/{cacheName}`, `DELETE /api/v1/cache`
- [ ] Registrar `CacheNotFoundException` en `GlobalExceptionHandler` — mapear a 404 con `{ "message": "...", "code": "CACHE_NOT_FOUND" }`

#### Implementación (HU-109 — Monitoreo)

- [ ] Verificar/agregar `spring-boot-starter-actuator` en `pom.xml` si no está presente
- [ ] Habilitar `recordStats()` en cada `Caffeine.newBuilder()` dentro de `CacheConfig`
- [ ] Implementar `CacheService.getStats()` que retorna `List<CacheStatsResponse>` con `name`, `estimatedSize`, `hitCount`, `missCount`, `ttlSeconds`
- [ ] Crear `model/dto/CacheStatsResponse.java` — DTO con los campos de métricas

#### Tests Backend

- [ ] `test_getSubscribers_secondCall_doesNotInvokeCatalogsClient` — cache hit, sin llamada HTTP
- [ ] `test_getAgents_secondCall_doesNotInvokeClient`
- [ ] `test_getBusinessLines_secondCall_doesNotInvokeClient`
- [ ] `test_getRiskClassifications_secondCall_doesNotInvokeClient`
- [ ] `test_getGuarantees_secondCall_doesNotInvokeClient`
- [ ] `test_getTariffCat_sameZona_secondCall_doesNotInvokeClient`
- [ ] `test_getTariffCat_differentZona_callsClientEachTime` — claves distintas no comparten entrada
- [ ] `test_getTariffsFire_secondCall_doesNotInvokeClient`
- [ ] `test_getTariffsElectronicEquipment_secondCall_doesNotInvokeClient`
- [ ] `test_getByZipCode_sameZip_secondCall_doesNotInvokeClient`
- [ ] `test_getRulesForDataType_sameDataType_secondCall_doesNotInvokeRepository`
- [ ] `test_cacheRefreshService_scheduledTask_evictsAndReloadsSubscribers` — `CacheRefreshServiceTest`
- [ ] `test_cacheRefreshService_externalServiceFails_doesNotPropagate` — `CacheRefreshServiceTest`
- [ ] `test_cacheService_getStats_returnsAllCacheNames` — `CacheServiceImplTest`
- [ ] `test_cacheService_evict_knownCache_clearsEntries` — `CacheServiceImplTest`
- [ ] `test_cacheService_evict_unknownCache_throwsCacheNotFoundException` — `CacheServiceImplTest`
- [ ] `test_cacheService_evictAll_clearsAllCaches` — `CacheServiceImplTest`
- [ ] `test_cacheController_getStats_returns200` — `CacheControllerTest`
- [ ] `test_cacheController_evictByName_returns204` — `CacheControllerTest`
- [ ] `test_cacheController_evictByName_notFound_returns404` — `CacheControllerTest`
- [ ] `test_cacheController_evictAll_returns204` — `CacheControllerTest`
- [ ] `test_cacheController_noJwt_returns401` — `CacheControllerTest`

### Frontend

No aplica — feature exclusivamente backend.

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-105.1 a 109.4
- [ ] Ejecutar skill `/risk-identifier` → clasificar riesgo de stale data, cache stampede, scheduler failure
- [ ] Verificar que todos los tests de caché pasan con `mvn test`
- [ ] Verificar cobertura JaCoCo ≥ 80% global tras los cambios
- [ ] Probar manualmente: segunda llamada a `GET /api/v1/catalogs/subscribers` no llama a plataforma-core-ohs (verificar en logs)
- [ ] Probar manualmente: `DELETE /api/v1/cache/catalogs-subscribers` + siguiente GET recarga desde origen
- [ ] Verificar que `/api/v1/cache/stats` retorna hitCount > 0 después de requests
- [ ] Verificar que el scheduler refresca al menos un catálogo (activar manualmente via endpoint de evicción + esperar siguiente cron)
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
