## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: core

### 1. Descripción

Esta feature permite gestionar el ciclo de vida de una cotización mediante la transición entre estados, desde su creación hasta su posible emisión, incluyendo visualización del estado actual.

---

### 2. Objetivo de Negocio

Controlar el flujo de la cotización asegurando trazabilidad, gobernanza del proceso y coherencia en las etapas del negocio.

---

### 3. Alcance Funcional

Incluye:

* Inicialización en estado "Borrador"
* Transición automática a "Calculada"
* Cambio manual a "Aprobada" o "Rechazada"
* Cambio a "Emitida" para cotizaciones aprobadas
* Visualización del estado actual

No incluye:

* Proceso real de emisión de póliza
* Integración con sistemas externos de emisión
* Auditoría avanzada de estados (histórico detallado)

---

### 4. Historias de Usuario

| HU     | Nombre             | Descripción corta      |
| ------ | ------------------ | ---------------------- |
| HU-024 | Estado inicial     | Cotización en borrador |
| HU-025 | Estado calculada   | Post cálculo exitoso   |
| HU-026 | Aprobación/Rechazo | Cambio manual          |
| HU-027 | Emitida            | Estado final           |
| HU-028 | Ver estado         | Visualización actual   |

---

### 5. Flujo Funcional

1. Usuario inicia cotización → estado "Borrador" (HU-024)
2. Usuario ejecuta cálculo → estado "Calculada" (HU-025)
3. Usuario decide aprobar o rechazar (HU-026)
4. Si es aprobada, puede pasar a "Emitida" (HU-027)
5. En todo momento se visualiza el estado actual (HU-028)

---

### 6. Dependencias Técnicas

* API de cotizaciones
* Motor de cálculo (para transición a "Calculada")
* Reglas de negocio de estados

---

### 7. Consideraciones Técnicas

* Definir máquina de estados explícita (state machine)
* Validar transiciones permitidas (no saltos inválidos)
* Manejo de concurrencia en cambios de estado
* Persistencia del estado en la cotización
* Posibilidad de extender estados sin romper el flujo actual
