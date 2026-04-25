'use strict';

async function up(db) {
  await db.collection('tariffsCat').insertMany([
    // ZONA_A: CDMX — alta actividad sísmica, riesgo TEV significativo
    { zona: 'ZONA_A', factorTEV: 0.0015, factorFHM: 0.0002 },
    // ZONA_B: Jalisco / Nuevo León — riesgo sísmico moderado
    { zona: 'ZONA_B', factorTEV: 0.0008, factorFHM: 0.0003 },
    // ZONA_C: Puebla / Veracruz — riesgo mixto
    { zona: 'ZONA_C', factorTEV: 0.0006, factorFHM: 0.0005 },
    // ZONA_D: Costa del Pacífico y Golfo — alto riesgo ciclónico (FHM)
    { zona: 'ZONA_D', factorTEV: 0.0004, factorFHM: 0.0020 },
  ]);
}

async function down(db) {
  await db.collection('tariffsCat').deleteMany({
    zona: { $in: ['ZONA_A', 'ZONA_B', 'ZONA_C', 'ZONA_D'] },
  });
}

module.exports = { up, down };
