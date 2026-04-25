'use strict';

const express = require('express');
const { query, body, validationResult } = require('express-validator');
const TariffFire = require('../models/TariffFire');
const TariffCat = require('../models/TariffCat');
const TariffElectronicEquipment = require('../models/TariffElectronicEquipment');
const mockScenarioInterceptor = require('../middleware/mockScenarioInterceptor');

const router = express.Router();

const VALID_ZONES = ['ZONA_A', 'ZONA_B', 'ZONA_C', 'ZONA_D'];

router.get('/tariffs/fire', mockScenarioInterceptor, async (req, res) => {
  try {
    const filter = {};
    if (req.query.zona) filter.zonaRiesgo = req.query.zona;
    if (req.query.tipoConstructivo) filter.tipoConstructivo = req.query.tipoConstructivo;

    const tariffs = await TariffFire.find(filter, { _id: 0, __v: 0 });
    return res.status(200).json(tariffs);
  } catch (err) {
    console.error('GET /v1/tariffs/fire error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

router.put(
  '/tariffs/fire',
  mockScenarioInterceptor,
  body('zonaRiesgo').isIn(VALID_ZONES).withMessage('zonaRiesgo inválida'),
  body('tipoConstructivo').isString().notEmpty().withMessage('tipoConstructivo es requerido'),
  body('tasaBase').isFloat({ min: 0 }).withMessage('tasaBase debe ser un número positivo'),
  body('factorRecargo').isFloat({ min: 1 }).withMessage('factorRecargo debe ser >= 1'),
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ message: errors.array()[0].msg, code: 'INVALID_REQUEST' });
    }

    try {
      const { zonaRiesgo, tipoConstructivo, tasaBase, factorRecargo, vigenciaDesde, vigenciaHasta } = req.body;

      const updated = await TariffFire.findOneAndUpdate(
        { zonaRiesgo, tipoConstructivo },
        { tasaBase, factorRecargo, vigenciaDesde, vigenciaHasta },
        { new: true, upsert: true, projection: { _id: 0, __v: 0 } }
      );

      return res.status(200).json(updated);
    } catch (err) {
      console.error('PUT /v1/tariffs/fire error:', err.message);
      return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
    }
  }
);

router.get(
  '/tariffs/cat',
  mockScenarioInterceptor,
  query('zona').notEmpty().withMessage('El parámetro zona es requerido'),
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ message: errors.array()[0].msg, code: 'INVALID_REQUEST' });
    }

    try {
      const tariff = await TariffCat.findOne({ zona: req.query.zona }, { _id: 0, __v: 0 });
      if (!tariff) {
        return res.status(404).json({
          message: 'Tarifa no encontrada para los parámetros indicados',
          code: 'TARIFF_NOT_FOUND',
        });
      }
      return res.status(200).json(tariff);
    } catch (err) {
      console.error('GET /v1/tariffs/cat error:', err.message);
      return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
    }
  }
);

router.get('/tariffs/electronic-equipment', mockScenarioInterceptor, async (req, res) => {
  try {
    const factors = await TariffElectronicEquipment.find({}, { _id: 0, __v: 0 });
    return res.status(200).json(factors);
  } catch (err) {
    console.error('GET /v1/tariffs/electronic-equipment error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

module.exports = router;
