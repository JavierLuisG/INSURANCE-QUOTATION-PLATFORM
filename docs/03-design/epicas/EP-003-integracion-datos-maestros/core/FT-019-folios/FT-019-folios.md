## FT-019: Generación y Gestión de Folios Alfanuméricos

### HU-086: Generar Folio Alfanumérico Único

**Descripción**:
Como sistema,
Quiero generar un folio alfanumérico único siguiendo el patrón 'PREFIJO-AAAA-NNNNNN' (ej. 'COT-202X-000001'),
Para identificar cada cotización de forma inequívoca y robusta.

**Criterios de Aceptación**:
- Dado que se solicita un nuevo folio, cuando el sistema lo genera, entonces el folio cumple con el patrón 'PREFIJO-AAAA-NNNNNN'.
- Dado que se han generado folios previamente, cuando se solicita uno nuevo, entonces el componente numérico se incrementa correctamente.
- Dado que se genera un folio, cuando se persiste la cotización, entonces el folio se asocia y guarda con ella.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- Ninguna

**Componentes Técnicos**:
- Servicio de generación de folios
- Módulo de secuencia numérica

**Notas de Implementación**:
El prefijo debe ser configurable. El año (AAAA) debe ser el actual.

**Estado**: Backlog

---
### HU-087: Persistir Secuencia de Folios de Forma Segura

**Descripción**:
Como sistema,
Quiero persistir la última secuencia numérica utilizada para la generación de folios de forma segura y consistente,
Para asegurar la unicidad y continuidad de los folios, incluso después de reinicios del sistema.

**Criterios de Aceptación**:
- Dado que se genera un nuevo folio, cuando el sistema actualiza la secuencia, entonces la nueva secuencia se guarda en una base de datos o almacenamiento persistente.
- Dado que el sistema se reinicia, cuando solicita un nuevo folio, entonces recupera la última secuencia persistida y continúa desde ahí.
- Dado que la persistencia de la secuencia falla, cuando el sistema lo detecta, entonces se registra un error y se notifica para posible intervención manual.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-086

**Componentes Técnicos**:
- Módulo de persistencia de secuencia
- Base de datos (MongoDB u otra)

**Notas de Implementación**:
La operación de actualización de la secuencia debe ser atómica y transaccional para evitar duplicados en entornos concurrentes.
**Estado**: Backlog

---
### HU-088: Implementar Reintentos en Generación de Folio

**Descripción**:
Como sistema,
Quiero tener un mecanismo de reintento automático configurable en caso de fallo en la generación del folio,
Para mejorar la resiliencia del proceso y reducir la necesidad de intervención manual.

**Criterios de Aceptación**:
- Dado que la generación del folio falla inicialmente (ej. conflicto de concurrencia), cuando el sistema lo detecta, entonces realiza un reintento automático después de un breve periodo.
- Dado que se configura un máximo de 3 reintentos, cuando la generación del folio falla por tercera vez, entonces el sistema detiene los reintentos.
- Dado que un reintento es exitoso, cuando el sistema lo logra, entonces el folio se genera y el proceso continúa normalmente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-086, HU-087

**Componentes Técnicos**:
- Estrategia de reintentos (ej. Exponential Backoff)
- Servicio de generación de folios

**Notas de Implementación**:
Los reintentos deben considerar el tipo de error; no todos los errores justifican un reintento.

**Estado**: Backlog

---
### HU-089: Notificar Fallo Persistente de Generación de Folio

**Descripción**:
Como sistema,
Quiero notificar al usuario o al sistema si la generación del folio falla persistentemente después de los reintentos,
Para permitir una intervención manual y evitar que el proceso de cotización se bloquee.

**Criterios de Aceptación**:
- Dado que la generación del folio falla después de todos los reintentos, cuando el sistema lo detecta, entonces muestra un mensaje de error claro al usuario.
- Dado que la generación del folio falla persistentemente, cuando el sistema lo detecta, entonces envía una alerta a un canal de monitoreo o un correo a los administradores.
- Dado que se produce un fallo persistente, cuando el usuario es notificado, entonces se le ofrece una opción para reintentar manualmente o contactar soporte.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-088

**Componentes Técnicos**:
- Servicio de notificación de errores
- Interfaz de usuario (frontend)
- Módulo de logging

**Notas de Implementación**:
El mensaje al usuario debe ser informativo y ofrecer un camino a seguir.

**Estado**: Backlog

---
### HU-090: Asegurar Idempotencia en Generación de Folios

**Descripción**:
Como sistema,
Quiero que la generación de folios sea idempotente,
Para evitar la creación de folios duplicados para la misma solicitud de cotización.

**Criterios de Aceptación**:
- Dado que se intenta generar un folio para una cotización que ya tiene uno asignado, cuando el sistema lo detecta, entonces devuelve el folio existente sin generar uno nuevo.
- Dado que una solicitud de generación de folio se procesa múltiples veces debido a reintentos de red, cuando el sistema lo maneja, entonces solo se genera un único folio.
- Dado que se utiliza un identificador de solicitud único (ej. ID de transacción), cuando se solicita un folio, entonces este ID se usa para verificar si ya se generó un folio.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-086

**Componentes Técnicos**:
- Servicio de generación de folios
- Mecanismo de detección de solicitudes duplicadas

**Notas de Implementación**:
Puede requerir un identificador de solicitud/transacción en la entrada de la API.

**Estado**: Backlog

---
### HU-091: Manejar Concurrencia en Generación de Folios

**Descripción**:
Como sistema,
Quiero manejar la concurrencia en la generación de folios,
Para evitar conflictos y asegurar la unicidad de los folios incluso bajo alta carga de solicitudes.

**Criterios de Aceptación**:
- Dado que múltiples usuarios solicitan folios simultáneamente, cuando el sistema los procesa, entonces cada usuario recibe un folio único.
- Dado que se produce un intento de generar el mismo folio por concurrencia, cuando el sistema lo detecta, entonces se resuelve el conflicto y se asegura la unicidad (ej. mediante bloqueo optimista o distribuidos).
- Dado que el sistema está bajo carga, cuando se generan folios, entonces el rendimiento es aceptable y no hay cuellos de botella por la generación de folios.

**Prioridad**: Alta

**Estimación**: 5 puntos de historia

**Dependencias**:
- HU-086, HU-087

**Componentes Técnicos**:
- Mecanismos de concurrencia (bloqueo, transacciones)
- Almacenamiento persistente de la secuencia

**Notas de Implementación**:
Considerar el uso de bases de datos que soporten operaciones atómicas o un servicio de IDs distribuidos.

**Estado**: Backlog

---
