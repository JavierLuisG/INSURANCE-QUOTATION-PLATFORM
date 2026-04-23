## FT-014: Gestión de Concurrencia y Versionado Optimista

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: core

### 1. Descripción

Esta feature gestiona el control de concurrencia sobre las cotizaciones mediante un mecanismo de versionado optimista, permitiendo detectar y manejar conflictos cuando múltiples usuarios modifican la misma información.

---

### 2. Objetivo de Negocio

Evitar la pérdida de información y garantizar la integridad de los datos cuando múltiples usuarios interactúan simultáneamente con una misma cotización.

---

### 3. Alcance Funcional

Incluye:

* Control de versión en cotizaciones
* Detección de conflictos de concurrencia
* Rechazo de operaciones inconsistentes
* Notificación de conflictos al usuario
* Recarga de la última versión disponible

No incluye:

* Persistencia de resultados (FT-013)
* Gestión de datos de la cotización (FT-001, FT-002, FT-003)
* Resolución automática de conflictos (merge avanzado)

---

### 4. Historias de Usuario

| HU     | Nombre                    | Descripción corta        |
| ------ | ------------------------- | ------------------------ |
| HU-064 | Control de versión        | Incremento de versión    |
| HU-065 | Detección de conflictos   | Comparación de versiones |
| HU-066 | Notificación de conflicto | Mensaje al usuario       |
| HU-067 | Recarga de cotización     | Obtener versión actual   |

---

### 5. Flujo Funcional

1. Usuario carga cotización con versión actual
2. Usuario realiza modificaciones
3. Otro usuario puede modificar en paralelo
4. Al guardar, el sistema compara versiones (HU-065)
5. Si coinciden → se guarda y se incrementa versión (HU-064)
6. Si no coinciden → se rechaza operación (HU-066)
7. Usuario puede recargar última versión (HU-067)

---

### 6. Dependencias Técnicas

* FT-013: Persistencia y actualización de metadatos
* Repositorio de cotizaciones (MongoDB)
* API de cotizaciones
* Manejo de errores estandarizado

---

### 7. Consideraciones Técnicas

* Implementación de versionado optimista (campo `version`)
* Comparación atómica en operaciones de persistencia (e.g., `update where version = X`)
* Manejo de errores de concurrencia (HTTP 409 Conflict)
* Consistencia con actualizaciones de metadatos (fecha + versión)
* Diseño de mensajes claros para el usuario
* Posible extensión futura a estrategias de merge

---