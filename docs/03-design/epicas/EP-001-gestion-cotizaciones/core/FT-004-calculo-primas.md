## FT-004: Ejecución y Persistencia del Cálculo de Primas

### HU-015: Iniciar el cálculo de primas de la cotización

Como usuario, quiero iniciar el proceso de cálculo de la prima de la cotización, para obtener los resultados financieros.

**Criterios de Aceptación**:
- Dado que he completado los datos generales y al menos una ubicación con coberturas, cuando hago clic en el botón "Calcular Prima", entonces el sistema inicia el proceso de cálculo.
- Dado que la cotización tiene datos incompletos o inválidos, cuando intento calcular la prima, entonces el sistema me impide el cálculo y muestra los errores.
- Dado que el cálculo se está ejecutando, cuando el usuario interactúa con la interfaz, entonces se muestra un indicador de carga para evitar acciones duplicadas.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-001: Creación y Edición de Datos Generales de la Cotización
- FT-002: Gestión Dinámica de Ubicaciones de Riesgo
- FT-003: Configuración y Selección de Coberturas por Ubicación
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Botón "Calcular Prima", Indicador de carga.
- Backend: API de cotizaciones (endpoint de cálculo).

**Notas de Implementación**:
- El botón de cálculo debe estar deshabilitado si faltan datos críticos.
- La ejecución del cálculo puede ser un proceso síncrono o asíncrono dependiendo de la complejidad.

**Estado**: Backlog

---
### HU-016: Calcular prima neta y comercial total de la cotización

Como usuario, quiero que el sistema calcule la prima neta y comercial total de la cotización, para conocer el costo final del seguro.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo de la prima, cuando este finaliza exitosamente, entonces el sistema devuelve la prima neta total y la prima comercial total de la cotización.
- Dado que el cálculo incluye factores técnicos y reglas de negocio, cuando se realiza el cálculo, entonces estos se aplican correctamente para obtener los valores finales.
- Dado que los resultados del cálculo se obtienen, cuando se consultan, entonces se muestran con al menos dos decimales y formato de moneda.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-007: Integración con Servicios de Referencia (Tarifas)
- FT-009: Implementación de Reglas de Negocio y Validaciones
**Componentes Técnicos**:
- Backend: Motor de cálculo de primas, Módulo de reglas de negocio.

**Notas de Implementación**:
- La lógica de cálculo debe ser modular y testeable.
- Se debe asegurar la trazabilidad de los factores y reglas aplicadas.

**Estado**: Backlog

---
### HU-017: Calcular y mostrar prima por cada ubicación de riesgo

Como usuario, quiero que el sistema calcule y desglose la prima para cada ubicación de riesgo individualmente, para entender la contribución de cada una al total.

**Criterios de Aceptación**:
- Dado que se ha iniciado el cálculo, cuando este finaliza, entonces el sistema devuelve la prima (neta y comercial) desglosada por cada ubicación de riesgo.
- Dado que una ubicación no tiene coberturas o tiene datos inválidos, cuando se realiza el cálculo, entonces su prima se muestra como cero o con un indicador de error.
- Dado que el cálculo por ubicación se ha completado, cuando se visualizan los resultados, entonces cada ubicación muestra su prima correspondiente.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia
**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-002: Gestión Dinámica de Ubicaciones de Riesgo
- FT-003: Configuración y Selección de Coberturas por Ubicación

**Componentes Técnicos**:
- Backend: Motor de cálculo de primas (lógica por ubicación).
- Frontend: Interfaz de visualización de resultados por ubicación.

**Notas de Implementación**:
- La agregación de primas individuales debe coincidir con la prima total.
- Se debe manejar la escala y precisión de los valores monetarios.

**Estado**: Backlog

---
### HU-018: Persistir resultados del cálculo de primas con la cotización

Como usuario, quiero que los resultados del cálculo de la prima (neta, comercial, por ubicación) se guarden de forma persistente con la cotización, para consultarlos posteriormente.

**Criterios de Aceptación**:
- Dado que un cálculo de prima se ha realizado exitosamente, cuando se guardan los resultados, entonces estos se asocian a la versión actual de la cotización.
- Dado que se ha guardado una cotización con resultados de cálculo, cuando se carga la cotización, entonces los resultados financieros previamente calculados se muestran.
- Dado que se realiza un nuevo cálculo, cuando se guardan los nuevos resultados, entonces sobrescriben los anteriores o se versionan si aplica.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-016: Calcular prima neta y comercial total de la cotización
- HU-017: Calcular y mostrar prima por cada ubicación de riesgo
- FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

**Componentes Técnicos**:
- Backend: API de persistencia de cotizaciones (actualización de resultados financieros).
- Base de Datos: Esquema de almacenamiento de resultados de cálculo.

**Notas de Implementación**:
- La persistencia de los resultados debe ser transaccional con la cotización.
- Se debe considerar si se requiere un historial de cálculos o solo el último.

**Estado**: Backlog

---
### HU-019: Aplicar factores técnicos y reglas de negocio en el cálculo

Como usuario, quiero que el cálculo de primas incorpore los factores técnicos y reglas de negocio definidos, para asegurar la precisión y validez del precio.

**Criterios de Aceptación**:
- Dado que existen factores técnicos (e.g., tasas, recargos) y reglas de negocio (e.g., descuentos por antigüedad), cuando se ejecuta el cálculo, entonces estos se aplican automáticamente.
- Dado que una regla de negocio requiere una validación específica, cuando los datos no la cumplen, entonces el cálculo se detiene o se ajusta según la regla.
- Dado que los factores y reglas se han aplicado, cuando se audita el cálculo, entonces se puede trazar cómo se llegó al resultado final.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-015: Iniciar el cálculo de primas de la cotización
- FT-007: Integración con Servicios de Referencia (Tarifas)
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Backend: Motor de reglas de negocio, Módulo de aplicación de factores técnicos.

**Notas de Implementación**:
- La gestión de reglas y factores debe ser parametrizable y no "hardcodeada".
- Se debe documentar la lógica de cada regla y factor aplicado.

**Estado**: Backlog

---
