---
name: SPEC-012 test suite — Auth JWT
description: Tests unitarios MockMvc y JwtService para HU-AUTH-01 a HU-AUTH-04; caveats BCryptPasswordEncoder como @Mock y ReflectionTestUtils para JwtService
type: project
---

Tests generados el 2026-05-01 para la feature de autenticación JWT (SPEC-012).

## Archivos creados

- `src/test/java/com/plataformas_danos_back/service/AuthServiceImplTest.java` — 7 tests unitarios con MockitoExtension
- `src/test/java/com/plataformas_danos_back/security/JwtServiceTest.java` — 3 tests de generación y validación de JWT
- `src/test/java/com/plataformas_danos_back/controller/AuthControllerMockMvcTest.java` — 9 tests MockMvc standalone

## Caveats importantes

- `BCryptPasswordEncoder` se mockea con `@Mock` (no se instancia real) porque AuthServiceImpl lo recibe por constructor injection y Mockito lo inyecta sin problema.
- `JwtService` usa `@Value` — se deben inyectar `jwtSecret` y `expirationSeconds` con `ReflectionTestUtils.setField` antes de cada test. El secret debe tener al menos 32 chars para HMAC-SHA256 con JJWT 0.12.6.
- El parser JJWT 0.12.6 usa `Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload()` (API fluida diferente a versiones anteriores).
- El `GlobalExceptionHandler` maneja `InvalidCredentialsException` → 401 `INVALID_CREDENTIALS`, `UserAlreadyExistsException` → 409 `USER_ALREADY_EXISTS`, `InvalidRoleException` → 400 `INVALID_ROLE`, `MethodArgumentNotValidException` → 400 `INVALID_REQUEST`.
- El standaloneSetup de MockMvc no activa filtros de seguridad — los tests de 401 se modelan lanzando `InvalidCredentialsException` desde el service mock, no vía Spring Security.
- `RegisterRequest.role` con cadena vacía `""` falla primero en Bean Validation (`@NotBlank`) antes de llegar a la validación `VALID_ROLES` del service — el controller test verifica `INVALID_REQUEST`, el service test verifica `InvalidRoleException` (porque el service recibe el objeto directamente sin pasar por el validator de Spring MVC).

**Why:** El comportamiento de validación en dos capas (Bean Validation en controller, lógica de dominio en service) requiere escenarios separados por capa para cobertura correcta.
**How to apply:** Al añadir nuevas validaciones de rol o contraseña, verificar si la restricción vive en el DTO (Bean Validation) o en el service (lógica de negocio) y escribir el test en la capa correcta.
