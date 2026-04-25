'use strict';

const mongoose = require('mongoose');

const riskClassificationSchema = new mongoose.Schema({
  id: {
    type: String,
    required: true,
    unique: true,
    trim: true,
  },
  nombre: {
    type: String,
    required: true,
    maxlength: 200,
    trim: true,
  },
  descripcion: {
    type: String,
    required: true,
    maxlength: 500,
    trim: true,
  },
});

module.exports = mongoose.model('RiskClassification', riskClassificationSchema, 'riskClassifications');
