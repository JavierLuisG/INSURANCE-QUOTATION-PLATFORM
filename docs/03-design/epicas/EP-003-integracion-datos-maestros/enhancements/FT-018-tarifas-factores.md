## FT-018: Conectividad y Consumo de Tarifas y Factores Técnicos

### HU-188: Consultar Tarifas y Factores Técnicos Requeridos
**Descripción**:
Como sistema,
Quiero poder consultar las tarifas y factores técnicos requeridos desde `Plataforma-core-ohs` (o su mock),
Para obtener los valores que se usarán en el cálculo de las primas.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere una tarifa (e.g., incendio) o un factor (e.g., equipo electrónico), cuando el sistema lo consulta, entonces obtiene el valor correspondiente del servicio externo.
- Dado que el servicio externo no devuelve un valor para una consulta, cuando se realiza, entonces el sistema maneja la ausencia de datos y notifica.
- Dado que la consulta es exitosa, cuando se completa, entonces el valor está disponible para el cálculo.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Cliente API REST para `Plataforma-core-ohs`).

**Notas de Implementación**: Asegurar que las consultas sean específicas y eficientes para cada tipo de tarifa/factor.

**Estado**: Backlog

---
### HU-189: Mapear Datos de Tarifas y Factores para Cálculo
**Descripción**:
Como sistema,
Quiero que los datos de tarifas y factores se recuperen y mapeen correctamente para ser utilizados en la lógica de cálculo de primas,
Para asegurar que los valores se interpretan y aplican de forma precisa.

**Criterios de Aceptación**:
- Dado que se recibe un dato de tarifa o factor del servicio externo, cuando se procesa, entonces se mapea a la estructura de datos que el motor de cálculo espera.
- Dado que el mapeo es exitoso, cuando se completa, entonces el motor de cálculo puede usar el valor directamente en sus fórmulas.
- Dado que el dato de tarifa o factor es inconsistente o requiere transformación, cuando se mapea, entonces se aplican las reglas de transformación necesarias.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-188 (Consultar Tarifas y Factores Técnicos Requeridos)

**Componentes Técnicos**: Backend (Módulo de Adaptación de Tarifas).

**Notas de Implementación**: El mapeo debe ser robusto para manejar diferentes estructuras de datos de tarifas.

**Estado**: Backlog

---
### HU-190: Manejar Errores y Ausencia de Datos en Tarifas
**Descripción**:
Como sistema,
Quiero manejar los errores de conexión o la ausencia/inconsistencia de datos en las tarifas, notificando al sistema o usuario,
Para asegurar la robustez del cálculo y la integridad de los resultados.

**Criterios de Aceptación**:
- Dado que el servicio de tarifas no está disponible, cuando se intenta consultar, entonces el sistema registra un error y puede usar un valor por defecto o fallar el cálculo.
- Dado que una tarifa requerida devuelve un valor inconsistente o nulo, cuando se utiliza en el cálculo, entonces el sistema lo detecta y notifica.
- Dado que hay un error o ausencia de datos en tarifas, cuando se notifica, entonces se proporciona información suficiente para el diagnóstico.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-188 (Consultar Tarifas y Factores Técnicos Requeridos)

**Componentes Técnicos**: Backend (Módulo de Manejo de Errores, Servicio de Notificación).

**Notas de Implementación**: Definir políticas de fallback o valores por defecto para errores de tarifas.

**Estado**: Backlog

---
