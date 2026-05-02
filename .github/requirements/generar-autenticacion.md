Quiero implementar un sistema de autenticación y autorización basado en JWT para la aplicación.

### Analisis

Analiza el código fuente del proyecto y determina:

1. Qué actores existen en el sistema (usuarios, roles implícitos, etc.)
2. Qué entidades están relacionadas con el acceso a información (por ejemplo: cotizaciones)
3. Qué endpoints existen actualmente y cuáles deberían protegerse
4. Qué tipo de arquitectura se está utilizando (por ejemplo: monolito, hexagonal, etc.)
5. Cómo se están manejando actualmente las capas (controladores, servicios, repositorios)
6. Cualquier indicio de autorización implícita (por ejemplo: acceso por tipo de usuario)
7. Qué rutas hay en el proyecto y están protegidas actualmente (si aplica)

### Contexto

Hay rutas que requieren autenticación, pero no existe ningún mecanismo implementado para generar o validar tokens.

---

### Objetivo

Diseñar e implementar un sistema de autenticación y autorización que permita:

1. Registro de usuarios
2. Inicio de sesión (login)
3. Generación y validación de tokens JWT
4. Protección de endpoints mediante autenticación

---

### Requerimientos funcionales mínimos

- El sistema debe permitir el registro de usuarios con credenciales (email y contraseña).
- El sistema debe permitir login y retornar un JWT válido.
- El token JWT debe ser requerido para acceder a endpoints protegidos.

---

### Requerimientos técnicos

- Usar autenticación basada en JWT (JSON Web Tokens).
- Definir estructura del token (claims como `sub`, `iat`, `exp`).
- Definir expiración del token.
- Definir mecanismo de validación del token en cada request. 