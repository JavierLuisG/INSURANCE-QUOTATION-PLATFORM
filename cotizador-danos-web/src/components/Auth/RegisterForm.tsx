'use client';

import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import Link from 'next/link';
import { useAuth } from '@/hooks/useAuth';
import { registerSchema, VALID_ROLES, type RegisterFormData } from '@/lib/schemas/auth.schema';

const ROLE_LABELS: Record<string, string> = {
  creador_cotizacion: 'Creador de cotización',
  agente_ventas: 'Agente de ventas',
  vendedor: 'Vendedor',
  administrador_ventas: 'Administrador de ventas',
  editor_cotizaciones: 'Editor de cotizaciones',
};

interface RegisterFormProps {
  onSuccess: () => void;
}

export default function RegisterForm({ onSuccess }: RegisterFormProps) {
  const { register: registerUser } = useAuth();
  const [serverError, setServerError] = useState<string | null>(null);
  const [successMessage, setSuccessMessage] = useState<string | null>(null);

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
  } = useForm<RegisterFormData>({
    resolver: zodResolver(registerSchema),
  });

  async function onSubmit(data: RegisterFormData) {
    setServerError(null);
    setSuccessMessage(null);
    try {
      const message = await registerUser(data);
      setSuccessMessage(message);
      onSuccess();
    } catch (err: unknown) {
      const axiosError = err as { response?: { data?: { message?: string } } };
      const apiMessage = axiosError?.response?.data?.message;
      if (apiMessage) {
        setServerError(apiMessage);
      } else {
        setServerError('Error al registrar el usuario. Intenta de nuevo.');
      }
    }
  }

  return (
    <form onSubmit={handleSubmit(onSubmit)} noValidate className="space-y-5">
      <div>
        <label
          htmlFor="email"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Correo electrónico
        </label>
        <input
          id="email"
          type="email"
          autoComplete="email"
          {...register('email')}
          className="w-full rounded-lg border border-gray-300 px-4 py-2.5 text-sm text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50"
          placeholder="correo@ejemplo.com"
          disabled={isSubmitting}
        />
        {errors.email && (
          <p className="mt-1 text-xs text-red-600">{errors.email.message}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="password"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Contraseña
        </label>
        <input
          id="password"
          type="password"
          autoComplete="new-password"
          {...register('password')}
          className="w-full rounded-lg border border-gray-300 px-4 py-2.5 text-sm text-gray-900 placeholder-gray-400 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50"
          placeholder="••••••••"
          disabled={isSubmitting}
        />
        {errors.password && (
          <p className="mt-1 text-xs text-red-600">{errors.password.message}</p>
        )}
      </div>

      <div>
        <label
          htmlFor="role"
          className="block text-sm font-medium text-gray-700 mb-1"
        >
          Rol
        </label>
        <select
          id="role"
          {...register('role')}
          className="w-full rounded-lg border border-gray-300 px-4 py-2.5 text-sm text-gray-900 focus:border-blue-500 focus:outline-none focus:ring-1 focus:ring-blue-500 disabled:opacity-50 bg-white"
          disabled={isSubmitting}
        >
          <option value="">Selecciona un rol</option>
          {VALID_ROLES.map((rol) => (
            <option key={rol} value={rol}>
              {ROLE_LABELS[rol]}
            </option>
          ))}
        </select>
        {errors.role && (
          <p className="mt-1 text-xs text-red-600">{errors.role.message}</p>
        )}
      </div>

      {serverError && (
        <div className="rounded-lg bg-red-50 border border-red-200 px-4 py-3">
          <p className="text-sm text-red-700">{serverError}</p>
        </div>
      )}

      {successMessage && (
        <div className="rounded-lg bg-green-50 border border-green-200 px-4 py-3">
          <p className="text-sm text-green-700">{successMessage}</p>
        </div>
      )}

      <button
        type="submit"
        disabled={isSubmitting}
        className="w-full rounded-lg bg-blue-600 px-4 py-2.5 text-sm font-semibold text-white hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed transition-colors"
      >
        {isSubmitting ? 'Registrando...' : 'Crear cuenta'}
      </button>

      <p className="text-center text-sm text-gray-600">
        ¿Ya tienes cuenta?{' '}
        <Link
          href="/login"
          className="font-medium text-blue-600 hover:text-blue-500 hover:underline"
        >
          Inicia sesión
        </Link>
      </p>
    </form>
  );
}
