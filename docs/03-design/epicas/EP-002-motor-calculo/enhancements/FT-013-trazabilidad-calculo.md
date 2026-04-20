## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

### HU-170: Persistir Prima Neta y Comercial en Cotización
**Descripción**:
Como sistema,
Quiero persistir la prima neta, prima comercial y el desglose por ubicación como parte del documento de cotización en MongoDB,
Para que los resultados financieros sean intrínsecos a la cotización.

**Criterios de Aceptación**:
- Dado que se ha completado el cálculo, cuando se guardan los resultados, entonces los valores de prima neta total, prima comercial total y el desglose por ubicación se añaden al documento de la cotización.
- Dado que el documento de cotización se consulta, cuando se recupera, entonces contiene todos los resultados del cálculo.
- Dado que los resultados se persisten, cuando se guarda, entonces se aseguran los tipos de datos correctos (e.g., numéricos, decimales).

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-126 (Persistir Resultados del Cálculo de Prima), HU-167 (Calcular Prima Comercial Total)

**Componentes Técnicos**: Backend (Repositorio de Cotizaciones), Base de Datos (MongoDB).

**Notas de Implementación**: Diseño de esquema de datos en MongoDB para resultados de cálculo.

**Estado**: Backlog

---
### HU-171: Asegurar Persistencia Atómica del Cálculo
**Descripción**:
Como sistema,
Quiero asegurar que la operación de persistencia del cálculo es atómica,
Para garantizar que todos los resultados se guarden o ninguno, manteniendo la consistencia.

**Criterios de Aceptación**:
- Dado que se intenta guardar los resultados del cálculo, cuando la operación es exitosa, entonces todos los componentes de la prima (neta, comercial, desglose) se guardan juntos.
- Dado que ocurre un error durante la persistencia de los resultados, cuando la operación falla, entonces ningún resultado parcial se guarda y el estado de la cotización no se actualiza a "Calculada".
- Dado que la persistencia es atómica, cuando se completa, entonces la cotización en la base de datos es consistente con el cálculo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-170 (Persistir Prima Neta y Comercial en Cotización)

**Componentes Técnicos**: Backend (Módulo de Persistencia de Resultados, Transacciones en MongoDB).

**Notas de Implementación**: Requiere un manejo cuidadoso de las transacciones o la simulación de atomicidad en MongoDB.

**Estado**: Backlog

---
### HU-172: Actualizar Metadatos de Cotización Tras Persistencia de Cálculo
**Descripción**:
Como sistema,
Quiero que el sistema actualice el campo `fechaUltimaActualizacion` y el número de versión de la cotización tras cada persistencia de cálculo,
Para reflejar que la cotización ha sido modificada y sus resultados financieros actualizados.

**Criterios de Aceptación**:
- Dado que se persisten los resultados de un cálculo, cuando la operación es exitosa, entonces el campo `fechaUltimaActualizacion` de la cotización se actualiza.
- Dado que se persisten los resultados de un cálculo, cuando la operación es exitosa, entonces el número de `version` de la cotización se incrementa.
- Dado que los metadatos se actualizan, cuando se consulta la cotización, entonces reflejan los cambios del cálculo.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-145 (Incrementar Versión en Ediciones de Cotización), HU-146 (Actualizar Fecha de Última Actualización en Ediciones)

**Componentes Técnicos**: Backend (Módulo de Persistencia de Resultados).

**Notas de Implementación**: La actualización de metadatos debe ser parte de la misma operación atómica de persistencia de cálculo.

**Estado**: Backlog

---
### HU-173: Registrar Snapshot para Trazabilidad del Cálculo
**Descripción**:
Como sistema,
Quiero registrar un snapshot de parámetros de entrada relevantes, identificadores y valores de tarifas/factores utilizados, el resultado detallado del cálculo y metadatos de ejecución,
Para permitir la trazabilidad y auditoría de cómo se llegó a un resultado específico.

**Criterios de Aceptación**:
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se guarda un snapshot de los datos clave de entrada (sumas aseguradas, coberturas, datos clave de ubicación).
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se registran los identificadores y versión lógica de tarifas/factores utilizados (tipo de tarifa, versión o timestamp).
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se registran los valores numéricos concretos de los factores aplicados y el resultado detallado del cálculo.
- Dado que se realiza un cálculo exitoso, cuando se persisten los resultados, entonces se guardan metadatos de ejecución (fecha/hora del cálculo, versión de la cotización).
- Dado que se necesita auditar un cálculo, cuando se consulta el snapshot, entonces es posible reconstruir los insumos y lógica aplicados.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-160 (Parámetros Disponibles para Motores), HU-169 (Asegurar Precisión del Cálculo)

**Componentes Técnicos**: Backend (Componente de Auditoría/Logging de Cálculo, Repositorio de Cotizaciones).

**Notas de Implementación**: El diseño del esquema de datos debe evitar duplicidades innecesarias y facilitar consultas de trazabilidad.

**Estado**: Backlog

---
