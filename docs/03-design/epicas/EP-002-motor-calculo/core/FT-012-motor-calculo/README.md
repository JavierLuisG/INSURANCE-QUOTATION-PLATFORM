## FT-012: Motor Central de Cálculo de Primas

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: core

### 1. Descripción

Esta feature implementa el motor central encargado de calcular las primas de la cotización, tanto a nivel de ubicación como a nivel total, aplicando tarifas, factores técnicos y reglas comerciales.

---

### 2. Objetivo de Negocio

Garantizar que el cálculo de primas sea preciso, consistente y basado en parámetros técnicos actualizados, permitiendo obtener un precio confiable para la cotización.

---

### 3. Alcance Funcional

Incluye:

* Cálculo de prima neta por ubicación
* Aplicación de factores técnicos (CAT, FHM, etc.)
* Cálculo de prima comercial total
* Generación de desglose de primas por ubicación
* Cálculo específico por tipo de cobertura (incendio, equipo electrónico)

No incluye:

* Validaciones previas al cálculo (FT-011)
* Persistencia de resultados (FT-004)
* Visualización de resultados (FT-005)

---

### 4. Historias de Usuario

| HU     | Nombre                   | Descripción corta           |
| ------ | ------------------------ | --------------------------- |
| HU-054 | Prima neta por ubicación | Cálculo base por riesgo     |
| HU-055 | Factores CAT y FHM       | Ajustes técnicos            |
| HU-056 | Prima comercial total    | Suma + factores comerciales |
| HU-057 | Desglose por ubicación   | Detalle de cálculo          |
| HU-058 | Prima de incendio        | Suma * tarifa               |
| HU-059 | Prima equipo electrónico | Suma * factor               |

---

### 5. Flujo Funcional

1. Se reciben datos validados de la cotización
2. Se calcula prima neta por cada ubicación (HU-054)
3. Se aplican factores técnicos (HU-055)
4. Se calculan componentes específicos (HU-058, HU-059)
5. Se consolidan resultados por ubicación (HU-057)
6. Se calcula prima comercial total (HU-056)

---

### 6. Dependencias Técnicas

* FT-010: Parámetros y tarifas
* FT-011: Validaciones previas
* Servicios de catálogos y factores técnicos
* Motor de reglas de negocio

---

### 7. Consideraciones Técnicas

* Diseño modular del motor de cálculo (estrategias por cobertura)
* Uso de objetos inmutables para evitar efectos colaterales
* Precisión en operaciones numéricas (BigDecimal)
* Orden controlado de aplicación de factores
* Alta testabilidad (unit + integration tests)
* Posible desacople en sub-motores (incendio, CAT, FHM)

---