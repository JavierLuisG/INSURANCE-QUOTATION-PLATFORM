'use strict';

jest.mock('../../src/models/MockScenario');

const MockScenario = require('../../src/models/MockScenario');
const request = require('supertest');
const express = require('express');
const router = require('../../src/routes/mockScenarios');

describe('POST /_mock/scenarios', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/', router);
    jest.clearAllMocks();
  });

  it('test_mock_scenario_create_success', async () => {
    // GIVEN
    const payload = {
      endpointPath: '/v1/subscribers',
      scenarioType: 'DELAY',
      delayMs: 2000,
    };
    const savedScenario = {
      endpointPath: '/v1/subscribers',
      scenarioType: 'DELAY',
      delayMs: 2000,
      activo: true,
    };
    MockScenario.findOneAndUpdate.mockResolvedValue(savedScenario);

    // WHEN
    const res = await request(app).post('/_mock/scenarios').send(payload);

    // THEN
    expect(res.status).toBe(200);
    expect(res.body.endpointPath).toBe('/v1/subscribers');
    expect(res.body.scenarioType).toBe('DELAY');
  });

  it('test_mock_scenario_invalid_delay_returns_400', async () => {
    // GIVEN
    const payload = {
      endpointPath: '/v1/subscribers',
      scenarioType: 'DELAY',
      delayMs: -1,
    };

    // WHEN
    const res = await request(app).post('/_mock/scenarios').send(payload);

    // THEN
    expect(res.status).toBe(400);
    expect(res.body.code).toBe('INVALID_REQUEST');
  });

  it('test_mock_scenario_delay_type_without_delay_ms_returns_400', async () => {
    // GIVEN
    const payload = {
      endpointPath: '/v1/subscribers',
      scenarioType: 'DELAY',
      // delayMs deliberately omitted
    };

    // WHEN
    const res = await request(app).post('/_mock/scenarios').send(payload);

    // THEN
    expect(res.status).toBe(400);
    expect(res.body.code).toBe('INVALID_REQUEST');
  });

  it('test_mock_scenario_invalid_scenario_type_returns_400', async () => {
    // GIVEN
    const payload = {
      endpointPath: '/v1/subscribers',
      scenarioType: 'UNKNOWN_TYPE',
    };

    // WHEN
    const res = await request(app).post('/_mock/scenarios').send(payload);

    // THEN
    expect(res.status).toBe(400);
    expect(res.body.code).toBe('INVALID_REQUEST');
  });
});

describe('DELETE /_mock/scenarios/:endpointPath', () => {
  let app;

  beforeEach(() => {
    app = express();
    app.use(express.json());
    app.use('/', router);
    jest.clearAllMocks();
  });

  it('test_mock_scenario_delete_success', async () => {
    // GIVEN
    MockScenario.deleteOne.mockResolvedValue({ deletedCount: 1 });

    // WHEN
    const res = await request(app).delete('/_mock/scenarios/v1/subscribers');

    // THEN
    expect(res.status).toBe(204);
    expect(res.body).toEqual({});
    expect(MockScenario.deleteOne).toHaveBeenCalledWith({ endpointPath: '/v1/subscribers' });
  });
});
