# Historias de Usuario

## FT-001: Creación y Edición de Datos Generales de la Cotización

### HU-001: Iniciar nueva cotización con folio automático

Como usuario, quiero iniciar una nueva cotización, para que el sistema me asigne un folio único de forma idempotente.

**Criterios de Aceptación**:
- Dado que estoy en la pantalla principal del cotizador, cuando selecciono la opción para crear una nueva cotización, entonces se genera un nuevo folio de cotización.
- Dado que se genera un folio para la cotización, cuando se produce un error temporal y se reintenta la creación, entonces se mantiene el mismo folio generado inicialmente.
- Dado que se ha iniciado una nueva cotización, cuando se carga la interfaz, entonces el campo "Folio" se muestra prellenado y no editable.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-007: Integración con Servicio de Folios

**Componentes Técnicos**:
- Frontend: Botón "Nueva Cotización", Campo de visualización de Folio.
- Backend: API de cotizaciones (endpoint de creación), Servicio de Folios (`Plataforma-core-ohs`).

**Notas de Implementación**:
- La lógica de generación de folios debe ser robusta para garantizar unicidad e idempotencia.
- Considerar el manejo de errores si el servicio de folios no está disponible.

**Estado**: Backlog

---
### HU-002: Cargar y editar cotización existente por folio

Como usuario, quiero buscar y cargar una cotización existente por su folio, para poder continuar con su edición o revisión.

**Criterios de Aceptación**:
- Dado que conozco el folio de una cotización, cuando lo ingreso en el campo de búsqueda de cotizaciones, entonces el sistema carga los datos generales de la cotización.
- Dado que ingreso un folio inexistente, cuando intento cargar la cotización, entonces el sistema muestra un mensaje de error indicando que no se encontró la cotización.
- Dado que la cotización se ha cargado, cuando modifico cualquiera de sus datos generales, entonces puedo guardarlos.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- Ninguna

**Componentes Técnicos**:
- Frontend: Campo de búsqueda de folio, Botón "Cargar".
- Backend: API de cotizaciones (endpoint de consulta y actualización).

**Notas de Implementación**:
- La búsqueda debe ser eficiente y tolerante a errores de entrada parcial (opcionalmente).
- Se debe asegurar que solo los usuarios autorizados puedan cargar y editar cotizaciones.

**Estado**: Backlog

---
### HU-003: Capturar y validar datos generales del asegurado

Como usuario, quiero introducir el nombre y RFC del asegurado, para identificar correctamente al cliente en la cotización.

**Criterios de Aceptación**:
- Dado que estoy en la sección de datos generales, cuando introduzco un nombre válido en el campo "Nombre del Asegurado", entonces el campo acepta el valor.
- Dado que estoy en la sección de datos generales, cuando introduzco un RFC válido, entonces el campo "RFC" acepta el valor y se guarda correctamente.
- Dado que introduzco un RFC con formato inválido, cuando intento guardar la cotización, entonces el sistema muestra un mensaje de error de validación.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- Ninguna

**Componentes Técnicos**:
- Frontend: Campos de texto "Nombre Asegurado", "RFC".
- Backend: API de cotizaciones (validación y persistencia de datos generales).

**Notas de Implementación**:
- Implementar validaciones de formato de RFC tanto en frontend como en backend.
- Considerar la longitud máxima para el nombre del asegurado.

**Estado**: Backlog

---
### HU-004: Seleccionar tipo de seguro, moneda y canal de venta de catálogos

Como usuario, quiero seleccionar el tipo de seguro, la moneda y el canal de venta de listas predefinidas, para estandarizar la información de la cotización.

**Criterios de Aceptación**:
- Dado que estoy en la sección de datos generales, cuando hago clic en el campo "Tipo de Seguro", entonces se muestra una lista de opciones válidas obtenidas de un catálogo.
- Dado que selecciono una opción de "Tipo de Seguro", "Moneda" o "Canal de Venta", entonces el valor se guarda correctamente con la cotización.
- Dado que no selecciono una opción para un campo obligatorio, cuando intento guardar, entonces veo un mensaje de validación.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-007: Integración con Servicios de Referencia (Catálogos)

**Componentes Técnicos**:
- Frontend: Dropdowns/selectores para Tipo de Seguro, Moneda, Canal de Venta.
- Backend: API de cotizaciones (persistencia), Integración con `Plataforma-core-ohs` para catálogos.

**Notas de Implementación**:
- La carga de catálogos debe ser asíncrona y manejar estados de carga/error.
- Asegurar que los catálogos sean configurables y actualizables.

**Estado**: Backlog

---
### HU-005: Establecer y validar vigencia de la cotización

Como usuario, quiero definir las fechas de inicio y fin de la vigencia de la cotización, para especificar el período de cobertura.

**Criterios de Aceptación**:
- Dado que estoy en la sección de datos generales, cuando introduzco las fechas de inicio y fin de vigencia, entonces el sistema las guarda.
- Dado que la fecha de fin es anterior a la fecha de inicio, cuando intento guardar, entonces el sistema muestra un mensaje de error de validación.
- Dado que introduzco fechas con formato inválido, cuando intento guardar, entonces el sistema muestra un mensaje de error.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Selectores de fecha para vigencia.
- Backend: API de cotizaciones (validación y persistencia de fechas).

**Notas de Implementación**:
- La validación de fechas debe considerar rangos lógicos y formatos.
- Las fechas deben ser almacenadas de forma consistente (e.g., ISO 8601).

**Estado**: Backlog

---
## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

### HU-006: Agregar una nueva ubicación de riesgo a la cotizaciónComo usuario, quiero agregar una nueva ubicación de riesgo a la cotización, para detallar los diferentes lugares a asegurar.

**Criterios de Aceptación**:
- Dado que estoy editando una cotización, cuando hago clic en "Agregar Ubicación", entonces se crea una nueva sección o pestaña para una ubicación.
- Dado que he agregado una ubicación, cuando la guardo, entonces se asocia correctamente a la cotización padre.
- Dado que se ha alcanzado el límite de 10 ubicaciones, cuando intento agregar otra, entonces el sistema me notifica que no puedo añadir más.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-001: Iniciar nueva cotización con folio automático

**Componentes Técnicos**:
- Frontend: Botón "Agregar Ubicación", Interfaz de gestión de ubicaciones (maestro-detalle/pestañas).
- Backend: API de cotizaciones (endpoint de adición de ubicaciones).

**Notas de Implementación**:
- La interfaz debe permitir una navegación clara entre las ubicaciones.
- Se debe manejar el límite de ubicaciones configurado (e.g., 10).

**Estado**: Backlog

---
### HU-007: Editar detalles específicos de una ubicación de riesgo

**Descripción**:
Como usuario,
Quiero modificar los datos de una ubicación de riesgo existente,
Para corregir o actualizar su información con todos los campos del dominio requeridos.

**Criterios de Aceptación**:
- Dado que he seleccionado una ubicación, cuando edito sus campos, entonces el formulario expone todos los campos del dominio: `nombreUbicacion`, `direccion`, `codigoPostal`, `estado`, `municipio`, `colonia`, `ciudad`, `tipoConstructivo`, `nivel`, `anioConstruccion`, `giro` (con `giro.claveIncendio`), `garantías[]` y `zonaCatastrofica`.
- Dado que modifico los datos de una ubicación y guardo, entonces los cambios se persisten correctamente a través del endpoint `PATCH /v1/quotes/{folio}/locations/{índice}`.
- Dado que un campo obligatorio de la ubicación se deja vacío, cuando intento guardar, entonces el sistema muestra un mensaje de validación indicando el campo específico.
- Dado que los datos de la ubicación se han guardado, cuando consulto la cotización, entonces los datos actualizados se muestran y el campo `estadoValidacion` refleja si la ubicación está `COMPLETA` o `INCOMPLETA`.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-006: Agregar una nueva ubicación de riesgo a la cotización

**Componentes Técnicos**:
- Frontend: Formulario de detalles de ubicación con todos los campos del dominio.
- Backend: API de cotizaciones (endpoint PATCH de actualización de ubicaciones).

**Notas de Implementación**:
- La actualización parcial de ubicaciones debe ser posible sin afectar otras ubicaciones o datos generales.
- Los campos `alertasBloqueantes` y `estadoValidacion` se recalculan automáticamente tras cada guardado.
- El campo `zonaCatastrofica` se obtiene automáticamente al validar el `codigoPostal` contra el catálogo de CP.

**Estado**: Backlog

---

### HU-008: Marcar una ubicación de riesgo como inactiva

**Descripción**:
Como usuario,
Quiero marcar una ubicación de riesgo como inactiva cuando ya no sea necesaria,
Para mantener la cotización organizada sin perder el historial de la ubicación.

**Criterios de Aceptación**:
- Dado que tengo múltiples ubicaciones en una cotización, cuando selecciono una ubicación y confirmo marcarla como inactiva, entonces su `estadoValidacion` cambia a `INACTIVA` y deja de aparecer en el flujo activo de la cotización.
- Dado que una ubicación está marcada como `INACTIVA`, cuando se ejecuta el cálculo de prima, entonces esa ubicación se excluye del proceso de cálculo sin generar errores.
- Dado que se marca una ubicación como inactiva, cuando consulto la cotización, entonces la ubicación permanece en el histórico pero con indicador visual de inactiva.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-006: Agregar una nueva ubicación de riesgo a la cotización

**Componentes Técnicos**:
- Frontend: Opción "Marcar como Inactiva" con diálogo de confirmación.
- Backend: API de cotizaciones (PATCH de ubicación con cambio de `estadoValidacion`).

**Notas de Implementación**:
- Las ubicaciones **nunca se eliminan físicamente** del documento de cotización, conforme al requisito del reto técnico.
- Implementar confirmación antes de marcar como inactiva para evitar acciones accidentales.

**Estado**: Backlog

---
### HU-009: Consultar y validar código postal de ubicación

Como usuario, quiero introducir el código postal de una ubicación y que se valide contra un catálogo, para asegurar la exactitud de la dirección.

**Criterios de Aceptación**:
- Dado que introduzco un código postal en el campo de la ubicación, cuando el sistema lo valida, entonces se verifica contra el catálogo de CP.
- Dado que introduzco un código postal válido, cuando el sistema lo valida, entonces se autocompletan o sugieren datos relacionados (e.g., estado, ciudad, colonia).
- Dado que introduzco un código postal inválido o inexistente, cuando el sistema lo valida, entonces se muestra un mensaje de error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-007: Integración con Servicios de Referencia (Catálogos de CP)
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Campo de texto para Código Postal, Sugerencias/Autocompletado.
- Backend: API de cotizaciones (validación de CP), Integración con `Plataforma-core-ohs` (catálogo de CP).

**Notas de Implementación**:
- La integración con el servicio de CP debe ser eficiente para no ralentizar la captura.
- Se debe manejar la latencia del servicio externo y mostrar un indicador de carga.

**Estado**: Backlog

---
### HU-010: Visualizar alertas por datos incompletos en ubicaciones

Como usuario, quiero ver alertas visuales si una ubicación tiene datos incompletos o inválidos, para saber qué información necesita ser corregida.

**Criterios de Aceptación**:
- Dado que una ubicación tiene campos obligatorios sin completar, cuando estoy en la vista de ubicaciones, entonces se muestra un indicador visual (e.g., icono, color) en la pestaña o sección de esa ubicación.
- Dado que una ubicación tiene errores de validación, cuando intento guardar la cotización, entonces se me redirige o se resalta la ubicación con errores.
- Dado que completo todos los datos requeridos y válidos de una ubicación, cuando la guardo, entonces el indicador de alerta desaparece.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Lógica de visualización de alertas, Componentes UI para indicadores.
- Backend: API de cotizaciones (respuestas de validación).

**Notas de Implementación**:
- La retroalimentación visual debe ser clara y no intrusiva.
- Las validaciones deben ser ejecutadas en tiempo real o al intentar guardar.

**Estado**: Backlog

---
## FT-003: Configuración y Selección de Coberturas por Ubicación

### HU-011: Visualizar catálogo de coberturas disponibles por tipo de seguro

Como usuario, quiero ver un catálogo de coberturas disponibles para el tipo de seguro seleccionado, para elegir las protecciones adecuadas.

**Criterios de Aceptación**:
- Dado que he seleccionado un tipo de seguro para la cotización, cuando accedo a la sección de coberturas de una ubicación, entonces se muestra la lista de coberturas aplicables a ese tipo de seguro.
- Dado que cambio el tipo de seguro de la cotización, cuando vuelvo a la sección de coberturas, entonces la lista de coberturas se actualiza según el nuevo tipo de seguro.
- Dado que no hay coberturas disponibles para un tipo de seguro, cuando accedo a la sección, entonces se muestra un mensaje informativo.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-004: Seleccionar tipo de seguro, moneda y canal de venta de catálogos
- FT-007: Integración con Servicios de Referencia (Catálogos de Coberturas)

**Componentes Técnicos**:
- Frontend: Interfaz de listado de coberturas.
- Backend: API de cotizaciones (consulta de coberturas por tipo de seguro), Integración con `Plataforma-core-ohs` (catálogo de coberturas).

**Notas de Implementación**:
- La carga de coberturas debe ser dinámica y filtrarse según el tipo de seguro de la cotización.
- Se debe permitir la paginación o búsqueda si el catálogo es muy extenso.

**Estado**: Backlog

---
### HU-012: Seleccionar y deseleccionar coberturas para una ubicación

Como usuario, quiero poder seleccionar o deseleccionar coberturas individuales para cada ubicación de riesgo, para personalizar la protección.

**Criterios de Aceptación**:
- Dado que estoy en la sección de coberturas de una ubicación, cuando selecciono una cobertura del catálogo, entonces esta se marca como activa para esa ubicación.
- Dado que una cobertura está seleccionada, cuando la deselecciono, entonces deja de estar activa para esa ubicación.
- Dado que guardo la cotización, cuando la consulto de nuevo, entonces las selecciones de coberturas por ubicación persisten.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-006: Agregar una nueva ubicación de riesgo a la cotización
- HU-011: Visualizar catálogo de coberturas disponibles por tipo de seguro

**Componentes Técnicos**:
- Frontend: Checkboxes/switches para selección de coberturas.
- Backend: API de cotizaciones (persistencia de coberturas seleccionadas por ubicación).

**Notas de Implementación**:
- La interfaz debe indicar claramente qué coberturas están seleccionadas.
- Considerar la posibilidad de coberturas obligatorias que no puedan deseleccionarse.

**Estado**: Backlog

---
### HU-013: Configurar parámetros específicos de cobertura (Sumas Aseguradas, Deducibles)

Como usuario, quiero configurar sumas aseguradas y deducibles específicos para cada cobertura y ubicación, para ajustar la protección a las necesidades del cliente.

**Criterios de Aceptación**:
- Dado que he seleccionado una cobertura que permite configuración de parámetros, cuando accedo a su detalle, entonces puedo introducir la suma asegurada y el deducible.
- Dado que introduzco un valor inválido para una suma asegurada o deducible, cuando intento guardar, entonces el sistema muestra un mensaje de validación.
- Dado que los parámetros de cobertura se han guardado, cuando consulto la cotización, entonces los valores configurados se muestran correctamente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia
**Dependencias**:
- HU-012: Seleccionar y deseleccionar coberturas para una ubicación
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Campos de entrada numérica para sumas aseguradas y deducibles.
- Backend: API de cotizaciones (validación y persistencia de parámetros de cobertura).

**Notas de Implementación**:
- Las validaciones deben considerar rangos mínimos y máximos o pasos para los valores.
- La interfaz debe ser intuitiva para la configuración de múltiples parámetros por cobertura.

**Estado**: Backlog

---
### HU-014: Visualizar resumen de coberturas activas por ubicación

Como usuario, quiero ver un resumen claro de las coberturas activas y sus parámetros para cada ubicación, para una revisión rápida.

**Criterios de Aceptación**:
- Dado que he configurado coberturas para una ubicación, cuando accedo a la vista de resumen de la ubicación, entonces se lista cada cobertura activa con su suma asegurada y deducible.
- Dado que una ubicación no tiene coberturas seleccionadas, cuando accedo a su resumen, entonces se muestra un mensaje indicando que no hay coberturas.
- Dado que se han realizado cambios en las coberturas, cuando accedo al resumen, entonces la información se actualiza automáticamente.
**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-012: Seleccionar y deseleccionar coberturas para una ubicación
- HU-013: Configurar parámetros específicos de cobertura (Sumas Aseguradas, Deducibles)

**Componentes Técnicos**:
- Frontend: Componente de resumen de coberturas por ubicación.

**Notas de Implementación**:
- El resumen debe ser conciso y fácil de leer.
- Se puede considerar la opción de exportar este resumen.

**Estado**: Backlog

---
## FT-004: Ejecución y Persistencia del Cálculo de Primas

### HU-015: Iniciar el cálculo de primas de la cotización

**Descripción**:
Como usuario,
Quiero iniciar el proceso de cálculo de la prima de la cotización,
Para obtener los resultados financieros de las ubicaciones válidas.

**Criterios de Aceptación**:
- Dado que he completado los datos generales y al menos una ubicación con los campos mínimos requeridos (`codigoPostal` válido, `giro.claveIncendio` y garantías tarifables), cuando hago clic en el botón "Calcular Prima", entonces el sistema inicia el proceso de cálculo para las ubicaciones válidas.
- Dado que alguna ubicación tiene datos incompletos (sin CP válido, sin `giro.claveIncendio` o sin garantías tarifables), cuando inicio el cálculo, entonces **el sistema calcula las demás ubicaciones válidas** y muestra una alerta indicando qué ubicaciones fueron excluidas y por qué.
- Dado que **todas** las ubicaciones están incompletas o inactivas y no existe ninguna calculable, cuando intento calcular, entonces el sistema informa que no hay ubicaciones válidas para calcular y no procede.
- Dado que el cálculo se está ejecutando, cuando el usuario interactúa con la interfaz, entonces se muestra un indicador de carga.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-001: Creación y Edición de Datos Generales de la Cotización
- FT-002: Gestión Dinámica de Ubicaciones de Riesgo
- FT-003: Configuración y Selección de Coberturas por Ubicación
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Botón "Calcular Prima", Indicador de carga, Alertas por ubicaciones excluidas.
- Backend: API de cotizaciones (`POST /v1/quotes/{folio}/calculate`).

**Notas de Implementación**:
- El botón de cálculo solo se deshabilita si **no hay ninguna ubicación calculable** en absoluto.
- Las ubicaciones incompletas generan `alertasBloqueantes` y son excluidas individualmente del cálculo; las demás continúan.
- Este comportamiento es un requisito explícito del escenario de aceptación del reto técnico.

**Estado**: Backlog

---
### HU-016: Calcular prima neta y comercial total de la cotización

Como usuario, quiero que el sistema calcule la prima neta y comercial total de la cotización, para conocer el costo final del seguro.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo de la prima, cuando este finaliza exitosamente, entonces el sistema devuelve la prima neta total y la prima comercial total de la cotización.
- Dado que el cálculo incluye factores técnicos y reglas de negocio, cuando se realiza el cálculo, entonces estos se aplican correctamente para obtener los valores finales.
- Dado que los resultados del cálculo se obtienen, cuando se consultan, entonces se muestran con al menos dos decimales y formato de moneda.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-007: Integración con Servicios de Referencia (Tarifas)
- FT-009: Implementación de Reglas de Negocio y Validaciones
**Componentes Técnicos**:
- Backend: Motor de cálculo de primas, Módulo de reglas de negocio.

**Notas de Implementación**:
- La lógica de cálculo debe ser modular y testeable.
- Se debe asegurar la trazabilidad de los factores y reglas aplicadas.

**Estado**: Backlog

---
### HU-017: Calcular y mostrar prima por cada ubicación de riesgo

Como usuario, quiero que el sistema calcule y desglose la prima para cada ubicación de riesgo individualmente, para entender la contribución de cada una al total.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo, cuando este finaliza, entonces el sistema devuelve la prima (neta y comercial) desglosada por cada ubicación de riesgo.
- Dado que una ubicación no tiene coberturas o tiene datos inválidos, cuando se realiza el cálculo, entonces su prima se muestra como cero o con un indicador de error.
- Dado que el cálculo por ubicación se ha completado, cuando se visualizan los resultados, entonces cada ubicación muestra su prima correspondiente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia
**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-002: Gestión Dinámica de Ubicaciones de Riesgo
- FT-003: Configuración y Selección de Coberturas por Ubicación

**Componentes Técnicos**:
- Backend: Motor de cálculo de primas (lógica por ubicación).
- Frontend: Interfaz de visualización de resultados por ubicación.

**Notas de Implementación**:
- La agregación de primas individuales debe coincidir con la prima total.
- Se debe manejar la escala y precisión de los valores monetarios.

**Estado**: Backlog

---
### HU-018: Persistir resultados del cálculo de primas con la cotización

Como usuario, quiero que los resultados del cálculo de la prima (neta, comercial, por ubicación) se guarden de forma persistente con la cotización, para consultarlos posteriormente.

**Criterios de Aceptación**:
- Dado que un cálculo de prima se ha realizado exitosamente, cuando se guardan los resultados, entonces estos se asocian a la versión actual de la cotización.
- Dado que se ha guardado una cotización con resultados de cálculo, cuando se carga la cotización, entonces los resultados financieros previamente calculados se muestran.
- Dado que se realiza un nuevo cálculo, cuando se guardan los nuevos resultados, entonces sobrescriben los anteriores o se versionan si aplica.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-016: Calcular prima neta y comercial total de la cotización
- HU-017: Calcular y mostrar prima por cada ubicación de riesgo
- FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Componentes Técnicos**:
- Backend: API de persistencia de cotizaciones (actualización de resultados financieros).
- Base de Datos: Esquema de almacenamiento de resultados de cálculo.

**Notas de Implementación**:
- La persistencia de los resultados debe ser transaccional con la cotización.
- Se debe considerar si se requiere un historial de cálculos o solo el último.

**Estado**: Backlog

---
### HU-019: Aplicar factores técnicos y reglas de negocio en el cálculo

Como usuario, quiero que el cálculo de primas incorpore los factores técnicos y reglas de negocio definidos, para asegurar la precisión y validez del precio.

**Criterios de Aceptación**:
- Dado que existen factores técnicos (e.g., tasas, recargos) y reglas de negocio (e.g., descuentos por antigüedad), cuando se ejecuta el cálculo, entonces estos se aplican automáticamente.
- Dado que una regla de negocio requiere una validación específica, cuando los datos no la cumplen, entonces el cálculo se detiene o se ajusta según la regla.
- Dado que los factores y reglas se han aplicado, cuando se audita el cálculo, entonces se puede trazar cómo se llegó al resultado final.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-007: Integración con Servicios de Referencia (Tarifas)
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Backend: Motor de reglas de negocio, Módulo de aplicación de factores técnicos.

**Notas de Implementación**:
- La gestión de reglas y factores debe ser parametrizable y no "hardcodeada".
- Se debe documentar la lógica de cada regla y factor aplicado.

**Estado**: Backlog

---
## FT-005: Visualización Detallada de Resultados Financieros

### HU-020: Visualizar resumen de prima neta y comercial total

Como usuario, quiero ver un resumen claro de la prima neta y comercial total de la cotización, para tener una visión global del costo.

**Criterios de Aceptación**:
- Dado que la cotización ha sido calculada, cuando accedo a la sección de resultados financieros, entonces se muestran la prima neta total y la prima comercial total.
- Dado que los resultados se muestran, cuando se consulta la información, entonces los valores son los del último cálculo realizado.
- Dado que la cotización no ha sido calculada, cuando accedo a la sección, entonces se muestra un mensaje indicando que el cálculo no ha sido ejecutado.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-016: Calcular prima neta y comercial total de la cotización
- HU-018: Persistir resultados del cálculo de primas con la cotización

**Componentes Técnicos**:
- Frontend: Componente de resumen de primas totales.
- Backend: API de cotizaciones (consulta de resultados financieros).

**Notas de Implementación**:
- Los valores deben presentarse con formato de moneda y decimales apropiados.
- La interfaz debe ser reactiva a los cambios en el estado de cálculo.

**Estado**: Backlog

---
### HU-021: Visualizar desglose de prima por cada ubicación de riesgo

Como usuario, quiero ver el desglose de la prima (neta y comercial) por cada ubicación de riesgo, para entender el costo asociado a cada una.

**Criterios de Aceptación**:
- Dado que la cotización ha sido calculada, cuando accedo a la sección de resultados financieros, entonces se presenta una lista de ubicaciones, cada una con su prima neta y comercial.
- Dado que una ubicación fue eliminada, cuando se visualizan los resultados, entonces su prima ya no aparece en el desglose.
- Dado que se ha realizado un nuevo cálculo, cuando se consulta el desglose, entonces los valores se actualizan para reflejar el último cálculo.
**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-017: Calcular y mostrar prima por cada ubicación de riesgo
- HU-018: Persistir resultados del cálculo de primas con la cotización

**Componentes Técnicos**:
- Frontend: Tabla o lista de desglose de primas por ubicación.

**Notas de Implementación**:
- Se debe asegurar que el desglose sea coherente con el cálculo total.
- La interfaz debe permitir ordenar o filtrar las ubicaciones si hay muchas.

**Estado**: Backlog

---
### HU-022: Visualizar componentes adicionales del precio (impuestos, recargos)

Como usuario, quiero ver los componentes adicionales que afectan el precio final (impuestos, recargos, descuentos), para comprender la composición del costo.

**Criterios de Aceptación**:
- Dado que la cotización ha sido calculada, cuando accedo a la sección de resultados financieros, entonces se muestran los impuestos, recargos y descuentos aplicados.
- Dado que se aplica un recargo específico, cuando se visualizan los resultados, entonces este recargo se lista con su valor.
- Dado que no se aplican impuestos o recargos, cuando se visualizan los resultados, entonces estas secciones no aparecen o muestran cero.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-016: Calcular prima neta y comercial total de la cotización
- HU-019: Aplicar factores técnicos y reglas de negocio en el cálculo

**Componentes Técnicos**:
- Frontend: Sección de desglose de componentes adicionales.
- Backend: API de cotizaciones (consulta de componentes financieros).

**Notas de Implementación**:
- La presentación de estos componentes debe ser clara y fácil de interpretar.
- Se debe considerar si estos componentes se desglosan a nivel total o también por ubicación.

**Estado**: Backlog

---
### HU-023: Sincronizar resultados financieros con el último cálculo

Como usuario, quiero que la información de los resultados financieros siempre refleje el último cálculo exitoso de la cotización, para garantizar la veracidad de los datos.

**Criterios de Aceptación**:
- Dado que se ha realizado un nuevo cálculo de la cotización, cuando se consultan los resultados, entonces los valores mostrados corresponden a este último cálculo.
- Dado que no se ha realizado ningún cálculo o el último falló, cuando se consultan los resultados, entonces se muestra un estado que lo indica.
- Dado que se modifican datos de la cotización después de un cálculo, cuando se consultan los resultados, entonces se indica que el cálculo actual podría estar desactualizado.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-018: Persistir resultados del cálculo de primas con la cotización
- FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

**Componentes Técnicos**:
- Frontend: Lógica de sincronización de UI con el estado del cálculo.
- Backend: API de cotizaciones (versión del cálculo).

**Notas de Implementación**:
- Se puede usar un campo de fecha/hora de último cálculo para la sincronización.
- Considerar un mensaje de advertencia si la cotización ha sido modificada desde el último cálculo.

**Estado**: Backlog

---
## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

### HU-024: Iniciar cotización en estado "Borrador"

Como usuario, quiero que cada nueva cotización inicie automáticamente en estado "Borrador", para indicar que está en proceso de creación.

**Criterios de Aceptación**:
- Dado que se ha creado una nueva cotización (HU-001), cuando se guarda por primera vez, entonces su estado se establece como "Borrador".
- Dado que consulto una cotización recién creada, cuando veo su información, entonces el estado visible es "Borrador".
- Dado que el estado es "Borrador", cuando se realizan modificaciones, entonces el estado permanece en "Borrador" hasta que se cumplen otras condiciones.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- HU-001: Iniciar nueva cotización con folio automático

**Componentes Técnicos**:
- Backend: Lógica de inicialización de estado en la creación de cotización.

**Notas de Implementación**:
- El estado "Borrador" debe ser el valor por defecto para nuevas cotizaciones.
- Asegurar que este estado se persista correctamente en la base de datos.

**Estado**: Backlog

---
### HU-025: Actualizar estado a "Calculada" tras cálculo exitoso

Como usuario, quiero que el estado de la cotización se actualice a "Calculada" automáticamente después de un cálculo exitoso, para reflejar su progreso.

**Criterios de Aceptación**:
- Dado que se ha realizado un cálculo de prima exitoso (HU-015), cuando el proceso finaliza, entonces el estado de la cotización cambia a "Calculada".
- Dado que el cálculo falla, cuando el proceso termina, entonces el estado de la cotización no cambia a "Calculada" y se mantiene el estado anterior.
- Dado que el estado es "Calculada", cuando consulto la cotización, entonces este estado es visible.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Componentes Técnicos**:
- Backend: Lógica de actualización de estado post-cálculo.

**Notas de Implementación**:
- La actualización de estado debe ser parte de la transacción de guardado de resultados de cálculo.
- Considerar si se permite recalcular una cotización "Calculada".

**Estado**: Backlog

---
### HU-026: Cambiar estado a "Aprobada" o "Rechazada" manualmente

Como usuario, quiero poder cambiar manualmente el estado de una cotización a "Aprobada" o "Rechazada", para indicar la decisión del cliente.

**Criterios de Aceptación**:
- Dado que el estado de la cotización es "Calculada", cuando selecciono la opción "Aprobar" o "Rechazar", entonces el estado se actualiza en consecuencia.
- Dado que el estado de la cotización no es "Calculada", cuando intento cambiar a "Aprobada" o "Rechazada", entonces el sistema me impide la acción o me advierte.
- Dado que el estado se ha actualizado, cuando consulto la cotización, entonces el nuevo estado es visible.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-025: Actualizar estado a "Calculada" tras cálculo exitoso
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Botones/menús para cambiar estado.
- Backend: API de cotizaciones (endpoint de actualización de estado), Lógica de transiciones de estado.

**Notas de Implementación**:
- Las transiciones de estado deben seguir una máquina de estados definida.
- Se puede requerir un campo de comentarios para justificar el cambio de estado.

**Estado**: Backlog

---
### HU-027: Establecer estado "Emitida" para cotizaciones aprobadas

Como usuario, quiero poder establecer el estado de una cotización a "Emitida" una vez que ha sido aprobada, para finalizar el ciclo de vida.

**Criterios de Aceptación**:
- Dado que el estado de la cotización es "Aprobada", cuando selecciono la opción "Emitir", entonces el estado se actualiza a "Emitida".
- Dado que el estado de la cotización no es "Aprobada", cuando intento cambiar a "Emitida", entonces el sistema me impide la acción.
- Dado que el estado es "Emitida", cuando consulto la cotización, entonces el nuevo estado es visible y la cotización se considera finalizada.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-026: Cambiar estado a "Aprobada" o "Rechazada" manualmente
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Botón/opción "Emitir".
- Backend: API de cotizaciones (endpoint de actualización de estado), Lógica de transiciones de estado.

**Notas de Implementación**:
- El estado "Emitida" generalmente bloquea futuras ediciones de la cotización.
- Considerar la integración con un sistema de emisión de pólizas.

**Estado**: Backlog

---
### HU-028: Visualizar el estado actual de la cotización

Como usuario, quiero ver claramente el estado actual de la cotización en la interfaz, para conocer su progreso en todo momento.

**Criterios de Aceptación**:
- Dado que he cargado una cotización, cuando visualizo sus datos, entonces el estado actual (e.g., Borrador, Calculada, Aprobada) se muestra de forma prominente.
- Dado que el estado de la cotización cambia, cuando la interfaz se actualiza, entonces el estado mostrado también se actualiza.
- Dado que la cotización tiene un estado específico, cuando se muestra, entonces se utiliza una representación visual consistente (e.g., color, etiqueta).

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- Todas las HUs de FT-006

**Componentes Técnicos**:
- Frontend: Componente de visualización de estado.

**Notas de Implementación**:
- La visibilidad del estado debe ser constante en las pantallas de edición y consulta.
- Usar un patrón de diseño para los estados (e.g., badges, etiquetas).

**Estado**: Backlog

---
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
## FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

### HU-035: Incrementar versión de cotización en cada edición

Como desarrollador, quiero que cada vez que se edita una cotización, se incremente un campo de versión, para mantener un control de cambios.

**Criterios de Aceptación**:
- Dado que una cotización se guarda después de una edición, cuando se persiste, entonces el campo `version` se incrementa en uno.
- Dado que la cotización se crea por primera vez, cuando se guarda, entonces el campo `version` se inicializa en 1.
- Dado que se intenta guardar una versión antigua de la cotización, cuando el sistema detecta una versión más reciente, entonces previene la sobrescritura.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Componentes Técnicos**:
- Backend: Capa de persistencia, Lógica de control de concurrencia.
- Base de Datos: Campo `version` en el esquema de cotizaciones.

**Notas de Implementación**:
- El campo de versión puede ser un número entero o un timestamp.
- La lógica de incremento debe ser atómica con la operación de guardado.

**Estado**: Backlog

---
### HU-036: Actualizar fecha de última modificación en cada edición

Como desarrollador, quiero que en cada edición de una cotización se actualice el campo `fechaUltimaActualizacion`, para conocer cuándo fue el último cambio.

**Criterios de Aceptación**:
- Dado que una cotización se guarda después de una edición, cuando se persiste, entonces el campo `fechaUltimaActualizacion` se actualiza con la fecha y hora actuales.
- Dado que la cotización se crea por primera vez, cuando se guarda, entonces el campo `fechaUltimaActualizacion` se establece.
- Dado que consulto una cotización, cuando veo sus metadatos, entonces la `fechaUltimaActualizacion` refleja el último guardado.

**Prioridad**: Media

**Estimación**: 1 punto de historia

**Dependencias**:
- Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Componentes Técnicos**:
- Backend: Capa de persistencia.
- Base de Datos: Campo `fechaUltimaActualizacion` en el esquema de cotizaciones.

**Notas de Implementación**:
- El formato de la fecha debe ser consistente (e.g., ISO 8601 con zona horaria).
- Esta actualización debe ser automática y no requerir intervención del usuario.

**Estado**: Backlog

---
### HU-037: Implementar versionado optimista para prevenir conflictos

Como desarrollador, quiero implementar un mecanismo de versionado optimista, para prevenir la pérdida de datos por ediciones concurrentes de la misma cotización.

**Criterios de Aceptación**:
- Dado que dos usuarios intentan editar y guardar la misma cotización simultáneamente, cuando el segundo usuario intenta guardar una versión desactualizada, entonces el sistema rechaza la operación.
- Dado que se rechaza una operación por versionado optimista, cuando el sistema lo detecta, entonces notifica al usuario que la cotización ha sido modificada por otro usuario.
- Dado que se previene un conflicto, cuando el usuario afectado decide recargar, entonces puede ver la versión más reciente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-035: Incrementar versión de cotización en cada edición
- Todas las features que modifican datos de cotización (FT-001, FT-002, FT-003, FT-004, FT-006)

**Componentes Técnicos**:
- Backend: Lógica de control de concurrencia en la capa de persistencia.
- Frontend: Manejo de errores de concurrencia, Mensajes al usuario.

**Notas de Implementación**:
- La estrategia de versionado puede basarse en un número de versión o ETag.
- Se debe diseñar una estrategia para que el usuario pueda resolver el conflicto (e.g., recargar y volver a aplicar cambios).

**Estado**: Backlog

---
### HU-038: Permitir actualización parcial de campos de cotización

Como desarrollador, quiero que el sistema permita la actualización parcial de los campos de la cotización, para optimizar las operaciones de guardado.

**Criterios de Aceptación**:
- Dado que solo se modifica un subconjunto de los campos de la cotización, cuando se guarda, entonces solo los campos modificados se actualizan en la base de datos.
- Dado que se actualiza parcialmente una cotización, cuando se persiste, entonces el resto de los datos no modificados permanecen intactos.
- Dado que se realiza una actualización parcial, cuando se guarda, entonces el campo `version` y `fechaUltimaActualizacion` se actualizan.
**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Componentes Técnicos**:
- Backend: Capa de persistencia (ej. uso de `PATCH` en API REST o lógica de `update` en ORM/ODM).

**Notas de Implementación**:
- Esto requiere un diseño cuidadoso de la API y la capa de datos para manejar objetos parciales.
- Asegurar que las validaciones se apliquen solo a los campos presentes en la actualización parcial.

**Estado**: Backlog

---
## FT-009: Implementación de Reglas de Negocio y Validaciones

### HU-039: Validar datos generales de la cotización

Como usuario, quiero que los datos generales de la cotización se validen según reglas de negocio, para asegurar la calidad de la información.

**Criterios de Aceptación**:
- Dado que introduzco un RFC con formato incorrecto, cuando intento guardar, entonces el sistema muestra un mensaje de error de validación.
- Dado que la fecha de fin de vigencia es anterior a la fecha de inicio, cuando intento guardar, entonces el sistema muestra un error.
- Dado que un campo obligatorio (e.g., Nombre Asegurado) está vacío, cuando intento guardar, entonces el sistema me lo indica.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-001: Creación y Edición de Datos Generales de la Cotización

**Componentes Técnicos**:
- Backend: Módulo de validación de datos generales.
- Frontend: Validaciones en el formulario.

**Notas de Implementación**:
- Implementar validaciones tanto en el frontend para una respuesta rápida como en el backend para seguridad.
- Los mensajes de error deben ser claros y orientar al usuario.

**Estado**: Backlog

---
### HU-040: Validar datos específicos de cada ubicación de riesgo

Como usuario, quiero que los datos específicos de cada ubicación de riesgo se validen, para asegurar la consistencia y corrección de la información de riesgo.

**Criterios de Aceptación**:
- Dado que introduzco un código postal inexistente, cuando intento guardar la ubicación, entonces el sistema muestra un error.
- Dado que el valor del bien excede un límite predefinido, cuando intento guardar, entonces el sistema muestra una advertencia o error.
- Dado que un campo obligatorio de la ubicación (e.g., dirección, uso) está vacío, cuando intento guardar, entonces el sistema me lo indica.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- FT-002: Gestión Dinámica de Ubicaciones de Riesgo
- HU-009: Consultar y validar código postal de ubicación

**Componentes Técnicos**:
- Backend: Módulo de validación de datos de ubicación.
- Frontend: Validaciones en el formulario de ubicación.

**Notas de Implementación**:
- Las reglas de validación deben ser configurables y escalables.
- Se debe diferenciar entre errores que impiden el guardado y advertencias.

**Estado**: Backlog

---
### HU-041: Aplicar reglas de negocio para el cálculo de primas

Como desarrollador, quiero que la lógica de cálculo de primas incorpore todas las reglas de negocio y factores técnicos definidos, para asegurar la precisión del precio.

**Criterios de Aceptación**:
- Dado que se aplica un recargo por una característica específica de la ubicación, cuando se ejecuta el cálculo, entonces el recargo se suma correctamente a la prima.
- Dado que existe un descuento por un canal de venta, cuando se ejecuta el cálculo, entonces el descuento se aplica a la prima comercial.
- Dado que una combinación de coberturas o riesgos requiere un factor especial, cuando se calcula, entonces ese factor se aplica según la regla definida.

**Prioridad**: Alta

**Estimación**: 5 puntos de historia

**Dependencias**:
- FT-004: Ejecución y Persistencia del Cálculo de Primas
- FT-007: Integración con Servicios de Referencia (Tarifas)

**Componentes Técnicos**:
- Backend: Motor de reglas de negocio, Módulo de cálculo de primas.

**Notas de Implementación**:
- Las reglas deben ser parametrizables y fáciles de mantener/actualizar.
- Se debe documentar cada regla de negocio y su impacto en el cálculo.

**Estado**: Backlog

---
### HU-042: Mostrar mensajes de error claros y útiles

Como usuario, quiero que el sistema me proporcione mensajes de error claros y útiles cuando las validaciones fallan, para saber cómo corregir los problemas.

**Criterios de Aceptación**:
- Dado que un campo no cumple con una validación, cuando intento guardar, entonces el mensaje de error indica específicamente qué campo y qué regla no se cumple.
- Dado que el sistema encuentra un error técnico, cuando me lo notifica, entonces el mensaje es amigable y sugiere una acción (e.g., "intente de nuevo").
- Dado que se muestran mensajes de error, cuando se corrigen los problemas, entonces los mensajes desaparecen.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-039: Validar datos generales de la cotización
- HU-040: Validar datos específicos de cada ubicación de riesgo

**Componentes Técnicos**:
- Frontend: Componentes de visualización de mensajes de error.
- Backend: API de cotizaciones (respuestas con mensajes de error estandarizados).

**Notas de Implementación**:
- Estandarizar el formato de los mensajes de error.
- Considerar la internacionalización si es necesario.

**Estado**: Backlog

---
### HU-043: Documentar y trazar las reglas de negocio

Como analista funcional, quiero que las reglas de negocio sean trazables y documentadas, para asegurar la transparencia y el mantenimiento del sistema.

**Criterios de Aceptación**:
- Dado que una regla de negocio está implementada, cuando se consulta la documentación técnica, entonces se describe la regla, su propósito y su implementación.
- Dado que una regla de negocio se aplica en el código, cuando se revisa, entonces está claramente identificada y mapeada con la documentación.
- Dado que se requiere modificar una regla, cuando se identifica, entonces la documentación y el código son fáciles de actualizar.
**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Backend: Código de reglas de negocio con comentarios/documentación interna.
- Documentación: Archivos de especificación de reglas de negocio.

**Notas de Implementación**:
- Utilizar herramientas de documentación (e.g., Swagger/OpenAPI para API, Confluence para reglas).
- Mantener la documentación sincronizada con el código.

**Estado**: Backlog

---
## FT-010: Configuración y Gestión de Parámetros de Cálculo

### HU-044: Ingestión de Tarifas de Incendio
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta de tarifas de incendio desde el servicio `Plataforma-core-ohs`,
Para que el motor de cálculo tenga acceso a los datos más recientes y correctos de incendio.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga de tarifas de incendio, entonces el sistema obtiene las tarifas correctamente.
- Dado que las tarifas de incendio son cargadas, cuando el motor de cálculo las consulta, entonces recibe los valores actualizados.
- Dado que ocurre un error al obtener las tarifas de incendio, cuando el sistema intenta cargarlas, entonces se registra el error y se notifica al administrador.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de parámetros de cálculo

**Notas de Implementación**:
La simulación debe replicar fielmente el contrato de la API real. Se debe considerar un mecanismo de caché para reducir llamadas repetidas si la latencia es un problema.

**Estado**: Backlog

---
### HU-045: Ingestión de Tarifas CAT
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta de tarifas de catástrofe (CAT) desde el servicio `Plataforma-core-ohs`,
Para que el motor de cálculo aplique los factores de catástrofe correctos según la zona.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga de tarifas CAT, entonces el sistema obtiene los factores correctamente.
- Dado que las tarifas CAT son cargadas, cuando el motor de cálculo las consulta para una zona específica, entonces recibe los valores actualizados.
- Dado que los factores CAT tienen fechas de vigencia, cuando el sistema los carga, entonces se consideran estas fechas para la disponibilidad.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)
- HU-044 (patrón similar de integración)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de parámetros de cálculo

**Notas de Implementación**:
Asegurar que el mapeo de zonas CAT sea consistente con el `catalogo_cp_zonas`.

**Estado**: Backlog

---
### HU-046: Ingestión de Tarifa FHM y Factores de Equipo Electrónico
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta de la tarifa FHM y los factores de equipo electrónico desde `Plataforma-core-ohs`,
Para asegurar cálculos precisos en estas coberturas específicas.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga de tarifa FHM y factores de equipo electrónico, entonces el sistema los obtiene.
- Dado que los parámetros FHM y de equipo electrónico son cargados, cuando el motor de cálculo los requiere, entonces están disponibles y actualizados.
- Dado que un factor es nulo o inválido en la fuente, cuando el sistema lo ingesta, entonces se maneja el error o se utiliza un valor por defecto configurable.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de parámetros de cálculo

**Notas de Implementación**:
Definir cómo se manejan los valores predeterminados o los errores de datos para estos parámetros.

**Estado**: Backlog

---
### HU-047: Ingestión y Mapeo de Catálogo de Códigos Postales y Zonas
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta del `catalogo_cp_zonas` desde `Plataforma-core-ohs` y realice el mapeo de zonas,
Para que las ubicaciones de riesgo se clasifiquen correctamente según su código postal.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga del catálogo CP-Zonas, entonces el sistema obtiene los datos.
- Dado que el catálogo CP-Zonas es cargado, cuando se consulta un código postal, entonces el sistema devuelve la zona CAT y el nivel técnico asociado.
- Dado que un código postal no se encuentra en el catálogo, cuando se consulta, entonces el sistema devuelve un valor por defecto o un error indicando la falta de mapeo.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de catálogos
- Mapeador de datos de CP a zonas

**Notas de Implementación**:
La estructura del catálogo debe ser eficiente para consultas frecuentes. Considerar indexación o estructuras de datos optimizadas.

**Estado**: Backlog

---
### HU-048: Disponibilidad de Parámetros para Motores
**Descripción**:
Como desarrollador del motor de cálculo,
Quiero que todos los parámetros, tarifas y catálogos ingestado sean accesibles y estén actualizados,
Para que el motor de validación y cálculo opere con información consistente y correcta.

**Criterios de Aceptación**:
- Dado que los parámetros y tarifas han sido cargados (HU-044, HU-045, HU-046, HU-047), cuando el Motor de Validación o el Motor Central de Cálculo los solicitan, entonces los reciben sin latencia significativa.
- Dado que un parámetro ha sido actualizado en la fuente, cuando el sistema lo refresca, entonces los motores subsiguientes usan la nueva versión.
- Dado que los parámetros no están disponibles, cuando los motores intentan acceder a ellos, entonces se maneja la excepción adecuadamente y se evita el cálculo.
**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- HU-044, HU-045, HU-046, HU-047

**Componentes Técnicos**:
- Fachada de acceso a parámetros
- Servicios de consulta de catálogos

**Notas de Implementación**:
Se debe implementar una capa de servicio que actúe como un punto centralizado para que los motores accedan a los parámetros, ocultando la complejidad de la ingestión.

**Estado**: Backlog

---
## FT-011: Motor de Validación de Reglas de Negocio

### HU-049: Validación de Rangos de Suma Asegurada
**Descripción**:
Como usuario,
Quiero que el sistema valide que las sumas aseguradas de cada ubicación estén dentro de los rangos predefinidos,
Para evitar errores en la cotización y asegurar que los montos sean coherentes con las políticas de suscripción.

**Criterios de Aceptación**:
- Dado que ingreso una suma asegurada dentro del rango permitido, cuando se ejecuta la validación, entonces la suma asegurada se considera válida.
- Dado que ingreso una suma asegurada por debajo del mínimo, cuando se ejecuta la validación, entonces el sistema reporta un error específico para esa suma asegurada.
- Dado que ingreso una suma asegurada por encima del máximo, cuando se ejecuta la validación, entonces el sistema reporta un error específico para esa suma asegurada.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-010 (para obtener rangos de validación si son dinámicos)

**Componentes Técnicos**:
- Módulo de reglas de validación
- Componente de reporte de errores

**Notas de Implementación**:
Los rangos deben ser configurables y posiblemente depender de otros factores como el tipo de riesgo o la zona.

**Estado**: Backlog

---
### HU-050: Validación de Código Postal y Zona
**Descripción**:
Como usuario,
Quiero que el sistema valide los códigos postales de las ubicaciones contra el `catalogo_cp_zonas`,
Para asegurar la correcta clasificación geográfica del riesgo y la aplicación de factores específicos.

**Criterios de Aceptación**:
- Dado que ingreso un código postal existente en el `catalogo_cp_zonas`, cuando se ejecuta la validación, entonces el código postal se considera válido y se asigna la zona correspondiente.
- Dado que ingreso un código postal no existente en el `catalogo_cp_zonas`, cuando se ejecuta la validación, entonces el sistema reporta un error indicando un código postal inválido.
- Dado que el `catalogo_cp_zonas` no está disponible, cuando se intenta validar un código postal, entonces el sistema maneja la situación (e.g., error de sistema o validación predeterminada).

**Prioridad**: Alta
**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-047 (para la disponibilidad del `catalogo_cp_zonas`)

**Componentes Técnicos**:
- Módulo de reglas de validación
- Servicio de consulta de catálogos (CP)
- Componente de reporte de errores

**Notas de Implementación**:
La validación debe ser performante, especialmente si hay muchas ubicaciones.

**Estado**: Backlog

---
### HU-051: Verificación de Datos Mínimos por Ubicación
**Descripción**:
Como usuario,
Quiero que el sistema verifique que todas las ubicaciones tengan los datos mínimos requeridos para el cálculo de prima,
Para garantizar que el cálculo pueda proceder correctamente y evitar resultados incompletos.

**Criterios de Aceptación**:
- Dado que todos los campos obligatorios de una ubicación están completos, cuando se ejecuta la validación, entonces la ubicación se considera completa.
- Dado que falta un campo obligatorio en una ubicación, cuando se ejecuta la validación, entonces el sistema reporta un error específico para el campo faltante.
- Dado que se intenta calcular con una ubicación incompleta, cuando se ejecuta la validación, entonces el cálculo es bloqueado y se muestran los errores.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- Ninguna específica de otra HU, pero depende del modelo de datos de la cotización.

**Componentes Técnicos**:
- Módulo de reglas de validación
- Componente de reporte de errores

**Notas de Implementación**:
La definición de "datos mínimos" debe ser clara y estar documentada en el modelo de dominio.

**Estado**: Backlog

---
### HU-052: Generación de Mensajes de Error Claros
**Descripción**:
Como usuario,
Quiero que el sistema me muestre mensajes de error claros y específicos cuando una validación falle,
Para entender rápidamente qué debo corregir y cómo proceder.

**Criterios de Aceptación**:
- Dado que una validación falla (e.g., suma asegurada fuera de rango), cuando se muestra el error, entonces el mensaje es descriptivo e indica la acción correctiva.
- Dado que múltiples validaciones fallan en una misma ubicación, cuando se muestran los errores, entonces cada error es listado individualmente y es comprensible.
- Dado que un mensaje de error se genera, cuando el usuario lo ve, entonces puede identificar fácilmente el campo o la regla que causó el problema.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-049, HU-050, HU-051

**Componentes Técnicos**:
- Componente de reporte de errores
- Módulo de mensajes de usuario

**Notas de Implementación**:
Los mensajes deben ser localizables y consistentes en su tono y formato.

**Estado**: Backlog---
### HU-053: Bloqueo de Cálculo por Errores de Validación
**Descripción**:
Como usuario,
Quiero que el sistema impida el cálculo de la prima si existen errores de validación activos en la cotización o sus ubicaciones,
Para asegurar que solo se realicen cálculos con datos válidos y completos.

**Criterios de Aceptación**:
- Dado que existen errores de validación en la cotización o en alguna de sus ubicaciones, cuando se intenta ejecutar el cálculo, entonces el cálculo es abortado.
- Dado que el cálculo es abortado por errores de validación, cuando el usuario es notificado, entonces se le redirige o se le indica que resuelva los errores primero.
- Dado que no hay errores de validación activos, cuando se intenta ejecutar el cálculo, entonces el cálculo puede proceder normalmente.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- HU-049, HU-050, HU-051, HU-052

**Componentes Técnicos**:
- Módulo de orquestación de cálculo
- Componente de control de flujo

**Notas de Implementación**:
La lógica de bloqueo debe ser una pre-condición estricta para la ejecución del Motor Central de Cálculo.

**Estado**: Backlog

---
## FT-012: Motor Central de Cálculo de Primas

### HU-054: Cálculo de Prima Neta por Ubicación
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima neta para cada ubicación de riesgo, utilizando las tarifas y factores técnicos correspondientes,
Para obtener el costo base del seguro para cada propiedad asegurada.

**Criterios de Aceptación**:
- Dado que una ubicación tiene todos los datos válidos, cuando se ejecuta el cálculo, entonces se obtiene una prima neta individual para esa ubicación.
- Dado que se aplican las tarifas de incendio correctas según la suma asegurada y tipo de riesgo, cuando se calcula la prima neta, entonces el valor es preciso.
- Dado que hay múltiples ubicaciones, cuando se ejecuta el cálculo, entonces cada una tiene su prima neta calculada de forma independiente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- FT-010 (para acceso a tarifas y factores)
- FT-011 (las validaciones deben pasarse)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima
- Servicio de consulta de parámetros

**Notas de Implementación**:
La lógica de cálculo debe ser modular y fácil de testear. Se recomienda el uso de objetos de valor inmutables para los parámetros de entrada y los resultados intermedios.

**Estado**: Backlog

---
### HU-055: Aplicación de Factores CAT y FHM
**Descripción**:
Como usuario,
Quiero que el sistema aplique los factores de Catástrofe (CAT) y FHM según la zona y condiciones de la ubicación,
Para ajustar la prima neta por estos riesgos específicos y obtener una prima técnica más completa.

**Criterios de Aceptación**:
- Dado que una ubicación está en una zona CAT específica, cuando se calcula la prima, entonces se aplica el factor CAT correspondiente a esa zona.
- Dado que una ubicación cumple con las condiciones para la tarifa FHM, cuando se calcula la prima, entonces se aplica la cuota FHM definida.
- Dado que los factores CAT o FHM no aplican a una ubicación, cuando se calcula la prima, entonces no se incluyen en el cálculo o se usan valores neutros.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-045 (para tarifas CAT)
- HU-046 (para tarifa FHM)
- HU-054 (se aplica sobre la prima neta)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima
- Servicio de aplicación de factores

**Notas de Implementación**:
Asegurar la correcta identificación de la zona para aplicar el factor CAT.

**Estado**: Backlog

---
### HU-056: Cálculo de Prima Comercial Total
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima comercial total de la cotización, a partir de la suma de las primas netas y la aplicación de factores comerciales,
Para conocer el costo final que se presentará al cliente.

**Criterios de Aceptación**:
- Dado que las primas netas de todas las ubicaciones han sido calculadas, cuando se ejecuta el cálculo de prima comercial, entonces se suman las primas netas.
- Dado que existen factores comerciales (e.g., gastos de expedición, impuestos), cuando se calcula la prima comercial, entonces se aplican correctamente sobre la suma de primas netas.
- Dado que no hay factores comerciales, cuando se calcula la prima comercial, entonces el valor es igual a la suma de las primas netas.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-054 (requiere las primas netas por ubicación)
- HU-055 (requiere las primas técnicas ajustadas)

**Componentes Técnicos**:
- Algoritmos de consolidación de primas
- Servicio de aplicación de factores comerciales

**Notas de Implementación**:
Definir los factores comerciales y su orden de aplicación.

**Estado**: Backlog

---
### HU-057: Generación de Desglose de Primas por Ubicación
**Descripción**:Como usuario,
Quiero que el sistema genere el desglose detallado de primas por cada ubicación de riesgo,
Para entender la composición del costo y para fines de auditoría.

**Criterios de Aceptación**:
- Dado que las primas han sido calculadas para cada ubicación, cuando se solicita el desglose, entonces se muestra la prima neta, CAT, FHM y cualquier otro componente por ubicación.
- Dado que el desglose se genera, cuando se compara con el cálculo total, entonces la suma de los componentes individuales coincide con los totales calculados.
- Dado que una ubicación no tiene ciertos factores (e.g., no aplica CAT), cuando se muestra el desglose, entonces esos componentes se muestran como cero o no aplicables.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-054, HU-055

**Componentes Técnicos**:
- Algoritmos de cálculo de prima
- Estructura de datos de resultados

**Notas de Implementación**:
El desglose debe ser claro y fácil de interpretar, posiblemente en un formato estructurado (JSON).

**Estado**: Backlog

---
### HU-058: Cálculo Preciso de Prima de Incendio
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima de incendio basándose en la suma asegurada y las tarifas correspondientes,
Para obtener un valor preciso y fundamental del riesgo de incendio.

**Criterios de Aceptación**:
- Dado que tengo la suma asegurada y la tarifa de incendio aplicable, cuando se invoca el cálculo de prima de incendio, entonces el resultado es `SumaAsegurada * TarifaIncendio`.
- Dado que la tarifa de incendio es variable por zona o tipo de construcción, cuando se calcula, entonces se utiliza la tarifa correcta para la ubicación.
- Dado que la suma asegurada es cero, cuando se calcula la prima de incendio, entonces el resultado es cero.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-044 (para tarifas de incendio)
- HU-054 (es un componente de la prima neta)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima (sub-componente de incendio)

**Notas de Implementación**:
La fórmula debe ser verificada y documentada.

**Estado**: Backlog

---
### HU-059: Cálculo Preciso de Prima de Equipo Electrónico
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima de equipo electrónico basándose en la suma asegurada y los factores correspondientes,
Para obtener un valor preciso para esta cobertura específica.

**Criterios de Aceptación**:
- Dado que tengo la suma asegurada y el factor de equipo electrónico aplicable, cuando se invoca el cálculo de prima de equipo electrónico, entonces el resultado es `SumaAsegurada * FactorEquipoElectronico`.
- Dado que el factor de equipo electrónico es variable por clase o nivel de zona, cuando se calcula, entonces se utiliza el factor correcto para la ubicación.
- Dado que la suma asegurada de equipo electrónico es cero, cuando se calcula la prima, entonces el resultado es cero.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-046 (para factores de equipo electrónico)
- HU-054 (es un componente de la prima neta)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima (sub-componente de equipo electrónico)

**Notas de Implementación**:
La fórmula debe ser verificada y documentada.

**Estado**: Backlog

---
## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

### HU-060: Persistencia de Resultados de Cálculo en Cotización
**Descripción**:
Como usuario,
Quiero que los resultados de la prima neta, prima comercial y el desglose por ubicación se guarden automáticamente en el documento de cotización en MongoDB,
Para que estén disponibles para consulta futura y no se pierdan al cerrar la aplicación.

**Criterios de Aceptación**:
- Dado que se ha ejecutado un cálculo exitoso, cuando se invoca la persistencia, entonces la prima neta total, prima comercial y el desglose por ubicación se guardan en el documento de cotización.
- Dado que los resultados de cálculo son guardados, cuando consulto la cotización, entonces puedo ver los valores financieros actualizados.
- Dado que un resultado no se puede guardar (e.g., error de DB), cuando se intenta la persistencia, entonces la operación es revertida y se notifica el error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-012 (necesita los resultados del cálculo)

**Componentes Técnicos**:
- Repositorio de cotizaciones (MongoDB)
- Módulo de persistencia de resultados

**Notas de Implementación**:
El esquema de datos de MongoDB debe ser diseñado para almacenar estos resultados de manera eficiente y consultable.

**Estado**: Backlog

---
### HU-061: Atomicidad en la Persistencia del Cálculo
**Descripción**:
Como desarrollador,
Quiero que la operación de guardar los resultados del cálculo sea atómica,
Para asegurar que todos los datos se guarden correctamente o ninguno, manteniendo la consistencia del documento de cotización.

**Criterios de Aceptación**:
- Dado que se intenta guardar los resultados de cálculo, cuando ocurre un fallo en la mitad de la operación, entonces ningún cambio parcial se persiste en la base de datos.
- Dado que la operación de persistencia se completa con éxito, cuando se verifica el documento en la base de datos, entonces todos los resultados de cálculo están presentes y son consistentes.
- Dado que se utiliza una transacción o enfoque atómico, cuando se guardan los resultados, entonces la integridad de los datos está garantizada.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-060
**Componentes Técnicos**:
- Repositorio de cotizaciones (MongoDB)
- Manejo de transacciones o "all-or-nothing" en MongoDB

**Notas de Implementación**:
Considerar el uso de transacciones de MongoDB (si aplica a la versión y configuración) o un patrón de "dos fases commit" simulado si es necesario para asegurar la atomicidad.

**Estado**: Backlog

---
### HU-062: Actualización de Metadatos de Cotización
**Descripción**:
Como desarrollador,
Quiero que al guardar los resultados del cálculo, se actualice el campo `fechaUltimaActualizacion` y el número de versión de la cotización,Para mantener un registro de cambios y facilitar la gestión de concurrencia.

**Criterios de Aceptación**:
- Dado que se persiste un cálculo exitoso, cuando se guarda la cotización, entonces el campo `fechaUltimaActualizacion` se actualiza con la fecha y hora actuales.
- Dado que se persiste un cálculo exitoso, cuando se guarda la cotización, entonces el número de versión de la cotización se incrementa en uno.
- Dado que la cotización se guarda sin cambios en los resultados de cálculo, cuando se verifica, entonces solo la `fechaUltimaActualizacion` y la versión se actualizan.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-060
- FT-014 (trabaja en conjunto con el versionado optimista)

**Componentes Técnicos**:
- Repositorio de cotizaciones
- Módulo de actualización de metadatos

**Notas de Implementación**:
Esta actualización debe ser parte de la misma operación atómica de persistencia.

**Estado**: Backlog

---
### HU-063: Registro de Parámetros para Trazabilidad
**Descripción**:
Como auditor,
Quiero que el sistema registre los parámetros y tarifas clave utilizados para un cálculo específico,
Para poder auditar y entender cómo se llegó a un resultado específico en cualquier momento.

**Criterios de Aceptación**:
- Dado que se ejecuta un cálculo de prima, cuando los resultados se persisten, entonces los valores clave de tarifas (incendio, CAT, FHM) y otros parámetros (rangos, CP-zona) se registran junto con la cotización.
- Dado que se consulta una cotización histórica, cuando se visualiza su trazabilidad, entonces se muestran los parámetros exactos usados en ese cálculo.
- Dado que un parámetro utilizado en el cálculo cambia su valor en el tiempo, cuando se audita un cálculo anterior, entonces se ve el valor del parámetro en el momento del cálculo, no el actual.

**Prioridad**: Media

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-060 (se registra al persistir)
- FT-010 (son los parámetros a registrar)

**Componentes Técnicos**:
- Componente de auditoría/logging de cálculo
- Diseño de esquema de datos de cotización (para almacenar info de trazabilidad)

**Notas de Implementación**:
Decidir qué nivel de detalle de parámetros se debe almacenar para no sobrecargar el documento de cotización, quizás solo IDs de versiones de catálogos o un hash de los parámetros.

**Estado**: Backlog

---
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
## FT-016: Integración de Catálogo de Códigos Postales y Zonas

### HU-074: Consultar Información de Código Postal y Zona

**Descripción**:Como usuario,
Quiero consultar un código postal y obtener su información de zona (CAT, nivel técnico) desde el servicio `Plataforma-core-ohs` (o su mock),
Para que el cotizador pueda aplicar tarifas y factores de riesgo precisos basados en la ubicación.

**Criterios de Aceptación**:
- Dado que ingreso un código postal en el formulario, cuando el sistema lo valida, entonces muestra la zona CAT y el nivel técnico asociados.
- Dado que el código postal es válido y existe en el catálogo, cuando el sistema lo consulta, entonces recupera la información de zona CAT y nivel técnico.
- Dado que la consulta es exitosa, cuando la información de zona se obtiene, entonces se guarda con los datos de ubicación de riesgo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068 (Conectividad básica)
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de CP
- Módulo de mapeo de datos

**Notas de Implementación**:
La consulta debe ser rápida y eficiente para no retrasar la interacción del usuario.

**Estado**: Backlog

---
### HU-075: Validar Código Postal

**Descripción**:
Como usuario,Quiero que el sistema valide el código postal ingresado,
Para asegurar que es un código postal válido y existente en el catálogo, y evitar errores en la cotización.

**Criterios de Aceptación**:
- Dado que ingreso un código postal con formato incorrecto, cuando el sistema lo valida, entonces muestra un mensaje de error de formato.
- Dado que ingreso un código postal válido pero no encontrado en el catálogo, cuando el sistema lo valida, entonces muestra un mensaje indicando que el CP no existe.
- Dado que ingreso un código postal válido y existente, cuando el sistema lo valida, entonces permite continuar con la captura de la ubicación.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-074

**Componentes Técnicos**:
- Módulo de validación de CP
- Cliente API REST
- Interfaz de usuario (frontend)

**Notas de Implementación**:
La validación puede ser tanto en el frontend (formato básico) como en el backend (existencia en catálogo).

**Estado**: Backlog

---
### HU-076: Mapear Información de Zonas de Código Postal

**Descripción**:
Como sistema,
Quiero mapear la información de zonas (CAT, nivel técnico) obtenida de un código postal del servicio externo,
Para que esté disponible en el modelo de datos interno y pueda ser utilizada en la lógica de cálculo de primas por ubicación.

**Criterios de Aceptación**:
- Dado que se recibe una respuesta de consulta de CP, cuando el sistema la procesa, entonces los campos de zona CAT y nivel técnico se extraen y mapean correctamente.
- Dado que la información de zona CAT se mapea, cuando se guarda la ubicación, entonces el valor mapeado se persiste.
- Dado que el servicio externo no devuelve alguna información de zona, cuando el sistema la mapea, entonces se asigna un valor por defecto o se registra una inconsistencia.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-074

**Componentes Técnicos**:
- Capa de mapeo de datos
- Modelo de datos de ubicación

**Notas de Implementación**:
Asegurar que los tipos de datos sean compatibles entre el origen y el destino.

**Estado**: Backlog

---
## FT-017: Integración de Catálogos de Clasificación de Riesgo y Garantías

### HU-077: Recuperar Catálogo de Clasificación de Riesgo

**Descripción**:
Como sistema,
Quiero recuperar el catálogo de clasificación de riesgo desde el servicio `Plataforma-core-ohs` (o su mock),
Para ofrecer opciones actualizadas en la definición de coberturas de la cotización.

**Criterios de Aceptación**:
- Dado que el usuario está configurando coberturas, cuando el sistema necesita el catálogo de riesgo, entonces lo consulta al servicio externo y lo muestra.
- Dado que el catálogo de riesgo se carga exitosamente, cuando el usuario selecciona una clasificación, entonces esta se asocia correctamente a la ubicación o cobertura.
- Dado que la recuperación del catálogo de riesgo falla, cuando el sistema lo intenta, entonces se registra el error y se muestra un mensaje adecuado al usuario.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068 (Conectividad básica)
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Capa de mapeo de datos
- Repositorio de catálogos

**Notas de Implementación**:
El catálogo puede influir en las primas y condiciones de suscripción.

**Estado**: Backlog

---
### HU-078: Recuperar Catálogo de Garantías

**Descripción**:
Como sistema,
Quiero recuperar el catálogo de garantías desde el servicio `Plataforma-core-ohs` (o su mock),
Para ofrecer opciones actualizadas y completas en la configuración de coberturas de la cotización.

**Criterios de Aceptación**:
- Dado que el usuario está definiendo las coberturas, cuando el sistema necesita el catálogo de garantías, entonces lo consulta y lo presenta en la interfaz de usuario.
- Dado que el catálogo de garantías se carga, cuando el usuario selecciona una o varias garantías, entonces estas se asocian a la cobertura.
- Dado que el servicio devuelve un catálogo de garantías vacío, cuando el sistema lo procesa, entonces no se muestran opciones de garantía al usuario.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068 (Conectividad básica)
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Capa de mapeo de datos
- Repositorio de catálogos

**Notas de Implementación**:
Las garantías pueden tener reglas de negocio complejas para su combinación o exclusión.

**Estado**: Backlog

---
### HU-079: Mapear Datos de Clasificación de Riesgo y Garantías

**Descripción**:
Como sistema,
Quiero mapear los datos de clasificación de riesgo y garantías del servicio externo a mi modelo interno,
Para su correcta utilización en la lógica de negocio y la interfaz de usuario.

**Criterios de Aceptación**:
- Dado que se reciben datos de clasificación de riesgo, cuando el sistema los mapea, entonces se transforman al formato del modelo de datos interno.
- Dado que se reciben datos de garantías, cuando el sistema los mapea, entonces se transforman al formato del modelo de datos interno, incluyendo atributos como ID, nombre y descripción.
- Dado que el mapeo es exitoso, cuando los datos se utilizan, entonces los valores son consistentes con las expectativas de la lógica de negocio.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-077, HU-078

**Componentes Técnicos**:
- Capa de mapeo de datos
- Modelos de datos internos

**Notas de Implementación**:
Asegurar que los identificadores de riesgo y garantía sean únicos y estables.

**Estado**: Backlog

---
## FT-018: Conectividad y Consumo de Tarifas y Factores Técnicos

### HU-080: Consultar Tarifas de Incendio

**Descripción**:
Como sistema,Quiero consultar las tarifas de incendio desde el servicio `Plataforma-core-ohs` (o su mock),
Para realizar el cálculo preciso de las primas de incendio.

**Criterios de Aceptación**:
- Dado que se requiere el cálculo de prima de incendio, cuando el sistema consulta las tarifas, entonces recibe la información de tasas y factores relevantes.
- Dado que la consulta de tarifas de incendio es exitosa, cuando los datos se reciben, entonces están disponibles para el módulo de cálculo.
- Dado que el servicio de tarifas de incendio no responde, cuando el sistema lo consulta, entonces se aplica la estrategia de reintento y se registra el fallo si persiste.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068 (Conectividad básica)
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de tarifas
- Repositorio de tarifas

**Notas de Implementación**:
La estructura de la respuesta de tarifas debe estar bien definida y documentada.

**Estado**: Backlog

---
### HU-081: Consultar Tarifas CAT

**Descripción**:
Como sistema,
Quiero consultar las tarifas CAT (Catástrofes) desde el servicio `Plataforma-core-ohs` (o su mock),
Para realizar el cálculo preciso de las primas por ubicación según la zona CAT.

**Criterios de Aceptación**:
- Dado que se requiere el cálculo de prima CAT, cuando el sistema consulta las tarifas con la zona CAT de la ubicación, entonces recibe el factor CAT correspondiente.
- Dado que la zona CAT de una ubicación es "Zona A", cuando el sistema consulta, entonces obtiene el factor CAT específico para "Zona A".
- Dado que el servicio devuelve un factor CAT nulo o inválido, cuando el sistema lo procesa, entonces se utiliza un valor por defecto o se registra una alerta.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068, HU-076

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de tarifas
- Repositorio de tarifas

**Notas de Implementación**:
La dependencia de la zona CAT de la ubicación es crucial para esta consulta.

**Estado**: Backlog

---
### HU-082: Consultar Tarifas FHM

**Descripción**:
Como sistema,
Quiero consultar las tarifas FHM (Fenómenos Hidrometeorológicos) desde el servicio `Plataforma-core-ohs` (o su mock),
Para realizar el cálculo preciso de las primas por ubicación.

**Criterios de Aceptación**:
- Dado que se requiere el cálculo de prima FHM, cuando el sistema consulta las tarifas, entonces recibe las cuotas FHM por grupo, zona y condición.
- Dado que el servicio de tarifas FHM está disponible, cuando el sistema lo consulta, entonces la respuesta se recibe en un tiempo aceptable.
- Dado que se produce un error en la consulta de tarifas FHM, cuando el sistema lo detecta, entonces notifica al módulo de cálculo y registra el evento.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de tarifas
- Repositorio de tarifas

**Notas de Implementación**:
La complejidad de las tarifas FHM puede requerir un mapeo detallado.

**Estado**: Backlog

---
### HU-083: Consultar Factores Técnicos de Equipo Electrónico

**Descripción**:
Como sistema,
Quiero consultar los factores técnicos para equipo electrónico desde el servicio `Plataforma-core-ohs` (o su mock),
Para aplicar los cálculos específicos para este tipo de cobertura.

**Criterios de Aceptación**:
- Dado que se está cotizando equipo electrónico, cuando el sistema necesita los factores técnicos, entonces consulta el servicio externo.
- Dado que la consulta de factores técnicos es exitosa, cuando los datos se reciben, entonces el módulo de cálculo puede aplicarlos.
- Dado que el servicio no devuelve factores para una clase específica de equipo electrónico, cuando el sistema lo procesa, entonces se utiliza un factor por defecto o se marca una alerta.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de factores
- Repositorio de factores

**Notas de Implementación**:
Los factores pueden variar según la clase y el nivel de zona del equipo.

**Estado**: Backlog

---
### HU-084: Mapear Tarifas y Factores Técnicos

**Descripción**:
Como sistema,
Quiero mapear las tarifas (incendio, CAT, FHM) y factores técnicos a mi modelo interno,
Para su correcta aplicación en la lógica de cálculo de primas.

**Criterios de Aceptación**:
- Dado que se reciben datos de tarifas, cuando el sistema los procesa, entonces se transforman al formato esperado por el módulo de cálculo.
- Dado que se reciben datos de factores técnicos, cuando el sistema los procesa, entonces se transforman al formato esperado por el módulo de cálculo.
- Dado que el mapeo es exitoso, cuando el módulo de cálculo solicita una tarifa, entonces recibe un objeto con los atributos correctos.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-080, HU-081, HU-082, HU-083

**Componentes Técnicos**:
- Capa de mapeo de datos
- Modelos de datos de tarifas/factores

**Notas de Implementación**:
La complejidad de las tarifas puede requerir un modelo de datos interno flexible.

**Estado**: Backlog

---
### HU-085: Manejar Errores en Consulta de Tarifas y Factores Técnicos

**Descripción**:
Como sistema,
Quiero manejar errores de conexión o la ausencia/inconsistencia de datos al consultar tarifas y factores técnicos,
Para notificar al sistema o usuario y asegurar la integridad del cálculo.

**Criterios de Aceptación**:
- Dado que el servicio de tarifas devuelve un error de validación, cuando el sistema lo recibe, entonces registra el error y notifica al módulo de cálculo que no se pudieron obtener las tarifas.
- Dado que el servicio de tarifas está inaccesible, cuando el sistema lo consulta, entonces se aplica el mecanismo de reintento y, si falla, se informa al usuario.
- Dado que se detecta una inconsistencia crítica en los datos de tarifas recibidos, cuando el sistema los procesa, entonces se impide el cálculo y se registra la inconsistencia.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia
**Dependencias**:
- HU-080, HU-081, HU-082, HU-083

**Componentes Técnicos**:
- Módulo de manejo de excepciones
- Estrategia de reintentos
- Servicio de logging/notificación

**Notas de Implementación**:
Es crucial definir qué errores son críticos y detienen el cálculo, y cuáles pueden tener un fallback.

**Estado**: Backlog---
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
## FT-020: Simulación de Servicio `Plataforma-core-ohs` (Mock Server)

### HU-092: Configurar Mock Server Base

**Descripción**:
Como desarrollador,
Quiero un mock server operativo y accesible para el cotizador,
Para simular las respuestas del servicio `Plataforma-core-ohs` y desarrollar de forma independiente.

**Criterios de Aceptación**:
- Dado que inicio el mock server, cuando accedo a su URL base, entonces recibo una respuesta indicando que está activo.
- Dado que el mock server está configurado, cuando el cotizador intenta conectarse, entonces la conexión se establece correctamente.
- Dado que la configuración del mock server es flexible, cuando necesito cambiar un puerto o una URL, entonces puedo hacerlo fácilmente.

**Prioridad**: Crítica

**Estimación**: 3 puntos de historia

**Dependencias**:
- Ninguna

**Componentes Técnicos**:
- Framework de mock server (ej. WireMock, Mountebank)
- Contenedor (ej. Docker)

**Notas de Implementación**:
Debe ser fácil de desplegar y configurar en entornos de desarrollo local y CI.

**Estado**: Backlog

---
### HU-093: Simular Endpoints de Catálogos Básicos

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para catálogos de suscriptores, agentes y giros,
Para que el cotizador pueda consumirlos y probar la funcionalidad de selección de catálogos.
**Criterios de Aceptación**:
- Dado que el cotizador solicita el catálogo de suscriptores, cuando el mock server lo recibe, entonces devuelve una lista de suscriptores predefinida.
- Dado que el cotizador solicita el catálogo de agentes, cuando el mock server lo recibe, entonces devuelve una lista de agentes predefinida.
- Dado que el cotizador solicita el catálogo de giros, cuando el mock server lo recibe, entonces devuelve una lista de giros predefinida.
- Dado que el mock server simula un error en la obtención de un catálogo, cuando el cotizador lo consulta, entonces recibe el código de error esperado.

**Prioridad**: Crítica

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para catálogos

**Notas de Implementación**:Los datos de prueba deben ser representativos y variados.

**Estado**: Backlog

---
### HU-094: Simular Endpoints de Códigos Postales y Zonas

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para códigos postales y sus zonas (CAT, nivel técnico),
Para probar la lógica de validación y aplicación de tarifas por ubicación en el cotizador.

**Criterios de Aceptación**:
- Dado que el cotizador consulta un código postal válido, cuando el mock server lo recibe, entonces devuelve la información de zona CAT y nivel técnico asociada.
- Dado que el cotizador consulta un código postal no existente, cuando el mock server lo recibe, entonces devuelve una respuesta indicando que no se encontró el CP.
- Dado que se simula una falla de servicio, cuando el cotizador consulta un CP, entonces recibe un error HTTP (ej. 500).
**Prioridad**: Crítica

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para CP y zonas

**Notas de Implementación**:
Incluir casos de éxito, no encontrado y errores de servicio.

**Estado**: Backlog

---
### HU-095: Simular Endpoints de Clasificación de Riesgo y Garantías

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para catálogos de clasificación de riesgo y garantías,
Para probar la configuración de coberturas y la evaluación del riesgo asociado.

**Criterios de Aceptación**:
- Dado que el cotizador solicita el catálogo de clasificación de riesgo, cuando el mock server lo recibe, entonces devuelve una lista predefinida de clasificaciones.
- Dado que el cotizador solicita el catálogo de garantías, cuando el mock server lo recibe, entonces devuelve una lista predefinida de garantías.
- Dado que se simula un catálogo vacío, cuando el cotizador lo consulta, entonces recibe una respuesta con una lista vacía.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para riesgo y garantías

**Notas de Implementación**:
Asegurar que los IDs y nombres sean consistentes con los esperados por el cotizador.

**Estado**: Backlog

---
### HU-096: Simular Endpoints de Tarifas y Factores Técnicos

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para tarifas (incendio, CAT, FHM) y factores técnicos,Para probar la lógica de cálculo de primas en el cotizador con datos consistentes.

**Criterios de Aceptación**:
- Dado que el cotizador solicita tarifas de incendio, cuando el mock server lo recibe, entonces devuelve las tasas y factores esperados.
- Dado que el cotizador solicita tarifas CAT para una zona específica, cuando el mock server lo recibe, entonces devuelve el factor CAT correspondiente.
- Dado que el cotizador solicita factores técnicos de equipo electrónico, cuando el mock server lo recibe, entonces devuelve los factores por clase y nivel de zona.
- Dado que se simula una tarifa no encontrada, cuando el cotizador la consulta, entonces recibe una respuesta que indica su ausencia.

**Prioridad**: Crítica

**Estimación**: 5 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para tarifas y factores
**Notas de Implementación**:
Los datos de tarifas pueden ser complejos; el mock debe poder manejar diferentes escenarios de consulta.

**Estado**: Backlog

---
### HU-097: Poblar Base de Datos del Mock con Migraciones

**Descripción**:
Como desarrollador,
Quiero que la base de datos (MongoDB) del mock server se pueble y actualice mediante migraciones (Flyway),
Para mantener datos de prueba consistentes y versionados a lo largo del evolución del proyecto.

**Criterios de Aceptación**:
- Dado que inicio el mock server con una base de datos vacía, cuando se ejecuta, entonces las migraciones (Flyway) se aplican y la DB se puebla con datos iniciales.
- Dado que se añade una nueva migración con datos actualizados, cuando el mock server se reinicia, entonces la DB se actualiza a la nueva versión de datos.
- Dado que los datos de prueba son versionados, cuando el mock server se usa en diferentes ramas de desarrollo, entonces cada rama puede tener su conjunto de datos consistente.

**Prioridad**: Crítica

**Estimación**: 5 puntos de historia
**Dependencias**:
- HU-092

**Componentes Técnicos**:
- MongoDB
- Flyway (o similar para NoSQL)
- Scripts de datos de prueba

**Notas de Implementación**:
Definir un esquema de versionado claro para las migraciones de datos.

**Estado**: Backlog

---
### HU-098: Configurar Respuestas Dinámicas y Errores en el Mock

**Descripción**:
Como desarrollador,
Quiero poder configurar respuestas dinámicas y escenarios de error controlados en el mock server,
Para facilitar pruebas de resiliencia y diferentes comportamientos del servicio externo.

**Criterios de Aceptación**:
- Dado que el mock server está configurado, cuando el cotizador consulta un catálogo, entonces puedo configurar que devuelva una respuesta con un retraso específico.
- Dado que quiero probar un escenario de fallo, cuando configuro el mock server, entonces puedo hacer que un endpoint devuelva un error HTTP 500.
- Dado que necesito probar la validación de datos, cuando configuro el mock server, entonces puedo hacer que devuelva datos malformados para un catálogo específico.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server (capacidades de escenarios y retrasos)

**Notas de Implementación**:
La interfaz del mock server debe permitir una fácil configuración de estos escenarios.

**Estado**: Backlog

---
### HU-099: Validar Estabilidad del Mock Server

**Descripción**:
Como desarrollador,
Quiero realizar pruebas de carga simuladas en el mock server,
Para validar su estabilidad y disponibilidad bajo diferentes niveles de concurrencia y asegurar que es un reemplazo fiable.

**Criterios de Aceptación**:
- Dado que el mock server está operativo, cuando se ejecutan pruebas de carga con N solicitudes por segundo, entonces el mock server responde consistentemente sin caídas.
- Dado que el mock server está bajo carga, cuando el cotizador lo consulta, entonces los tiempos de respuesta del mock se mantienen dentro de los límites aceptables.
- Dado que se detectan problemas de rendimiento o estabilidad durante las pruebas de carga, cuando el mock server se analiza, entonces se identifican y corrigen los cuellos de botella.

**Prioridad**: Media

**Estimación**: 5 puntos de historia

**Dependencias**:
- HU-092, HU-093, HU-094, HU-095, HU-096

**Componentes Técnicos**:
- Herramienta de pruebas de carga (ej. JMeter, Gatling)
- Entorno de ejecución del mock server

**Notas de Implementación**:
Las pruebas de carga deben reflejar el uso esperado del mock por el cotizador.

**Estado**: Backlog

---
## FT-021: Capa de Validación y Gestión de Inconsistencias de Datos Maestros
### HU-100: Implementar Reglas de Validación de Datos Maestros

**Descripción**:
Como sistema,
Quiero implementar reglas de validación para los datos maestros clave (catálogos, tarifas, folios) recibidos de `Plataforma-core-ohs`,
Para asegurar su consistencia, formato correcto y conformidad con las expectativas del negocio.

**Criterios de Aceptación**:
- Dado que se recibe un catálogo de suscriptores, cuando el sistema lo procesa, entonces valida que los campos obligatorios (ej. ID, nombre) no sean nulos.
- Dado que se recibe una tarifa CAT, cuando el sistema la procesa, entonces valida que el factor sea un número positivo.
- Dado que se recibe un código postal, cuando el sistema lo procesa, entonces valida su formato y longitud.
- Dado que un dato no cumple las reglas de validación, cuando el sistema lo detecta, entonces marca el dato como inconsistente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- FT-015, FT-016, FT-017, FT-018

**Componentes Técnicos**:
- Módulo de validación de datos
- Reglas de negocio para validación

**Notas de Implementación**:
Las reglas deben ser configurables y extensibles.

**Estado**: Backlog

---
### HU-101: Registrar Inconsistencias Detectadas

**Descripción**:
Como sistema,
Quiero registrar las inconsistencias de datos detectadas en un log o repositorio específico con detalles suficientes,Para su análisis, trazabilidad y posterior resolución.

**Criterios de Aceptación**:
- Dado que se detecta una inconsistencia, cuando el sistema la registra, entonces incluye el tipo de inconsistencia, el dato afectado, la fecha y la fuente.
- Dado que se registra una inconsistencia, cuando el sistema lo hace, entonces el registro es persistente y accesible para los administradores.
- Dado que hay múltiples inconsistencias en una misma carga de datos, cuando el sistema las registra, entonces cada una se anota individualmente.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-100

**Componentes Técnicos**:
- Servicio de logging/repositorio de inconsistencias
- Base de datos (MongoDB u otra)

**Notas de Implementación**:
El formato del log debe ser estructurado para facilitar el análisis.

**Estado**: Backlog

---
### HU-102: Aplicar Corrección Automática de Inconsistencias

**Descripción**:
Como sistema,
Quiero poder aplicar reglas de corrección automática para tipos de inconsistencias predefinidos,
Para mantener la calidad del dato sin intervención manual en casos simples.

**Criterios de Aceptación**:
- Dado que se detecta una inconsistencia de formato menor (ej. espacios extra), cuando el sistema la procesa, entonces aplica una regla de limpieza y corrige el dato automáticamente.
- Dado que se detecta un dato nulo en un campo opcional, cuando el sistema lo procesa, entonces le asigna un valor por defecto predefinido.
- Dado que se aplica una corrección automática, cuando el sistema lo hace, entonces registra que se realizó una corrección y el valor original.

**Prioridad**: Media

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-100, HU-101

**Componentes Técnicos**:
- Módulo de corrección de datos
- Reglas de negocio para corrección

**Notas de Implementación**:
Las reglas de corrección automática deben ser conservadoras para evitar introducir nuevos errores.

**Estado**: Backlog

---
### HU-103: Notificar Inconsistencias que Requieren Intervención

**Descripción**:
Como sistema,
Quiero activar una notificación (ej. log, alerta, correo) cuando se detectan inconsistencias que requieren intervención manual,
Para asegurar su resolución oportuna y evitar que afecten el negocio.

**Criterios de Aceptación**:
- Dado que se detecta una inconsistencia crítica que no puede corregirse automáticamente, cuando el sistema lo detecta, entonces envía una alerta a los administradores.
- Dado que se envía una notificación, cuando el sistema lo hace, entonces incluye detalles de la inconsistencia y un enlace a su registro.
- Dado que un umbral de inconsistencias se supera, cuando el sistema lo detecta, entonces envía una alerta de alto nivel.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-100, HU-101, HU-102

**Componentes Técnicos**:
- Servicio de notificación (ej. email, Slack)
- Módulo de alertas

**Notas de Implementación**:
La configuración de los umbrales de alerta y los destinatarios debe ser flexible.

**Estado**: Backlog

---
### HU-104: Definir Reglas de Validación con Analistas Funcionales

**Descripción**:
Como analista,
Quiero definir las reglas de validación de datos maestros en conjunto con los analistas funcionales,
Para asegurar que cubren los casos de negocio y las expectativas de calidad del dato.

**Criterios de Aceptación**:
- Dado que se definen los contratos de API, cuando los analistas funcionales revisan los datos, entonces especifican las reglas de validación para cada campo relevante.
- Dado que se identifican posibles inconsistencias, cuando los analistas funcionales las revisan, entonces definen si se corrigen automáticamente o requieren notificación.
- Dado que las reglas de validación están documentadas, cuando el equipo de desarrollo las implementa, entonces se asegura su correcta aplicación.

**Prioridad**: Alta

**Estimación**: 2 días

**Dependencias**:
- HU-100, HU-102

**Componentes Técnicos**:
- Documentación de reglas de negocio
- Herramientas de colaboración

**Notas de Implementación**:
Este es un paso de definición que impacta la implementación técnica.

**Estado**: Backlog

---
## FT-022: Gestión de Caché y Estrategia de Actualización de Datos Maestros

### HU-105: Almacenar Datos Maestros en Caché

**Descripción**:
Como sistema,
Quiero almacenar los datos maestros clave (catálogos, tarifas) en caché,
Para optimizar el acceso, reducir los tiempos de respuesta y disminuir la carga sobre los servicios externos.

**Criterios de Aceptación**:
- Dado que se consulta un catálogo por primera vez, cuando el sistema lo obtiene, entonces lo guarda en caché.
- Dado que se consulta un catálogo que ya está en caché, cuando el sistema lo solicita, entonces lo recupera directamente de la caché sin llamar al servicio externo.
- Dado que la caché se utiliza, cuando el sistema lo hace, entonces el tiempo de respuesta para datos en caché es significativamente menor que la consulta directa.

**Prioridad**: Alta
**Estimación**: 4 puntos de historia

**Dependencias**:
- FT-015, FT-016, FT-017, FT-018

**Componentes Técnicos**:
- Framework de caché (ej. Caffeine, Redis)
- Repositorios de datos maestros

**Notas de Implementación**:
Elegir la solución de caché adecuada (en memoria o distribuida) según los requisitos de escalabilidad y persistencia.

**Estado**: Backlog

---
### HU-106: Configurar Política de Invalidación de Caché por TTL

**Descripción**:
Como sistema,
Quiero configurar una política de invalidación de caché basada en tiempo (TTL - Time To Live),
Para asegurar que los datos en caché estén frescos y consistentes sin sobrecargar la fuente original.

**Criterios de Aceptación**:
- Dado que un dato maestro se guarda en caché, cuando el sistema lo hace, entonces se le asigna un tiempo de vida (TTL) configurable.
- Dado que el TTL de un dato en caché expira, cuando el sistema lo consulta, entonces el dato se considera inválido y se consulta la fuente original.
- Dado que el TTL se configura para diferentes catálogos, cuando el sistema lo aplica, entonces cada catálogo tiene su propia política de frescura.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-105

**Componentes Técnicos**:
- Configuración del framework de caché
- Políticas de caché por tipo de dato

**Notas de Implementación**:
Los TTLs deben ser definidos en conjunto con los dueños de los datos para reflejar su frecuencia de actualización.

**Estado**: Backlog

---
### HU-107: Implementar Mecanismo de Actualización Programada de Caché

**Descripción**:
Como sistema,
Quiero implementar un mecanismo de actualización programada de la caché para datos maestros clave,
Para refrescar los datos periódicamente y asegurar su disponibilidad y frescura.

**Criterios de Aceptación**:
- Dado que se configura una tarea programada, cuando llega el momento de ejecución, entonces el sistema inicia la actualización de los catálogos en caché.
- Dado que la actualización programada se ejecuta, cuando el sistema lo hace, entonces consulta los servicios externos y refresca los datos en caché.
- Dado que la actualización programada falla, cuando el sistema lo detecta, entonces registra el error y notifica a los administradores.

**Prioridad**: Media

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-105, HU-106

**Componentes Técnicos**:
- Scheduler de tareas (ej. Spring Scheduler, Quartz)
- Módulo de sincronización de datos

**Notas de Implementación**:
Considerar cómo manejar las actualizaciones durante el horario de mayor uso para minimizar impacto.

**Estado**: Backlog

---
### HU-108: Implementar Invalidación de Caché Bajo Demanda

**Descripción**:
Como sistema,
Quiero poder invalidar la caché bajo demanda (ej. por evento de actualización o acción manual),
Para reflejar cambios urgentes en los datos maestros de forma inmediata.

**Criterios de Aceptación**:
- Dado que se produce un evento de actualización en el servicio `Plataforma-core-ohs`, cuando el sistema lo detecta (ej. webhook, mensaje de cola), entonces invalida la entrada de caché correspondiente.
- Dado que un administrador necesita refrescar un catálogo, cuando lo solicita (ej. vía API interna), entonces la caché de ese catálogo se invalida.
- Dado que la invalidación bajo demanda se ejecuta, cuando el sistema lo hace, entonces las siguientes consultas a ese dato maestro irán a la fuente original.

**Prioridad**: Media

**Estimación**: 4 puntos de historia
**Dependencias**:
- HU-105

**Componentes Técnicos**:
- API de gestión de caché
- Mecanismo de escucha de eventos (si aplica)

**Notas de Implementación**:
Asegurar que la invalidación sea granular para no afectar el rendimiento de otros datos en caché.

**Estado**: Backlog

---
### HU-109: Monitorear Rendimiento y Consistencia del Caché

**Descripción**:
Como desarrollador,
Quiero monitorear el rendimiento del caché y la consistencia de los datos,
Para asegurar que cumple con los SLAs de tiempo de respuesta y optimizar su configuración.

**Criterios de Aceptación**:
- Dado que el caché está en uso, cuando se monitorea el sistema, entonces se pueden ver métricas como hits, misses y tiempo de respuesta del caché.
- Dado que se detecta una baja tasa de hits o un alto tiempo de respuesta del caché, cuando se analiza, entonces se ajusta la configuración o estrategia.
- Dado que se sospecha de inconsistencias, cuando se realizan pruebas de consistencia, entonces se verifica que los datos en caché coinciden con la fuente original (dentro de la política de frescura).

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-105, HU-106, HU-107, HU-108

**Componentes Técnicos**:
- Herramientas de monitoreo (ej. Prometheus, Grafana)
- Métricas del framework de caché
- Pruebas de consistencia

**Notas de Implementación**:
Las métricas deben ser accesibles y claras para identificar problemas rápidamente.

**Estado**: Backlog

---

## FT-001: Creación y Edición de Datos Generales de la Cotización

### HU-110: Crear Nueva Cotización
**Descripción**:
Como usuario,
Quiero iniciar una nueva cotización con un folio único,
Para comenzar el proceso de registro de datos.

**Criterios de Aceptación**:
- Dado que estoy en la interfaz de creación de cotizaciones, cuando solicito una nueva, entonces se genera un folio único y me es asignado.
- Dado que el sistema genera un folio, cuando lo asigna, entonces la operación es idempotente y no crea duplicados.
- Dado que se crea una nueva cotización, cuando se guarda, entonces su estado inicial es "Borrador".

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-188 (Generación de Folios Alfanuméricos)

**Componentes Técnicos**: Frontend (Formulario de Creación), Backend (API de Cotizaciones), Servicio de Folios.

**Notas de Implementación**: La generación de folios debe ser robusta y manejar reintentos. El folio se mostrará al usuario inmediatamente.

**Estado**: Backlog

---
### HU-111: Cargar Cotización Existente
**Descripción**:
Como usuario,
Quiero abrir una cotización existente utilizando su folio,
Para revisar o continuar editando la información previamente guardada.

**Criterios de Aceptación**:
- Dado que estoy en la interfaz principal, cuando introduzco un folio válido, entonces se carga la cotización correspondiente con todos sus datos.
- Dado que introduzco un folio no existente, cuando intento cargar, entonces recibo un mensaje de error claro.
- Dado que la cotización se carga, cuando es exitoso, entonces el estado de la cotización se muestra correctamente.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: Ninguna

**Componentes Técnicos**: Frontend (Campo de búsqueda por folio), Backend (API de Consulta de Cotizaciones).

**Notas de Implementación**: La búsqueda debe ser eficiente y tolerante a mayúsculas/minúsculas si aplica.

**Estado**: Backlog

---
### HU-112: Editar Datos Generales de la Cotización
**Descripción**:
Como usuario,
Quiero modificar los datos generales de una cotización (Nombre Asegurado, RFC, Tipo de Seguro, Moneda, Vigencia, Canal de Venta),
Para mantener la información de la cotización actualizada y precisa.

**Criterios de Aceptación**:
- Dado que tengo una cotización abierta, cuando edito un campo de datos generales y guardo, entonces los cambios se persisten correctamente.
- Dado que modifico un campo, cuando guardo, entonces el campo `fechaUltimaActualizacion` se actualiza y el número de versión se incrementa.
- Dado que un campo de selección (e.g., Tipo de Seguro) tiene opciones de catálogo, cuando lo selecciono, entonces se valida contra el catálogo correspondiente.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-141 (Consumir Catálogos de Suscriptores, Agentes y Giros), HU-148 (Prevenir Sobrescritura con Versionado Optimista)

**Componentes Técnicos**: Frontend (Formulario de Datos Generales), Backend (API de Edición de Cotizaciones).

**Notas de Implementación**: Implementar validaciones de formato (e.g., RFC) y rangos (e.g., Vigencia).

**Estado**: Backlog

---
### HU-113: Seleccionar Opciones de Catálogos Básicos
**Descripción**:
Como usuario,
Quiero seleccionar opciones de catálogos básicos (Suscriptores, Agentes, Giros) en los campos correspondientes,
Para asegurar la consistencia de los datos y agilizar la entrada de información.

**Criterios de Aceptación**:
- Dado que estoy en un campo de selección de catálogo (e.g., Suscriptor), cuando hago clic, entonces se muestran opciones válidas obtenidas del servicio de referencia.
- Dado que selecciono una opción del catálogo, cuando guardo la cotización, entonces el ID o valor de la selección se persiste correctamente.
- Dado que el catálogo no está disponible, cuando intento seleccionar una opción, entonces el sistema me lo notifica y no me permite seleccionar un valor inválido.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-141 (Consumir Catálogos de Suscriptores, Agentes y Giros)

**Componentes Técnicos**: Frontend (Componentes de selección de catálogos), Backend (API de Consulta de Catálogos).

**Notas de Implementación**: Los catálogos deben ser consumidos de `Plataforma-core-ohs` o su simulación.

**Estado**: Backlog

---
## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

### HU-114: Agregar Nueva Ubicación de Riesgo

**Descripción**:
Como usuario,
Quiero añadir una nueva ubicación de riesgo a mi cotización,
Para especificar múltiples lugares de interés para el seguro con todos sus datos del dominio.

**Criterios de Aceptación**:
- Dado que tengo una cotización abierta, cuando hago clic en "Agregar Ubicación", entonces se presenta un formulario para capturar los datos del dominio de la ubicación: `nombreUbicacion`, `direccion`, `codigoPostal`, `estado`, `municipio`, `colonia`, `ciudad`, `tipoConstructivo`, `nivel`, `anioConstruccion`, `giro` (con `giro.claveIncendio`), `garantías[]`.
- Dado que he alcanzado el límite configurable de ubicaciones, cuando intento agregar una nueva, entonces el sistema me notifica que no puedo añadir más.
- Dado que agrego una ubicación, cuando guardo la cotización, entonces la nueva ubicación se persiste como parte de la cotización con un `índice` asignado y `estadoValidacion` calculado.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-112 (Editar Datos Generales de la Cotización), HU-152 (Validar Datos Específicos de Ubicación de Riesgo)

**Componentes Técnicos**: Frontend (Botón "Agregar Ubicación", Formulario de Ubicación con campos del dominio), Backend (API de Cotizaciones para agregar ubicaciones).

**Notas de Implementación**:
- El límite de ubicaciones debe ser configurable a nivel de sistema.
- Al guardar, el backend calcula automáticamente `estadoValidacion` y `alertasBloqueantes`.
- El campo `zonaCatastrofica` se obtiene automáticamente del catálogo CP al validar el código postal.

**Estado**: Backlog

---
### HU-115: Editar Detalles de Ubicación de Riesgo

**Descripción**:
Como usuario,
Quiero modificar los datos específicos de una ubicación de riesgo existente con todos sus campos del dominio,
Para asegurar la precisión de la evaluación del riesgo.

**Criterios de Aceptación**:
- Dado que tengo una cotización con ubicaciones, cuando selecciono una ubicación para editar, entonces sus datos se cargan en el formulario con todos los campos del dominio: `nombreUbicacion`, `direccion`, `codigoPostal`, `estado`, `municipio`, `colonia`, `ciudad`, `tipoConstructivo`, `nivel`, `anioConstruccion`, `giro` (con `giro.claveIncendio`), `garantías[]`, `zonaCatastrofica`.
- Dado que modifico los datos de una ubicación y guardo, entonces los cambios se persisten a través de `PATCH /v1/quotes/{folio}/locations/{índice}` y el `estadoValidacion` se recalcula.
- Dado que ingreso un código postal válido, cuando el sistema lo valida, entonces `zonaCatastrofica` se actualiza automáticamente con los datos de la zona CAT y nivel técnico.
- Dado que intento guardar datos inválidos (e.g., código postal incorrecto o sin garantías), cuando confirmo la edición, entonces el sistema actualiza `alertasBloqueantes` con los campos problemáticos.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-114 (Agregar Nueva Ubicación de Riesgo), HU-152 (Validar Datos Específicos de Ubicación de Riesgo)

**Componentes Técnicos**: Frontend (Formulario de Edición de Ubicación con todos los campos del dominio), Backend (PATCH de ubicaciones).

**Notas de Implementación**:
- Los campos `alertasBloqueantes` y `estadoValidacion` se recalculan automáticamente en el backend tras cada edición.
- El formulario debe mostrar visualmente cuáles campos son los `alertasBloqueantes` activos de esa ubicación.

**Estado**: Backlog

---

### HU-116: Marcar Ubicación de Riesgo como Inactiva

**Descripción**:
Como usuario,
Quiero marcar una ubicación de riesgo como inactiva en mi cotización,
Para excluirla del flujo activo y del cálculo sin eliminarla del histórico.

**Criterios de Aceptación**:
- Dado que tengo una cotización con ubicaciones, cuando selecciono una ubicación y confirmo marcarla como inactiva, entonces su `estadoValidacion` cambia a `INACTIVA`.
- Dado que una ubicación está marcada como `INACTIVA`, cuando se ejecuta el cálculo, entonces esa ubicación no se procesa ni se incluye en el resultado financiero.
- Dado que marco una ubicación como inactiva, cuando guardo, entonces el número de versión de la cotización se incrementa.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-115 (Editar Detalles de Ubicación)

**Componentes Técnicos**: Frontend (Opción "Marcar como Inactiva"), Backend (PATCH de ubicación con cambio de `estadoValidacion`).

**Notas de Implementación**:
- Las ubicaciones **no se eliminan físicamente** del documento en MongoDB, conforme al requisito del reto técnico.
- Se debe solicitar confirmación al usuario antes de marcar una ubicación como inactiva.

**Estado**: Backlog

---
### HU-117: Visualizar Múltiples Ubicaciones de Riesgo
**Descripción**:
Como usuario,
Quiero ver un resumen de todas las ubicaciones de riesgo en mi cotización,
Para tener una visión general de los riesgos asegurados y navegar entre ellas fácilmente.

**Criterios de Aceptación**:
- Dado que una cotización tiene múltiples ubicaciones, cuando la abro, entonces la interfaz muestra una lista o pestañas con cada ubicación.
- Dado que hago clic en una ubicación en la lista o pestaña, cuando navego, entonces se muestran los detalles completos de esa ubicación para edición.
- Dado que hay un límite de ubicaciones, cuando se alcanza, entonces la interfaz lo indica claramente.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-114 (Agregar Nueva Ubicación)

**Componentes Técnicos**: Frontend (Interfaz de Maestro-Detalle/Pestañas para Ubicaciones).

**Notas de Implementación**: Implementar un patrón de interfaz híbrido basado en maestro-detalle con navegación tipo pestañas.

**Estado**: Backlog

---
### HU-118: Validar Código Postal de Ubicación
**Descripción**:
Como usuario,
Quiero que el sistema valide el código postal de cada ubicación de riesgo,
Para asegurar que la dirección es válida y obtener la zonificación de riesgo correcta.

**Criterios de Aceptación**:
- Dado que ingreso un código postal en una ubicación, cuando el campo pierde el foco o se guarda, entonces el sistema valida el CP contra el `catalogo_cp_zonas`.
- Dado que ingreso un código postal válido, cuando se valida, entonces se muestran los datos de zona (CAT, nivel técnico) asociados a ese CP.
- Dado que ingreso un código postal inválido o no encontrado, cuando se valida, entonces el sistema muestra un mensaje de error claro y no permite guardar la ubicación con un CP erróneo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-142 (Consumir Datos de Códigos Postales y Zonas de Riesgo), HU-163 (Validar Códigos Postales Contra Catálogo)

**Componentes Técnicos**: Frontend (Campo de Código Postal), Backend (Servicio de Validación de CP).

**Notas de Implementación**: La validación puede ser asíncrona y en tiempo real.

**Estado**: Backlog

---
### HU-119: Recibir Alertas por Datos Incompletos de Ubicación
**Descripción**:
Como usuario,
Quiero recibir alertas visuales si una ubicación de riesgo tiene datos incompletos o inválidos,
Para saber qué información necesito completar antes de calcular la prima.

**Criterios de Aceptación**:
- Dado que una ubicación tiene campos obligatorios vacíos, cuando la visualizo, entonces se muestra una alerta visual (e.g., icono, color) indicando datos incompletos.
- Dado que una ubicación tiene datos inválidos (e.g., CP incorrecto), cuando la visualizo, entonces se muestra una alerta indicando la inconsistencia.
- Dado que completo o corrijo los datos de una ubicación, cuando guardo, entonces la alerta visual desaparece.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-115 (Editar Detalles de Ubicación de Riesgo), HU-152 (Validar Datos Específicos de Ubicación de Riesgo)

**Componentes Técnicos**: Frontend (Elementos de Alerta Visual en la Interfaz de Ubicaciones).

**Notas de Implementación**: Las alertas deben ser claras y no obstructivas, guiando al usuario a la acción.

**Estado**: Backlog

---
## FT-003: Configuración y Selección de Coberturas por Ubicación

### HU-120: Visualizar Catálogo de Coberturas por Ubicación
**Descripción**:
Como usuario,
Quiero ver el catálogo de coberturas disponibles para cada ubicación de riesgo y tipo de seguro,
Para seleccionar las protecciones adecuadas para mis clientes.

**Criterios de Aceptación**:
- Dado que estoy en la sección de coberturas de una ubicación, cuando accedo, entonces se muestra una lista de coberturas relevantes para el tipo de seguro de la cotización.
- Dado que el catálogo de coberturas tiene descripciones, cuando lo consulto, entonces puedo ver la información detallada de cada cobertura.
- Dado que no hay coberturas disponibles para el tipo de seguro, cuando accedo, entonces se muestra un mensaje informativo.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-115 (Editar Detalles de Ubicación de Riesgo), HU-143 (Consumir Catálogos de Clasificación de Riesgo y Garantías)

**Componentes Técnicos**: Frontend (Interfaz de Selección de Coberturas), Backend (API de Consulta de Coberturas).

**Notas de Implementación**: El catálogo de coberturas debe ser configurable y estar asociado a tipos de seguro.

**Estado**: Backlog

---
### HU-121: Seleccionar y Deseleccionar Coberturas por Ubicación
**Descripción**:
Como usuario,
Quiero poder seleccionar o deseleccionar coberturas específicas para cada ubicación de riesgo,
Para personalizar la protección ofrecida según las necesidades del cliente.

**Criterios de Aceptación**:
- Dado que visualizo el catálogo de coberturas, cuando selecciono una o varias, entonces se marcan como activas para la ubicación.
- Dado que una cobertura está seleccionada, cuando la deselecciono, entonces deja de estar activa para la ubicación.
- Dado que guardo la cotización, cuando se persisten los cambios, entonces las coberturas seleccionadas se asocian correctamente a la ubicación.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-120 (Visualizar Catálogo de Coberturas), HU-115 (Editar Detalles de Ubicación)
**Componentes Técnicos**: Frontend (Controles de Selección de Coberturas), Backend (API de Cotizaciones para gestión de coberturas).

**Notas de Implementación**: La selección de coberturas debe ser intuitiva (e.g., checkboxes, toggles).

**Estado**: Backlog

---
### HU-122: Configurar Parámetros Específicos de Cobertura
**Descripción**:
Como usuario,
Quiero configurar parámetros específicos para cada cobertura seleccionada (e.g., sumas aseguradas, deducibles),
Para ajustar con precisión el alcance y las condiciones de la protección.

**Criterios de Aceptación**:
- Dado que selecciono una cobertura que requiere parámetros, cuando la activo, entonces se habilitan los campos para configurar sus valores (e.g., suma asegurada, deducible).
- Dado que ingreso valores en los parámetros de cobertura y guardo, entonces estos valores se persisten junto con la cobertura y la ubicación.
- Dado que los parámetros tienen rangos de validación, cuando ingreso un valor fuera de rango, entonces el sistema me lo notifica con un mensaje de error.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**: HU-121 (Seleccionar y Deseleccionar Coberturas por Ubicación), HU-162 (Validar Rangos de Sumas Aseguradas)

**Componentes Técnicos**: Frontend (Campos de Entrada de Parámetros), Backend (API de Cotizaciones para persistencia de parámetros).

**Notas de Implementación**: La interfaz debe mostrar claramente qué parámetros son configurables para cada cobertura.

**Estado**: Backlog

---
### HU-123: Visualizar Coberturas Activas por Ubicación
**Descripción**:
Como usuario,
Quiero ver claramente qué coberturas están activas para cada ubicación de riesgo,
Para tener un resumen rápido de la protección configurada.

**Criterios de Aceptación**:
- Dado que he seleccionado coberturas para una ubicación, cuando la visualizo, entonces se muestra una indicación clara de las coberturas activas.
- Dado que una cobertura tiene parámetros configurados, cuando la visualizo, entonces se muestran también sus valores (e.g., suma asegurada, deducible).
- Dado que no hay coberturas activas para una ubicación, cuando la visualizo, entonces se muestra un mensaje indicando la ausencia de coberturas.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-121 (Seleccionar y Deseleccionar Coberturas)

**Componentes Técnicos**: Frontend (Elementos de Visualización de Coberturas Activas).

**Notas de Implementación**: La presentación debe ser concisa y fácil de entender.

**Estado**: Backlog

---
## FT-004: Ejecución y Persistencia del Cálculo de Primas

### HU-124: Iniciar Proceso de Cálculo de Prima

**Descripción**:
Como usuario,
Quiero solicitar el cálculo de la prima de mi cotización,
Para obtener los resultados financieros de las ubicaciones válidas.

**Criterios de Aceptación**:
- Dado que tengo al menos una ubicación con `estadoValidacion: COMPLETA`, cuando hago clic en el botón "Calcular Prima", entonces el sistema inicia el proceso de cálculo para las ubicaciones calculables.
- Dado que algunas ubicaciones tienen `estadoValidacion: INCOMPLETA`, cuando inicio el cálculo, entonces el sistema **continúa calculando las ubicaciones válidas** y muestra las alertas de las excluidas sin detener el proceso.
- Dado que **todas** las ubicaciones tienen `estadoValidacion: INCOMPLETA` o `INACTIVA` y no existe ninguna calculable, cuando intento calcular, entonces el sistema notifica que no hay ubicaciones válidas y no procede con el cálculo.
- Dado que el cálculo se inicia, cuando está en progreso, entonces la interfaz muestra un indicador de carga.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-166 (Prevenir Ejecución de Cálculo cuando no hay ubicaciones válidas), HU-165 (Proporcionar Mensajes de Error Específicos de Validación)

**Componentes Técnicos**: Frontend (Botón "Calcular Prima"), Backend (Endpoint `POST /v1/quotes/{folio}/calculate`).

**Notas de Implementación**:
- El botón solo se deshabilita completamente cuando no existe ninguna ubicación calculable.
- Las ubicaciones incompletas se excluyen del cálculo individualmente, no bloquean el proceso general.

**Estado**: Backlog

---
### HU-125: Calcular Prima Neta y Comercial Total
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima neta y comercial total de la cotización,
Para conocer el costo global del seguro.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo, cuando finaliza exitosamente, entonces el sistema calcula la prima neta total sumando las primas netas de las ubicaciones.
- Dado que se ha iniciado el cálculo, cuando finaliza exitosamente, entonces el sistema calcula la prima comercial total aplicando factores comerciales y otros recargos a la prima neta total.
- Dado que los cálculos se realizan, cuando se completan, entonces los resultados están disponibles para persistencia y visualización.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-126 (Calcular Prima por Ubicación de Riesgo), HU-169 (Calcular Prima Comercial Total)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas).

**Notas de Implementación**: Las fórmulas de cálculo deben ser las simplificadas y documentadas.

**Estado**: Backlog

---
### HU-126: Calcular Prima por Ubicación de Riesgo
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima para cada ubicación de riesgo individualmente,
Para entender el desglose del costo por cada lugar asegurado.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo, cuando finaliza exitosamente, entonces el sistema calcula la prima neta para cada ubicación de riesgo.
- Dado que la ubicación tiene coberturas y parámetros, cuando se calcula su prima, entonces se aplican las tarifas y factores técnicos correspondientes.
- Dado que se calcula la prima por ubicación, cuando se completa, entonces el resultado está disponible para persistencia y visualización.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-122 (Configurar Parámetros Específicos de Cobertura), HU-167 (Calcular Prima Neta por Ubicación)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas).

**Notas de Implementación**: Los factores técnicos (incendio, CAT, FHM, equipo electrónico) deben ser aplicados por ubicación.

**Estado**: Backlog

---
### HU-127: Persistir Resultados del Cálculo de Prima
**Descripción**:
Como usuario,
Quiero que los resultados del cálculo (prima neta, comercial, por ubicación) se guarden con la cotización,
Para que estén disponibles para consulta futura y no se pierdan.

**Criterios de Aceptación**:
- Dado que el cálculo de la prima ha finalizado exitosamente, cuando se guardan los resultados, entonces la prima neta total, comercial total y el desglose por ubicación se persisten en la cotización.
- Dado que los resultados se persisten, cuando se guarda la cotización, entonces la operación es atómica y consistente.
- Dado que los resultados se persisten, cuando se guarda la cotización, entonces el estado de la cotización cambia a "Calculada".

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-125 (Calcular Prima Neta y Comercial Total), HU-126 (Calcular Prima por Ubicación de Riesgo), HU-172 (Persistir Prima Neta y Comercial en Cotización)

**Componentes Técnicos**: Backend (API de Persistencia de Cotizaciones), Base de Datos (MongoDB).

**Notas de Implementación**: La persistencia debe incluir el versionado optimista.

**Estado**: Backlog

---
### HU-128: Aplicar Reglas de Negocio y Factores Técnicos en Cálculo
**Descripción**:
Como usuario,
Quiero que el cálculo de la prima considere las reglas de negocio y los factores técnicos definidos,
Para asegurar que la prima refleje correctamente el riesgo y las políticas de la compañía.

**Criterios de Aceptación**:
- Dado que se realiza un cálculo, cuando se aplican las reglas de negocio, entonces los recargos o descuentos se consideran en la prima final.
- Dado que se realiza un cálculo, cuando se aplican los factores técnicos (e.g., CAT, FHM), entonces el costo de la prima por ubicación se ajusta según estos factores.
- Dado que los factores técnicos son obtenidos de catálogos, cuando se aplican, entonces se utilizan los valores correctos y actualizados.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia
**Dependencias**: HU-168 (Aplicar Factores de Catástrofe (CAT) y FHM), HU-164 (Verificar Datos Mínimos Requeridos por Ubicación)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas, Módulo de Reglas de Negocio).

**Notas de Implementación**: La lógica de aplicación de reglas y factores debe ser modular y testeable.

**Estado**: Backlog

---
## FT-005: Visualización Detallada de Resultados Financieros

### HU-129: Visualizar Resumen de Prima Neta y Comercial
**Descripción**:
Como usuario,
Quiero ver un resumen claro de la prima neta y comercial total de mi cotización,
Para tener una comprensión rápida del costo global.

**Criterios de Aceptación**:
- Dado que una cotización ha sido calculada, cuando la visualizo, entonces se muestra la prima neta total y la prima comercial total en un área destacada.
- Dado que los valores de la prima son numéricos, cuando se muestran, entonces están formateados correctamente (e.g., moneda, decimales).
- Dado que la cotización no ha sido calculada, cuando la visualizo, entonces los campos de prima total están vacíos o indican "Pendiente de Cálculo".

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-127 (Persistir Resultados del Cálculo de Prima)

**Componentes Técnicos**: Frontend (Interfaz de Resultados Financieros).

**Notas de Implementación**: La información debe ser fácil de encontrar y leer.

**Estado**: Backlog

---
### HU-130: Visualizar Desglose de Prima por Ubicación
**Descripción**:
Como usuario,
Quiero ver el desglose de la prima calculada para cada ubicación de riesgo,
Para entender cómo se distribuye el costo total del seguro.

**Criterios de Aceptación**:
- Dado que una cotización ha sido calculada, cuando visualizo los resultados, entonces se muestra la prima asignada a cada ubicación de riesgo.
- Dado que selecciono una ubicación específica, cuando la visualizo, entonces puedo ver su prima individual en detalle.
- Dado que los valores de la prima por ubicación son numéricos, cuando se muestran, entonces están formateados correctamente.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-127 (Persistir Resultados del Cálculo de Prima)

**Componentes Técnicos**: Frontend (Interfaz de Resultados Financieros, Sección de Ubicaciones).

**Notas de Implementación**: La tabla o lista de ubicaciones debe incluir su prima correspondiente.

**Estado**: Backlog

---
### HU-131: Visualizar Componentes Adicionales de la Prima
**Descripción**:
Como usuario,
Quiero ver los componentes adicionales de la prima, como impuestos y recargos básicos,
Para entender la composición completa del precio final del seguro.

**Criterios de Aceptación**:
- Dado que una cotización ha sido calculada, cuando visualizo los resultados, entonces se muestran los impuestos y recargos básicos aplicados.
- Dado que los componentes adicionales son numéricos, cuando se muestran, entonces están formateados correctamente.
- Dado que no hay impuestos o recargos aplicables, cuando visualizo los resultados, entonces estos campos no se muestran o indican "N/A".

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-127 (Persistir Resultados del Cálculo de Prima)

**Componentes Técnicos**: Frontend (Interfaz de Resultados Financieros, Sección de Desglose).

**Notas de Implementación**: Solo se mostrarán los impuestos y recargos básicos definidos en el cálculo.

**Estado**: Backlog

---
### HU-132: Sincronizar Visualización de Resultados Financieros
**Descripción**:
Como usuario,
Quiero que los resultados financieros mostrados estén siempre sincronizados con el último cálculo realizado,
Para asegurar que la información es actual y precisa.

**Criterios de Aceptación**:
- Dado que se ha realizado un nuevo cálculo de prima, cuando accedo a la sección de resultados, entonces se muestran los resultados del cálculo más reciente.
- Dado que se han realizado modificaciones a la cotización (ubicaciones, coberturas) después de un cálculo, cuando visualizo los resultados, entonces se muestra una advertencia de que el cálculo puede estar desactualizado o se invalida el cálculo anterior.
- Dado que un cálculo falla, cuando accedo a los resultados, entonces se muestra un mensaje de error y no se muestran resultados desactualizados.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-127 (Persistir Resultados del Cálculo de Prima), HU-138 (Cualquier Modificación Invalida Cálculo)

**Componentes Técnicos**: Frontend (Lógica de Actualización de UI), Backend (API de Consulta de Cotizaciones).

**Notas de Implementación**: El sistema debe tener un mecanismo para invalidar o marcar como desactualizado un cálculo si los datos de la cotización cambian.

**Estado**: Backlog

---
## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

### HU-133: Cotización Inicia en Estado "Borrador"
**Descripción**:
Como usuario,
Quiero que cada nueva cotización comience automáticamente en el estado "Borrador",
Para indicar que aún está en proceso de creación y edición.

**Criterios de Aceptación**:
- Dado que creo una nueva cotización, cuando se guarda por primera vez, entonces su estado se establece como "Borrador".
- Dado que consulto una cotización recién creada, cuando visualizo su estado, entonces se muestra "Borrador".
- Dado que una cotización está en "Borrador", cuando se realizan modificaciones, entonces permanece en "Borrador" hasta que se inicie un cálculo.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**: HU-110 (Crear Nueva Cotización)

**Componentes Técnicos**: Backend (Lógica de Inicialización de Cotización).

**Notas de Implementación**: El estado "Borrador" es el estado inicial por defecto.

**Estado**: Backlog

---
### HU-134: Actualización Automática a Estado "Calculada"
**Descripción**:
Como usuario,
Quiero que el estado de la cotización se actualice automáticamente a "Calculada" tras una ejecución exitosa del cálculo,
Para reflejar que la información financiera está disponible y es válida.

**Criterios de Aceptación**:
- Dado que una cotización en estado "Borrador" se calcula exitosamente, cuando finaliza el proceso, entonces su estado cambia a "Calculada".
- Dado que el cálculo falla, cuando finaliza el proceso, entonces el estado de la cotización no cambia a "Calculada" y permanece en el estado anterior (e.g., "Borrador" o "Pendiente de Cálculo").
- Dado que el estado es "Calculada", cuando se visualiza, entonces indica que los resultados de prima están disponibles.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-127 (Persistir Resultados del Cálculo de Prima), HU-136 (No se puede calcular sin validaciones previas)

**Componentes Técnicos**: Backend (Lógica de Transición de Estados).

**Notas de Implementación**: La transición solo ocurre si todas las validaciones previas son exitosas.

**Estado**: Backlog

---
### HU-135: Cambiar Manualmente Estado a "Aprobada" o "Rechazada"
**Descripción**:
Como usuario,
Quiero poder cambiar manualmente el estado de una cotización a "Aprobada" o "Rechazada" desde "Calculada",
Para indicar el resultado de la negociación con el cliente.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Calculada", cuando selecciono "Aprobar", entonces su estado cambia a "Aprobada".
- Dado que una cotización está en estado "Calculada", cuando selecciono "Rechazar", entonces su estado cambia a "Rechazada".
- Dado que la cotización no está en estado "Calculada", cuando intento aprobar o rechazar, entonces el sistema me lo impide.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-134 (Actualización Automática a Estado "Calculada"), HU-137 (No se puede aprobar sin cálculo previo)

**Componentes Técnicos**: Frontend (Botones de Acción de Estado), Backend (API de Actualización de Estado).

**Notas de Implementación**: Estos cambios de estado son acciones manuales del usuario.

**Estado**: Backlog

---
### HU-136: No se puede calcular sin validaciones previas
**Descripción**:
Como usuario,
Quiero que el sistema me impida calcular una cotización si no cumple con las validaciones previas,
Para evitar cálculos erróneos y asegurar la calidad de los datos.

**Criterios de Aceptación**:
- Dado que una cotización tiene ubicaciones incompletas o inválidas, cuando intento calcular la prima, entonces el sistema me muestra los errores de validación y no procede.
- Dado que una cotización tiene coberturas no definidas o con parámetros erróneos, cuando intento calcular la prima, entonces el sistema me lo impide.
- Dado que todas las validaciones son exitosas, cuando intento calcular, entonces el cálculo procede sin impedimentos.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-166 (Prevenir Ejecución de Cálculo con Errores de Validación)

**Componentes Técnicos**: Backend (Motor de Validación de Reglas de Negocio), Frontend (Mensajes de Error).

**Notas de Implementación**: La validación debe ser exhaustiva antes de invocar el motor de cálculo.

**Estado**: Backlog

---
### HU-137: No se puede aprobar sin cálculo previo
**Descripción**:
Como usuario,
Quiero que el sistema me impida aprobar una cotización si no ha sido previamente calculada,
Para asegurar que solo se aprueban cotizaciones con información financiera validada.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Borrador" o "Pendiente de Cálculo", cuando intento cambiar su estado a "Aprobada", entonces el sistema me lo impide y muestra un mensaje de error.
- Dado que una cotización está en estado "Calculada", cuando intento cambiar su estado a "Aprobada", entonces la operación es exitosa.
- Dado que una cotización ha sido "Rechazada", cuando intento cambiar su estado a "Aprobada", entonces el sistema me lo impide.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-134 (Actualización Automática a Estado "Calculada"), HU-135 (Cambiar Manualmente Estado)

**Componentes Técnicos**: Backend (Lógica de Transición de Estados).

**Notas de Implementación**: Las reglas de transición de estado deben ser estrictas.

**Estado**: Backlog

---
### HU-138: Cualquier Modificación Invalida Cálculo
**Descripción**:
Como usuario,
Quiero que cualquier modificación en una cotización en estado "CALCULADA" o superior invalide el cálculo,
Para asegurar que los resultados financieros siempre correspondan a la información actual.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Calculada", cuando modifico datos generales o de una ubicación, entonces el estado de la cotización regresa a "Borrador" o "Pendiente de Cálculo".
- Dado que el estado regresa a "Borrador" o "Pendiente de Cálculo", cuando visualizo los resultados financieros, entonces se indica que están desactualizados o no disponibles.
- Dado que una cotización está en estado "Aprobada" o "Rechazada", cuando intento modificarla, entonces el sistema me lo permite pero regresa a un estado de borrador para recalculo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-112 (Editar Datos Generales), HU-115 (Editar Detalles de Ubicación)

**Componentes Técnicos**: Backend (Lógica de Transición de Estados y Validación).

**Notas de Implementación**: Esta regla es crucial para mantener la integridad de los datos financieros.

**Estado**: Backlog

---
### HU-139: Establecer Estado "Emitida"
**Descripción**:
Como usuario,
Quiero poder establecer el estado de una cotización como "Emitida" una vez que ha sido "Aprobada",
Para indicar que la póliza ha sido formalmente emitida y es un estado terminal.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Aprobada", cuando selecciono "Emitir", entonces su estado cambia a "Emitida".
- Dado que una cotización está en estado "Emitida", cuando intento modificarla, entonces el sistema me lo impide o me notifica que es un estado terminal.
- Dado que una cotización no está en estado "Aprobada", cuando intento cambiar su estado a "Emitida", entonces el sistema me lo impide.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-135 (Cambiar Manualmente Estado a "Aprobada" o "Rechazada")

**Componentes Técnicos**: Frontend (Botón "Emitir"), Backend (API de Actualización de Estado).

**Notas de Implementación**: "Emitida" debe ser un estado terminal sin transiciones de salida.

**Estado**: Backlog

---
### HU-140: Visualizar Estado Actual de la Cotización
**Descripción**:
Como usuario,
Quiero ver claramente el estado actual de la cotización,
Para tener un seguimiento visual del progreso del proceso de venta.

**Criterios de Aceptación**:
- Dado que tengo una cotización abierta, cuando la visualizo, entonces se muestra una etiqueta o indicador con su estado actual (e.g., Borrador, Calculada).
- Dado que el estado de la cotización cambia, cuando la consulto de nuevo, entonces la interfaz refleja el nuevo estado.
- Dado que el estado es importante, cuando se muestra, entonces es prominente y fácil de identificar.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**: Ninguna (es una funcionalidad de visualización)

**Componentes Técnicos**: Frontend (Elemento de Visualización de Estado).

**Notas de Implementación**: El estado puede ser representado con colores o iconos para mayor claridad.

**Estado**: Backlog

---
## FT-007: Integración con Servicios de Referencia (Catálogos y Tarifas)

### HU-141: Consumir Catálogos de Suscriptores, Agentes y Giros
**Descripción**:
Como sistema,
Quiero consumir los catálogos de suscriptores, agentes y giros desde `Plataforma-core-ohs` (o su simulación),
Para proveer opciones de selección actualizadas en la interfaz de datos generales.

**Criterios de Aceptación**:
- Dado que el sistema requiere los catálogos, cuando realiza la consulta, entonces obtiene los datos de suscriptores, agentes y giros del servicio externo.
- Dado que los datos son recibidos, cuando se procesan, entonces se mapean correctamente al modelo interno del cotizador.
- Dado que el servicio externo no responde, cuando se realiza la consulta, entonces el sistema maneja la excepción y registra el error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Clientes API REST para `Plataforma-core-ohs`), Capa de Integración.

**Notas de Implementación**: Considerar estrategia de caché para estos catálogos.

**Estado**: Backlog

---
## FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

### HU-142: Consumir Datos de Códigos Postales y Zonas de Riesgo
**Descripción**:
Como sistema,
Quiero consultar y validar información de códigos postales y sus zonas de riesgo desde `Plataforma-core-ohs` (o su simulación),
Para asegurar la correcta evaluación del riesgo por ubicación.

**Criterios de Aceptación**:
- Dado que el sistema requiere validar un código postal, cuando realiza la consulta, entonces obtiene la información de zona (CAT, nivel técnico) asociada al CP.
- Dado que el código postal es inválido o no encontrado en el catálogo, cuando se realiza la consulta, entonces el servicio devuelve una indicación de error.
- Dado que los datos de zona se reciben, cuando se procesan, entonces están disponibles para la lógica de cálculo de primas.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Clientes API REST para `Plataforma-core-ohs`), Servicio de Validación de CP.
**Notas de Implementación**: Optimizar la consulta de CP para grandes volúmenes.

**Estado**: Backlog

---
### HU-143: Consumir Catálogos de Clasificación de Riesgo y Garantías
**Descripción**:
Como sistema,
Quiero obtener los catálogos de clasificación de riesgo y garantías desde `Plataforma-core-ohs` (o su simulación),
Para permitir la configuración detallada de coberturas y la evaluación de riesgo.

**Criterios de Aceptación**:
- Dado que el sistema requiere los catálogos, cuando realiza la consulta, entonces obtiene los datos de clasificación de riesgo y garantías del servicio externo.
- Dado que los datos son recibidos, cuando se procesan, entonces se mapean correctamente al modelo interno del cotizador.
- Dado que los catálogos se actualizan en el sistema de origen, cuando se consultan, entonces los cambios se reflejan en el cotizador.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Clientes API REST para `Plataforma-core-ohs`), Capa de Integración.

**Notas de Implementación**: Estos catálogos suelen ser menos voluminosos y de actualización menos frecuente.

**Estado**: Backlog

---
### HU-144: Consumir Tarifas y Factores Técnicos
**Descripción**:
Como sistema,
Quiero consultar las tarifas (incendio, CAT, FHM) y factores técnicos (equipo electrónico) desde `Plataforma-core-ohs` (o su simulación),
Para utilizarlos en el cálculo preciso de las primas.

**Criterios de Aceptación**:
- Dado que el sistema requiere tarifas o factores, cuando realiza la consulta, entonces obtiene los datos necesarios del servicio externo.
- Dado que los datos son recibidos, cuando se procesan, entonces se mapean correctamente para ser utilizados en la lógica de cálculo.
- Dado que el servicio externo no responde o devuelve datos inconsistentes, cuando se realiza la consulta, entonces el sistema maneja el error y notifica.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Clientes API REST para `Plataforma-core-ohs`), Módulo de Adaptación de Tarifas.

**Notas de Implementación**: La estructura de tarifas y factores puede ser compleja y requiere mapeo cuidadoso.

**Estado**: Backlog

---
### HU-145: Manejo de Errores de Comunicación con Servicio Externo
**Descripción**:
Como sistema,
Quiero manejar posibles errores de comunicación con el servicio `Plataforma-core-ohs`,
Para asegurar la robustez de la integración y evitar fallos en el cotizador.

**Criterios de Aceptación**:
- Dado que `Plataforma-core-ohs` no está disponible, cuando el sistema intenta consultarlo, entonces se registra un error y se notifica al usuario o sistema.
- Dado que la respuesta de `Plataforma-core-ohs` es inconsistente, cuando el sistema la recibe, entonces se maneja la inconsistencia y se registra.
- Dado que hay un error de comunicación, cuando se intenta una operación dependiente, entonces el sistema puede reintentar la operación o usar datos en caché (si aplica).

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-141, HU-142, HU-143, HU-144

**Componentes Técnicos**: Capa de Integración Backend (Manejo de Excepciones, Circuit Breakers, Reintentos).

**Notas de Implementación**: Implementar patrones de resiliencia como Circuit Breaker y Retry.

**Estado**: Backlog

---
## FT-009: Implementación de Reglas de Negocio y Validaciones

### HU-146: Incrementar Versión en Ediciones de Cotización
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

**Dependencias**: Todas las HUs que modifican la cotización (ej. HU-112, HU-115)

**Componentes Técnicos**: Backend (Capa de Persistencia, Modelo de Datos de Cotización).

**Notas de Implementación**: La gestión del campo de versión debe ser automática y transparente para el usuario.

**Estado**: Backlog

---
### HU-147: Actualizar Fecha de Última Actualización en Ediciones
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

**Dependencias**: HU-146 (Incrementar Versión en Ediciones de Cotización)

**Componentes Técnicos**: Backend (Capa de Persistencia, Modelo de Datos de Cotización).

**Notas de Implementación**: La actualización de la fecha debe ser automática.

**Estado**: Backlog

---
### HU-148: Prevenir Sobrescritura con Versionado Optimista
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

**Dependencias**: HU-146 (Incrementar Versión en Ediciones de Cotización), HU-177 (Comparar Versiones de Cotización en Guardado)

**Componentes Técnicos**: Backend (Lógica de Control de Concurrencia en Persistencia).

**Notas de Implementación**: La estrategia específica para el versionado optimista será un número de versión incremental gestionado por el backend.

**Estado**: Backlog

---
### HU-149: Permitir Actualización Parcial de Campos
**Descripción**:
Como sistema,
Quiero permitir la actualización parcial de campos de la cotización sin afectar otros datos,Para optimizar las operaciones de guardado y reducir la carga de datos.

**Criterios de Aceptación**:
- Dado que un usuario modifica solo un subconjunto de campos de la cotización, cuando guarda, entonces solo esos campos modificados se actualizan en la base de datos.
- Dado que se realiza una actualización parcial, cuando se completa, entonces los campos no modificados permanecen intactos.
- Dado que una actualización parcial es exitosa, cuando se guarda, entonces el campo `fechaUltimaActualizacion` y la `version` se actualizan.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-146 (Incrementar Versión en Ediciones de Cotización)

**Componentes Técnicos**: Backend (API de Actualización Parcial, Capa de Persistencia).

**Notas de Implementación**: Utilizar DTOs específicos para actualizaciones o mapeo inteligente de campos.

**Estado**: Backlog

---
### HU-150: Persistencia Transaccional de Cotización y Ubicaciones
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

**Dependencias**: HU-114 (Agregar Nueva Ubicación), HU-116 (Eliminar Ubicación de Riesgo)

**Componentes Técnicos**: Backend (Capa de Persistencia, Transacciones en MongoDB).

**Notas de Implementación**: MongoDB no tiene transacciones ACID a nivel de múltiples documentos por defecto, por lo que se debe simular la atomicidad a nivel de agregado.

**Estado**: Backlog

---
## FT-010: Configuración y Gestión de Parámetros de Cálculo

### HU-151: Validar Datos Generales de la Cotización
**Descripción**:
Como sistema,
Quiero implementar reglas de validación para los datos generales de la cotización (ej., formato RFC, rangos de vigencia),
Para asegurar la consistencia y corrección de la información inicial.

**Criterios de Aceptación**:
- Dado que el usuario ingresa un RFC, cuando se valida, entonces se comprueba que cumple con el formato requerido.
- Dado que el usuario selecciona una vigencia, cuando se valida, entonces se comprueba que las fechas de inicio y fin son coherentes y dentro de rangos válidos.
- Dado que un campo obligatorio está vacío, cuando se guarda la cotización, entonces el sistema muestra un mensaje de error claro y no permite guardar.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-112 (Editar Datos Generales de la Cotización)

**Componentes Técnicos**: Backend (Módulo de Validación de Datos Generales).

**Notas de Implementación**: Las validaciones deben ejecutarse tanto en frontend como en backend.

**Estado**: Backlog

---
### HU-152: Validar Datos Específicos de Ubicación de Riesgo
**Descripción**:
Como sistema,
Quiero implementar reglas de validación para los datos específicos de cada ubicación de riesgo (ej., valor del bien, año de construcción),
Para asegurar la integridad de la información utilizada en la evaluación del riesgo.

**Criterios de Aceptación**:
- Dado que el usuario ingresa el valor de un bien, cuando se valida, entonces se comprueba que está dentro de los rangos permitidos.
- Dado que el usuario ingresa el año de construcción, cuando se valida, entonces se comprueba que es un año válido y consistente.
- Dado que un campo obligatorio de una ubicación está vacío, cuando se guarda la ubicación, entonces el sistema muestra un mensaje de error claro.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-115 (Editar Detalles de Ubicación de Riesgo)

**Componentes Técnicos**: Backend (Módulo de Validación de Datos de Ubicación).

**Notas de Implementación**: Las reglas de validación deben ser configurables y extensibles.

**Estado**: Backlog

---
### HU-153: Aplicar Lógica de Negocio en Cálculo de Primas
**Descripción**:
Como sistema,
Quiero que la lógica de cálculo de primas incorpore las reglas de negocio y factores técnicos definidos (ej., aplicación de recargos, descuentos),
Para asegurar que la prima final sea correcta y consistente con las políticas de suscripción.

**Criterios de Aceptación**:
- Dado que se cumplen ciertas condiciones (ej. tipo de cliente, canal de venta), cuando se calcula la prima, entonces se aplican los recargos o descuentos correspondientes.
- Dado que se utilizan factores técnicos, cuando se aplican en el cálculo, entonces se hace según las especificaciones de las tarifas.
- Dado que una regla de negocio impacta el cálculo, cuando se ejecuta, entonces el resultado final de la prima lo refleja.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-128 (Aplicar Reglas de Negocio y Factores Técnicos en Cálculo)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas, Módulo de Reglas de Negocio).

**Notas de Implementación**: La lógica de negocio debe ser trazable y documentada.

**Estado**: Backlog

---
### HU-154: Proporcionar Mensajes de Error Claros
**Descripción**:
Como sistema,
Quiero proporcionar mensajes de error claros y útiles cuando las validaciones fallan,
Para guiar al usuario en la corrección de los datos.

**Criterios de Aceptación**:
- Dado que una validación falla, cuando se muestra el error, entonces el mensaje indica claramente qué campo es erróneo y por qué.
- Dado que múltiples validaciones fallan, cuando se muestran los errores, entonces se presentan de forma organizada y fácil de entender.
- Dado que un error es crítico, cuando se muestra, entonces se destaca para la acción inmediata del usuario.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-151 (Validar Datos Generales), HU-152 (Validar Datos Específicos de Ubicación)

**Componentes Técnicos**: Frontend (Sistema de Notificaciones/Validaciones de UI), Backend (Servicio de Mensajes de Error).

**Notas de Implementación**: Los mensajes deben ser amigables y orientados a la solución.

**Estado**: Backlog

---
### HU-155: Asegurar Trazabilidad de Reglas de Negocio
**Descripción**:
Como sistema,
Quiero que las reglas de negocio implementadas sean trazables y documentadas,
Para facilitar la auditoría, mantenimiento y comprensión de la lógica del sistema.

**Criterios de Aceptación**:
- Dado que se implementa una regla de negocio, cuando se realiza, entonces existe documentación que describe su propósito, condiciones y efectos.
- Dado que se necesita auditar un cálculo, cuando se consulta, entonces es posible identificar qué reglas de negocio fueron aplicadas y con qué parámetros.
- Dado que se requiere modificar una regla, cuando se busca, entonces su implementación es fácilmente identificable en el código.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-153 (Aplicar Lógica de Negocio en Cálculo de Primas)

**Componentes Técnicos**: Backend (Módulo de Reglas de Negocio, Herramientas de Documentación).

**Notas de Implementación**: Considerar el uso de un motor de reglas o un enfoque basado en especificaciones.

**Estado**: Backlog

---
### HU-156: Consumir Tarifas de Incendio
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `tarifas_incendio` del servicio `Plataforma-core-ohs`,
Para obtener las tasas base necesarias para el cálculo de primas.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere tarifas de incendio, cuando las consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que las tarifas se reciben, cuando se procesan, entonces se mapean correctamente y están disponibles para el cálculo.
- Dado que el servicio de tarifas no está disponible, cuando se realiza la consulta, entonces el sistema maneja la excepción y registra el error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.
**Notas de Implementación**: La simulación debe ser fiel a los contratos de la API real.

**Estado**: Backlog

---
### HU-157: Consumir Tarifas de Catástrofe (CAT)
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `tarifas_cat` del servicio `Plataforma-core-ohs`,
Para aplicar los factores de catástrofe según la zona de riesgo.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere tarifas CAT, cuando las consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que las tarifas se reciben, cuando se procesan, entonces se mapean correctamente y están disponibles para el cálculo.
- Dado que se consulta una zona de riesgo, cuando se aplican las tarifas CAT, entonces se utiliza el factor correcto para esa zona.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.

**Notas de Implementación**: Se debe considerar cómo se relaciona la zona con la tarifa CAT.

**Estado**: Backlog

---
### HU-158: Consumir Tarifa FHM
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `tarifa_fhm` del servicio `Plataforma-core-ohs`,
Para aplicar las cuotas de Fenómenos Hidrometeorológicos (FHM) en el cálculo.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere tarifa FHM, cuando la consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que la tarifa se recibe, cuando se procesa, entonces se mapea correctamente y está disponible para el cálculo.
- Dado que se consulta una ubicación, cuando se aplica la tarifa FHM, entonces se utiliza la cuota correcta según el grupo, zona y condición.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.

**Notas de Implementación**: La tarifa FHM puede tener múltiples criterios de aplicación.

**Estado**: Backlog

---
### HU-159: Consumir Factores de Equipo Electrónico
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `factores_equipo_electronico` del servicio `Plataforma-core-ohs`,
Para aplicar el factor técnico de equipo electrónico en el cálculo de primas.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere factores de equipo electrónico, cuando los consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que los factores se reciben, cuando se procesan, entonces se mapean correctamente y están disponibles para el cálculo.
- Dado que se consulta una ubicación con equipo electrónico, cuando se aplica el factor, entonces se utiliza el valor correcto según la clase y nivel de zona.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.

**Notas de Implementación**: Definir cómo se clasifica el equipo electrónico y su relación con las zonas.

**Estado**: Backlog

---
### HU-160: Consumir Catálogo de Códigos Postales y Zonas (EP-002)
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `catalogo_cp_zonas` del servicio `Plataforma-core-ohs` y mapear zonas,
Para obtener la relación entre códigos postales y sus zonas (CAT, nivel técnico) para el cálculo.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere el catálogo CP-Zonas, cuando lo consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que el catálogo se recibe, cuando se procesa, entonces los códigos postales se mapean correctamente a sus zonas CAT y nivel técnico.
- Dado que se consulta un código postal, cuando se obtienen sus zonas, entonces la información es precisa y completa.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-092 (Configurar Mock Server Base, FT-020)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Mapeador de Datos.

**Notas de Implementación**: La gestión de versionado o fechas de vigencia para estos parámetros es una mejora futura.

**Estado**: Backlog

---
### HU-161: Parámetros Disponibles para Motores de Cálculo y Validación
**Descripción**:
Como sistema,
Quiero que todos los parámetros y tarifas cargados estén disponibles para el Motor de Validación y el Motor Central de Cálculo,
Para asegurar que ambos motores operan con la información más reciente y correcta.

**Criterios de Aceptación**:
- Dado que los parámetros y tarifas han sido cargados exitosamente, cuando el Motor de Validación los requiere, entonces tiene acceso a ellos.
- Dado que los parámetros y tarifas han sido cargados exitosamente, cuando el Motor Central de Cálculo los requiere, entonces tiene acceso a ellos.
- Dado que un parámetro se actualiza, cuando se consulta, entonces ambos motores reciben el valor actualizado.
**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**: HU-156, HU-157, HU-158, HU-159, HU-160

**Componentes Técnicos**: Backend (Repositorios de Parámetros, Servicios de Consulta de Parámetros).

**Notas de Implementación**: Implementar una interfaz común para acceder a los parámetros.

**Estado**: Backlog

---
## FT-011: Motor de Validación de Reglas de Negocio

### HU-162: Validar Rangos de Sumas Aseguradas
**Descripción**:
Como sistema,
Quiero validar que las sumas aseguradas de las coberturas estén dentro de los rangos predefinidos,
Para asegurar que los valores son coherentes con las políticas de suscripción.

**Criterios de Aceptación**:
- Dado que el usuario ingresa una suma asegurada, cuando se valida, entonces se comprueba que está entre el mínimo y el máximo permitido.
- Dado que la suma asegurada está fuera de rango, cuando se valida, entonces el sistema genera un error de validación claro.
- Dado que la suma asegurada está dentro de rango, cuando se valida, entonces no se genera ningún error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-122 (Configurar Parámetros Específicos de Cobertura), HU-161 (Parámetros Disponibles para Motores)

**Componentes Técnicos**: Backend (Módulo de Reglas de Validación).

**Notas de Implementación**: Los rangos deben ser configurables y accesibles desde los parámetros de cálculo.

**Estado**: Backlog

---
### HU-163: Validar Códigos Postales Contra Catálogo
**Descripción**:
Como sistema,
Quiero validar los códigos postales de las ubicaciones contra el `catalogo_cp_zonas` provisto,
Para asegurar que solo se usan códigos postales válidos y asociados a zonas de riesgo.

**Criterios de Aceptación**:
- Dado que una ubicación tiene un código postal, cuando se valida, entonces se verifica su existencia en el `catalogo_cp_zonas`.
- Dado que el código postal no se encuentra en el catálogo, cuando se valida, entonces el sistema genera un error de validación.
- Dado que el código postal es válido y existe en el catálogo, cuando se valida, entonces no se genera ningún error.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-118 (Validar Código Postal de Ubicación), HU-160 (Consumir Catálogo de Códigos Postales y Zonas)

**Componentes Técnicos**: Backend (Módulo de Reglas de Validación, Servicio de Consulta de Catálogos).

**Notas de Implementación**: La validación debe ser eficiente, especialmente con grandes catálogos de CP.

**Estado**: Backlog

---
### HU-164: Verificar Datos Mínimos Requeridos por Ubicación
**Descripción**:
Como sistema,
Quiero verificar que todas las ubicaciones tengan los datos mínimos requeridos para el cálculo,
Para asegurar que el motor de cálculo recibe información completa.

**Criterios de Aceptación**:
- Dado que una ubicación es procesada para cálculo, cuando se valida, entonces se comprueba que todos los campos obligatorios están completos.
- Dado que faltan datos mínimos en una ubicación, cuando se valida, entonces el sistema genera un error de validación específico para esa ubicación.
- Dado que todos los datos mínimos están presentes, cuando se valida, entonces no se genera ningún error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-115 (Editar Detalles de Ubicación de Riesgo)

**Componentes Técnicos**: Backend (Módulo de Reglas de Validación).

**Notas de Implementación**: La definición de "datos mínimos" debe ser clara y documentada.

**Estado**: Backlog

---
### HU-165: Proporcionar Mensajes de Error Específicos de Validación
**Descripción**:
Como sistema,
Quiero que el motor de validación proporcione mensajes de error claros y específicos para cada regla incumplida,
Para facilitar la identificación y corrección de los problemas por parte del usuario.

**Criterios de Aceptación**:
- Dado que una regla de validación falla, cuando se reporta el error, entonces el mensaje identifica la regla específica y el campo afectado.
- Dado que múltiples reglas fallan, cuando se reportan los errores, entonces se agrupan o listan de forma comprensible.
- Dado que un error es de alta prioridad, cuando se reporta, entonces se destaca visualmente en la interfaz de usuario.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-162, HU-163, HU-164

**Componentes Técnicos**: Backend (Componente de Reporte de Errores), Frontend (Interfaz de Mensajes de Error).

**Notas de Implementación**: Los mensajes deben ser orientados al usuario final.

**Estado**: Backlog

---
### HU-166: Prevenir Ejecución de Cálculo cuando No Existen Ubicaciones Válidas

**Descripción**:
Como sistema,
Quiero que el cálculo de prima no se ejecute únicamente cuando no existe ninguna ubicación calculable,
Para evitar calcular cotizaciones sin ningún riesgo válido asegurado.

**Criterios de Aceptación**:
- Dado que la cotización tiene al menos una ubicación con `estadoValidacion: COMPLETA`, cuando se inicia el cálculo, entonces el sistema **procede con el cálculo** para esas ubicaciones, ignorando las incompletas.
- Dado que una ubicación tiene `estadoValidacion: INCOMPLETA`, cuando el motor de cálculo la evalúa, entonces esa ubicación se **excluye individualmente** del cálculo y sus `alertasBloqueantes` se incluyen en el resultado como parte de `primasPorUbicacion[]` con estado `EXCLUIDA`.
- Dado que **todas** las ubicaciones de la cotización tienen `estadoValidacion: INCOMPLETA` o `INACTIVA`, cuando se intenta calcular, entonces el sistema impide el cálculo y notifica que no hay ubicaciones calculables.
- Dado que el cálculo procede con ubicaciones parcialmente excluidas, cuando finaliza, entonces el resultado incluye las primas de las ubicaciones válidas y las alertas de las excluidas en un mismo response.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-124 (Iniciar Proceso de Cálculo de Prima), HU-165 (Proporcionar Mensajes de Error Específicos de Validación)

**Componentes Técnicos**: Backend (Motor de Validación de Reglas de Negocio, Endpoint de Cálculo `POST /v1/quotes/{folio}/calculate`).

**Notas de Implementación**:
- Este comportamiento es el requisito explícito del reto: "si una ubicación está incompleta, genera alerta, pero no debe impedir calcular las demás."
- El motor de validación determina `estadoValidacion` de cada ubicación **antes** de iterar el motor de cálculo.
- Los criterios mínimos para que una ubicación sea calculable son: `codigoPostal` válido en el catálogo, `giro.claveIncendio` presente y al menos una garantía tarifable.

**Estado**: Backlog

---
## FT-012: Motor Central de Cálculo de Primas
### HU-167: Calcular Prima Neta por Ubicación

**Descripción**:
Como sistema,
Quiero calcular la prima neta para cada ubicación de riesgo con `estadoValidacion: COMPLETA` utilizando las tarifas y factores correspondientes,
Para determinar el costo base de la cobertura por cada lugar asegurado calculable.

**Criterios de Aceptación**:
- Dado que se proporciona una ubicación con `estadoValidacion: COMPLETA` y sus coberturas, cuando se ejecuta el cálculo, entonces se aplican los componentes técnicos activos de los 14 disponibles según las garantías y coberturas configuradas.
- Dado que la ubicación tiene `giro.claveIncendio` y garantías tarifables, cuando se calcula la prima, entonces se usan las tarifas de incendio correspondientes al giro como base del cálculo.
- Dado que una ubicación tiene `estadoValidacion: INCOMPLETA`, cuando el motor de cálculo la evalúa, entonces **la omite completamente** y registra la exclusión en el resultado.
- Dado que el cálculo es exitoso, cuando se completa, entonces la prima neta resultante es un valor numérico preciso con al menos dos decimales.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-126 (Calcular Prima por Ubicación de Riesgo), HU-161 (Parámetros Disponibles para Motores)

**Componentes Técnicos**: Backend (Algoritmos de Cálculo de Prima en el Motor de Cálculo).

**Notas de Implementación**:
- Solo se procesan ubicaciones con `estadoValidacion: COMPLETA`.
- El motor itera los 14 componentes técnicos pero solo aplica los activos según `opcionesCobertura` y `garantías[]`.
- Las fórmulas simplificadas deben ser implementadas con alta precisión y documentadas para trazabilidad.

**Estado**: Backlog

---
### HU-168: Aplicar Factores de Catástrofe (CAT) y FHM
**Descripción**:
Como sistema,
Quiero aplicar los factores de Catástrofe (CAT) y FHM según la zona y condiciones de la ubicación,
Para ajustar la prima neta por el riesgo específico de eventos catastróficos.

**Criterios de Aceptación**:
- Dado que una ubicación está en una zona CAT, cuando se calcula la prima, entonces el factor CAT correspondiente se aplica a la prima neta.
- Dado que una ubicación cumple las condiciones FHM, cuando se calcula la prima, entonces la cuota FHM correspondiente se aplica.
- Dado que los factores se aplican, cuando se completa el cálculo, entonces el resultado refleja el ajuste por CAT y FHM.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-147 (Calcular Prima Neta por Ubicación), HU-157 (Consumir Tarifas de Catástrofe (CAT)), HU-158 (Consumir Tarifa FHM)

**Componentes Técnicos**: Backend (Servicio de Aplicación de Factores en el Motor de Cálculo).

**Notas de Implementación**: La lógica de aplicación debe ser clara y basada en los catálogos de zonas.

**Estado**: Backlog

---
### HU-169: Calcular Prima Comercial Total
**Descripción**:
Como sistema,
Quiero calcular la prima comercial total de la cotización a partir de la suma de las primas netas y la aplicación de factores comerciales,
Para determinar el precio final que se presenta al cliente.

**Criterios de Aceptación**:
- Dado que se han calculado las primas netas por ubicación, cuando se ejecuta el cálculo, entonces se suman para obtener la prima neta total.
- Dado que la prima neta total se ha obtenido, cuando se calcula la prima comercial, entonces se aplican los factores comerciales (e.g., recargos administrativos, impuestos).
- Dado que el cálculo es exitoso, cuando se completa, entonces la prima comercial resultante es un valor numérico preciso.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia
**Dependencias**: HU-147 (Calcular Prima Neta por Ubicación)

**Componentes Técnicos**: Backend (Consolidación de Primas en el Motor de Cálculo).

**Notas de Implementación**: Los factores comerciales deben estar definidos y ser accesibles.

**Estado**: Backlog

---
### HU-170: Generar Desglose de Primas por Ubicación
**Descripción**:
Como sistema,
Quiero generar el desglose de primas por cada ubicación de riesgo,
Para proporcionar una vista detallada de cómo se compone el costo total.

**Criterios de Aceptación**:
- Dado que se han calculado las primas por ubicación, cuando se completa el proceso, entonces el desglose de primas por cada ubicación está disponible.
- Dado que el desglose se genera, cuando se almacena, entonces incluye la prima neta, y los ajustes por factores para cada ubicación.
- Dado que el desglose se genera, cuando se consulta, entonces la información es consistente con el cálculo total.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-147 (Calcular Prima Neta por Ubicación)

**Componentes Técnicos**: Backend (Consolidación de Primas en el Motor de Cálculo).

**Notas de Implementación**: El desglose debe ser granular y fácil de interpretar.

**Estado**: Backlog

---
### HU-171: Asegurar Precisión del Cálculo Según Fórmulas Simplificadas
**Descripción**:
Como sistema,
Quiero que los cálculos sean 100% precisos según las fórmulas simplificadas y documentadas,
Para garantizar la fiabilidad de los resultados financieros.

**Criterios de Aceptación**:
- Dado que se ejecuta un cálculo, cuando se compara el resultado con un cálculo manual basado en las fórmulas documentadas, entonces ambos coinciden.
- Dado que se modifican los parámetros de entrada, cuando se recalcula, entonces el resultado se ajusta de forma predecible según las fórmulas.
- Dado que se implementa una fórmula, cuando se prueba, entonces la cobertura unitaria es alta (>90%).

**Prioridad**: Alta

**Estimación**: 5 puntos de historia

**Dependencias**: Todas las HUs de cálculo (HU-147, HU-168, HU-169, HU-170)

**Componentes Técnicos**: Backend (Algoritmos de Cálculo de Prima, Pruebas Unitarias).

**Notas de Implementación**: La lógica de cálculo se basará en la interpretación directa de los datos proporcionados por el servicio, utilizando fórmulas simplificadas definidas en el alcance del proyecto, sin implementar lógica actuarial compleja o inferida.

**Estado**: Backlog

---
## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

### HU-172: Persistir Prima Neta y Comercial en Cotización
**Descripción**:
Como sistema,
Quiero persistir la prima neta, prima comercial y el desglose por ubicación como parte del documento de cotización en MongoDB,
Para que los resultados financieros sean intrínsecos a la cotización.

**Criterios de Aceptación**:
- Dado que se ha completado el cálculo, cuando se guardan los resultados, entonces los valores de prima neta total, prima comercial total y el desglose por ubicación se añaden al documento de la cotización.
- Dado que el documento de cotización se consulta, cuando se recupera, entonces contiene todos los resultados del cálculo.
- Dado que los resultados se persisten, cuando se guarda, entonces se aseguran los tipos de datos correctos (e.g., numéricos, decimales).

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-127 (Persistir Resultados del Cálculo de Prima), HU-169 (Calcular Prima Comercial Total)

**Componentes Técnicos**: Backend (Repositorio de Cotizaciones), Base de Datos (MongoDB).

**Notas de Implementación**: Diseño de esquema de datos en MongoDB para resultados de cálculo.

**Estado**: Backlog

---
### HU-173: Asegurar Persistencia Atómica del Cálculo
**Descripción**:
Como sistema,
Quiero asegurar que la operación de persistencia del cálculo es atómica,
Para garantizar que todos los resultados se guarden o ninguno, manteniendo la consistencia.

**Criterios de Aceptación**:
- Dado que se intenta guardar los resultados del cálculo, cuando la operación es exitosa, entonces todos los componentes de la prima (neta, comercial, desglose) se guardan juntos.
- Dado que ocurre un error durante la persistencia de los resultados, cuando la operación falla, entonces ningún resultado parcial se guarda y el estado de la cotización no se actualiza a "Calculada".
- Dado que la persistencia es atómica, cuando se completa, entonces la cotización en la base de datos es consistente con el cálculo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-172 (Persistir Prima Neta y Comercial en Cotización)

**Componentes Técnicos**: Backend (Módulo de Persistencia de Resultados, Transacciones en MongoDB).

**Notas de Implementación**: Requiere un manejo cuidadoso de las transacciones o la simulación de atomicidad en MongoDB.

**Estado**: Backlog

---
### HU-174: Actualizar Metadatos de Cotización Tras Persistencia de Cálculo
**Descripción**:
Como sistema,
Quiero que el sistema actualice el campo `fechaUltimaActualizacion` y el número de versión de la cotización tras cada persistencia de cálculo,
Para reflejar que la cotización ha sido modificada y sus resultados financieros actualizados.

**Criterios de Aceptación**:
- Dado que se persisten los resultados de un cálculo, cuando la operación es exitosa, entonces el campo `fechaUltimaActualizacion` de la cotización se actualiza.
- Dado que se persisten los resultados de un cálculo, cuando la operación es exitosa, entonces el número de `version` de la cotización se incrementa.
- Dado que los metadatos se actualizan, cuando se consulta la cotización, entonces reflejan los cambios del cálculo.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-146 (Incrementar Versión en Ediciones de Cotización), HU-147 (Actualizar Fecha de Última Actualización en Ediciones)

**Componentes Técnicos**: Backend (Módulo de Persistencia de Resultados).

**Notas de Implementación**: La actualización de metadatos debe ser parte de la misma operación atómica de persistencia de cálculo.

**Estado**: Backlog

---
### HU-175: Registrar Snapshot para Trazabilidad del Cálculo
**Descripción**:
Como sistema,
Quiero registrar un snapshot de parámetros de entrada relevantes, identificadores y valores de tarifas/factores utilizados, el resultado detallado del cálculo y metadatos de ejecución,
Para permitir la trazabilidad y auditoría de cómo se llegó a un resultado específico.

**Criterios de Aceptación**:
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se guarda un snapshot de los datos clave de entrada (sumas aseguradas, coberturas, datos clave de ubicación).
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se registran los identificadores y versión lógica de tarifas/factores utilizados (tipo de tarifa, versión o timestamp).
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se registran los valores numéricos concretos de los factores aplicados y el resultado detallado del cálculo.
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se guardan metadatos de ejecución (fecha/hora del cálculo, versión de la cotización).
- Dado que se necesita auditar un cálculo, cuando se consulta el snapshot, entonces es posible reconstruir los insumos y lógica aplicados.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-161 (Parámetros Disponibles para Motores), HU-171 (Asegurar Precisión del Cálculo)

**Componentes Técnicos**: Backend (Componente de Auditoría/Logging de Cálculo, Repositorio de Cotizaciones).

**Notas de Implementación**: El diseño del esquema de datos debe evitar duplicidades innecesarias y facilitar consultas de trazabilidad.

**Estado**: Backlog

---
## FT-014: Gestión de Concurrencia y Versionado Optimista

### HU-176: Implementar Campo de Versión Incremental
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

**Dependencias**: HU-146 (Incrementar Versión en Ediciones de Cotización)

**Componentes Técnicos**: Backend (Modelo de Datos de Cotización, Capa de Persistencia).

**Notas de Implementación**: El campo de versión debe ser gestionado automáticamente por el backend.

**Estado**: Backlog

---
### HU-177: Comparar Versiones de Cotización en Guardado
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

**Dependencias**: HU-176 (Implementar Campo de Versión Incremental)

**Componentes Técnicos**: Backend (Lógica de Control de Concurrencia en Persistencia).

**Notas de Implementación**: Esta comparación debe realizarse como parte de la operación transaccional de guardado.

**Estado**: Backlog

---
### HU-178: Detectar Conflicto de Concurrencia
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

**Dependencias**: HU-177 (Comparar Versiones de Cotización en Guardado)

**Componentes Técnicos**: Backend (Lógica de Detección de Conflicto).

**Notas de Implementación**: La detección debe ser explícita y generar una excepción o un código de error específico.

**Estado**: Backlog

---
### HU-179: Notificar Usuario de Versión Más Reciente
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

**Dependencias**: HU-178 (Detectar Conflicto de Concurrencia)

**Componentes Técnicos**: Frontend (Sistema de Notificación al Usuario), Backend (Servicio de Mensajes de Error de Concurrencia).

**Notas de Implementación**: La notificación debe ser amigable y ofrecer opciones de acción.
**Estado**: Backlog

---
### HU-180: Permitir Recargar Última Versión de Cotización
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

**Dependencias**: HU-179 (Notificar Usuario de Versión Más Reciente)

**Componentes Técnicos**: Frontend (Botón "Recargar", Lógica de Recarga de Datos).

**Notas de Implementación**: La recarga debe ser una operación que actualice completamente la vista de la cotización.

**Estado**: Backlog

---

### HU-181: Gestión de Layout de Ubicaciones

**Descripción**:
Como usuario,
Quiero poder consultar y configurar el layout de ubicaciones de mi cotización,
Para definir la estructura de campos que se capturarán en cada ubicación del folio.

**Criterios de Aceptación**:
- Dado que tengo una cotización abierta, cuando accedo a la sección de layout (`GET /v1/quotes/{folio}/locations/layout`), entonces el sistema devuelve la configuración actual de `configuracionLayout` asociada al folio.
- Dado que el usuario configura o modifica el layout, cuando guarda los cambios (`PUT /v1/quotes/{folio}/locations/layout`), entonces el sistema persiste la nueva configuración de `configuracionLayout` en la cotización.
- Dado que el layout se actualiza, cuando el usuario accede al formulario de captura de ubicaciones, entonces la interfaz renderiza los campos conforme a la configuración guardada en `configuracionLayout`.
- Dado que es la primera vez que se abre la cotización, cuando se consulta el layout, entonces el sistema devuelve una configuración por defecto que incluye todos los campos obligatorios del dominio de Ubicación.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Feature Padre**: FT-001 (Creación y Edición de Datos Generales de la Cotización)

**Dependencias**: HU-110 (Crear Nueva Cotización)

**Componentes Técnicos**:
- Frontend: Vista de configuración de layout, Renderizado dinámico del formulario de ubicaciones basado en `configuracionLayout`.
- Backend: Endpoints `GET /v1/quotes/{folio}/locations/layout` y `PUT /v1/quotes/{folio}/locations/layout`.

**Notas de Implementación**:
- El `configuracionLayout` es parte del dominio mínimo de la cotización según el reto técnico.
- Los endpoints de layout son parte de los endpoints mínimos esperados definidos en el reto.
- El layout por defecto debe incluir: `nombreUbicacion`, `direccion`, `codigoPostal`, `tipoConstructivo`, `nivel`, `anioConstruccion`, `giro`, `garantías[]`.

**Estado**: Backlog

---

### HU-182: Pantalla de Información Técnica del Cálculo

**Descripción**:
Como usuario,
Quiero acceder a una vista detallada con el desglose técnico del cálculo de prima por ubicación y por componente,
Para entender en detalle cómo se compuso cada valor de prima calculada.

**Criterios de Aceptación**:
- Dado que una cotización ha sido calculada, cuando el usuario navega a `/quotes/{folio}/technical-info`, entonces la interfaz muestra el desglose técnico de cada componente de cálculo activo por ubicación.
- Dado que el desglose se muestra, cuando el usuario lo visualiza, entonces puede ver los valores aplicados de cada uno de los componentes técnicos activos (Incendio edificios, Incendio contenidos, CAT TEV, CAT FHM, y demás activos).
- Dado que una ubicación fue excluida del cálculo, cuando el usuario la visualiza en esta pantalla, entonces se muestra con su indicador de `alertasBloqueantes` y los motivos de exclusión.
- Dado que la cotización no ha sido calculada, cuando el usuario accede a esta ruta, entonces el sistema muestra un mensaje indicando que primero debe ejecutarse el cálculo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Feature Padre**: FT-005 (Visualización Detallada de Resultados Financieros)

**Dependencias**: HU-129 (Visualizar Resumen de Prima Neta y Comercial), HU-127 (Persistir Resultados del Cálculo de Prima)

**Componentes Técnicos**:
- Frontend: Ruta `/quotes/{folio}/technical-info`, componente de tabla técnica con desglose por componente y por ubicación.
- Backend: API de consulta de cotizaciones (lectura del snapshot de trazabilidad).

**Notas de Implementación**:
- Esta ruta corresponde al endpoint de frontend `/quotes/{folio}/technical-info` especificado en el reto técnico.
- El desglose técnico usa los datos del snapshot de trazabilidad del cálculo (FT-013).

**Estado**: Backlog

---

### HU-183: Pantalla de Términos y Condiciones

**Descripción**:
Como usuario,
Quiero acceder a una pantalla de términos y condiciones antes de aprobar o emitir una cotización,
Para revisar y confirmar las condiciones del seguro previo a su formalización.

**Criterios de Aceptación**:
- Dado que una cotización está en estado "Calculada", cuando el usuario navega a `/quotes/{folio}/terms-and-conditions`, entonces la interfaz presenta el resumen de las condiciones de la cotización (cobertura, prima, vigencia) y los términos para su aprobación.
- Dado que el usuario revisa los términos y los acepta, cuando confirma la aceptación, entonces el sistema puede proceder a la transición de estado hacia "Aprobada".
- Dado que el usuario no ha aceptado los términos, cuando intenta avanzar al estado "Aprobada" por otra ruta, entonces el sistema verifica que haya pasado por el flujo de términos.
- Dado que la cotización no está en estado "Calculada" o superior, cuando el usuario intenta acceder a esta ruta, entonces el sistema le redirige indicando que primero debe calcularse.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Feature Padre**: FT-006 (Gestión del Ciclo de Vida y Estados de la Cotización)

**Dependencias**: HU-134 (Actualización Automática a Estado "Calculada"), HU-135 (Cambiar Manualmente Estado a "Aprobada" o "Rechazada")

**Componentes Técnicos**:
- Frontend: Ruta `/quotes/{folio}/terms-and-conditions`, componente de revisión y aceptación de términos.
- Backend: No requiere endpoint nuevo; usa `GET /v1/quotes/{folio}/state` y el endpoint de actualización de estado existente.

**Notas de Implementación**:
- Esta ruta corresponde al endpoint de frontend `/quotes/{folio}/terms-and-conditions` especificado en el reto técnico.
- La aceptación de términos puede ser un flag en el estado del folio o simplemente una validación de UI.

**Estado**: Backlog

---

### HU-184: Calcular Componentes Técnicos de Prima por Ubicación (14 Componentes)

**Descripción**:
Como sistema,
Quiero calcular y aplicar cada uno de los 14 componentes técnicos de prima para las ubicaciones calculables,
Para determinar con precisión y trazabilidad cada parte del costo de la cobertura.

**Criterios de Aceptación**:
- Dado que se ejecuta el cálculo para una ubicación calculable, cuando el motor procesa los componentes, entonces evalúa los siguientes 14 componentes y aplica los activos según las coberturas configuradas:
  1. **Incendio edificios**: prima base por suma asegurada de edificio × tarifa de incendio del giro.
  2. **Incendio contenidos**: prima base por suma asegurada de contenidos × tarifa de incendio del giro.
  3. **Extensión de cobertura**: factor aplicado sobre incendio edificios y/o contenidos.
  4. **CAT TEV**: factor de catástrofe por zona CAT aplicado a la prima de incendio.
  5. **CAT FHM**: cuota FHM por grupo, zona y condición de la ubicación.
  6. **Remoción de escombros**: porcentaje sobre la prima de incendio o suma fija si está activa.
  7. **Gastos extraordinarios**: porcentaje sobre la prima de incendio si está activa.
  8. **Pérdida de rentas**: prima base por suma asegurada de pérdida de rentas si está activa.
  9. **BI (Business Interruption)**: prima base por suma asegurada de BI si está activa.
  10. **Equipo electrónico**: suma asegurada × factor técnico de equipo electrónico por clase y zona.
  11. **Robo**: suma asegurada × tarifa de robo si está activa.
  12. **Dinero y valores**: suma asegurada × tarifa correspondiente si está activa.
  13. **Vidrios**: suma asegurada × tarifa de vidrios si está activa.
  14. **Anuncios luminosos**: suma asegurada × tarifa de anuncios si está activa.
- Dado que un componente no está activo (no está en `opcionesCobertura` o no tiene garantía tarifable), cuando el motor lo evalúa, entonces lo omite y no impacta la prima.
- Dado que el cálculo de todos los componentes activos se completa, cuando se consolida, entonces la prima neta de la ubicación es la suma de todos los componentes aplicados.
- Dado que se requiere trazabilidad, cuando los resultados se persisten, entonces el snapshot incluye el valor calculado de cada componente activo individualmente.

**Prioridad**: Alta

**Estimación**: 8 puntos de historia

**Feature Padre**: FT-012 (Motor Central de Cálculo de Primas)

**Dependencias**: HU-167 (Calcular Prima Neta por Ubicación), HU-168 (Aplicar Factores de Catástrofe CAT y FHM), HU-156 (Consumir Tarifas de Incendio), HU-157 (Consumir Tarifas de Catástrofe), HU-158 (Consumir Tarifa FHM), HU-159 (Consumir Factores de Equipo Electrónico), HU-161 (Parámetros Disponibles para Motores)

**Componentes Técnicos**:
- Backend: Motor de cálculo con implementación de los 14 componentes, Servicio de aplicación de factores, Consolidación de resultados.

**Notas de Implementación**:
- No es obligatorio replicar exactamente una fórmula actuarial real; debe existir una lógica **consistente, trazable y documentada** para cada componente.
- Cada componente debe tener su propia clase o función de cálculo, facilitando el testing unitario con >90% de cobertura.
- Los componentes deben implementarse de forma modular; agregar o desactivar un componente no debe afectar a los demás.
- La documentación del algoritmo de cada componente es un entregable obligatorio del reto técnico.

**Estado**: Backlog

---
