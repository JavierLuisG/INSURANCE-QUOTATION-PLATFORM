'use strict';

const VIGENCIA_DESDE = new Date('2026-01-01T00:00:00.000Z');

async function up(db) {
  const tiposConstructivos = [
    { tipo: 'Concreto', factorRecargo: 1.00 },
    { tipo: 'Mampostería', factorRecargo: 1.05 },
    { tipo: 'Metal/Estructura metálica', factorRecargo: 1.10 },
    { tipo: 'Madera', factorRecargo: 1.40 },
    { tipo: 'Mixto', factorRecargo: 1.15 },
  ];

  // Tasas base por zona (mayor zona = mayor riesgo CAT acumulado)
  const tasasPorZona = {
    ZONA_A: 0.0012,
    ZONA_B: 0.0010,
    ZONA_C: 0.0008,
    ZONA_D: 0.0011,
  };

  const tariffs = [];
  for (const zona of ['ZONA_A', 'ZONA_B', 'ZONA_C', 'ZONA_D']) {
    for (const { tipo, factorRecargo } of tiposConstructivos) {
      tariffs.push({
        zonaRiesgo: zona,
        tipoConstructivo: tipo,
        tasaBase: tasasPorZona[zona],
        factorRecargo,
        vigenciaDesde: VIGENCIA_DESDE,
        vigenciaHasta: null,
      });
    }
  }

  await db.collection('tariffsFire').insertMany(tariffs);
}

async function down(db) {
  await db.collection('tariffsFire').deleteMany({
    zonaRiesgo: { $in: ['ZONA_A', 'ZONA_B', 'ZONA_C', 'ZONA_D'] },
    vigenciaDesde: VIGENCIA_DESDE,
  });
}

module.exports = { up, down };
