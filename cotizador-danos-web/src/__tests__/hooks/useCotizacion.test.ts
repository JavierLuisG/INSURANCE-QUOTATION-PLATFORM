import { describe, it, expect, beforeEach } from 'vitest';
import { renderHook, act } from '@testing-library/react';
import { http, HttpResponse } from 'msw';
import { useCotizacion } from '@/hooks/useCotizacion';
import { useCotizacionStore } from '@/store/cotizacionStore';
import { server } from '../mocks/server';
import { COTIZACION_FIXTURE } from '../mocks/handlers';

const BASE_URL = '';

// ---------------------------------------------------------------------------
// Setup: reset store + token de sesión entre cada test
// ---------------------------------------------------------------------------
beforeEach(() => {
  useCotizacionStore.getState().reset();
  sessionStorage.setItem('token', 'test-token-hook');
});

describe('useCotizacion', () => {
  // ---------------------------------------------------------------------------
  // Test 15: iniciar devuelve cotización con folio asignado
  // ---------------------------------------------------------------------------
  it('iniciar devuelve cotización con folio asignado', async () => {
    // GIVEN — el servidor responde 201 con una cotización nueva (folio asignado)
    server.use(
      http.post(`${BASE_URL}/api/v1/cotizaciones`, () =>
        HttpResponse.json(
          {
            ...COTIZACION_FIXTURE,
            folio: 'COT-2026-000001',
            estadoValidacion: 'INCOMPLETA',
            nombreAsegurado: null,
          },
          { status: 201 }
        )
      )
    );

    const { result } = renderHook(() => useCotizacion());

    // WHEN — se llama a iniciar()
    await act(async () => {
      await result.current.iniciar();
    });

    // THEN — la cotización en el store tiene el folio asignado y estado INCOMPLETA
    expect(result.current.cotizacion).not.toBeNull();
    expect(result.current.cotizacion?.folio).toBe('COT-2026-000001');
    expect(result.current.cotizacion?.estadoValidacion).toBe('INCOMPLETA');
    expect(result.current.error).toBeNull();
  });

  // ---------------------------------------------------------------------------
  // Test 16: cargar actualiza el store con los datos de la cotización
  // ---------------------------------------------------------------------------
  it('cargar actualiza el store con los datos de la cotización', async () => {
    // GIVEN — el servidor devuelve la cotización completa al consultar por folio
    server.use(
      http.get(`${BASE_URL}/api/v1/cotizaciones/:folio`, () =>
        HttpResponse.json(COTIZACION_FIXTURE, { status: 200 })
      )
    );

    const { result } = renderHook(() => useCotizacion());

    // WHEN — se llama a cargar con un folio existente
    await act(async () => {
      await result.current.cargar('COT-2026-000001');
    });

    // THEN — el store refleja exactamente los datos retornados por la API
    expect(result.current.cotizacion?.folio).toBe('COT-2026-000001');
    expect(result.current.cotizacion?.nombreAsegurado).toBe('Juan Pérez García');
    expect(result.current.loading).toBe(false);
    expect(result.current.error).toBeNull();
  });

  // ---------------------------------------------------------------------------
  // Test 17: cargar establece error para folio inexistente
  // ---------------------------------------------------------------------------
  it('cargar establece error para folio inexistente', async () => {
    // GIVEN — el servidor responde 404 para el folio consultado
    server.use(
      http.get(`${BASE_URL}/api/v1/cotizaciones/:folio`, () =>
        HttpResponse.json(
          { message: 'No se encontró la cotización' },
          { status: 404 }
        )
      )
    );

    const { result } = renderHook(() => useCotizacion());

    // WHEN — se llama a cargar con un folio que no existe
    await act(async () => {
      await result.current.cargar('COT-2026-999999');
    });

    // THEN — el error del store contiene el mensaje de folio no encontrado
    expect(result.current.cotizacion).toBeNull();
    expect(result.current.error).toBe('No se encontró la cotización');
    expect(result.current.loading).toBe(false);
  });

  // ---------------------------------------------------------------------------
  // Test 18: actualizar maneja conflicto de versión (409) correctamente
  // ---------------------------------------------------------------------------
  it('actualizar maneja conflicto de versión (409) correctamente', async () => {
    // GIVEN — el servidor responde 409 (versionado optimista, BR-008 / CRITERIO-2.5)
    server.use(
      http.put(`${BASE_URL}/api/v1/cotizaciones/:folio`, () =>
        HttpResponse.json(
          {
            message:
              'La cotización fue modificada por otro usuario. Recargue e intente de nuevo.',
          },
          { status: 409 }
        )
      )
    );

    const { result } = renderHook(() => useCotizacion());

    // WHEN — se llama a actualizar y el backend reporta conflicto de versión
    await act(async () => {
      await result.current.actualizar('COT-2026-000001', {
        nombreAsegurado: 'Juan Pérez',
        version: 0,
      });
    });

    // THEN — el store tiene el mensaje de conflicto de versión y la cotización no cambia
    expect(result.current.error).toBe(
      'La cotización fue modificada por otro usuario. Recargue e intente de nuevo.'
    );
    expect(result.current.cotizacion).toBeNull();
    expect(result.current.loading).toBe(false);
  });
});
