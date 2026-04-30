---
name: Configuración de tests frontend — cotizador-danos-web
description: Stack, paths de tests, alias TS y patrón MSW ya establecidos en el proyecto
type: project
---

El proyecto `cotizador-danos-web` usa Vitest 3.x + @testing-library/react + MSW 2.x.

- `vitest.config.ts` creado en `cotizador-danos-web/` con alias `@/*` → `./src/*`, entorno jsdom y setupFiles apuntando a `src/__tests__/setup.ts`.
- `src/__tests__/setup.ts` arranca/resetea/cierra el servidor MSW via `beforeAll/afterEach/afterAll`.
- `src/__tests__/mocks/server.ts` — servidor MSW (Node) construido con `setupServer`.
- `src/__tests__/mocks/handlers.ts` — handlers por defecto para todos los endpoints de cotizaciones y catálogos, más fixtures exportados (`COTIZACION_FIXTURE`, `TIPOS_SEGUROS_FIXTURE`, etc.).

**Why:** la configuración no existía; se creó junto con los primeros 20 tests de SPEC-011.

**How to apply:** nuevos tests deben importar `server` y fixtures desde `src/__tests__/mocks/`. Para sobreescribir un handler en un test específico usar `server.use(...)` dentro del test.
