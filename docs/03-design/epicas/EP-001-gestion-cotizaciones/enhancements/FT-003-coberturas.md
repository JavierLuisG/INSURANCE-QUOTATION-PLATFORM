## FT-003: Configuración y Selección de Coberturas por Ubicación

### HU-119: Visualizar Catálogo de Coberturas por Ubicación
**Descripción**:
Como usuario,
Quiero ver el catálogo de coberturas disponibles para cada ubicación de riesgo y tipo de seguro,
Para seleccionar las protecciones adecuadas para mis clientes.

**Criterios de Aceptación**:
- Dado que estoy en la sección de coberturas de una ubicación, cuando accedo, entonces se muestra una lista de coberturas relevantes para el tipo de seguro de la cotización.
- Dado que el catálogo de coberturas tiene descripciones, cuando lo consulto, entonces puedo ver la información detallada de cada cobertura.
- Dado que no hay coberturas disponibles para el tipo de seguro, cuando accedo, entonces se muestra un mensaje informativo.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-114 (Editar Detalles de Ubicación), HU-153 (Integración de Catálogos de Clasificación de Riesgo y Garantías)

**Componentes Técnicos**: Frontend (Interfaz de Selección de Coberturas), Backend (API de Consulta de Coberturas).

**Notas de Implementación**: El catálogo de coberturas debe ser configurable y estar asociado a tipos de seguro.

**Estado**: Backlog

---
### HU-120: Seleccionar y Deseleccionar Coberturas por Ubicación
**Descripción**:
Como usuario,
Quiero poder seleccionar o deseleccionar coberturas específicas para cada ubicación de riesgo,
Para personalizar la protección ofrecida según las necesidades del cliente.

**Criterios de Aceptación**:
- Dado que visualizo el catálogo de coberturas, cuando selecciono una o varias, entonces se marcan como activas para la ubicación.
- Dado que una cobertura está seleccionada, cuando la deselecciono, entonces deja de estar activa para la ubicación.
- Dado que guardo la cotización, cuando se persisten los cambios, entonces las coberturas seleccionadas se asocian correctamente a la ubicación.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-119 (Visualizar Catálogo de Coberturas), HU-114 (Editar Detalles de Ubicación)
**Componentes Técnicos**: Frontend (Controles de Selección de Coberturas), Backend (API de Cotizaciones para gestión de coberturas).

**Notas de Implementación**: La selección de coberturas debe ser intuitiva (e.g., checkboxes, toggles).

**Estado**: Backlog

---
### HU-121: Configurar Parámetros Específicos de Cobertura
**Descripción**:
Como usuario,
Quiero configurar parámetros específicos para cada cobertura seleccionada (e.g., sumas aseguradas, deducibles),
Para ajustar con precisión el alcance y las condiciones de la protección.

**Criterios de Aceptación**:
- Dado que selecciono una cobertura que requiere parámetros, cuando la activo, entonces se habilitan los campos para configurar sus valores (e.g., suma asegurada, deducible).
- Dado que ingreso valores en los parámetros de cobertura y guardo, entonces estos valores se persisten junto con la cobertura y la ubicación.
- Dado que los parámetros tienen rangos de validación, cuando ingreso un valor fuera de rango, entonces el sistema me lo notifica con un mensaje de error.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**: HU-120 (Seleccionar y Deseleccionar Coberturas), HU-148 (Validar Rangos de Sumas Aseguradas)

**Componentes Técnicos**: Frontend (Campos de Entrada de Parámetros), Backend (API de Cotizaciones para persistencia de parámetros).

**Notas de Implementación**: La interfaz debe mostrar claramente qué parámetros son configurables para cada cobertura.

**Estado**: Backlog

---
### HU-122: Visualizar Coberturas Activas por Ubicación
**Descripción**:
Como usuario,
Quiero ver claramente qué coberturas están activas para cada ubicación de riesgo,
Para tener un resumen rápido de la protección configurada.

**Criterios de Aceptación**:
- Dado que he seleccionado coberturas para una ubicación, cuando la visualizo, entonces se muestra una indicación clara de las coberturas activas.
- Dado que una cobertura tiene parámetros configurados, cuando la visualizo, entonces se muestran también sus valores (e.g., suma asegurada, deducible).
- Dado que no hay coberturas activas para una ubicación, cuando la visualizo, entonces se muestra un mensaje indicando la ausencia de coberturas.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-120 (Seleccionar y Deseleccionar Coberturas)

**Componentes Técnicos**: Frontend (Elementos de Visualización de Coberturas Activas).

**Notas de Implementación**: La presentación debe ser concisa y fácil de entender.

**Estado**: Backlog

---
