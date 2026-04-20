## FT-004: Ejecución y Persistencia del Cálculo de Primas

### HU-123: Iniciar Proceso de Cálculo de Prima
**Descripción**:
Como usuario,
Quiero solicitar el cálculo de la prima de mi cotización,
Para obtener los resultados financieros actualizados.

**Criterios de Aceptación**:
- Dado que tengo una cotización con datos válidos, cuando hago clic en el botón "Calcular Prima", entonces el sistema inicia el proceso de cálculo.
- Dado que la cotización tiene errores de validación, cuando intento calcular la prima, entonces el sistema me lo impide y muestra los errores.
- Dado que el cálculo se inicia, cuando está en progreso, entonces la interfaz muestra un indicador de carga.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-147 (Prevenir Cálculo con Errores de Validación), HU-149 (Validación de Datos por Ubicación)

**Componentes Técnicos**: Frontend (Botón "Calcular Prima"), Backend (Endpoint de Cálculo de Prima).

**Notas de Implementación**: El botón debe estar deshabilitado si hay validaciones pendientes.

**Estado**: Backlog

---
### HU-124: Calcular Prima Neta y Comercial Total
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima neta y comercial total de la cotización,
Para conocer el costo global del seguro.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo, cuando finaliza exitosamente, entonces el sistema calcula la prima neta total sumando las primas netas de las ubicaciones.
- Dado que se ha iniciado el cálculo, cuando finaliza exitosamente, entonces el sistema calcula la prima comercial total aplicando factores comerciales y otros recargos a la prima neta total.
- Dado que los cálculos se realizan, cuando se completan, entonces los resultados están disponibles para persistencia y visualización.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-125 (Calcular Prima por Ubicación), HU-143 (Calcular Prima Comercial Total)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas).

**Notas de Implementación**: Las fórmulas de cálculo deben ser las simplificadas y documentadas.

**Estado**: Backlog

---
### HU-125: Calcular Prima por Ubicación de Riesgo
**Descripción**:
Como usuario,
Quiero que el sistema calcule la prima para cada ubicación de riesgo individualmente,
Para entender el desglose del costo por cada lugar asegurado.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo, cuando finaliza exitosamente, entonces el sistema calcula la prima neta para cada ubicación de riesgo.
- Dado que la ubicación tiene coberturas y parámetros, cuando se calcula su prima, entonces se aplican las tarifas y factores técnicos correspondientes.
- Dado que se calcula la prima por ubicación, cuando se completa, entonces el resultado está disponible para persistencia y visualización.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-121 (Configurar Parámetros de Cobertura), HU-141 (Calcular Prima Neta por Ubicación)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas).

**Notas de Implementación**: Los factores técnicos (incendio, CAT, FHM, equipo electrónico) deben ser aplicados por ubicación.

**Estado**: Backlog

---
### HU-126: Persistir Resultados del Cálculo de Prima
**Descripción**:
Como usuario,
Quiero que los resultados del cálculo (prima neta, comercial, por ubicación) se guarden con la cotización,
Para que estén disponibles para consulta futura y no se pierdan.

**Criterios de Aceptación**:
- Dado que el cálculo de la prima ha finalizado exitosamente, cuando se guardan los resultados, entonces la prima neta total, comercial total y el desglose por ubicación se persisten en la cotización.
- Dado que los resultados se persisten, cuando se guarda la cotización, entonces la operación es atómica y consistente.
- Dado que los resultados se persisten, cuando se guarda la cotización, entonces el estado de la cotización cambia a "Calculada".

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-124 (Calcular Prima Neta y Comercial Total), HU-125 (Calcular Prima por Ubicación), HU-159 (Persistir Prima Neta y Comercial)

**Componentes Técnicos**: Backend (API de Persistencia de Cotizaciones), Base de Datos (MongoDB).

**Notas de Implementación**: La persistencia debe incluir el versionado optimista.

**Estado**: Backlog

---
### HU-127: Aplicar Reglas de Negocio y Factores Técnicos en Cálculo
**Descripción**:
Como usuario,
Quiero que el cálculo de la prima considere las reglas de negocio y los factores técnicos definidos,
Para asegurar que la prima refleje correctamente el riesgo y las políticas de la compañía.

**Criterios de Aceptación**:
- Dado que se realiza un cálculo, cuando se aplican las reglas de negocio, entonces los recargos o descuentos se consideran en la prima final.
- Dado que se realiza un cálculo, cuando se aplican los factores técnicos (e.g., CAT, FHM), entonces el costo de la prima por ubicación se ajusta según estos factores.
- Dado que los factores técnicos son obtenidos de catálogos, cuando se aplican, entonces se utilizan los valores correctos y actualizados.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia
**Dependencias**: HU-145 (Aplicar Factores de Catástrofe y FHM), HU-149 (Validación de Datos por Ubicación)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas, Módulo de Reglas de Negocio).

**Notas de Implementación**: La lógica de aplicación de reglas y factores debe ser modular y testeable.

**Estado**: Backlog

---
