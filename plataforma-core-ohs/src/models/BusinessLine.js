'use strict';

const mongoose = require('mongoose');

const businessLineSchema = new mongoose.Schema({
  id: {
    type: String,
    required: true,
    unique: true,
    trim: true,
  },
  descripcion: {
    type: String,
    required: true,
    maxlength: 300,
    trim: true,
  },
  claveIncendio: {
    type: String,
    required: true,
    maxlength: 20,
    trim: true,
  },
  activo: {
    type: Boolean,
    required: true,
    default: true,
  },
});

module.exports = mongoose.model('BusinessLine', businessLineSchema, 'businessLines');
