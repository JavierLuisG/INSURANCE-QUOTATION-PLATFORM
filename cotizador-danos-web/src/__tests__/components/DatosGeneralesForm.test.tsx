import { describe, it, expect, beforeEach } from 'vitest';
import { render, screen, waitFor } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { http, HttpResponse } from 'msw';
import DatosGeneralesForm from '@/components/Cotizador/DatosGeneralesForm';
import { server } from '../mocks/server';
import { COTIZACION_FIXTURE } from '../mocks/handlers';
import { useCotizacionStore } from '@/store/cotizacionStore';

// La URL base que usa Axios en tests (NEXT_PUBLIC_API_URL no definida → cadena vacía)
const BASE_URL = '';

// ---------------------------------------------------------------------------
// Helpers de setup
// ---------------------------------------------------------------------------

/** Resetea el store de Zustand entre tests para garantizar aislamiento */
beforeEach(() => {
  useCotizacionStore.getState().reset();
  // Asegura que sessionStorage no interfiere con el token
  sessionStorage.setItem('token', 'test-token');
});

describe('DatosGeneralesForm', () => {
  // ---------------------------------------------------------------------------
  // Test 13: envía datos correctos al servicio al hacer clic en Guardar
  // ---------------------------------------------------------------------------
  it('envía datos correctos al servicio al hacer clic en Guardar', async () => {
    // GIVEN — cotización pre-cargada, handlers MSW configurados para responder 200
    const user = userEvent.setup();
    let capturedBody: unknown;

    server.use(
      http.put(`${BASE_URL}/api/v1/cotizaciones/:folio`, async ({ request }) => {
        capturedBody = await request.json();
        return HttpResponse.json(
          { ...COTIZACION_FIXTURE, version: 1 },
          { status: 200 }
        );
      }),
      http.get(`${BASE_URL}/api/v1/catalogos/tipoSeguro`, () =>
        HttpResponse.json([], { status: 200 })
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/moneda`, () =>
        HttpResponse.json([], { status: 200 })
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/canalVenta`, () =>
        HttpResponse.json([], { status: 200 })
      )
    );

    render(
      <DatosGeneralesForm
        folio="COT-2026-000001"
        cotizacion={{
          ...COTIZACION_FIXTURE,
          nombreAsegurado: 'Juan Pérez',
          rfcAsegurado: 'PEPJ800326IG0',
        }}
      />
    );

    // WHEN — el usuario hace clic en Guardar sin modificar nada
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN — el servicio fue llamado con los datos correctos incluyendo version
    await waitFor(() => {
      expect(capturedBody).toMatchObject({
        nombreAsegurado: 'Juan Pérez',
        rfcAsegurado: 'PEPJ800326IG0',
        version: 0,
      });
    });
  });

  // ---------------------------------------------------------------------------
  // Test 14: no envía si hay errores de validación
  // ---------------------------------------------------------------------------
  it('no envía si hay errores de validación', async () => {
    // GIVEN — cotización con RFC inválido para forzar error de validación
    const user = userEvent.setup();
    let putCalled = false;

    server.use(
      http.put(`${BASE_URL}/api/v1/cotizaciones/:folio`, () => {
        putCalled = true;
        return HttpResponse.json({}, { status: 200 });
      }),
      http.get(`${BASE_URL}/api/v1/catalogos/tipoSeguro`, () =>
        HttpResponse.json([], { status: 200 })
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/moneda`, () =>
        HttpResponse.json([], { status: 200 })
      ),
      http.get(`${BASE_URL}/api/v1/catalogos/canalVenta`, () =>
        HttpResponse.json([], { status: 200 })
      )
    );

    render(
      <DatosGeneralesForm
        folio="COT-2026-000001"
        cotizacion={null}
      />
    );

    // WHEN — el usuario escribe un RFC inválido y presiona Guardar
    const rfcInput = screen.getByLabelText(/rfc/i);
    await user.type(rfcInput, 'RFCINVALIDO123');
    await user.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN — el error de validación se muestra y el servicio NO fue llamado
    await waitFor(() => {
      expect(screen.getByRole('alert')).toHaveTextContent(
        /formato del rfc introducido no es válido/i
      );
    });
    expect(putCalled).toBe(false);
  });
});
