## FT-014: Gestión de Concurrencia y Versionado Optimista

### HU-064: Control de Versión para Cotizaciones
**Descripción**:
Como desarrollador,
Quiero que cada cotización tenga un campo de versión que se incremente en cada actualización exitosa,
Para habilitar el control de concurrencia y detectar modificaciones simultáneas.

**Criterios de Aceptación**:
- Dado que una cotización es creada, cuando se guarda por primera vez, entonces su campo de versión se inicializa (e.g., en 1).
- Dado que una cotización es modificada y guardada exitosamente, cuando se persiste, entonces su campo de versión se incrementa en uno.
- Dado que se intenta guardar una cotización sin modificarla, cuando la operación se completa, entonces el campo de versión no se incrementa.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-062 (es el mecanismo que actualiza la versión)

**Componentes Técnicos**:
- Lógica de control de versión
- Modelo de datos de cotización (campo `version`)

**Notas de Implementación**:
El campo `version` debe ser un tipo numérico (entero) y no nulo.

**Estado**: Backlog

---
### HU-065: Detección de Conflictos de Concurrencia
**Descripción**:
Como usuario,
Quiero que el sistema detecte cuando otra persona ha modificado la cotización que estoy editando,
Para evitar sobrescribir sus cambios inadvertidamente.

**Criterios de Aceptación**:
- Dado que estoy editando una cotización con versión `X`, cuando otro usuario guarda una modificación que cambia la versión a `Y` (donde `Y > X`), entonces mi intento de guardar con versión `X` detecta un conflicto.
- Dado que mi versión en memoria coincide con la versión en la base de datos, cuando intento guardar, entonces la operación procede sin conflicto.
- Dado que se detecta un conflicto, cuando se intenta guardar, entonces la operación de guardado es rechazada por el backend.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-064 (requiere el campo de versión)

**Componentes Técnicos**:
- Lógica de control de versión (en el servicio de persistencia)
- Manejador de errores de concurrencia

**Notas de Implementación**:
El backend debe comparar la versión enviada por el cliente con la versión actual en la DB como parte de la validación de guardado.

**Estado**: Backlog

---
### HU-066: Notificación de Conflicto al Usuario
**Descripción**:
Como usuario,
Quiero ser notificado claramente si se detecta un conflicto de concurrencia al intentar guardar,
Para saber que necesito revisar la situación antes de continuar.

**Criterios de Aceptación**:
- Dado que se ha detectado un conflicto de concurrencia, cuando mi intento de guardar es rechazado, entonces recibo un mensaje de error específico que indica que la cotización ha sido modificada por otro usuario.
- Dado que el mensaje de conflicto se muestra, cuando lo leo, entonces entiendo que mis cambios no han sido guardados y que hay una versión más reciente.
- Dado que el conflicto ocurre, cuando el sistema lo notifica, entonces no se pierde mi trabajo actual, sino que se me da la opción de resolverlo.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-065 (requiere la detección del conflicto)

**Componentes Técnicos**:
- Sistema de notificación al usuario (backend para generar el mensaje)
- Módulo de manejo de errores en la API

**Notas de Implementación**:
El mensaje debe ser amigable y ofrecer una indicación de lo que el usuario puede hacer a continuación.

**Estado**: Backlog

---
### HU-067: Recarga de la Última Versión de la Cotización
**Descripción**:
Como usuario,
Quiero poder recargar la versión más reciente de la cotización desde la base de datos después de un conflicto,
Para trabajar con la información actualizada y reintentar mis cambios si es necesario.

**Criterios de Aceptación**:
- Dado que se me ha notificado un conflicto de concurrencia, cuando elijo "recargar", entonces el sistema obtiene la última versión de la cotización desde la base de datos.
- Dado que la cotización se ha recargado, cuando la visualizo, entonces veo los cambios realizados por el otro usuario.
- Dado que recargo la cotización, cuando mis cambios no guardados se pierden, entonces soy consciente de esta consecuencia (idealmente se me advierte antes de recargar).

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-066 (ocurre después de la notificación)

**Componentes Técnicos**:
- Servicio de consulta de cotizaciones
- Lógica de interfaz de usuario para recargar

**Notas de Implementación**:
Se debe considerar cómo el frontend gestiona los cambios no guardados del usuario antes de la recarga, quizás con una advertencia o una opción para fusionar (si fuera más complejo).

**Estado**: Backlog

---
