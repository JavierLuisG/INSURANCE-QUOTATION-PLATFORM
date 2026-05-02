---
name: Estructura frontend cotizador-danos-web
description: Estructura de carpetas, alias de paths y dependencias clave del proyecto Next.js 14
type: project
---

El frontend vive en `cotizador-danos-web/src/`. El alias `@/*` apunta a `./src/*` (tsconfig.json).

Las capas creadas hasta ahora:
- `src/lib/schemas/` — Zod schemas
- `src/lib/services/` — Axios services
- `src/store/` — Zustand stores
- `src/hooks/` — hooks React
- `src/components/Cotizador/` — componentes de UI del cotizador
- `src/app/cotizaciones/` — páginas App Router

**Why:** El proyecto arrancó como un create-next-app vacío (solo app/page.tsx y app/layout.tsx). Toda la estructura de capas fue creada en EP-001 FT-001.

**How to apply:** Al añadir nuevas features, seguir el mismo patrón de carpetas. Siempre usar `@/` para imports internos.

Dependencias instaladas en package.json (verificado 2026-05-01):
- `react-hook-form` ^7.74.0 — formularios (YA instalado)
- `@hookform/resolvers` ^5.2.2 — resolver Zod (YA instalado)
- `zustand` ^5.0.3, `axios` ^1.7.9, `zod` ^3.24.1

El token JWT del usuario se almacena en Zustand `authStore` (`token`, `role`, `email`) Y en cookie `auth-token` (para que el Next.js middleware pueda leerla server-side). Las acciones `setAuth`/`clearAuth` sincronizan ambos. Los hooks `useCotizacion` y `useCatalogos` aún reciben `token` como parámetro (refactor fuera de scope de SPEC-012).

Capas añadidas en SPEC-012 (auth-jwt):
- `src/store/authStore.ts` — Zustand auth store con cookie sync
- `src/lib/schemas/auth.schema.ts` — loginSchema, registerSchema, roleEnum
- `src/lib/services/authService.ts` — loginUser, registerUser + interceptor Axios global
- `src/hooks/useAuth.ts` — hook de autenticación (decodifica JWT con atob)
- `src/components/Auth/LoginForm.tsx` — formulario login (react-hook-form + Zod)
- `src/components/Auth/RegisterForm.tsx` — formulario registro con select de rol
- `src/app/login/page.tsx` — página pública /login
- `src/app/register/page.tsx` — página pública /register
- `src/middleware.ts` — protege /cotizaciones/** leyendo cookie auth-token
