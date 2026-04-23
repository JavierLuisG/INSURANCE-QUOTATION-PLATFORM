## FT-009: Implementación de Reglas de Negocio y Validaciones

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: core

### 1. Descripción

Esta feature define e implementa las reglas de negocio y validaciones necesarias para garantizar la calidad, consistencia y precisión de la información en todo el flujo de la cotización, desde la captura de datos hasta el cálculo de primas.

---

### 2. Objetivo de Negocio

Asegurar que los datos ingresados y procesados en el cotizador sean válidos, coherentes y alineados con las reglas del negocio, reduciendo errores operativos y mejorando la confiabilidad del cálculo.

---

### 3. Alcance Funcional

Incluye:

* Validación de datos generales de cotización
* Validación de datos de ubicaciones de riesgo
* Aplicación de reglas de negocio en cálculo de primas
* Gestión y visualización de mensajes de error
* Documentación y trazabilidad de reglas de negocio

No incluye:

* Definición funcional de nuevas reglas (solo implementación)
* Motores de reglas externos (si no están contemplados en arquitectura)

---

### 4. Historias de Usuario

| HU     | Nombre                     | Descripción corta              |
| ------ | -------------------------- | ------------------------------ |
| HU-039 | Validación datos generales | RFC, vigencia, obligatorios    |
| HU-040 | Validación ubicaciones     | Dirección, CP, valores         |
| HU-041 | Reglas de cálculo          | Factores, recargos, descuentos |
| HU-042 | Mensajes de error          | Feedback claro al usuario      |
| HU-043 | Documentación reglas       | Trazabilidad y mantenimiento   |

---

### 5. Flujo Funcional

1. Usuario captura o modifica datos
2. Sistema ejecuta validaciones (frontend + backend)
3. Si hay errores → se bloquea operación y se muestran mensajes
4. Si es válido → se permite persistencia o cálculo
5. Durante el cálculo → se aplican reglas de negocio
6. Se generan resultados consistentes y trazables

---

### 6. Dependencias Técnicas

* API de cotizaciones (validaciones y reglas)
* Motor de cálculo de primas
* Servicios de referencia (`Plataforma-core-ohs`)
* Features FT-001, FT-002, FT-004

---

### 7. Consideraciones Técnicas

* Validaciones duplicadas: frontend (UX) + backend (seguridad)
* Separación clara entre validaciones y reglas de negocio
* Reglas parametrizables (evitar hardcode)
* Estandarización de errores (códigos + mensajes)
* Soporte para warnings vs errores bloqueantes
* Trazabilidad entre regla → implementación → documentación
