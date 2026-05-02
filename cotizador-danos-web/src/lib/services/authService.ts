import axios from 'axios';
import { useAuthStore } from '@/store/authStore';
import type {
  LoginFormData,
  LoginResponse,
  RegisterFormData,
  RegisterResponse,
} from '@/lib/schemas/auth.schema';

export const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
});

api.interceptors.request.use((config) => {
  const token = useAuthStore.getState().token;
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

export async function loginUser(data: LoginFormData): Promise<LoginResponse> {
  const response = await api.post<LoginResponse>('/api/v1/auth/login', data);
  return response.data;
}

export async function registerUser(data: RegisterFormData): Promise<RegisterResponse> {
  const response = await api.post<RegisterResponse>('/api/v1/auth/register', data);
  return response.data;
}
