---
name: SPEC-011 test suite — Cotizacion (EP-001 FT-001 Datos Generales)
description: Tests unitarios y MockMvc para HU-001 a HU-005; caveats de seguridad standalone y versionado optimista
type: project
---

Tests generados para SPEC-011 (18 en total, todos marcados [x] en el spec).

**Archivos creados:**
- `plataformas-danos-back/src/test/java/com/plataformas_danos_back/service/CotizacionServiceImplTest.java` — 6 tests con MockitoExtension
- `plataformas-danos-back/src/test/java/com/plataformas_danos_back/validator/RfcValidatorTest.java` — 3 tests, instancia directa (no necesita Spring)
- `plataformas-danos-back/src/test/java/com/plataformas_danos_back/repository/CotizacionRepositoryTest.java` — 1 test con @DataMongoTest + Testcontainers `mongo:7`
- `plataformas-danos-back/src/test/java/com/plataformas_danos_back/controller/CotizacionControllerMockMvcTest.java` — 8 tests MockMvc standalone

**Caveats importantes:**

1. **Test de 401 en standaloneSetup:** El standaloneSetup no activa filtros JWT. El test `POST_sinToken_retorna401` simula el 401 haciendo que el servicio lance `InsufficientAuthenticationException`, que el `GlobalExceptionHandler` captura como `AuthenticationException` y devuelve 401. No usar `@SpringBootTest` solo para este caso.

2. **Test de 403 en standaloneSetup:** El test `PUT_sinRolEdicion_retorna403` usa el mock del servicio para lanzar `AccessDeniedException`. El `GlobalExceptionHandler` la captura y devuelve 403 con `code=ACCESS_DENIED`.

3. **Roles en standalone:** Se usa `autenticarConRoles()` helper que carga `UsernamePasswordAuthenticationToken` con `SimpleGrantedAuthority("ROLE_" + role)` en el `SecurityContextHolder`. Se limpia en `@BeforeEach`.

4. **Conflicto de versión (OptimisticLock):** El service lanza `CotizacionConflictException` tanto en el check de versión en `validarVersionOptimista()` como al capturar `OptimisticLockingFailureException` del repo. El test del service cubre el primer path (versión desigual antes del save). El path del `save()` quedaría cubierto con un test adicional si se requiere.

5. **RfcValidator:** RFC persona física válido: `PEPJ800326IG0` (13 chars). RFC persona moral válido: `SAT930101OI1` (12 chars).

**Why:** spring-security-test no está en el pom.xml (solo spring-boot-starter-security), por lo que no se puede usar `@WithMockUser` ni `SecurityMockMvcRequestPostProcessors.user()`. El workaround es SecurityContextHolder manual.

**How to apply:** Para cualquier futuro test de controller con seguridad en este proyecto, usar el helper `autenticarConRoles()` en lugar de anotar con `@WithMockUser`.
