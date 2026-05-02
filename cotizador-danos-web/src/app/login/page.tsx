'use client';

import { useEffect } from 'react';
import { useRouter } from 'next/navigation';
import LoginForm from '@/components/Auth/LoginForm';
import { useAuth } from '@/hooks/useAuth';

export default function LoginPage() {
  const router = useRouter();
  const { isAuthenticated } = useAuth();

  useEffect(() => {
    if (isAuthenticated) {
      router.replace('/');
    }
  }, [isAuthenticated, router]);

  function handleSuccess() {
    router.push('/');
  }

  return (
    <main className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 px-8 py-10">
          <div className="mb-8 text-center">
            <h1 className="text-2xl font-bold text-gray-900">Iniciar sesión</h1>
            <p className="mt-2 text-sm text-gray-500">
              Accede al cotizador de daños
            </p>
          </div>
          <LoginForm onSuccess={handleSuccess} />
        </div>
      </div>
    </main>
  );
}
