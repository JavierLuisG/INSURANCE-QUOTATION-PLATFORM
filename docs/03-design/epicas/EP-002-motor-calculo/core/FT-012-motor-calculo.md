## FT-012: Motor Central de Cálculo de Primas

### HU-054: Cálculo de Prima Neta por Ubicación
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima neta para cada ubicación de riesgo, utilizando las tarifas y factores técnicos correspondientes,
Para obtener el costo base del seguro para cada propiedad asegurada.

**Criterios de Aceptación**:
- Dado que una ubicación tiene todos los datos válidos, cuando se ejecuta el cálculo, entonces se obtiene una prima neta individual para esa ubicación.
- Dado que se aplican las tarifas de incendio correctas según la suma asegurada y tipo de riesgo, cuando se calcula la prima neta, entonces el valor es preciso.
- Dado que hay múltiples ubicaciones, cuando se ejecuta el cálculo, entonces cada una tiene su prima neta calculada de forma independiente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- FT-010 (para acceso a tarifas y factores)
- FT-011 (las validaciones deben pasarse)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima
- Servicio de consulta de parámetros

**Notas de Implementación**:
La lógica de cálculo debe ser modular y fácil de testear. Se recomienda el uso de objetos de valor inmutables para los parámetros de entrada y los resultados intermedios.

**Estado**: Backlog

---
### HU-055: Aplicación de Factores CAT y FHM
**Descripción**:
Como usuario,
Quiero que el sistema aplique los factores de Catástrofe (CAT) y FHM según la zona y condiciones de la ubicación,
Para ajustar la prima neta por estos riesgos específicos y obtener una prima técnica más completa.

**Criterios de Aceptación**:
- Dado que una ubicación está en una zona CAT específica, cuando se calcula la prima, entonces se aplica el factor CAT correspondiente a esa zona.
- Dado que una ubicación cumple con las condiciones para la tarifa FHM, cuando se calcula la prima, entonces se aplica la cuota FHM definida.
- Dado que los factores CAT o FHM no aplican a una ubicación, cuando se calcula la prima, entonces no se incluyen en el cálculo o se usan valores neutros.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-045 (para tarifas CAT)
- HU-046 (para tarifa FHM)
- HU-054 (se aplica sobre la prima neta)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima
- Servicio de aplicación de factores

**Notas de Implementación**:
Asegurar la correcta identificación de la zona para aplicar el factor CAT.

**Estado**: Backlog

---
### HU-056: Cálculo de Prima Comercial Total
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima comercial total de la cotización, a partir de la suma de las primas netas y la aplicación de factores comerciales,
Para conocer el costo final que se presentará al cliente.

**Criterios de Aceptación**:
- Dado que las primas netas de todas las ubicaciones han sido calculadas, cuando se ejecuta el cálculo de prima comercial, entonces se suman las primas netas.
- Dado que existen factores comerciales (e.g., gastos de expedición, impuestos), cuando se calcula la prima comercial, entonces se aplican correctamente sobre la suma de primas netas.
- Dado que no hay factores comerciales, cuando se calcula la prima comercial, entonces el valor es igual a la suma de las primas netas.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-054 (requiere las primas netas por ubicación)
- HU-055 (requiere las primas técnicas ajustadas)

**Componentes Técnicos**:
- Algoritmos de consolidación de primas
- Servicio de aplicación de factores comerciales

**Notas de Implementación**:
Definir los factores comerciales y su orden de aplicación.

**Estado**: Backlog

---
### HU-057: Generación de Desglose de Primas por Ubicación
**Descripción**:Como usuario,
Quiero que el sistema genere el desglose detallado de primas por cada ubicación de riesgo,
Para entender la composición del costo y para fines de auditoría.

**Criterios de Aceptación**:
- Dado que las primas han sido calculadas para cada ubicación, cuando se solicita el desglose, entonces se muestra la prima neta, CAT, FHM y cualquier otro componente por ubicación.
- Dado que el desglose se genera, cuando se compara con el cálculo total, entonces la suma de los componentes individuales coincide con los totales calculados.
- Dado que una ubicación no tiene ciertos factores (e.g., no aplica CAT), cuando se muestra el desglose, entonces esos componentes se muestran como cero o no aplicables.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-054, HU-055

**Componentes Técnicos**:
- Algoritmos de cálculo de prima
- Estructura de datos de resultados

**Notas de Implementación**:
El desglose debe ser claro y fácil de interpretar, posiblemente en un formato estructurado (JSON).

**Estado**: Backlog

---
### HU-058: Cálculo Preciso de Prima de Incendio
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima de incendio basándose en la suma asegurada y las tarifas correspondientes,
Para obtener un valor preciso y fundamental del riesgo de incendio.

**Criterios de Aceptación**:
- Dado que tengo la suma asegurada y la tarifa de incendio aplicable, cuando se invoca el cálculo de prima de incendio, entonces el resultado es `SumaAsegurada * TarifaIncendio`.
- Dado que la tarifa de incendio es variable por zona o tipo de construcción, cuando se calcula, entonces se utiliza la tarifa correcta para la ubicación.
- Dado que la suma asegurada es cero, cuando se calcula la prima de incendio, entonces el resultado es cero.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-044 (para tarifas de incendio)
- HU-054 (es un componente de la prima neta)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima (sub-componente de incendio)

**Notas de Implementación**:
La fórmula debe ser verificada y documentada.

**Estado**: Backlog

---
### HU-059: Cálculo Preciso de Prima de Equipo Electrónico
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima de equipo electrónico basándose en la suma asegurada y los factores correspondientes,
Para obtener un valor preciso para esta cobertura específica.

**Criterios de Aceptación**:
- Dado que tengo la suma asegurada y el factor de equipo electrónico aplicable, cuando se invoca el cálculo de prima de equipo electrónico, entonces el resultado es `SumaAsegurada * FactorEquipoElectronico`.
- Dado que el factor de equipo electrónico es variable por clase o nivel de zona, cuando se calcula, entonces se utiliza el factor correcto para la ubicación.
- Dado que la suma asegurada de equipo electrónico es cero, cuando se calcula la prima, entonces el resultado es cero.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-046 (para factores de equipo electrónico)
- HU-054 (es un componente de la prima neta)

**Componentes Técnicos**:
- Algoritmos de cálculo de prima (sub-componente de equipo electrónico)

**Notas de Implementación**:
La fórmula debe ser verificada y documentada.

**Estado**: Backlog

---
