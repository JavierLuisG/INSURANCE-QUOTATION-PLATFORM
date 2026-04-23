## FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: enhancements

---

### 1. Descripción

Esta feature introduce capacidades avanzadas de persistencia para el módulo de cotización, incorporando control de concurrencia mediante versionado optimista, trazabilidad de cambios y consistencia transaccional en la gestión de cotizaciones y sus ubicaciones.

---

### 2. Objetivo de Negocio

Garantizar la integridad, consistencia y trazabilidad de las cotizaciones, incluso en escenarios de edición concurrente, asegurando que los datos reflejen siempre el estado más reciente y válido sin pérdida de información.

---

### 3. Alcance Funcional

Incluye:

* Gestión de versionado de cotizaciones

  * Incremento automático de versión en cada edición
  * Inicialización de versión en creación
* Control de concurrencia (optimistic locking)

  * Detección de conflictos por versión desactualizada
* Gestión de auditoría básica

  * Actualización de `fechaUltimaActualizacion`
* Actualización parcial de cotizaciones

  * Persistencia selectiva de campos modificados
* Persistencia transaccional

  * Cotización + ubicaciones como unidad consistente
* Manejo de conflictos de concurrencia

No incluye:

* Lógica de cálculo de primas
* Reglas de negocio del cotizador
* Integraciones externas (catálogos/tarifas)
* Versionado histórico completo tipo event sourcing
* Auditoría avanzada (quién cambió qué campo)

---

### 4. Historias de Usuario

| HU     | Nombre                     | Descripción corta                                    |
| ------ | -------------------------- | ---------------------------------------------------- |
| HU-149 | Incremento de versión      | Incrementar versión automáticamente en cada edición  |
| HU-150 | Fecha de actualización     | Actualizar timestamp en cada modificación            |
| HU-151 | Versionado optimista       | Evitar sobrescritura por concurrencia                |
| HU-152 | Actualización parcial      | Permitir updates parciales de cotización             |
| HU-153 | Persistencia transaccional | Asegurar consistencia entre cotización y ubicaciones |

---

### 5. Flujo Funcional

1. El usuario crea o edita una cotización
2. El sistema determina el tipo de operación:

   * Creación → `version = 1`
   * Edición → `version = version + 1`
3. Se actualiza `fechaUltimaActualizacion`
4. En caso de edición:

   * Se valida la versión contra la persistida
   * Si hay mismatch → se rechaza la operación (conflicto de concurrencia)
5. Si la operación incluye cambios parciales:

   * Solo los campos modificados se persisten
   * Los demás campos permanecen intactos
6. Persistencia final:

   * Cotización + ubicaciones se guardan en una operación transaccional lógica
7. En caso de error:

   * Se ejecuta rollback lógico (no persistencia parcial inconsistente)
   * Se mantiene estado previo

---

### 6. Dependencias Técnicas

* Capa de persistencia del backend (repositorios / gateways)
* Modelo de dominio de Cotización (incluyendo versionado)
* Manejo de transacciones (o estrategia equivalente en MongoDB)
* Módulo de mapeo DTO ↔ dominio
* Sistema de validación de concurrencia
* Manejo de timestamps centralizado
* API de actualización parcial (PATCH o equivalente)
* Dependencias funcionales previas de gestión de ubicaciones

---

### 7. Consideraciones Técnicas

* Implementación de **optimistic locking** basado en campo `version`
* Validación de concurrencia en capa de persistencia o servicio de aplicación
* Uso de `version` como mecanismo único de control (evitar duplicidad de fuentes de verdad)
* `fechaUltimaActualizacion` debe ser generada por el backend (no confiable desde cliente)
* Actualizaciones parciales deben evitar sobrescritura completa del agregado
* Uso de patrones:

  * Repository
  * Unit of Work (conceptual si el stack lo permite)
* Estrategia transaccional:

  * MongoDB: consistencia a nivel de agregado o simulación de atomicidad
* Manejo explícito de errores de concurrencia:

  * Error semántico claro (conflicto de versión)
* Garantizar idempotencia en operaciones críticas de guardado
* Evitar side-effects fuera del boundary de persistencia

---
