---
name: Patrón de tests de auth — useAuth hook y LoginForm/RegisterForm
description: Convenciones establecidas al generar la suite de auth (hook + 2 componentes)
type: feedback
---

Para los tests del hook `useAuth` se usa MSW con `BASE_URL = ''` (string vacío) para que los interceptores coincidan con las llamadas de Axios cuando `NEXT_PUBLIC_API_URL` no está definido en el entorno de tests.

El store Zustand `useAuthStore` se resetea en cada `beforeEach` con `useAuthStore.setState({ token: null, role: null, email: null })` porque el módulo lo inicializa desde cookies al importarse.

Los tests de los componentes `LoginForm` y `RegisterForm` mockean el módulo `@/hooks/useAuth` completo con `vi.mock`, nunca usan MSW. Así se evita complejidad de red en tests de UI y se delega la prueba de integración real al test del hook.

`makeFakeJwt()` es un helper local definido en `useAuth.test.ts` que genera un JWT de tres partes con payload codificado en base64url-safe mediante `btoa(unescape(encodeURIComponent(JSON.stringify(payload))))`.

**Why:** el módulo `authStore.ts` lee cookies en tiempo de importación, lo que causa contaminación entre tests si no se limpia el store; el mismo issue ya se documentó en el prompt de la tarea.

**How to apply:** cualquier test futuro que importe `useAuthStore` o `useAuth` debe hacer el reset en `beforeEach`. Nuevos tests de componentes de auth deben siempre mockear `@/hooks/useAuth`, no el servicio.
