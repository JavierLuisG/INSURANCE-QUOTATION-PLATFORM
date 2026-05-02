---
name: SPEC-012 — Autenticación JWT implementada
description: Feature auth-jwt en plataformas-danos-back: entidad User, JwtService, AuthService+Impl, AuthController, 3 excepciones, UserRepository; SecurityConfig modificado con BCryptPasswordEncoder (strength 12) y permit /api/v1/auth/**; spec IN_PROGRESS
type: project
---

Feature auth-jwt (SPEC-012) — 10 artefactos nuevos creados, 3 archivos modificados.

**Artefactos nuevos:**
- `model/entity/User.java` — @Document("users"), @Indexed(unique=true) en email, campos: id, email, password, role, active, createdAt, updatedAt
- `model/dto/RegisterRequest.java` — @NotBlank @Email en email; @Pattern regex complejidad contraseña
- `model/dto/LoginRequest.java` — @NotBlank @Email en email
- `model/dto/LoginResponse.java` — token (String), expiresIn (long)
- `model/dto/MessageResponse.java` — message (String)
- `repository/UserRepository.java` — findByEmail(String): Optional<User>
- `security/JwtService.java` — generateToken(User): String; usa ${jwt.secret} y ${jwt.expiration:86400}; claims: sub=email, roles=List.of(role)
- `service/AuthService.java` — interfaz con register() y login()
- `service/AuthServiceImpl.java` — VALID_ROLES Set<String> con 5 roles; register valida rol, detecta duplicado, hashea BCrypt; login busca por email, verifica password, genera token
- `controller/AuthController.java` — POST /api/v1/auth/register (201) y POST /api/v1/auth/login (200)
- `exception/UserAlreadyExistsException.java` — HTTP 409, code USER_ALREADY_EXISTS
- `exception/InvalidCredentialsException.java` — HTTP 401, code INVALID_CREDENTIALS (mensaje genérico anti-enumeración)
- `exception/InvalidRoleException.java` — HTTP 400, code INVALID_ROLE

**Archivos modificados:**
- `config/SecurityConfig.java` — añadido BCryptPasswordEncoder bean (strength 12) y .requestMatchers("/api/v1/auth/**").permitAll()
- `exception/GlobalExceptionHandler.java` — handlers para UserAlreadyExistsException, InvalidCredentialsException, InvalidRoleException
- `src/main/resources/application.yaml` — añadido jwt.expiration: ${JWT_EXPIRATION:86400}

**Why:** Cierra la brecha entre el JwtAuthenticationFilter existente y la generación de tokens; sin este feature no había forma de obtener un JWT válido.

**How to apply:** BCryptPasswordEncoder se inyecta por constructor (no @Autowired). El JwtAuthenticationFilter NO fue modificado — lee sub y roles del token exactamente como JwtService los emite.
