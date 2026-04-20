## FT-010: Configuración y Gestión de Parámetros de Cálculo

### HU-044: Ingestión de Tarifas de Incendio
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta de tarifas de incendio desde el servicio `Plataforma-core-ohs`,
Para que el motor de cálculo tenga acceso a los datos más recientes y correctos de incendio.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga de tarifas de incendio, entonces el sistema obtiene las tarifas correctamente.
- Dado que las tarifas de incendio son cargadas, cuando el motor de cálculo las consulta, entonces recibe los valores actualizados.
- Dado que ocurre un error al obtener las tarifas de incendio, cuando el sistema intenta cargarlas, entonces se registra el error y se notifica al administrador.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de parámetros de cálculo

**Notas de Implementación**:
La simulación debe replicar fielmente el contrato de la API real. Se debe considerar un mecanismo de caché para reducir llamadas repetidas si la latencia es un problema.

**Estado**: Backlog

---
### HU-045: Ingestión de Tarifas CAT
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta de tarifas de catástrofe (CAT) desde el servicio `Plataforma-core-ohs`,
Para que el motor de cálculo aplique los factores de catástrofe correctos según la zona.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga de tarifas CAT, entonces el sistema obtiene los factores correctamente.
- Dado que las tarifas CAT son cargadas, cuando el motor de cálculo las consulta para una zona específica, entonces recibe los valores actualizados.
- Dado que los factores CAT tienen fechas de vigencia, cuando el sistema los carga, entonces se consideran estas fechas para la disponibilidad.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)
- HU-044 (patrón similar de integración)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de parámetros de cálculo

**Notas de Implementación**:
Asegurar que el mapeo de zonas CAT sea consistente con el `catalogo_cp_zonas`.

**Estado**: Backlog

---
### HU-046: Ingestión de Tarifa FHM y Factores de Equipo Electrónico
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta de la tarifa FHM y los factores de equipo electrónico desde `Plataforma-core-ohs`,
Para asegurar cálculos precisos en estas coberturas específicas.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga de tarifa FHM y factores de equipo electrónico, entonces el sistema los obtiene.
- Dado que los parámetros FHM y de equipo electrónico son cargados, cuando el motor de cálculo los requiere, entonces están disponibles y actualizados.
- Dado que un factor es nulo o inválido en la fuente, cuando el sistema lo ingesta, entonces se maneja el error o se utiliza un valor por defecto configurable.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de parámetros de cálculo

**Notas de Implementación**:
Definir cómo se manejan los valores predeterminados o los errores de datos para estos parámetros.

**Estado**: Backlog

---
### HU-047: Ingestión y Mapeo de Catálogo de Códigos Postales y Zonas
**Descripción**:
Como administrador de parámetros,
Quiero que el sistema ingeste o simule la consulta del `catalogo_cp_zonas` desde `Plataforma-core-ohs` y realice el mapeo de zonas,
Para que las ubicaciones de riesgo se clasifiquen correctamente según su código postal.

**Criterios de Aceptación**:
- Dado que el servicio `Plataforma-core-ohs` está disponible (o simulado), cuando se solicita la carga del catálogo CP-Zonas, entonces el sistema obtiene los datos.
- Dado que el catálogo CP-Zonas es cargado, cuando se consulta un código postal, entonces el sistema devuelve la zona CAT y el nivel técnico asociado.
- Dado que un código postal no se encuentra en el catálogo, cuando se consulta, entonces el sistema devuelve un valor por defecto o un error indicando la falta de mapeo.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- `Plataforma-core-ohs` (simulación o real)

**Componentes Técnicos**:
- Adaptador de integración `Plataforma-core-ohs`
- Repositorio de catálogos
- Mapeador de datos de CP a zonas

**Notas de Implementación**:
La estructura del catálogo debe ser eficiente para consultas frecuentes. Considerar indexación o estructuras de datos optimizadas.

**Estado**: Backlog

---
### HU-048: Disponibilidad de Parámetros para Motores
**Descripción**:
Como desarrollador del motor de cálculo,
Quiero que todos los parámetros, tarifas y catálogos ingestado sean accesibles y estén actualizados,
Para que el motor de validación y cálculo opere con información consistente y correcta.

**Criterios de Aceptación**:
- Dado que los parámetros y tarifas han sido cargados (HU-044, HU-045, HU-046, HU-047), cuando el Motor de Validación o el Motor Central de Cálculo los solicitan, entonces los reciben sin latencia significativa.
- Dado que un parámetro ha sido actualizado en la fuente, cuando el sistema lo refresca, entonces los motores subsiguientes usan la nueva versión.
- Dado que los parámetros no están disponibles, cuando los motores intentan acceder a ellos, entonces se maneja la excepción adecuadamente y se evita el cálculo.
**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- HU-044, HU-045, HU-046, HU-047

**Componentes Técnicos**:
- Fachada de acceso a parámetros
- Servicios de consulta de catálogos

**Notas de Implementación**:
Se debe implementar una capa de servicio que actúe como un punto centralizado para que los motores accedan a los parámetros, ocultando la complejidad de la ingestión.

**Estado**: Backlog

---
