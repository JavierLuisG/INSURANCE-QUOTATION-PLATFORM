'use strict';

const express = require('express');
const mongoose = require('mongoose');

const router = express.Router();

const folioCounterSchema = new mongoose.Schema({
  _id: { type: String, required: true },
  seq: { type: Number, default: 0 },
});

const FolioCounter = mongoose.models.FolioCounter || mongoose.model('FolioCounter', folioCounterSchema, 'folioCounters');

router.get('/folios', async (req, res) => {
  try {
    const year = new Date().getFullYear();
    const counterId = `cotizacion-${year}`;

    const counter = await FolioCounter.findOneAndUpdate(
      { _id: counterId },
      { $inc: { seq: 1 } },
      { new: true, upsert: true }
    );

    const sequence = String(counter.seq).padStart(6, '0');
    const folio = `COT-${year}-${sequence}`;

    return res.status(200).json({ folio });
  } catch (err) {
    console.error('GET /v1/folios error:', err.message);
    return res.status(500).json({ message: 'Error interno del servidor', code: 'INTERNAL_ERROR' });
  }
});

module.exports = router;
