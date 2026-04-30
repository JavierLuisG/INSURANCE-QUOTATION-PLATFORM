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

Dependencias que NO estaban en package.json original y se deben instalar:
- `react-hook-form` — formularios
- `@hookform/resolvers` — resolver Zod para react-hook-form

El token JWT del usuario se almacena en `sessionStorage` bajo la clave `'token'`. Este patrón se usa en los hooks `useCotizacion` y `useCatalogos`.
