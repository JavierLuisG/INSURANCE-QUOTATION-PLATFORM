'use strict';

// FolioCounter is defined inline inside the route module using mongoose.models / mongoose.model.
// We mock mongoose so the model registration is intercepted and findOneAndUpdate is controllable.
jest.mock('mongoose', () => {
  const findOneAndUpdateMock = jest.fn();
  const modelMock = {
    findOneAndUpdate: findOneAndUpdateMock,
  };

  const Schema = function () {};
  Schema.prototype = {};

  return {
    Schema,
    models: {},
    model: jest.fn(() => modelMock),
    _findOneAndUpdateMock: findOneAndUpdateMock,
  };
});

const mongoose = require('mongoose');
const request = require('supertest');
const express = require('express');
const router = require('../../src/routes/folios');

describe('GET /v1/folios', () => {
  let app;
  const year = new Date().getFullYear();

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/v1', router);
    jest.clearAllMocks();
  });

  it('test_folio_generation_sequential', async () => {
    // GIVEN
    mongoose._findOneAndUpdateMock.mockResolvedValue({ _id: `cotizacion-${year}`, seq: 1 });

    // WHEN
    const res = await request(app).get('/v1/folios');

    // THEN
    expect(res.status).toBe(200);
    expect(res.body).toHaveProperty('folio');
    expect(res.body.folio).toMatch(/^COT-\d{4}-\d{6}$/);
  });

  it('test_folio_increments_on_second_call', async () => {
    // GIVEN
    mongoose._findOneAndUpdateMock
      .mockResolvedValueOnce({ _id: `cotizacion-${year}`, seq: 1 })
      .mockResolvedValueOnce({ _id: `cotizacion-${year}`, seq: 2 });

    // WHEN
    const res1 = await request(app).get('/v1/folios');
    const res2 = await request(app).get('/v1/folios');

    // THEN
    expect(res1.status).toBe(200);
    expect(res2.status).toBe(200);
    expect(res1.body.folio).not.toBe(res2.body.folio);
    expect(res1.body.folio).toBe(`COT-${year}-000001`);
    expect(res2.body.folio).toBe(`COT-${year}-000002`);
  });
});
