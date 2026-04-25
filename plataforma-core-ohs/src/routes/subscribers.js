'use strict';

const express = require('express');
const Subscriber = require('../models/Subscriber');
const mockScenarioInterceptor = require('../middleware/mockScenarioInterceptor');

const router = express.Router();

router.get('/subscribers', mockScenarioInterceptor, async (req, res) => {
  try {
    const subscribers = await Subscriber.find({}, { _id: 0, __v: 0 });
    return res.status(200).json(subscribers);
  } catch (err) {
    console.error('GET /v1/subscribers error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

module.exports = router;
