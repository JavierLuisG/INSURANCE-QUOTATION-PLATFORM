## FT-005: Visualización Detallada de Resultados Financieros

### HU-128: Visualizar Resumen de Prima Neta y Comercial
**Descripción**:
Como usuario,
Quiero ver un resumen claro de la prima neta y comercial total de mi cotización,
Para tener una comprensión rápida del costo global.

**Criterios de Aceptación**:
- Dado que una cotización ha sido calculada, cuando la visualizo, entonces se muestra la prima neta total y la prima comercial total en un área destacada.
- Dado que los valores de la prima son numéricos, cuando se muestran, entonces están formateados correctamente (e.g., moneda, decimales).
- Dado que la cotización no ha sido calculada, cuando la visualizo, entonces los campos de prima total están vacíos o indican "Pendiente de Cálculo".

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-126 (Persistir Resultados del Cálculo de Prima)

**Componentes Técnicos**: Frontend (Interfaz de Resultados Financieros).

**Notas de Implementación**: La información debe ser fácil de encontrar y leer.

**Estado**: Backlog

---
### HU-129: Visualizar Desglose de Prima por Ubicación
**Descripción**:
Como usuario,
Quiero ver el desglose de la prima calculada para cada ubicación de riesgo,
Para entender cómo se distribuye el costo total del seguro.

**Criterios de Aceptación**:
- Dado que una cotización ha sido calculada, cuando visualizo los resultados, entonces se muestra la prima asignada a cada ubicación de riesgo.
- Dado que selecciono una ubicación específica, cuando la visualizo, entonces puedo ver su prima individual en detalle.
- Dado que los valores de la prima por ubicación son numéricos, cuando se muestran, entonces están formateados correctamente.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-126 (Persistir Resultados del Cálculo de Prima)

**Componentes Técnicos**: Frontend (Interfaz de Resultados Financieros, Sección de Ubicaciones).

**Notas de Implementación**: La tabla o lista de ubicaciones debe incluir su prima correspondiente.

**Estado**: Backlog

---
### HU-130: Visualizar Componentes Adicionales de la Prima
**Descripción**:
Como usuario,
Quiero ver los componentes adicionales de la prima, como impuestos y recargos básicos,
Para entender la composición completa del precio final del seguro.

**Criterios de Aceptación**:
- Dado que una cotización ha sido calculada, cuando visualizo los resultados, entonces se muestran los impuestos y recargos básicos aplicados.
- Dado que los componentes adicionales son numéricos, cuando se muestran, entonces están formateados correctamente.
- Dado que no hay impuestos o recargos aplicables, cuando visualizo los resultados, entonces estos campos no se muestran o indican "N/A".

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-126 (Persistir Resultados del Cálculo de Prima)

**Componentes Técnicos**: Frontend (Interfaz de Resultados Financieros, Sección de Desglose).

**Notas de Implementación**: Solo se mostrarán los impuestos y recargos básicos definidos en el cálculo.

**Estado**: Backlog

---
### HU-131: Sincronizar Visualización de Resultados Financieros
**Descripción**:
Como usuario,
Quiero que los resultados financieros mostrados estén siempre sincronizados con el último cálculo realizado,
Para asegurar que la información es actual y precisa.

**Criterios de Aceptación**:
- Dado que se ha realizado un nuevo cálculo de prima, cuando accedo a la sección de resultados, entonces se muestran los resultados del cálculo más reciente.
- Dado que se han realizado modificaciones a la cotización (ubicaciones, coberturas) después de un cálculo, cuando visualizo los resultados, entonces se muestra una advertencia de que el cálculo puede estar desactualizado o se invalida el cálculo anterior.
- Dado que un cálculo falla, cuando accedo a los resultados, entonces se muestra un mensaje de error y no se muestran resultados desactualizados.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-126 (Persistir Resultados del Cálculo de Prima), HU-137 (Cualquier Modificación Invalida Cálculo)

**Componentes Técnicos**: Frontend (Lógica de Actualización de UI), Backend (API de Consulta de Cotizaciones).

**Notas de Implementación**: El sistema debe tener un mecanismo para invalidar o marcar como desactualizado un cálculo si los datos de la cotización cambian.

**Estado**: Backlog

---
