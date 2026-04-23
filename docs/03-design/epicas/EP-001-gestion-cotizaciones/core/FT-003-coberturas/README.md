## FT-003: Configuración y Selección de Coberturas por Ubicación

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: core

### 1. Descripción

Esta feature permite la visualización, selección y configuración de coberturas asociadas a cada ubicación de riesgo dentro de una cotización, incluyendo la parametrización de valores como suma asegurada y deducibles, así como la visualización de un resumen de coberturas activas.

---

### 2. Objetivo de Negocio

Permitir definir de manera precisa las protecciones aplicables a cada ubicación de riesgo, asegurando flexibilidad en la configuración y control sobre los elementos que impactan el cálculo de la prima.

---

### 3. Alcance Funcional

Incluye:

* Consulta de catálogo de coberturas por tipo de seguro
* Selección y deselección de coberturas por ubicación
* Configuración de parámetros de coberturas (sumas aseguradas, deducibles)
* Visualización de coberturas activas por ubicación

No incluye:

* Cálculo de prima
* Reglas avanzadas de suscripción
* Validaciones actuariales complejas

---

### 4. Historias de Usuario

| HU     | Nombre                 | Descripción corta                |
| ------ | ---------------------- | -------------------------------- |
| HU-011 | Ver coberturas         | Catálogo por tipo de seguro      |
| HU-012 | Seleccionar coberturas | Activar/desactivar por ubicación |
| HU-013 | Configurar cobertura   | Sumas aseguradas y deducibles    |
| HU-014 | Resumen coberturas     | Vista consolidada por ubicación  |

---

### 5. Flujo Funcional

1. Usuario accede a una ubicación de riesgo
2. Sistema muestra catálogo de coberturas (HU-011)
3. Usuario selecciona o deselecciona coberturas (HU-012)
4. Usuario configura parámetros de cada cobertura (HU-013)
5. Sistema actualiza y muestra resumen de coberturas activas (HU-014)

---

### 6. Dependencias Técnicas

* API de cotizaciones
* Servicio de catálogos (`Plataforma-core-ohs`)
* Configuración de productos / coberturas

---

### 7. Consideraciones Técnicas

* Las coberturas dependen del tipo de seguro seleccionado
* Validaciones de parámetros (rangos de suma asegurada, deducibles)
* Persistencia por ubicación (no global)
* Manejo de consistencia al cambiar tipo de seguro
* Diseño desacoplado para permitir agregar nuevas coberturas sin cambios estructurales
