import { http, HttpResponse } from 'msw';

const BASE_URL = 'http://localhost:3000';

/** Cotización de ejemplo usada en los handlers por defecto */
export const COTIZACION_FIXTURE = {
  folio: 'COT-2026-000001',
  estadoValidacion: 'INCOMPLETA' as const,
  nombreAsegurado: 'Juan Pérez García',
  rfcAsegurado: 'PEPJ800326IG0',
  tipoSeguroId: 'tipo-001',
  monedaId: 'moneda-001',
  canalVentaId: 'canal-001',
  fechaInicioVigencia: '2026-05-01',
  fechaFinVigencia: '2026-05-31',
  createdAt: '2026-04-29T10:00:00Z',
  updatedAt: '2026-04-29T10:00:00Z',
  version: 0,
};

/** Catálogos de ejemplo */
export const TIPOS_SEGUROS_FIXTURE = [
  { id: 'tipo-001', nombre: 'Automóvil', activo: true },
  { id: 'tipo-002', nombre: 'Hogar', activo: true },
];

export const MONEDAS_FIXTURE = [
  { id: 'moneda-001', nombre: 'MXN', activo: true },
  { id: 'moneda-002', nombre: 'USD', activo: true },
];

export const CANALES_VENTA_FIXTURE = [
  { id: 'canal-001', nombre: 'Agente', activo: true },
  { id: 'canal-002', nombre: 'Digital', activo: true },
];

export const handlers = [
  // POST /api/v1/cotizaciones — iniciar cotización
  http.post(`${BASE_URL}/api/v1/cotizaciones`, () => {
    return HttpResponse.json(
      {
        ...COTIZACION_FIXTURE,
        nombreAsegurado: null,
        rfcAsegurado: null,
        tipoSeguroId: null,
        monedaId: null,
        canalVentaId: null,
        fechaInicioVigencia: null,
        fechaFinVigencia: null,
      },
      { status: 201 }
    );
  }),

  // GET /api/v1/cotizaciones/:folio — obtener cotización
  http.get(`${BASE_URL}/api/v1/cotizaciones/:folio`, ({ params }) => {
    const { folio } = params;
    if (folio === 'COT-2026-999999') {
      return HttpResponse.json(
        { message: 'No se encontró la cotización' },
        { status: 404 }
      );
    }
    return HttpResponse.json(COTIZACION_FIXTURE, { status: 200 });
  }),

  // PUT /api/v1/cotizaciones/:folio — actualizar cotización
  http.put(`${BASE_URL}/api/v1/cotizaciones/:folio`, () => {
    return HttpResponse.json(
      { ...COTIZACION_FIXTURE, version: 1, updatedAt: '2026-04-29T11:00:00Z' },
      { status: 200 }
    );
  }),

  // GET /api/v1/catalogos/tipoSeguro
  http.get(`${BASE_URL}/api/v1/catalogos/tipoSeguro`, () => {
    return HttpResponse.json(TIPOS_SEGUROS_FIXTURE, { status: 200 });
  }),

  // GET /api/v1/catalogos/moneda
  http.get(`${BASE_URL}/api/v1/catalogos/moneda`, () => {
    return HttpResponse.json(MONEDAS_FIXTURE, { status: 200 });
  }),

  // GET /api/v1/catalogos/canalVenta
  http.get(`${BASE_URL}/api/v1/catalogos/canalVenta`, () => {
    return HttpResponse.json(CANALES_VENTA_FIXTURE, { status: 200 });
  }),
];
