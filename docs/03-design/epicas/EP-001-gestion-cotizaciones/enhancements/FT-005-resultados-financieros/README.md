## FT-005: Visualización Detallada de Resultados Financieros

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: enhancements

### 1. Descripción

Esta feature habilita la visualización estructurada, clara y consistente de los resultados financieros derivados del cálculo de primas, incluyendo el resumen global, el desglose por ubicación, los componentes adicionales (impuestos/recargos) y una vista técnica detallada basada en la trazabilidad del cálculo.

---

### 2. Objetivo de Negocio

Proveer al usuario visibilidad completa y confiable del resultado económico de la cotización, permitiendo tanto una comprensión rápida del costo total como un análisis detallado de cómo se compone la prima, facilitando la toma de decisiones y la auditoría del cálculo.

---

### 3. Alcance Funcional

Incluye:

* Visualización del resumen de prima neta y comercial
* Desglose de primas por ubicación
* Visualización de componentes adicionales (impuestos, recargos)
* Sincronización de resultados con el último cálculo ejecutado
* Vista técnica detallada del cálculo (por componente y ubicación)

No incluye:

* Ejecución del cálculo (FT-004)
* Definición de reglas de negocio o validaciones (FT-011)

---

### 4. Historias de Usuario

| HU     | Nombre                      | Descripción corta                         |
| ------ | --------------------------- | ----------------------------------------- |
| HU-130 | Ver resumen de primas       | Muestra prima neta y comercial total      |
| HU-131 | Ver prima por ubicación     | Muestra desglose por ubicación            |
| HU-132 | Ver componentes adicionales | Muestra impuestos y recargos              |
| HU-133 | Sincronizar resultados      | Garantiza consistencia con último cálculo |
| HU-134 | Vista técnica del cálculo   | Muestra desglose técnico detallado        |

---

### 5. Flujo Funcional

1. Usuario accede a la cotización calculada
2. Sistema consulta el documento persistido de la cotización (incluye resultados y snapshot)
3. Sistema valida estado del cálculo:

   * Si está calculada → muestra resultados
   * Si no → muestra estado “Pendiente de cálculo”
4. UI renderiza resumen financiero (HU-130):

   * Prima neta total
   * Prima comercial total
5. UI renderiza desglose por ubicación (HU-131):

   * Lista/tablas con prima individual
   * Indicador de ubicaciones excluidas
6. UI renderiza componentes adicionales (HU-132):

   * Impuestos
   * Recargos
7. Sistema verifica vigencia del cálculo (HU-133):

   * Si hay cambios posteriores → muestra advertencia de desactualización
8. Usuario puede navegar a vista técnica `/quotes/{folio}/technical-info` (HU-134)
9. Sistema carga snapshot de trazabilidad
10. UI muestra desglose técnico:

* Por ubicación
* Por componente (Incendio, CAT, FHM, etc.)

11. Si una ubicación fue excluida:

* Se muestra con `alertasBloqueantes` y motivo

---

### 6. Dependencias Técnicas

* API de consulta de cotizaciones (read model)
* Persistencia de resultados de cálculo (FT-013)
* Snapshot de trazabilidad de parámetros
* Mecanismo de invalidación de cálculo (post-modificación)
* Frontend:

  * Vista de resultados financieros
  * Ruta técnica `/quotes/{folio}/technical-info`
* Sistema de formateo de moneda

---

### 7. Consideraciones Técnicas

* Separar claramente:

  * **Modelo de escritura (write model)** → cálculo
  * **Modelo de lectura (read model)** → visualización optimizada
* Los datos mostrados deben provenir de un **snapshot inmutable del cálculo**:

  * Evitar recalcular en frontend
* Formateo:

  * Centralizado (ej. utility de currency formatting)
  * Consistente en toda la UI
* Manejo de estados:

  * `PENDIENTE`
  * `CALCULADA`
  * `DESACTUALIZADA`
* Estrategia de invalidación:

  * Cualquier cambio en:

    * ubicaciones
    * coberturas
    * parámetros
      → marca el cálculo como stale
* Vista técnica (HU-134):

  * Basada en trazabilidad (no reconstrucción del cálculo)
  * Estructura recomendada:

    ```json
    {
      "ubicacionId": 1,
      "componentes": [
        { "tipo": "INCENDIO", "valor": 1000 },
        { "tipo": "CAT", "valor": 200 },
        { "tipo": "FHM", "valor": 150 }
      ]
    }
    ```
* UI debe soportar:

  * Estados vacíos
  * Datos parciales
  * Ubicaciones excluidas
* Performance:

  * Evitar payloads excesivos → paginar o lazy load en vista técnica si crece
* Trazabilidad:

  * Clave para auditoría → no opcional en vista técnica
* Consistencia:

  * La suma del desglose debe coincidir exactamente con los totales
* UX:

  * Diferenciar claramente:

    * datos válidos
    * datos excluidos
    * datos desactualizados

---
