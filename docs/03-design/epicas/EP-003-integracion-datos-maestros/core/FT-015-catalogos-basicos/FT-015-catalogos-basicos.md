## FT-015: Conectividad y Consumo de Catálogos Básicos (Suscriptores, Agentes, Giros)

### HU-068: Conectar a Servicio de Catálogos Básicos
**Descripción**:
Como sistema,
Quiero establecer conexión con `Plataforma-core-ohs` (o su mock) para catálogos básicos,
Para poder consultar la información necesaria de suscriptores, agentes y giros.

**Criterios de Aceptación**:
- Dado que el cotizador se inicia, cuando intenta conectarse a `Plataforma-core-ohs` para catálogos básicos, entonces la conexión se establece exitosamente.
- Dado que el servicio `Plataforma-core-ohs` no está disponible, cuando el cotizador intenta conectarse, entonces se registra un error de conexión y se notifica al sistema.
- Dado que la configuración de conexión es incorrecta, cuando el cotizador intenta conectarse, entonces se registra un error de configuración y no se procede con la consulta.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Módulo de configuración de conexión

**Notas de Implementación**:
La conexión debe ser configurable (URL del servicio, credenciales). Se debe considerar un timeout para las solicitudes.

**Estado**: Backlog

---
### HU-069: Recuperar Catálogo de Suscriptores

**Descripción**:
Como sistema,
Quiero recuperar el catálogo de suscriptores desde el servicio `Plataforma-core-ohs` (o su mock),
Para ofrecer una lista actualizada en el formulario de cotización.

**Criterios de Aceptación**:
- Dado que el cotizador necesita el catálogo de suscriptores, cuando realiza una solicitud al servicio, entonces recibe una lista de suscriptores con sus IDs y nombres.
- Dado que el servicio devuelve una lista vacía, cuando el cotizador procesa la respuesta, entonces el catálogo de suscriptores se muestra vacío en la UI.
- Dado que la respuesta del servicio contiene datos malformados, cuando el cotizador los procesa, entonces se registra un error de mapeo y se utiliza una lista vacía o caché.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Cliente API REST
- Capa de mapeo de datos
- Repositorio de catálogos

**Notas de Implementación**:
El mapeo debe ser robusto para manejar posibles variaciones en el contrato de la API. Considerar paginación si el catálogo es muy grande.

**Estado**: Backlog

---
### HU-070: Recuperar Catálogo de Agentes

**Descripción**:
Como sistema,
Quiero recuperar el catálogo de agentes desde el servicio `Plataforma-core-ohs` (o su mock),
Para ofrecer una lista actualizada en el formulario de cotización.

**Criterios de Aceptación**:
- Dado que el cotizador necesita el catálogo de agentes, cuando realiza una solicitud al servicio, entonces recibe una lista de agentes con sus IDs y nombres.
- Dado que el servicio de agentes está temporalmente inactivo, cuando el cotizador intenta recuperarlo, entonces se aplica la estrategia de reintento y, si falla, se notifica.
- Dado que el usuario selecciona un agente, cuando el formulario se guarda, entonces el ID del agente se persiste correctamente con la cotización.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Cliente API REST
- Capa de mapeo de datos
- Repositorio de catálogos

**Notas de Implementación**:
Se debe asegurar que el ID del agente sea el valor que se persiste y no solo el nombre.

**Estado**: Backlog

---
### HU-071: Recuperar Catálogo de Giros

**Descripción**:
Como sistema,
Quiero recuperar el catálogo de giros desde el servicio `Plataforma-core-ohs` (o su mock),
Para ofrecer una lista actualizada en el formulario de cotización.

**Criterios de Aceptación**:
- Dado que el cotizador necesita el catálogo de giros, cuando realiza una solicitud al servicio, entonces recibe una lista de giros con sus IDs y descripciones.
- Dado que el catálogo de giros se actualiza en el origen, cuando el cotizador lo consulta, entonces los cambios se reflejan en la lista mostrada al usuario.
- Dado que la recuperación del catálogo de giros falla, cuando el cotizador intenta obtenerlo, entonces se muestra un mensaje de error genérico al usuario y se registra el fallo.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Cliente API REST
- Capa de mapeo de datos
- Repositorio de catálogos

**Notas de Implementación**:
La interfaz de usuario debe permitir seleccionar un giro y mostrar su descripción.

**Estado**: Backlog

---
### HU-072: Mapear y Transformar Datos de Catálogos Básicos

**Descripción**:
Como sistema,
Quiero mapear y transformar los datos de los catálogos básicos (suscriptores, agentes, giros) al modelo interno del cotizador,
Para garantizar su correcta utilización en la lógica de negocio y la interfaz de usuario.

**Criterios de Aceptación**:
- Dado que se reciben datos de un catálogo externo, cuando el sistema los procesa, entonces se transforman al formato del modelo de datos interno sin pérdida de información relevante.
- Dado que el formato del servicio externo cambia, cuando el sistema lo detecta, entonces el mapeo se puede ajustar sin afectar la lógica de negocio aguas abajo.
- Dado que un campo obligatorio del modelo interno falta en la respuesta externa, cuando el mapeo se ejecuta, entonces se genera un error específico y se registra.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-069, HU-070, HU-071

**Componentes Técnicos**:
- Capa de mapeo de datos
- Modelos de datos internos

**Notas de Implementación**:Se recomienda el uso de un patrón de adaptador o DTO para el mapeo.

**Estado**: Backlog

---
### HU-073: Manejar Errores y Reintentos de Conectividad de Catálogos Básicos

**Descripción**:
Como sistema,
Quiero tener un mecanismo robusto de manejo de errores y reintentos ante fallos de conectividad o datos inconsistentes del servicio externo de catálogos básicos,
Para asegurar la resiliencia y notificar fallos que requieran atención.

**Criterios de Aceptación**:
- Dado que el servicio externo no responde, cuando el cotizador intenta consultarlo, entonces se realiza un número configurable de reintentos con un backoff exponencial.
- Dado que todos los reintentos fallan, cuando el sistema no puede obtener el catálogo, entonces se registra un error crítico y se notifica a los administradores.
- Dado que el servicio devuelve un código de error HTTP (ej. 500), cuando el cotizador lo recibe, entonces se maneja como un fallo de conectividad y se activa el mecanismo de reintento.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Módulo de manejo de excepciones
- Estrategia de reintentos (ej. Resilience4j)
- Servicio de logging/notificación

**Notas de Implementación**:
Configuración del número de reintentos y tiempo de espera. Distinguir entre errores recuperables y no recuperables.

**Estado**: Backlog

---
