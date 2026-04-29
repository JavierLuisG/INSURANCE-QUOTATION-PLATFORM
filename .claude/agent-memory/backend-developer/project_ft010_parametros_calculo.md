---
name: FT-010 — Parámetros de Cálculo implementados
description: Capa de ingestión y consulta de parámetros técnicos en plataformas-danos-back; 4 entidades, 4 repos, 7 DTOs, 2 servicios, 1 controller
type: project
---

SPEC-010 (EP-002 FT-010) implementada en IN_PROGRESS. Módulo de parámetros de cálculo completo en `plataformas-danos-back`.

**Why:** El motor de cálculo (FT-012) y el motor de validación de reglas (FT-011) necesitan acceso centralizado a tarifas y factores técnicos con caché y vigencias.

**How to apply:** Consultar estos artefactos si FT-011 o FT-012 requieren datos de tarifas; todos exponen datos via `ParametroCalculoService`.

## Artefactos creados

### Entidades (`model/entity/`)
- `TarifaIncendio.java` — colección `tarifas_incendio`, CompoundIndex (zonaGeografica, tipoInmueble, fechaVigenciaInicio)
- `TarifaCAT.java` — colección `tarifas_cat`, CompoundIndex (zonaCAT, fechaVigenciaInicio)
- `TarifaFHM.java` — colección `tarifas_fhm`
- `CatalogoCPZonas.java` — colección `catalogo_cp_zonas`, @Indexed unique codigoPostal, @Indexed zonaCAT

### Repositorios (`repository/`)
- `TarifaIncendioRepository` — findByZonaGeograficaAndTipoInmueble, findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull
- `TarifaCATRepository` — findByZonaCAT, findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull
- `TarifaFHMRepository` — findFirstByOrderByCreatedAtDesc
- `CatalogoCPZonasRepository` — findByCodigoPostal, findByZonaCAT

### DTOs (`model/dto/`)
- `CargarTarifasRequest` — campo opcional `origenForzado`
- `IngestStatusResponse` — requestId, status, mensaje, timestamp
- `TarifaIncendioResponse`, `TarifaCATResponse`, `TarifaFHMResponse`, `CatalogoCPZonasResponse`
- `ParametrosStatusResponse` — status anidado con inner classes TarifasStatus y TarifaFHMStatus

### Excepciones (`exception/`)
- `ParametroNoDisponibleException` → HTTP 503, code="PARAMETRO_NO_DISPONIBLE"
- `IngestEnProgresoException` → HTTP 409, code="INGEST_EN_PROGRESO"

### Servicios (`service/`)
- `ParametroCalculoService` + `ParametroCalculoServiceImpl` — @Cacheable en los 4 métodos de consulta
- `IngestorParametrosService` + `IngestorParametrosServiceImpl` — AtomicBoolean por tipo, @CacheEvict, delega a TariffsService/ZipCodeService, modo SIMULACION hardcodeado

### Controlador (`controller/`)
- `ParametroCalculoController` — 4 POST /load + 5 GET bajo `/api/v1/parameters`

## Caches registrados
- `parameters-tarifas-incendio` (max 1000, TTL 24h)
- `parameters-tarifas-cat` (max 500, TTL 24h)
- `parameters-tarifas-fhm` (max 10, TTL 24h)
- `parameters-cp-zonas` (max 100000, TTL 24h)

## Decisiones de diseño
- La ingestión de datos externos se delega a `TariffsService` (fire, cat, electronicEquipment) y `ZipCodeService` ya existentes — no hay llamadas HTTP directas en el ingestor
- `origenForzado=SIMULACION` activa datos hardcodeados; cualquier otro valor usa el servicio externo
- La validación de zonaCAT para tarifas CAT consulta `CatalogoCPZonasRepository.findByZonaCAT` — si la colección está vacía (primera carga), las CAT desde servicio externo son descartadas; cargar CP-Zonas primero
