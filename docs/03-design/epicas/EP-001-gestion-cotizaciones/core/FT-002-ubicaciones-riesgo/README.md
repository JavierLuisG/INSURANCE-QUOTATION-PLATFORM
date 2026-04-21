## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

### 1. Descripción

Esta feature permite la creación, edición, gestión y control del ciclo de vida de las ubicaciones de riesgo dentro de una cotización, asegurando que cada ubicación contenga información completa, válida y estructurada para su uso en validaciones y cálculos posteriores.

---

### 2. Objetivo de Negocio

Permitir la correcta definición y administración de múltiples ubicaciones de riesgo dentro de una cotización, garantizando integridad de datos, trazabilidad y preparación adecuada para el cálculo de primas.

---

### 3. Alcance Funcional

Incluye:

* Creación de múltiples ubicaciones de riesgo
* Edición completa de datos de ubicación
* Marcado de ubicaciones como inactivas (soft delete)
* Validación de código postal contra catálogo externo
* Visualización de alertas por datos incompletos
* Asociación de ubicaciones a una cotización

No incluye:

* Validaciones avanzadas de negocio (FT-011)
* Cálculo de primas (FT-012)
* Persistencia de resultados de cálculo (FT-013)

---

### 4. Historias de Usuario

| HU     | Nombre                | Descripción corta                            |
| ------ | --------------------- | -------------------------------------------- |
| HU-006 | Agregar ubicación     | Crea una nueva ubicación en la cotización    |
| HU-007 | Editar ubicación      | Modifica datos completos de una ubicación    |
| HU-008 | Inactivar ubicación   | Marca ubicación como inactiva sin eliminarla |
| HU-009 | Validar código postal | Verifica CP contra catálogo externo          |
| HU-010 | Alertas visuales      | Indica errores o datos incompletos           |

---

### 5. Flujo Funcional

1. Usuario crea o abre una cotización
2. Agrega una nueva ubicación (HU-006)
3. Ingresa y edita datos de la ubicación (HU-007)
4. Sistema valida código postal y autocompleta información (HU-009)
5. Sistema evalúa completitud y genera alertas visuales (HU-010)
6. Usuario puede marcar ubicación como inactiva (HU-008)
7. Ubicaciones activas quedan disponibles para validación y cálculo

---

### 6. Dependencias Técnicas

* API de cotizaciones (gestión de ubicaciones)
* Servicio de catálogo de códigos postales (`Plataforma-core-ohs`)
* Módulo de validaciones (FT-009)
* Modelo de dominio de Ubicación
* Componentes UI de gestión (tabs / maestro-detalle)

---

### 7. Consideraciones Técnicas

* Las ubicaciones deben manejarse como **entidades dentro del agregado Cotización**
* No se eliminan físicamente (soft delete mediante `estadoValidacion = INACTIVA`)
* Límite configurable de ubicaciones (ej. máximo 10)
* Actualizaciones deben ser parciales (`PATCH`) para evitar sobreescritura
* El estado de la ubicación (`COMPLETA`, `INCOMPLETA`, `INACTIVA`) debe calcularse automáticamente
* Integración con catálogo de CP debe ser eficiente (posible cache)
* Validaciones pueden ser híbridas: frontend (formato) + backend (existencia)
* UI debe reflejar estado mediante indicadores claros (UX-driven validation)
* Preparado para consumo por motores de validación y cálculo posteriores
* Estructura de datos debe permitir extensibilidad (nuevos campos de riesgo en el futuro)

---
