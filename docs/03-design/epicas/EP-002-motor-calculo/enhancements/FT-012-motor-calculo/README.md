## FT-012: Motor Central de Cálculo de Primas

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: enhancements

---

### 1. Descripción

Esta feature define el motor central encargado del cálculo de primas por ubicación y consolidación de la prima comercial de una cotización, aplicando tarifas, factores técnicos y componentes de riesgo de forma estructurada, trazable y determinística.

---

### 2. Objetivo de Negocio

Calcular de forma precisa, consistente y auditable el costo total de una cotización, integrando todos los factores técnicos, comerciales y de riesgo, garantizando transparencia en el desglose del cálculo para cada ubicación asegurada.

---

### 3. Alcance Funcional

Incluye:

* Cálculo de prima neta por ubicación

  * Solo ubicaciones con `estadoValidacion: COMPLETA`
* Aplicación de factores de riesgo:

  * CAT (catástrofe)
  * FHM (fenómenos hidrometeorológicos)
* Cálculo de prima comercial total

  * Suma de primas netas
  * Aplicación de factores comerciales
* Desglose de primas por ubicación

  * Componentes técnicos individuales
* Implementación de 14 componentes técnicos de cálculo
* Consolidación final de resultados financieros
* Control de exclusión de ubicaciones no válidas
* Trazabilidad completa del cálculo por componente

No incluye:

* Validaciones de negocio (FT-011)
* Reglas de validación de datos (FT-009)
* Integración con servicios externos (FT-007 / FT-010)
* Persistencia de datos (FT-008)
* UI de cotización o resultados
* Definición de tarifas o parámetros externos

---

### 4. Historias de Usuario

| HU     | Nombre                    | Descripción corta                                 |
| ------ | ------------------------- | ------------------------------------------------- |
| HU-170 | Prima neta por ubicación  | Cálculo base por ubicación válida                 |
| HU-171 | Factores CAT y FHM        | Aplicación de factores de riesgo                  |
| HU-172 | Prima comercial total     | Consolidación final de prima                      |
| HU-173 | Desglose por ubicación    | Detalle del cálculo por ubicación                 |
| HU-174 | Precisión de fórmulas     | Garantizar exactitud matemática y consistencia    |
| HU-175 | Componentes técnicos (14) | Cálculo modular de todos los componentes técnicos |

---

### 5. Flujo Funcional

1. El sistema recibe una cotización validada
2. El motor de cálculo filtra ubicaciones:

   * Solo `COMPLETA` participan en cálculo
   * `INCOMPLETA` se excluyen con registro de motivo
3. Para cada ubicación válida:

   * Se calculan los 14 componentes técnicos
   * Se aplican tarifas base (incendio, etc.)
   * Se aplican factores técnicos (CAT, FHM, equipo electrónico)
4. Se genera:

   * Prima neta por ubicación
   * Desglose detallado por componente
5. Se consolidan resultados:

   * Suma de primas netas
   * Aplicación de factores comerciales
6. Se obtiene:

   * Prima comercial total final
   * Desglose completo por ubicación
7. Se garantiza:

   * Precisión numérica consistente
   * Reproducibilidad del cálculo
8. Se persiste (si aplica en capas superiores):

   * Snapshot detallado de cálculo por componente

---

### 6. Dependencias Técnicas

* Motor de validación de reglas (FT-011)
* Motor de parámetros y tarifas (FT-010)
* Servicios de catálogos y factores (FT-007 / FT-010)
* Repositorio de parámetros de cálculo
* Modelos de dominio de cotización y ubicación
* Servicio de cálculo central (domain service / application service)
* Módulos de cálculo por componente (strategy pattern recomendado)
* Sistema de trazabilidad de cálculos (logging estructurado o snapshot model)
* Capa de consolidación financiera

---

### 7. Consideraciones Técnicas

* El motor de cálculo debe ser **determinístico y puro**

  * Mismos inputs → mismos outputs
* Diseño altamente modular:

  * Cada uno de los 14 componentes debe ser independiente
* Arquitectura recomendada:

  * Strategy Pattern para componentes
  * Pipeline de cálculo por etapas
* Separación estricta entre:

  * Cálculo técnico
  * Consolidación comercial
* Precisión numérica:

  * Uso de `BigDecimal` o equivalente
  * Evitar floating point
* Trazabilidad obligatoria:

  * Cada componente debe registrar input/output
* El motor debe soportar:

  * Cálculo parcial por ubicación
  * Exclusión explícita de ubicaciones inválidas
* Pruebas unitarias con alta cobertura (>90%):

  * Cada componente debe ser testeable aisladamente
* Extensibilidad:

  * Nuevos componentes sin modificar el core del motor
* Reglas de negocio deben ser configurables vía parámetros, no hardcodeadas
* Consolidación final debe ser independiente del cálculo por componente
* El diseño debe priorizar:

  * Claridad sobre optimización prematura
  * Trazabilidad sobre complejidad implícita

---
