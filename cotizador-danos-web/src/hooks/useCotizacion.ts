import axios from 'axios';
import { useCotizacionStore } from '@/store/cotizacionStore';
import {
  iniciarCotizacion,
  getCotizacion,
  actualizarCotizacion,
} from '@/lib/services/cotizacionService';
import type { CotizacionRequest, CotizacionResponse } from '@/lib/schemas/cotizacion.schema';

/**
 * Hook que orquesta las operaciones CRUD sobre la cotización activa.
 *
 * Consume cotizacionStore para estado global y expone acciones
 * tipadas que encapsulan la gestión de errores.
 */
export function useCotizacion() {
  const { cotizacion, loading, error, setCotizacion, setLoading, setError } =
    useCotizacionStore();

  /**
   * Crea una nueva cotización y la establece como la cotización activa en el store.
   * El token se obtiene desde sessionStorage (patrón de auth del proyecto).
   */
  async function iniciar(): Promise<void> {
    const token = getToken();
    setLoading(true);
    setError(null);
    try {
      const data: CotizacionResponse = await iniciarCotizacion(token);
      setCotizacion(data);
    } catch (err) {
      setError(resolveErrorMessage(err, 'No se pudo iniciar la cotización. Inténtelo de nuevo más tarde.'));
    } finally {
      setLoading(false);
    }
  }

  /**
   * Carga una cotización existente por folio y la establece en el store.
   */
  async function cargar(folio: string): Promise<void> {
    const token = getToken();
    setLoading(true);
    setError(null);
    try {
      const data: CotizacionResponse = await getCotizacion(folio, token);
      setCotizacion(data);
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.status === 404) {
        setError('No se encontró la cotización');
      } else if (axios.isAxiosError(err) && err.response?.status === 403) {
        setError('Acceso denegado: No tiene permisos para esta acción');
      } else {
        setError(resolveErrorMessage(err, 'Error al cargar la cotización'));
      }
    } finally {
      setLoading(false);
    }
  }

  /**
   * Actualiza los datos generales de una cotización.
   * Maneja HTTP 409 (conflicto de versión optimista) con mensaje legible.
   */
  async function actualizar(folio: string, data: CotizacionRequest): Promise<void> {
    const token = getToken();
    setLoading(true);
    setError(null);
    try {
      const updated: CotizacionResponse = await actualizarCotizacion(folio, data, token);
      setCotizacion(updated);
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.status === 409) {
        setError('La cotización fue modificada por otro usuario. Recargue e intente de nuevo.');
      } else if (axios.isAxiosError(err) && err.response?.status === 403) {
        setError('Acceso denegado: No tiene permisos para esta acción');
      } else if (axios.isAxiosError(err) && err.response?.status === 404) {
        setError('No se encontró la cotización');
      } else {
        setError(resolveErrorMessage(err, 'Error al guardar los cambios'));
      }
    } finally {
      setLoading(false);
    }
  }

  return { cotizacion, loading, error, iniciar, cargar, actualizar };
}

// ---------------------------------------------------------------------------
// Helpers privados del módulo
// ---------------------------------------------------------------------------

function getToken(): string {
  if (typeof window !== 'undefined') {
    return sessionStorage.getItem('token') ?? '';
  }
  return '';
}

function resolveErrorMessage(err: unknown, fallback: string): string {
  if (axios.isAxiosError(err) && err.response?.data?.message) {
    return String(err.response.data.message);
  }
  if (err instanceof Error) {
    return err.message;
  }
  return fallback;
}
