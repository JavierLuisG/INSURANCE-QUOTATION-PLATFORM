## FT-018: Conectividad y Consumo de Tarifas y Factores Técnicos

### 1. Descripción

Esta feature permite la integración con servicios externos (`Plataforma-core-ohs` o su mock) para la consulta, recuperación y transformación de tarifas y factores técnicos (incendio, CAT, FHM y equipo electrónico), necesarios para el cálculo de primas.

---

### 2. Objetivo de Negocio

Garantizar que el motor de cálculo opere con información técnica actualizada, precisa y consistente, asegurando resultados confiables en la cotización.

---

### 3. Alcance Funcional

Incluye:

* Consulta de tarifas de incendio
* Consulta de tarifas CAT por zona
* Consulta de tarifas FHM
* Consulta de factores técnicos de equipo electrónico
* Transformación (mapeo) de datos externos a modelo interno
* Manejo de errores y resiliencia en integraciones

No incluye:

* Ejecución del cálculo de primas (FT-012)
* Validaciones de reglas de negocio (FT-011)

---

### 4. Historias de Usuario

| HU     | Nombre                      | Descripción corta                      |
| ------ | --------------------------- | -------------------------------------- |
| HU-080 | Tarifas incendio            | Consulta tasas base de incendio        |
| HU-081 | Tarifas CAT                 | Obtiene factores por zona CAT          |
| HU-082 | Tarifas FHM                 | Consulta factores hidrometeorológicos  |
| HU-083 | Factores equipo electrónico | Consulta factores técnicos específicos |
| HU-084 | Mapear tarifas/factores     | Transforma datos al modelo interno     |
| HU-085 | Manejo de errores           | Gestiona fallos en integración         |

---

### 5. Flujo Funcional

1. Motor de cálculo requiere tarifas/factores
2. Sistema consulta servicio externo (`Plataforma-core-ohs`) (HU-080–HU-083)
3. Se reciben datos de tarifas y factores técnicos
4. Sistema transforma respuesta al modelo interno (HU-084)
5. Datos quedan disponibles para el motor de cálculo
6. En caso de error, se ejecuta estrategia de resiliencia (HU-085)

---

### 6. Dependencias Técnicas

* Servicio externo `Plataforma-core-ohs` (o mock)
* Cliente API REST
* Repositorio de tarifas y factores
* Capa de mapeo (DTOs / adaptadores)
* Estrategia de resiliencia (reintentos, timeouts)

---

### 7. Consideraciones Técnicas

* Uso de Anti-Corruption Layer para desacoplar contratos externos
* Estrategias de resiliencia: retry con backoff, timeouts y circuit breaker
* Definición de fallback para datos faltantes o inválidos
* Cacheo de tarifas para optimizar performance y reducir latencia
* Validación de consistencia e integridad de datos antes de exponerlos al motor de cálculo
* Diseño de modelos internos flexibles para soportar variabilidad de tarifas
