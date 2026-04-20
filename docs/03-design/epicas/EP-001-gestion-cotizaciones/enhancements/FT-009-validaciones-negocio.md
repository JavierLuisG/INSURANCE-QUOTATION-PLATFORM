## FT-009: Implementación de Reglas de Negocio y Validaciones

### HU-145: Incrementar Versión en Ediciones de Cotización
**Descripción**:
Como sistema,
Quiero que cada edición de una cotización incremente automáticamente un campo de versión,
Para facilitar el control de concurrencia y la trazabilidad de los cambios.

**Criterios de Aceptación**:
- Dado que se guarda una cotización modificada, cuando la operación es exitosa, entonces el campo `version` de la cotización se incrementa en uno.
- Dado que se crea una nueva cotización, cuando se guarda por primera vez, entonces su campo `version` se inicializa en un valor (e.g., 1).
- Dado que ocurre un error al guardar, cuando la operación falla, entonces el campo `version` no se incrementa.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: Todas las HUs que modifican la cotización (ej. HU-111, HU-114)

**Componentes Técnicos**: Backend (Capa de Persistencia, Modelo de Datos de Cotización).

**Notas de Implementación**: La gestión del campo de versión debe ser automática y transparente para el usuario.

**Estado**: Backlog

---
### HU-146: Actualizar Fecha de Última Actualización en Ediciones
**Descripción**:
Como sistema,
Quiero que cada edición de una cotización actualice el campo `fechaUltimaActualizacion`,
Para tener un registro de cuándo fue la última modificación de la cotización.

**Criterios de Aceptación**:
- Dado que se guarda una cotización modificada, cuando la operación es exitosa, entonces el campo `fechaUltimaActualizacion` se actualiza con la fecha y hora actual.
- Dado que se crea una nueva cotización, cuando se guarda por primera vez, entonces su campo `fechaUltimaActualizacion` se establece con la fecha y hora de creación.
- Dado que ocurre un error al guardar, cuando la operación falla, entonces el campo `fechaUltimaActualizacion` no se actualiza.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**: HU-145 (Incrementar Versión en Ediciones de Cotización)

**Componentes Técnicos**: Backend (Capa de Persistencia, Modelo de Datos de Cotización).

**Notas de Implementación**: La actualización de la fecha debe ser automática.

**Estado**: Backlog

---
### HU-147: Prevenir Sobrescritura con Versionado Optimista
**Descripción**:
Como sistema,
Quiero prevenir la sobrescritura de cambios si una versión más reciente ya fue guardada (versionado optimista),
Para evitar la pérdida de datos en ediciones concurrentes.

**Criterios de Aceptación**:
- Dado que un usuario intenta guardar una cotización con una versión desactualizada, cuando la operación se realiza, entonces el sistema detecta el conflicto y la rechaza.
- Dado que se detecta un conflicto de versión, cuando el sistema lo notifica, entonces se envía un mensaje de error al usuario indicando que la cotización ha sido modificada por otro usuario.
- Dado que la versión de la cotización en memoria coincide con la de la base de datos, cuando se guarda, entonces la operación es exitosa.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-145 (Incrementar Versión en Ediciones de Cotización), HU-164 (Sistema Compara Versiones en Guardado)

**Componentes Técnicos**: Backend (Lógica de Control de Concurrencia en Persistencia).

**Notas de Implementación**: La estrategia específica para el versionado optimista será un número de versión incremental gestionado por el backend.

**Estado**: Backlog

---
### HU-148: Permitir Actualización Parcial de Campos
**Descripción**:
Como sistema,
Quiero permitir la actualización parcial de campos de la cotización sin afectar otros datos,Para optimizar las operaciones de guardado y reducir la carga de datos.

**Criterios de Aceptación**:
- Dado que un usuario modifica solo un subconjunto de campos de la cotización, cuando guarda, entonces solo esos campos modificados se actualizan en la base de datos.
- Dado que se realiza una actualización parcial, cuando se completa, entonces los campos no modificados permanecen intactos.
- Dado que una actualización parcial es exitosa, cuando se guarda, entonces el campo `fechaUltimaActualizacion` y la `version` se actualizan.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-145 (Incrementar Versión en Ediciones de Cotización)

**Componentes Técnicos**: Backend (API de Actualización Parcial, Capa de Persistencia).

**Notas de Implementación**: Utilizar DTOs específicos para actualizaciones o mapeo inteligente de campos.

**Estado**: Backlog

---
### HU-149: Persistencia Transaccional de Cotización y Ubicaciones
**Descripción**:
Como sistema,
Quiero asegurar que la persistencia de la cotización y sus ubicaciones es transaccional y consistente,
Para garantizar la integridad de los datos en caso de errores.

**Criterios de Aceptación**:
- Dado que se guarda una cotización con sus ubicaciones, cuando la operación es exitosa, entonces todos los datos (cotización y todas sus ubicaciones) se persisten.
- Dado que ocurre un error durante la persistencia de una ubicación, cuando la operación falla, entonces todos los cambios (cotización y ubicaciones) se deshacen (rollback).
- Dado que la persistencia es transaccional, cuando se completa, entonces la base de datos refleja un estado consistente.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-113 (Agregar Nueva Ubicación), HU-115 (Eliminar Ubicación de Riesgo)

**Componentes Técnicos**: Backend (Capa de Persistencia, Transacciones en MongoDB).

**Notas de Implementación**: MongoDB no tiene transacciones ACID a nivel de múltiples documentos por defecto, por lo que se debe simular la atomicidad a nivel de agregado.

**Estado**: Backlog

---
