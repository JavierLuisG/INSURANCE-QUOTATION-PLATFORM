import axios from 'axios';
import type { CatalogoItem } from '@/lib/schemas/cotizacion.schema';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL,
});

export type TipoCatalogo = 'tipoSeguro' | 'moneda' | 'canalVenta';

/**
 * Obtiene los ítems de un catálogo desde Plataforma-core-ohs.
 * GET /api/v1/catalogos/{tipo}
 *
 * @param tipo - Tipo de catálogo a consultar.
 * @param token - JWT Bearer token del usuario autenticado.
 * @returns Lista de ítems del catálogo.
 */
export async function getCatalogos(tipo: TipoCatalogo, token: string): Promise<CatalogoItem[]> {
  const response = await api.get<CatalogoItem[]>(`/api/v1/catalogos/${tipo}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  return response.data;
}
