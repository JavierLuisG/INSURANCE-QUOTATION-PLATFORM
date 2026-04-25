'use strict';

const mongoose = require('mongoose');

const tariffCatSchema = new mongoose.Schema({
  zona: {
    type: String,
    required: true,
    unique: true,
    enum: ['ZONA_A', 'ZONA_B', 'ZONA_C', 'ZONA_D'],
  },
  factorTEV: {
    type: Number,
    required: true,
    min: 0,
  },
  factorFHM: {
    type: Number,
    required: true,
    min: 0,
  },
});

module.exports = mongoose.model('TariffCat', tariffCatSchema, 'tariffsCat');
