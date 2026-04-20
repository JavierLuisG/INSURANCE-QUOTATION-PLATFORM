## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

### HU-060: Persistencia de Resultados de Cálculo en Cotización
**Descripción**:
Como usuario,
Quiero que los resultados de la prima neta, prima comercial y el desglose por ubicación se guarden automáticamente en el documento de cotización en MongoDB,
Para que estén disponibles para consulta futura y no se pierdan al cerrar la aplicación.

**Criterios de Aceptación**:
- Dado que se ha ejecutado un cálculo exitoso, cuando se invoca la persistencia, entonces la prima neta total, prima comercial y el desglose por ubicación se guardan en el documento de cotización.
- Dado que los resultados de cálculo son guardados, cuando consulto la cotización, entonces puedo ver los valores financieros actualizados.
- Dado que un resultado no se puede guardar (e.g., error de DB), cuando se intenta la persistencia, entonces la operación es revertida y se notifica el error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-012 (necesita los resultados del cálculo)

**Componentes Técnicos**:
- Repositorio de cotizaciones (MongoDB)
- Módulo de persistencia de resultados

**Notas de Implementación**:
El esquema de datos de MongoDB debe ser diseñado para almacenar estos resultados de manera eficiente y consultable.

**Estado**: Backlog

---
### HU-061: Atomicidad en la Persistencia del Cálculo
**Descripción**:
Como desarrollador,
Quiero que la operación de guardar los resultados del cálculo sea atómica,
Para asegurar que todos los datos se guarden correctamente o ninguno, manteniendo la consistencia del documento de cotización.

**Criterios de Aceptación**:
- Dado que se intenta guardar los resultados de cálculo, cuando ocurre un fallo en la mitad de la operación, entonces ningún cambio parcial se persiste en la base de datos.
- Dado que la operación de persistencia se completa con éxito, cuando se verifica el documento en la base de datos, entonces todos los resultados de cálculo están presentes y son consistentes.
- Dado que se utiliza una transacción o enfoque atómico, cuando se guardan los resultados, entonces la integridad de los datos está garantizada.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-060
**Componentes Técnicos**:
- Repositorio de cotizaciones (MongoDB)
- Manejo de transacciones o "all-or-nothing" en MongoDB

**Notas de Implementación**:
Considerar el uso de transacciones de MongoDB (si aplica a la versión y configuración) o un patrón de "dos fases commit" simulado si es necesario para asegurar la atomicidad.

**Estado**: Backlog

---
### HU-062: Actualización de Metadatos de Cotización
**Descripción**:
Como desarrollador,
Quiero que al guardar los resultados del cálculo, se actualice el campo `fechaUltimaActualizacion` y el número de versión de la cotización,Para mantener un registro de cambios y facilitar la gestión de concurrencia.

**Criterios de Aceptación**:
- Dado que se persiste un cálculo exitoso, cuando se guarda la cotización, entonces el campo `fechaUltimaActualizacion` se actualiza con la fecha y hora actuales.
- Dado que se persiste un cálculo exitoso, cuando se guarda la cotización, entonces el número de versión de la cotización se incrementa en uno.
- Dado que la cotización se guarda sin cambios en los resultados de cálculo, cuando se verifica, entonces solo la `fechaUltimaActualizacion` y la versión se actualizan.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-060
- FT-014 (trabaja en conjunto con el versionado optimista)

**Componentes Técnicos**:
- Repositorio de cotizaciones
- Módulo de actualización de metadatos

**Notas de Implementación**:
Esta actualización debe ser parte de la misma operación atómica de persistencia.

**Estado**: Backlog

---
### HU-063: Registro de Parámetros para Trazabilidad
**Descripción**:
Como auditor,
Quiero que el sistema registre los parámetros y tarifas clave utilizados para un cálculo específico,
Para poder auditar y entender cómo se llegó a un resultado específico en cualquier momento.

**Criterios de Aceptación**:
- Dado que se ejecuta un cálculo de prima, cuando los resultados se persisten, entonces los valores clave de tarifas (incendio, CAT, FHM) y otros parámetros (rangos, CP-zona) se registran junto con la cotización.
- Dado que se consulta una cotización histórica, cuando se visualiza su trazabilidad, entonces se muestran los parámetros exactos usados en ese cálculo.
- Dado que un parámetro utilizado en el cálculo cambia su valor en el tiempo, cuando se audita un cálculo anterior, entonces se ve el valor del parámetro en el momento del cálculo, no el actual.

**Prioridad**: Media

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-060 (se registra al persistir)
- FT-010 (son los parámetros a registrar)

**Componentes Técnicos**:
- Componente de auditoría/logging de cálculo
- Diseño de esquema de datos de cotización (para almacenar info de trazabilidad)

**Notas de Implementación**:
Decidir qué nivel de detalle de parámetros se debe almacenar para no sobrecargar el documento de cotización, quizás solo IDs de versiones de catálogos o un hash de los parámetros.

**Estado**: Backlog

---
