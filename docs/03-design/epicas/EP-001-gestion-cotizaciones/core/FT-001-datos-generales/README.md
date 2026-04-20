## FT-001: Captura y Validación de Datos Generales de la Cotización

### 1. Descripción
Esta feature permite la creación, carga y edición de los datos generales de una cotización, incluyendo la asignación de folio, datos del asegurado, selección de catálogos y vigencia.

---

### 2. Objetivo de Negocio
Garantizar que toda cotización tenga información básica válida, estructurada y consistente para permitir su posterior cálculo y emisión.

---

### 3. Alcance Funcional
Incluye:
- Creación de cotización con folio automático
- Carga de cotización existente
- Captura de datos del asegurado
- Selección de catálogos (tipo seguro, moneda, canal)
- Definición de vigencia

No incluye:
- Cálculo de primas
- Gestión de coberturas

---

### 4. Historias de Usuario

| HU | Nombre | Descripción corta |
|----|------|------------------|
| HU-001 | Iniciar nueva cotización | Genera folio automático |
| HU-002 | Cargar cotización | Consulta por folio |
| HU-003 | Datos del asegurado | Nombre + RFC |
| HU-004 | Selección de catálogos | Tipo, moneda, canal |
| HU-005 | Vigencia | Fechas inicio/fin |

---

### 5. Flujo Funcional
1. Usuario inicia cotización (HU-001)
2. Puede cargar existente (HU-002)
3. Captura datos (HU-003)
4. Selecciona catálogos (HU-004)
5. Define vigencia (HU-005)

---

### 6. Dependencias Técnicas
- Servicio de folios (`Plataforma-core-ohs`)
- API de cotizaciones
- Servicios de catálogos

---

### 7. Consideraciones Técnicas
- Idempotencia en creación
- Validaciones frontend + backend
- Manejo de errores en integraciones externas

---