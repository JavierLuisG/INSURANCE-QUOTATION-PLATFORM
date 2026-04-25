'use strict';

async function up(db) {
  await db.collection('zipCodes').insertMany([
    // ZONA_A — CDMX (alto riesgo sísmico y de inundación)
    { codigoPostal: '06600', zonaCAT: 'ZONA_A', nivelTecnico: 'ALTO', estado: 'Ciudad de México', municipio: 'Cuauhtémoc', ciudad: 'Ciudad de México' },
    { codigoPostal: '06700', zonaCAT: 'ZONA_A', nivelTecnico: 'ALTO', estado: 'Ciudad de México', municipio: 'Venustiano Carranza', ciudad: 'Ciudad de México' },
    { codigoPostal: '03100', zonaCAT: 'ZONA_A', nivelTecnico: 'ALTO', estado: 'Ciudad de México', municipio: 'Benito Juárez', ciudad: 'Ciudad de México' },
    { codigoPostal: '09000', zonaCAT: 'ZONA_A', nivelTecnico: 'ALTO', estado: 'Ciudad de México', municipio: 'Iztapalapa', ciudad: 'Ciudad de México' },
    { codigoPostal: '15900', zonaCAT: 'ZONA_A', nivelTecnico: 'ALTO', estado: 'Ciudad de México', municipio: 'Venustiano Carranza', ciudad: 'Ciudad de México' },
    // ZONA_B — Jalisco y Nuevo León
    { codigoPostal: '44100', zonaCAT: 'ZONA_B', nivelTecnico: 'ALTO', estado: 'Jalisco', municipio: 'Guadalajara', ciudad: 'Guadalajara' },
    { codigoPostal: '44200', zonaCAT: 'ZONA_B', nivelTecnico: 'MEDIO', estado: 'Jalisco', municipio: 'Guadalajara', ciudad: 'Guadalajara' },
    { codigoPostal: '64000', zonaCAT: 'ZONA_B', nivelTecnico: 'ALTO', estado: 'Nuevo León', municipio: 'Monterrey', ciudad: 'Monterrey' },
    { codigoPostal: '64500', zonaCAT: 'ZONA_B', nivelTecnico: 'MEDIO', estado: 'Nuevo León', municipio: 'San Nicolás de los Garza', ciudad: 'Monterrey' },
    { codigoPostal: '45050', zonaCAT: 'ZONA_B', nivelTecnico: 'MEDIO', estado: 'Jalisco', municipio: 'Zapopan', ciudad: 'Guadalajara' },
    // ZONA_C — Puebla, Veracruz, Querétaro
    { codigoPostal: '72000', zonaCAT: 'ZONA_C', nivelTecnico: 'MEDIO', estado: 'Puebla', municipio: 'Puebla', ciudad: 'Puebla de Zaragoza' },
    { codigoPostal: '91700', zonaCAT: 'ZONA_C', nivelTecnico: 'MEDIO', estado: 'Veracruz', municipio: 'Veracruz', ciudad: 'Veracruz' },
    { codigoPostal: '76000', zonaCAT: 'ZONA_C', nivelTecnico: 'MEDIO', estado: 'Querétaro', municipio: 'Querétaro', ciudad: 'Querétaro' },
    { codigoPostal: '20000', zonaCAT: 'ZONA_C', nivelTecnico: 'BAJO', estado: 'Aguascalientes', municipio: 'Aguascalientes', ciudad: 'Aguascalientes' },
    { codigoPostal: '58000', zonaCAT: 'ZONA_C', nivelTecnico: 'BAJO', estado: 'Michoacán', municipio: 'Morelia', ciudad: 'Morelia' },
    // ZONA_D — Yucatán, Quintana Roo, Chiapas (alto riesgo ciclónico)
    { codigoPostal: '97000', zonaCAT: 'ZONA_D', nivelTecnico: 'ALTO', estado: 'Yucatán', municipio: 'Mérida', ciudad: 'Mérida' },
    { codigoPostal: '77500', zonaCAT: 'ZONA_D', nivelTecnico: 'ALTO', estado: 'Quintana Roo', municipio: 'Benito Juárez', ciudad: 'Cancún' },
    { codigoPostal: '29000', zonaCAT: 'ZONA_D', nivelTecnico: 'MEDIO', estado: 'Chiapas', municipio: 'Tuxtla Gutiérrez', ciudad: 'Tuxtla Gutiérrez' },
    { codigoPostal: '80000', zonaCAT: 'ZONA_D', nivelTecnico: 'ALTO', estado: 'Sinaloa', municipio: 'Culiacán', ciudad: 'Culiacán' },
    { codigoPostal: '83000', zonaCAT: 'ZONA_D', nivelTecnico: 'ALTO', estado: 'Sonora', municipio: 'Hermosillo', ciudad: 'Hermosillo' },
  ]);
}

async function down(db) {
  const zipCodes = [
    '06600', '06700', '03100', '09000', '15900',
    '44100', '44200', '64000', '64500', '45050',
    '72000', '91700', '76000', '20000', '58000',
    '97000', '77500', '29000', '80000', '83000',
  ];
  await db.collection('zipCodes').deleteMany({ codigoPostal: { $in: zipCodes } });
}

module.exports = { up, down };
