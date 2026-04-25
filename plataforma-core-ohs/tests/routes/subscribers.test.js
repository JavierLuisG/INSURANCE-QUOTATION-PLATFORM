'use strict';

jest.mock('../../src/models/Subscriber');
jest.mock('../../src/middleware/mockScenarioInterceptor', () => (req, res, next) => next());

const Subscriber = require('../../src/models/Subscriber');
const request = require('supertest');
const express = require('express');
const router = require('../../src/routes/subscribers');

describe('GET /v1/subscribers', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/v1', router);
    jest.clearAllMocks();
  });

  it('test_subscribers_returns_list', async () => {
    // GIVEN
    const mockSubscribers = [
      { id: 'SUB-001', nombre: 'Grupo Asegurador Azteca S.A. de C.V.', clave: 'GAA', activo: true },
      { id: 'SUB-002', nombre: 'Reaseguradora Nacional de Daños', clave: 'RND', activo: true },
    ];
    Subscriber.find.mockResolvedValue(mockSubscribers);

    // WHEN
    const res = await request(app).get('/v1/subscribers');

    // THEN
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body).toHaveLength(2);
    expect(res.body[0].id).toBe('SUB-001');
  });

  it('test_subscribers_empty_returns_200', async () => {
    // GIVEN
    Subscriber.find.mockResolvedValue([]);

    // WHEN
    const res = await request(app).get('/v1/subscribers');

    // THEN
    expect(res.status).toBe(200);
    expect(res.body).toEqual([]);
  });
});
