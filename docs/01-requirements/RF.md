## Requerimientos Funcionales - Módulo: cotizador-danos-web (Frontend SPA)

### RF-001: Creación y Apertura de Folio
**Descripción**: El sistema debe permitir al usuario crear un nuevo folio de cotización o abrir uno existente para su consulta o edición.
**Objetivo**: Facilitar el inicio de una nueva cotización o la continuación de una existente.
**Criterios de Aceptación**:
- Dado que el usuario está en la página principal, cuando selecciona "Crear Nuevo Folio", entonces el sistema crea un nuevo folio y lo muestra en la interfaz.
- Dado que el usuario tiene un número de folio, cuando lo ingresa en el campo de búsqueda y selecciona "Abrir Folio", entonces el sistema carga los datos del folio correspondiente y los muestra en la interfaz.
**Prioridad**: Alta
**Dependencias**: RF-009 (Creación Idempotente de Folios)
**Datos de Entrada**: Solicitud de nuevo folio, Número de folio existente
**Datos de Salida**: Nuevo folio creado, Datos de folio existente

### RF-002: Gestión de Datos Generales de Cotización
**Descripción**: El sistema debe permitir al usuario capturar, consultar y editar la información general de la cotización, como datos del asegurado, agente, tipo de giro, etc.
**Objetivo**: Registrar la información básica y contextual de cada cotización.
**Criterios de Aceptación**:
- Dado que un folio está abierto, cuando el usuario ingresa datos en los campos de información general (ej. nombre de suscriptor, agente, giro) y guarda, entonces el sistema persiste estos datos asociados al folio.
- Dado que un folio con datos generales guardados está abierto, cuando el usuario consulta la sección de datos generales, entonces el sistema muestra la información previamente guardada.
- Dado que el usuario modifica un dato general y guarda, entonces el sistema actualiza el dato y persiste la nueva versión.
- Dado que el usuario intenta guardar los datos generales, cuando los campos 'nombre de suscriptor', 'ID de agente', 'tipo de giro', 'fecha de inicio de vigencia' y 'fecha de fin de vigencia' están vacíos, entonces el sistema muestra una alerta de campos obligatorios.
- Dado que el usuario accede a la sección de configuración, cuando selecciona 'Gestionar Layouts', entonces el sistema permite configurar o previsualizar la estructura de campos de los datos generales.
**Prioridad**: Alta
**Dependencias**: RF-010 (Gestión de Datos Generales de Cotización en Backend)
**Datos de Entrada**: Nombre de suscriptor, ID de agente, Tipo de giro, Fecha de inicio/fin de vigencia, etc.
**Datos de Salida**: Datos generales de la cotización guardados/actualizados

### RF-003: Consulta de Catálogos de Referencia
**Descripción**: El sistema debe permitir al usuario consultar y seleccionar valores de catálogos de referencia, como suscriptores, agentes, giros y códigos postales, para completar los datos de la cotización y las ubicaciones. La interacción con catálogos extensos se realizará mediante un modal de búsqueda con paginación y filtros, complementado con autocompletado para selección rápida, y dropdowns para catálogos pequeños.
**Objetivo**: Asegurar la consistencia y validez de los datos ingresados utilizando valores predefinidos.
**Criterios de Aceptación**:
- Dado que el usuario está capturando datos generales, cuando interactúa con el campo "Suscriptor", entonces el sistema muestra una lista de suscriptores disponibles para selección.
- Dado que el usuario ingresa un código postal en una ubicación, cuando el sistema lo valida, entonces muestra la información asociada al CP (ej. colonia, municipio, estado, zona) o un mensaje de error si no es válido. Si la información no es completa, se mostrará la disponible y se permitirá al usuario completar manualmente los campos faltantes, con una advertencia visual.
**Prioridad**: Media
**Dependencias**: RF-017, RF-018 (Provisión de Catálogos en Servicio de Referencia)
**Datos de Entrada**: Criterios de búsqueda para catálogos (ej. nombre de suscriptor, código postal)
**Datos de Salida**: Listas de suscriptores, agentes, giros; datos de código postal

### RF-004: Gestión de Ubicaciones de Riesgo
**Descripción**: El sistema debe permitir al usuario registrar, consultar y editar una o varias ubicaciones de riesgo dentro de una cotización, incluyendo sus configuraciones y datos específicos (ej. dirección, valores asegurados, clasificación de riesgo, garantías). Las ubicaciones no podrán ser eliminadas una vez creadas, solo editadas o marcadas como inactivas.
**Objetivo**: Detallar los diferentes puntos geográficos y sus características para la evaluación del riesgo y el cálculo de la prima.
**Criterios de Aceptación**:
- Dado que un folio está abierto, cuando el usuario selecciona "Agregar Ubicación", entonces el sistema presenta un formulario para capturar los detalles de una nueva ubicación.
- Dado que el usuario ha capturado los datos de una ubicación y los guarda, entonces la ubicación se registra y se muestra en una lista dentro del folio.
- Dado que una ubicación está registrada, cuando el usuario la selecciona para editar, entonces el sistema carga sus datos en el formulario para modificación.
- Dado que una ubicación ha sido modificada y guardada, entonces el sistema actualiza sus datos asociados al folio.
- Dado que una ubicación tiene datos incompletos, cuando el usuario intenta guardar, entonces el sistema muestra alertas indicando los campos faltantes o inválidos mediante validación en línea por campo y un mensaje de resumen en la parte superior del formulario.
- Dado que el usuario intenta guardar una ubicación, cuando los campos 'dirección completa', 'código postal', 'valores asegurados', 'clasificación de riesgo' y 'garantías' están vacíos o incompletos, entonces el sistema muestra una alerta de campos obligatorios.**Prioridad**: Alta
**Dependencias**: RF-011 (Gestión de Ubicaciones de Riesgo en Backend), RF-015 (Aplicación de Reglas de Negocio)
**Datos de Entrada**: Dirección, Código Postal, Valores asegurados, Clasificación de riesgo, Garantías, etc.
**Datos de Salida**: Ubicaciones de riesgo registradas, actualizadas o eliminadas

### RF-005: Visualización del Progreso y Estado del Folio
**Descripción**: El sistema debe mostrar al usuario el estado actual del folio de cotización y el progreso de la captura de datos, incluyendo alertas si hay ubicaciones incompletas o datos pendientes. Se definirán estados claros (ej. Borrador, Datos Incompletos, Listo para Cálculo, Calculado, Error de Cálculo) con indicadores visuales distintivos en el frontend.
**Objetivo**: Guiar al usuario a través del proceso de cotización y asegurar la integridad de los datos.
**Criterios de Aceptación**:
- Dado que el usuario está en la vista del folio, cuando una o más ubicaciones tienen datos incompletos, entonces el sistema muestra un icono de advertencia claramente visible en cada ubicación con problemas y un resumen de alertas en el encabezado del folio, indicando un estado de "Datos Incompletos".
- Dado que todos los datos requeridos para una ubicación están completos, entonces el sistema indica que la ubicación está lista para el cálculo, contribuyendo a un estado de "Listo para Cálculo" del folio.
- Dado que el cálculo de prima ha sido ejecutado, entonces el sistema actualiza el estado del folio para reflejar que los resultados están disponibles, indicando un estado de "Calculado" o "Error de Cálculo".
**Prioridad**: Media
**Dependencias**: RF-012 (Consulta de Estado y Coberturas en Backend)
**Datos de Entrada**: Ninguna (derivado del estado interno del folio)
**Datos de Salida**: Indicadores visuales de estado y alertas

### RF-006: Configuración de Opciones de Cobertura
**Descripción**: El sistema debe permitir al usuario seleccionar y configurar las opciones de cobertura deseadas para la cotización, que pueden influir en el cálculo de la prima. Se presentará una lista fija de coberturas para todas las cotizaciones, sin variación.
**Objetivo**: Adaptar la cotización a las necesidades específicas del cliente.
**Criterios de Aceptación**:
- Dado que un folio está abierto, cuando el usuario accede a la sección de coberturas, entonces el sistema presenta una lista de checkboxes o toggles, con descripciones claras y, si aplica, campos para configurar valores (ej. deducibles, sumas aseguradas).
- Dado que el usuario selecciona o deselecciona una cobertura, entonces el sistema actualiza la configuración de coberturas del folio.
**Prioridad**: Media
**Dependencias**: RF-012 (Consulta de Estado y Coberturas en Backend)
**Datos de Entrada**: Selección de coberturas
**Datos de Salida**: Configuración de coberturas del folio

### RF-007: Ejecución del Cálculo de la Prima
**Descripción**: El sistema debe permitir al usuario iniciar el proceso de cálculo de la prima neta y comercial para el folio completo, incluyendo el desglose por ubicación.
**Objetivo**: Generar los resultados financieros de la cotización basándose en los datos capturados y las reglas de negocio.
**Criterios de Aceptación**:
- Dado que todos los campos obligatorios definidos para los datos generales del folio y cada ubicación están llenos y pasan las validaciones de negocio, cuando el usuario selecciona "Calcular Prima", entonces el sistema inicia el proceso de cálculo.
- Dado que el cálculo está en progreso, entonces el sistema muestra un spinner o barra de progreso con el mensaje 'Calculando prima...'
- Dado que el cálculo se ha completado exitosamente, entonces el sistema indica que los resultados están disponibles para visualización y muestra un mensaje de éxito.
- Dado que faltan datos o hay errores de validación, cuando el usuario intenta calcular la prima, entonces el sistema muestra un mensaje de error y no procede con el cálculo.
**Prioridad**: Alta
**Dependencias**: RF-013 (Ejecución y Persistencia del Cálculo de Prima en Backend), RF-005 (Visualización del Progreso)
**Datos de Entrada**: Solicitud de cálculo de prima
**Datos de Salida**: Indicador de cálculo completado/fallido

### RF-008: Visualización de Resultados Financieros
**Descripción**: El sistema debe mostrar al usuario los resultados detallados del cálculo de la prima, incluyendo la prima neta, prima comercial y el desglose de primas por cada ubicación de riesgo. Se presentará una tabla interactiva como vista principal, mostrando prima neta, prima comercial y desglose por ubicación.
**Objetivo**: Presentar de manera clara y comprensible el costo del seguro.
**Criterios de Aceptación**:
- Dado que el cálculo de la prima se ha completado, cuando el usuario accede a la sección de resultados, entonces el sistema muestra la prima neta total, la prima comercial total y la prima calculada para cada ubicación en una tabla interactiva.
- Dado que se han aplicado diferentes factores o tarifas, entonces el sistema permite expandir la tabla para mostrar un desglose detallado que incluya la prima neta, recargos, impuestos y cualquier otro factor que contribuya a la prima comercial total y por ubicación.
- Dado que el usuario desea un formato externo, entonces el sistema permite la exportación de los resultados a PDF y Excel.
- Dado que se desea una interpretación rápida, entonces el sistema puede incluir indicadores visuales opcionales (ej. gráficos simples) sin reemplazar la tabla como fuente principal de información.
**Prioridad**: Alta
**Dependencias**: RF-013 (Ejecución y Persistencia del Cálculo de Prima en Backend)
**Datos de Entrada**: Ninguna (derivado del resultado del cálculo)
**Datos de Salida**: Prima neta, Prima comercial, Desglose de primas por ubicación

### RF-009: Autenticación de Usuarios
**Descripción**: El sistema debe permitir a los usuarios (Asegurado, Agente de Seguros) autenticarse mediante credenciales (usuario/contraseña) y gestionar su sesión para acceder a las funcionalidades del cotizador.
**Objetivo**: Garantizar el acceso seguro y controlado a la aplicación.
**Criterios de Aceptación**:
- Dado que un usuario no autenticado intenta acceder a una funcionalidad protegida, cuando ingresa sus credenciales válidas, entonces el sistema lo autentica y le permite el acceso.
- Dado que un usuario ha iniciado sesión, cuando su sesión expira o cierra sesión, entonces el sistema invalida su acceso y requiere una nueva autenticación.
**Prioridad**: Alta
**Dependencias**: `plataformas-danos-back` (para validación de credenciales y gestión de tokens)
**Datos de Entrada**: Usuario, Contraseña
**Datos de Salida**: Token de sesión (JWT), Estado de autenticación

### RF-010: Manejo y Visualización de Errores Genéricos
**Descripción**: El sistema debe centralizar la visualización de errores genéricos (ej. errores de red, errores del servidor) que son respondidos por el backend, mostrando mensajes claros y amigables al usuario.
**Objetivo**: Mejorar la experiencia del usuario al proporcionar retroalimentación clara sobre problemas técnicos.
**Criterios de Aceptación**:
- Dado que se produce un error en el backend (ej. 500 Internal Server Error, 404 Not Found), cuando el frontend recibe la respuesta de error, entonces muestra un mensaje genérico adecuado al usuario sin exponer detalles técnicos sensibles.
- Dado que la conexión con el backend falla (ej. error de red), cuando el frontend intenta realizar una operación, entonces muestra una alerta indicando problemas de conectividad.
**Prioridad**: Media
**Dependencias**: `plataformas-danos-back` (para estandarización de respuestas de error)
**Datos de Entrada**: Respuestas de error del backend
**Datos de Salida**: Alertas/mensajes de error para el usuario

### RF-011: Gestión de Roles de Usuario y Acceso a Funcionalidades
**Descripción**: El sistema debe implementar un sistema básico de roles para diferenciar las funcionalidades accesibles por los Asegurados y los Agentes de Seguros, mostrando solo las opciones relevantes para cada tipo de usuario.**Objetivo**: Asegurar que los usuarios solo accedan a las funcionalidades para las que tienen permiso.
**Criterios de Aceptación**:
- Dado que un usuario con rol 'Asegurado' ha iniciado sesión, cuando navega por la aplicación, entonces solo tiene acceso a las funcionalidades permitidas para asegurados.
- Dado que un usuario con rol 'Agente de Seguros' ha iniciado sesión, cuando navega por la aplicación, entonces tiene acceso a las funcionalidades permitidas para agentes, que pueden incluir opciones adicionales no disponibles para asegurados.
**Prioridad**: Alta
**Dependencias**: RF-009 (Autenticación de Usuarios)
**Datos de Entrada**: Rol de usuario (obtenido tras autenticación)
**Datos de Salida**: Interfaz de usuario adaptada al rol, Restricciones de acceso a funcionalidades

### RF-012: Persistencia Local de Datos en Progreso
**Descripción**: El sistema debe implementar mecanismos para proteger los datos en progreso del usuario en el frontend, incluyendo guardado automático local, advertencias al salir y recuperación de sesión, para evitar la pérdida de información debido a cierres accidentales o interrupciones inesperadas.
**Objetivo**: Prevenir la pérdida de información no guardada oficialmente y mejorar la resiliencia de la interfaz de usuario.
**Criterios de Aceptación**:
- Dado que el usuario está capturando datos en un folio, cuando realiza cambios, entonces el sistema guarda automáticamente una copia de los datos en progreso en el almacenamiento local del navegador, con versionado básico por folio, sin reemplazar la persistencia oficial en el backend.
- Dado que el usuario intenta salir de la aplicación o cerrar la pestaña con cambios no guardados en el backend pero presentes en el almacenamiento local, entonces el sistema muestra una advertencia clara pidiendo confirmación para salir sin guardar y ofreciendo la opción de mantener la sesión activa.
- Dado que un usuario reingresa a la aplicación después de una sesión previa con datos en progreso no guardados oficialmente, entonces el sistema detecta la existencia de datos locales y ofrece la opción de restaurar el estado previo del formulario desde el almacenamiento local.
**Prioridad**: Media
**Dependencias**: Ninguna
**Datos de Entrada**: Cambios del usuario en el formulario
**Datos de Salida**: Datos guardados en local storage, Advertencias, Opciones de restauración

## Requerimientos Funcionales - Módulo: plataformas-danos-back (Backend Principal)

### RF-009: Creación Idempotente de Folios
**Descripción**: El sistema debe permitir la creación de nuevos folios de cotización, asegurando que la operación sea idempotente, es decir, que múltiples solicitudes con los mismos parámetros de creación resulten en la creación de un único folio.**Objetivo**: Garantizar la unicidad de los folios y prevenir duplicados accidentales.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para crear un folio, cuando el folio no existe, entonces el sistema crea un nuevo folio y devuelve su ID.
- Dado que se recibe una solicitud para crear un folio, cuando ya existe un folio asociado a la misma solicitud, entonces el sistema devuelve el ID del folio existente sin crear uno nuevo.
**Prioridad**: Alta
**Dependencias**: RF-019 (Generación Secuencial de Folios en Plataforma-core-ohs)
**Datos de Entrada**: Solicitud de creación de folio
**Datos de Salida**: ID del folio creado/existente

### RF-010: Gestión de Datos Generales de Cotización (Backend)
**Descripción**: El sistema debe gestionar la persistencia (guardar, consultar, editar) de los datos generales de una cotización, incluyendo la configuración de layouts y metadatos de actualización.
**Objetivo**: Almacenar y recuperar la información básica de la cotización de forma estructurada.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para guardar/actualizar datos generales de un folio, cuando los datos son válidos, entonces el sistema persiste la información y actualiza la fecha de última actualización y la versión.
- Dado que se recibe una solicitud de consulta para un folio, entonces el sistema devuelve los datos generales de la cotización asociados a ese folio.
- Dado que se intenta actualizar un folio con una versión desactualizada, entonces el sistema rechaza la operación debido a un conflicto de versionado optimista.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: ID de folio, Datos generales de cotización (suscriptor, agente, giro, etc.), Versión actual
**Datos de Salida**: Confirmación de guardado/actualización, Datos generales de cotización

### RF-011: Gestión de Ubicaciones de Riesgo (Backend)
**Descripción**: El sistema debe permitir el registro, consulta, edición y resumen de múltiples ubicaciones de riesgo asociadas a un folio de cotización, manteniendo la integridad de los datos. No se permitirá la eliminación de ubicaciones, solo su edición o marcaje como inactivas.
**Objetivo**: Administrar de manera robusta la información detallada de cada ubicación de riesgo.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para agregar una nueva ubicación a un folio, cuando los datos de la ubicación son válidos, entonces el sistema la persiste y la asocia al folio.
- Dado que se recibe una solicitud para consultar las ubicaciones de un folio, entonces el sistema devuelve la lista de todas las ubicaciones registradas para ese folio.
- Dado que se recibe una solicitud para editar una ubicación existente, cuando los datos son válidos y la versión coincide, entonces el sistema actualiza la ubicación y sus metadatos.
- Dado que se recibe una solicitud para resumir las ubicaciones de un folio, entonces el sistema devuelve un listado conciso de las ubicaciones con sus identificadores clave.
**Prioridad**: Alta
**Dependencias**: RF-015 (Aplicación de Reglas de Negocio)
**Datos de Entrada**: ID de folio, Datos de ubicación (dirección, valores, clasificación, garantías), ID de ubicación, Versión
**Datos de Salida**: Confirmación de operación, Lista de ubicaciones, Ubicación específica

### RF-012: Consulta de Estado y Opciones de Cobertura (Backend)
**Descripción**: El sistema debe proporcionar la capacidad de consultar el estado actual de una cotización (ej. Borrador, Datos Incompletos, Listo para Cálculo, Calculado, Error de Cálculo) y las opciones de cobertura seleccionadas para un folio.
**Objetivo**: Soportar la interfaz de usuario en la visualización del progreso y la configuración de la cotización.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para consultar el estado de un folio, entonces el sistema evalúa la completitud de los datos y devuelve un indicador de estado (Borrador, Datos Incompletos, Listo para Cálculo, Calculado, Error de Cálculo).
- Dado que se recibe una solicitud para consultar las opciones de cobertura de un folio, entonces el sistema devuelve la configuración de coberturas actualmente seleccionada.
**Prioridad**: Media
**Dependencias**: Ninguna
**Datos de Entrada**: ID de folio
**Datos de Salida**: Estado del folio, Opciones de cobertura seleccionadas

### RF-013: Ejecución y Persistencia del Cálculo de Prima
**Descripción**: El sistema debe ejecutar la lógica de negocio para calcular la prima neta, la prima comercial y el desglose de primas por ubicación, y persistir estos resultados financieros dentro del folio de cotización.
**Objetivo**: Generar y almacenar los resultados monetarios de la cotización.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para calcular la prima de un folio con datos completos, cuando la lógica de cálculo se ejecuta, entonces el sistema calcula la prima neta, prima comercial y primas por ubicación.
- Dado que el cálculo se ha completado, entonces el sistema persiste los resultados financieros en el folio de cotización.
- Dado que la lógica de cálculo requiere parámetros externos (ej. tarifas, factores), cuando se ejecuta el cálculo, entonces el sistema consulta el servicio `Plataforma-core-ohs` para obtenerlos.
**Prioridad**: Alta
**Dependencias**: RF-015 (Aplicación de Reglas de Negocio), RF-021 (Consulta de Tarifas y Factores Técnicos)
**Datos de Entrada**: ID de folio, Datos de ubicaciones, Opciones de cobertura
**Datos de Salida**: Prima neta, Prima comercial, Desglose de primas por ubicación, Estado de cálculo

### RF-014: Manejo de Versionado Optimista
**Descripción**: El sistema debe implementar un mecanismo de versionado optimista para todas las operaciones de edición sobre los datos principales de la cotización (folio y ubicaciones), previniendo conflictos de concurrencia.
**Objetivo**: Asegurar la integridad de los datos frente a modificaciones simultáneas.
**Criterios de Aceptación**:
- Dado que un usuario intenta actualizar un recurso (folio o ubicación) con una versión desactualizada, entonces el sistema rechaza la operación y devuelve un error de conflicto.
- Dado que un usuario actualiza un recurso con la versión correcta, entonces el sistema incrementa la versión del recurso y actualiza la fecha de última modificación.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: Recurso a actualizar (folio/ubicación), Versión esperada
**Datos de Salida**: Recurso actualizado con nueva versión, Error de conflicto

### RF-015: Aplicación de Reglas de Negocio para Cálculo y Validación
**Descripción**: El sistema debe aplicar reglas de negocio predefinidas para la validación de los datos de las ubicaciones y para la ejecución de la lógica de cálculo de la prima.
**Objetivo**: Asegurar la validez de los datos y la exactitud de los cálculos según las políticas de negocio.
**Criterios de Aceptación**:
- Dado que se ingresan datos de ubicación, cuando se guardan, entonces el sistema valida los datos contra las reglas de negocio (ej. rangos de valores, formatos) y devuelve errores si no cumplen.
- Dado que se ejecuta el cálculo de la prima, entonces el sistema aplica las reglas de negocio para determinar los factores, tasas y cuotas relevantes.
- Dado que una regla de negocio requiere un dato externo (ej. zona de CP, clasificación de riesgo), entonces el sistema consulta el servicio `Plataforma-core-ohs` para obtenerlo.
**Prioridad**: Alta
**Dependencias**: RF-018, RF-020, RF-021 (Catálogos y Tarifas de Plataforma-core-ohs)
**Datos de Entrada**: Datos de ubicación, Parámetros de cálculo
**Datos de Salida**: Resultados de validación (errores/éxito), Factores/tasas aplicados en cálculo

### RF-016: Integración con Servicio de Referencia `Plataforma-core-ohs`
**Descripción**: El sistema debe consumir o simular los servicios expuestos por `Plataforma-core-ohs` para obtener catálogos (suscriptores, agentes, giros, CP, clasificación de riesgo, garantías) y tarifas necesarias para la cotización y el cálculo.
**Objetivo**: Centralizar la gestión de datos maestros y tarifas en un servicio externo y utilizarlos en el proceso de cotización.
**Criterios de Aceptación**:
- Dado que se requiere un catálogo (ej. suscriptores), cuando el backend lo solicita, entonces el servicio `Plataforma-core-ohs` devuelve la lista de elementos del catálogo.
- Dado que se requiere la validación o información de un código postal, cuando el backend lo solicita, entonces el servicio `Plataforma-core-ohs` devuelve los datos asociados al CP.
- Dado que se requieren tarifas o factores técnicos para el cálculo, cuando el backend los solicita, entonces el servicio `Plataforma-core-ohs` devuelve los datos tarifarios correspondientes.
**Prioridad**: Alta
**Dependencias**: RF-017, RF-018, RF-019, RF-020, RF-021 (Funcionalidades de Plataforma-core-ohs)
**Datos de Entrada**: Solicitudes de catálogos, códigos postales, tarifas
**Datos de Salida**: Datos de catálogos, información de CP, tarifas y factores

## Requerimientos Funcionales - Módulo: Plataforma-core-ohs (Servicio de Referencia)

### RF-017: Provisión de Catálogo de Suscriptores, Agentes y Giros
**Descripción**: El servicio debe proporcionar una API para consultar los catálogos de suscriptores, agentes y giros, permitiendo la búsqueda y recuperación de sus datos.
**Objetivo**: Centralizar y distribuir la información de entidades clave para el negocio.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de catálogo de suscriptores, entonces el servicio devuelve una lista de suscriptores con sus identificadores y nombres.
- Dado que se recibe una solicitud de catálogo de agentes, entonces el servicio devuelve una lista de agentes con sus identificadores y nombres.
- Dado que se recibe una solicitud de catálogo de giros, entonces el servicio devuelve una lista de giros con sus identificadores y descripciones.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: Criterios de búsqueda (opcional), Tipo de catálogo
**Datos de Salida**: Lista de elementos del catálogo (suscriptores, agentes, giros)

### RF-018: Validación y Consulta de Códigos Postales
**Descripción**: El servicio debe proporcionar una API para validar códigos postales y consultar la información asociada a ellos, como zona CAT y nivel técnico.
**Objetivo**: Asegurar la correcta ubicación geográfica y obtener datos relevantes para la clasificación de riesgo.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de validación para un código postal, cuando el CP es válido y existe, entonces el servicio devuelve la información geográfica asociada (ej. ciudad, estado, zona CAT).
- Dado que se recibe una solicitud de validación para un código postal, cuando el CP no es válido o no existe, entonces el servicio devuelve un indicador de error.
**Prioridad**: Alta
**Dependencias**: Base de datos `catalogo_cp_zonas`
**Datos de Entrada**: Código postal
**Datos de Salida**: Información geográfica (zona CAT, nivel técnico, etc.) o error

### RF-019: Generación Secuencial de Folios
**Descripción**: El servicio debe proporcionar una API para generar folios secuenciales únicos para las cotizaciones.
**Objetivo**: Asegurar la asignación de identificadores únicos y consecutivos para cada nueva cotización.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para generar un nuevo folio, entonces el servicio genera y devuelve un identificador de folio único y secuencial.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: Solicitud de generación de folio
**Datos de Salida**: Nuevo número de folio

### RF-020: Suministro de Catálogos de Clasificación de Riesgo y Garantías
**Descripción**: El servicio debe proporcionar una API para consultar los catálogos de clasificación de riesgo y garantías disponibles.
**Objetivo**: Ofrecer opciones estandarizadas para definir el perfil de riesgo y las protecciones adicionales en las ubicaciones.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de catálogo de clasificación de riesgo, entonces el servicio devuelve una lista de clasificaciones de riesgo.
- Dado que se recibe una solicitud de catálogo de garantías, entonces el servicio devuelve una lista de garantías disponibles.
**Prioridad**: Media
**Dependencias**: Ninguna
**Datos de Entrada**: Tipo de catálogo (riesgo/garantías)
**Datos de Salida**: Lista de clasificaciones de riesgo, Lista de garantías

### RF-021: Consulta de Tarifas y Factores Técnicos
**Descripción**: El servicio debe proporcionar una API para consultar las tarifas base y los factores técnicos (ej. factores CAT, cuotas FHM, factores de equipo electrónico) necesarios para el cálculo de primas.
**Objetivo**: Centralizar la gestión y distribución de los datos tarifarios y actuariales.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de tarifas para un tipo de seguro (ej. incendio), entonces el servicio devuelve las tasas base y metadatos técnicos correspondientes.
- Dado que se recibe una solicitud de factores CAT para una zona específica, entonces el servicio devuelve los factores CAT aplicables.
- Dado que se recibe una solicitud de cuotas FHM para un grupo, zona y condición, entonces el servicio devuelve las cuotas FHM.
- Dado que se recibe una solicitud de factores de equipo electrónico para una clase y nivel de zona, entonces el servicio devuelve los factores técnicos.
**Prioridad**: Alta
**Dependencias**: Bases de datos `tarifas_incendio`, `tarifas_cat`, `tarifa_fhm`, `factores_equipo_electronico`
**Datos de Entrada**: Tipo de tarifa/factor, Parámetros de búsqueda (ej. zona, grupo, clase)
**Datos de Salida**: Tarifas y factores técnicos aplicables

## Requerimientos Funcionales - Módulo: cotizador-danos-web (Frontend SPA)

### RF-001: Creación y Apertura de Folio
**Descripción**: El sistema debe permitir al usuario crear un nuevo folio de cotización o abrir uno existente para su consulta o edición.
**Objetivo**: Facilitar el inicio de una nueva cotización o la continuación de una existente.
**Criterios de Aceptación**:
- Dado que el usuario está en la página principal, cuando selecciona "Crear Nuevo Folio", entonces el sistema crea un nuevo folio y lo muestra en la interfaz.
- Dado que el usuario tiene un número de folio, cuando lo ingresa en el campo de búsqueda y selecciona "Abrir Folio", entonces el sistema carga los datos del folio correspondiente y los muestra en la interfaz.
**Prioridad**: Alta
**Dependencias**: RF-009 (Creación Idempotente de Folios)
**Datos de Entrada**: Solicitud de nuevo folio, Número de folio existente
**Datos de Salida**: Nuevo folio creado, Datos de folio existente

### RF-002: Gestión de Datos Generales de Cotización
**Descripción**: El sistema debe permitir al usuario capturar, consultar y editar la información general de la cotización, como datos del asegurado, agente, tipo de giro, etc.
**Objetivo**: Registrar la información básica y contextual de cada cotización.
**Criterios de Aceptación**:
- Dado que un folio está abierto, cuando el usuario ingresa datos en los campos de información general (ej. nombre de suscriptor, agente, giro) y guarda, entonces el sistema persiste estos datos asociados al folio.
- Dado que un folio con datos generales guardados está abierto, cuando el usuario consulta la sección de datos generales, entonces el sistema muestra la información previamente guardada.
- Dado que el usuario modifica un dato general y guarda, entonces el sistema actualiza el dato y persiste la nueva versión.
- Dado que el usuario intenta guardar los datos generales, cuando los campos 'nombre de suscriptor', 'ID de agente', 'tipo de giro', 'fecha de inicio de vigencia' y 'fecha de fin de vigencia' están vacíos, entonces el sistema muestra una alerta de campos obligatorios.
- Dado que el usuario accede a la sección de configuración, cuando selecciona 'Gestionar Layouts', entonces el sistema permite configurar o previsualizar la estructura de campos de los datos generales.
**Prioridad**: Alta
**Dependencias**: RF-010 (Gestión de Datos Generales de Cotización en Backend)
**Datos de Entrada**: Nombre de suscriptor, ID de agente, Tipo de giro, Fecha de inicio/fin de vigencia, etc.
**Datos de Salida**: Datos generales de la cotización guardados/actualizados

### RF-003: Consulta de Catálogos de Referencia
**Descripción**: El sistema debe permitir al usuario consultar y seleccionar valores de catálogos de referencia, como suscriptores, agentes, giros y códigos postales, para completar los datos de la cotización y las ubicaciones. La interacción con catálogos extensos se realizará mediante un modal de búsqueda con paginación y filtros, complementado con autocompletado para selección rápida, y dropdowns para catálogos pequeños.
**Objetivo**: Asegurar la consistencia y validez de los datos ingresados utilizando valores predefinidos.
**Criterios de Aceptación**:
- Dado que el usuario está capturando datos generales, cuando interactúa con el campo "Suscriptor", entonces el sistema muestra una lista de suscriptores disponibles para selección.
- Dado que el usuario ingresa un código postal en una ubicación, cuando el sistema lo valida, entonces muestra la información asociada al CP (ej. colonia, municipio, estado, zona) o un mensaje de error si no es válido. Si la información no es completa, se mostrará la disponible y se permitirá al usuario completar manualmente los campos faltantes, con una advertencia visual.
**Prioridad**: Media
**Dependencias**: RF-017, RF-018 (Provisión de Catálogos en Servicio de Referencia)
**Datos de Entrada**: Criterios de búsqueda para catálogos (ej. nombre de suscriptor, código postal)
**Datos de Salida**: Listas de suscriptores, agentes, giros; datos de código postal

### RF-004: Gestión de Ubicaciones de Riesgo
**Descripción**: El sistema debe permitir al usuario registrar, consultar y editar una o varias ubicaciones de riesgo dentro de una cotización, incluyendo sus configuraciones y datos específicos (ej. dirección, valores asegurados, clasificación de riesgo, garantías). Las ubicaciones no podrán ser eliminadas una vez creadas, solo editadas o marcadas como inactivas.
**Objetivo**: Detallar los diferentes puntos geográficos y sus características para la evaluación del riesgo y el cálculo de la prima.
**Criterios de Aceptación**:
- Dado que un folio está abierto, cuando el usuario selecciona "Agregar Ubicación", entonces el sistema presenta un formulario para capturar los detalles de una nueva ubicación.
- Dado que el usuario ha capturado los datos de una ubicación y los guarda, entonces la ubicación se registra y se muestra en una lista dentro del folio.
- Dado que una ubicación está registrada, cuando el usuario la selecciona para editar, entonces el sistema carga sus datos en el formulario para modificación.
- Dado que una ubicación ha sido modificada y guardada, entonces el sistema actualiza sus datos asociados al folio.
- Dado que una ubicación tiene datos incompletos, cuando el usuario intenta guardar, entonces el sistema muestra alertas indicando los campos faltantes o inválidos mediante validación en línea por campo y un mensaje de resumen en la parte superior del formulario.
- Dado que el usuario intenta guardar una ubicación, cuando los campos 'dirección completa', 'código postal', 'valores asegurados', 'clasificación de riesgo' y 'garantías' están vacíos o incompletos, entonces el sistema muestra una alerta de campos obligatorios.**Prioridad**: Alta
**Dependencias**: RF-011 (Gestión de Ubicaciones de Riesgo en Backend), RF-015 (Aplicación de Reglas de Negocio)
**Datos de Entrada**: Dirección, Código Postal, Valores asegurados, Clasificación de riesgo, Garantías, etc.
**Datos de Salida**: Ubicaciones de riesgo registradas, actualizadas o eliminadas

### RF-005: Visualización del Progreso y Estado del Folio
**Descripción**: El sistema debe mostrar al usuario el estado actual del folio de cotización y el progreso de la captura de datos, incluyendo alertas si hay ubicaciones incompletas o datos pendientes. Se definirán estados claros (ej. Borrador, Datos Incompletos, Listo para Cálculo, Calculado, Error de Cálculo) con indicadores visuales distintivos en el frontend.
**Objetivo**: Guiar al usuario a través del proceso de cotización y asegurar la integridad de los datos.
**Criterios de Aceptación**:
- Dado que el usuario está en la vista del folio, cuando una o más ubicaciones tienen datos incompletos, entonces el sistema muestra un icono de advertencia claramente visible en cada ubicación con problemas y un resumen de alertas en el encabezado del folio, indicando un estado de "Datos Incompletos".
- Dado que todos los datos requeridos para una ubicación están completos, entonces el sistema indica que la ubicación está lista para el cálculo, contribuyendo a un estado de "Listo para Cálculo" del folio.
- Dado que el cálculo de prima ha sido ejecutado, entonces el sistema actualiza el estado del folio para reflejar que los resultados están disponibles, indicando un estado de "Calculado" o "Error de Cálculo".
**Prioridad**: Media
**Dependencias**: RF-012 (Consulta de Estado y Coberturas en Backend)
**Datos de Entrada**: Ninguna (derivado del estado interno del folio)
**Datos de Salida**: Indicadores visuales de estado y alertas

### RF-006: Configuración de Opciones de Cobertura
**Descripción**: El sistema debe permitir al usuario seleccionar y configurar las opciones de cobertura deseadas para la cotización, que pueden influir en el cálculo de la prima. Se presentará una lista fija de coberturas para todas las cotizaciones, sin variación.
**Objetivo**: Adaptar la cotización a las necesidades específicas del cliente.
**Criterios de Aceptación**:
- Dado que un folio está abierto, cuando el usuario accede a la sección de coberturas, entonces el sistema presenta una lista de checkboxes o toggles, con descripciones claras y, si aplica, campos para configurar valores (ej. deducibles, sumas aseguradas).
- Dado que el usuario selecciona o deselecciona una cobertura, entonces el sistema actualiza la configuración de coberturas del folio.
**Prioridad**: Media
**Dependencias**: RF-012 (Consulta de Estado y Coberturas en Backend)
**Datos de Entrada**: Selección de coberturas
**Datos de Salida**: Configuración de coberturas del folio

### RF-007: Ejecución del Cálculo de la Prima
**Descripción**: El sistema debe permitir al usuario iniciar el proceso de cálculo de la prima neta y comercial para el folio completo, incluyendo el desglose por ubicación.
**Objetivo**: Generar los resultados financieros de la cotización basándose en los datos capturados y las reglas de negocio.
**Criterios de Aceptación**:
- Dado que todos los campos obligatorios definidos para los datos generales del folio y cada ubicación están llenos y pasan las validaciones de negocio, cuando el usuario selecciona "Calcular Prima", entonces el sistema inicia el proceso de cálculo.
- Dado que el cálculo está en progreso, entonces el sistema muestra un spinner o barra de progreso con el mensaje 'Calculando prima...'
- Dado que el cálculo se ha completado exitosamente, entonces el sistema indica que los resultados están disponibles para visualización y muestra un mensaje de éxito.
- Dado que faltan datos o hay errores de validación, cuando el usuario intenta calcular la prima, entonces el sistema muestra un mensaje de error y no procede con el cálculo.
**Prioridad**: Alta
**Dependencias**: RF-013 (Ejecución y Persistencia del Cálculo de Prima en Backend), RF-005 (Visualización del Progreso)
**Datos de Entrada**: Solicitud de cálculo de prima
**Datos de Salida**: Indicador de cálculo completado/fallido

### RF-008: Visualización de Resultados Financieros
**Descripción**: El sistema debe mostrar al usuario los resultados detallados del cálculo de la prima, incluyendo la prima neta, prima comercial y el desglose de primas por cada ubicación de riesgo. Se presentará una tabla interactiva como vista principal, mostrando prima neta, prima comercial y desglose por ubicación.
**Objetivo**: Presentar de manera clara y comprensible el costo del seguro.
**Criterios de Aceptación**:
- Dado que el cálculo de la prima se ha completado, cuando el usuario accede a la sección de resultados, entonces el sistema muestra la prima neta total, la prima comercial total y la prima calculada para cada ubicación en una tabla interactiva.
- Dado que se han aplicado diferentes factores o tarifas, entonces el sistema permite expandir la tabla para mostrar un desglose detallado que incluya la prima neta, recargos, impuestos y cualquier otro factor que contribuya a la prima comercial total y por ubicación.
- Dado que el usuario desea un formato externo, entonces el sistema permite la exportación de los resultados a PDF y Excel.
- Dado que se desea una interpretación rápida, entonces el sistema puede incluir indicadores visuales opcionales (ej. gráficos simples) sin reemplazar la tabla como fuente principal de información.
**Prioridad**: Alta
**Dependencias**: RF-013 (Ejecución y Persistencia del Cálculo de Prima en Backend)
**Datos de Entrada**: Ninguna (derivado del resultado del cálculo)
**Datos de Salida**: Prima neta, Prima comercial, Desglose de primas por ubicación

### RF-009: Autenticación de Usuarios
**Descripción**: El sistema debe permitir a los usuarios (Asegurado, Agente de Seguros) autenticarse mediante credenciales (usuario/contraseña) y gestionar su sesión para acceder a las funcionalidades del cotizador.
**Objetivo**: Garantizar el acceso seguro y controlado a la aplicación.
**Criterios de Aceptación**:
- Dado que un usuario no autenticado intenta acceder a una funcionalidad protegida, cuando ingresa sus credenciales válidas, entonces el sistema lo autentica y le permite el acceso.
- Dado que un usuario ha iniciado sesión, cuando su sesión expira o cierra sesión, entonces el sistema invalida su acceso y requiere una nueva autenticación.
**Prioridad**: Alta
**Dependencias**: `plataformas-danos-back` (para validación de credenciales y gestión de tokens)
**Datos de Entrada**: Usuario, Contraseña
**Datos de Salida**: Token de sesión (JWT), Estado de autenticación

### RF-010: Manejo y Visualización de Errores Genéricos
**Descripción**: El sistema debe centralizar la visualización de errores genéricos (ej. errores de red, errores del servidor) que son respondidos por el backend, mostrando mensajes claros y amigables al usuario.
**Objetivo**: Mejorar la experiencia del usuario al proporcionar retroalimentación clara sobre problemas técnicos.
**Criterios de Aceptación**:
- Dado que se produce un error en el backend (ej. 500 Internal Server Error, 404 Not Found), cuando el frontend recibe la respuesta de error, entonces muestra un mensaje genérico adecuado al usuario sin exponer detalles técnicos sensibles.
- Dado que la conexión con el backend falla (ej. error de red), cuando el frontend intenta realizar una operación, entonces muestra una alerta indicando problemas de conectividad.
**Prioridad**: Media
**Dependencias**: `plataformas-danos-back` (para estandarización de respuestas de error)
**Datos de Entrada**: Respuestas de error del backend
**Datos de Salida**: Alertas/mensajes de error para el usuario

### RF-011: Gestión de Roles de Usuario y Acceso a Funcionalidades
**Descripción**: El sistema debe implementar un sistema básico de roles para diferenciar las funcionalidades accesibles por los Asegurados y los Agentes de Seguros, mostrando solo las opciones relevantes para cada tipo de usuario.**Objetivo**: Asegurar que los usuarios solo accedan a las funcionalidades para las que tienen permiso.
**Criterios de Aceptación**:
- Dado que un usuario con rol 'Asegurado' ha iniciado sesión, cuando navega por la aplicación, entonces solo tiene acceso a las funcionalidades permitidas para asegurados.
- Dado que un usuario con rol 'Agente de Seguros' ha iniciado sesión, cuando navega por la aplicación, entonces tiene acceso a las funcionalidades permitidas para agentes, que pueden incluir opciones adicionales no disponibles para asegurados.
**Prioridad**: Alta
**Dependencias**: RF-009 (Autenticación de Usuarios)
**Datos de Entrada**: Rol de usuario (obtenido tras autenticación)
**Datos de Salida**: Interfaz de usuario adaptada al rol, Restricciones de acceso a funcionalidades

### RF-012: Persistencia Local de Datos en Progreso
**Descripción**: El sistema debe implementar mecanismos para proteger los datos en progreso del usuario en el frontend, incluyendo guardado automático local, advertencias al salir y recuperación de sesión, para evitar la pérdida de información debido a cierres accidentales o interrupciones inesperadas.
**Objetivo**: Prevenir la pérdida de información no guardada oficialmente y mejorar la resiliencia de la interfaz de usuario.
**Criterios de Aceptación**:
- Dado que el usuario está capturando datos en un folio, cuando realiza cambios, entonces el sistema guarda automáticamente una copia de los datos en progreso en el almacenamiento local del navegador, con versionado básico por folio, sin reemplazar la persistencia oficial en el backend.
- Dado que el usuario intenta salir de la aplicación o cerrar la pestaña con cambios no guardados en el backend pero presentes en el almacenamiento local, entonces el sistema muestra una advertencia clara pidiendo confirmación para salir sin guardar y ofreciendo la opción de mantener la sesión activa.
- Dado que un usuario reingresa a la aplicación después de una sesión previa con datos en progreso no guardados oficialmente, entonces el sistema detecta la existencia de datos locales y ofrece la opción de restaurar el estado previo del formulario desde el almacenamiento local.
**Prioridad**: Media
**Dependencias**: Ninguna
**Datos de Entrada**: Cambios del usuario en el formulario
**Datos de Salida**: Datos guardados en local storage, Advertencias, Opciones de restauración

## Requerimientos Funcionales - Módulo: plataformas-danos-back (Backend Principal)

### RF-009: Creación Idempotente de Folios
**Descripción**: El sistema debe permitir la creación de nuevos folios de cotización, asegurando que la operación sea idempotente, es decir, que múltiples solicitudes con los mismos parámetros de creación resulten en la creación de un único folio.**Objetivo**: Garantizar la unicidad de los folios y prevenir duplicados accidentales.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para crear un folio, cuando el folio no existe, entonces el sistema crea un nuevo folio y devuelve su ID.
- Dado que se recibe una solicitud para crear un folio, cuando ya existe un folio asociado a la misma solicitud, entonces el sistema devuelve el ID del folio existente sin crear uno nuevo.
**Prioridad**: Alta
**Dependencias**: RF-019 (Generación Secuencial de Folios en Plataforma-core-ohs)
**Datos de Entrada**: Solicitud de creación de folio
**Datos de Salida**: ID del folio creado/existente

### RF-010: Gestión de Datos Generales de Cotización (Backend)
**Descripción**: El sistema debe gestionar la persistencia (guardar, consultar, editar) de los datos generales de una cotización, incluyendo la configuración de layouts y metadatos de actualización.
**Objetivo**: Almacenar y recuperar la información básica de la cotización de forma estructurada.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para guardar/actualizar datos generales de un folio, cuando los datos son válidos, entonces el sistema persiste la información y actualiza la fecha de última actualización y la versión.
- Dado que se recibe una solicitud de consulta para un folio, entonces el sistema devuelve los datos generales de la cotización asociados a ese folio.
- Dado que se intenta actualizar un folio con una versión desactualizada, entonces el sistema rechaza la operación debido a un conflicto de versionado optimista.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: ID de folio, Datos generales de cotización (suscriptor, agente, giro, etc.), Versión actual
**Datos de Salida**: Confirmación de guardado/actualización, Datos generales de cotización

### RF-011: Gestión de Ubicaciones de Riesgo (Backend)
**Descripción**: El sistema debe permitir el registro, consulta, edición y resumen de múltiples ubicaciones de riesgo asociadas a un folio de cotización, manteniendo la integridad de los datos. No se permitirá la eliminación de ubicaciones, solo su edición o marcaje como inactivas.
**Objetivo**: Administrar de manera robusta la información detallada de cada ubicación de riesgo.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para agregar una nueva ubicación a un folio, cuando los datos de la ubicación son válidos, entonces el sistema la persiste y la asocia al folio.
- Dado que se recibe una solicitud para consultar las ubicaciones de un folio, entonces el sistema devuelve la lista de todas las ubicaciones registradas para ese folio.
- Dado que se recibe una solicitud para editar una ubicación existente, cuando los datos son válidos y la versión coincide, entonces el sistema actualiza la ubicación y sus metadatos.
- Dado que se recibe una solicitud para resumir las ubicaciones de un folio, entonces el sistema devuelve un listado conciso de las ubicaciones con sus identificadores clave.
**Prioridad**: Alta
**Dependencias**: RF-015 (Aplicación de Reglas de Negocio)
**Datos de Entrada**: ID de folio, Datos de ubicación (dirección, valores, clasificación, garantías), ID de ubicación, Versión
**Datos de Salida**: Confirmación de operación, Lista de ubicaciones, Ubicación específica

### RF-012: Consulta de Estado y Opciones de Cobertura (Backend)
**Descripción**: El sistema debe proporcionar la capacidad de consultar el estado actual de una cotización (ej. Borrador, Datos Incompletos, Listo para Cálculo, Calculado, Error de Cálculo) y las opciones de cobertura seleccionadas para un folio.
**Objetivo**: Soportar la interfaz de usuario en la visualización del progreso y la configuración de la cotización.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para consultar el estado de un folio, entonces el sistema evalúa la completitud de los datos y devuelve un indicador de estado (Borrador, Datos Incompletos, Listo para Cálculo, Calculado, Error de Cálculo).
- Dado que se recibe una solicitud para consultar las opciones de cobertura de un folio, entonces el sistema devuelve la configuración de coberturas actualmente seleccionada.
**Prioridad**: Media
**Dependencias**: Ninguna
**Datos de Entrada**: ID de folio
**Datos de Salida**: Estado del folio, Opciones de cobertura seleccionadas

### RF-013: Ejecución y Persistencia del Cálculo de Prima
**Descripción**: El sistema debe ejecutar la lógica de negocio para calcular la prima neta, la prima comercial y el desglose de primas por ubicación, y persistir estos resultados financieros dentro del folio de cotización.
**Objetivo**: Generar y almacenar los resultados monetarios de la cotización.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para calcular la prima de un folio con datos completos, cuando la lógica de cálculo se ejecuta, entonces el sistema calcula la prima neta, prima comercial y primas por ubicación.
- Dado que el cálculo se ha completado, entonces el sistema persiste los resultados financieros en el folio de cotización.
- Dado que la lógica de cálculo requiere parámetros externos (ej. tarifas, factores), cuando se ejecuta el cálculo, entonces el sistema consulta el servicio `Plataforma-core-ohs` para obtenerlos.
**Prioridad**: Alta
**Dependencias**: RF-015 (Aplicación de Reglas de Negocio), RF-021 (Consulta de Tarifas y Factores Técnicos)
**Datos de Entrada**: ID de folio, Datos de ubicaciones, Opciones de cobertura
**Datos de Salida**: Prima neta, Prima comercial, Desglose de primas por ubicación, Estado de cálculo

### RF-014: Manejo de Versionado Optimista
**Descripción**: El sistema debe implementar un mecanismo de versionado optimista para todas las operaciones de edición sobre los datos principales de la cotización (folio y ubicaciones), previniendo conflictos de concurrencia.
**Objetivo**: Asegurar la integridad de los datos frente a modificaciones simultáneas.
**Criterios de Aceptación**:
- Dado que un usuario intenta actualizar un recurso (folio o ubicación) con una versión desactualizada, entonces el sistema rechaza la operación y devuelve un error de conflicto.
- Dado que un usuario actualiza un recurso con la versión correcta, entonces el sistema incrementa la versión del recurso y actualiza la fecha de última modificación.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: Recurso a actualizar (folio/ubicación), Versión esperada
**Datos de Salida**: Recurso actualizado con nueva versión, Error de conflicto

### RF-015: Aplicación de Reglas de Negocio para Cálculo y Validación
**Descripción**: El sistema debe aplicar reglas de negocio predefinidas para la validación de los datos de las ubicaciones y para la ejecución de la lógica de cálculo de la prima.
**Objetivo**: Asegurar la validez de los datos y la exactitud de los cálculos según las políticas de negocio.
**Criterios de Aceptación**:
- Dado que se ingresan datos de ubicación, cuando se guardan, entonces el sistema valida los datos contra las reglas de negocio (ej. rangos de valores, formatos) y devuelve errores si no cumplen.
- Dado que se ejecuta el cálculo de la prima, entonces el sistema aplica las reglas de negocio para determinar los factores, tasas y cuotas relevantes.
- Dado que una regla de negocio requiere un dato externo (ej. zona de CP, clasificación de riesgo), entonces el sistema consulta el servicio `Plataforma-core-ohs` para obtenerlo.
**Prioridad**: Alta
**Dependencias**: RF-018, RF-020, RF-021 (Catálogos y Tarifas de Plataforma-core-ohs)
**Datos de Entrada**: Datos de ubicación, Parámetros de cálculo
**Datos de Salida**: Resultados de validación (errores/éxito), Factores/tasas aplicados en cálculo

### RF-016: Integración con Servicio de Referencia `Plataforma-core-ohs`
**Descripción**: El sistema debe consumir o simular los servicios expuestos por `Plataforma-core-ohs` para obtener catálogos (suscriptores, agentes, giros, CP, clasificación de riesgo, garantías) y tarifas necesarias para la cotización y el cálculo.
**Objetivo**: Centralizar la gestión de datos maestros y tarifas en un servicio externo y utilizarlos en el proceso de cotización.
**Criterios de Aceptación**:
- Dado que se requiere un catálogo (ej. suscriptores), cuando el backend lo solicita, entonces el servicio `Plataforma-core-ohs` devuelve la lista de elementos del catálogo.
- Dado que se requiere la validación o información de un código postal, cuando el backend lo solicita, entonces el servicio `Plataforma-core-ohs` devuelve los datos asociados al CP.
- Dado que se requieren tarifas o factores técnicos para el cálculo, cuando el backend los solicita, entonces el servicio `Plataforma-core-ohs` devuelve los datos tarifarios correspondientes.
**Prioridad**: Alta
**Dependencias**: RF-017, RF-018, RF-019, RF-020, RF-021 (Funcionalidades de Plataforma-core-ohs)
**Datos de Entrada**: Solicitudes de catálogos, códigos postales, tarifas
**Datos de Salida**: Datos de catálogos, información de CP, tarifas y factores

## Requerimientos Funcionales - Módulo: Plataforma-core-ohs (Servicio de Referencia)

### RF-017: Provisión de Catálogo de Suscriptores, Agentes y Giros
**Descripción**: El servicio debe proporcionar una API para consultar los catálogos de suscriptores, agentes y giros, permitiendo la búsqueda y recuperación de sus datos.
**Objetivo**: Centralizar y distribuir la información de entidades clave para el negocio.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de catálogo de suscriptores, entonces el servicio devuelve una lista de suscriptores con sus identificadores y nombres.
- Dado que se recibe una solicitud de catálogo de agentes, entonces el servicio devuelve una lista de agentes con sus identificadores y nombres.
- Dado que se recibe una solicitud de catálogo de giros, entonces el servicio devuelve una lista de giros con sus identificadores y descripciones.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: Criterios de búsqueda (opcional), Tipo de catálogo
**Datos de Salida**: Lista de elementos del catálogo (suscriptores, agentes, giros)

### RF-018: Validación y Consulta de Códigos Postales
**Descripción**: El servicio debe proporcionar una API para validar códigos postales y consultar la información asociada a ellos, como zona CAT y nivel técnico.
**Objetivo**: Asegurar la correcta ubicación geográfica y obtener datos relevantes para la clasificación de riesgo.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de validación para un código postal, cuando el CP es válido y existe, entonces el servicio devuelve la información geográfica asociada (ej. ciudad, estado, zona CAT).
- Dado que se recibe una solicitud de validación para un código postal, cuando el CP no es válido o no existe, entonces el servicio devuelve un indicador de error.
**Prioridad**: Alta
**Dependencias**: Base de datos `catalogo_cp_zonas`
**Datos de Entrada**: Código postal
**Datos de Salida**: Información geográfica (zona CAT, nivel técnico, etc.) o error

### RF-019: Generación Secuencial de Folios
**Descripción**: El servicio debe proporcionar una API para generar folios secuenciales únicos para las cotizaciones.
**Objetivo**: Asegurar la asignación de identificadores únicos y consecutivos para cada nueva cotización.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud para generar un nuevo folio, entonces el servicio genera y devuelve un identificador de folio único y secuencial.
**Prioridad**: Alta
**Dependencias**: Ninguna
**Datos de Entrada**: Solicitud de generación de folio
**Datos de Salida**: Nuevo número de folio

### RF-020: Suministro de Catálogos de Clasificación de Riesgo y Garantías
**Descripción**: El servicio debe proporcionar una API para consultar los catálogos de clasificación de riesgo y garantías disponibles.
**Objetivo**: Ofrecer opciones estandarizadas para definir el perfil de riesgo y las protecciones adicionales en las ubicaciones.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de catálogo de clasificación de riesgo, entonces el servicio devuelve una lista de clasificaciones de riesgo.
- Dado que se recibe una solicitud de catálogo de garantías, entonces el servicio devuelve una lista de garantías disponibles.
**Prioridad**: Media
**Dependencias**: Ninguna
**Datos de Entrada**: Tipo de catálogo (riesgo/garantías)
**Datos de Salida**: Lista de clasificaciones de riesgo, Lista de garantías

### RF-021: Consulta de Tarifas y Factores Técnicos
**Descripción**: El servicio debe proporcionar una API para consultar las tarifas base y los factores técnicos (ej. factores CAT, cuotas FHM, factores de equipo electrónico) necesarios para el cálculo de primas.
**Objetivo**: Centralizar la gestión y distribución de los datos tarifarios y actuariales.
**Criterios de Aceptación**:
- Dado que se recibe una solicitud de tarifas para un tipo de seguro (ej. incendio), entonces el servicio devuelve las tasas base y metadatos técnicos correspondientes.
- Dado que se recibe una solicitud de factores CAT para una zona específica, entonces el servicio devuelve los factores CAT aplicables.
- Dado que se recibe una solicitud de cuotas FHM para un grupo, zona y condición, entonces el servicio devuelve las cuotas FHM.
- Dado que se recibe una solicitud de factores de equipo electrónico para una clase y nivel de zona, entonces el servicio devuelve los factores técnicos.
**Prioridad**: Alta
**Dependencias**: Bases de datos `tarifas_incendio`, `tarifas_cat`, `tarifa_fhm`, `factores_equipo_electronico`
**Datos de Entrada**: Tipo de tarifa/factor, Parámetros de búsqueda (ej. zona, grupo, clase)
**Datos de Salida**: Tarifas y factores técnicos aplicables