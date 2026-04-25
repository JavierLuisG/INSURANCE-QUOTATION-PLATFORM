'use strict';

async function up(db) {
  await db.collection('subscribers').insertMany([
    { id: 'SUB-001', nombre: 'Grupo Asegurador Azteca S.A. de C.V.', clave: 'GAA', activo: true },
    { id: 'SUB-002', nombre: 'Reaseguradora Nacional de Daños', clave: 'RND', activo: true },
    { id: 'SUB-003', nombre: 'Cía. de Seguros del Norte S.A.', clave: 'CSN', activo: true },
    { id: 'SUB-004', nombre: 'Aseguradora Metropolitana S.A.', clave: 'AME', activo: true },
    { id: 'SUB-005', nombre: 'Seguros Pacífico Centro S.A. de C.V.', clave: 'SPC', activo: true },
    { id: 'SUB-006', nombre: 'Alianza de Riesgos Industriales S.A.', clave: 'ARI', activo: false },
  ]);
}

async function down(db) {
  await db.collection('subscribers').deleteMany({
    id: { $in: ['SUB-001', 'SUB-002', 'SUB-003', 'SUB-004', 'SUB-005', 'SUB-006'] },
  });
}

module.exports = { up, down };
