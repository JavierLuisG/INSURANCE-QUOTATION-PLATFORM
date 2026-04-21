## FT-004: Ejecución y Persistencia del Cálculo de Primas

### 1. Descripción

Esta feature orquesta el proceso completo de cálculo de primas dentro de una cotización, desde la iniciación del cálculo por parte del usuario, pasando por el cálculo individual por ubicación, la consolidación de resultados (prima neta y comercial), la aplicación de reglas de negocio y factores técnicos, hasta la persistencia final de los resultados de forma consistente.

---

### 2. Objetivo de Negocio

Permitir la obtención confiable, consistente y trazable del costo del seguro, asegurando que el cálculo se realice únicamente sobre datos válidos, aplicando correctamente reglas y factores técnicos, y garantizando que los resultados queden persistidos para su consulta y uso posterior.

---

### 3. Alcance Funcional

Incluye:

* Inicio del proceso de cálculo de prima
* Ejecución del cálculo por ubicación de riesgo (filtrado por estado)
* Consolidación de prima neta total y prima comercial
* Aplicación de reglas de negocio y factores técnicos (CAT, FHM, etc.)
* Persistencia de resultados en la cotización
* Manejo de escenarios parciales (ubicaciones válidas vs inválidas)

No incluye:

* Definición de reglas de validación (FT-011)
* Ingestión de parámetros y tarifas (FT-010 / FT-018)

---

### 4. Historias de Usuario

| HU     | Nombre                       | Descripción corta                               |
| ------ | ---------------------------- | ----------------------------------------------- |
| HU-125 | Iniciar cálculo              | Dispara el proceso de cálculo de la cotización  |
| HU-126 | Calcular prima total         | Calcula prima neta y comercial consolidada      |
| HU-127 | Calcular prima por ubicación | Ejecuta cálculo individual por ubicación válida |
| HU-128 | Persistir resultados         | Guarda resultados del cálculo en la cotización  |
| HU-129 | Aplicar reglas y factores    | Integra reglas de negocio y factores técnicos   |

---

### 5. Flujo Funcional

1. Usuario inicia el cálculo desde la UI (HU-125)
2. Sistema valida si existen ubicaciones calculables (`estadoValidacion: COMPLETA`)

   * Si no existen → se bloquea el proceso
   * Si existen → continúa
3. Sistema filtra ubicaciones:

   * Incluye: `COMPLETA`
   * Excluye: `INCOMPLETA`, `INACTIVA`
4. Sistema ejecuta cálculo por ubicación (HU-127):

   * Aplica coberturas y parámetros configurados
   * Aplica factores técnicos (CAT, FHM, etc.)
5. Sistema aplica reglas de negocio (HU-129)
6. Sistema consolida resultados:

   * Suma primas netas
   * Aplica factores comerciales → prima comercial total (HU-126)
7. Sistema genera estructura de resultados (totales + desglose)
8. Sistema persiste resultados de forma atómica (HU-128):

   * Actualiza documento de cotización
   * Incrementa versión
   * Cambia estado a “Calculada”
9. Sistema responde al frontend con resultados
10. UI muestra resultados y alertas de ubicaciones excluidas

---

### 6. Dependencias Técnicas

* Motor Central de Cálculo (dominio)
* Servicio de parámetros y tarifas (FT-010 / FT-018)
* Módulo de validación (FT-011)
* API de cotizaciones (`POST /v1/quotes/{folio}/calculate`)
* Repositorio de cotizaciones (MongoDB)
* Módulo de reglas de negocio
* Sistema de versionado optimista (FT-014)

---

### 7. Consideraciones Técnicas

* El cálculo debe ser **idempotente**: mismo input → mismo output
* Separar claramente:

  * **Orquestación** (application layer)
  * **Lógica de cálculo** (domain layer)
* Filtrado explícito de ubicaciones:

  * No mezclar lógica de validación dentro del motor de cálculo
* Diseño recomendado:

  * `CalculationOrchestrator`
  * `PremiumCalculationService`
  * `LocationCalculationStrategy`
* Uso de **Value Objects** para:

  * Sumas aseguradas
  * Primas
  * Factores
* Evitar efectos colaterales durante el cálculo (pure functions)
* Persistencia debe ser:

  * Atómica
  * Versionada
  * Consistente con FT-013 y FT-014
* Manejar escenarios parciales:

  * Cálculo exitoso con exclusión de ubicaciones inválidas
* Logging estructurado para:

  * Debug
  * Auditoría
* Preparar integración con trazabilidad (snapshot de parámetros usados)
* Considerar performance:

  * Cálculo por ubicación potencialmente paralelizable
* Manejo de errores:

  * Fallo en cálculo → no persistir
  * Fallo en persistencia → rollback lógico
* El estado `"Calculada"` debe ser derivado exclusivamente de una ejecución exitosa

---
