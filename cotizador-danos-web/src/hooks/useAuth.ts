import { useAuthStore } from '@/store/authStore';
import { loginUser, registerUser } from '@/lib/services/authService';
import type { LoginFormData, RegisterFormData } from '@/lib/schemas/auth.schema';

interface JwtPayload {
  sub: string;
  roles: string[];
  iat: number;
  exp: number;
}

function decodeJwtPayload(token: string): JwtPayload {
  const parts = token.split('.');
  if (parts.length !== 3) {
    throw new Error('Token JWT inválido');
  }
  const base64Payload = parts[1].replace(/-/g, '+').replace(/_/g, '/');
  const jsonPayload = atob(base64Payload);
  return JSON.parse(jsonPayload) as JwtPayload;
}

export function useAuth() {
  const { token, role, email, setAuth, clearAuth } = useAuthStore();

  const isAuthenticated = token !== null;

  async function login(data: LoginFormData): Promise<void> {
    const response = await loginUser(data);
    const payload = decodeJwtPayload(response.token);
    const userEmail = payload.sub;
    const userRole = Array.isArray(payload.roles) && payload.roles.length > 0
      ? payload.roles[0]
      : '';
    setAuth(response.token, userRole, userEmail);
  }

  async function register(data: RegisterFormData): Promise<string> {
    const response = await registerUser(data);
    return response.message;
  }

  function logout(): void {
    clearAuth();
  }

  return {
    token,
    role,
    email,
    isAuthenticated,
    login,
    register,
    logout,
  };
}
