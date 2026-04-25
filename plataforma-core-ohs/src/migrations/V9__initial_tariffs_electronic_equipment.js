'use strict';

async function up(db) {
  // Clases de equipo electrónico (A = menor riesgo, B = riesgo medio, C = mayor riesgo)
  // nivelZona refleja la exposición al riesgo de la ubicación
  await db.collection('tariffsElectronicEquipment').insertMany([
    { clase: 'A', nivelZona: 'ALTO', factor: 0.0025 },
    { clase: 'A', nivelZona: 'MEDIO', factor: 0.0020 },
    { clase: 'A', nivelZona: 'BAJO', factor: 0.0015 },
    { clase: 'B', nivelZona: 'ALTO', factor: 0.0035 },
    { clase: 'B', nivelZona: 'MEDIO', factor: 0.0028 },
    { clase: 'B', nivelZona: 'BAJO', factor: 0.0022 },
    { clase: 'C', nivelZona: 'ALTO', factor: 0.0050 },
    { clase: 'C', nivelZona: 'MEDIO', factor: 0.0040 },
    { clase: 'C', nivelZona: 'BAJO', factor: 0.0032 },
  ]);
}

async function down(db) {
  await db.collection('tariffsElectronicEquipment').deleteMany({
    clase: { $in: ['A', 'B', 'C'] },
  });
}

module.exports = { up, down };
