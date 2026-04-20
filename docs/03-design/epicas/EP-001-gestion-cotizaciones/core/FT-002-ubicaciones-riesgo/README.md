## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

### 1. Descripción

Esta feature permite la gestión de múltiples ubicaciones de riesgo dentro de una cotización, incluyendo su creación, edición, eliminación, validación de dirección mediante código postal y control de completitud de datos.

---

### 2. Objetivo de Negocio

Permitir la correcta definición de los riesgos a asegurar mediante ubicaciones estructuradas y validadas, garantizando calidad de datos para el cálculo de primas.

---

### 3. Alcance Funcional

Incluye:

* Agregar nuevas ubicaciones de riesgo
* Editar información de ubicaciones
* Eliminar ubicaciones
* Validar código postal contra catálogo
* Autocompletar datos de dirección
* Visualizar alertas por datos incompletos

No incluye:

* Cálculo de prima
* Evaluación de riesgo técnico
* Geolocalización avanzada

---

### 4. Historias de Usuario

| HU     | Nombre                | Descripción corta               |
| ------ | --------------------- | ------------------------------- |
| HU-006 | Agregar ubicación     | Crear nueva ubicación de riesgo |
| HU-007 | Editar ubicación      | Modificar datos de ubicación    |
| HU-008 | Eliminar ubicación    | Remover ubicación               |
| HU-009 | Validar código postal | Consulta y autocompletado       |
| HU-010 | Alertas de datos      | Indicadores de completitud      |

---

### 5. Flujo Funcional

1. Usuario agrega ubicación (HU-006)
2. Ingresa código postal y se valida (HU-009)
3. Sistema autocompleta dirección
4. Usuario edita información adicional (HU-007)
5. Sistema valida completitud (HU-010)
6. Usuario puede eliminar ubicación (HU-008)

---

### 6. Dependencias Técnicas

* API de cotizaciones
* Servicio de catálogos (`Plataforma-core-ohs`)
* Servicio de validación de código postal

---

### 7. Consideraciones Técnicas

* Validación de CP en frontend (formato) y backend (existencia)
* Manejo de latencia en servicio externo
* Uso de autocompletado basado en catálogo
* Control de estado de completitud por ubicación
* Manejo de errores en integraciones externas

---