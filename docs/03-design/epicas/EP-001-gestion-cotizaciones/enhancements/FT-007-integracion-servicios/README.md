## FT-007: Integración con Servicios de Referencia (Catálogos y Tarifas)

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: enhancements

---

### 1. Descripción

Esta feature permite la integración del cotizador con servicios externos (`Plataforma-core-ohs` o su mock) para consumir catálogos, códigos postales, tarifas y factores técnicos necesarios para la operación del sistema.

---

### 2. Objetivo de Negocio

Garantizar que el cotizador utilice información actualizada y consistente (catálogos, zonas de riesgo, tarifas) para la captura de datos y el cálculo de primas.

---

### 3. Alcance Funcional

Incluye:

* Consumo de catálogos:

  * Suscriptores
  * Agentes
  * Giros
* Consulta de códigos postales y zonas de riesgo
* Consumo de:

  * Clasificación de riesgo
  * Garantías
* Consulta de tarifas:

  * Incendio
  * CAT
  * FHM
* Consulta de factores técnicos
* Manejo de errores de integración
* Mapeo de datos externos a modelo interno

No incluye:

* Lógica de cálculo de primas (FT-004)
* Validaciones de negocio internas
* Persistencia de datos de cotización

---

### 4. Historias de Usuario

| HU     | Nombre             | Descripción corta                        |
| ------ | ------------------ | ---------------------------------------- |
| HU-144 | Catálogos básicos  | Consumir suscriptores, agentes y giros   |
| HU-145 | Códigos postales   | Consultar CP y zonas de riesgo           |
| HU-146 | Riesgo y garantías | Consumir catálogos de riesgo y garantías |
| HU-147 | Tarifas y factores | Consumir tarifas y factores técnicos     |
| HU-148 | Manejo de errores  | Manejar fallos de integración            |

---

### 5. Flujo Funcional

1. El sistema requiere datos externos (catálogo, CP, tarifas)
2. Se realiza llamada a `Plataforma-core-ohs` (o mock)
3. Se recibe la respuesta:

   * Se mapea al modelo interno
   * Se pone disponible para:

     * UI (catálogos)
     * lógica de negocio (tarifas, factores)
4. Si ocurre un error:

   * Se registra
   * Se maneja (retry, fallback o notificación)

---

### 6. Dependencias Técnicas

* Mock server (`FT-020`)
* Cliente HTTP (REST)
* Capa de integración/adaptadores
* Módulo de mapeo (DTO → modelo interno)
* Sistema de logging
* Posible caché de catálogos

---

### 7. Consideraciones Técnicas

* Uso de **adaptadores** para desacoplar el servicio externo
* Mapeo robusto ante cambios de contrato
* Implementar:

  * Reintentos (retry)
  * Circuit breaker
  * Timeouts
* Manejo de errores diferenciando:

  * conexión
  * datos inválidos
* Cachear catálogos de baja variabilidad
* Validar consistencia antes de exponer datos al dominio
* Mantener contratos externos aislados del core del sistema

---