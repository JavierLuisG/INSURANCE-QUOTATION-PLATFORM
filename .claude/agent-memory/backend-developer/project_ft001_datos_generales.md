---
name: FT-001 — Datos Generales del Cotizador implementados
description: Backend EP-001 FT-001: entidad Cotizacion, 2 DTOs, repo, validator RFC, 2 clientes HTTP, service+impl, controller, security JWT — spec IN_PROGRESS
type: project
---

El módulo de Datos Generales del Cotizador fue implementado completamente en el backend Spring Boot.

**Why:** Feature central del cotizador — permite crear, cargar y editar cotizaciones con folio único. Implementado 2026-04-29.

**How to apply:** Al trabajar en features que dependan de cotizaciones (cálculo de primas, coberturas, etc.), la entidad base ya existe en `model/entity/Cotizacion.java` y los endpoints en `POST/GET/PUT /api/v1/cotizaciones`.

Artefactos creados:
- `model/entity/Cotizacion.java` — colección `cotizaciones`, @Version, @Indexed(unique) en folio
- `model/dto/CotizacionRequest.java` — campos opcionales + version Long
- `model/dto/CotizacionResponse.java` — sin _id, todos los campos públicos
- `repository/CotizacionRepository.java` — findByFolio(String)
- `validator/RfcValidator.java` — regex SAT personas físicas (13) y morales (12)
- `client/FoliosClient.java` + `FoliosClientImpl.java` — GET /v1/folios con @CircuitBreaker/@Retry foliosCircuitBreaker/foliosRetry
- `client/CotizadorCatalogosClient.java` + `CotizadorCatalogosClientImpl.java` — GET /v1/catalogos/{tipo}/{id}
- `service/CotizacionService.java` (interfaz) + `CotizacionServiceImpl.java`
- `controller/CotizacionController.java` — POST 201, GET 200, PUT 200 con @PreAuthorize por rol
- `security/JwtAuthenticationFilter.java` — extrae roles del claim "roles" del JWT
- `config/SecurityConfig.java` — actualizado con @EnableMethodSecurity + JWT filter
- `config/JwtFilterConfig.java` — desregistra JwtAuthenticationFilter del servlet chain auto-registro
- `exception/CotizacionNotFoundException.java`, `CotizacionConflictException.java`, `FolioServiceUnavailableException.java`
- `GlobalExceptionHandler.java` — actualizado con handlers 404, 409, 503, 401, 403
- `application.yaml` — añadidos foliosCircuitBreaker y foliosRetry en resilience4j

Nota: El servicio mock de plataforma-core-ohs expone GET /v1/folios (no POST).
Los roles JWT se leen del claim "roles" como lista de strings; Spring Security los prefija con ROLE_.
