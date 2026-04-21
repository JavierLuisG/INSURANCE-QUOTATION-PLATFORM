## FT-001: Creación y Edición de Datos Generales de la Cotización

### 1. Descripción

Esta feature permite la creación, carga y edición de los datos generales de una cotización, incluyendo la generación de folio, captura de información del asegurado, selección de catálogos y configuración inicial del layout de ubicaciones.

---

### 2. Objetivo de Negocio

Garantizar que cada cotización inicie con información base completa, consistente y estructurada, habilitando el flujo completo del proceso de cotización.

---

### 3. Alcance Funcional

Incluye:

* Creación de cotización con folio único
* Carga de cotización existente por folio
* Edición de datos generales (asegurado, vigencia, tipo de seguro, etc.)
* Selección de catálogos básicos (suscriptores, agentes, giros)
* Configuración y persistencia del layout de ubicaciones

No incluye:

* Gestión de ubicaciones de riesgo (FT-002)
* Cálculo de primas (FT-012)
* Validaciones avanzadas de negocio (FT-009, FT-011)

---

### 4. Historias de Usuario

| HU     | Nombre                 | Descripción corta                |
| ------ | ---------------------- | -------------------------------- |
| HU-110 | Crear cotización       | Genera folio único               |
| HU-111 | Cargar cotización      | Consulta por folio               |
| HU-112 | Editar datos generales | Actualiza información base       |
| HU-113 | Seleccionar catálogos  | Usa catálogos básicos            |
| HU-114 | Configurar layout      | Define estructura de ubicaciones |

---

### 5. Flujo Funcional

1. Usuario inicia nueva cotización (HU-110)
2. Sistema genera folio único y estado "Borrador"
3. Usuario puede cargar cotización existente (HU-111)
4. Usuario captura o edita datos generales (HU-112)
5. Selecciona valores desde catálogos básicos (HU-113)
6. Configura layout de ubicaciones (HU-114)
7. Sistema persiste cambios con control de versión y fecha

---

### 6. Dependencias Técnicas

* Servicio de folios (generación de identificadores únicos)
* API de cotizaciones (creación, consulta, edición)
* Servicios de catálogos básicos (`Plataforma-core-ohs` o mock)
* Mecanismo de versionado optimista
* Persistencia de configuración de layout

---

### 7. Consideraciones Técnicas

* Generación de folios idempotente y resistente a colisiones
* Validaciones en frontend y backend para datos generales
* Uso de catálogos desacoplados mediante adaptadores (anti-corruption layer)
* Control de concurrencia mediante versionado optimista
* Layout dinámico basado en configuración persistida (renderizado dinámico en frontend)
* Diseño del modelo de cotización como agregado raíz que encapsula configuración y metadatos
