---
name: SPEC-010 test suite — ParametroCalculo
description: Tests unitarios y MockMvc generados para la feature ep-002-ft-010-core-parametros-calculo (HU-044 a HU-048)
type: project
---

Tests generados en 2026-04-29 para SPEC-010.

**Archivos creados:**
- `plataformas-danos-back/src/test/java/com/plataformas_danos_back/service/ParametroCalculoServiceImplTest.java` — 9 tests unitarios con MockitoExtension
- `plataformas-danos-back/src/test/java/com/plataformas_danos_back/service/IngestorParametrosServiceImplTest.java` — 8 tests unitarios con MockitoExtension
- `plataformas-danos-back/src/test/java/com/plataformas_danos_back/controller/ParametroCalculoControllerMockMvcTest.java` — 9 tests MockMvc standaloneSetup

**Caveats de implementación:**
- `IngestorParametrosServiceImpl` usa `AtomicBoolean` como campos `final` inicializados en el constructor — para forzar estado "en progreso" en tests se debe usar `ReflectionTestUtils.setField(service, "ingestIncendioEnProgreso", new AtomicBoolean(true))`
- El filtro por `?zona=` en `/tarifas-cat` lo aplica el controller (no el service) — los mocks del service retornan la lista completa y el test verifica el filtrado en la respuesta HTTP
- `TarifaFHMRepository.save()` se usa (no `saveAll`) para la ingestión FHM — confirmado en la implementación
- Dos items del spec quedan sin cubrir: `test_validadorVigencias_valida_fechasInconsistentes_rechaza` (BR-003, no hay clase ValidadorVigencias en implementación actual) y `test_parametrosCacheService_refresca_...` (requiere contexto Spring para probar @CacheEvict) y `test_parametroController_postCargarTarifasIncendio_returns401_noAuth` (requiere Spring Security activo)

**Why:** Tests generados como parte de la tarea QA de SPEC-010 antes de marcar la feature como IMPLEMENTED.
**How to apply:** Si se extiende SPEC-010 con tests de integración (Testcontainers), ver la lista pendiente en el spec líneas 762-770.
