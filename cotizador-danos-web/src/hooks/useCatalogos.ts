import { useState, useEffect } from 'react';
import { getCatalogos } from '@/lib/services/catalogoService';
import type { CatalogoItem } from '@/lib/schemas/cotizacion.schema';

interface CatalogosState {
  tiposSeguros: CatalogoItem[];
  monedas: CatalogoItem[];
  canalesVenta: CatalogoItem[];
  loading: boolean;
  error: string | null;
}

/**
 * Hook que carga los tres catálogos de forma asíncrona e independiente.
 *
 * Cada catálogo se carga en paralelo; si uno falla el error queda en `error`
 * pero los demás se siguen intentando (Promise.allSettled).
 */
export function useCatalogos(): CatalogosState {
  const [tiposSeguros, setTiposSeguros] = useState<CatalogoItem[]>([]);
  const [monedas, setMonedas] = useState<CatalogoItem[]>([]);
  const [canalesVenta, setCanalesVenta] = useState<CatalogoItem[]>([]);
  const [loading, setLoading] = useState<boolean>(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let cancelled = false;

    async function fetchAll() {
      setLoading(true);
      setError(null);

      const token = typeof window !== 'undefined'
        ? (sessionStorage.getItem('token') ?? '')
        : '';

      const [tiposResult, monedasResult, canalesResult] = await Promise.allSettled([
        getCatalogos('tipoSeguro', token),
        getCatalogos('moneda', token),
        getCatalogos('canalVenta', token),
      ]);

      if (cancelled) return;

      const errors: string[] = [];

      if (tiposResult.status === 'fulfilled') {
        setTiposSeguros(tiposResult.value);
      } else {
        errors.push('tipoSeguro');
      }

      if (monedasResult.status === 'fulfilled') {
        setMonedas(monedasResult.value);
      } else {
        errors.push('moneda');
      }

      if (canalesResult.status === 'fulfilled') {
        setCanalesVenta(canalesResult.value);
      } else {
        errors.push('canalVenta');
      }

      if (errors.length > 0) {
        setError('No se pudieron cargar las opciones. Intente de nuevo más tarde.');
      }

      setLoading(false);
    }

    fetchAll();

    return () => {
      cancelled = true;
    };
  }, []);

  return { tiposSeguros, monedas, canalesVenta, loading, error };
}
