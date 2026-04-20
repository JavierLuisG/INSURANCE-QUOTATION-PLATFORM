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
