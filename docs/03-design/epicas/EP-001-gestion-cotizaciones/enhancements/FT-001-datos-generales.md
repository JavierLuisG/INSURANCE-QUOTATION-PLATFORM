## FT-001: Creación y Edición de Datos Generales de la Cotización

### HU-109: Crear Nueva Cotización
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
### HU-110: Cargar Cotización Existente
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
### HU-111: Editar Datos Generales de la Cotización
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

**Dependencias**: HU-151 (Consumir Catálogos Básicos), HU-155 (Manejo de Versionado Optimista en Ediciones)

**Componentes Técnicos**: Frontend (Formulario de Datos Generales), Backend (API de Edición de Cotizaciones).

**Notas de Implementación**: Implementar validaciones de formato (e.g., RFC) y rangos (e.g., Vigencia).

**Estado**: Backlog

---
### HU-112: Seleccionar Opciones de Catálogos Básicos
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

**Dependencias**: HU-151 (Consumir Catálogos Básicos)

**Componentes Técnicos**: Frontend (Componentes de selección de catálogos), Backend (API de Consulta de Catálogos).

**Notas de Implementación**: Los catálogos deben ser consumidos de `Plataforma-core-ohs` o su simulación.

**Estado**: Backlog

---
