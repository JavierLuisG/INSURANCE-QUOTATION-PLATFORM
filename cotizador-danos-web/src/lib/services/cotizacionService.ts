import axios from 'axios';
import type { CotizacionResponse, CotizacionRequest } from '@/lib/schemas/cotizacion.schema';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
});

/**
 * Inicia una nueva cotización.
 * POST /api/v1/cotizaciones
 *
 * @returns La cotización creada con folio asignado y estado INCOMPLETA.
 */
export async function iniciarCotizacion(token: string): Promise<CotizacionResponse> {
  const response = await api.post<CotizacionResponse>(
    '/api/v1/cotizaciones',
    {},
    {
      headers: { Authorization: `Bearer ${token}` },
    }
  );
  return response.data;
}

/**
 * Obtiene los datos generales de una cotización por su folio.
 * GET /api/v1/cotizaciones/{folio}
 */
export async function getCotizacion(folio: string, token: string): Promise<CotizacionResponse> {
  const response = await api.get<CotizacionResponse>(
    `/api/v1/cotizaciones/${encodeURIComponent(folio)}`,
    {
      headers: { Authorization: `Bearer ${token}` },
    }
  );
  return response.data;
}

/**
 * Actualiza los datos generales de una cotización existente.
 * PUT /api/v1/cotizaciones/{folio}
 *
 * Incluye el campo version para versionado optimista (HTTP 409 si hay conflicto).
 */
export async function actualizarCotizacion(
  folio: string,
  data: CotizacionRequest,
  token: string
): Promise<CotizacionResponse> {
  const response = await api.put<CotizacionResponse>(
    `/api/v1/cotizaciones/${encodeURIComponent(folio)}`,
    data,
    {
      headers: { Authorization: `Bearer ${token}` },
    }
  );
  return response.data;
}
