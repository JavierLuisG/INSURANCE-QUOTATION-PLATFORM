## FT-012: Motor Central de Cálculo de Primas
### HU-146: Calcular Prima Neta por Ubicación
**Descripción**:
Como sistema,
Quiero calcular la prima neta para cada ubicación de riesgo utilizando las tarifas y factores correspondientes,
Para determinar el costo base de la cobertura por cada lugar asegurado.

**Criterios de Aceptación**:
- Dado que se proporciona una ubicación con sus datos y coberturas, cuando se ejecuta el cálculo, entonces se obtiene la prima neta para esa ubicación.
- Dado que la ubicación tiene tarifas de incendio y factores aplicables, cuando se calcula la prima, entonces estos se usan en la fórmula.
- Dado que el cálculo es exitoso, cuando se completa, entonces la prima neta resultante es un valor numérico preciso.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-125 (Calcular Prima por Ubicación de Riesgo), HU-160 (Parámetros Disponibles para Motores)

**Componentes Técnicos**: Backend (Algoritmos de Cálculo de Prima en el Motor de Cálculo).

**Notas de Implementación**: Las fórmulas simplificadas deben ser implementadas con alta precisión.

**Estado**: Backlog

---
### HU-166: Aplicar Factores de Catástrofe (CAT) y FHM
**Descripción**:
Como sistema,
Quiero aplicar los factores de Catástrofe (CAT) y FHM según la zona y condiciones de la ubicación,
Para ajustar la prima neta por el riesgo específico de eventos catastróficos.

**Criterios de Aceptación**:
- Dado que una ubicación está en una zona CAT, cuando se calcula la prima, entonces el factor CAT correspondiente se aplica a la prima neta.
- Dado que una ubicación cumple las condiciones FHM, cuando se calcula la prima, entonces la cuota FHM correspondiente se aplica.
- Dado que los factores se aplican, cuando se completa el cálculo, entonces el resultado refleja el ajuste por CAT y FHM.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-146 (Calcular Prima Neta por Ubicación), HU-156 (Consumir Tarifas de Catástrofe (CAT)), HU-157 (Consumir Tarifa FHM)

**Componentes Técnicos**: Backend (Servicio de Aplicación de Factores en el Motor de Cálculo).

**Notas de Implementación**: La lógica de aplicación debe ser clara y basada en los catálogos de zonas.

**Estado**: Backlog

---
### HU-167: Calcular Prima Comercial Total
**Descripción**:
Como sistema,
Quiero calcular la prima comercial total de la cotización a partir de la suma de las primas netas y la aplicación de factores comerciales,
Para determinar el precio final que se presenta al cliente.

**Criterios de Aceptación**:
- Dado que se han calculado las primas netas por ubicación, cuando se ejecuta el cálculo, entonces se suman para obtener la prima neta total.
- Dado que la prima neta total se ha obtenido, cuando se calcula la prima comercial, entonces se aplican los factores comerciales (e.g., recargos administrativos, impuestos).
- Dado que el cálculo es exitoso, cuando se completa, entonces la prima comercial resultante es un valor numérico preciso.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia
**Dependencias**: HU-146 (Calcular Prima Neta por Ubicación)

**Componentes Técnicos**: Backend (Consolidación de Primas en el Motor de Cálculo).

**Notas de Implementación**: Los factores comerciales deben estar definidos y ser accesibles.

**Estado**: Backlog

---
### HU-168: Generar Desglose de Primas por Ubicación
**Descripción**:
Como sistema,
Quiero generar el desglose de primas por cada ubicación de riesgo,
Para proporcionar una vista detallada de cómo se compone el costo total.

**Criterios de Aceptación**:
- Dado que se han calculado las primas por ubicación, cuando se completa el proceso, entonces el desglose de primas por cada ubicación está disponible.
- Dado que el desglose se genera, cuando se almacena, entonces incluye la prima neta, y los ajustes por factores para cada ubicación.
- Dado que el desglose se genera, cuando se consulta, entonces la información es consistente con el cálculo total.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-146 (Calcular Prima Neta por Ubicación)

**Componentes Técnicos**: Backend (Consolidación de Primas en el Motor de Cálculo).

**Notas de Implementación**: El desglose debe ser granular y fácil de interpretar.

**Estado**: Backlog

---
### HU-169: Asegurar Precisión del Cálculo Según Fórmulas Simplificadas
**Descripción**:
Como sistema,
Quiero que los cálculos sean 100% precisos según las fórmulas simplificadas y documentadas,
Para garantizar la fiabilidad de los resultados financieros.

**Criterios de Aceptación**:
- Dado que se ejecuta un cálculo, cuando se compara el resultado con un cálculo manual basado en las fórmulas documentadas, entonces ambos coinciden.
- Dado que se modifican los parámetros de entrada, cuando se recalcula, entonces el resultado se ajusta de forma predecible según las fórmulas.
- Dado que se implementa una fórmula, cuando se prueba, entonces la cobertura unitaria es alta (>90%).

**Prioridad**: Alta

**Estimación**: 5 puntos de historia

**Dependencias**: Todas las HUs de cálculo (HU-146, HU-166, HU-167, HU-168)

**Componentes Técnicos**: Backend (Algoritmos de Cálculo de Prima, Pruebas Unitarias).

**Notas de Implementación**: La lógica de cálculo se basará en la interpretación directa de los datos proporcionados por el servicio, utilizando fórmulas simplificadas definidas en el alcance del proyecto, sin implementar lógica actuarial compleja o inferida.

**Estado**: Backlog

---
