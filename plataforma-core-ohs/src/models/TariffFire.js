'use strict';

const mongoose = require('mongoose');

const tariffFireSchema = new mongoose.Schema({
  zonaRiesgo: {
    type: String,
    required: true,
    enum: ['ZONA_A', 'ZONA_B', 'ZONA_C', 'ZONA_D'],
  },
  tipoConstructivo: {
    type: String,
    required: true,
    maxlength: 50,
    trim: true,
  },
  tasaBase: {
    type: Number,
    required: true,
    min: 0,
  },
  factorRecargo: {
    type: Number,
    required: true,
    min: 1,
  },
  vigenciaDesde: {
    type: Date,
    required: true,
  },
  vigenciaHasta: {
    type: Date,
  },
});

module.exports = mongoose.model('TariffFire', tariffFireSchema, 'tariffsFire');
