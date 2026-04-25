'use strict';

const express = require('express');
const RiskClassification = require('../models/RiskClassification');
const Guarantee = require('../models/Guarantee');
const mockScenarioInterceptor = require('../middleware/mockScenarioInterceptor');

const router = express.Router();

router.get('/catalogs/risk-classification', mockScenarioInterceptor, async (req, res) => {
  try {
    const classifications = await RiskClassification.find({}, { _id: 0, __v: 0 });
    return res.status(200).json(classifications);
  } catch (err) {
    console.error('GET /v1/catalogs/risk-classification error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

router.get('/catalogs/guarantees', mockScenarioInterceptor, async (req, res) => {
  try {
    const guarantees = await Guarantee.find({}, { _id: 0, __v: 0 });
    return res.status(200).json(guarantees);
  } catch (err) {
    console.error('GET /v1/catalogs/guarantees error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

module.exports = router;
