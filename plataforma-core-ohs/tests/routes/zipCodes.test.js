'use strict';

jest.mock('../../src/models/ZipCode');
jest.mock('../../src/middleware/mockScenarioInterceptor', () => (req, res, next) => next());

const ZipCode = require('../../src/models/ZipCode');
const request = require('supertest');
const express = require('express');
const router = require('../../src/routes/zipCodes');

describe('GET /v1/zip-codes/:zipCode', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/v1', router);
    jest.clearAllMocks();
  });

  it('test_zip_code_valid_returns_zona', async () => {
    // GIVEN
    const mockZipCode = {
      codigoPostal: '06600',
      zonaCAT: 'ZONA_A',
      nivelTecnico: 'ALTO',
      estado: 'Ciudad de México',
      municipio: 'Cuauhtémoc',
      ciudad: 'Ciudad de México',
    };
    ZipCode.findOne.mockResolvedValue(mockZipCode);

    // WHEN
    const res = await request(app).get('/v1/zip-codes/06600');

    // THEN
    expect(res.status).toBe(200);
    expect(res.body.codigoPostal).toBe('06600');
    expect(res.body.zonaCAT).toBe('ZONA_A');
  });

  it('test_zip_code_not_found_returns_404', async () => {
    // GIVEN
    ZipCode.findOne.mockResolvedValue(null);

    // WHEN
    const res = await request(app).get('/v1/zip-codes/99999');

    // THEN
    expect(res.status).toBe(404);
    expect(res.body.code).toBe('ZIP_NOT_FOUND');
  });

  it('test_zip_code_invalid_format_returns_400', async () => {
    // GIVEN
    // No DB call needed — validation rejects before reaching the model

    // WHEN
    const res = await request(app).get('/v1/zip-codes/ABCDE');

    // THEN
    expect(res.status).toBe(400);
    expect(res.body.code).toBe('INVALID_ZIP_FORMAT');
  });
});

describe('POST /v1/zip-codes/validate', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/v1', router);
    jest.clearAllMocks();
  });

  it('test_zip_codes_validate_batch', async () => {
    // GIVEN
    const foundZipCodes = [
      {
        codigoPostal: '06600',
        zonaCAT: 'ZONA_A',
        nivelTecnico: 'ALTO',
        estado: 'Ciudad de México',
        municipio: 'Cuauhtémoc',
      },
    ];
    ZipCode.find.mockResolvedValue(foundZipCodes);

    // WHEN
    const res = await request(app)
      .post('/v1/zip-codes/validate')
      .send({ zipCodes: ['06600', '99999'] });

    // THEN
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('results');
    expect(res.body.results).toHaveLength(2);
    expect(res.body.results[0].codigoPostal).toBe('06600');
    expect(res.body.results[0].valido).toBe(true);
    expect(res.body.results[1].codigoPostal).toBe('99999');
    expect(res.body.results[1].valido).toBe(false);
  });

  it('test_zip_codes_validate_missing_body_returns_400', async () => {
    // GIVEN
    // No zipCodes array in body

    // WHEN
    const res = await request(app)
      .post('/v1/zip-codes/validate')
      .send({});

    // THEN
    expect(res.status).toBe(400);
    expect(res.body.code).toBe('INVALID_REQUEST');
  });
});
