import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { http, HttpResponse } from 'msw';
import { server } from '../mocks/server';
import { useAuth } from '@/hooks/useAuth';
import { useAuthStore } from '@/store/authStore';

// ─── JWT fixture helper ──────────────────────────────────────────────────────
// Genera un JWT falso con payload codificado en base64url-safe.
// La firma no se valida en los tests: cualquier string sirve como tercera parte.
function makeFakeJwt(overrides?: {
  sub?: string;
  roles?: string[];
  exp?: number;
  iat?: number;
}): string {
  const now = Math.floor(Date.now() / 1000);
  const payload = {
    sub: overrides?.sub ?? 'test@example.com',
    roles: overrides?.roles ?? ['agente_ventas'],
    exp: overrides?.exp ?? now + 86400,
    iat: overrides?.iat ?? now,
  };
  const header = btoa(JSON.stringify({ alg: 'HS256', typ: 'JWT' }));
  const body = btoa(unescape(encodeURIComponent(JSON.stringify(payload))));
  return `${header}.${body}.fake-signature`;
}

// ─── Constantes ──────────────────────────────────────────────────────────────
const BASE_URL = '';

// ─── Setup ───────────────────────────────────────────────────────────────────
beforeEach(() => {
  // Resetear el store Zustand entre tests para garantizar aislamiento.
  // El store se inicializa desde la cookie al importar el módulo; al forzar
  // el estado a null evitamos que un test contamine al siguiente.
  useAuthStore.setState({ token: null, role: null, email: null });
});

// ─── Suite ───────────────────────────────────────────────────────────────────
describe('useAuth', () => {
  // ── Estado inicial ─────────────────────────────────────────────────────────
  it('isAuthenticated es false con store vacío', () => {
    // GIVEN — store limpio establecido en beforeEach
    const { result } = renderHook(() => useAuth());

    // WHEN — no hay interacción; se lee el estado inicial

    // THEN
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.token).toBeNull();
    expect(result.current.role).toBeNull();
    expect(result.current.email).toBeNull();
  });

  // ── login — happy path ─────────────────────────────────────────────────────
  it('login almacena token y marca isAuthenticated en éxito', async () => {
    // GIVEN
    const fakeToken = makeFakeJwt({ sub: 'user@example.com', roles: ['agente_ventas'] });
    server.use(
      http.post(`${BASE_URL}/api/v1/auth/login`, () =>
        HttpResponse.json({ token: fakeToken, expiresIn: 86400 }, { status: 200 })
      )
    );
    const { result } = renderHook(() => useAuth());

    // WHEN
    await act(async () => {
      await result.current.login({ email: 'user@example.com', password: 'Password1' });
    });

    // THEN
    expect(result.current.isAuthenticated).toBe(true);
    expect(result.current.token).toBe(fakeToken);
    expect(result.current.role).toBe('agente_ventas');
    expect(result.current.email).toBe('user@example.com');
  });

  // ── login — error path ─────────────────────────────────────────────────────
  it('login no modifica store en error de credenciales inválidas', async () => {
    // GIVEN
    server.use(
      http.post(`${BASE_URL}/api/v1/auth/login`, () =>
        HttpResponse.json({ message: 'Credenciales inválidas' }, { status: 401 })
      )
    );
    const { result } = renderHook(() => useAuth());

    // WHEN
    await act(async () => {
      await expect(
        result.current.login({ email: 'wrong@example.com', password: 'BadPass1' })
      ).rejects.toThrow();
    });

    // THEN
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.token).toBeNull();
  });

  // ── register — happy path ──────────────────────────────────────────────────
  it('register retorna mensaje de éxito', async () => {
    // GIVEN
    server.use(
      http.post(`${BASE_URL}/api/v1/auth/register`, () =>
        HttpResponse.json({ message: 'Usuario registrado correctamente' }, { status: 201 })
      )
    );
    const { result } = renderHook(() => useAuth());

    // WHEN
    let message = '';
    await act(async () => {
      message = await result.current.register({
        email: 'nuevo@example.com',
        password: 'Password1',
        role: 'agente_ventas',
      });
    });

    // THEN
    expect(message).toBe('Usuario registrado correctamente');
    // El store no debe cambiar: register no hace login automático
    expect(result.current.isAuthenticated).toBe(false);
  });

  // ── logout ────────────────────────────────────────────────────────────────
  it('logout limpia el store', async () => {
    // GIVEN — inyectar un token previo directamente en el store
    const fakeToken = makeFakeJwt();
    useAuthStore.setState({ token: fakeToken, role: 'agente_ventas', email: 'user@example.com' });
    const { result } = renderHook(() => useAuth());
    expect(result.current.isAuthenticated).toBe(true);

    // WHEN
    act(() => {
      result.current.logout();
    });

    // THEN
    expect(result.current.isAuthenticated).toBe(false);
    expect(result.current.token).toBeNull();
    expect(result.current.role).toBeNull();
    expect(result.current.email).toBeNull();
  });
});
