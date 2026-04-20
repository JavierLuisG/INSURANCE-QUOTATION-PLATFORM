## FT-004: Ejecución y Persistencia del Cálculo de Primas

### 1. Descripción

Esta feature permite ejecutar el cálculo de primas de una cotización, considerando coberturas, ubicaciones y factores técnicos, generando resultados a nivel global y por ubicación, y asegurando su persistencia.

---

### 2. Objetivo de Negocio

Obtener el valor económico de la cotización de forma consistente y trazable, permitiendo al usuario conocer el costo del seguro antes de su emisión.

---

### 3. Alcance Funcional

Incluye:

* Disparar el proceso de cálculo de primas
* Cálculo de prima neta y prima comercial
* Cálculo desglosado por ubicación
* Aplicación de factores técnicos y reglas de negocio
* Persistencia de resultados del cálculo

No incluye:

* Emisión de póliza
* Pago o facturación
* Simulación avanzada de escenarios

---

### 4. Historias de Usuario

| HU     | Nombre               | Descripción corta          |
| ------ | -------------------- | -------------------------- |
| HU-015 | Iniciar cálculo      | Disparar cálculo de primas |
| HU-016 | Calcular prima total | Neta y comercial           |
| HU-017 | Prima por ubicación  | Desglose por riesgo        |
| HU-018 | Persistir cálculo    | Guardar resultados         |
| HU-019 | Aplicar reglas       | Factores técnicos          |

---

### 5. Flujo Funcional

1. Usuario inicia cálculo de primas (HU-015)
2. Sistema recopila datos de cotización (ubicaciones y coberturas)
3. Se aplican factores técnicos y reglas (HU-019)
4. Sistema calcula prima por ubicación (HU-017)
5. Sistema consolida prima total (HU-016)
6. Se persisten resultados del cálculo (HU-018)
7. Se presentan resultados al usuario

---

### 6. Dependencias Técnicas

* API de cotizaciones
* Motor de cálculo de primas
* Configuración de productos y reglas
* Servicios de factores técnicos

---

### 7. Consideraciones Técnicas

* El cálculo debe ser determinístico e idempotente
* Separación clara entre orquestación y motor de cálculo
* Manejo de precisión numérica (decimales, redondeos)
* Posibilidad de recalcular ante cambios en la cotización
* Trazabilidad de factores aplicados en el cálculo
