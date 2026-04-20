## FT-019: Generación y Gestión de Folios Alfanuméricos

### HU-191: Generar Folios Únicos con Patrón Específico
**Descripción**:
Como sistema,
Quiero generar folios alfanuméricos únicos siguiendo el patrón especificado ('PREFIJO-AAAA-NNNNNN'),
Para asignar un identificador consistente a cada nueva cotización.

**Criterios de Aceptación**:
- Dado que se solicita un nuevo folio, cuando se genera, entonces cumple con el formato 'PREFIJO-AAAA-NNNNNN' (ej. 'COT-202X-000001').
- Dado que se genera un folio, cuando se asigna, entonces es único dentro del sistema.
- Dado que el prefijo es configurable, cuando se usa, entonces se aplica el valor configurado (por defecto "COT").

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: Ninguna

**Componentes Técnicos**: Backend (Servicio de Generación de Folios).

**Notas de Implementación**: La secuencia numérica debe ser incremental y persistente.

**Estado**: Backlog

---
### HU-192: Implementar Mecanismo de Reintento para Generación de Folios
**Descripción**:
Como sistema,
Quiero implementar un mecanismo de reintento automático configurable en caso de fallo en la generación del folio,
Para aumentar la resiliencia del proceso de creación de cotizaciones.

**Criterios de Aceptación**:
- Dado que la generación de un folio falla inicialmente (ej. por conflicto de concurrencia), cuando se detecta el error, entonces el sistema reintenta la generación automáticamente.
- Dado que los reintentos se configuran, cuando se agotan, entonces el sistema deja de intentar y notifica el fallo persistente.
- Dado que un reintento es exitoso, cuando se completa, entonces el folio se genera y se asigna sin problemas.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-191 (Generar Folios Únicos con Patrón Específico)

**Componentes Técnicos**: Backend (Servicio de Generación de Folios, Lógica de Reintentos).

**Notas de Implementación**: El número de reintentos y el tiempo de espera deben ser configurables.

**Estado**: Backlog

---
### HU-193: Notificar Fallo Persistente de Generación de Folio
**Descripción**:
Como sistema,
Quiero que si la generación de folios falla persistentemente después de los reintentos, se notifique al usuario o al sistema para intervención manual,
Para asegurar que no se pierdan solicitudes de cotización debido a un fallo en la asignación de folio.

**Criterios de Aceptación**:
- Dado que la generación de folio falla después de agotar todos los reintentos, cuando se produce, entonces se genera una notificación (ej. log, alerta, correo electrónico).
- Dado que la notificación se genera, cuando se recibe, entonces contiene información suficiente para diagnosticar el problema y tomar acción manual.
- Dado que se notifica un fallo persistente, cuando se produce, entonces el usuario puede optar por reintentar manualmente o cancelar la operación.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-192 (Implementar Mecanismo de Reintento para Generación de Folios)

**Componentes Técnicos**: Backend (Servicio de Notificación de Errores).

**Notas de Implementación**: La notificación debe ser clara sobre la necesidad de intervención.

**Estado**: Backlog

---
### HU-194: Asegurar Generación de Folios Idempotente
**Descripción**:
Como sistema,
Quiero que la generación de folios sea idempotente,
Para evitar la creación de folios duplicados para la misma solicitud de cotización.

**Criterios de Aceptación**:
- Dado que se realiza una solicitud de generación de folio, cuando se procesa, entonces se genera un único folio para esa solicitud, incluso si la solicitud se envía varias veces.
- Dado que un folio se genera, cuando se consulta, entonces no existe otro folio con el mismo valor.
- Dado que la generación es idempotente, cuando se completa, entonces no hay duplicados de folios en el sistema.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-191 (Generar Folios Únicos con Patrón Específico)

**Componentes Técnicos**: Backend (Servicio de Generación de Folios, Mecanismo de Bloqueo o Clave Única).

**Notas de Implementación**: Utilizar un ID de solicitud único o un bloqueo distribuido para garantizar la idempotencia.

**Estado**: Backlog

---
