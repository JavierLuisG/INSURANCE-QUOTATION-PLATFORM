## FT-014: Gestión de Concurrencia y Versionado Optimista

### HU-174: Implementar Campo de Versión Incremental
**Descripción**:
Como sistema,
Quiero utilizar un campo de versión incremental para cada cotización,
Para detectar si una cotización ha sido modificada por otro usuario o proceso.

**Criterios de Aceptación**:
- Dado que se crea una nueva cotización, cuando se guarda, entonces el campo `version` se inicializa en 1.
- Dado que se actualiza una cotización, cuando se guarda, entonces el campo `version` se incrementa en 1.
- Dado que el campo de versión es numérico, cuando se utiliza, entonces permite comparaciones para detección de concurrencia.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-145 (Incrementar Versión en Ediciones de Cotización)

**Componentes Técnicos**: Backend (Modelo de Datos de Cotización, Capa de Persistencia).

**Notas de Implementación**: El campo de versión debe ser gestionado automáticamente por el backend.

**Estado**: Backlog

---
### HU-175: Comparar Versiones de Cotización en Guardado
**Descripción**:
Como sistema,
Quiero que al intentar guardar una cotización, se compare la versión de la cotización en memoria con la versión en la base de datos,
Para detectar posibles conflictos de concurrencia.

**Criterios de Aceptación**:
- Dado que un usuario intenta guardar una cotización, cuando se inicia la operación de guardado, entonces el sistema lee la versión actual de la cotización en la base de datos.
- Dado que la versión en memoria coincide con la de la base de datos, cuando se compara, entonces la operación de guardado procede.
- Dado que la versión en memoria es diferente a la de la base de datos, cuando se compara, entonces el sistema identifica un conflicto potencial.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-174 (Implementar Campo de Versión Incremental)

**Componentes Técnicos**: Backend (Lógica de Control de Concurrencia en Persistencia).

**Notas de Implementación**: Esta comparación debe realizarse como parte de la operación transaccional de guardado.

**Estado**: Backlog

---
### HU-176: Detectar Conflicto de Concurrencia
**Descripción**:
Como sistema,
Quiero que si las versiones no coinciden al intentar guardar una cotización, se detecte un conflicto de concurrencia,
Para activar el mecanismo de resolución de conflictos.

**Criterios de Aceptación**:
- Dado que la versión de la cotización en la base de datos es mayor que la versión que el usuario intentó guardar, cuando se realiza la comparación, entonces se detecta un conflicto de concurrencia.
- Dado que se detecta un conflicto, cuando se notifica, entonces el sistema lo marca para su manejo posterior.
- Dado que no hay conflicto de versión, cuando se compara, entonces el sistema permite continuar con la operación de guardado.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-175 (Comparar Versiones de Cotización en Guardado)

**Componentes Técnicos**: Backend (Lógica de Detección de Conflicto).

**Notas de Implementación**: La detección debe ser explícita y generar una excepción o un código de error específico.

**Estado**: Backlog

---
### HU-177: Notificar Usuario de Versión Más Reciente
**Descripción**:
Como sistema,
Quiero que en caso de conflicto de concurrencia, se notifique al usuario de la existencia de una versión más reciente,
Para informarle que sus cambios podrían sobrescribir los de otro.

**Criterios de Aceptación**:
- Dado que se detecta un conflicto de concurrencia, cuando se notifica al usuario, entonces se muestra un mensaje claro indicando que la cotización ha sido actualizada por otro usuario.
- Dado que el mensaje se muestra, cuando lo visualiza el usuario, entonces le informa sobre la necesidad de recargar la cotización.
- Dado que la notificación es crítica, cuando se muestra, entonces es prominente y requiere una acción del usuario.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-176 (Detectar Conflicto de Concurrencia)

**Componentes Técnicos**: Frontend (Sistema de Notificación al Usuario), Backend (Servicio de Mensajes de Error de Concurrencia).

**Notas de Implementación**: La notificación debe ser amigable y ofrecer opciones de acción.
**Estado**: Backlog

---
### HU-178: Permitir Recargar Última Versión de Cotización
**Descripción**:
Como usuario,
Quiero que el sistema me permita recargar la cotización con la última versión desde la base de datos en caso de conflicto,
Para poder ver los cambios de otros usuarios y reintentar mis propias modificaciones.

**Criterios de Aceptación**:
- Dado que recibo una notificación de conflicto, cuando hago clic en "Recargar", entonces la cotización se carga nuevamente desde la base de datos con la versión más reciente.
- Dado que la cotización se recarga, cuando se muestra, entonces refleja los cambios realizados por otros usuarios.
- Dado que recargo la cotización, cuando intento guardar mis cambios de nuevo, entonces puedo hacerlo si no hay nuevos conflictos.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-177 (Notificar Usuario de Versión Más Reciente)

**Componentes Técnicos**: Frontend (Botón "Recargar", Lógica de Recarga de Datos).

**Notas de Implementación**: La recarga debe ser una operación que actualice completamente la vista de la cotización.

**Estado**: Backlog

---
