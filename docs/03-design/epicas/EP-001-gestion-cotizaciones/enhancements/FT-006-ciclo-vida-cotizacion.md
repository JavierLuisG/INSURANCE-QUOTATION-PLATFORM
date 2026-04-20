## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

### HU-132: Cotización Inicia en Estado "Borrador"
**Descripción**:
Como usuario,
Quiero que cada nueva cotización comience automáticamente en el estado "Borrador",
Para indicar que aún está en proceso de creación y edición.

**Criterios de Aceptación**:
- Dado que creo una nueva cotización, cuando se guarda por primera vez, entonces su estado se establece como "Borrador".
- Dado que consulto una cotización recién creada, cuando visualizo su estado, entonces se muestra "Borrador".
- Dado que una cotización está en "Borrador", cuando se realizan modificaciones, entonces permanece en "Borrador" hasta que se inicie un cálculo.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**: HU-109 (Crear Nueva Cotización)

**Componentes Técnicos**: Backend (Lógica de Inicialización de Cotización).

**Notas de Implementación**: El estado "Borrador" es el estado inicial por defecto.

**Estado**: Backlog

---
### HU-133: Actualización Automática a Estado "Calculada"
**Descripción**:
Como usuario,
Quiero que el estado de la cotización se actualice automáticamente a "Calculada" tras una ejecución exitosa del cálculo,
Para reflejar que la información financiera está disponible y es válida.

**Criterios de Aceptación**:
- Dado que una cotización en estado "Borrador" se calcula exitosamente, cuando finaliza el proceso, entonces su estado cambia a "Calculada".
- Dado que el cálculo falla, cuando finaliza el proceso, entonces el estado de la cotización no cambia a "Calculada" y permanece en el estado anterior (e.g., "Borrador" o "Pendiente de Cálculo").
- Dado que el estado es "Calculada", cuando se visualiza, entonces indica que los resultados de prima están disponibles.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-126 (Persistir Resultados del Cálculo de Prima), HU-135 (No se puede calcular sin validaciones previas)

**Componentes Técnicos**: Backend (Lógica de Transición de Estados).

**Notas de Implementación**: La transición solo ocurre si todas las validaciones previas son exitosas.

**Estado**: Backlog

---
### HU-134: Cambiar Manualmente Estado a "Aprobada" o "Rechazada"
**Descripción**:
Como usuario,
Quiero poder cambiar manualmente el estado de una cotización a "Aprobada" o "Rechazada" desde "Calculada",
Para indicar el resultado de la negociación con el cliente.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Calculada", cuando selecciono "Aprobar", entonces su estado cambia a "Aprobada".
- Dado que una cotización está en estado "Calculada", cuando selecciono "Rechazar", entonces su estado cambia a "Rechazada".
- Dado que la cotización no está en estado "Calculada", cuando intento aprobar o rechazar, entonces el sistema me lo impide.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-133 (Actualización Automática a Estado "Calculada"), HU-136 (No se puede aprobar sin cálculo previo)

**Componentes Técnicos**: Frontend (Botones de Acción de Estado), Backend (API de Actualización de Estado).

**Notas de Implementación**: Estos cambios de estado son acciones manuales del usuario.

**Estado**: Backlog

---
### HU-135: No se puede calcular sin validaciones previas
**Descripción**:
Como usuario,
Quiero que el sistema me impida calcular una cotización si no cumple con las validaciones previas,
Para evitar cálculos erróneos y asegurar la calidad de los datos.

**Criterios de Aceptación**:
- Dado que una cotización tiene ubicaciones incompletas o inválidas, cuando intento calcular la prima, entonces el sistema me muestra los errores de validación y no procede.
- Dado que una cotización tiene coberturas no definidas o con parámetros erróneos, cuando intento calcular la prima, entonces el sistema me lo impide.
- Dado que todas las validaciones son exitosas, cuando intento calcular, entonces el cálculo procede sin impedimentos.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-147 (Prevenir Cálculo con Errores de Validación)

**Componentes Técnicos**: Backend (Motor de Validación de Reglas de Negocio), Frontend (Mensajes de Error).

**Notas de Implementación**: La validación debe ser exhaustiva antes de invocar el motor de cálculo.

**Estado**: Backlog

---
### HU-136: No se puede aprobar sin cálculo previo
**Descripción**:
Como usuario,
Quiero que el sistema me impida aprobar una cotización si no ha sido previamente calculada,
Para asegurar que solo se aprueban cotizaciones con información financiera validada.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Borrador" o "Pendiente de Cálculo", cuando intento cambiar su estado a "Aprobada", entonces el sistema me lo impide y muestra un mensaje de error.
- Dado que una cotización está en estado "Calculada", cuando intento cambiar su estado a "Aprobada", entonces la operación es exitosa.
- Dado que una cotización ha sido "Rechazada", cuando intento cambiar su estado a "Aprobada", entonces el sistema me lo impide.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-133 (Actualización Automática a Estado "Calculada"), HU-134 (Cambiar Manualmente Estado)

**Componentes Técnicos**: Backend (Lógica de Transición de Estados).

**Notas de Implementación**: Las reglas de transición de estado deben ser estrictas.

**Estado**: Backlog

---
### HU-137: Cualquier Modificación Invalida Cálculo
**Descripción**:
Como usuario,
Quiero que cualquier modificación en una cotización en estado "CALCULADA" o superior invalide el cálculo,
Para asegurar que los resultados financieros siempre correspondan a la información actual.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Calculada", cuando modifico datos generales o de una ubicación, entonces el estado de la cotización regresa a "Borrador" o "Pendiente de Cálculo".
- Dado que el estado regresa a "Borrador" o "Pendiente de Cálculo", cuando visualizo los resultados financieros, entonces se indica que están desactualizados o no disponibles.
- Dado que una cotización está en estado "Aprobada" o "Rechazada", cuando intento modificarla, entonces el sistema me lo permite pero regresa a un estado de borrador para recalculo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-111 (Editar Datos Generales), HU-114 (Editar Detalles de Ubicación)

**Componentes Técnicos**: Backend (Lógica de Transición de Estados y Validación).

**Notas de Implementación**: Esta regla es crucial para mantener la integridad de los datos financieros.

**Estado**: Backlog

---
### HU-138: Establecer Estado "Emitida"
**Descripción**:
Como usuario,
Quiero poder establecer el estado de una cotización como "Emitida" una vez que ha sido "Aprobada",
Para indicar que la póliza ha sido formalmente emitida y es un estado terminal.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Aprobada", cuando selecciono "Emitir", entonces su estado cambia a "Emitida".
- Dado que una cotización está en estado "Emitida", cuando intento modificarla, entonces el sistema me lo impide o me notifica que es un estado terminal.
- Dado que una cotización no está en estado "Aprobada", cuando intento cambiar su estado a "Emitida", entonces el sistema me lo impide.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-134 (Cambiar Manualmente Estado a "Aprobada" o "Rechazada")

**Componentes Técnicos**: Frontend (Botón "Emitir"), Backend (API de Actualización de Estado).

**Notas de Implementación**: "Emitida" debe ser un estado terminal sin transiciones de salida.

**Estado**: Backlog

---
### HU-139: Visualizar Estado Actual de la Cotización
**Descripción**:
Como usuario,
Quiero ver claramente el estado actual de la cotización,
Para tener un seguimiento visual del progreso del proceso de venta.

**Criterios de Aceptación**:
- Dado que tengo una cotización abierta, cuando la visualizo, entonces se muestra una etiqueta o indicador con su estado actual (e.g., Borrador, Calculada).
- Dado que el estado de la cotización cambia, cuando la consulto de nuevo, entonces la interfaz refleja el nuevo estado.
- Dado que el estado es importante, cuando se muestra, entonces es prominente y fácil de identificar.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**: Ninguna (es una funcionalidad de visualización)

**Componentes Técnicos**: Frontend (Elemento de Visualización de Estado).

**Notas de Implementación**: El estado puede ser representado con colores o iconos para mayor claridad.

**Estado**: Backlog

---
