'use strict';

async function up(db) {
  await db.collection('businessLines').insertMany([
    { id: 'GIR-001', descripcion: 'Manufactura ligera (textil, confección, calzado)', claveIncendio: 'MAN-L', activo: true },
    { id: 'GIR-002', descripcion: 'Manufactura pesada (metalúrgica, química, petroquímica)', claveIncendio: 'MAN-P', activo: true },
    { id: 'GIR-003', descripcion: 'Comercio al detalle (tiendas, boutiques, farmacias)', claveIncendio: 'COM-D', activo: true },
    { id: 'GIR-004', descripcion: 'Comercio al mayoreo (bodegas de distribución)', claveIncendio: 'COM-M', activo: true },
    { id: 'GIR-005', descripcion: 'Servicios de alimentación (restaurantes, cocinas industriales)', claveIncendio: 'SRV-A', activo: true },
    { id: 'GIR-006', descripcion: 'Servicios profesionales (oficinas, despachos, consultorios)', claveIncendio: 'SRV-P', activo: true },
    { id: 'GIR-007', descripcion: 'Educación (escuelas, universidades, centros de capacitación)', claveIncendio: 'EDU-G', activo: true },
    { id: 'GIR-008', descripcion: 'Hotelería y hospedaje (hoteles, moteles, hostales)', claveIncendio: 'HOS-G', activo: true },
    { id: 'GIR-009', descripcion: 'Salud y farmacéutico (hospitales, clínicas, laboratorios)', claveIncendio: 'SAL-G', activo: true },
    { id: 'GIR-010', descripcion: 'Almacenamiento general (bodegas de mercancía diversa)', claveIncendio: 'ALM-G', activo: true },
    { id: 'GIR-011', descripcion: 'Almacenamiento de materiales inflamables', claveIncendio: 'ALM-I', activo: true },
    { id: 'GIR-012', descripcion: 'Tecnología e informática (data centers, desarrollo de software)', claveIncendio: 'TEC-G', activo: true },
  ]);
}

async function down(db) {
  await db.collection('businessLines').deleteMany({
    id: {
      $in: [
        'GIR-001', 'GIR-002', 'GIR-003', 'GIR-004',
        'GIR-005', 'GIR-006', 'GIR-007', 'GIR-008',
        'GIR-009', 'GIR-010', 'GIR-011', 'GIR-012',
      ],
    },
  });
}

module.exports = { up, down };
