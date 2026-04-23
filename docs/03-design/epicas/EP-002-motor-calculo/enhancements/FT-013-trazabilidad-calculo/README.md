## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: enhancements

---

### 1. Descripción

Esta feature define la capa de persistencia de resultados del motor de cálculo, asegurando que las primas calculadas, sus desgloses y la información de ejecución queden almacenados dentro de la cotización, junto con mecanismos de auditoría y trazabilidad completa del proceso de cálculo.

---

### 2. Objetivo de Negocio

Garantizar que cada resultado de cálculo sea persistido de forma consistente, atómica y auditable, permitiendo reconstruir cómo se obtuvo una prima específica y asegurando integridad financiera y trazabilidad completa del proceso de cotización.

---

### 3. Alcance Funcional

Incluye:

* Persistencia de resultados de cálculo en la cotización

  * Prima neta total
  * Prima comercial total
  * Desglose por ubicación
* Persistencia atómica del resultado completo
* Actualización de metadatos de cotización tras cálculo

  * `version`
  * `fechaUltimaActualizacion`
* Registro de snapshot de cálculo (trazabilidad completa)

  * Entradas del cálculo
  * Parámetros utilizados
  * Factores aplicados
  * Resultados detallados
  * Metadatos de ejecución
* Soporte para auditoría de cálculos financieros

No incluye:

* Motor de cálculo de primas (FT-012)
* Validaciones de negocio (FT-011)
* Gestión de parámetros o tarifas (FT-010)
* Integraciones externas (FT-007)
* UI de cotización o visualización de resultados
* Reglas de negocio de pricing

---

### 4. Historias de Usuario

| HU     | Nombre                     | Descripción corta                               |
| ------ | -------------------------- | ----------------------------------------------- |
| HU-176 | Persistencia de primas     | Guardar resultados financieros en la cotización |
| HU-177 | Persistencia atómica       | Asegurar consistencia total o rollback completo |
| HU-178 | Actualización de metadatos | Incrementar versión y timestamp tras cálculo    |
| HU-179 | Snapshot de trazabilidad   | Registrar evidencia completa del cálculo        |

---

### 5. Flujo Funcional

1. El motor de cálculo finaliza la ejecución
2. El sistema agrupa resultados:

   * Prima neta total
   * Prima comercial total
   * Desglose por ubicación
3. Se construye snapshot de trazabilidad:

   * Inputs del cálculo
   * Parámetros usados
   * Factores aplicados
   * Resultados intermedios y finales
4. Se inicia persistencia:

   * Se actualiza documento de cotización en MongoDB
5. Operación atómica:

   * Se guardan resultados + snapshot + metadatos en una sola transacción lógica
   * Si falla → rollback completo
6. Se actualizan metadatos:

   * Incremento de `version`
   * Actualización de `fechaUltimaActualizacion`
7. Resultado final:

   * Cotización persistida con resultados consistentes y auditables

---

### 6. Dependencias Técnicas

* Motor de cálculo de primas (FT-012)
* Motor de validación (FT-011, indirectamente)
* Repositorio de cotizaciones (MongoDB)
* Sistema de transacciones o estrategia de atomicidad lógica
* Modelo de dominio de cotización con resultados embebidos
* Módulo de snapshot/auditoría de cálculo
* Integración con sistema de versionado (FT-008)
* Servicios de parámetros y factores (FT-010)

---

### 7. Consideraciones Técnicas

* La persistencia debe ser tratada como una **operación crítica financiera**
* Diseño recomendado:

  * Aggregates en MongoDB (Cotización como raíz única)
* Atomicidad:

  * Simulación de transacción o uso de transacciones si el cluster lo permite
* Snapshot debe ser:

  * Estructurado
  * Versionable
  * No redundante pero suficiente para reconstrucción del cálculo
* Evitar sobrecargar documento principal:

  * Balance entre trazabilidad y tamaño del documento
* Garantizar consistencia entre:

  * Resultado del motor de cálculo
  * Datos persistidos
* Metadatos de ejecución deben incluir:

  * Identificador de ejecución (trace id)
  * Timestamp exacto
  * Versión de cotización
* El snapshot debe permitir:

  * Auditoría posterior sin necesidad de recalcular
* Escritura debe ser idempotente en caso de reintentos
* Separar claramente:

  * Resultados financieros
  * Evidencia de cálculo
  * Metadatos operativos

---
