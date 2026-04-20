## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

### HU-024: Iniciar cotización en estado "Borrador"

Como usuario, quiero que cada nueva cotización inicie automáticamente en estado "Borrador", para indicar que está en proceso de creación.

**Criterios de Aceptación**:
- Dado que se ha creado una nueva cotización (HU-001), cuando se guarda por primera vez, entonces su estado se establece como "Borrador".
- Dado que consulto una cotización recién creada, cuando veo su información, entonces el estado visible es "Borrador".
- Dado que el estado es "Borrador", cuando se realizan modificaciones, entonces el estado permanece en "Borrador" hasta que se cumplen otras condiciones.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- HU-001: Iniciar nueva cotización con folio automático

**Componentes Técnicos**:
- Backend: Lógica de inicialización de estado en la creación de cotización.

**Notas de Implementación**:
- El estado "Borrador" debe ser el valor por defecto para nuevas cotizaciones.
- Asegurar que este estado se persista correctamente en la base de datos.

**Estado**: Backlog

---
### HU-025: Actualizar estado a "Calculada" tras cálculo exitoso

Como usuario, quiero que el estado de la cotización se actualice a "Calculada" automáticamente después de un cálculo exitoso, para reflejar su progreso.

**Criterios de Aceptación**:
- Dado que se ha realizado un cálculo de prima exitoso (HU-015), cuando el proceso finaliza, entonces el estado de la cotización cambia a "Calculada".
- Dado que el cálculo falla, cuando el proceso termina, entonces el estado de la cotización no cambia a "Calculada" y se mantiene el estado anterior.
- Dado que el estado es "Calculada", cuando consulto la cotización, entonces este estado es visible.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Componentes Técnicos**:
- Backend: Lógica de actualización de estado post-cálculo.

**Notas de Implementación**:
- La actualización de estado debe ser parte de la transacción de guardado de resultados de cálculo.
- Considerar si se permite recalcular una cotización "Calculada".

**Estado**: Backlog

---
### HU-026: Cambiar estado a "Aprobada" o "Rechazada" manualmente

Como usuario, quiero poder cambiar manualmente el estado de una cotización a "Aprobada" o "Rechazada", para indicar la decisión del cliente.

**Criterios de Aceptación**:
- Dado que el estado de la cotización es "Calculada", cuando selecciono la opción "Aprobar" o "Rechazar", entonces el estado se actualiza en consecuencia.
- Dado que el estado de la cotización no es "Calculada", cuando intento cambiar a "Aprobada" o "Rechazada", entonces el sistema me impide la acción o me advierte.
- Dado que el estado se ha actualizado, cuando consulto la cotización, entonces el nuevo estado es visible.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-025: Actualizar estado a "Calculada" tras cálculo exitoso
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Botones/menús para cambiar estado.
- Backend: API de cotizaciones (endpoint de actualización de estado), Lógica de transiciones de estado.

**Notas de Implementación**:
- Las transiciones de estado deben seguir una máquina de estados definida.
- Se puede requerir un campo de comentarios para justificar el cambio de estado.

**Estado**: Backlog

---
### HU-027: Establecer estado "Emitida" para cotizaciones aprobadas

Como usuario, quiero poder establecer el estado de una cotización a "Emitida" una vez que ha sido aprobada, para finalizar el ciclo de vida.

**Criterios de Aceptación**:
- Dado que el estado de la cotización es "Aprobada", cuando selecciono la opción "Emitir", entonces el estado se actualiza a "Emitida".
- Dado que el estado de la cotización no es "Aprobada", cuando intento cambiar a "Emitida", entonces el sistema me impide la acción.
- Dado que el estado es "Emitida", cuando consulto la cotización, entonces el nuevo estado es visible y la cotización se considera finalizada.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-026: Cambiar estado a "Aprobada" o "Rechazada" manualmente
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Botón/opción "Emitir".
- Backend: API de cotizaciones (endpoint de actualización de estado), Lógica de transiciones de estado.

**Notas de Implementación**:
- El estado "Emitida" generalmente bloquea futuras ediciones de la cotización.
- Considerar la integración con un sistema de emisión de pólizas.

**Estado**: Backlog

---
### HU-028: Visualizar el estado actual de la cotización

Como usuario, quiero ver claramente el estado actual de la cotización en la interfaz, para conocer su progreso en todo momento.

**Criterios de Aceptación**:
- Dado que he cargado una cotización, cuando visualizo sus datos, entonces el estado actual (e.g., Borrador, Calculada, Aprobada) se muestra de forma prominente.
- Dado que el estado de la cotización cambia, cuando la interfaz se actualiza, entonces el estado mostrado también se actualiza.
- Dado que la cotización tiene un estado específico, cuando se muestra, entonces se utiliza una representación visual consistente (e.g., color, etiqueta).

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- Todas las HUs de FT-006

**Componentes Técnicos**:
- Frontend: Componente de visualización de estado.

**Notas de Implementación**:
- La visibilidad del estado debe ser constante en las pantallas de edición y consulta.
- Usar un patrón de diseño para los estados (e.g., badges, etiquetas).

**Estado**: Backlog

---
