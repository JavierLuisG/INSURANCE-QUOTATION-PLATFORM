## FT-010: Configuración y Gestión de Parámetros de Cálculo

---

### 1. Descripción

Esta feature define la gestión centralizada de parámetros técnicos y tarifarios utilizados en el cálculo de primas, mediante la integración con servicios externos o simulados, asegurando su disponibilidad consistente para los motores de validación y cálculo.

---

### 2. Objetivo de Negocio

Garantizar que el sistema de cotización disponga de tarifas, factores técnicos y catálogos actualizados y consistentes, permitiendo cálculos precisos, alineados con reglas actuariales y condiciones de riesgo.

---

### 3. Alcance Funcional

Incluye:

* Consumo de tarifas desde servicio externo o mock:

  * Incendio
  * CAT (Catástrofe)
  * FHM (Fenómenos Hidrometeorológicos)
* Consumo de factores técnicos:

  * Equipo electrónico
* Consumo de catálogos de referencia:

  * Códigos postales y zonas de riesgo
* Mapeo de datos externos a modelo interno
* Disponibilización de parámetros para motores internos:

  * Motor de validación
  * Motor de cálculo
* Manejo de errores de integración con servicios de parámetros

No incluye:

* Lógica de cálculo de primas (FT-009 / motor central)
* Reglas de negocio de validación (FT-009)
* Persistencia de cotizaciones (FT-008)
* UI de administración de tarifas
* Versionamiento histórico de parámetros

---

### 4. Historias de Usuario

| HU     | Nombre                       | Descripción corta                                    |
| ------ | ---------------------------- | ---------------------------------------------------- |
| HU-159 | Tarifas de incendio          | Consumir o simular tarifas base de incendio          |
| HU-160 | Tarifas CAT                  | Consumir factores de catástrofe por zona             |
| HU-161 | Tarifa FHM                   | Consumir cuotas de fenómenos hidrometeorológicos     |
| HU-162 | Factores equipo electrónico  | Consumir factores técnicos de equipos electrónicos   |
| HU-163 | Catálogo CP-Zonas            | Relación entre códigos postales y zonas de riesgo    |
| HU-164 | Disponibilidad de parámetros | Exponer parámetros a motores de cálculo y validación |

---

### 5. Flujo Funcional

1. El sistema requiere parámetros para validación o cálculo
2. Se consulta el adaptador de parámetros:

   * Servicio externo `Plataforma-core-ohs` o mock
3. Se obtienen respuestas de:

   * Tarifas (Incendio, CAT, FHM)
   * Factores técnicos
   * Catálogo CP-Zonas
4. Se realiza procesamiento:

   * Normalización de estructuras
   * Mapeo a modelo interno estándar
5. Se almacenan en repositorio en memoria o caché funcional
6. Los motores internos consumen los parámetros:

   * Motor de validación → reglas de consistencia
   * Motor de cálculo → determinación de prima
7. En caso de error:

   * Se registra incidente
   * Se mantiene último valor válido si existe (fallback opcional)

---

### 6. Dependencias Técnicas

* Cliente HTTP para integración con `Plataforma-core-ohs`
* Mock server base (FT-020)
* Repositorio de parámetros (in-memory o cache distribuida)
* Mapeadores DTO → modelo interno
* Capa de abstracción (ports/adapters)
* Motor de validación (FT-009)
* Motor de cálculo de primas (FT-004 / FT-006 implícito)
* Sistema de logging y monitoreo de integraciones

---

### 7. Consideraciones Técnicas

* Diseñar capa de parámetros como **fuente única de verdad en runtime**
* Separar claramente:

  * Fetch de datos (integración)
  * Resolución de parámetros (servicio interno)
* Implementar estrategia de **cache con invalidación controlada**
* Garantizar consistencia entre motores:

  * Validación y cálculo deben consumir el mismo set de parámetros
* Manejo de fallos:

  * Retry + fallback a último valor conocido (si aplica)
* Evitar acoplamiento directo del motor de cálculo a servicios externos
* Definir contratos estables para cada tipo de parámetro
* Diseñar el sistema para soportar:

  * Extensión de nuevos parámetros sin cambios estructurales
* Considerar separación por contexto:

  * Tarificación
  * Riesgo
  * Factores técnicos
* Trazabilidad básica de qué versión de parámetros fue usada en cálculo (aunque no haya versionado formal aún)

---
