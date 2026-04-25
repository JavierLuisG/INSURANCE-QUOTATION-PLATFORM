'use strict';

const express = require('express');
const { param, body, validationResult } = require('express-validator');
const ZipCode = require('../models/ZipCode');
const mockScenarioInterceptor = require('../middleware/mockScenarioInterceptor');

const router = express.Router();

const ZIP_REGEX = /^\d{5}$/;

router.get(
  '/zip-codes/:zipCode',
  mockScenarioInterceptor,
  param('zipCode').matches(ZIP_REGEX).withMessage('Formato de código postal inválido'),
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ message: 'Formato de código postal inválido', code: 'INVALID_ZIP_FORMAT' });
    }

    try {
      const record = await ZipCode.findOne({ codigoPostal: req.params.zipCode }, { _id: 0, __v: 0 });
      if (!record) {
        return res.status(404).json({ message: 'Código postal no encontrado', code: 'ZIP_NOT_FOUND' });
      }
      return res.status(200).json(record);
    } catch (err) {
      console.error('GET /v1/zip-codes/:zipCode error:', err.message);
      return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
    }
  }
);

router.post(
  '/zip-codes/validate',
  mockScenarioInterceptor,
  body('zipCodes')
    .isArray({ min: 1 })
    .withMessage('zipCodes debe ser un array con al menos un elemento'),
  body('zipCodes.*')
    .matches(ZIP_REGEX)
    .withMessage('Cada código postal debe tener formato de 5 dígitos'),
  async (req, res) => {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({ message: errors.array()[0].msg, code: 'INVALID_REQUEST' });
    }

    try {
      const { zipCodes } = req.body;
      const found = await ZipCode.find({ codigoPostal: { $in: zipCodes } }, { _id: 0, __v: 0 });

      const foundMap = new Map(found.map((z) => [z.codigoPostal, z]));

      const results = zipCodes.map((cp) => {
        const record = foundMap.get(cp);
        if (record) {
          return {
            codigoPostal: cp,
            valido: true,
            zonaCAT: record.zonaCAT,
            nivelTecnico: record.nivelTecnico,
            estado: record.estado,
            municipio: record.municipio,
          };
        }
        return { codigoPostal: cp, valido: false, error: 'Código postal no encontrado' };
      });

      return res.status(200).json({ results });
    } catch (err) {
      console.error('POST /v1/zip-codes/validate error:', err.message);
      return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
    }
  }
);

module.exports = router;
