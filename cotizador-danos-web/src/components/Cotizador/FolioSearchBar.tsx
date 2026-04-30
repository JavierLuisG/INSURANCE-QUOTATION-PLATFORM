'use client';

import { useState } from 'react';
import { z } from 'zod';
import { FOLIO_REGEX } from '@/lib/schemas/cotizacion.schema';

const folioSchema = z.string().regex(FOLIO_REGEX, 'Formato de folio inválido. Use el formato COT-AAAA-NNNNNN');

interface FolioSearchBarProps {
  onLoad: (folio: string) => void;
}

/**
 * Barra de búsqueda de cotización por folio.
 *
 * Valida el formato del folio con Zod antes de invocar `onLoad`.
 * Si el formato es incorrecto muestra el error inline debajo del input.
 */
export default function FolioSearchBar({ onLoad }: FolioSearchBarProps) {
  const [value, setValue] = useState('');
  const [error, setError] = useState<string | null>(null);

  function handleLoad() {
    const result = folioSchema.safeParse(value.trim());
    if (!result.success) {
      setError(result.error.issues[0].message);
      return;
    }
    setError(null);
    onLoad(result.data);
  }

  function handleKeyDown(e: React.KeyboardEvent<HTMLInputElement>) {
    if (e.key === 'Enter') {
      handleLoad();
    }
  }

  return (
    <div className="flex flex-col gap-1">
      <div className="flex gap-2">
        <input
          type="text"
          value={value}
          onChange={(e) => {
            setValue(e.target.value);
            if (error) setError(null);
          }}
          onKeyDown={handleKeyDown}
          placeholder="COT-2026-000001"
          aria-label="Buscar cotización por folio"
          className="flex-1 rounded-md border border-gray-300 px-3 py-2 text-sm shadow-sm
                     placeholder:text-gray-400 focus:border-blue-500 focus:outline-none
                     focus:ring-1 focus:ring-blue-500"
        />
        <button
          type="button"
          onClick={handleLoad}
          className="rounded-md bg-blue-600 px-4 py-2 text-sm font-medium text-white
                     hover:bg-blue-700 focus:outline-none focus:ring-2 focus:ring-blue-500
                     focus:ring-offset-2 disabled:opacity-50"
        >
          Cargar
        </button>
      </div>
      {error && (
        <p className="text-sm text-red-600" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}
