---
description: Reglas de frontend para este proyecto (Next.js 14 App Router + TypeScript 5 + Tailwind + Zustand). Se aplica automáticamente a archivos frontend.
paths:
  - "cotizador-danos-web/**"
---

# Reglas de Frontend — Next.js 14 + TypeScript 5 + Tailwind CSS

## Stack aprobado

- **Next.js 14** con **App Router** (`app/` directory)
- **TypeScript 5** — tipado estático obligatorio en todo el proyecto
- **Tailwind CSS 3.4.x** — estilos utilitarios (único sistema de estilos aprobado)
- **Zustand 5.x** — estado global de cotización (ubicaciones, coberturas, estado de máquina)
- **Zod 3.x** — validación de formularios y esquemas
- **Axios 1.7.x** — cliente HTTP para API REST del backend

**Prohibido:** React Router, CSS Modules, styled-components, CSS-in-JS, Bootstrap, Redux, MobX, Firebase SDK, fetch directo en componentes, Vite.

## Arquitectura por Capas

```
services → hooks/zustand store → components → app/ (App Router pages)
```

| Capa | Responsabilidad | Prohibido |
|------|----------------|-----------|
| `lib/services/` | Llamadas HTTP via Axios al backend | Estado, lógica de negocio |
| `store/` | Estado global Zustand (slices por dominio) | Render JSX, llamadas directas a red |
| `hooks/` | Hooks React que consumen store o services | Render JSX |
| `components/` | UI reutilizable — props + eventos, sin estado global | Estado global, llamadas API |
| `app/` | Páginas y layouts de App Router (Server y Client Components) | Lógica de negocio, llamadas API directas |

## Convenciones Obligatorias

- **Estilos**: SIEMPRE Tailwind — NUNCA CSS Modules, estilos inline o clases globales ad-hoc
- **Estado global**: SIEMPRE Zustand store — nunca duplicar estado de cotización
- **Variables de entorno**: prefijo `NEXT_PUBLIC_` para variables accesibles en cliente
- **API calls**: en `lib/services/` via Axios; token en header `Authorization: Bearer <token>`
- **Validación**: Zod para todo schema de formulario y respuesta de API
- **Tipos**: interfaces/tipos en `types/` o junto al componente que los consume

## Llamadas a la API (patrón obligatorio)

```typescript
// lib/services/quotationService.ts
import axios from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
});

export async function createQuotation(data: QuotationRequest, token: string) {
  const res = await api.post<QuotationResponse>('/api/v1/quotations', data, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return res.data;
}
```

## Estado Global con Zustand (patrón obligatorio)

```typescript
// store/quotationStore.ts
import { create } from 'zustand';

interface QuotationStore {
  coverages: Coverage[];
  setCoverages: (coverages: Coverage[]) => void;
}

export const useQuotationStore = create<QuotationStore>((set) => ({
  coverages: [],
  setCoverages: (coverages) => set({ coverages }),
}));
```

## Nomenclatura de Archivos

| Artefacto | Convención | Ejemplo |
|-----------|-----------|---------|
| Page (App Router) | `app/<ruta>/page.tsx` | `app/quotation/page.tsx` |
| Layout | `app/<ruta>/layout.tsx` | `app/quotation/layout.tsx` |
| Component | `components/<Feature>/<Component>.tsx` | `components/Quotation/CoverageForm.tsx` |
| Hook | `hooks/use<Feature>.ts` | `hooks/useQuotation.ts` |
| Service | `lib/services/<feature>Service.ts` | `lib/services/quotationService.ts` |
| Store | `store/<feature>Store.ts` | `store/quotationStore.ts` |
| Zod schema | `lib/schemas/<feature>.schema.ts` | `lib/schemas/quotation.schema.ts` |

- PascalCase para componentes (`.tsx`)
- camelCase con prefijo `use` para hooks
- camelCase para services y stores
- `'use client'` obligatorio en componentes con estado, efectos o eventos de usuario

## Anti-patrones Prohibidos

- Llamadas Axios directas en componentes o páginas (van en services)
- Estado de cotización duplicado fuera del Zustand store
- Estilos con CSS Modules, styled-components o inline styles
- Lógica de negocio en componentes (va en hooks o store)
- Hardcodear URLs de API (usar `NEXT_PUBLIC_API_URL`)
- Mezclar Server Components y Client Components sin marcar explícitamente `'use client'`

## Lineamientos completos

`.claude/docs/lineamientos/dev-guidelines.md` — Clean Code, SOLID, API REST, Seguridad, Observabilidad.
