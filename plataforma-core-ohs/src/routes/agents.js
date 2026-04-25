'use strict';

const express = require('express');
const Agent = require('../models/Agent');
const mockScenarioInterceptor = require('../middleware/mockScenarioInterceptor');

const router = express.Router();

router.get('/agents', mockScenarioInterceptor, async (req, res) => {
  try {
    const agents = await Agent.find({}, { _id: 0, __v: 0 });
    return res.status(200).json(agents);
  } catch (err) {
    console.error('GET /v1/agents error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

module.exports = router;
