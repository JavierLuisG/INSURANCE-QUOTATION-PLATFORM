'use strict';

const mongoose = require('mongoose');

const zipCodeSchema = new mongoose.Schema({
  codigoPostal: {
    type: String,
    required: true,
    unique: true,
    match: /^\d{5}$/,
  },
  zonaCAT: {
    type: String,
    required: true,
    enum: ['ZONA_A', 'ZONA_B', 'ZONA_C', 'ZONA_D'],
  },
  nivelTecnico: {
    type: String,
    required: true,
    enum: ['ALTO', 'MEDIO', 'BAJO'],
  },
  estado: {
    type: String,
    required: true,
    maxlength: 100,
    trim: true,
  },
  municipio: {
    type: String,
    required: true,
    maxlength: 200,
    trim: true,
  },
  ciudad: {
    type: String,
    maxlength: 200,
    trim: true,
  },
});

module.exports = mongoose.model('ZipCode', zipCodeSchema, 'zipCodes');
