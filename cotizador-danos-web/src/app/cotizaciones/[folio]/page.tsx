'use client';

import { useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import { useCotizacion } from '@/hooks/useCotizacion';
import CotizadorHeader from '@/components/Cotizador/CotizadorHeader';
import DatosGeneralesForm from '@/components/Cotizador/DatosGeneralesForm';
import FolioSearchBar from '@/components/Cotizador/FolioSearchBar';

/**
 * Página: Editar Cotización (/cotizaciones/[folio])
 *
 * Al montar carga la cotización identificada por `params.folio`.
 * Permite buscar otra cotización mediante FolioSearchBar.
 * Protegida por rol (agente_ventas, vendedor, administrador_ventas, editor_cotizaciones).
 *
 * Nota de autenticación: la protección de rol se gestiona en el middleware
 * de Next.js (middleware.ts). Esta página asume que el usuario ya está autenticado.
 */
export default function EditarCotizacionPage() {
  const params = useParams<{ folio: string }>();
  const router = useRouter();
  const folio = decodeURIComponent(params.folio ?? '');

  const { cotizacion, loading, error, cargar } = useCotizacion();

  useEffect(() => {
    if (folio) {
      cargar(folio);
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [folio]);

  function handleLoad(newFolio: string) {
    router.push(`/cotizaciones/${encodeURIComponent(newFolio)}`);
  }

  return (
    <main className="min-h-screen bg-gray-50 px-4 py-8">
      <div className="mx-auto max-w-3xl space-y-6">
        <div className="flex flex-col gap-2">
          <h1 className="text-xl font-bold text-gray-900">Editar Cotización</h1>
          <FolioSearchBar onLoad={handleLoad} />
        </div>

        {loading && !cotizacion && (
          <div className="flex items-center justify-center py-12">
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
              <p className="text-sm text-gray-500">Cargando cotización...</p>
            </div>
          </div>
        )}

        {error && !cotizacion && (
          <div
            className="rounded-lg border border-red-200 bg-red-50 px-4 py-3 text-sm text-red-700"
            role="alert"
          >
            {error}
          </div>
        )}

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
