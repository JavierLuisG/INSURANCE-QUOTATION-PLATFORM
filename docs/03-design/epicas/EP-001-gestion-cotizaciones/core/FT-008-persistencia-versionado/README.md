## FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: core

### 1. Descripción

Esta feature implementa mecanismos avanzados de persistencia para las cotizaciones, incluyendo control de versiones, auditoría de modificaciones, manejo de concurrencia mediante versionado optimista y soporte para actualizaciones parciales.

---

### 2. Objetivo de Negocio

Garantizar la integridad, consistencia y trazabilidad de la información de cotizaciones, evitando pérdida de datos por concurrencia y optimizando las operaciones de actualización.

---

### 3. Alcance Funcional

Incluye:

* Control de versión de cotizaciones
* Registro de última fecha de modificación
* Prevención de conflictos por concurrencia (versionado optimista)
* Actualizaciones parciales de datos

No incluye:

* Versionado histórico completo (audit trail detallado)
* Manejo de eventos de auditoría externos

---

### 4. Historias de Usuario

| HU     | Nombre                | Descripción corta        |
| ------ | --------------------- | ------------------------ |
| HU-035 | Control de versión    | Incremento automático    |
| HU-036 | Fecha actualización   | Timestamp automático     |
| HU-037 | Versionado optimista  | Prevención de conflictos |
| HU-038 | Actualización parcial | Patch de campos          |

---

### 5. Flujo Funcional

1. Usuario edita cotización
2. Sistema valida versión actual
3. Si versión coincide → permite guardar
4. Se actualiza información (total o parcial)
5. Se incrementa versión
6. Se actualiza fecha de modificación
7. Si versión no coincide → se rechaza operación

---

### 6. Dependencias Técnicas

* Base de datos (campo `version`, `fechaUltimaActualizacion`)
* API de cotizaciones (control de concurrencia)
* Todas las features que modifican la cotización (FT-001 a FT-006)

---

### 7. Consideraciones Técnicas

* Uso de versionado optimista (campo `version` o ETag)
* Operaciones de persistencia deben ser atómicas
* Validación de versión en cada update
* Soporte para operaciones `PATCH` o updates parciales
* Manejo explícito de errores de concurrencia en frontend
* Formato estándar de fechas (ISO 8601)
