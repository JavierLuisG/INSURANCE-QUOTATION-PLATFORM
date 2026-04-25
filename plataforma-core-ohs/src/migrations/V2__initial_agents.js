'use strict';

async function up(db) {
  await db.collection('agents').insertMany([
    { id: 'AGT-001', nombre: 'Juan Carlos Pérez García', clave: 'JPG', activo: true },
    { id: 'AGT-002', nombre: 'María Elena Rodríguez López', clave: 'MRL', activo: true },
    { id: 'AGT-003', nombre: 'Roberto Antonio Sánchez Díaz', clave: 'RSD', activo: true },
    { id: 'AGT-004', nombre: 'Laura Patricia Hernández Cruz', clave: 'LHC', activo: true },
    { id: 'AGT-005', nombre: 'Alejandro Miguel Torres Vega', clave: 'ATV', activo: true },
    { id: 'AGT-006', nombre: 'Carmen Isabel Flores Reyes', clave: 'CFR', activo: false },
  ]);
}

async function down(db) {
  await db.collection('agents').deleteMany({
    id: { $in: ['AGT-001', 'AGT-002', 'AGT-003', 'AGT-004', 'AGT-005', 'AGT-006'] },
  });
}

module.exports = { up, down };
