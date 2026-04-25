'use strict';

const express = require('express');
const { body, validationResult } = require('express-validator');
const MockScenario = require('../models/MockScenario');

const router = express.Router();

const VALID_SCENARIO_TYPES = ['DELAY', 'HTTP_ERROR', 'MALFORMED_DATA', 'NORMAL'];

router.post(
  '/_mock/scenarios',
  body('endpointPath').isString().notEmpty().withMessage('endpointPath es requerido'),
  body('scenarioType').isIn(VALID_SCENARIO_TYPES).withMessage(`scenarioType debe ser uno de: ${VALID_SCENARIO_TYPES.join(', ')}`),
  body('delayMs')
    .optional()
    .isInt({ min: 0 })
    .withMessage('delayMs debe ser un entero >= 0'),
  body('httpStatusCode')
    .optional()
    .isInt({ min: 400, max: 599 })
    .withMessage('httpStatusCode debe estar entre 400 y 599'),
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ message: errors.array()[0].msg, code: 'INVALID_REQUEST' });
    }

    const { endpointPath, scenarioType, delayMs, httpStatusCode, responseBody } = req.body;

    if (scenarioType === 'DELAY' && delayMs === undefined) {
      return res.status(400).json({ message: 'delayMs es requerido para scenarioType DELAY', code: 'INVALID_REQUEST' });
    }

    if (scenarioType === 'HTTP_ERROR' && httpStatusCode === undefined) {
      return res.status(400).json({
        message: 'httpStatusCode es requerido para scenarioType HTTP_ERROR',
        code: 'INVALID_REQUEST',
      });
    }

    try {
      const scenario = await MockScenario.findOneAndUpdate(
        { endpointPath },
        { endpointPath, scenarioType, delayMs, httpStatusCode, responseBody, activo: true },
        { new: true, upsert: true, projection: { _id: 0, __v: 0 } }
      );

      return res.status(200).json(scenario);
    } catch (err) {
      console.error('POST /_mock/scenarios error:', err.message);
      return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
    }
  }
);

router.delete('/_mock/scenarios/:endpointPath(*)', async (req, res) => {
  try {
    const endpointPath = '/' + req.params.endpointPath;
    await MockScenario.deleteOne({ endpointPath });
    return res.status(204).send();
  } catch (err) {
    console.error('DELETE /_mock/scenarios error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

module.exports = router;
