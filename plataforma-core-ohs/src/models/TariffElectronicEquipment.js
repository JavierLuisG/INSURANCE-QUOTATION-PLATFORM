'use strict';

const mongoose = require('mongoose');

const tariffElectronicEquipmentSchema = new mongoose.Schema({
  clase: {
    type: String,
    required: true,
    trim: true,
  },
  nivelZona: {
    type: String,
    required: true,
    enum: ['ALTO', 'MEDIO', 'BAJO'],
  },
  factor: {
    type: Number,
    required: true,
    min: 0,
  },
});

module.exports = mongoose.model(
  'TariffElectronicEquipment',
  tariffElectronicEquipmentSchema,
  'tariffsElectronicEquipment'
);
