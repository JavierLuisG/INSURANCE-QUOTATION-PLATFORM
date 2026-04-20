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