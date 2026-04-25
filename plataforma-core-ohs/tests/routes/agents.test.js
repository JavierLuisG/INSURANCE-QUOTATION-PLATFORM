'use strict';

jest.mock('../../src/models/Agent');
jest.mock('../../src/middleware/mockScenarioInterceptor', () => (req, res, next) => next());

const Agent = require('../../src/models/Agent');
const request = require('supertest');
const express = require('express');
const router = require('../../src/routes/agents');

describe('GET /v1/agents', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/v1', router);
    jest.clearAllMocks();
  });

  it('test_agents_returns_empty_list', async () => {
    // GIVEN
    Agent.find.mockResolvedValue([]);

    // WHEN
    const res = await request(app).get('/v1/agents');

    // THEN
    expect(res.status).toBe(200);
    expect(res.body).toEqual([]);
  });

  it('test_agents_returns_list', async () => {
    // GIVEN
    const mockAgents = [
      { id: 'AGT-001', nombre: 'Carlos Mendoza', clave: 'CME', activo: true },
      { id: 'AGT-002', nombre: 'Laura Vega', clave: 'LVE', activo: true },
    ];
    Agent.find.mockResolvedValue(mockAgents);

    // WHEN
    const res = await request(app).get('/v1/agents');

    // THEN
    expect(res.status).toBe(200);
    expect(Array.isArray(res.body)).toBe(true);
    expect(res.body).toHaveLength(2);
  });
});
