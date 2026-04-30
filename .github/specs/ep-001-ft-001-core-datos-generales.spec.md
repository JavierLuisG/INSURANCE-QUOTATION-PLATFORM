---
id: SPEC-011
status: IN_PROGRESS
feature: ep-001-ft-001-core-datos-generales
created: 2026-04-29
updated: 2026-04-29
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-009  # ep-001-ft-007-core-integracion-servicios (Servicio de Folios y Catálogos)
  - SPEC-003  # ep-003-ft-015-core-catalogos-basicos
  - SPEC-002  # ep-003-ft-019-core-folios
---

# Spec: EP-001 FT-001 — Datos Generales del Cotizador

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción
Esta feature cubre el núcleo inicial del cotizador de seguros: la creación de una nueva cotización con folio único idempotente, la carga y edición de cotizaciones existentes por folio, y la captura validada de los datos generales del asegurado (nombre, RFC, tipo de seguro, moneda, canal de venta y vigencia). Atiende a usuarios autenticados con roles de creación/edición de cotizaciones y es el punto de entrada obligatorio para cualquier flujo posterior del módulo de cotizaciones.

### Requerimiento de Negocio
Los usuarios del cotizador deben poder iniciar una nueva cotización que reciba un folio único asignado automáticamente por el Servicio de Folios (`Plataforma-core-ohs`), buscar y reanudar cotizaciones existentes por folio, capturar y validar los datos generales del asegurado (nombre y RFC), seleccionar parámetros estandarizados desde catálogos externos (tipo de seguro, moneda y canal de venta) y definir el período de vigencia (fechas de inicio y fin). Toda la información debe persistirse en MongoDB y el sistema debe ser resiliente ante fallos de los servicios externos.

---

### Historias de Usuario

#### HU-001: Iniciar Nueva Cotización con Folio Automático

```
Como:        Usuario autenticado con rol creador_cotizacion o agente_ventas
Quiero:      Iniciar una nueva cotización y recibir un folio único asignado automáticamente
Para:        Garantizar la trazabilidad de cada proceso de cotización con un identificador único e idempotente

Prioridad:   Alta
Estimación:  S (2 SP)
Dependencias: SPEC-009 (Servicio de Folios en Plataforma-core-ohs), SPEC-002 (Folios)
Capa:        Ambas
```

#### Criterios de Aceptación — HU-001

**Happy Path**
```gherkin
CRITERIO-1.1: Generación de folio al iniciar cotización
  Dado que: soy un usuario autenticado con permisos de creación de cotización
  Cuando:   selecciono la opción "Nueva Cotización" en la pantalla principal del cotizador
  Entonces: el sistema llama al Servicio de Folios (Plataforma-core-ohs) y obtiene un folio único
  Y:        se crea un nuevo registro de cotización en MongoDB con estado "INCOMPLETA" y el folio asignado
  Y:        el campo "Folio" se muestra prellenado y deshabilitado para edición en la interfaz
  Y:        se registra un log INFO con el folio generado y el id de la cotización
```

**Error Path**
```gherkin
CRITERIO-1.2: Servicio de Folios no disponible
  Dado que: soy un usuario autenticado en la pantalla principal del cotizador
  Y:        el Servicio de Folios (Plataforma-core-ohs) no está disponible o devuelve error
  Cuando:   selecciono la opción "Nueva Cotización"
  Entonces: el Circuit Breaker actúa y se retorna HTTP 503
  Y:        la interfaz muestra el mensaje "No se pudo iniciar la cotización. Inténtelo de nuevo más tarde."
  Y:        no se crea ningún registro de cotización en MongoDB
  Y:        se registra un log ERROR con el detalle del fallo
```

**Edge Case**
```gherkin
CRITERIO-1.3: Idempotencia ante reintentos por error temporal
  Dado que: el sistema intentó crear una cotización y obtuvo un folio del Servicio de Folios
  Y:        ocurrió un error de red antes de persistir la cotización
  Cuando:   el sistema reintenta la operación (hasta 3 veces con backoff exponencial)
  Entonces: se utiliza el mismo folio obtenido inicialmente
  Y:        la cotización se persiste con ese folio sin generar duplicados
```

---

#### HU-002: Cargar y Editar Cotización Existente por Folio

```
Como:        Usuario autenticado con rol agente_ventas, vendedor o administrador_ventas
Quiero:      Buscar y cargar una cotización existente por su folio
Para:        Continuar con la edición o revisión de cotizaciones previamente creadas

Prioridad:   Alta
Estimación:  M (3 SP)
Dependencias: HU-001 (debe existir una cotización con folio)
Capa:        Ambas
```

#### Criterios de Aceptación — HU-002

**Happy Path**
```gherkin
CRITERIO-2.1: Carga exitosa de cotización por folio existente
  Dado que: soy un usuario autenticado con permisos de edición de cotizaciones
  Y:        existe una cotización con folio "COT-2026-000001" en el sistema
  Cuando:   ingreso "COT-2026-000001" en el campo de búsqueda y presiono "Cargar"
  Entonces: el sistema devuelve HTTP 200 con los datos generales de la cotización
  Y:        el formulario de edición se precarga con los datos de la cotización
  Y:        el campo "Folio" permanece deshabilitado para edición
```

**Happy Path — Edición y Guardado**
```gherkin
CRITERIO-2.2: Guardar cambios en datos generales de cotización cargada
  Dado que: la cotización "COT-2026-000001" está cargada en el formulario de edición
  Cuando:   modifico uno o más campos de datos generales y presiono "Guardar"
  Entonces: el sistema persiste los cambios en MongoDB con HTTP 200
  Y:        el campo updatedAt se actualiza al timestamp actual en UTC
  Y:        se incrementa el campo version (versionado optimista)
  Y:        la interfaz muestra confirmación "Cambios guardados con éxito"
```

**Error Path**
```gherkin
CRITERIO-2.3: Folio inexistente o mal formado
  Dado que: soy un usuario autenticado con permisos de edición
  Cuando:   ingreso un folio inexistente (ej. "COT-2026-999999") o con formato inválido (ej. "ABC-123")
  Y:        presiono "Cargar"
  Entonces: el sistema devuelve HTTP 404 con mensaje "No se encontró la cotización"
  Y:        el formulario permanece vacío
```

**Error Path — Autorización**
```gherkin
CRITERIO-2.4: Usuario sin permisos intenta editar cotización
  Dado que: soy un usuario autenticado sin el rol autorizado para editar cotizaciones
  Cuando:   intento cargar o guardar una cotización
  Entonces: el sistema devuelve HTTP 403
  Y:        la interfaz muestra el mensaje "Acceso denegado: No tiene permisos para esta acción"
```

**Edge Case — Concurrencia**
```gherkin
CRITERIO-2.5: Conflicto de versión por edición concurrente
  Dado que: dos usuarios cargan la cotización "COT-2026-000001" simultáneamente
  Cuando:   ambos intentan guardar cambios al mismo tiempo
  Entonces: el primero en guardar recibe HTTP 200 y persiste sus cambios
  Y:        el segundo recibe HTTP 409 con mensaje "La cotización fue modificada por otro usuario. Recargue e intente de nuevo."
```

---

#### HU-003: Captura y Validación de Datos Generales del Asegurado

```
Como:        Usuario autenticado con rol creador_cotizacion o agente_ventas
Quiero:      Introducir el nombre y RFC del asegurado en la sección de datos generales
Para:        Identificar correctamente al cliente en la cotización con datos validados

Prioridad:   Alta
Estimación:  S (2 SP)
Dependencias: HU-001 (cotización debe existir)
Capa:        Ambas
```

#### Criterios de Aceptación — HU-003

**Happy Path**
```gherkin
CRITERIO-3.1: Guardar nombre y RFC válidos del asegurado
  Dado que: estoy en la sección de datos generales de una cotización activa
  Cuando:   introduzco "Juan Pérez García" en "Nombre del Asegurado" y "PEPJ8003261G0" en "RFC"
  Y:        presiono "Guardar"
  Entonces: el sistema valida el RFC con la expresión regular del SAT (personas físicas y morales)
  Y:        persiste los datos con HTTP 200
  Y:        los campos nombreAsegurado y rfcAsegurado se actualizan en MongoDB
```

**Error Path — RFC inválido**
```gherkin
CRITERIO-3.2: Rechazo de RFC con formato inválido
  Dado que: estoy en la sección de datos generales de una cotización activa
  Y:        he introducido "Juan Pérez García" como nombre válido
  Cuando:   introduzco "RFCINVALIDO123" en el campo "RFC" y presiono "Guardar"
  Entonces: el sistema devuelve HTTP 400 con mensaje "El formato del RFC introducido no es válido. Por favor, verifique."
  Y:        la cotización no se guarda
  Y:        el campo RFC muestra el error de validación inline en la interfaz
```

**Error Path — Nombre vacío**
```gherkin
CRITERIO-3.3: Rechazo cuando nombre del asegurado está vacío
  Dado que: estoy en la sección de datos generales de una cotización activa
  Cuando:   dejo el campo "Nombre del Asegurado" vacío y presiono "Guardar"
  Entonces: el sistema devuelve HTTP 400 indicando que el campo es obligatorio
  Y:        la cotización no se guarda
```

**Edge Case — Longitud máxima de nombre**
```gherkin
CRITERIO-3.4: Nombre del asegurado truncado a 255 caracteres
  Dado que: estoy en la sección de datos generales de una cotización activa
  Cuando:   intento introducir un nombre con más de 255 caracteres en el campo "Nombre del Asegurado"
  Entonces: el campo limita la entrada a 255 caracteres en el frontend
  Y:        el backend rechaza con HTTP 400 si se supera el límite por elusión del frontend
```

---

#### HU-004: Selección de Parámetros de Cotización (Tipo Seguro, Moneda, Canal Venta)

```
Como:        Usuario autenticado con rol creador_cotizacion o agente_ventas
Quiero:      Seleccionar el tipo de seguro, moneda y canal de venta desde listas predefinidas de catálogos
Para:        Estandarizar la información de la cotización con valores controlados

Prioridad:   Media
Estimación:  S (2 SP)
Dependencias: SPEC-009 (Integración Catálogos Plataforma-core-ohs), SPEC-003 (Catálogos Básicos)
Capa:        Ambas
```

#### Criterios de Aceptación — HU-004

**Happy Path**
```gherkin
CRITERIO-4.1: Visualización de opciones de catálogo en selectores
  Dado que: estoy en la sección de datos generales de una nueva cotización
  Cuando:   la sección se carga o hago clic en el campo "Tipo de Seguro"
  Entonces: el sistema consulta Plataforma-core-ohs y obtiene la lista de tipos de seguro activos
  Y:        el selector muestra las opciones con un indicador de carga mientras se obtienen
  Y:        lo mismo aplica para los selectores "Moneda" y "Canal de Venta"
```

**Happy Path — Persistencia**
```gherkin
CRITERIO-4.2: Guardar parámetros seleccionados en la cotización
  Dado que: he seleccionado una opción válida para "Tipo de Seguro", "Moneda" y "Canal de Venta"
  Cuando:   presiono "Guardar"
  Entonces: el sistema persiste los IDs seleccionados (tipoSeguroId, monedaId, canalVentaId) con HTTP 200
  Y:        los valores quedan asociados a la cotización en MongoDB
```

**Error Path — Catálogo no disponible**
```gherkin
CRITERIO-4.3: Manejo de fallo del servicio de catálogos
  Dado que: Plataforma-core-ohs no está disponible o devuelve error
  Cuando:   intento cargar la sección de datos generales
  Entonces: el Circuit Breaker actúa y los selectores muestran el mensaje "No se pudieron cargar las opciones. Intente de nuevo más tarde."
  Y:        los selectores se deshabilitan hasta que el servicio esté disponible
  Y:        se registra un log ERROR con el detalle del fallo
```

**Error Path — Campo obligatorio no seleccionado**
```gherkin
CRITERIO-4.4: Validación de campos obligatorios de catálogo
  Dado que: estoy en la sección de datos generales de una cotización
  Y:        no he seleccionado un valor para "Tipo de Seguro"
  Cuando:   intento guardar la cotización
  Entonces: el sistema devuelve HTTP 400 con mensaje "Por favor, seleccione un valor para Tipo de Seguro"
  Y:        la cotización no se guarda hasta completar todos los campos obligatorios
```

**Edge Case — Catálogo vacío**
```gherkin
CRITERIO-4.5: Catálogo devuelve lista vacía
  Dado que: Plataforma-core-ohs devuelve un catálogo vacío para "Moneda"
  Cuando:   hago clic en el selector "Moneda"
  Entonces: el selector no muestra opciones seleccionables
  Y:        se muestra el mensaje informativo "No hay opciones disponibles"
```

---

#### HU-005: Gestión de Vigencia de Cotizaciones

```
Como:        Usuario autenticado con rol editor_cotizaciones o administrador_ventas
Quiero:      Definir las fechas de inicio y fin de la vigencia de la cotización
Para:        Especificar el período de cobertura válido de la oferta comercial

Prioridad:   Alta
Estimación:  S (2 SP)
Dependencias: HU-001 (cotización debe existir)
Capa:        Ambas
```

#### Criterios de Aceptación — HU-005

**Happy Path**
```gherkin
CRITERIO-5.1: Guardar fechas de vigencia válidas
  Dado que: estoy en la sección de datos generales de una cotización activa
  Y:        introduzco fechaInicioVigencia: "2026-05-01" y fechaFinVigencia: "2026-05-31" (formato ISO 8601)
  Cuando:   presiono "Guardar"
  Entonces: el sistema valida que fechaFinVigencia >= fechaInicioVigencia
  Y:        persiste las fechas en MongoDB con HTTP 200
  Y:        no se muestra ningún mensaje de error
```

**Error Path — Rango lógico inválido**
```gherkin
CRITERIO-5.2: Fecha fin anterior a fecha inicio
  Dado que: estoy en la sección de datos generales de una cotización activa
  Y:        introduzco fechaInicioVigencia: "2026-05-31" y fechaFinVigencia: "2026-05-01"
  Cuando:   presiono "Guardar"
  Entonces: el sistema devuelve HTTP 400 con mensaje "La fecha de fin de vigencia no puede ser anterior a la fecha de inicio"
  Y:        las fechas no se guardan en MongoDB
```

**Error Path — Formato inválido**
```gherkin
CRITERIO-5.3: Formato de fecha no es ISO 8601
  Dado que: estoy en la sección de datos generales de una cotización activa
  Y:        introduzco una fecha con formato inválido (ej. "01/05/26") en el campo de fecha inicio
  Cuando:   presiono "Guardar"
  Entonces: el sistema devuelve HTTP 400 con mensaje "Formato de fecha inválido. Por favor, use el formato AAAA-MM-DD."
  Y:        las fechas no se guardan
```

**Edge Case — Fechas iguales**
```gherkin
CRITERIO-5.4: Fecha inicio igual a fecha fin (vigencia de un día)
  Dado que: estoy en la sección de datos generales de una cotización activa
  Y:        introduzco fechaInicioVigencia: "2026-05-01" y fechaFinVigencia: "2026-05-01"
  Cuando:   presiono "Guardar"
  Entonces: el sistema acepta las fechas (fechaFin >= fechaInicio se cumple)
  Y:        las fechas se persisten correctamente con HTTP 200
```

---

### Reglas de Negocio

1. **BR-001 — Folio único e idempotente:** Cada cotización tiene un folio único en formato `COT-YYYY-NNNNNN`, generado por `Plataforma-core-ohs`. El mecanismo de creación debe ser idempotente: reintentos no deben generar folios distintos.
2. **BR-002 — Folio no editable:** Una vez asignado, el folio no puede modificarse ni por el usuario ni por la API.
3. **BR-003 — Validación de RFC SAT:** El RFC del asegurado debe cumplir el formato del SAT: 12 caracteres para personas morales, 13 para personas físicas. La misma expresión regular debe aplicarse en frontend y backend.
4. **BR-004 — Nombre del asegurado obligatorio y máximo 255 chars:** Solo caracteres alfabéticos, espacios, guiones y apóstrofes permitidos.
5. **BR-005 — Parámetros de catálogo obligatorios:** `tipoSeguroId`, `monedaId` y `canalVentaId` son obligatorios y deben existir en los catálogos de `Plataforma-core-ohs` al momento de guardar.
6. **BR-006 — Rango de vigencia lógico:** `fechaFinVigencia >= fechaInicioVigencia`. Fechas en ISO 8601 (`YYYY-MM-DD`), sin componente de hora ni zona horaria.
7. **BR-007 — RBAC:** Solo roles `creador_cotizacion`, `agente_ventas`, `vendedor`, `administrador_ventas` y `editor_cotizaciones` pueden crear o editar cotizaciones.
8. **BR-008 — Versionado optimista:** El documento `Cotizacion` usa el campo `version` (Long) para detectar ediciones concurrentes. Un conflicto devuelve HTTP 409.
9. **BR-009 — Estado inicial:** Una cotización recién creada inicia en `INCOMPLETA`.
10. **BR-010 — Resiliencia externa:** Todas las llamadas a `Plataforma-core-ohs` tienen timeout de 5 s, 3 reintentos con backoff exponencial y Circuit Breaker con Resilience4j.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas
| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `Cotizacion` | colección `cotizaciones` | nueva | Documento principal de una cotización de seguros |

#### Campos del modelo — `Cotizacion`
| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | String | sí | auto-generado | `@Id` interno de MongoDB (no se expone en API) |
| `folio` | String | sí | formato `COT-\d{4}-\d{6}`, único | Folio de negocio asignado por Servicio de Folios |
| `estadoValidacion` | String (enum) | sí | `COMPLETA \| INCOMPLETA \| INACTIVA` | Estado de completitud de la cotización |
| `nombreAsegurado` | String | no* | max 255 chars, regex `^[a-zA-ZáéíóúÁÉÍÓÚñÑ\s'\-\.]+$` | Nombre completo del asegurado |
| `rfcAsegurado` | String | no* | regex RFC SAT (12-13 chars) | RFC del asegurado (persona física o moral) |
| `tipoSeguroId` | String | no* | debe existir en catálogo | ID del tipo de seguro seleccionado |
| `monedaId` | String | no* | debe existir en catálogo | ID de la moneda seleccionada |
| `canalVentaId` | String | no* | debe existir en catálogo | ID del canal de venta seleccionado |
| `fechaInicioVigencia` | String | no* | ISO 8601 `YYYY-MM-DD` | Fecha de inicio de la cobertura |
| `fechaFinVigencia` | String | no* | ISO 8601 `YYYY-MM-DD`, >= fechaInicio | Fecha de fin de la cobertura |
| `createdAt` | Instant | sí | auto-generado (UTC) | Timestamp de creación |
| `updatedAt` | Instant | sí | auto-gestionado (UTC) | Timestamp de última modificación |
| `version` | Long | sí | auto-gestionado por `@Version` | Versión para control de concurrencia optimista |

> *Campos no obligatorios al crear (cotización puede quedar INCOMPLETA), pero todos obligatorios para transición a COMPLETA.

#### Índices / Constraints
- `folio`: índice único (`@Indexed(unique = true)`) — búsqueda primaria y garantía de unicidad de negocio.
- `estadoValidacion`: índice simple — filtrado frecuente por estado.
- `createdAt`: índice descendente — listado cronológico.

---

### API Endpoints

#### POST /api/v1/cotizaciones
- **Descripción**: Inicia una nueva cotización. Obtiene el folio del Servicio de Folios de `Plataforma-core-ohs` con idempotencia.
- **Auth requerida**: sí — JWT Bearer token
- **Roles permitidos**: `creador_cotizacion`, `agente_ventas`
- **Request Body**: vacío (el folio es asignado automáticamente)
- **Response 201**:
  ```json
  {
    "folio": "COT-2026-000001",
    "estadoValidacion": "INCOMPLETA",
    "nombreAsegurado": null,
    "rfcAsegurado": null,
    "tipoSeguroId": null,
    "monedaId": null,
    "canalVentaId": null,
    "fechaInicioVigencia": null,
    "fechaFinVigencia": null,
    "createdAt": "2026-04-29T10:00:00Z",
    "updatedAt": "2026-04-29T10:00:00Z",
    "version": 0
  }
  ```
- **Response 401**: token ausente o expirado
- **Response 403**: rol sin permisos de creación
- **Response 503**: Servicio de Folios no disponible (Circuit Breaker abierto)

---

#### GET /api/v1/cotizaciones/{folio}
- **Descripción**: Obtiene los datos generales de una cotización por su folio.
- **Auth requerida**: sí — JWT Bearer token
- **Roles permitidos**: `creador_cotizacion`, `agente_ventas`, `vendedor`, `administrador_ventas`, `editor_cotizaciones`
- **Path param**: `folio` — formato `COT-YYYY-NNNNNN`
- **Response 200**:
  ```json
  {
    "folio": "COT-2026-000001",
    "estadoValidacion": "INCOMPLETA",
    "nombreAsegurado": "Juan Pérez García",
    "rfcAsegurado": "PEPJ8003261G0",
    "tipoSeguroId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "monedaId": "f0e9d8c7-b6a5-4321-fedc-ba9876543210",
    "canalVentaId": "1a2b3c4d-5e6f-7890-abcd-ef1234567890",
    "fechaInicioVigencia": "2026-05-01",
    "fechaFinVigencia": "2026-05-31",
    "createdAt": "2026-04-29T10:00:00Z",
    "updatedAt": "2026-04-29T10:30:00Z",
    "version": 2
  }
  ```
- **Response 400**: formato de folio inválido en path param
- **Response 401**: token ausente o expirado
- **Response 403**: sin permisos de lectura
- **Response 404**: cotización con ese folio no existe

---

#### PUT /api/v1/cotizaciones/{folio}
- **Descripción**: Actualiza los datos generales de una cotización existente. Aplica versionado optimista.
- **Auth requerida**: sí — JWT Bearer token
- **Roles permitidos**: `agente_ventas`, `vendedor`, `administrador_ventas`, `editor_cotizaciones`
- **Path param**: `folio` — formato `COT-YYYY-NNNNNN`
- **Request Body** (todos los campos son opcionales; se actualizan solo los enviados):
  ```json
  {
    "nombreAsegurado": "Juan Pérez García",
    "rfcAsegurado": "PEPJ8003261G0",
    "tipoSeguroId": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
    "monedaId": "f0e9d8c7-b6a5-4321-fedc-ba9876543210",
    "canalVentaId": "1a2b3c4d-5e6f-7890-abcd-ef1234567890",
    "fechaInicioVigencia": "2026-05-01",
    "fechaFinVigencia": "2026-05-31",
    "version": 2
  }
  ```
- **Response 200**: cotización actualizada (mismo esquema que GET /api/v1/cotizaciones/{folio})
- **Response 400**: campo inválido (RFC mal formado, fecha con formato incorrecto, rango de fechas inválido, campo obligatorio vacío)
- **Response 401**: token ausente o expirado
- **Response 403**: sin permisos de edición
- **Response 404**: cotización no encontrada
- **Response 409**: conflicto de versión — `"La cotización fue modificada por otro usuario. Recargue e intente de nuevo."`

---

### Diseño Frontend

#### Componentes nuevos
| Componente | Archivo | Props principales | Descripción |
|------------|---------|------------------|-------------|
| `CotizadorHeader` | `components/Cotizador/CotizadorHeader.tsx` | `folio: string` | Encabezado con folio (read-only) y estado de la cotización |
| `DatosGeneralesForm` | `components/Cotizador/DatosGeneralesForm.tsx` | `folio, onSave` | Formulario unificado con todos los campos de datos generales |
| `AseguradoFields` | `components/Cotizador/AseguradoFields.tsx` | `control, errors` | Subcampos nombre y RFC con validación Zod inline |
| `ParametrosSelector` | `components/Cotizador/ParametrosSelector.tsx` | `control, errors, catalogs` | Dropdowns de tipo seguro, moneda y canal de venta |
| `VigenciaFields` | `components/Cotizador/VigenciaFields.tsx` | `control, errors` | DatePickers de fechaInicioVigencia y fechaFinVigencia |
| `FolioSearchBar` | `components/Cotizador/FolioSearchBar.tsx` | `onLoad` | Campo de búsqueda de cotización por folio |

#### Páginas nuevas
| Página | Archivo | Ruta | Protegida |
|--------|---------|------|-----------|
| `NuevaCotizacionPage` | `app/cotizaciones/nueva/page.tsx` | `/cotizaciones/nueva` | sí (roles de creación) |
| `EditarCotizacionPage` | `app/cotizaciones/[folio]/page.tsx` | `/cotizaciones/[folio]` | sí (roles de edición) |

#### Hooks y State
| Hook/Store | Archivo | Retorna | Descripción |
|------------|---------|---------|-------------|
| `useCotizacion` | `hooks/useCotizacion.ts` | `{ cotizacion, loading, error, iniciar, cargar, actualizar }` | Operaciones CRUD sobre cotización |
| `useCatalogos` | `hooks/useCatalogos.ts` | `{ tiposSeguros, monedas, canalesVenta, loading, error }` | Carga asíncrona de catálogos desde Plataforma-core-ohs |
| `cotizacionStore` | `store/cotizacionStore.ts` | Zustand store — estado global de la cotización activa | Estado global persistido en sesión |

#### Zod Schemas
| Schema | Archivo | Descripción |
|--------|---------|-------------|
| `cotizacionSchema` | `lib/schemas/cotizacion.schema.ts` | Validación de respuesta API y formulario (RFC regex SAT, fechas ISO, campos obligatorios) |
| `datosGeneralesSchema` | `lib/schemas/datosGenerales.schema.ts` | Subschema para validación parcial del formulario de datos generales |

#### Services (llamadas API)
| Función | Archivo | Endpoint |
|---------|---------|---------|
| `iniciarCotizacion(token)` | `lib/services/cotizacionService.ts` | `POST /api/v1/cotizaciones` |
| `getCotizacion(folio, token)` | `lib/services/cotizacionService.ts` | `GET /api/v1/cotizaciones/{folio}` |
| `actualizarCotizacion(folio, data, token)` | `lib/services/cotizacionService.ts` | `PUT /api/v1/cotizaciones/{folio}` |
| `getCatalogos(tipo, token)` | `lib/services/catalogoService.ts` | `GET /api/v1/catalogos/{tipo}` (via SPEC-009) |

---

### Arquitectura y Dependencias

**Backend:**
- Paquetes: `spring-boot-starter-web`, `spring-boot-starter-data-mongodb`, `spring-boot-starter-security`, `resilience4j-spring-boot3`, `jjwt`, `lombok` — todos ya en el stack definido.
- Integración con `Plataforma-core-ohs` vía HTTP client (RestTemplate o WebClient) con Resilience4j Circuit Breaker + Retry configurado: timeout 5 s, 3 reintentos, backoff exponencial.
- La lógica de validación de RFC se implementa en un `RfcValidator` reutilizable (componente `@Component`) invocado desde el Service.

**Frontend:**
- Axios con interceptor para inyectar `Authorization: Bearer <token>` en todas las llamadas.
- Zustand para estado global de cotización activa.
- Zod para validación de formularios y respuestas API.
- React Hook Form + Zod resolver para manejo del formulario de datos generales.

**Dependencias externas:**
- `Plataforma-core-ohs` → Servicio de Folios (HU-001) y Catálogos (HU-004). Ver SPEC-009 y SPEC-002.

### Notas de Implementación
> - El campo `folio` se persiste en MongoDB pero el campo `_id` interno NUNCA se expone en las respuestas API.
> - La validación de existencia de `tipoSeguroId`, `monedaId` y `canalVentaId` en catálogos se realiza en el backend al momento del PUT para evitar inconsistencias por desincronización de catálogos en frontend.
> - Las fechas de vigencia se almacenan como `String` (`YYYY-MM-DD`) en MongoDB para evitar problemas de zona horaria; la validación de formato e integridad se hace en el Service.
> - El Circuit Breaker para el Servicio de Folios debe estar en estado CLOSED por defecto; si se abre, la creación de cotizaciones falla rápido con 503 en lugar de esperar timeout.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación
- [x] Crear entidad `Cotizacion.java` en `model/entity/` con `@Document(collection = "cotizaciones")`, todos los campos del modelo, `@Id`, `@Version`, `@Indexed(unique=true)` en folio, `createdAt`/`updatedAt` como `Instant`
- [x] Crear `CotizacionRequest.java` (DTO de actualización, campos opcionales, incluye `version`)
- [x] Crear `CotizacionResponse.java` (DTO de respuesta, excluye `_id` de MongoDB)
- [x] Implementar `CotizacionRepository.java` extendiendo `MongoRepository<Cotizacion, String>` con `Optional<Cotizacion> findByFolio(String folio)`
- [x] Implementar `RfcValidator.java` como `@Component` con regex SAT para personas físicas y morales
- [x] Implementar `CotizacionService.java` (interfaz) con métodos: `iniciar()`, `obtenerPorFolio(folio)`, `actualizar(folio, request)`
- [x] Implementar `CotizacionServiceImpl.java`:
  - `iniciar()`: llama cliente de Folios con Resilience4j, persiste con `estadoValidacion=INCOMPLETA`
  - `obtenerPorFolio(folio)`: busca en repo, lanza `NotFoundException` si no existe
  - `actualizar(folio, request)`: valida RFC, rango de fechas, IDs de catálogos, aplica versionado optimista
- [x] Configurar cliente HTTP para `Plataforma-core-ohs` (Servicio de Folios) con Circuit Breaker `foliosCircuitBreaker` y Retry `foliosRetry` en `application.yml`
- [x] Implementar `CotizacionController.java` con endpoints: `POST /api/v1/cotizaciones`, `GET /api/v1/cotizaciones/{folio}`, `PUT /api/v1/cotizaciones/{folio}`
- [x] Configurar Spring Security para RBAC: mapear roles JWT a los endpoints de cotizaciones
- [x] Registrar módulo de cotizaciones en el contexto de Spring Boot

#### Tests Backend
- [ ] `CotizacionServiceImplTest` — `iniciar_cuandoServicioFoliosDisponible_creaDocumentoConFolioYEstadoIncompleta`
- [ ] `CotizacionServiceImplTest` — `iniciar_cuandoServicioFoliosFalla_lanzaServiceUnavailableException`
- [ ] `CotizacionServiceImplTest` — `actualizar_cuandoRfcInvalido_lanzaValidationException`
- [ ] `CotizacionServiceImplTest` — `actualizar_cuandoFechaFinAnteriorAInicio_lanzaValidationException`
- [ ] `CotizacionServiceImplTest` — `obtenerPorFolio_cuandoFolioNoExiste_lanzaNotFoundException`
- [ ] `CotizacionServiceImplTest` — `actualizar_cuandoConflictoDeVersion_lanzaOptimisticLockException`
- [ ] `RfcValidatorTest` — `validar_rfcPersonaFisicaValido_retornaTrue`
- [ ] `RfcValidatorTest` — `validar_rfcPersonaMoralValido_retornaTrue`
- [ ] `RfcValidatorTest` — `validar_rfcMalFormado_retornaFalse`
- [ ] `CotizacionRepositoryTest` (Testcontainers MongoDB) — `findByFolio_cuandoExiste_retornaDocumento`
- [ ] `CotizacionControllerTest` — `POST_/api/v1/cotizaciones_retorna201_conFolioAsignado`
- [ ] `CotizacionControllerTest` — `POST_/api/v1/cotizaciones_sinToken_retorna401`
- [ ] `CotizacionControllerTest` — `GET_/api/v1/cotizaciones/{folio}_folioExistente_retorna200`
- [ ] `CotizacionControllerTest` — `GET_/api/v1/cotizaciones/{folio}_folioInexistente_retorna404`
- [ ] `CotizacionControllerTest` — `PUT_/api/v1/cotizaciones/{folio}_datosValidos_retorna200`
- [ ] `CotizacionControllerTest` — `PUT_/api/v1/cotizaciones/{folio}_rfcInvalido_retorna400`
- [ ] `CotizacionControllerTest` — `PUT_/api/v1/cotizaciones/{folio}_conflictoVersion_retorna409`
- [ ] `CotizacionControllerTest` — `PUT_/api/v1/cotizaciones/{folio}_sinRolEdicion_retorna403`

---

### Frontend

#### Implementación
- [x] Crear `lib/schemas/cotizacion.schema.ts` con Zod: validar RFC (regex SAT), fechas ISO 8601, rango vigencia, campos obligatorios de catálogo
- [x] Crear `lib/schemas/datosGenerales.schema.ts` para validación parcial del formulario
- [x] Crear `lib/services/cotizacionService.ts` — funciones `iniciarCotizacion`, `getCotizacion`, `actualizarCotizacion` con Axios + token Bearer
- [x] Crear `lib/services/catalogoService.ts` — función `getCatalogos(tipo, token)` con manejo de error y estado de carga
- [x] Crear `store/cotizacionStore.ts` — Zustand store con campos: `cotizacion`, `loading`, `error`, acciones: `setLoading`, `setCotizacion`, `setError`, `reset`
- [x] Crear `hooks/useCotizacion.ts` — orquesta llamadas al service, actualiza store, expone `iniciar()`, `cargar(folio)`, `actualizar(folio, data)`
- [x] Crear `hooks/useCatalogos.ts` — carga asíncrona de tiposSeguros, monedas, canalesVenta con estado de loading/error individual
- [x] Implementar `components/Cotizador/FolioSearchBar.tsx` — campo texto + botón "Cargar", validación de formato de folio con Zod
- [x] Implementar `components/Cotizador/CotizadorHeader.tsx` — muestra folio (read-only) y badge de `estadoValidacion`
- [x] Implementar `components/Cotizador/AseguradoFields.tsx` — campos nombre y RFC con validación inline en tiempo real
- [x] Implementar `components/Cotizador/ParametrosSelector.tsx` — tres dropdowns con spinner de carga y mensaje de error por catálogo
- [x] Implementar `components/Cotizador/VigenciaFields.tsx` — dos DatePickers con validación de rango cruzado
- [x] Implementar `components/Cotizador/DatosGeneralesForm.tsx` — integra todos los subcampos, React Hook Form + Zod resolver, botón "Guardar" con estado de loading
- [x] Implementar `app/cotizaciones/nueva/page.tsx` — llama `iniciarCotizacion()` al cargar, renderiza `CotizadorHeader` y `DatosGeneralesForm`
- [x] Implementar `app/cotizaciones/[folio]/page.tsx` — llama `cargar(folio)` al cargar, renderiza `FolioSearchBar`, `CotizadorHeader` y `DatosGeneralesForm`
- [ ] Proteger rutas con middleware de autenticación JWT y verificación de roles

#### Tests Frontend
- [ ] `FolioSearchBar llama onLoad con folio válido al presionar Cargar`
- [ ] `FolioSearchBar muestra error de formato para folio mal formado`
- [ ] `CotizadorHeader muestra el folio y el estado de la cotización correctamente`
- [ ] `CotizadorHeader campo folio está deshabilitado`
- [ ] `AseguradoFields acepta RFC válido sin mostrar error`
- [ ] `AseguradoFields muestra error inline para RFC inválido`
- [ ] `AseguradoFields limita nombre a 255 caracteres`
- [ ] `ParametrosSelector muestra spinner mientras carga catálogos`
- [ ] `ParametrosSelector muestra opciones del catálogo al cargar correctamente`
- [ ] `ParametrosSelector muestra error cuando el servicio de catálogos falla`
- [ ] `VigenciaFields muestra error cuando fechaFin es anterior a fechaInicio`
- [ ] `VigenciaFields acepta fechas iguales (vigencia de un día)`
- [ ] `DatosGeneralesForm envía datos correctos al servicio al hacer clic en Guardar`
- [ ] `DatosGeneralesForm no envía si hay errores de validación`
- [ ] `useCotizacion iniciar devuelve cotización con folio asignado`
- [ ] `useCotizacion cargar actualiza el store con los datos de la cotización`
- [ ] `useCotizacion cargar establece error para folio inexistente`
- [ ] `useCotizacion actualizar maneja conflicto de versión (409) correctamente`
- [ ] `useCatalogos carga los tres catálogos al montar`
- [ ] `useCatalogos maneja error de catálogo individualmente`

---

### QA
- [ ] Ejecutar skill `/gherkin-case-generator` → criterios CRITERIO-1.1 a CRITERIO-5.4
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD de riesgos (HU-001 Circuit Breaker, HU-002 concurrencia, HU-003 validación RFC, HU-004 catálogos externos, HU-005 fechas)
- [ ] Verificar cobertura de tests backend ≥ 80% global con JaCoCo
- [ ] Verificar cobertura de tests frontend con Vitest
- [ ] Validar que las reglas de negocio BR-001 a BR-010 están cubiertas por criterios de aceptación
- [ ] Ejecutar pruebas de integración con Testcontainers MongoDB para el repositorio
- [ ] Validar comportamiento del Circuit Breaker ante fallo de `Plataforma-core-ohs` (Folios y Catálogos)
- [ ] Verificar que el campo `_id` de MongoDB no se expone en ninguna respuesta API
- [ ] Verificar comportamiento de versionado optimista con prueba de concurrencia
- [ ] Actualizar estado spec: `status: IMPLEMENTED` al completar implementación
