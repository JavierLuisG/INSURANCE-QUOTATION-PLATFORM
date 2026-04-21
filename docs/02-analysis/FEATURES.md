# Contenido general de Features

**EP-001: Gestión Integral de Cotizaciones de Daños**
- **FT-001**: Creación y Edición de Datos Generales de la Cotización
- **FT-002**: Gestión Dinámica de Ubicaciones de Riesgo
- **FT-003**: Configuración y Selección de Coberturas por Ubicación
- **FT-004**: Ejecución y Persistencia del Cálculo de Primas
- **FT-005**: Visualización Detallada de Resultados Financieros
- **FT-006**: Gestión del Ciclo de Vida y Estados de la Cotización
- **FT-007**: Integración con Servicios de Referencia (Catálogos y Tarifas)
- **FT-008**: Gestión de Persistencia Avanzada y Versionado Optimista
- **FT-009**: Implementación de Reglas de Negocio y Validaciones

**EP-002: Motor de Cálculo y Reglas de Negocio**
- **FT-010**: Configuración y Gestión de Parámetros de Cálculo
- **FT-011**: Motor de Validación de Reglas de Negocio
- **FT-012**: Motor Central de Cálculo de Primas
- **FT-013**: Persistencia y Trazabilidad de Resultados de Cálculo
- **FT-014**: Gestión de Concurrencia y Versionado Optimista

**EP-003: Integración y Gestión de Datos Maestros**
- **FT-015**: Conectividad y Consumo de Catálogos Básicos (Suscriptores, Agentes, Giros)
- **FT-016**: Integración de Catálogo de Códigos Postales y Zonas
- **FT-017**: Integración de Catálogos de Clasificación de Riesgo y Garantías
- **FT-018**: Conectividad y Consumo de Tarifas y Factores Técnicos
- **FT-019**: Generación y Gestión de Folios Alfanuméricos
- **FT-020**: Simulación de Servicio `Plataforma-core-ohs` (Mock Server)
- **FT-021**: Capa de Validación y Gestión de Inconsistencias de Datos Maestros
- **FT-022**: Gestión de Caché y Estrategia de Actualización de Datos Maestros
---

# Features de la Épica: Gestión Integral de Cotizaciones de Daños (EP-001)

## FT-001: Creación y Edición de Datos Generales de la Cotización

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Permite al usuario iniciar una nueva cotización o abrir una existente, capturando y modificando la información básica como Nombre del Asegurado, RFC, Tipo de Seguro, Moneda, Vigencia (fecha inicio/fin) y Canal de Venta. Incluye también la gestión y configuración del layout de ubicaciones (`configuracionLayout`), que define la estructura de campos a capturar por ubicación. Gestiona la generación de folios y la consulta de catálogos básicos de suscriptores, agentes y giros.

**Valor para el Usuario**: Permite iniciar rápidamente el proceso de cotización con información esencial, configurar cómo se capturarán las ubicaciones, y asegura la consistencia de los datos iniciales, reduciendo el tiempo de entrada de información.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede crear un nuevo folio de cotización de forma idempotente.
- [ ] El usuario puede cargar y editar una cotización existente por su folio.
- [ ] Se pueden capturar y guardar todos los campos de datos generales (Nombre Asegurado, RFC, Tipo Seguro, Moneda, Vigencia, Canal Venta).
- [ ] Los campos de selección (e.g., Tipo de Seguro, Canal de Venta) ofrecen opciones válidas desde los servicios de referencia de `Plataforma-core-ohs`.
- [ ] El usuario puede consultar y configurar el layout de ubicaciones del folio (`GET /v1/quotes/{folio}/locations/layout` y `PUT /v1/quotes/{folio}/locations/layout`).
- [ ] La información general y la configuración de layout se guardan de forma persistente.

**Componentes Principales**: Interfaz de datos generales, API de cotizaciones, Integración con servicio de folios, Integración con catálogos básicos, Endpoints de layout de ubicaciones.
**Historias de Usuario Estimadas**: 5-7 HUs

**Dependencias**: FT-007

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Considerar la idempotencia en la creación de folios para evitar duplicados. Los catálogos para Tipo de Seguro y Canal de Venta serán provistos por endpoints específicos de `Plataforma-core-ohs`. El `configuracionLayout` define dinámicamente los campos que se solicitan por ubicación y debe consultarse antes de renderizar el formulario de ubicaciones en el frontend.

---
## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Permite al usuario agregar y editar múltiples ubicaciones de riesgo dentro de una cotización, con un límite por defecto de 10 ubicaciones que será configurable a nivel de sistema. **Las ubicaciones no se eliminan una vez creadas; solo pueden editarse o marcarse como inactivas.** Cada ubicación contempla los siguientes campos del dominio: `índice`, `nombreUbicacion`, `direccion`, `codigoPostal`, `estado`, `municipio`, `colonia`, `ciudad`, `tipoConstructivo`, `nivel`, `anioConstruccion`, `giro` (con `giro.claveIncendio`), `garantías[]`, `zonaCatastrofica`, `alertasBloqueantes` y `estadoValidacion`. Incluye integración con catálogos de códigos postales y visualización de alertas por datos incompletos.

**Valor para el Usuario**: Facilita la especificación detallada del riesgo para cotizaciones complejas con múltiples ubicaciones, asegurando la precisión de los datos y agilizando el proceso de captura.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede agregar nuevas ubicaciones de riesgo a una cotización, hasta el límite establecido, y el sistema notifica claramente cuando se alcanza el máximo.
- [ ] El usuario puede editar los detalles de una ubicación existente con todos sus campos de dominio (`PATCH /v1/quotes/{folio}/locations/{índice}`).
- [ ] Las ubicaciones **no se eliminan**; el usuario puede marcarlas como inactivas (`estadoValidacion: INACTIVA`).
- [ ] Cada ubicación captura sus campos específicos del dominio según la lista definida en el dominio mínimo del reto.
- [ ] Se valida el código postal de cada ubicación contra el catálogo de CP y se obtiene `zonaCatastrofica` automáticamente.
- [ ] Se muestra una alerta visual si una ubicación tiene datos incompletos o inválidos (`alertasBloqueantes`).
- [ ] Se implementa un patrón de interfaz híbrido basado en maestro-detalle con navegación tipo pestañas para la gestión de ubicaciones.
- [ ] El endpoint `GET /v1/quotes/{folio}/locations/summary` devuelve un resumen de las ubicaciones con sus identificadores clave y estado de validación.

**Componentes Principales**: Interfaz de gestión de ubicaciones, API de cotizaciones (ubicaciones), Integración con catálogo de códigos postales, Endpoint de summary.

**Historias de Usuario Estimadas**: 6-8 HUs

**Dependencias**: FT-001, FT-007, FT-009

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Alta

**Estimación**: 4-6 semanas / 2-3 sprints

**Estado**: Backlog

**Notas Técnicas**: La regla de no-eliminación es un requisito del reto técnico. El campo `estadoValidacion` indica si la ubicación es `COMPLETA`, `INCOMPLETA` o `INACTIVA`. El campo `alertasBloqueantes` lista los campos faltantes o inválidos que impiden el cálculo de esa ubicación específica. El límite de ubicaciones será configurable por parámetro de sistema.

---
## FT-003: Configuración y Selección de Coberturas por Ubicación

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Permite al usuario visualizar y seleccionar opciones de cobertura predefinidas de un catálogo para cada ubicación de riesgo. La selección de coberturas debe reflejarse en los parámetros de cálculo de la prima y puede incluir la configuración de sumas aseguradas o deducibles específicos.

**Valor para el Usuario**: Otorga flexibilidad al agente para personalizar la protección ofrecida, ajustándose a las necesidades específicas de cada ubicación y cliente, lo que mejora la propuesta de valor de la cotización.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede ver el catálogo de coberturas disponibles para cada tipo de seguro (`GET /v1/quotes/{folio}/coverage-options`).
- [ ] El usuario puede seleccionar una o varias coberturas para cada ubicación (`PUT /v1/quotes/{folio}/coverage-options`).
- [ ] Las coberturas seleccionadas se asocian correctamente a la ubicación correspondiente.
- [ ] La interfaz permite configurar parámetros específicos de cobertura (e.g., sumas aseguradas, deducibles).
- [ ] La interfaz indica claramente qué coberturas están activas para cada ubicación.

**Componentes Principales**: Interfaz de selección de coberturas, API de cotizaciones (coberturas), Integración con catálogo de coberturas.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-002, FT-007

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Definición de la estructura de datos para coberturas, sus parámetros y su impacto en el cálculo. El catálogo de coberturas y sus parámetros específicos se gestionará internamente en el backend.

---
## FT-004: Ejecución y Persistencia del Cálculo de Primas

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Permite al usuario solicitar el cálculo de la prima neta y comercial de la cotización mediante `POST /v1/quotes/{folio}/calculate`, incluyendo el desglose por cada ubicación de riesgo y la aplicación de factores técnicos y reglas de negocio. **Regla clave**: si una ubicación está incompleta (sin `codigoPostal` válido, sin `giro.claveIncendio` o sin garantías tarifables), esa ubicación se marca con `alertasBloqueantes` y se excluye del cálculo, pero el proceso continúa para todas las demás ubicaciones válidas. El cálculo solo se bloquea completamente si no existe ninguna ubicación calculable.

**Valor para el Usuario**: Proporciona al usuario información financiera precisa y detallada de manera automatizada, fundamental para la toma de decisiones y la presentación al cliente.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede iniciar el proceso de cálculo de la prima.
- [ ] El sistema calcula la prima neta y comercial total de la cotización.
- [ ] El sistema calcula y muestra la prima para cada ubicación calculable individualmente.
- [ ] Las ubicaciones incompletas (sin CP válido, sin `giro.claveIncendio` o sin garantías tarifables) generan `alertasBloqueantes` y se excluyen del cálculo, **sin impedir el cálculo de las demás**.
- [ ] El cálculo se bloquea completamente **solo** si no hay ninguna ubicación calculable en la cotización.
- [ ] Los resultados del cálculo (`primaNeta`, `primaComercial`, `primasPorUbicacion[]`) se guardan de forma persistente y atómica con la cotización.
- [ ] El cálculo considera las reglas de negocio y los factores técnicos definidos.

**Componentes Principales**: Botón de cálculo, Backend del motor de cálculo (`POST /v1/quotes/{folio}/calculate`), API de persistencia.

**Historias de Usuario Estimadas**: 4-6 HUs
**Dependencias**: FT-001, FT-002, FT-003, FT-007, FT-009

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Alta

**Estimación**: 4-8 semanas / 2-4 sprints

**Estado**: Backlog

**Notas Técnicas**: La regla de "alerta sin bloqueo total" es un requisito explícito del reto técnico y debe implementarse en el motor de cálculo antes de la iteración por ubicaciones. Se implementará una lógica de cálculo básica para las fórmulas simplificadas y se iterará sobre ella para su refinamiento.

---
## FT-005: Visualización Detallada de Resultados Financieros

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Permite al usuario consultar los resultados completos del cálculo de la prima, incluyendo el resumen de primas, impuestos y recargos básicos, y el desglose financiero tanto a nivel total como por ubicación. La ruta `/quotes/{folio}/technical-info` en el frontend expone la vista técnica detallada con el desglose de cada componente de cálculo por ubicación. La interfaz debe presentar esta información de manera clara y organizada.

**Valor para el Usuario**: Ofrece transparencia y claridad sobre la composición del costo total del seguro, facilitando la explicación al cliente y la justificación de la cotización.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede ver el resumen de la prima neta y comercial total.
- [ ] Se muestra el desglose de la prima por cada ubicación calculada.
- [ ] Se muestran las alertas para las ubicaciones que no pudieron calcularse por estar incompletas.
- [ ] Se pueden visualizar los componentes adicionales como impuestos y recargos básicos que afectan el precio final.
- [ ] Los resultados se presentan de manera clara y organizada en la interfaz (ruta `/quotes/{folio}/technical-info`).
- [ ] La información de los resultados financieros está sincronizada con el último cálculo realizado.

**Componentes Principales**: Interfaz de visualización de resultados, API de consulta de cotizaciones, Ruta frontend `/quotes/{folio}/technical-info`.

**Historias de Usuario Estimadas**: 4-6 HUs
**Dependencias**: FT-004

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Asegurar que la presentación de datos sea consistente con los cálculos y permita diferentes niveles de detalle. Solo se mostrarán impuestos y recargos básicos definidos en el cálculo. La ruta `/quotes/{folio}/technical-info` muestra el desglose técnico detallado por componente de cálculo.

---
## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Implementa la gestión de los diferentes estados por los que pasa una cotización (Borrador, Pendiente de Cálculo, Calculada, Aprobada, Rechazada, Emitida) y las transiciones válidas entre ellos, reflejando el progreso del proceso de venta. Incluye la consulta de estado (`GET /v1/quotes/{folio}/state`) y la pantalla de términos y condiciones (`/quotes/{folio}/terms-and-conditions`) como paso previo a la aprobación/emisión. Se define un modelo de estados basado en una máquina de estados controlada desde el dominio.

**Valor para el Usuario**: Proporciona un seguimiento claro del estado de cada cotización, mejorando la organización y la eficiencia en el proceso de venta de seguros.

**Criterios de Aceptación de la Feature**:
- [ ] La cotización inicia en estado "Borrador".
- [ ] El estado de la cotización se actualiza automáticamente a "Calculada" tras una ejecución exitosa del cálculo.
- [ ] El usuario puede cambiar manualmente el estado a "Aprobada" o "Rechazada" desde "Calculada".
- [ ] El estado "Emitida" se puede establecer una vez que la cotización ha sido "Aprobada" y es un estado terminal.
- [ ] La interfaz muestra claramente el estado actual de la cotización (`GET /v1/quotes/{folio}/state`).
- [ ] La ruta `/quotes/{folio}/terms-and-conditions` presenta los términos y condiciones antes del proceso de aprobación/emisión.
- [ ] Se implementan reglas de negocio para transiciones de estado válidas:
    - No se puede calcular una cotización si no hay ninguna ubicación calculable.
    - No se puede aprobar una cotización sin haber sido previamente calculada.
    - Una cotización rechazada no puede ser emitida.
    - Cualquier modificación en estado CALCULADA o superior invalida el cálculo y regresa a BORRADOR o PENDIENTE_CALCULO.

**Componentes Principales**: Backend de gestión de estados, API de actualización de estado, Interfaz de visualización de estado, Ruta frontend `/quotes/{folio}/terms-and-conditions`.

**Historias de Usuario Estimadas**: 5-7 HUs

**Dependencias**: FT-001, FT-004, FT-009

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Modelado del estado de la cotización y las transiciones permitidas, utilizando una máquina de estados en el dominio. Las transiciones serán validadas a nivel de agregado (Cotización) para garantizar consistencia.

---
## FT-007: Integración con Servicios de Referencia (Catálogos y Tarifas)

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Establece la integración con el servicio `Plataforma-core-ohs` (o su simulación) para consultar y obtener todos los catálogos maestros (suscriptores, agentes, giros, códigos postales, clasificación de riesgo, garantías) y tarifas/factores técnicos necesarios para las validaciones y cálculos de la cotización.

**Valor para el Usuario**: Asegura la consistencia y veracidad de los datos utilizados en las cotizaciones al centralizar la gestión de información de referencia.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema consume catálogos de suscriptores, agentes y giros (`GET /v1/subscribers`, `GET /v1/agents`, `GET /v1/business-lines`).
- [ ] El sistema consulta y valida información de códigos postales y zonas de riesgo (`GET /v1/zip-codes/{zipCode}`, `POST /v1/zip-codes/validate`).
- [ ] Se obtienen catálogos de clasificación de riesgo y garantías (`GET /v1/catalogs/risk-classification`, `GET /v1/catalogs/guarantees`).
- [ ] Se consultan tarifas y factores técnicos para el cálculo de primas (`GET|PUT /v1/tariffs/...`).
- [ ] La integración es robusta y maneja posibles errores de comunicación con el servicio externo.

**Componentes Principales**: Capa de integración backend, Clientes API para `Plataforma-core-ohs`, Almacenamiento temporal de catálogos (opcional).

**Historias de Usuario Estimadas**: 5-8 HUs

**Dependencias**: Ninguna (es una dependencia técnica transversal, pero esta feature agrupa su implementación).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Alta

**Estimación**: 4-8 semanas / 2-4 sprints

**Estado**: Backlog

**Notas Técnicas**: Diseño de contratos API, manejo de caches si es necesario, implementación de stubs para desarrollo y pruebas.

---
## FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Implementa mecanismos robustos para la persistencia de datos de cotizaciones, incluyendo la actualización parcial de campos, el incremento de versión y la gestión de la fecha de última actualización. Se asegura el versionado optimista para prevenir conflictos en ediciones concurrentes.

**Valor para el Usuario**: Garantiza la integridad y la trazabilidad de los datos de las cotizaciones, evitando pérdidas de información por ediciones simultáneas.

**Criterios de Aceptación de la Feature**:
- [ ] Las ediciones de cotizaciones incrementan un campo de versión.
- [ ] Las ediciones actualizan el campo `fechaUltimaActualizacion`.
- [ ] El sistema previene la sobrescritura de cambios si una versión más reciente ya fue guardada (versionado optimista).
- [ ] Se permite la actualización parcial de campos sin afectar otros datos de la cotización.
- [ ] La persistencia de la cotización y sus ubicaciones es transaccional y consistente.

**Componentes Principales**: Capa de persistencia backend, Diseño de esquema de base de datos, Lógica de control de concurrencia.

**Historias de Usuario Estimadas**: 3-5 HUs
**Dependencias**: Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La estrategia de versionado optimista utilizará un número de versión incremental gestionado por el backend.

---
## FT-009: Implementación de Reglas de Negocio y Validaciones

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Implementa las reglas de negocio y validaciones específicas para la captura de datos de cotizaciones y ubicaciones, así como las que rigen el cálculo de primas, asegurando la consistencia y corrección de la información en todo el sistema.

**Valor para el Usuario**: Garantiza la exactitud y cumplimiento normativo de las cotizaciones, minimizando errores en la captura de datos.

**Criterios de Aceptación de la Feature**:
- [ ] Se implementan las reglas de validación para los datos generales de la cotización (ej., formato RFC, rangos de vigencia).
- [ ] Se implementan las reglas de validación para los datos específicos de cada ubicación de riesgo (ej., valor del bien, año de construcción).
- [ ] La lógica de cálculo de primas incorpora las reglas de negocio y factores técnicos definidos.
- [ ] El sistema proporciona mensajes de error claros y útiles cuando las validaciones fallan.
- [ ] Las reglas de negocio son trazables y documentadas.

**Componentes Principales**: Módulo de validación backend, Módulo de reglas de negocio, Lógica de aplicación de factores técnicos.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-001, FT-002, FT-003, FT-004

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se implementarán las validaciones más críticas primero. Las reglas deben ser modulares y extensibles.

# Features de la Épica: Motor de Cálculo y Reglas de Negocio (EP-002)

## FT-010: Configuración y Gestión de Parámetros de Cálculo

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Esta feature se encarga de la ingestión, almacenamiento y gestión de todos los parámetros, tarifas y catálogos externos necesarios para el motor de cálculo y las reglas de negocio. Incluye la integración con el servicio `Plataforma-core-ohs` (o su simulación) para obtener datos maestros como tarifas de incendio, factores CAT, cuotas FHM y la relación de códigos postales con zonas.

**Valor para el Usuario**: Garantiza que los cálculos de primas se basen en datos actualizados y correctos.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede consumir o simular la consulta de `tarifas_incendio` del servicio `Plataforma-core-ohs`.
- [ ] El sistema puede consumir o simular la consulta de `tarifas_cat` del servicio `Plataforma-core-ohs`.
- [ ] El sistema puede consumir o simular la consulta de `tarifa_fhm` del servicio `Plataforma-core-ohs`.
- [ ] El sistema puede consumir o simular la consulta de `factores_equipo_electronico` del servicio `Plataforma-core-ohs`.
- [ ] El sistema puede consumir o simular la consulta de `catalogo_cp_zonas` del servicio `Plataforma-core-ohs` y mapear zonas correctamente.
- [ ] Los parámetros y tarifas cargados están disponibles para el Motor de Validación y el Motor Central de Cálculo.

**Componentes Principales**: Adaptadores de integración (`Plataforma-core-ohs`), Repositorios de parámetros, Mapeadores de datos.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: `Plataforma-core-ohs` (simulación o real).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La simulación debe ser fiel a los contratos de la API real.

---
## FT-011: Motor de Validación de Reglas de Negocio

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Implementa las reglas de negocio necesarias para validar la integridad y corrección de los datos de una cotización y sus ubicaciones antes de proceder con el cálculo de primas. Determina el `estadoValidacion` de cada ubicación y establece sus `alertasBloqueantes`.

**Valor para el Usuario**: Asegura que solo se realicen cálculos con datos válidos y completos, reduciendo errores y reprocesos.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema valida que las sumas aseguradas estén dentro de los rangos predefinidos.
- [ ] El sistema valida los códigos postales de las ubicaciones contra el `catalogo_cp_zonas` provisto.
- [ ] El sistema verifica que cada ubicación calculable tenga: `codigoPostal` válido, `giro.claveIncendio` y al menos una garantía tarifable.
- [ ] Las ubicaciones que no cumplen los requisitos mínimos se marcan con `alertasBloqueantes` y `estadoValidacion: INCOMPLETA`, sin bloquear el cálculo de las demás.
- [ ] El motor de validación proporciona mensajes de error claros y específicos para cada regla incumplida.

**Componentes Principales**: Módulo de reglas de validación, Servicio de consulta de catálogos (CP), Componente de reporte de errores.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-010 (para acceso a catálogos como `catalogo_cp_zonas`).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La implementación de reglas debe ser modular y extensible para futuras adiciones.

---
## FT-012: Motor Central de Cálculo de Primas

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Desarrolla la lógica central para el cálculo de la prima neta, la prima comercial y el desglose de primas por cada ubicación de riesgo calculable. Utiliza las fórmulas simplificadas y documentadas provistas, aplicando los diversos factores técnicos y parámetros. **Los componentes técnicos que el cálculo debe contemplar por ubicación son**: Incendio edificios, Incendio contenidos, Extensión de cobertura, CAT TEV, CAT FHM, Remoción de escombros, Gastos extraordinarios, Pérdida de rentas, BI (Business Interruption), Equipo electrónico, Robo, Dinero y valores, Vidrios, y Anuncios luminosos. Solo se procesan los componentes activos según las coberturas seleccionadas (`opcionesCobertura`) y las garantías tarifables de la ubicación.

**Valor para el Usuario**: Proporciona los resultados financieros clave de la cotización, permitiendo a los usuarios conocer el costo de la póliza de manera precisa y detallada.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema itera únicamente sobre las ubicaciones con `estadoValidacion: COMPLETA`.
- [ ] El sistema calcula la prima neta para cada ubicación calculable aplicando los componentes activos de los 14 disponibles según las coberturas configuradas.
- [ ] El sistema aplica los factores de Catástrofe (CAT TEV y CAT FHM) según la zona y condiciones de la ubicación.
- [ ] El sistema calcula la prima comercial total de la cotización a partir de la suma de las primas netas y la aplicación de factores comerciales (`parametros_calculo`).
- [ ] El sistema genera el desglose de primas por cada ubicación calculable.
- [ ] Los cálculos son 100% precisos según las fórmulas simplificadas y documentadas.

**Componentes Principales**: Algoritmos de cálculo de prima por componente, Servicio de aplicación de factores, Consolidación de primas.

**Historias de Usuario Estimadas**: 6-9 HUs

**Dependencias**: FT-010 (para acceso a parámetros y tarifas), FT-011 (las validaciones deben pasarse antes del cálculo).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Alta

**Estimación**: 4-8 semanas / 2-4 sprints

**Estado**: Backlog

**Notas Técnicas**: La lógica de cálculo debe ser altamente testeable con cobertura unitaria >90%. No es obligatorio replicar exactamente una fórmula actuarial real, pero sí debe existir una lógica consistente, trazable y documentada para los 14 componentes. Se recomienda el uso de objetos de valor o clases inmutables para los parámetros y resultados.

---
## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Gestiona la persistencia de los resultados del cálculo de primas (`primaNeta`, `primaComercial`, `primasPorUbicacion[]`) dentro del agregado de la cotización en MongoDB, de forma atómica. Implementa mecanismos para la trazabilidad de los cálculos.

**Valor para el Usuario**: Garantiza que los resultados de las cotizaciones se almacenen de forma segura y estén disponibles para consulta futura, y que se pueda entender cómo se derivó cada cálculo.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema persiste `primaNeta`, `primaComercial` y el desglose por ubicación como parte del documento de cotización en MongoDB.
- [ ] La operación de persistencia del cálculo es atómica.
- [ ] El sistema actualiza `fechaUltimaActualizacion` y el número de versión tras cada persistencia de cálculo.
- [ ] El sistema registra un snapshot de parámetros de entrada relevantes, identificadores de tarifas/factores utilizados, valores numéricos aplicados y metadatos de ejecución para trazabilidad.

**Componentes Principales**: Repositorio de cotizaciones, Módulo de persistencia de resultados, Componente de auditoría/logging de cálculo.
**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-012 (necesita los resultados del cálculo), FT-014 (trabaja en conjunto con la gestión de versionado).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La trazabilidad prioriza la auditabilidad sobre el almacenamiento mínimo, permitiendo explicar "cómo se llegó al resultado" sin depender de datos externos cambiantes.

---
## FT-014: Gestión de Concurrencia y Versionado Optimista

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Implementa un mecanismo de control de concurrencia basado en versionado optimista para las cotizaciones. En caso de conflicto, el sistema notificará al usuario y le permitirá recargar la versión más reciente antes de reintentar.

**Valor para el Usuario**: Protege la integridad de los datos al prevenir que los cambios de un usuario sobrescriban inadvertidamente los cambios de otro.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema utiliza un campo de versión para cada cotización que se incrementa en cada actualización.
- [ ] Al intentar guardar una cotización, el sistema compara la versión en memoria con la versión en la base de datos.
- [ ] Si las versiones no coinciden, el sistema detecta un conflicto de concurrencia.
- [ ] En caso de conflicto, el sistema notifica al usuario de la existencia de una versión más reciente.
- [ ] El sistema permite al usuario recargar la cotización con la última versión desde la base de datos.

**Componentes Principales**: Lógica de control de versión, Manejador de errores de concurrencia, Sistema de notificación al usuario (backend).

**Historias de Usuario Estimadas**: 3-4 HUs

**Dependencias**: FT-013 (la persistencia debe integrar el control de versión).

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se recomienda integrar el campo de versión directamente en el modelo de datos principal de la cotización.

# Features de la Épica: Integración y Gestión de Datos Maestros (EP-003)

## FT-015: Conectividad y Consumo de Catálogos Básicos (Suscriptores, Agentes, Giros)

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Establece la conexión y el consumo de los catálogos de suscriptores, agentes y giros desde el servicio `Plataforma-core-ohs` (o su simulación), asegurando que el cotizador disponga de esta información actualizada para la selección en los formularios.

**Valor para el Usuario**: El usuario puede seleccionar entidades de catálogos actualizados y consistentes, reduciendo errores y el esfuerzo de entrada manual de datos.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede conectarse al servicio `Plataforma-core-ohs` (o su mock) para obtener los catálogos de suscriptores, agentes y giros.
- [ ] Los datos de los catálogos se recuperan, mapean y transforman correctamente al modelo de datos interno del cotizador.
- [ ] Se implementa un mecanismo robusto para el manejo de errores y reintentos ante fallos de conectividad.

**Componentes Principales**: Cliente API REST, capa de mapeo de datos, repositorio de catálogos.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se utilizarán contratos de ejemplo y se refinarán de forma iterativa.

---
## FT-016: Integración de Catálogo de Códigos Postales y Zonas

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa la integración para consultar y validar información de códigos postales y sus respectivas zonas (CAT, nivel técnico) a través del servicio `Plataforma-core-ohs` (o su simulación).

**Valor para el Usuario**: El cotizador puede aplicar tarifas y factores de riesgo precisos basados en la ubicación geográfica de cada riesgo.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede consultar códigos postales y obtener su información de zona desde `Plataforma-core-ohs`.
- [ ] La validación de códigos postales se realiza correctamente, informando al usuario si un CP es inválido.
- [ ] La información de zonas obtenida se mapea y está disponible para la lógica de cálculo de primas por ubicación.

**Componentes Principales**: Cliente API REST, módulo de validación y consulta de CP, repositorio de CP y zonas.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)
**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se implementará una estrategia híbrida basada en consulta bajo demanda con caché local inteligente (TTL configurable de 1-24 horas).

---
## FT-017: Integración de Catálogos de Clasificación de Riesgo y Garantías

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Desarrolla la integración para obtener los catálogos de clasificación de riesgo y garantías desde el servicio `Plataforma-core-ohs`.

**Valor para el Usuario**: El usuario tiene acceso a un conjunto completo y actualizado de opciones para definir la clasificación de riesgo y las garantías aplicables.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede recuperar los catálogos de clasificación de riesgo y garantías desde `Plataforma-core-ohs`.
- [ ] Los datos de estos catálogos se mapean correctamente y están disponibles para la interfaz de usuario y la lógica de negocio.
- [ ] Los cambios realizados en estos catálogos en el sistema de origen se reflejan de manera consistente en el cotizador.

**Componentes Principales**: Cliente API REST, capa de mapeo de catálogos específicos.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)
**Prioridad en la Épica**: Media

**Complejidad Técnica**: Baja

**Estimación**: 1-3 semanas / 1 sprint

**Estado**: Backlog

**Notas Técnicas**: Estos catálogos suelen ser menos voluminosos y de actualización menos frecuente.

---
## FT-018: Conectividad y Consumo de Tarifas y Factores Técnicos

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa la conexión y el consumo de tarifas (incendio, CAT TEV, FHM) y factores técnicos (equipo electrónico, otros) desde el servicio `Plataforma-core-ohs` (o su simulación), fundamentales para el cálculo de los 14 componentes técnicos de prima.

**Valor para el Usuario**: El cotizador realiza cálculos de primas precisos y actualizados con base en las tarifas y factores técnicos oficiales.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede consultar las tarifas y factores técnicos requeridos desde `Plataforma-core-ohs`.
- [ ] Los datos de tarifas y factores se recuperan y mapean correctamente para ser utilizados en la lógica de cálculo de primas.
- [ ] Se manejan los errores de conexión o la ausencia/inconsistencia de datos, notificando al sistema o usuario.

**Componentes Principales**: Cliente API REST, módulo de adaptación de tarifas, repositorio de tarifas.

**Historias de Usuario Estimadas**: 4-7 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La lógica de cálculo utilizará fórmulas simplificadas definidas en el alcance del proyecto, sin implementar lógica actuarial compleja.

---
## FT-019: Generación y Gestión de Folios Alfanuméricos

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Desarrolla la funcionalidad para generar folios alfanuméricos únicos (ej. 'COT-202X-000001') con un prefijo fijo y una secuencia numérica. Incluye mecanismos de reintento en caso de fallo y notificación al usuario para acción manual si la generación no es exitosa.

**Valor para el Usuario**: Asegura que cada cotización tenga un identificador único y robusto, con un proceso resiliente ante fallos.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema genera folios únicos siguiendo el patrón especificado ('PREFIJO-AAAA-NNNNNN').
- [ ] Se implementa un mecanismo de reintento automático configurable en caso de fallo.
- [ ] Si la generación falla persistentemente, se notifica al usuario o al sistema para intervención manual.
- [ ] La generación de folios es idempotente, evitando la creación de folios duplicados.

**Componentes Principales**: Servicio de generación de folios, módulo de secuencia numérica, servicio de notificación de errores.

**Historias de Usuario Estimadas**: 5-7 HUs

**Dependencias**: Ninguna

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: El prefijo "COT" será el valor por defecto, configurable vía variable de entorno.

---
## FT-020: Simulación de Servicio `Plataforma-core-ohs` (Mock Server)

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa un mock server robusto que simula el servicio `Plataforma-core-ohs` API REST, replicando sus contratos y soportado por una base de datos (preferentemente MongoDB) poblada con migraciones (ej. Flyway).

**Valor para el Usuario**: Permite a los equipos de desarrollo y pruebas trabajar de forma independiente del servicio real.

**Criterios de Aceptación de la Feature**:
- [ ] El mock server está operativo y accesible para el cotizador.
- [ ] Simula fielmente los endpoints de los catálogos y tarifas de `Plataforma-core-ohs`, incluyendo: `GET /v1/subscribers`, `GET /v1/agents`, `GET /v1/business-lines`, `GET /v1/zip-codes/{zipCode}`, `POST /v1/zip-codes/validate`, `GET /v1/folios`, `GET /v1/catalogs/risk-classification`, `GET /v1/catalogs/guarantees`, `GET|PUT /v1/tariffs/...`
- [ ] Permite configurar respuestas dinámicas y escenarios de error controlados para pruebas.
- [ ] La base de datos del mock server se puebla y actualiza mediante migraciones (Flyway).
- [ ] La disponibilidad del servicio simulado se garantiza mediante pruebas de carga simuladas.

**Componentes Principales**: Framework de mock server (ej. WireMock, Mountebank), MongoDB, Flyway, scripts de datos de prueba.

**Historias de Usuario Estimadas**: 6-8 HUs

**Dependencias**: Ninguna (es una dependencia crítica para el desarrollo del resto de las features de integración)

**Prioridad en la Épica**: Crítica

**Complejidad Técnica**: Alta

**Estimación**: 3-6 semanas / 2-3 sprints

**Estado**: Backlog

**Notas Técnicas**: Es fundamental definir los contratos de API de `Plataforma-core-ohs` antes de implementar el mock. Los contratos deben documentarse y entregarse como parte del reto.

---
## FT-021: Capa de Validación y Gestión de Inconsistencias de Datos Maestros

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Desarrolla una capa de integración que valida la consistencia y el formato de los datos maestros recibidos de `Plataforma-core-ohs`, registrando inconsistencias y aplicando corrección automática cuando sea posible.

**Valor para el Usuario**: Asegura la calidad y fiabilidad de los datos utilizados por el cotizador.

**Criterios de Aceptación de la Feature**:
- [ ] Se implementan reglas de validación para los datos maestros clave (catálogos, tarifas, folios).
- [ ] Las inconsistencias detectadas se registran en un log o repositorio específico.
- [ ] El sistema puede aplicar reglas de corrección automática para tipos de inconsistencias predefinidos.
- [ ] Se activa una notificación cuando se detectan inconsistencias que requieren intervención manual.

**Componentes Principales**: Módulo de validación de datos, servicio de logging, servicio de notificación, reglas de negocio para corrección.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-015, FT-016, FT-017, FT-018

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se implementarán las validaciones más críticas primero.

---
## FT-022: Gestión de Caché y Estrategia de Actualización de Datos Maestros

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa una estrategia de caché para optimizar el acceso a los datos maestros frecuentemente consultados (catálogos, tarifas) y define un mecanismo de actualización basado en TTL configurable.

**Valor para el Usuario**: Mejora significativamente el rendimiento del cotizador al reducir el tiempo de respuesta de las consultas de datos maestros.

**Criterios de Aceptación de la Feature**:
- [ ] Los datos maestros clave se almacenan en caché de forma eficiente.
- [ ] El acceso a los datos en caché es más rápido que la consulta directa al servicio externo.
- [ ] Existe un mecanismo configurable para invalidar o actualizar los datos en caché (TTL basado en tiempo).
- [ ] La consistencia de los datos en caché con la fuente original se mantiene según la política definida.

**Componentes Principales**: Framework de caché (ej. Caffeine, Redis), módulo de sincronización de datos, scheduler de actualización.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-015, FT-016, FT-017, FT-018

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se implementará una estrategia de caché basada en TTL configurable (Catálogos estáticos: 12–24 horas; Tarifas/factores: 1–6 horas), con estrategia de desalojo LRU. No se implementará invalidación por eventos en la primera versión.
