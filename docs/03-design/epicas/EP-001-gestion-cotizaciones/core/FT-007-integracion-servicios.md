## FT-007: Integración con Servicios de Referencia (Catálogos y Tarifas)

### HU-029: Consumir catálogos de suscriptores, agentes y giros

Como usuario, quiero que el sistema consulte catálogos de suscriptores, agentes y giros desde el servicio de referencia, para asegurar la información de negocio.

**Criterios de Aceptación**:
- Dado que necesito seleccionar un suscriptor, agente o giro, cuando accedo al campo correspondiente, entonces las opciones se cargan desde `Plataforma-core-ohs`.
- Dado que el servicio de catálogos responde con éxito, cuando se muestran las opciones, entonces estas son válidas y actualizadas.
- Dado que el servicio de catálogos no está disponible, cuando intento cargar las opciones, entonces el sistema muestra un mensaje de error y usa un fallback (si aplica).

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-001: Creación y Edición de Datos Generales de la Cotización

**Componentes Técnicos**:
- Backend: Cliente API para `Plataforma-core-ohs` (catálogos).
- Frontend: Componentes de selección (dropdowns).
**Notas de Implementación**:
- Se debe implementar un mecanismo de caché para los catálogos si son estáticos o cambian poco.
- Manejar la paginación y búsqueda si los catálogos son extensos.

**Estado**: Backlog

---
### HU-030: Consultar y validar información de códigos postales y zonas de riesgo

Como usuario, quiero que el sistema consulte información de códigos postales y zonas de riesgo desde el servicio de referencia, para validar direcciones y aplicar factores.

**Criterios de Aceptación**:
- Dado que introduzco un código postal, cuando el sistema lo valida, entonces consulta el catálogo de CP de `Plataforma-core-ohs` para obtener detalles (municipio, estado, zona de riesgo).
- Dado que un código postal está asociado a una zona de riesgo, cuando se recupera esa información, entonces está disponible para el cálculo de primas.
- Dado que el servicio de CP no está disponible, cuando intento validar un CP, entonces el sistema gestiona el error y notifica al usuario.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-009: Consultar y validar código postal de ubicación

**Componentes Técnicos**:
- Backend: Cliente API para `Plataforma-core-ohs` (catálogo de CP).
**Notas de Implementación**:
- La integración debe ser eficiente para soportar validaciones en tiempo real en el frontend.
- Se debe diseñar un contrato claro para la respuesta del servicio de CP.

**Estado**: Backlog---
### HU-031: Obtener catálogos de clasificación de riesgo y garantías

Como usuario, quiero que el sistema obtenga catálogos de clasificación de riesgo y garantías desde el servicio de referencia, para asociarlos a las ubicaciones y coberturas.

**Criterios de Aceptación**:
- Dado que necesito clasificar el riesgo de una ubicación o seleccionar una garantía, cuando accedo a la opción correspondiente, entonces las opciones se cargan desde `Plataforma-core-ohs`.
- Dado que los catálogos se cargan correctamente, cuando se utilizan, entonces los datos son consistentes con la información de referencia.
- Dado que el servicio de `Plataforma-core-ohs` no proporciona estos catálogos, cuando se accede, entonces se utiliza un mecanismo de simulación o datos predefinidos.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-002: Gestión Dinámica de Ubicaciones de Riesgo
- FT-003: Configuración y Selección de Coberturas por Ubicación

**Componentes Técnicos**:
- Backend: Cliente API para `Plataforma-core-ohs` (catálogos de riesgo/garantías).

**Notas de Implementación**:
- Asegurar que la estructura de datos de estos catálogos sea compatible con el modelo de cotización.
- Considerar la simulación o stubs para el desarrollo si el servicio real no está disponible.

**Estado**: Backlog

---
### HU-032: Consultar tarifas y factores técnicos para el cálculo de primas

Como usuario, quiero que el sistema consulte tarifas y factores técnicos desde el servicio de referencia, para realizar cálculos de prima precisos.

**Criterios de Aceptación**:
- Dado que se inicia un cálculo de prima, cuando el motor de cálculo lo requiere, entonces se consultan las tarifas (e.g., `tarifas_incendio`, `tarifas_cat`, `tarifa_fhm`) y factores técnicos de `Plataforma-core-ohs`.
- Dado que las tarifas y factores se obtienen, cuando se usan en el cálculo, entonces se aplican según las reglas de negocio.
- Dado que el servicio de tarifas no responde, cuando se intenta calcular, entonces el sistema gestiona el error y no permite el cálculo o utiliza valores por defecto (si es aceptable).

**Prioridad**: Alta

**Estimación**: 5 puntos de historia

**Dependencias**:
- FT-004: Ejecución y Persistencia del Cálculo de Primas
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Backend: Cliente API para `Plataforma-core-ohs` (tarifas y factores técnicos), Motor de cálculo.

**Notas de Implementación**:
- La integración con el servicio de tarifas es crítica para la exactitud de los cálculos.
- Se debe manejar la complejidad de diferentes tipos de tarifas y sus parámetros de consulta.

**Estado**: Backlog

---
### HU-033: Implementar robustez en la integración con servicios externos

Como desarrollador, quiero que la integración con `Plataforma-core-ohs` sea robusta, para manejar errores de comunicación y asegurar la estabilidad del sistema.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` no está disponible, cuando el sistema intenta consultarlo, entonces se implementa un mecanismo de reintento.
- Dado que el servicio `Plataforma-core-ohs` devuelve un error, cuando el sistema lo procesa, entonces se registra el error y se notifica al usuario o se usa un fallback.
- Dado que la integración se realiza, cuando se implementa, entonces se utilizan timeouts para evitar esperas indefinidas.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- Todas las HUs de FT-007

**Componentes Técnicos**:
- Backend: Capa de integración, Manejo de excepciones, Circuit Breaker.

**Notas de Implementación**:
- Utilizar patrones de resiliencia como Circuit Breaker, Retry, Fallback.
- Implementar logging detallado para la depuración de problemas de integración.

**Estado**: Backlog

---
### HU-034: Simular el servicio Plataforma-core-ohs para desarrollo y pruebas

Como desarrollador, quiero poder simular el servicio `Plataforma-core-ohs`, para facilitar el desarrollo y las pruebas sin depender del servicio real.

**Criterios de Aceptación**:
- Dado que el servicio real no está disponible, cuando ejecuto el sistema en modo de desarrollo/pruebas, entonces el sistema utiliza un stub o mock server para `Plataforma-core-ohs`.
- Dado que se utiliza la simulación, cuando se realizan operaciones que requieren el servicio externo, entonces el sistema responde con datos predefinidos.
- Dado que la simulación está configurada, cuando se ejecuta el sistema, entonces se puede alternar entre la simulación y el servicio real.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- Ninguna (es una dependencia técnica transversal para el equipo)

**Componentes Técnicos**:
- Backend: Stubs, Mock servers o Fixtures versionadas para `Plataforma-core-ohs`.

**Notas de Implementación**:
- Los datos de los mocks deben ser representativos y versionados.
- La configuración para usar el mock debe ser sencilla.

**Estado**: Backlog

---
