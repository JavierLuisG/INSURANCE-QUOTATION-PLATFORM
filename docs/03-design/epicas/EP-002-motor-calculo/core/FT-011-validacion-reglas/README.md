## FT-011: Motor de Validación de Reglas de Negocio

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: core

### 1. Descripción

Esta feature implementa un motor especializado en la validación de reglas de negocio sobre la cotización y sus ubicaciones, asegurando que los datos cumplan condiciones mínimas y restricciones antes de permitir el cálculo de primas.

---

### 2. Objetivo de Negocio

Prevenir errores en el cálculo y garantizar que todas las cotizaciones se procesen únicamente con información válida, completa y coherente con las políticas de suscripción.

---

### 3. Alcance Funcional

Incluye:

* Validación de rangos de suma asegurada
* Validación de código postal y zona
* Verificación de datos mínimos por ubicación
* Generación estructurada de errores
* Bloqueo del cálculo ante inconsistencias

No incluye:

* Cálculo de primas
* Definición de tarifas o parámetros (solo validación)

---

### 4. Historias de Usuario

| HU     | Nombre                | Descripción corta         |
| ------ | --------------------- | ------------------------- |
| HU-049 | Rangos suma asegurada | Validación de límites     |
| HU-050 | Validación CP/Zona    | Clasificación geográfica  |
| HU-051 | Datos mínimos         | Campos obligatorios       |
| HU-052 | Mensajes de error     | Feedback claro            |
| HU-053 | Bloqueo de cálculo    | Pre-condición obligatoria |

---

### 5. Flujo Funcional

1. Usuario captura/modifica datos
2. Se ejecuta motor de validación
3. Se evalúan reglas sobre cotización y ubicaciones
4. Se generan errores (si existen)
5. Si hay errores → se bloquea cálculo
6. Si no hay errores → se habilita cálculo

---

### 6. Dependencias Técnicas

* FT-010 (parámetros y catálogos)
* FT-002 (modelo de ubicaciones)
* Orquestador de cálculo (FT-004)
* Servicio de catálogos (`Plataforma-core-ohs`)

---

### 7. Consideraciones Técnicas

* Motor desacoplado (reglas independientes del flujo)
* Reglas parametrizables (config-driven)
* Evaluación eficiente para múltiples ubicaciones
* Estructura estándar de errores (código, campo, mensaje)
* Validaciones determinísticas (sin efectos secundarios)
* Integración como pre-condición obligatoria del cálculo
