'use client';

// app/(ruta)/page.tsx — Plantilla de página con App Router
import { useState, useEffect } from 'react';
import { useQuotationStore } from '@/store/quotationStore';

export default function FeaturePage() {
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    // Cargar datos aquí usando el service correspondiente
    setLoading(false);
  }, []);

  if (loading) {
    return (
      <div className="flex min-h-[200px] items-center justify-center text-gray-500">
        Cargando...
      </div>
    );
  }

  return (
    <main className="mx-auto max-w-4xl px-4 py-8">
      <h1 className="mb-6 text-2xl font-semibold text-gray-900">Feature Page</h1>

      {error && (
        <div className="mb-4 rounded-md border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700">
          {error}
        </div>
      )}

      <div className="rounded-lg bg-white p-6 shadow-sm">
        {/* contenido del feature */}
      </div>
    </main>
  );
}
