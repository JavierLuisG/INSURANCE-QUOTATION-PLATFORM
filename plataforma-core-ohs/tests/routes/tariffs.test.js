'use strict';

jest.mock('../../src/models/TariffFire');
jest.mock('../../src/models/TariffCat');
jest.mock('../../src/models/TariffElectronicEquipment');
jest.mock('../../src/middleware/mockScenarioInterceptor', () => (req, res, next) => next());

const TariffFire = require('../../src/models/TariffFire');
const TariffCat = require('../../src/models/TariffCat');
const request = require('supertest');
const express = require('express');
const router = require('../../src/routes/tariffs');

describe('GET /v1/tariffs/fire', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/v1', router);
    jest.clearAllMocks();
  });

  it('test_tariffs_fire_returns_list', async () => {
    // GIVEN
    const mockTariffs = [
      { zonaRiesgo: 'ZONA_A', tipoConstructivo: 'TIPO_1', tasaBase: 0.025, factorRecargo: 1.2 },
      { zonaRiesgo: 'ZONA_B', tipoConstructivo: 'TIPO_2', tasaBase: 0.030, factorRecargo: 1.3 },
    ];
    TariffFire.find.mockResolvedValue(mockTariffs);

    // WHEN
    const res = await request(app).get('/v1/tariffs/fire');

    // THEN
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body).toHaveLength(2);
  });

  it('test_tariffs_fire_empty_returns_200', async () => {
    // GIVEN
    TariffFire.find.mockResolvedValue([]);

    // WHEN
    const res = await request(app).get('/v1/tariffs/fire');

    // THEN
    expect(res.status).toBe(200);
    expect(res.body).toEqual([]);
  });
});

describe('GET /v1/tariffs/cat', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/v1', router);
    jest.clearAllMocks();
  });

  it('test_tariffs_cat_zone_not_found_returns_404', async () => {
    // GIVEN
    TariffCat.findOne.mockResolvedValue(null);

    // WHEN
    const res = await request(app).get('/v1/tariffs/cat?zona=ZONA_INEXISTENTE');

    // THEN
    expect(res.status).toBe(404);
    expect(res.body.code).toBe('TARIFF_NOT_FOUND');
  });

  it('test_tariffs_cat_returns_tariff_for_zone', async () => {
    // GIVEN
    const mockTariff = { zona: 'ZONA_A', tasaBase: 0.005, factorEspecial: 1.1 };
    TariffCat.findOne.mockResolvedValue(mockTariff);

    // WHEN
    const res = await request(app).get('/v1/tariffs/cat?zona=ZONA_A');

    // THEN
    expect(res.status).toBe(200);
    expect(res.body.zona).toBe('ZONA_A');
  });

  it('test_tariffs_cat_missing_zona_returns_400', async () => {
    // GIVEN
    // zona query param omitted

    // WHEN
    const res = await request(app).get('/v1/tariffs/cat');

    // THEN
    expect(res.status).toBe(400);
    expect(res.body.code).toBe('INVALID_REQUEST');
  });
});
