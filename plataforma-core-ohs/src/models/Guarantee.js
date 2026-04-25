'use strict';

const mongoose = require('mongoose');

const guaranteeSchema = new mongoose.Schema({
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
  claveIncendio: {
    type: String,
    required: true,
    maxlength: 50,
    trim: true,
  },
  tarifable: {
    type: Boolean,
    required: true,
    default: true,
  },
});

module.exports = mongoose.model('Guarantee', guaranteeSchema, 'guarantees');
