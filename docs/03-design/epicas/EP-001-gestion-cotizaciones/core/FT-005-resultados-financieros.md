## FT-005: Visualización Detallada de Resultados Financieros

### HU-020: Visualizar resumen de prima neta y comercial total

Como usuario, quiero ver un resumen claro de la prima neta y comercial total de la cotización, para tener una visión global del costo.

**Criterios de Aceptación**:
- Dado que la cotización ha sido calculada, cuando accedo a la sección de resultados financieros, entonces se muestran la prima neta total y la prima comercial total.
- Dado que los resultados se muestran, cuando se consulta la información, entonces los valores son los del último cálculo realizado.
- Dado que la cotización no ha sido calculada, cuando accedo a la sección, entonces se muestra un mensaje indicando que el cálculo no ha sido ejecutado.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-016: Calcular prima neta y comercial total de la cotización
- HU-018: Persistir resultados del cálculo de primas con la cotización

**Componentes Técnicos**:
- Frontend: Componente de resumen de primas totales.
- Backend: API de cotizaciones (consulta de resultados financieros).

**Notas de Implementación**:
- Los valores deben presentarse con formato de moneda y decimales apropiados.
- La interfaz debe ser reactiva a los cambios en el estado de cálculo.

**Estado**: Backlog

---
### HU-021: Visualizar desglose de prima por cada ubicación de riesgo

Como usuario, quiero ver el desglose de la prima (neta y comercial) por cada ubicación de riesgo, para entender el costo asociado a cada una.

**Criterios de Aceptación**:
- Dado que la cotización ha sido calculada, cuando accedo a la sección de resultados financieros, entonces se presenta una lista de ubicaciones, cada una con su prima neta y comercial.
- Dado que una ubicación fue eliminada, cuando se visualizan los resultados, entonces su prima ya no aparece en el desglose.
- Dado que se ha realizado un nuevo cálculo, cuando se consulta el desglose, entonces los valores se actualizan para reflejar el último cálculo.
**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-017: Calcular y mostrar prima por cada ubicación de riesgo
- HU-018: Persistir resultados del cálculo de primas con la cotización

**Componentes Técnicos**:
- Frontend: Tabla o lista de desglose de primas por ubicación.

**Notas de Implementación**:
- Se debe asegurar que el desglose sea coherente con el cálculo total.
- La interfaz debe permitir ordenar o filtrar las ubicaciones si hay muchas.

**Estado**: Backlog

---
### HU-022: Visualizar componentes adicionales del precio (impuestos, recargos)

Como usuario, quiero ver los componentes adicionales que afectan el precio final (impuestos, recargos, descuentos), para comprender la composición del costo.

**Criterios de Aceptación**:
- Dado que la cotización ha sido calculada, cuando accedo a la sección de resultados financieros, entonces se muestran los impuestos, recargos y descuentos aplicados.
- Dado que se aplica un recargo específico, cuando se visualizan los resultados, entonces este recargo se lista con su valor.
- Dado que no se aplican impuestos o recargos, cuando se visualizan los resultados, entonces estas secciones no aparecen o muestran cero.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-016: Calcular prima neta y comercial total de la cotización
- HU-019: Aplicar factores técnicos y reglas de negocio en el cálculo

**Componentes Técnicos**:
- Frontend: Sección de desglose de componentes adicionales.
- Backend: API de cotizaciones (consulta de componentes financieros).

**Notas de Implementación**:
- La presentación de estos componentes debe ser clara y fácil de interpretar.
- Se debe considerar si estos componentes se desglosan a nivel total o también por ubicación.

**Estado**: Backlog

---
### HU-023: Sincronizar resultados financieros con el último cálculo

Como usuario, quiero que la información de los resultados financieros siempre refleje el último cálculo exitoso de la cotización, para garantizar la veracidad de los datos.

**Criterios de Aceptación**:
- Dado que se ha realizado un nuevo cálculo de la cotización, cuando se consultan los resultados, entonces los valores mostrados corresponden a este último cálculo.
- Dado que no se ha realizado ningún cálculo o el último falló, cuando se consultan los resultados, entonces se muestra un estado que lo indica.
- Dado que se modifican datos de la cotización después de un cálculo, cuando se consultan los resultados, entonces se indica que el cálculo actual podría estar desactualizado.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-018: Persistir resultados del cálculo de primas con la cotización
- FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

**Componentes Técnicos**:
- Frontend: Lógica de sincronización de UI con el estado del cálculo.
- Backend: API de cotizaciones (versión del cálculo).

**Notas de Implementación**:
- Se puede usar un campo de fecha/hora de último cálculo para la sincronización.
- Considerar un mensaje de advertencia si la cotización ha sido modificada desde el último cálculo.

**Estado**: Backlog

---
