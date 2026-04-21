## FT-005: Visualización Detallada de Resultados Financieros

### 1. Descripción

Esta feature permite la visualización de los resultados financieros de una cotización, incluyendo el resumen de prima total, desglose por ubicación y componentes adicionales como impuestos y recargos, asegurando que la información corresponda al último cálculo ejecutado.

---

### 2. Objetivo de Negocio

Proporcionar al usuario visibilidad clara, desglosada y confiable del costo de la cotización, facilitando la toma de decisiones.

---

### 3. Alcance Funcional

Incluye:

* Visualización de prima neta y prima comercial total
* Desglose de prima por ubicación de riesgo
* Visualización de impuestos y recargos
* Sincronización con el último cálculo realizado

No incluye:

* Ejecución del cálculo de primas
* Edición de coberturas o parámetros
* Emisión de póliza

---

### 4. Historias de Usuario

| HU     | Nombre                  | Descripción corta        |
| ------ | ----------------------- | ------------------------ |
| HU-020 | Ver prima total         | Resumen neto y comercial |
| HU-021 | Prima por ubicación     | Desglose por riesgo      |
| HU-022 | Componentes adicionales | Impuestos y recargos     |
| HU-023 | Sincronizar resultados  | Reflejar último cálculo  |

---

### 5. Flujo Funcional

1. Usuario accede a la sección de resultados
2. Sistema obtiene el último cálculo persistido (HU-023)
3. Se muestra la prima total (HU-020)
4. Se muestra desglose por ubicación (HU-021)
5. Se presentan impuestos y recargos (HU-022)

---

### 6. Dependencias Técnicas

* API de cotizaciones
* Resultados persistidos del cálculo (FT-004)
* Motor de cálculo de primas

---

### 7. Consideraciones Técnicas

* La información debe provenir de resultados persistidos, no recalculados en UI
* Control de versión del cálculo para evitar inconsistencias
* Manejo de precisión numérica en presentación
* Actualización automática o bajo demanda tras recálculo
* Separación clara entre capa de cálculo y capa de visualización
