'use strict';

const express = require('express');
const BusinessLine = require('../models/BusinessLine');
const mockScenarioInterceptor = require('../middleware/mockScenarioInterceptor');

const router = express.Router();

router.get('/business-lines', mockScenarioInterceptor, async (req, res) => {
  try {
    const businessLines = await BusinessLine.find({}, { _id: 0, __v: 0 });
    return res.status(200).json(businessLines);
  } catch (err) {
    console.error('GET /v1/business-lines error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

module.exports = router;
