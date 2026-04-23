---
applyTo: "cotizador-danos-web/**/*.{ts,tsx}"
---

> **Scope**: Se aplica al módulo `cotizador-danos-web`. Stack: Next.js 14 App Router + TypeScript 5 + Tailwind CSS + Zustand + Zod + Axios.

# Instrucciones para Archivos de Frontend (Next.js 14 + TypeScript + Tailwind)

## Convenciones Obligatorias

- **CSS**: SIEMPRE Tailwind CSS — NUNCA CSS Modules, styled-components, clases globales ad-hoc ni estilos inline.
- **Tipos**: TypeScript estricto en todo el proyecto. Prohibido `any`.
- **Estado global**: Zustand store — nunca duplicar estado de cotización.
- **Validación**: Zod para todo schema de formulario y respuesta de API.
- **Env vars**: Prefijo `NEXT_PUBLIC_` para variables accesibles en cliente (ej. `NEXT_PUBLIC_API_URL`).
- **Server vs Client Components**: marcar `'use client'` solo cuando el componente usa hooks, estado o eventos de usuario.

## Estructura de Archivos

```
cotizador-danos-web/
  app/                      ← App Router (páginas y layouts)
    (ruta)/page.tsx
    (ruta)/layout.tsx
  components/               ← Componentes reutilizables (.tsx)
  hooks/                    ← Custom hooks (use*.ts)
  lib/services/             ← Llamadas HTTP via Axios
  store/                    ← Zustand stores
  lib/schemas/              ← Zod schemas
  types/                    ← Interfaces y tipos TypeScript
```

## Llamadas a la API Backend

Usar siempre **Axios** (no `fetch`). Las llamadas van en `lib/services/`, nunca directamente en componentes o páginas.

```typescript
// lib/services/quotationService.ts
import axios from 'axios';

const api = axios.create({ baseURL: process.env.NEXT_PUBLIC_API_URL });

export async function createQuotation(data: QuotationRequest, token: string) {
  const res = await api.post<QuotationResponse>('/api/v1/quotations', data, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return res.data;
}
```

## Estado Global con Zustand

```typescript
// store/quotationStore.ts
import { create } from 'zustand';

interface QuotationStore {
  folio: string | null;
  setFolio: (folio: string) => void;
}

export const useQuotationStore = create<QuotationStore>((set) => ({
  folio: null,
  setFolio: (folio) => set({ folio }),
}));
```

## Navegación (App Router)

Usar `useRouter` de `next/navigation` o `<Link>` de `next/link`. NUNCA React Router.

```typescript
import { useRouter } from 'next/navigation';
const router = useRouter();
router.push('/cotizacion/coberturas');
```

## Componentes

- Un componente por archivo (`.tsx`).
- PascalCase para el nombre del componente y del archivo.
- Props tipadas con interface explícita — sin JSDoc, usar TypeScript directamente.
- `'use client'` obligatorio si el componente usa `useState`, `useEffect` o event handlers.
- No lógica de negocio en componentes — delegar a hooks o store.

## Rutas (App Router)

Las rutas viven en `app/`. Para registrar una nueva ruta crear `app/<ruta>/page.tsx`. Para rutas protegidas, usar middleware o un wrapper en el layout.

---

> Para estándares de Clean Code, SOLID, seguridad y observabilidad, ver `.github/docs/lineamientos/dev-guidelines.md`.
