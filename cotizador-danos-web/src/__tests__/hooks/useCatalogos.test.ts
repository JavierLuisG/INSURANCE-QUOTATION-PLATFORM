import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook, waitFor } from '@testing-library/react';
import { http, HttpResponse } from 'msw';
import { useCatalogos } from '@/hooks/useCatalogos';
import { server } from '../mocks/server';
import {
  TIPOS_SEGUROS_FIXTURE,
  MONEDAS_FIXTURE,
  CANALES_VENTA_FIXTURE,
} from '../mocks/handlers';

const BASE_URL = '';

// ---------------------------------------------------------------------------
// Setup: token de sesión para el hook (lee de sessionStorage)
// ---------------------------------------------------------------------------
beforeEach(() => {
  sessionStorage.setItem('token', 'test-token-catalogos');
});

describe('useCatalogos', () => {
  // ---------------------------------------------------------------------------
  // Test 19: carga los tres catálogos al montar
  // ---------------------------------------------------------------------------
  it('carga los tres catálogos al montar', async () => {
    // GIVEN — los tres endpoints de catálogos responden exitosamente
    server.use(
      http.get(`${BASE_URL}/api/v1/catalogos/tipoSeguro`, () =>
        HttpResponse.json(TIPOS_SEGUROS_FIXTURE, { status: 200 })
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/moneda`, () =>
        HttpResponse.json(MONEDAS_FIXTURE, { status: 200 })
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/canalVenta`, () =>
        HttpResponse.json(CANALES_VENTA_FIXTURE, { status: 200 })
      )
    );

    // WHEN — el hook se monta (useEffect dispara fetchAll en paralelo)
    const { result } = renderHook(() => useCatalogos());

    // THEN — los tres catálogos se cargan y el estado de error queda en null
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.tiposSeguros).toEqual(TIPOS_SEGUROS_FIXTURE);
    expect(result.current.monedas).toEqual(MONEDAS_FIXTURE);
    expect(result.current.canalesVenta).toEqual(CANALES_VENTA_FIXTURE);
    expect(result.current.error).toBeNull();
  });

  // ---------------------------------------------------------------------------
  // Test 20: maneja error de catálogo individualmente
  // ---------------------------------------------------------------------------
  it('maneja error de catálogo individualmente', async () => {
    // GIVEN — el catálogo de monedas falla con 503; tipoSeguro y canalVenta responden OK
    server.use(
      http.get(`${BASE_URL}/api/v1/catalogos/tipoSeguro`, () =>
        HttpResponse.json(TIPOS_SEGUROS_FIXTURE, { status: 200 })
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/moneda`, () =>
        HttpResponse.json(
          { message: 'Servicio no disponible' },
          { status: 503 }
        )
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/canalVenta`, () =>
        HttpResponse.json(CANALES_VENTA_FIXTURE, { status: 200 })
      )
    );

    // WHEN — el hook se monta; Promise.allSettled permite que los demás continúen
    const { result } = renderHook(() => useCatalogos());

    // THEN — el error queda registrado, pero los catálogos OK sí se cargaron
    await waitFor(() => {
      expect(result.current.loading).toBe(false);
    });

    expect(result.current.tiposSeguros).toEqual(TIPOS_SEGUROS_FIXTURE);
    expect(result.current.canalesVenta).toEqual(CANALES_VENTA_FIXTURE);
    // El catálogo fallido queda vacío
    expect(result.current.monedas).toEqual([]);
    // El error global indica que al menos uno falló
    expect(result.current.error).toBe(
      'No se pudieron cargar las opciones. Intente de nuevo más tarde.'
    );
  });
});
