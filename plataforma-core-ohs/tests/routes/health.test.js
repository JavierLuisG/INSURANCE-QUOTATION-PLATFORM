'use strict';

const request = require('supertest');
const express = require('express');

describe('GET /health', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.get('/health', (req, res) => {
      return res.status(200).json({
        status: 'UP',
        service: 'plataforma-core-ohs-mock',
        timestamp: new Date().toISOString(),
      });
    });
  });

  it('test_health_returns_200', async () => {
    // GIVEN
    // No setup needed — health endpoint has no dependencies

    // WHEN
    const res = await request(app).get('/health');

    // THEN
    expect(res.status).toBe(200);
    expect(res.body.status).toBe('UP');
    expect(res.body.service).toBe('plataforma-core-ohs-mock');
  });
});
