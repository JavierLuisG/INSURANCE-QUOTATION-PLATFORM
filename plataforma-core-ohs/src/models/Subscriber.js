'use strict';

const mongoose = require('mongoose');

const subscriberSchema = new mongoose.Schema({
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
  clave: {
    type: String,
    required: true,
    maxlength: 50,
    trim: true,
  },
  activo: {
    type: Boolean,
    required: true,
    default: true,
  },
});

module.exports = mongoose.model('Subscriber', subscriberSchema, 'subscribers');
