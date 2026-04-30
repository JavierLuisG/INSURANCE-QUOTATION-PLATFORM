---
name: EP-001 FT-001 — Datos Generales del Cotizador implementado
description: Estado de implementación frontend de SPEC-011, archivos creados y pendientes
type: project
---

SPEC-011 (ep-001-ft-001-core-datos-generales) — implementación frontend completada el 2026-04-29.

Archivos creados:
- `src/lib/schemas/cotizacion.schema.ts` — CotizacionResponse, CotizacionRequest, CatalogoItem, RFC_REGEX, FOLIO_REGEX
- `src/lib/schemas/datosGenerales.schema.ts` — DatosGeneralesFormValues con refinement de rango de fechas
- `src/lib/services/cotizacionService.ts` — iniciarCotizacion, getCotizacion, actualizarCotizacion
- `src/lib/services/catalogoService.ts` — getCatalogos(tipo, token)
- `src/store/cotizacionStore.ts` — useCotizacionStore (Zustand 5.x)
- `src/hooks/useCotizacion.ts` — iniciar, cargar, actualizar con manejo de errores HTTP
- `src/hooks/useCatalogos.ts` — Promise.allSettled para carga independiente de 3 catálogos
- `src/components/Cotizador/FolioSearchBar.tsx`
- `src/components/Cotizador/CotizadorHeader.tsx`
- `src/components/Cotizador/AseguradoFields.tsx`
- `src/components/Cotizador/ParametrosSelector.tsx`
- `src/components/Cotizador/VigenciaFields.tsx`
- `src/components/Cotizador/DatosGeneralesForm.tsx`
- `src/app/cotizaciones/nueva/page.tsx`
- `src/app/cotizaciones/[folio]/page.tsx`

**Pendiente (marcado en spec):** middleware de autenticación JWT y verificación de roles.

**Why:** La protección de rutas con middleware requiere definir la estrategia de auth (cookies vs. sessionStorage), que no está especificada en SPEC-011.

**How to apply:** Al implementar el middleware, tomar el token de `sessionStorage` (patrón ya usado en hooks) o bien de cookies HttpOnly si se cambia la estrategia.
