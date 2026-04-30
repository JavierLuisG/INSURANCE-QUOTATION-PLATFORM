'use client';

import { useEffect } from 'react';
import { useCotizacion } from '@/hooks/useCotizacion';
import CotizadorHeader from '@/components/Cotizador/CotizadorHeader';
import DatosGeneralesForm from '@/components/Cotizador/DatosGeneralesForm';

/**
 * Página: Nueva Cotización (/cotizaciones/nueva)
 *
 * Al montar llama a `iniciar()` para crear una cotización con folio automático
 * desde el Servicio de Folios. Protegida por rol (creador_cotizacion, agente_ventas).
 *
 * Nota de autenticación: la protección de rol se gestiona en el middleware
 * de Next.js (middleware.ts). Esta página asume que el usuario ya está autenticado.
 */
export default function NuevaCotizacionPage() {
  const { cotizacion, loading, error, iniciar } = useCotizacion();

  useEffect(() => {
    iniciar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  if (loading && !cotizacion) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="flex flex-col items-center gap-3">
          <svg
            className="h-8 w-8 animate-spin text-blue-600"
            xmlns="http://www.w3.org/2000/svg"
            fill="none"
            viewBox="0 0 24 24"
            aria-hidden="true"
          >
            <circle
              className="opacity-25"
              cx="12"
              cy="12"
              r="10"
              stroke="currentColor"
              strokeWidth="4"
            />
            <path
              className="opacity-75"
              fill="currentColor"
              d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
            />
          </svg>
          <p className="text-sm text-gray-500">Iniciando cotización...</p>
        </div>
      </main>
    );
  }

  if (error && !cotizacion) {
    return (
      <main className="flex min-h-screen items-center justify-center bg-gray-50">
        <div className="max-w-md rounded-lg border border-red-200 bg-red-50 p-6 text-center">
          <p className="text-sm font-medium text-red-700">{error}</p>
          <button
            type="button"
            onClick={() => iniciar()}
            className="mt-4 rounded-md bg-red-600 px-4 py-2 text-sm font-medium text-white
                       hover:bg-red-700 focus:outline-none focus:ring-2 focus:ring-red-500"
          >
            Reintentar
          </button>
        </div>
      </main>
    );
  }

  return (
    <main className="min-h-screen bg-gray-50 px-4 py-8">
      <div className="mx-auto max-w-3xl space-y-6">
        <h1 className="text-xl font-bold text-gray-900">Nueva Cotización</h1>

        {cotizacion && (
          <>
            <CotizadorHeader
              folio={cotizacion.folio}
              estadoValidacion={cotizacion.estadoValidacion}
            />
            <DatosGeneralesForm folio={cotizacion.folio} cotizacion={cotizacion} />
          </>
        )}
      </div>
    </main>
  );
}
