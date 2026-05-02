'use client';

import { useRouter } from 'next/navigation';
import RegisterForm from '@/components/Auth/RegisterForm';

export default function RegisterPage() {
  const router = useRouter();

  function handleSuccess() {
    router.push('/login');
  }

  return (
    <main className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-12">
      <div className="w-full max-w-md">
        <div className="bg-white rounded-2xl shadow-sm border border-gray-200 px-8 py-10">
          <div className="mb-8 text-center">
            <h1 className="text-2xl font-bold text-gray-900">Crear cuenta</h1>
            <p className="mt-2 text-sm text-gray-500">
              Regístrate para acceder al cotizador de daños
            </p>
          </div>
          <RegisterForm onSuccess={handleSuccess} />
        </div>
      </div>
    </main>
  );
}
