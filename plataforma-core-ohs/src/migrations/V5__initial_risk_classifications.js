'use strict';

async function up(db) {
  await db.collection('riskClassifications').insertMany([
    {
      id: 'CR-001',
      nombre: 'Riesgo Bajo',
      descripcion: 'Edificaciones de concreto o mampostería con bajo riesgo operacional, sin materiales inflamables y con sistemas contra incendio instalados.',
    },
    {
      id: 'CR-002',
      nombre: 'Riesgo Medio',
      descripcion: 'Edificaciones mixtas con presencia moderada de materiales combustibles y riesgo operacional ordinario, con salidas de emergencia adecuadas.',
    },
    {
      id: 'CR-003',
      nombre: 'Riesgo Alto',
      descripcion: 'Instalaciones con materiales altamente inflamables, procesos de alta temperatura, almacenamiento de químicos o alta densidad de carga de fuego.',
    },
    {
      id: 'CR-004',
      nombre: 'Riesgo Especial',
      descripcion: 'Establecimientos con características únicas que requieren análisis técnico individualizado: plantas petroquímicas, hospitales, centros de datos críticos.',
    },
  ]);
}

async function down(db) {
  await db.collection('riskClassifications').deleteMany({
    id: { $in: ['CR-001', 'CR-002', 'CR-003', 'CR-004'] },
  });
}

module.exports = { up, down };
