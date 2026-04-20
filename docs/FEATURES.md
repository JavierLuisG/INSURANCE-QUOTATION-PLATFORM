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

**Descripción**: Permite al usuario iniciar una nueva cotización o abrir una existente, capturando y modificando la información básica como Nombre del Asegurado, RFC, Tipo de Seguro, Moneda, Vigencia (fecha inicio/fin) y Canal de Venta. Incluye la gestión de folios y la consulta de catálogos básicos de suscriptores, agentes y giros.

**Valor para el Usuario**: Permite iniciar rápidamente el proceso de cotización con información esencial y asegura la consistencia de los datos iniciales, reduciendo el tiempo de entrada de información.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede crear un nuevo folio de cotización de forma idempotente.
- [ ] El usuario puede cargar y editar una cotización existente por su folio.
- [ ] Se pueden capturar y guardar todos los campos de datos generales (Nombre Asegurado, RFC, Tipo Seguro, Moneda, Vigencia, Canal Venta).
- [ ] Los campos de selección (e.g., Tipo de Seguro, Canal de Venta) ofrecen opciones válidas desde los servicios de referencia de `Plataforma-core-ohs`.
- [ ] La información general se guarda de forma persistente.

**Componentes Principales**: Interfaz de datos generales, API de cotizaciones, Integración con servicio de folios, Integración con catálogos básicos.
**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-007

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Considerar la idempotencia en la creación de folios para evitar duplicados. Los catálogos para Tipo de Seguro y Canal de Venta serán provistos por endpoints específicos de `Plataforma-core-ohs`.

---
## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Permite al usuario agregar, editar y eliminar múltiples ubicaciones de riesgo dentro de una cotización, con un límite por defecto de 10 ubicaciones que será configurable a nivel de sistema. Cada ubicación tendrá sus propios campos de captura y validaciones, incluyendo la integración con catálogos de códigos postales y la visualización de alertas por datos incompletos.

**Valor para el Usuario**: Facilita la especificación detallada del riesgo para cotizaciones complejas con múltiples ubicaciones, asegurando la precisión de los datos y agilizando el proceso de captura.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede agregar nuevas ubicaciones de riesgo a una cotización, hasta el límite establecido, y el sistema notifica claramente cuando se alcanza el máximo.
- [ ] El usuario puede editar los detalles de una ubicación existente.
- [ ] El usuario puede eliminar ubicaciones de riesgo de una cotización.
- [ ] Cada ubicación permite la captura de sus datos específicos (e.g., dirección, uso, características del inmueble).
- [ ] Se valida el código postal de cada ubicación contra el catálogo de CP.- [ ] Se muestra una alerta visual si una ubicación tiene datos incompletos o inválidos.
- [ ] Se implementa un patrón de interfaz híbrido basado en maestro-detalle con navegación tipo pestañas para la gestión de ubicaciones.

**Componentes Principales**: Interfaz de gestión de ubicaciones, API de cotizaciones (ubicaciones), Integración con catálogo de códigos postales.

**Historias de Usuario Estimadas**: 5-7 HUs

**Dependencias**: FT-001, FT-007, FT-009

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Alta

**Estimación**: 4-6 semanas / 2-3 sprints

**Estado**: Backlog

**Notas Técnicas**: Manejo de la lógica de validación de campos por ubicación y la persistencia de estructuras anidadas. Los campos de captura específicos para cada ubicación se definirán en el diseño de la interfaz de usuario con validación del negocio. El límite de ubicaciones será configurable por parámetro de sistema y validado en el dominio y backend.

---
## FT-003: Configuración y Selección de Coberturas por Ubicación

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Permite al usuario visualizar y seleccionar opciones de cobertura predefinidas de un catálogo para cada ubicación de riesgo. La selección de coberturas debe reflejarse en los parámetros de cálculo de la prima y puede incluir la configuración de sumas aseguradas o deducibles específicos.

**Valor para el Usuario**: Otorga flexibilidad al agente para personalizar la protección ofrecida, ajustándose a las necesidades específicas de cada ubicación y cliente, lo que mejora la propuesta de valor de la cotización.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede ver el catálogo de coberturas disponibles para cada tipo de seguro.
- [ ] El usuario puede seleccionar una o varias coberturas para cada ubicación.
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

**Descripción**: Permite al usuario solicitar el cálculo de la prima neta y comercial de la cotización, incluyendo el desglose por cada ubicación de riesgo y la aplicación de factores técnicos y reglas de negocio. Los resultados del cálculo deben ser persistidos junto con la cotización.

**Valor para el Usuario**: Proporciona al usuario información financiera precisa y detallada de manera automatizada, fundamental para la toma de decisiones y la presentación al cliente, reduciendo el esfuerzo manual y los errores.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede iniciar el proceso de cálculo de la prima.
- [ ] El sistema calcula la prima neta y comercial total de la cotización.
- [ ] El sistema calcula y muestra la prima para cada ubicación de riesgo individualmente.
- [ ] Los resultados del cálculo (prima neta, comercial, por ubicación) se guardan de forma persistente con la cotización.
- [ ] El cálculo debe considerar las reglas de negocio y los factores técnicos definidos.

**Componentes Principales**: Botón de cálculo, Backend del motor de cálculo, API de persistencia.

**Historias de Usuario Estimadas**: 4-6 HUs
**Dependencias**: FT-001, FT-002, FT-003, FT-007, FT-009

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Alta

**Estimación**: 4-8 semanas / 2-4 sprints

**Estado**: Backlog**Notas Técnicas**: Requiere integración con el motor de cálculo y reglas de negocio, y persistencia atómica de los resultados financieros. Se implementará una lógica de cálculo básica para las fórmulas simplificadas y se iterará sobre ella para su refinamiento.

---
## FT-005: Visualización Detallada de Resultados Financieros

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños
**Descripción**: Permite al usuario consultar los resultados completos del cálculo de la prima, incluyendo el resumen de primas, impuestos y recargos básicos, y cualquier otro desglose financiero relevante, tanto a nivel total como por ubicación. La interfaz debe presentar esta información de manera clara y organizada.

**Valor para el Usuario**: Ofrece transparencia y claridad sobre la composición del costo total del seguro, facilitando la explicación al cliente y la justificación de la cotización, lo que genera confianza.

**Criterios de Aceptación de la Feature**:
- [ ] El usuario puede ver el resumen de la prima neta y comercial total.
- [ ] Se muestra el desglose de la prima por cada ubicación de riesgo.
- [ ] Se pueden visualizar los componentes adicionales como impuestos y recargos básicos que afectan el precio final.
- [ ] Los resultados se presentan de manera clara y organizada en la interfaz.
- [ ] La información de los resultados financieros está sincronizada con el último cálculo realizado.

**Componentes Principales**: Interfaz de visualización de resultados, API de consulta de cotizaciones.

**Historias de Usuario Estimadas**: 3-5 HUs
**Dependencias**: FT-004

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Asegurar que la presentación de datos sea consistente con los cálculos y permita diferentes niveles de detalle. Solo se mostrarán impuestos y recargos básicos definidos en el cálculo.

---
## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Implementa la gestión de los diferentes estados por los que pasa una cotización (Borrador, Pendiente de Cálculo, Calculada, Aprobada, Rechazada, Emitida) y las transiciones válidas entre ellos, reflejando el progreso del proceso de venta y permitiendo al usuario actualizar el estado. Se define explícitamente un modelo de estados basado en una máquina de estados controlada desde el dominio, con transiciones válidas restringidas por reglas de negocio.

**Valor para el Usuario**: Proporciona un seguimiento claro del estado de cada cotización, mejorando la organización y la eficiencia en el proceso de venta de seguros y facilitando la gestión del pipeline.

**Criterios de Aceptación de la Feature**:
- [ ] La cotización inicia en estado "Borrador".
- [ ] El estado de la cotización se actualiza automáticamente a "Calculada" tras una ejecución exitosa del cálculo, siempre que las validaciones previas sean exitosas.
- [ ] El usuario puede cambiar manualmente el estado a "Aprobada" o "Rechazada" desde "Calculada".
- [ ] El estado "Emitida" se puede establecer una vez que la cotización ha sido "Aprobada" y es un estado terminal.
- [ ] La interfaz muestra claramente el estado actual de la cotización.
- [ ] Se implementan reglas de negocio para transiciones de estado válidas, asegurando que:
    - No se puede calcular una cotización si no cumple validaciones previas (ubicaciones completas, coberturas definidas, etc.).
    - No se puede aprobar una cotización sin haber sido previamente calculada.
    - Una cotización rechazada no puede ser emitida.
    - Cualquier modificación en estado CALCULADA o superior invalida el cálculo y regresa a BORRADOR o PENDIENTE_CALCULO según corresponda.

**Componentes Principales**: Backend de gestión de estados, API de actualización de estado, Interfaz de visualización de estado.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-001, FT-004, FT-009

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Modelado del estado de la cotización y las transiciones permitidas, utilizando una máquina de estados en el dominio. Las transiciones serán validadas a nivel de agregado (Cotización) para garantizar consistencia, evitando lógica condicional dispersa y favoreciendo un enfoque basado en reglas explícitas o patrón State.

---
## FT-007: Integración con Servicios de Referencia (Catálogos y Tarifas)

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Establece la integración con el servicio `Plataforma-core-ohs` (o su simulación) para consultar y obtener todos los catálogos maestros (suscriptores, agentes, giros, códigos postales, clasificación de riesgo, garantías) y tarifas/factores técnicos necesarios para las validaciones y cálculos de la cotización.

**Valor para el Usuario**: Asegura la consistencia y veracidad de los datos utilizados en las cotizaciones al centralizar la gestión de información de referencia, lo que reduce errores y garantiza la precisión de las primas.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema consume catálogos de suscriptores, agentes y giros.
- [ ] El sistema consulta y valida información de códigos postales y zonas de riesgo.
- [ ] Se obtienen catálogos de clasificación de riesgo y garantías.
- [ ] Se consultan tarifas y factores técnicos para el cálculo de primas.
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

**Valor para el Usuario**: Garantiza la integridad y la trazabilidad de los datos de las cotizaciones, evitando pérdidas de información por ediciones simultáneas y manteniendo un historial de cambios, lo que aumenta la fiabilidad del sistema.

**Criterios de Aceptación de la Feature**:
- [ ] Las ediciones de cotizaciones incrementan un campo de versión o similar.
- [ ] Las ediciones actualizan el campo `fechaUltimaActualizacion`.
- [ ] El sistema previene la sobrescritura de cambios si una versión más reciente ya fue guardada (versionado optimista).- [ ] Se permite la actualización parcial de campos sin afectar otros datos de la cotización.
- [ ] La persistencia de la cotización y sus ubicaciones es transaccional y consistente.

**Componentes Principales**: Capa de persistencia backend, Diseño de esquema de base de datos, Lógica de control de concurrencia.

**Historias de Usuario Estimadas**: 3-5 HUs
**Dependencias**: Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Elección de la estrategia de versionado (e.g., ETag, timestamp, número de versión), manejo de excepciones y reintentos. La estrategia específica para el versionado optimista será un número de versión incremental gestionado por el backend.

---
## FT-009: Implementación de Reglas de Negocio y Validaciones

**Épica Padre**: EP-001 - Gestión Integral de Cotizaciones de Daños

**Descripción**: Implementa las reglas de negocio y validaciones específicas para la captura de datos de cotizaciones y ubicaciones, así como las que rigen el cálculo de primas, asegurando la consistencia y corrección de la información en todo el sistema.

**Valor para el Usuario**: Garantiza la exactitud y cumplimiento normativo de las cotizaciones, minimizando errores en la captura de datos y asegurando que las primas calculadas sean correctas y consistentes con las políticas de negocio.

**Criterios de Aceptación de la Feature**:
- [ ] Se implementan las reglas de validación para los datos generales de la cotización (ej., formato RFC, rangos de vigencia).
- [ ] Se implementan las reglas de validación para los datos específicos de cada ubicación de riesgo (ej., valor del bien, año de construcción).
- [ ] La lógica de cálculo de primas incorpora las reglas de negocio y factores técnicos definidos (ej., aplicación de recargos, descuentos).
- [ ] El sistema proporciona mensajes de error claros y útiles cuando las validaciones fallan.
- [ ] Las reglas de negocio son trazables y documentadas.

**Componentes Principales**: Módulo de validación backend, Módulo de reglas de negocio, Lógica de aplicación de factores técnicos.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-001, FT-002, FT-003, FT-004

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Identificación y formalización de todas las reglas de negocio y validaciones, diseño de un motor de reglas o un enfoque basado en lógica de dominio. Se implementarán las validaciones más críticas primero, y se añadirán otras en futuras iteraciones.

# Features de la Épica: Motor de Cálculo y Reglas de Negocio (EP-002)

## FT-010: Configuración y Gestión de Parámetros de Cálculo

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Esta feature se encarga de la ingestión, almacenamiento y gestión de todos los parámetros, tarifas y catálogos externos necesarios para el motor de cálculo y las reglas de negocio. Incluye la integración con el servicio `Plataforma-core-ohs` (o su simulación) para obtener datos maestros como tarifas de incendio, factores CAT, cuotas FHM y la relación de códigos postales con zonas. Asegura que el motor de cálculo tenga acceso a la información más reciente y correcta para sus operaciones.

**Valor para el Usuario**: Garantiza que los cálculos de primas se basen en datos actualizados y correctos, lo que resulta en cotizaciones precisas y consistentes para los clientes.

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

**Notas Técnicas**: Se debe definir un mecanismo robusto para la carga y actualización de estos parámetros, considerando posibles versionados o fechas de vigencia si son aplicables en el futuro. La simulación debe ser fiel a los contratos de la API real. La gestión de versionado o fechas de vigencia para estos parámetros es una mejora futura, no en el alcance inicial del proyecto.

---
## FT-011: Motor de Validación de Reglas de Negocio

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Implementa las reglas de negocio necesarias para validar la integridad y corrección de los datos de una cotización y sus ubicaciones antes de proceder con el cálculo de primas. Esto incluye validaciones de rangos para sumas aseguradas, verificación de códigos postales contra catálogos y la comprobación de requisitos mínimos de datos para cada ubicación. El motor debe ser capaz de identificar y reportar claramente los errores de validación.

**Valor para el Usuario**: Asegura que solo se realicen cálculos con datos válidos y completos, reduciendo errores y reprocesos, y mejorando la calidad de las cotizaciones.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema valida que las sumas aseguradas estén dentro de los rangos predefinidos.
- [ ] El sistema valida los códigos postales de las ubicaciones contra el `catalogo_cp_zonas` provisto.
- [ ] El sistema verifica que todas las ubicaciones tengan los datos mínimos requeridos para el cálculo.
- [ ] El motor de validación proporciona mensajes de error claros y específicos para cada regla incumplida.
- [ ] El cálculo de prima no se ejecuta si existen errores de validación activos en la cotización o sus ubicaciones.

**Componentes Principales**: Módulo de reglas de validación, Servicio de consulta de catálogos (CP), Componente de reporte de errores.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-010 (para acceso a catálogos como `catalogo_cp_zonas`).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La implementación de reglas debe ser modular y extensible para futuras adiciones de validaciones. Se considera el uso de un patrón de especificación o un motor de reglas ligero. Se implementarán las validaciones más críticas primero, y se añadirán otras en futuras iteraciones.

---
## FT-012: Motor Central de Cálculo de Primas

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Desarrolla la lógica central para el cálculo de la prima neta, la prima comercial y el desglose de primas por cada ubicación de riesgo. Utiliza las fórmulas simplificadas y documentadas provistas, aplicando los diversos factores técnicos (incendio, CAT, FHM, equipo electrónico) y parámetros obtenidos a través de FT-010. Esta feature es el corazón del sistema para generar los resultados financieros de una cotización.

**Valor para el Usuario**: Proporciona los resultados financieros clave de la cotización, permitiendo a los usuarios conocer el costo de la póliza de manera precisa y detallada.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema calcula la prima neta para cada ubicación de riesgo utilizando las tarifas y factores correspondientes.
- [ ] El sistema aplica los factores de Catástrofe (CAT) y FHM según la zona y condiciones de la ubicación.
- [ ] El sistema calcula la prima comercial total de la cotización a partir de la suma de las primas netas y la aplicación de factores comerciales.
- [ ] El sistema genera el desglose de primas por cada ubicación de riesgo.
- [ ] Los cálculos son 100% precisos según las fórmulas simplificadas y documentadas.

**Componentes Principales**: Algoritmos de cálculo de prima, Servicio de aplicación de factores, Consolidación de primas.

**Historias de Usuario Estimadas**: 5-8 HUs

**Dependencias**: FT-010 (para acceso a parámetros y tarifas), FT-011 (las validaciones deben pasarse antes del cálculo).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Alta

**Estimación**: 4-8 semanas / 2-4 sprints**Estado**: Backlog

**Notas Técnicas**: La lógica de cálculo debe ser altamente testeable con cobertura unitaria >90%. Se recomienda el uso de objetos de valor o clases inmutables para representar los parámetros y resultados intermedios. Se implementará una lógica de cálculo básica para las fórmulas simplificadas y se iterará sobre ella para su refinamiento. La lógica de cálculo se basará en la interpretación directa de los datos proporcionados por el servicio, utilizando fórmulas simplificadas definidas en el alcance del proyecto, sin implementar lógica actuarial compleja o inferida.

---
## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Gestiona la persistencia de los resultados del cálculo de primas (neta, comercial, por ubicación) dentro del agregado de la cotización. Asegura que los resultados financieros se guarden de manera atómica y consistente en la base de datos (MongoDB). Además, implementa mecanismos para la trazabilidad de los cálculos, registrando información relevante que permita auditar cómo se llegó a un resultado específico sin duplicar innecesariamente datos externos.

**Valor para el Usuario**: Garantiza que los resultados de las cotizaciones se almacenen de forma segura y estén disponibles para consulta futura, y que se pueda entender cómo se derivó cada cálculo para fines de auditoría o revisión.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema persiste la prima neta, prima comercial y el desglose por ubicación como parte del documento de cotización en MongoDB.
- [ ] La operación de persistencia del cálculo es atómica, garantizando que todos los resultados se guarden o ninguno.
- [ ] El sistema actualiza el campo `fechaUltimaActualizacion` y el número de versión de la cotización tras cada persistencia de cálculo.
- [ ] El sistema registra un snapshot de parámetros de entrada relevantes (sumas aseguradas, coberturas seleccionadas, datos clave de ubicación), identificadores y versión lógica de tarifas/factores utilizados (tipo de tarifa, versión o timestamp de obtención), los valores numéricos concretos de los factores aplicados, el resultado detallado del cálculo y metadatos de ejecución (fecha/hora, versión de cotización) para permitir la trazabilidad.

**Componentes Principales**: Repositorio de cotizaciones, Módulo de persistencia de resultados, Componente de auditoría/logging de cálculo.
**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-012 (necesita los resultados del cálculo), FT-014 (trabaja en conjunto con la gestión de versionado).

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se debe considerar el diseño del esquema de datos en MongoDB para el almacenamiento de los resultados de cálculo y la información de trazabilidad, evitando duplicidades innecesarias y facilitando consultas. La trazabilidad prioriza la auditabilidad sobre el almacenamiento mínimo, permitiendo explicar "cómo se llegó al resultado" sin depender de datos externos cambiantes.

---
## FT-014: Gestión de Concurrencia y Versionado Optimista

**Épica Padre**: EP-002 - Motor de Cálculo y Reglas de Negocio

**Descripción**: Implementa un mecanismo de control de concurrencia basado en versionado optimista para las cotizaciones, utilizando un número de versión incremental gestionado por el backend. Este mecanismo detecta si una cotización ha sido modificada por otro usuario o proceso mientras se estaba editando. En caso de conflicto, el sistema notificará al usuario y le permitirá recargar la versión más reciente de la cotización antes de reintentar sus cambios, evitando la pérdida de datos o la sobrescritura no deseada.

**Valor para el Usuario**: Protege la integridad de los datos de la cotización al prevenir que los cambios de un usuario sobrescriban inadvertidamente los cambios de otro, especialmente en entornos multiusuario.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema utiliza un campo de versión para cada cotización que se incrementa en cada actualización.- [ ] Al intentar guardar una cotización, el sistema compara la versión de la cotización en memoria con la versión en la base de datos.
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

**Notas Técnicas**: La implementación de esta lógica debe ser genérica para cualquier operación de edición en la cotización. Se recomienda integrar el campo de versión directamente en el modelo de datos principal de la cotización.

# Features de la Épica: Integración y Gestión de Datos Maestros (EP-003)

## FT-015: Conectividad y Consumo de Catálogos Básicos (Suscriptores, Agentes, Giros)

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Establece la conexión y el consumo de los catálogos de suscriptores, agentes y giros desde el servicio `Plataforma-core-ohs` (o su simulación), asegurando que el cotizador disponga de esta información actualizada para la selección en los formularios.

**Valor para el Usuario**: El usuario puede seleccionar entidades de catálogos actualizados y consistentes, reduciendo errores y el esfuerzo de entrada manual de datos, y agilizando el proceso de cotización.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede conectarse al servicio `Plataforma-core-ohs` (o su mock) para obtener los catálogos de suscriptores, agentes y giros.
- [ ] Los datos de los catálogos se recuperan, mapean y transforman correctamente al modelo de datos interno del cotizador.
- [ ] Se implementa un mecanismo robusto para el manejo de errores y reintentos ante fallos de conectividad o datos inconsistentes del servicio externo.

**Componentes Principales**: Cliente API REST, capa de mapeo de datos, repositorio de catálogos.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Se requiere una definición clara de los contratos de API para estos catálogos. Es recomendable considerar una estrategia de caché para optimizar el rendimiento y reducir la carga sobre el servicio externo. Se utilizarán contratos de ejemplo y se refinarán de forma iterativa.

---
## FT-016: Integración de Catálogo de Códigos Postales y Zonas

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa la integración para consultar y validar información de códigos postales y sus respectivas zonas (CAT, nivel técnico) a través del servicio `Plataforma-core-ohs` (o su simulación), necesaria para la evaluación de riesgos y el cálculo de tarifas por ubicación.

**Valor para el Usuario**: El cotizador puede aplicar tarifas y factores de riesgo precisos basados en la ubicación geográfica de cada riesgo, mejorando la exactitud y consistencia de las primas calculadas.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede consultar códigos postales y obtener su información de zona (CAT, nivel técnico) desde `Plataforma-core-ohs` (o su mock).
- [ ] La validación de códigos postales se realiza correctamente, informando al usuario si un CP es inválido o no encontrado.
- [ ] La información de zonas obtenida se mapea y está disponible para la lógica de cálculo de primas por ubicación.

**Componentes Principales**: Cliente API REST, módulo de validación y consulta de CP, repositorio de CP y zonas.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)
**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Dada la posible gran cantidad de códigos postales, se debe diseñar una estrategia eficiente de consulta o carga inicial, y un mecanismo de caché para mejorar el rendimiento. Se implementará una estrategia híbrida basada en consulta bajo demanda con caché local inteligente (ej. Caffeine o Redis, con TTL configurable de 1-24 horas), optimizada para volumen alto y acceso frecuente, para balancear la frescura de datos y el rendimiento.

---
## FT-017: Integración de Catálogos de Clasificación de Riesgo y Garantías

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Desarrolla la integración para obtener los catálogos de clasificación de riesgo y garantías desde el servicio `Plataforma-core-ohs` (o su simulación), permitiendo la configuración detallada de las coberturas y la evaluación del riesgo asociado.

**Valor para el Usuario**: El usuario tiene acceso a un conjunto completo y actualizado de opciones para definir la clasificación de riesgo y las garantías aplicables a la cotización, asegurando la conformidad con las políticas de suscripción.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede recuperar los catálogos de clasificación de riesgo y garantías desde `Plataforma-core-ohs` (o su mock).
- [ ] Los datos de estos catálogos se mapean correctamente y están disponibles para la interfaz de usuario y la lógica de negocio.
- [ ] Los cambios realizados en estos catálogos en el sistema de origen se reflejan de manera consistente en el cotizador.

**Componentes Principales**: Cliente API REST, capa de mapeo de catálogos específicos.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)
**Prioridad en la Épica**: Media

**Complejidad Técnica**: Baja

**Estimación**: 1-3 semanas / 1 sprint

**Estado**: Backlog

**Notas Técnicas**: Estos catálogos suelen ser menos voluminosos y de actualización menos frecuente que otros, lo que simplifica su gestión y estrategia de caché.

---
## FT-018: Conectividad y Consumo de Tarifas y Factores Técnicos
**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa la conexión y el consumo de tarifas (incendio, CAT, FHM) y factores técnicos (equipo electrónico) desde el servicio `Plataforma-core-ohs` (o su simulación), fundamentales para el cálculo preciso de las primas netas y comerciales.

**Valor para el Usuario**: El cotizador realiza cálculos de primas precisos y actualizados con base en las tarifas y factores técnicos oficiales, garantizando la consistencia y fiabilidad de los resultados financieros.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema puede consultar las tarifas y factores técnicos requeridos desde `Plataforma-core-ohs` (o su mock).
- [ ] Los datos de tarifas y factores se recuperan y mapean correctamente para ser utilizados en la lógica de cálculo de primas.
- [ ] Se manejan los errores de conexión o la ausencia/inconsistencia de datos en las tarifas, notificando al sistema o usuario.

**Componentes Principales**: Cliente API REST, módulo de adaptación de tarifas, repositorio de tarifas.

**Historias de Usuario Estimadas**: 4-7 HUs

**Dependencias**: FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La estructura de las tarifas y factores puede ser compleja, requiriendo un mapeo cuidadoso y un entendimiento profundo de la lógica actuarial subyacente. La lógica de cálculo se basará en la interpretación directa de los datos proporcionados por el servicio, utilizando fórmulas simplificadas definidas en el alcance del proyecto, sin implementar lógica actuarial compleja o inferida.

---
## FT-019: Generación y Gestión de Folios Alfanuméricos

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Desarrolla la funcionalidad para generar folios alfanuméricos únicos (ej. 'COT-202X-000001') con un prefijo fijo y una secuencia numérica. El prefijo será "COT" por defecto, pero se diseñará como un parámetro configurable a nivel de sistema. Incluye mecanismos de reintento en caso de fallo y notificación al usuario para acción manual si la generación no es exitosa después de los reintentos.

**Valor para el Usuario**: Asegura que cada cotización tenga un identificador único y robusto, con un proceso resiliente ante fallos en la generación, lo que garantiza la trazabilidad y la integridad de los datos.

**Criterios de Aceptación de la Feature**:
- [ ] El sistema genera folios únicos siguiendo el patrón especificado ('PREFIJO-AAAA-NNNNNN').
- [ ] Se implementa un mecanismo de reintento automático configurable en caso de fallo en la generación del folio.
- [ ] Si la generación falla persistentemente después de los reintentos, se notifica al usuario o al sistema para intervención manual.
- [ ] La generación de folios es idempotente, evitando la creación de folios duplicados para la misma solicitud.

**Componentes Principales**: Servicio de generación de folios, módulo de secuencia numérica, servicio de notificación de errores.

**Historias de Usuario Estimadas**: 5-7 HUs

**Dependencias**: Ninguna

**Prioridad en la Épica**: Alta

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: Es crucial considerar la concurrencia en la generación de folios para evitar conflictos y asegurar la unicidad. Se debe persistir la última secuencia utilizada de forma segura. El prefijo "COT" será el valor por defecto, configurable vía variable de entorno o configuración del sistema, permitiendo flexibilidad futura sin cambiar la lógica de generación.

---
## FT-020: Simulación de Servicio `Plataforma-core-ohs` (Mock Server)

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa un mock server robusto que simula el servicio `Plataforma-core-ohs` API REST, replicando sus contratos y soportado por una base de datos (preferentemente MongoDB) poblada con migraciones (ej. Flyway). Esto permitirá generar respuestas dinámicas, controlar escenarios de prueba y garantizar consistencia en los datos de catálogos y tarifas.

**Valor para el Usuario**: Permite a los equipos de desarrollo y pruebas trabajar de forma independiente del servicio real, acelerando el desarrollo, facilitando la creación de escenarios de prueba complejos y consistentes, y minimizando dependencias externas.

**Criterios de Aceptación de la Feature**:
- [ ] El mock server está operativo y accesible para el cotizador.
- [ ] Simula fielmente los endpoints de los catálogos (suscriptores, agentes, giros, CP, riesgo, garantías) y tarifas de `Plataforma-core-ohs`.
- [ ] Permite configurar respuestas dinámicas y escenarios de error controlados para pruebas.
- [ ] La base de datos (MongoDB) del mock server se puebla y actualiza mediante migraciones (Flyway), manteniendo datos consistentes.
- [ ] La disponibilidad del servicio simulado se garantiza mediante pruebas de carga simuladas para validar su estabilidad.

**Componentes Principales**: Framework de mock server (ej. WireMock, Mountebank), MongoDB, Flyway, scripts de datos de prueba.

**Historias de Usuario Estimadas**: 6-8 HUs

**Dependencias**: Ninguna (es una dependencia crítica para el desarrollo del resto de las features de integración)

**Prioridad en la Épica**: Crítica

**Complejidad Técnica**: Alta

**Estimación**: 3-6 semanas / 2-3 sprints

**Estado**: Backlog

**Notas Técnicas**: Es fundamental definir los contratos de API de `Plataforma-core-ohs` antes de implementar el mock. La robustez y el versionado del mock server son clave para el progreso de todas las demás épicas dependientes. Se utilizarán contratos de ejemplo y se refinarán de forma iterativa.

---
## FT-021: Capa de Validación y Gestión de Inconsistencias de Datos Maestros

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Desarrolla una capa de integración que valida la consistencia y el formato de los datos maestros recibidos de `Plataforma-core-ohs`. Registra las inconsistencias detectadas y, cuando sea posible, intenta la corrección automática o notifica a los usuarios para revisión manual, garantizando la calidad del dato.

**Valor para el Usuario**: Asegura la calidad y fiabilidad de los datos utilizados por el cotizador, minimizando errores en los cálculos y presentaciones, y proporcionando trazabilidad de los problemas de datos para su resolución oportuna.

**Criterios de Aceptación de la Feature**:
- [ ] Se implementan reglas de validación para los datos maestros clave (catálogos, tarifas, folios).
- [ ] Las inconsistencias detectadas se registran en un log o repositorio específico con detalles suficientes para su análisis.
- [ ] El sistema puede aplicar reglas de corrección automática para tipos de inconsistencias predefinidos.
- [ ] Se activa una notificación (ej. log, alerta, correo) cuando se detectan inconsistencias que requieren intervención manual.

**Componentes Principales**: Módulo de validación de datos, servicio de logging, servicio de notificación, reglas de negocio para corrección.

**Historias de Usuario Estimadas**: 4-6 HUs

**Dependencias**: FT-015, FT-016, FT-017, FT-018

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La definición de las reglas de validación y los escenarios de corrección automática es crucial y debe hacerse en conjunto con los analistas funcionales para asegurar que cubren los casos de negocio. Se implementarán las validaciones más críticas primero, y se añadirán otras en futuras iteraciones.

---
## FT-022: Gestión de Caché y Estrategia de Actualización de Datos Maestros

**Épica Padre**: EP-003 - Integración y Gestión de Datos Maestros

**Descripción**: Implementa una estrategia de caché para optimizar el acceso a los datos maestros frecuentemente consultados (catálogos, tarifas) y define un mecanismo de actualización (ej. programado, bajo demanda, TTL) para asegurar que la información esté siempre fresca y consistente en el cotizador.

**Valor para el Usuario**: Mejora significativamente el rendimiento del cotizador al reducir el tiempo de respuesta de las consultas de datos maestros y disminuye la carga sobre los servicios externos, resultando en una experiencia de usuario más fluida y rápida.

**Criterios de Aceptación de la Feature**:
- [ ] Los datos maestros clave (catálogos, tarifas) se almacenan en caché de forma eficiente.
- [ ] El acceso a los datos en caché es más rápido que la consulta directa al servicio externo, cumpliendo los SLAs de tiempo de respuesta.
- [ ] Existe un mecanismo configurable para invalidar o actualizar los datos en caché (ej. TTL basado en tiempo, evento de actualización, invalidación manual).
- [ ] La consistencia de los datos en caché con la fuente original se mantiene según la política definida.

**Componentes Principales**: Framework de caché (ej. Caffeine, Redis), módulo de sincronización de datos, scheduler de actualización.

**Historias de Usuario Estimadas**: 3-5 HUs

**Dependencias**: FT-015, FT-016, FT-017, FT-018

**Prioridad en la Épica**: Media

**Complejidad Técnica**: Media

**Estimación**: 2-4 semanas / 1-2 sprints

**Estado**: Backlog

**Notas Técnicas**: La elección de la solución de caché adecuada y la definición de las políticas de caché (ej. cuándo invalidar, tamaño máximo, estrategia de desalojo) son fundamentales para el rendimiento y la consistencia del sistema. Se implementará una estrategia de caché basada en TTL configurable (Catálogos estáticos: 12–24 horas; Tarifas/factores: 1–6 horas), con estrategia de desalojo LRU y tamaño limitado por número de entradas, preparado para escalar a Redis si se requiere distribución, y sin implementar invalidación por eventos en la primera versión.