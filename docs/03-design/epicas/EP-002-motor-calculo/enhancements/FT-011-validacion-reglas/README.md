## FT-011: Motor de Validación de Reglas de Negocio

---

### 1. Descripción

Esta feature define el motor central de validación de reglas de negocio aplicado a cotizaciones y ubicaciones de riesgo, encargado de asegurar consistencia, completitud y validez de los datos antes de ejecutar procesos de cálculo de primas.

---

### 2. Objetivo de Negocio

Garantizar que únicamente datos válidos, completos y coherentes participen en el proceso de cálculo, reduciendo errores de suscripción, mejorando la calidad del riesgo evaluado y evitando cálculos inválidos o incompletos.

---

### 3. Alcance Funcional

Incluye:

* Validación de rangos de sumas aseguradas

  * Límites mínimos y máximos por cobertura
* Validación de códigos postales contra catálogo

  * Existencia en `catalogo_cp_zonas`
* Validación de datos mínimos por ubicación

  * Código postal válido
  * Giro con `claveIncendio`
  * Garantías tarifables
* Clasificación de estado de ubicación:

  * COMPLETA
  * INCOMPLETA
  * INACTIVA
* Generación de mensajes de error estructurados
* Agregación de alertas bloqueantes por ubicación
* Control de elegibilidad para cálculo de primas

No incluye:

* Ejecución del cálculo de primas (FT-009 / motor de cálculo)
* Persistencia de datos (FT-008)
* Integraciones externas (FT-007 / FT-010)
* UI de captura o edición de cotización
* Reglas de pricing (solo validación previa)

---

### 4. Historias de Usuario

| HU     | Nombre                                    | Descripción corta                                          |
| ------ | ----------------------------------------- | ---------------------------------------------------------- |
| HU-165 | Validación de sumas aseguradas            | Validar rangos mínimos y máximos por cobertura             |
| HU-166 | Validación de códigos postales            | Verificar CP contra catálogo de zonas                      |
| HU-167 | Validación de datos mínimos por ubicación | Determinar si una ubicación es válida para cálculo         |
| HU-168 | Mensajes de error de validación           | Generar errores claros y estructurados                     |
| HU-169 | Control de ejecución de cálculo           | Evitar cálculo sin ubicaciones válidas y filtrar inválidas |

---

### 5. Flujo Funcional

1. El sistema recibe una cotización con una o más ubicaciones
2. El motor de validación ejecuta reglas en secuencia:

   * Validación de sumas aseguradas por cobertura
   * Validación de códigos postales contra catálogo
   * Validación de datos mínimos por ubicación
3. Cada ubicación recibe un estado:

   * COMPLETA → elegible para cálculo
   * INCOMPLETA → excluida con alertas
   * INACTIVA → ignorada del proceso
4. Se generan resultados estructurados:

   * `alertasBloqueantes`
   * `estadoValidacion`
5. Si existen errores:

   * Se construye un reporte de validación consolidado
6. Antes del cálculo:

   * Se filtran solo ubicaciones COMPLETAS
7. Ejecución de reglas finales:

   * Si no hay ubicaciones válidas → se bloquea cálculo
   * Si hay al menos una válida → se ejecuta cálculo parcial
8. El resultado incluye:

   * Ubicaciones calculadas
   * Ubicaciones excluidas con motivos
   * Mensajes de validación estructurados

---

### 6. Dependencias Técnicas

* Motor de reglas de validación (core domain service)
* Catálogo CP-Zonas (FT-010 / HU-163)
* Parámetros de tarifas y coberturas (FT-010)
* Servicio de validación de negocio (FT-009)
* Endpoint de cálculo de primas (FT-009 / FT-004 implícito)
* Modelos de dominio de Ubicación y Cotización
* Sistema de errores estructurados (error catalog / validator response model)
* Capa de agregación de validaciones por ubicación

---

### 7. Consideraciones Técnicas

* El motor de validación actúa como **gate previo al motor de cálculo**
* Validaciones deben ser:

  * Determinísticas
  * Reproducibles
  * Independientes del orden de ejecución cuando sea posible
* Separación clara entre:

  * Validación de campo (schema-level)
  * Validación de dominio (business rules)
* `estadoValidacion` debe ser un derivado puro de reglas
* `alertasBloqueantes` debe ser acumulativo y no sobrescribirse
* El sistema debe soportar:

  * Validación parcial por ubicación
  * Cálculo parcial basado en subconjunto válido
* Diseño recomendado:

  * Specification Pattern o Rule Engine ligero
* Mensajes de error deben:

  * Ser específicos por regla
  * Referenciar campo + causa + regla
* Evitar bloqueo global por errores locales:

  * Solo bloquea si no existe ninguna ubicación válida
* Optimizar validación de CP contra catálogo:

  * Uso de índices o estructuras en memoria si aplica
* Trazabilidad de validación:

  * Cada decisión de estado debe ser explicable

---
