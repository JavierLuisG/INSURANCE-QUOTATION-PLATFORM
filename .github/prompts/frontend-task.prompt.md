---
name: frontend-task
description: Implementa una funcionalidad en el frontend Next.js 14 basada en una spec ASDD aprobada.
argument-hint: "<nombre-feature> (debe existir .github/specs/<nombre-feature>.spec.md)"
agent: Frontend Developer
tools:
  - edit/createFile
  - edit/editFiles
  - read/readFile
  - search/listDirectory
  - search
  - execute/runInTerminal
---

Implementa el frontend para el feature especificado, siguiendo la spec aprobada.

**Feature**: ${input:featureName:nombre del feature en kebab-case}

## Pasos obligatorios:

1. **Lee la spec** en `.github/specs/${input:featureName:nombre-feature}.spec.md` — si no existe, detente e informa al usuario.
2. **Consulta el diseño de referencia** en `docs/`:
   - Lee `docs/02-analysis/FEATURES.md` para identificar el FT-XXX y EP-XXX del feature.
   - Lee `docs/03-design/epicas/{EP-XXX}/core/{FT-XXX}/` — los HU files contienen flujos de usuario, estados UI y validaciones de formulario que complementan la spec.
   - Lee `docs/01-requirements/RNF.md` para SLOs de performance frontend (UI <500ms).
3. **Revisa el código existente** en `cotizador-danos-web/` para entender patrones actuales.
4. **Implementa en orden**:
   - `lib/services/` — funciones Axios para llamadas al backend
   - `store/` — Zustand slice si hay estado global nuevo
   - `lib/schemas/` — Zod schema para validación de formulario (si aplica)
   - `hooks/` — custom hook para estado local + consumo de service
   - `components/` — componentes reutilizables con Tailwind
   - `app/<ruta>/page.tsx` — página con App Router
5. **Verifica** el build: `cd cotizador-danos-web && npm run build`

## Restricciones:
- USAR Tailwind CSS exclusivamente — NUNCA CSS Modules, styled-components ni clases globales.
- Marcar `'use client'` en todo componente que use hooks, estado o event handlers.
- Variables de entorno: prefijo `NEXT_PUBLIC_` (ej. `NEXT_PUBLIC_API_URL`).
- Token JWT en header: `Authorization: Bearer <token>` para endpoints protegidos.
- NO usar React Router — usar `next/navigation` para navegación.
