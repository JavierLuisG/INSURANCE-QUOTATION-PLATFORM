import { create } from 'zustand';

interface AuthStore {
  token: string | null;
  role: string | null;
  email: string | null;
  setAuth: (token: string, role: string, email: string) => void;
  clearAuth: () => void;
}

function readCookieToken(): { token: string; role: string; email: string } | null {
  if (typeof document === 'undefined') return null;
  const match = document.cookie.split(';').find((c) => c.trim().startsWith('auth-token='));
  if (!match) return null;
  const token = match.trim().slice('auth-token='.length);
  if (!token) return null;
  try {
    const payload = JSON.parse(atob(token.split('.')[1]));
    const exp: number = payload.exp ?? 0;
    if (exp * 1000 < Date.now()) return null;
    const roles: unknown = payload.roles;
    const role = Array.isArray(roles) ? String(roles[0]) : String(roles ?? '');
    const email = String(payload.sub ?? '');
    return { token, role, email };
  } catch {
    return null;
  }
}

const stored = readCookieToken();

export const useAuthStore = create<AuthStore>((set) => ({
  token: stored?.token ?? null,
  role: stored?.role ?? null,
  email: stored?.email ?? null,

  setAuth: (token, role, email) => {
    if (typeof document !== 'undefined') {
      document.cookie = `auth-token=${token}; path=/; max-age=86400; SameSite=Strict`;
    }
    set({ token, role, email });
  },

  clearAuth: () => {
    if (typeof document !== 'undefined') {
      document.cookie = 'auth-token=; path=/; max-age=0';
    }
    set({ token: null, role: null, email: null });
  },
}));
