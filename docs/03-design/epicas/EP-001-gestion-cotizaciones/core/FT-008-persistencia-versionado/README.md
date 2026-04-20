## FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

### HU-035: Incrementar versión de cotización en cada edición

Como desarrollador, quiero que cada vez que se edita una cotización, se incremente un campo de versión, para mantener un control de cambios.

**Criterios de Aceptación**:
- Dado que una cotización se guarda después de una edición, cuando se persiste, entonces el campo `version` se incrementa en uno.
- Dado que la cotización se crea por primera vez, cuando se guarda, entonces el campo `version` se inicializa en 1.
- Dado que se intenta guardar una versión antigua de la cotización, cuando el sistema detecta una versión más reciente, entonces previene la sobrescritura.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Componentes Técnicos**:
- Backend: Capa de persistencia, Lógica de control de concurrencia.
- Base de Datos: Campo `version` en el esquema de cotizaciones.

**Notas de Implementación**:
- El campo de versión puede ser un número entero o un timestamp.
- La lógica de incremento debe ser atómica con la operación de guardado.

**Estado**: Backlog

---
### HU-036: Actualizar fecha de última modificación en cada edición

Como desarrollador, quiero que en cada edición de una cotización se actualice el campo `fechaUltimaActualizacion`, para conocer cuándo fue el último cambio.

**Criterios de Aceptación**:
- Dado que una cotización se guarda después de una edición, cuando se persiste, entonces el campo `fechaUltimaActualizacion` se actualiza con la fecha y hora actuales.
- Dado que la cotización se crea por primera vez, cuando se guarda, entonces el campo `fechaUltimaActualizacion` se establece.
- Dado que consulto una cotización, cuando veo sus metadatos, entonces la `fechaUltimaActualizacion` refleja el último guardado.

**Prioridad**: Media

**Estimación**: 1 punto de historia

**Dependencias**:
- Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Componentes Técnicos**:
- Backend: Capa de persistencia.
- Base de Datos: Campo `fechaUltimaActualizacion` en el esquema de cotizaciones.

**Notas de Implementación**:
- El formato de la fecha debe ser consistente (e.g., ISO 8601 con zona horaria).
- Esta actualización debe ser automática y no requerir intervención del usuario.

**Estado**: Backlog

---
### HU-037: Implementar versionado optimista para prevenir conflictos

Como desarrollador, quiero implementar un mecanismo de versionado optimista, para prevenir la pérdida de datos por ediciones concurrentes de la misma cotización.

**Criterios de Aceptación**:
- Dado que dos usuarios intentan editar y guardar la misma cotización simultáneamente, cuando el segundo usuario intenta guardar una versión desactualizada, entonces el sistema rechaza la operación.
- Dado que se rechaza una operación por versionado optimista, cuando el sistema lo detecta, entonces notifica al usuario que la cotización ha sido modificada por otro usuario.
- Dado que se previene un conflicto, cuando el usuario afectado decide recargar, entonces puede ver la versión más reciente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-035: Incrementar versión de cotización en cada edición
- Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Componentes Técnicos**:
- Backend: Lógica de control de concurrencia en la capa de persistencia.
- Frontend: Manejo de errores de concurrencia, Mensajes al usuario.

**Notas de Implementación**:
- La estrategia de versionado puede basarse en un número de versión o ETag.
- Se debe diseñar una estrategia para que el usuario pueda resolver el conflicto (e.g., recargar y volver a aplicar cambios).

**Estado**: Backlog

---
### HU-038: Permitir actualización parcial de campos de cotización

Como desarrollador, quiero que el sistema permita la actualización parcial de los campos de la cotización, para optimizar las operaciones de guardado.

**Criterios de Aceptación**:
- Dado que solo se modifica un subconjunto de los campos de la cotización, cuando se guarda, entonces solo los campos modificados se actualizan en la base de datos.
- Dado que se actualiza parcialmente una cotización, cuando se persiste, entonces el resto de los datos no modificados permanecen intactos.
- Dado que se realiza una actualización parcial, cuando se guarda, entonces el campo `version` y `fechaUltimaActualizacion` se actualizan.
**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Componentes Técnicos**:
- Backend: Capa de persistencia (ej. uso de `PATCH` en API REST o lógica de `update` en ORM/ODM).

**Notas de Implementación**:
- Esto requiere un diseño cuidadoso de la API y la capa de datos para manejar objetos parciales.
- Asegurar que las validaciones se apliquen solo a los campos presentes en la actualización parcial.

**Estado**: Backlog

---
