## FT-017: Integración de Catálogos de Clasificación de Riesgo y Garantías

### 1. Descripción

Esta feature permite la integración con servicios externos (`Plataforma-core-ohs` o su mock) para la obtención de catálogos de clasificación de riesgo y garantías, así como su transformación al modelo interno del cotizador para su uso en la configuración de coberturas.

---

### 2. Objetivo de Negocio

Asegurar que la configuración de coberturas se base en información actualizada, consistente y alineada con las políticas de suscripción, permitiendo una correcta definición del riesgo y sus garantías asociadas.

---

### 3. Alcance Funcional

Incluye:

* Consulta de catálogo de clasificación de riesgo
* Consulta de catálogo de garantías
* Transformación (mapeo) de datos externos a modelo interno
* Disponibilidad de catálogos para selección en configuración de coberturas

No incluye:

* Lógica de selección de coberturas (FT-003)
* Aplicación de reglas de negocio sobre coberturas (FT-009)

---

### 4. Historias de Usuario

| HU     | Nombre                | Descripción corta                  |
| ------ | --------------------- | ---------------------------------- |
| HU-077 | Catálogo de riesgo    | Consulta clasificaciones de riesgo |
| HU-078 | Catálogo de garantías | Consulta garantías disponibles     |
| HU-079 | Mapear catálogos      | Transforma datos a modelo interno  |

---

### 5. Flujo Funcional

1. Sistema requiere catálogo durante configuración de coberturas (HU-077, HU-078)
2. Se realiza llamada a servicio externo (`Plataforma-core-ohs`)
3. Se reciben datos de clasificación de riesgo y garantías
4. Sistema transforma respuesta a modelo interno (HU-079)
5. Catálogos quedan disponibles para selección en UI
6. Usuario selecciona clasificación y garantías asociadas

---

### 6. Dependencias Técnicas

* Servicio externo `Plataforma-core-ohs` (o mock)
* Cliente API REST
* Capa de mapeo (DTOs / adaptadores)
* Repositorio de catálogos internos

---

### 7. Consideraciones Técnicas

* Desacoplar contrato externo mediante adaptadores (anti-corruption layer)
* Manejo de errores y resiliencia (reintentos, fallback)
* Validación de integridad de datos (IDs únicos, consistencia)
* Posible cacheo de catálogos para optimizar performance
* Preparar estructura para soportar cambios en contratos externos sin impacto aguas abajo
