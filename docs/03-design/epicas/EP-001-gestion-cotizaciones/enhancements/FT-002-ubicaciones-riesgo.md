## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

### HU-113: Agregar Nueva Ubicación de Riesgo
**Descripción**:
Como usuario,
Quiero añadir una nueva ubicación de riesgo a mi cotización,
Para especificar múltiples lugares de interés para el seguro.

**Criterios de Aceptación**:
- Dado que tengo una cotización abierta, cuando hago clic en "Agregar Ubicación", entonces se presenta un nuevo formulario para la captura de datos de la ubicación.
- Dado que he alcanzado el límite configurable de ubicaciones, cuando intento agregar una nueva, entonces el sistema me notifica que no puedo añadir más.
- Dado que agrego una ubicación, cuando guardo la cotización, entonces la nueva ubicación se persiste como parte de la cotización.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-111 (Editar Datos Generales), HU-149 (Validación de Datos por Ubicación)

**Componentes Técnicos**: Frontend (Botón "Agregar Ubicación", Formulario de Ubicación), Backend (API de Cotizaciones para agregar ubicaciones).

**Notas de Implementación**: El límite de ubicaciones debe ser configurable a nivel de sistema.

**Estado**: Backlog

---
### HU-114: Editar Detalles de Ubicación de Riesgo
**Descripción**:
Como usuario,
Quiero modificar los datos específicos de una ubicación de riesgo existente (dirección, uso, características del inmueble),
Para asegurar la precisión de la evaluación del riesgo.

**Criterios de Aceptación**:
- Dado que tengo una cotización con ubicaciones, cuando selecciono una ubicación para editar, entonces sus datos se cargan en el formulario.
- Dado que modifico los datos de una ubicación y guardo, entonces los cambios se persisten correctamente para esa ubicación.
- Dado que intento guardar datos inválidos (e.g., código postal incorrecto), cuando confirmo la edición, entonces el sistema me muestra una alerta de validación.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-113 (Agregar Nueva Ubicación), HU-149 (Validación de Datos por Ubicación)

**Componentes Técnicos**: Frontend (Formulario de Edición de Ubicación), Backend (API de Cotizaciones para edición de ubicaciones).

**Notas de Implementación**: Se deben definir claramente los campos específicos para cada ubicación en el diseño de UI.

**Estado**: Backlog

---
### HU-115: Eliminar Ubicación de Riesgo
**Descripción**:
Como usuario,
Quiero eliminar una ubicación de riesgo de mi cotización,
Para corregir errores o ajustar la cobertura de la póliza.

**Criterios de Aceptación**:
- Dado que tengo una cotización con ubicaciones, cuando selecciono una ubicación y confirmo su eliminación, entonces la ubicación es removida de la cotización.
- Dado que elimino la última ubicación, cuando guardo, entonces la cotización sigue siendo válida sin ubicaciones o con un mensaje informativo.
- Dado que elimino una ubicación, cuando guardo, entonces el número de versión de la cotización se incrementa.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-114 (Editar Detalles de Ubicación)

**Componentes Técnicos**: Frontend (Botón "Eliminar Ubicación"), Backend (API de Cotizaciones para eliminación de ubicaciones).

**Notas de Implementación**: Se debe solicitar confirmación al usuario antes de eliminar una ubicación.

**Estado**: Backlog

---
### HU-116: Visualizar Múltiples Ubicaciones de Riesgo
**Descripción**:
Como usuario,
Quiero ver un resumen de todas las ubicaciones de riesgo en mi cotización,
Para tener una visión general de los riesgos asegurados y navegar entre ellas fácilmente.

**Criterios de Aceptación**:
- Dado que una cotización tiene múltiples ubicaciones, cuando la abro, entonces la interfaz muestra una lista o pestañas con cada ubicación.
- Dado que hago clic en una ubicación en la lista o pestaña, cuando navego, entonces se muestran los detalles completos de esa ubicación para edición.
- Dado que hay un límite de ubicaciones, cuando se alcanza, entonces la interfaz lo indica claramente.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-113 (Agregar Nueva Ubicación)

**Componentes Técnicos**: Frontend (Interfaz de Maestro-Detalle/Pestañas para Ubicaciones).

**Notas de Implementación**: Implementar un patrón de interfaz híbrido basado en maestro-detalle con navegación tipo pestañas.

**Estado**: Backlog

---
### HU-117: Validar Código Postal de Ubicación
**Descripción**:
Como usuario,
Quiero que el sistema valide el código postal de cada ubicación de riesgo,
Para asegurar que la dirección es válida y obtener la zonificación de riesgo correcta.

**Criterios de Aceptación**:
- Dado que ingreso un código postal en una ubicación, cuando el campo pierde el foco o se guarda, entonces el sistema valida el CP contra el `catalogo_cp_zonas`.
- Dado que ingreso un código postal válido, cuando se valida, entonces se muestran los datos de zona (CAT, nivel técnico) asociados a ese CP.
- Dado que ingreso un código postal inválido o no encontrado, cuando se valida, entonces el sistema muestra un mensaje de error claro y no permite guardar la ubicación con un CP erróneo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-152 (Integración de Catálogo de Códigos Postales y Zonas), HU-149 (Validación de Datos por Ubicación)

**Componentes Técnicos**: Frontend (Campo de Código Postal), Backend (Servicio de Validación de CP).

**Notas de Implementación**: La validación puede ser asíncrona y en tiempo real.

**Estado**: Backlog

---
### HU-118: Recibir Alertas por Datos Incompletos de Ubicación
**Descripción**:
Como usuario,
Quiero recibir alertas visuales si una ubicación de riesgo tiene datos incompletos o inválidos,
Para saber qué información necesito completar antes de calcular la prima.

**Criterios de Aceptación**:
- Dado que una ubicación tiene campos obligatorios vacíos, cuando la visualizo, entonces se muestra una alerta visual (e.g., icono, color) indicando datos incompletos.
- Dado que una ubicación tiene datos inválidos (e.g., CP incorrecto), cuando la visualizo, entonces se muestra una alerta indicando la inconsistencia.
- Dado que completo o corrijo los datos de una ubicación, cuando guardo, entonces la alerta visual desaparece.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-114 (Editar Detalles de Ubicación), HU-149 (Validación de Datos por Ubicación)

**Componentes Técnicos**: Frontend (Elementos de Alerta Visual en la Interfaz de Ubicaciones).

**Notas de Implementación**: Las alertas deben ser claras y no obstructivas, guiando al usuario a la acción.

**Estado**: Backlog

---
