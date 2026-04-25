'use strict';

async function up(db) {
  await db.collection('guarantees').insertMany([
    { id: 'G-001', nombre: 'Incendio Edificios', claveIncendio: 'INC-EDI', tarifable: true },
    { id: 'G-002', nombre: 'Incendio Contenidos', claveIncendio: 'INC-CON', tarifable: true },
    { id: 'G-003', nombre: 'Terremoto y/o Erupción Volcánica Edificios', claveIncendio: 'TEV-EDI', tarifable: true },
    { id: 'G-004', nombre: 'Terremoto y/o Erupción Volcánica Contenidos', claveIncendio: 'TEV-CON', tarifable: true },
    { id: 'G-005', nombre: 'Huracán, Ciclón, Tifón o Tornado Edificios', claveIncendio: 'FHM-EDI', tarifable: true },
    { id: 'G-006', nombre: 'Huracán, Ciclón, Tifón o Tornado Contenidos', claveIncendio: 'FHM-CON', tarifable: true },
    { id: 'G-007', nombre: 'Inundación Edificios', claveIncendio: 'INU-EDI', tarifable: true },
    { id: 'G-008', nombre: 'Inundación Contenidos', claveIncendio: 'INU-CON', tarifable: true },
    { id: 'G-009', nombre: 'Robo con Violencia', claveIncendio: 'ROB-VIO', tarifable: true },
    { id: 'G-010', nombre: 'Robo sin Violencia (Forzamiento)', claveIncendio: 'ROB-FOR', tarifable: true },
    { id: 'G-011', nombre: 'Equipo Electrónico', claveIncendio: 'EEL-GEN', tarifable: true },
    { id: 'G-012', nombre: 'Responsabilidad Civil General', claveIncendio: 'RCG-GEN', tarifable: false },
    { id: 'G-013', nombre: 'Daños por Agua (Lluvia)', claveIncendio: 'DAG-LLU', tarifable: true },
    { id: 'G-014', nombre: 'Cristales', claveIncendio: 'CRI-GEN', tarifable: true },
  ]);
}

async function down(db) {
  await db.collection('guarantees').deleteMany({
    id: {
      $in: [
        'G-001', 'G-002', 'G-003', 'G-004', 'G-005', 'G-006', 'G-007',
        'G-008', 'G-009', 'G-010', 'G-011', 'G-012', 'G-013', 'G-014',
      ],
    },
  });
}

module.exports = { up, down };
