## FT-003: Configuración y Selección de Coberturas por Ubicación

### 1. Descripción

Esta feature permite la gestión completa de coberturas asociadas a cada ubicación de riesgo dentro de una cotización, incluyendo la visualización del catálogo disponible, la selección/deselección de coberturas, la configuración de sus parámetros específicos y la visualización del estado final configurado por ubicación.

---

### 2. Objetivo de Negocio

Permitir la personalización precisa de la protección asegurada para cada ubicación, asegurando que las coberturas seleccionadas y sus parámetros reflejen adecuadamente el nivel de riesgo y las necesidades del cliente.

---

### 3. Alcance Funcional

Incluye:

* Visualización del catálogo de coberturas disponibles por tipo de seguro y ubicación
* Selección y deselección de coberturas por ubicación
* Configuración de parámetros específicos por cobertura (sumas aseguradas, deducibles, etc.)
* Visualización consolidada de coberturas activas por ubicación

No incluye:

* Validaciones complejas de negocio (FT-011)
* Cálculo de primas asociado a coberturas (FT-012)

---

### 4. Historias de Usuario

| HU     | Nombre                             | Descripción corta                              |
| ------ | ---------------------------------- | ---------------------------------------------- |
| HU-121 | Visualizar catálogo de coberturas  | Muestra coberturas disponibles por ubicación   |
| HU-122 | Seleccionar coberturas             | Activa o desactiva coberturas por ubicación    |
| HU-123 | Configurar parámetros de cobertura | Define valores como suma asegurada o deducible |
| HU-124 | Visualizar coberturas activas      | Muestra resumen de coberturas seleccionadas    |

---

### 5. Flujo Funcional

1. Usuario accede a la sección de coberturas de una ubicación (HU-121)
2. Sistema consulta y muestra el catálogo de coberturas disponibles según el tipo de seguro
3. Usuario selecciona o deselecciona coberturas (HU-122)
4. Para coberturas seleccionadas, el sistema habilita la configuración de parámetros (HU-123)
5. Usuario ingresa valores como suma asegurada y deducibles
6. Sistema valida rangos básicos de entrada (si aplica en frontend/backend)
7. Usuario guarda la configuración
8. Sistema persiste coberturas y parámetros asociados a la ubicación
9. Usuario visualiza el resumen de coberturas activas (HU-124)

---

### 6. Dependencias Técnicas

* API de cotizaciones (gestión de coberturas por ubicación)
* Servicio de catálogos de coberturas / garantías (Plataforma-core-ohs o mock)
* Modelo de dominio de coberturas y parámetros
* Módulo de validación de parámetros (integración con reglas de rango)
* Frontend (UI de selección y configuración dinámica)

---

### 7. Consideraciones Técnicas

* Modelar coberturas como **entidades asociadas a ubicación** (no globales a la cotización)
* Uso de estructuras tipo:

  ```json
  {
    "coberturaId": "INCENDIO",
    "activa": true,
    "parametros": {
      "sumaAsegurada": 1000000,
      "deducible": 5000
    }
  }
  ```
* Separar claramente:

  * **catálogo externo** (fuente de verdad)
  * **configuración interna** (estado de la cotización)
* Soportar configuración dinámica (no hardcodear coberturas en frontend)
* Validaciones de parámetros deben ser:

  * Declarativas (idealmente configurables)
  * Reutilizables por el motor de validación (FT-011)
* Preparar integración directa con el motor de cálculo (FT-012):

  * Cada cobertura debe ser consumible como input del cálculo
* Manejar idempotencia en selección/deselección (evitar duplicados)
* UI debe manejar:

  * Estados vacíos
  * Coberturas sin parámetros
  * Coberturas con múltiples parámetros
* Considerar lazy loading del catálogo si es grande
* Mantener consistencia entre IDs de catálogo y modelo interno (clave crítica para cálculo)

---
