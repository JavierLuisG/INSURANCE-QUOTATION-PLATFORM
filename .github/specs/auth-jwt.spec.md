---
id: SPEC-012
status: IN_PROGRESS
feature: auth-jwt
created: 2026-05-01
updated: 2026-05-01
author: spec-generator
version: "1.0"
related-specs: []
---

# Spec: Autenticación y Autorización JWT

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Implementar un sistema de autenticación y autorización basado en JWT que permita el registro de usuarios, inicio de sesión y protección de endpoints. Actualmente existe `JwtAuthenticationFilter` y `SecurityConfig` que validan tokens Bearer, pero no existe ningún mecanismo para generar dichos tokens ni una entidad `User` en MongoDB. Este feature cierra esa brecha añadiendo los endpoints `POST /api/v1/auth/register` y `POST /api/v1/auth/login`, la entidad `User`, y las páginas de login/registro en el frontend.

### Requerimiento de Negocio

> Tomado de `.github/requirements/generar-autenticacion.md`
>
> Implementar un sistema de autenticación y autorización basado en JWT que permita:
> 1. Registro de usuarios con credenciales (email y contraseña).
> 2. Inicio de sesión (login) y retorno de un JWT válido.
> 3. Generación y validación de tokens JWT con claims `sub`, `iat`, `exp` y `roles`.
> 4. Protección de endpoints mediante autenticación — los endpoints de cotizaciones solo deben ser accesibles con token válido.

### Historias de Usuario

#### HU-AUTH-01: Registro de usuario

```
Como:        Usuario nuevo de la plataforma de cotizaciones
Quiero:      Registrarme con email, contraseña y un rol del sistema
Para:        Obtener acceso a los flujos de cotización según mi perfil

Prioridad:   Alta
Estimación:  M
Dependencias: Ninguna
Capa:        Ambas
```

#### Criterios de Aceptación — HU-AUTH-01

**Happy Path**
```gherkin
CRITERIO-1.1: Registro exitoso de usuario nuevo
  Dado que:  el email "agente@ejemplo.com" no existe en la base de datos
  Cuando:    se envía POST /api/v1/auth/register con email, contraseña y rol "agente_ventas"
  Entonces:  se retorna HTTP 201
             y el cuerpo contiene { "message": "Usuario registrado exitosamente" }
             y la contraseña se almacena hasheada con BCrypt en MongoDB
```

**Error Path**
```gherkin
CRITERIO-1.2: Intento de registro con email ya existente
  Dado que:  el email "agente@ejemplo.com" ya está registrado
  Cuando:    se envía POST /api/v1/auth/register con el mismo email
  Entonces:  se retorna HTTP 409
             y el cuerpo contiene { "message": "El email ya está registrado", "code": "USER_ALREADY_EXISTS" }
```

**Error Path**
```gherkin
CRITERIO-1.3: Registro con datos inválidos
  Dado que:  no existe sesión activa
  Cuando:    se envía POST /api/v1/auth/register con email mal formado o contraseña vacía
  Entonces:  se retorna HTTP 400
             y el cuerpo contiene { "message": "<descripción de error de validación>", "code": "VALIDATION_ERROR" }
```

**Edge Case**
```gherkin
CRITERIO-1.4: Registro con rol inválido
  Dado que:  no existe sesión activa
  Cuando:    se envía POST /api/v1/auth/register con un rol que no pertenece al catálogo del sistema
  Entonces:  se retorna HTTP 400
             y el cuerpo contiene { "message": "Rol no válido", "code": "INVALID_ROLE" }
```

---

#### HU-AUTH-02: Inicio de sesión (login)

```
Como:        Usuario registrado en la plataforma
Quiero:      Autenticarme con mi email y contraseña
Para:        Recibir un JWT que me permita acceder a los endpoints protegidos

Prioridad:   Alta
Estimación:  S
Dependencias: HU-AUTH-01
Capa:        Ambas
```

#### Criterios de Aceptación — HU-AUTH-02

**Happy Path**
```gherkin
CRITERIO-2.1: Login exitoso con credenciales válidas
  Dado que:  el usuario "agente@ejemplo.com" está registrado con contraseña "P@ssword123"
  Cuando:    se envía POST /api/v1/auth/login con esas credenciales
  Entonces:  se retorna HTTP 200
             y el cuerpo contiene { "token": "<JWT>", "expiresIn": 86400 }
             y el JWT incluye claims: sub (email), roles (lista), iat, exp
```

**Error Path**
```gherkin
CRITERIO-2.2: Login con contraseña incorrecta
  Dado que:  el usuario "agente@ejemplo.com" existe
  Cuando:    se envía POST /api/v1/auth/login con contraseña incorrecta
  Entonces:  se retorna HTTP 401
             y el cuerpo contiene { "message": "Credenciales inválidas", "code": "INVALID_CREDENTIALS" }
```

**Error Path**
```gherkin
CRITERIO-2.3: Login con email no registrado
  Dado que:  el email "noexiste@ejemplo.com" no está en la base de datos
  Cuando:    se envía POST /api/v1/auth/login con ese email
  Entonces:  se retorna HTTP 401
             y el cuerpo contiene { "message": "Credenciales inválidas", "code": "INVALID_CREDENTIALS" }
```

---

#### HU-AUTH-03: Protección de endpoints existentes

```
Como:        Sistema de la plataforma
Quiero:      Que todos los endpoints de /api/v1/* (excepto /auth/**) requieran un JWT válido
Para:        Garantizar que solo usuarios autenticados accedan a cotizaciones y parámetros

Prioridad:   Alta
Estimación:  XS
Dependencias: HU-AUTH-02
Capa:        Backend
```

#### Criterios de Aceptación — HU-AUTH-03

**Happy Path**
```gherkin
CRITERIO-3.1: Acceso con token válido
  Dado que:  el usuario tiene un JWT válido no expirado con rol "agente_ventas"
  Cuando:    se envía GET /api/v1/cotizaciones/{folio} con header Authorization: Bearer <token>
  Entonces:  se retorna HTTP 200 con los datos de la cotización
```

**Error Path**
```gherkin
CRITERIO-3.2: Acceso sin token
  Dado que:  no se envía header Authorization
  Cuando:    se envía GET /api/v1/cotizaciones/{folio}
  Entonces:  se retorna HTTP 401
             y el cuerpo contiene { "message": "Token requerido", "code": "UNAUTHORIZED" }
```

**Error Path**
```gherkin
CRITERIO-3.3: Acceso con token expirado
  Dado que:  el token JWT tiene claim exp en el pasado
  Cuando:    se envía cualquier request a /api/v1/* con ese token
  Entonces:  se retorna HTTP 401
             y el cuerpo contiene { "message": "Token expirado o inválido", "code": "TOKEN_EXPIRED" }
```

---

#### HU-AUTH-04: Flujo de autenticación en el frontend

```
Como:        Usuario que accede al cotizador web
Quiero:      Ver una pantalla de login/registro antes de acceder a las cotizaciones
Para:        Iniciar sesión y que el token se use automáticamente en todas las peticiones

Prioridad:   Alta
Estimación:  L
Dependencias: HU-AUTH-02
Capa:        Frontend
```

#### Criterios de Aceptación — HU-AUTH-04

**Happy Path**
```gherkin
CRITERIO-4.1: Redirección a login cuando no autenticado
  Dado que:  el usuario no tiene token en el authStore
  Cuando:    intenta navegar a /cotizaciones/nueva
  Entonces:  es redirigido a /login
```

**Happy Path**
```gherkin
CRITERIO-4.2: Login exitoso redirige al home
  Dado que:  el usuario está en /login
  Cuando:    completa el formulario con credenciales válidas y envía
  Entonces:  el token se almacena en el authStore (Zustand)
             y es redirigido a /
```

**Happy Path**
```gherkin
CRITERIO-4.3: Token incluido en peticiones al backend
  Dado que:  el usuario tiene token almacenado en el authStore
  Cuando:    realiza cualquier acción que llame al backend (crear/ver cotización)
  Entonces:  la petición incluye el header Authorization: Bearer <token> automáticamente
```

---

### Reglas de Negocio

1. La contraseña debe tener mínimo 8 caracteres, al menos una mayúscula, una minúscula y un número.
2. La contraseña nunca se almacena en texto plano — siempre BCrypt con strength 12.
3. Los roles válidos son exactamente: `creador_cotizacion`, `agente_ventas`, `vendedor`, `administrador_ventas`, `editor_cotizaciones`.
4. El token JWT tiene duración de 24 horas (`exp = iat + 86400s`). No hay refresh token en esta versión.
5. Los endpoints `/api/v1/auth/register` y `/api/v1/auth/login` son públicos (sin token requerido).
6. El endpoint `/actuator/**` permanece público (ya configurado).
7. El claim `sub` del JWT debe ser el email del usuario.
8. El claim `roles` del JWT es una lista de strings (sin prefijo `ROLE_` — el filtro ya lo añade).
9. Ante credenciales inválidas, la respuesta es siempre `401` con mensaje genérico (no revelar si el email existe).
10. El campo `email` es único en la colección `users`.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas
| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `User` | colección `users` | **nueva** | Documento de usuario con credenciales y rol |

#### Campos del modelo `User`
| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | `String` | sí | auto (MongoDB ObjectId) | ID interno MongoDB |
| `email` | `String` | sí | formato email, único | Email del usuario (usado como `sub` en JWT) |
| `password` | `String` | sí | min 8 chars, BCrypt | Contraseña hasheada |
| `role` | `String` | sí | enum de 5 valores | Rol del usuario en el sistema |
| `active` | `Boolean` | sí | default `true` | Si el usuario está habilitado |
| `createdAt` | `Instant` | sí | auto-generado | Timestamp de creación |
| `updatedAt` | `Instant` | sí | auto-gestionado | Timestamp de última actualización |

#### Índices / Constraints
- Índice único en `email` — búsqueda por email en login y validación de unicidad en registro.

---

### API Endpoints

#### POST /api/v1/auth/register
- **Descripción**: Registra un nuevo usuario en el sistema
- **Auth requerida**: no
- **Request Body**:
  ```json
  {
    "email": "agente@ejemplo.com",
    "password": "P@ssword123",
    "role": "agente_ventas"
  }
  ```
- **Response 201**:
  ```json
  { "message": "Usuario registrado exitosamente" }
  ```
- **Response 400**: email inválido, contraseña débil, rol no válido
  ```json
  { "message": "El formato del email no es válido", "code": "VALIDATION_ERROR" }
  ```
- **Response 409**: email ya registrado
  ```json
  { "message": "El email ya está registrado", "code": "USER_ALREADY_EXISTS" }
  ```

#### POST /api/v1/auth/login
- **Descripción**: Autentica al usuario y retorna un JWT
- **Auth requerida**: no
- **Request Body**:
  ```json
  {
    "email": "agente@ejemplo.com",
    "password": "P@ssword123"
  }
  ```
- **Response 200**:
  ```json
  {
    "token": "<JWT firmado con HMAC-SHA256>",
    "expiresIn": 86400
  }
  ```
- **Response 400**: campos faltantes o inválidos
  ```json
  { "message": "El email es obligatorio", "code": "VALIDATION_ERROR" }
  ```
- **Response 401**: credenciales inválidas (email no existe o contraseña incorrecta)
  ```json
  { "message": "Credenciales inválidas", "code": "INVALID_CREDENTIALS" }
  ```

#### Modificación de SecurityConfig
- Añadir `.requestMatchers("/api/v1/auth/**").permitAll()` antes de `.anyRequest().authenticated()`
- El `JwtAuthenticationFilter` existente NO se modifica — ya maneja la validación.

---

### Estructura JWT

```
Header: { "alg": "HS256", "typ": "JWT" }

Payload:
{
  "sub": "agente@ejemplo.com",
  "roles": ["agente_ventas"],
  "iat": <epoch-segundos>,
  "exp": <iat + 86400>
}

Firma: HMAC-SHA256 con clave de jwt.secret (desde application.properties / env var)
```

---

### Diseño Frontend

#### Componentes nuevos
| Componente | Archivo | Props principales | Descripción |
|------------|---------|------------------|-------------|
| `LoginForm` | `components/Auth/LoginForm.tsx` | `onSuccess: () => void` | Formulario de login con email/password |
| `RegisterForm` | `components/Auth/RegisterForm.tsx` | `onSuccess: () => void` | Formulario de registro con email/password/rol |

#### Páginas nuevas
| Página | Archivo | Ruta | Protegida |
|--------|---------|------|-----------|
| `LoginPage` | `app/login/page.tsx` | `/login` | no |
| `RegisterPage` | `app/register/page.tsx` | `/register` | no |

#### Middleware de protección de rutas
| Archivo | Descripción |
|---------|-------------|
| `middleware.ts` (raíz de `src/`) | Next.js middleware que redirige a `/login` si no hay token en cookie/store |

#### Store (Zustand)
| Store | Archivo | Estado | Acciones |
|-------|---------|--------|----------|
| `authStore` | `store/authStore.ts` | `token: string \| null`, `role: string \| null`, `email: string \| null` | `setAuth(token, role, email)`, `clearAuth()` |

#### Hooks
| Hook | Archivo | Retorna | Descripción |
|------|---------|---------|-------------|
| `useAuth` | `hooks/useAuth.ts` | `{ token, role, email, login, register, logout, isAuthenticated }` | Lógica de autenticación que consume authStore y authService |

#### Services (llamadas API)
| Función | Archivo | Endpoint |
|---------|---------|---------|
| `loginUser(data)` | `lib/services/authService.ts` | `POST /api/v1/auth/login` |
| `registerUser(data)` | `lib/services/authService.ts` | `POST /api/v1/auth/register` |

#### Schemas Zod
| Schema | Archivo | Campos validados |
|--------|---------|-----------------|
| `loginSchema` | `lib/schemas/auth.schema.ts` | email (email válido), password (min 8 chars) |
| `registerSchema` | `lib/schemas/auth.schema.ts` | email, password (regex complejidad), role (enum) |

#### Modificación de Axios
- El interceptor de Axios en `lib/services/` debe añadir `Authorization: Bearer <token>` leyendo del `authStore` en cada request a `/api/v1/*`.
- En `cotizacionService.ts` y `catalogoService.ts` eliminar el parámetro `token` manual una vez que el interceptor esté configurado.

---

### Arquitectura y Dependencias

**Backend:**
- `BCryptPasswordEncoder` — nuevo bean en `SecurityConfig` (Spring Security ya lo incluye).
- `JwtService` — nuevo servicio en `security/` encargado de generar y firmar tokens.
- `AuthService` / `AuthServiceImpl` — lógica de registro y login.
- `AuthController` — endpoints `/api/v1/auth/**`.
- `UserRepository` — `MongoRepository<User, String>` con `findByEmail`.
- Sin dependencias nuevas en `pom.xml` — JJWT 0.12.6 y Spring Security ya están declarados.

**Frontend:**
- Sin paquetes npm nuevos — Zustand, Axios y Zod ya están en el proyecto.
- `middleware.ts` de Next.js para protección de rutas en el servidor.

### Notas de Implementación

> - El `JwtAuthenticationFilter` existente ya lee `sub` y `roles` del token — el `JwtService` nuevo debe emitir esos claims con esos nombres exactos.
> - La clave `jwt.secret` debe tener al menos 32 bytes para HMAC-SHA256. Gestionar mediante variable de entorno, nunca hardcodeada.
> - El token se almacena en Zustand (en memoria). Para persistencia entre recargas, considerar `localStorage` solo en desarrollo; en producción preferir cookie HttpOnly (fuera de alcance de esta versión).
> - La respuesta de error `401` en login debe ser genérica para prevenir enumeración de usuarios (CRITERIO-2.2 y 2.3 devuelven el mismo mensaje).

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación
- [x] Crear entidad `User.java` en `model/entity/` con índice único en `email`
- [x] Crear `RegisterRequest.java` y `LoginRequest.java` en `model/dto/`
- [x] Crear `LoginResponse.java` en `model/dto/` con `token` y `expiresIn`
- [x] Crear `UserRepository.java` con método `findByEmail(String email): Optional<User>`
- [x] Crear `JwtService.java` en `security/` — método `generateToken(User user): String` con claims `sub`, `roles`, `iat`, `exp`
- [x] Registrar `BCryptPasswordEncoder` como bean en `SecurityConfig`
- [x] Crear `AuthService.java` (interfaz) + `AuthServiceImpl.java` — métodos `register` y `login`
- [x] Crear `AuthController.java` — `POST /api/v1/auth/register` y `POST /api/v1/auth/login`
- [x] Actualizar `SecurityConfig` para permitir `/api/v1/auth/**` sin autenticación
- [x] Añadir `jwt.expiration=86400` en `application.properties`
- [ ] Crear `AuthEntryPointHandler` para respuestas 401 estructuradas (`{ message, code }`)

#### Tests Backend
- [ ] `AuthServiceTest_register_success` — happy path registro
- [ ] `AuthServiceTest_register_duplicate_email_throws_conflict` — email duplicado → 409
- [ ] `AuthServiceTest_register_invalid_role_throws_bad_request` — rol inválido → 400
- [ ] `AuthServiceTest_login_success_returns_token` — credenciales válidas → token JWT
- [ ] `AuthServiceTest_login_wrong_password_throws_unauthorized` — contraseña incorrecta → 401
- [ ] `AuthServiceTest_login_user_not_found_throws_unauthorized` — email no encontrado → 401
- [ ] `JwtServiceTest_generateToken_contains_expected_claims` — valida sub, roles, exp
- [ ] `JwtServiceTest_token_is_valid_for_24_hours` — exp = iat + 86400
- [ ] `AuthControllerTest_post_register_returns_201` — endpoint registro
- [ ] `AuthControllerTest_post_register_returns_409_duplicate` — email duplicado
- [ ] `AuthControllerTest_post_login_returns_200_with_token` — endpoint login exitoso
- [ ] `AuthControllerTest_post_login_returns_401_invalid_credentials` — credenciales inválidas
- [ ] `AuthControllerTest_protected_endpoint_returns_401_no_token` — endpoint protegido sin token

### Frontend

#### Implementación
- [x] Crear `store/authStore.ts` — Zustand slice con `token`, `role`, `email`, `setAuth`, `clearAuth`
- [x] Crear `lib/services/authService.ts` — `loginUser` y `registerUser` via Axios
- [x] Crear `lib/schemas/auth.schema.ts` — schemas Zod `loginSchema` y `registerSchema`
- [x] Crear `hooks/useAuth.ts` — consume authStore y authService; expone `login`, `register`, `logout`, `isAuthenticated`
- [x] Configurar interceptor Axios en `lib/services/` para añadir `Authorization: Bearer <token>` desde authStore
- [x] Crear `components/Auth/LoginForm.tsx` — formulario con validación Zod + react-hook-form
- [x] Crear `components/Auth/RegisterForm.tsx` — formulario con selector de rol
- [x] Crear `app/login/page.tsx` — página pública de login
- [x] Crear `app/register/page.tsx` — página pública de registro
- [x] Crear `src/middleware.ts` — proteger rutas `/cotizaciones/**` redirigiendo a `/login` sin token

#### Tests Frontend
- [ ] `LoginForm renders email and password inputs`
- [ ] `LoginForm shows validation error for empty fields`
- [ ] `LoginForm calls loginUser service on valid submit`
- [ ] `RegisterForm shows role selector with valid options`
- [ ] `RegisterForm shows password complexity error`
- [ ] `useAuth login stores token in authStore on success`
- [ ] `useAuth login returns error on invalid credentials`
- [ ] `useAuth logout clears authStore`
- [ ] `authStore setAuth updates token, role and email`
- [ ] `authStore clearAuth resets to null values`
- [ ] `authService loginUser calls POST /api/v1/auth/login`
- [ ] `authService registerUser calls POST /api/v1/auth/register`

### QA
- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 a 1.4, 2.1 a 2.3, 3.1 a 3.3, 4.1 a 4.3
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD (datos sensibles: passwords, tokens)
- [ ] Verificar que contraseñas no aparecen en logs (nivel DEBUG/INFO)
- [ ] Verificar que stack traces no se exponen en respuestas 4xx/5xx
- [ ] Prueba de seguridad manual: intentar acceder a `/api/v1/cotizaciones` sin token → 401
- [ ] Prueba de seguridad manual: token manipulado (firma inválida) → 401
- [ ] Prueba de seguridad manual: token expirado → 401
- [ ] Revisar cobertura de tests contra criterios de aceptación (≥ 80% global)
- [ ] Actualizar estado spec: `status: IMPLEMENTED`
