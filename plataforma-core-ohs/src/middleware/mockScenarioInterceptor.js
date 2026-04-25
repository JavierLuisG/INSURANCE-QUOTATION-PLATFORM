'use strict';

const MockScenario = require('../models/MockScenario');

async function mockScenarioInterceptor(req, res, next) {
  try {
    const scenario = await MockScenario.findOne({
      endpointPath: req.path,
      activo: true,
    });

    if (!scenario) {
      return next();
    }

    switch (scenario.scenarioType) {
      case 'NORMAL':
        return next();

      case 'DELAY':
        return setTimeout(() => next(), scenario.delayMs || 0);

      case 'HTTP_ERROR': {
        const statusCode = scenario.httpStatusCode || 500;
        let body;
        try {
          body = JSON.parse(scenario.responseBody);
        } catch {
          body = { message: 'Error simulado', code: 'MOCK_ERROR' };
        }
        return res.status(statusCode).json(body);
      }

      case 'MALFORMED_DATA': {
        res.setHeader('Content-Type', 'application/json');
        return res.send(scenario.responseBody || '{ invalid json }');
      }

      default:
        return next();
    }
  } catch (err) {
    // Error al consultar escenarios no debe bloquear el flujo normal
    console.error('mockScenarioInterceptor error:', err.message);
    return next();
  }
}

module.exports = mockScenarioInterceptor;
