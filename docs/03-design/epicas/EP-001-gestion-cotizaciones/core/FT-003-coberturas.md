## FT-003: Configuración y Selección de Coberturas por Ubicación

### HU-011: Visualizar catálogo de coberturas disponibles por tipo de seguro

Como usuario, quiero ver un catálogo de coberturas disponibles para el tipo de seguro seleccionado, para elegir las protecciones adecuadas.

**Criterios de Aceptación**:
- Dado que he seleccionado un tipo de seguro para la cotización, cuando accedo a la sección de coberturas de una ubicación, entonces se muestra la lista de coberturas aplicables a ese tipo de seguro.
- Dado que cambio el tipo de seguro de la cotización, cuando vuelvo a la sección de coberturas, entonces la lista de coberturas se actualiza según el nuevo tipo de seguro.
- Dado que no hay coberturas disponibles para un tipo de seguro, cuando accedo a la sección, entonces se muestra un mensaje informativo.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-004: Seleccionar tipo de seguro, moneda y canal de venta de catálogos
- FT-007: Integración con Servicios de Referencia (Catálogos de Coberturas)

**Componentes Técnicos**:
- Frontend: Interfaz de listado de coberturas.
- Backend: API de cotizaciones (consulta de coberturas por tipo de seguro), Integración con `Plataforma-core-ohs` (catálogo de coberturas).

**Notas de Implementación**:
- La carga de coberturas debe ser dinámica y filtrarse según el tipo de seguro de la cotización.
- Se debe permitir la paginación o búsqueda si el catálogo es muy extenso.

**Estado**: Backlog

---
### HU-012: Seleccionar y deseleccionar coberturas para una ubicación

Como usuario, quiero poder seleccionar o deseleccionar coberturas individuales para cada ubicación de riesgo, para personalizar la protección.

**Criterios de Aceptación**:
- Dado que estoy en la sección de coberturas de una ubicación, cuando selecciono una cobertura del catálogo, entonces esta se marca como activa para esa ubicación.
- Dado que una cobertura está seleccionada, cuando la deselecciono, entonces deja de estar activa para esa ubicación.
- Dado que guardo la cotización, cuando la consulto de nuevo, entonces las selecciones de coberturas por ubicación persisten.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-006: Agregar una nueva ubicación de riesgo a la cotización
- HU-011: Visualizar catálogo de coberturas disponibles por tipo de seguro

**Componentes Técnicos**:
- Frontend: Checkboxes/switches para selección de coberturas.
- Backend: API de cotizaciones (persistencia de coberturas seleccionadas por ubicación).

**Notas de Implementación**:
- La interfaz debe indicar claramente qué coberturas están seleccionadas.
- Considerar la posibilidad de coberturas obligatorias que no puedan deseleccionarse.

**Estado**: Backlog

---
### HU-013: Configurar parámetros específicos de cobertura (Sumas Aseguradas, Deducibles)

Como usuario, quiero configurar sumas aseguradas y deducibles específicos para cada cobertura y ubicación, para ajustar la protección a las necesidades del cliente.

**Criterios de Aceptación**:
- Dado que he seleccionado una cobertura que permite configuración de parámetros, cuando accedo a su detalle, entonces puedo introducir la suma asegurada y el deducible.
- Dado que introduzco un valor inválido para una suma asegurada o deducible, cuando intento guardar, entonces el sistema muestra un mensaje de validación.
- Dado que los parámetros de cobertura se han guardado, cuando consulto la cotización, entonces los valores configurados se muestran correctamente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia
**Dependencias**:
- HU-012: Seleccionar y deseleccionar coberturas para una ubicación
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Campos de entrada numérica para sumas aseguradas y deducibles.
- Backend: API de cotizaciones (validación y persistencia de parámetros de cobertura).

**Notas de Implementación**:
- Las validaciones deben considerar rangos mínimos y máximos o pasos para los valores.
- La interfaz debe ser intuitiva para la configuración de múltiples parámetros por cobertura.

**Estado**: Backlog

---
### HU-014: Visualizar resumen de coberturas activas por ubicación

Como usuario, quiero ver un resumen claro de las coberturas activas y sus parámetros para cada ubicación, para una revisión rápida.

**Criterios de Aceptación**:
- Dado que he configurado coberturas para una ubicación, cuando accedo a la vista de resumen de la ubicación, entonces se lista cada cobertura activa con su suma asegurada y deducible.
- Dado que una ubicación no tiene coberturas seleccionadas, cuando accedo a su resumen, entonces se muestra un mensaje indicando que no hay coberturas.
- Dado que se han realizado cambios en las coberturas, cuando accedo al resumen, entonces la información se actualiza automáticamente.
**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-012: Seleccionar y deseleccionar coberturas para una ubicación
- HU-013: Configurar parámetros específicos de cobertura (Sumas Aseguradas, Deducibles)

**Componentes Técnicos**:
- Frontend: Componente de resumen de coberturas por ubicación.

**Notas de Implementación**:
- El resumen debe ser conciso y fácil de leer.
- Se puede considerar la opción de exportar este resumen.

**Estado**: Backlog

---
