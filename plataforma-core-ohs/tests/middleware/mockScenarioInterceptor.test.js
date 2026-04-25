'use strict';

jest.mock('../../src/models/MockScenario');

const MockScenario = require('../../src/models/MockScenario');
const mockScenarioInterceptor = require('../../src/middleware/mockScenarioInterceptor');

function buildReqRes(path = '/v1/subscribers') {
  const req = { path };
  const res = {
    status: jest.fn().mockReturnThis(),
    json: jest.fn().mockReturnThis(),
    send: jest.fn().mockReturnThis(),
    setHeader: jest.fn(),
  };
  return { req, res };
}

describe('mockScenarioInterceptor', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    jest.useFakeTimers();
  });

  afterEach(() => {
    jest.useRealTimers();
  });

  it('test_mock_scenario_no_scenario_calls_next', async () => {
    // GIVEN
    MockScenario.findOne.mockResolvedValue(null);
    const { req, res } = buildReqRes();
    const next = jest.fn();

    // WHEN
    await mockScenarioInterceptor(req, res, next);

    // THEN
    expect(next).toHaveBeenCalledTimes(1);
    expect(res.status).not.toHaveBeenCalled();
  });

  it('test_mock_scenario_normal_calls_next', async () => {
    // GIVEN
    MockScenario.findOne.mockResolvedValue({ scenarioType: 'NORMAL', activo: true });
    const { req, res } = buildReqRes();
    const next = jest.fn();

    // WHEN
    await mockScenarioInterceptor(req, res, next);

    // THEN
    expect(next).toHaveBeenCalledTimes(1);
    expect(res.status).not.toHaveBeenCalled();
  });

  it('test_mock_scenario_delay_applied', async () => {
    // GIVEN
    MockScenario.findOne.mockResolvedValue({ scenarioType: 'DELAY', delayMs: 500, activo: true });
    const { req, res } = buildReqRes();
    const next = jest.fn();

    // WHEN
    const promise = mockScenarioInterceptor(req, res, next);
    // Flush microtask queue so the async function reaches setTimeout registration
    await Promise.resolve();
    // advance timers to trigger the registered setTimeout
    jest.runAllTimers();
    await promise;

    // THEN
    expect(next).toHaveBeenCalledTimes(1);
  });

  it('test_mock_scenario_http_error_applied', async () => {
    // GIVEN
    const responseBody = JSON.stringify({ message: 'Servicio no disponible', code: 'SERVICE_DOWN' });
    MockScenario.findOne.mockResolvedValue({
      scenarioType: 'HTTP_ERROR',
      httpStatusCode: 503,
      responseBody,
      activo: true,
    });
    const { req, res } = buildReqRes();
    const next = jest.fn();

    // WHEN
    await mockScenarioInterceptor(req, res, next);

    // THEN
    expect(next).not.toHaveBeenCalled();
    expect(res.status).toHaveBeenCalledWith(503);
    expect(res.json).toHaveBeenCalledWith({ message: 'Servicio no disponible', code: 'SERVICE_DOWN' });
  });

  it('test_mock_scenario_malformed_data', async () => {
    // GIVEN
    MockScenario.findOne.mockResolvedValue({
      scenarioType: 'MALFORMED_DATA',
      responseBody: '{ invalid json }',
      activo: true,
    });
    const { req, res } = buildReqRes();
    const next = jest.fn();

    // WHEN
    await mockScenarioInterceptor(req, res, next);

    // THEN
    expect(next).not.toHaveBeenCalled();
    expect(res.setHeader).toHaveBeenCalledWith('Content-Type', 'application/json');
    expect(res.send).toHaveBeenCalledWith('{ invalid json }');
  });
});
