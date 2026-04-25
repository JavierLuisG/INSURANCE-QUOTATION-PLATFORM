'use strict';

const mongoose = require('mongoose');

const mockScenarioSchema = new mongoose.Schema({
  endpointPath: {
    type: String,
    required: true,
    unique: true,
    trim: true,
  },
  scenarioType: {
    type: String,
    required: true,
    enum: ['DELAY', 'HTTP_ERROR', 'MALFORMED_DATA', 'NORMAL'],
  },
  delayMs: {
    type: Number,
    min: 0,
  },
  httpStatusCode: {
    type: Number,
    min: 400,
    max: 599,
  },
  responseBody: {
    type: String,
  },
  activo: {
    type: Boolean,
    required: true,
    default: true,
  },
});

module.exports = mongoose.model('MockScenario', mockScenarioSchema, 'mockScenarios');
