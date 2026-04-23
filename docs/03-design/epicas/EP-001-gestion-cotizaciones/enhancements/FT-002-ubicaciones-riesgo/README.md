## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: enhancements

### 1. Descripción

Esta feature permite la gestión completa de ubicaciones de riesgo dentro de una cotización, incluyendo la creación, edición, inactivación, visualización y validación de datos del dominio asociados a cada ubicación, así como la retroalimentación visual sobre su estado.

---

### 2. Objetivo de Negocio

Permitir la correcta definición y administración de múltiples ubicaciones de riesgo dentro de una cotización, asegurando que la información capturada sea completa, válida y útil para el cálculo de primas.

---

### 3. Alcance Funcional

Incluye:

* Agregar nuevas ubicaciones con datos completos del dominio
* Editar ubicaciones existentes
* Marcar ubicaciones como inactivas (soft delete lógico)
* Visualizar múltiples ubicaciones en una interfaz estructurada
* Validar código postal y obtener zonificación
* Mostrar alertas por datos incompletos o inválidos

No incluye:

* Cálculo de primas (FT-012)
* Aplicación de reglas de negocio avanzadas (FT-011)

---

### 4. Historias de Usuario

| HU     | Nombre                 | Descripción corta                          |
| ------ | ---------------------- | ------------------------------------------ |
| HU-115 | Agregar ubicación      | Crea nueva ubicación con datos del dominio |
| HU-116 | Editar ubicación       | Modifica datos y recalcula validaciones    |
| HU-117 | Inactivar ubicación    | Excluye ubicación sin eliminarla           |
| HU-118 | Visualizar ubicaciones | Lista y navega entre ubicaciones           |
| HU-119 | Validar código postal  | Verifica CP y obtiene zona                 |
| HU-120 | Alertas de datos       | Indica datos incompletos o inválidos       |

---

### 5. Flujo Funcional

1. Usuario agrega una nueva ubicación (HU-115)
2. Captura datos del dominio en formulario
3. Sistema valida código postal y obtiene zona (HU-119)
4. Usuario edita información según sea necesario (HU-116)
5. Sistema recalcula `estadoValidacion` y `alertasBloqueantes`
6. Usuario visualiza todas las ubicaciones en interfaz maestro-detalle (HU-118)
7. Sistema muestra alertas visuales si existen inconsistencias (HU-120)
8. Usuario puede marcar ubicación como inactiva (HU-117)
9. Ubicaciones activas quedan disponibles para procesos posteriores

---

### 6. Dependencias Técnicas

* API de cotizaciones (gestión de ubicaciones)
* Servicio de códigos postales y zonas (catálogo CP-Zonas)
* Módulo de validación de datos de ubicación
* Modelo de dominio de ubicación de riesgo
* Frontend (formularios dinámicos, navegación maestro-detalle)

---

### 7. Consideraciones Técnicas

* Uso de **soft delete** mediante `estadoValidacion = INACTIVA`
* Reglas de validación ejecutadas en backend con retroalimentación al frontend
* Cálculo automático de `estadoValidacion` y `alertasBloqueantes`
* Validación asíncrona del código postal (UX fluida)
* Control de límite configurable de ubicaciones
* Uso de `PATCH` para actualizaciones parciales (alineado con FT-008)
* Separación clara entre estado visual (UI) y estado de negocio (validación)
* Preparar estructura para integración con cálculo (exclusión de ubicaciones inactivas)
