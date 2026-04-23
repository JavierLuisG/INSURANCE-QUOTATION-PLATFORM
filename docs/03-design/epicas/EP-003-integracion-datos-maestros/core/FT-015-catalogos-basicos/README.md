## FT-015: Conectividad y Consumo de Catálogos Básicos (Suscriptores, Agentes, Giros)

**Épica Padre**: EP-003 — Integración y Gestión de Datos Maestros
**Capa**: core

### 1. Descripción

Esta feature permite la integración con servicios externos de catálogos básicos para obtener y transformar información de suscriptores, agentes y giros, asegurando su disponibilidad para el proceso de cotización.

---

### 2. Objetivo de Negocio

Garantizar que el sistema utilice información actualizada y confiable en la captura de datos, reduciendo errores manuales y asegurando consistencia con sistemas externos.

---

### 3. Alcance Funcional

Incluye:

* Conexión a servicios externos de catálogos
* Recuperación de catálogos de suscriptores, agentes y giros
* Transformación de datos al modelo interno
* Manejo de errores y reintentos en integraciones
* Soporte para entornos simulados (mock)

No incluye:

* Gestión interna de catálogos (persistencia propia avanzada)
* Parámetros técnicos de cálculo (FT-010)
* Validaciones de negocio (FT-009)

---

### 4. Historias de Usuario

| HU     | Nombre                | Descripción corta               |
| ------ | --------------------- | ------------------------------- |
| HU-068 | Conexión a catálogos  | Configuración e integración     |
| HU-069 | Catálogo suscriptores | Lista para selección            |
| HU-070 | Catálogo agentes      | Lista para selección            |
| HU-071 | Catálogo giros        | Lista para selección            |
| HU-072 | Mapeo de datos        | Transformación a modelo interno |
| HU-073 | Manejo de errores     | Reintentos y resiliencia        |

---

### 5. Flujo Funcional

1. Sistema establece conexión con servicio externo (HU-068)
2. Se solicitan catálogos requeridos (HU-069, HU-070, HU-071)
3. Se transforman los datos al modelo interno (HU-072)
4. Se almacenan temporalmente o se exponen a la UI
5. En caso de error, se aplican reintentos y manejo de fallos (HU-073)

---

### 6. Dependencias Técnicas

* FT-020: Simulación de servicios externos
* API externa `Plataforma-core-ohs`
* Cliente HTTP (REST)
* Librerías de resiliencia (ej. Resilience4j)

---

### 7. Consideraciones Técnicas

* Implementación de adaptadores (hexagonal architecture)
* Uso de DTOs para desacoplar contratos externos
* Configuración externalizada (URL, timeouts, credenciales)
* Estrategias de retry con backoff exponencial
* Posible uso de caché para mejorar performance
* Manejo de errores diferenciando fallos técnicos vs funcionales

---