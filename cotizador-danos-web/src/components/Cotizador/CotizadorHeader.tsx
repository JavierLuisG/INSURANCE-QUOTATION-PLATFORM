'use client';

import type { EstadoValidacion } from '@/lib/schemas/cotizacion.schema';

interface CotizadorHeaderProps {
  folio: string;
  estadoValidacion: string;
}

const estadoBadgeClasses: Record<EstadoValidacion, string> = {
  INCOMPLETA: 'bg-yellow-100 text-yellow-800',
  COMPLETA: 'bg-green-100 text-green-800',
  INACTIVA: 'bg-red-100 text-red-800',
};

const estadoLabel: Record<EstadoValidacion, string> = {
  INCOMPLETA: 'Incompleta',
  COMPLETA: 'Completa',
  INACTIVA: 'Inactiva',
};

/**
 * Encabezado del cotizador.
 *
 * Muestra el folio como campo read-only (deshabilitado para edición)
 * y un badge de color según el estadoValidacion de la cotización.
 */
export default function CotizadorHeader({ folio, estadoValidacion }: CotizadorHeaderProps) {
  const estado = estadoValidacion as EstadoValidacion;
  const badgeClass = estadoBadgeClasses[estado] ?? 'bg-gray-100 text-gray-800';
  const label = estadoLabel[estado] ?? estadoValidacion;

  return (
    <div className="flex flex-col gap-3 rounded-lg border border-gray-200 bg-white p-4 shadow-sm sm:flex-row sm:items-center sm:justify-between">
      <div className="flex flex-col gap-1">
        <label
          htmlFor="folio-display"
          className="text-xs font-medium uppercase tracking-wide text-gray-500"
        >
          Folio de Cotización
        </label>
        <input
          id="folio-display"
          type="text"
          value={folio}
          disabled
          readOnly
          aria-label="Folio de cotización (no editable)"
          className="w-48 rounded-md border border-gray-200 bg-gray-50 px-3 py-1.5 text-sm
                     font-mono text-gray-700 cursor-not-allowed select-all"
        />
      </div>

      <div className="flex items-center gap-2">
        <span className="text-xs font-medium text-gray-500">Estado:</span>
        <span
          className={`inline-flex items-center rounded-full px-3 py-1 text-xs font-semibold ${badgeClass}`}
        >
          {label}
        </span>
      </div>
    </div>
  );
}
