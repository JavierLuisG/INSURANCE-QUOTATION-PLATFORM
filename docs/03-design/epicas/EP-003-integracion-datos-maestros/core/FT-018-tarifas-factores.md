## FT-018: Conectividad y Consumo de Tarifas y Factores Técnicos

### HU-080: Consultar Tarifas de Incendio

**Descripción**:
Como sistema,Quiero consultar las tarifas de incendio desde el servicio `Plataforma-core-ohs` (o su mock),
Para realizar el cálculo preciso de las primas de incendio.

**Criterios de Aceptación**:
- Dado que se requiere el cálculo de prima de incendio, cuando el sistema consulta las tarifas, entonces recibe la información de tasas y factores relevantes.
- Dado que la consulta de tarifas de incendio es exitosa, cuando los datos se reciben, entonces están disponibles para el módulo de cálculo.
- Dado que el servicio de tarifas de incendio no responde, cuando el sistema lo consulta, entonces se aplica la estrategia de reintento y se registra el fallo si persiste.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068 (Conectividad básica)
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de tarifas
- Repositorio de tarifas

**Notas de Implementación**:
La estructura de la respuesta de tarifas debe estar bien definida y documentada.

**Estado**: Backlog

---
### HU-081: Consultar Tarifas CAT

**Descripción**:
Como sistema,
Quiero consultar las tarifas CAT (Catástrofes) desde el servicio `Plataforma-core-ohs` (o su mock),
Para realizar el cálculo preciso de las primas por ubicación según la zona CAT.

**Criterios de Aceptación**:
- Dado que se requiere el cálculo de prima CAT, cuando el sistema consulta las tarifas con la zona CAT de la ubicación, entonces recibe el factor CAT correspondiente.
- Dado que la zona CAT de una ubicación es "Zona A", cuando el sistema consulta, entonces obtiene el factor CAT específico para "Zona A".
- Dado que el servicio devuelve un factor CAT nulo o inválido, cuando el sistema lo procesa, entonces se utiliza un valor por defecto o se registra una alerta.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068, HU-076

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de tarifas
- Repositorio de tarifas

**Notas de Implementación**:
La dependencia de la zona CAT de la ubicación es crucial para esta consulta.

**Estado**: Backlog

---
### HU-082: Consultar Tarifas FHM

**Descripción**:
Como sistema,
Quiero consultar las tarifas FHM (Fenómenos Hidrometeorológicos) desde el servicio `Plataforma-core-ohs` (o su mock),
Para realizar el cálculo preciso de las primas por ubicación.

**Criterios de Aceptación**:
- Dado que se requiere el cálculo de prima FHM, cuando el sistema consulta las tarifas, entonces recibe las cuotas FHM por grupo, zona y condición.
- Dado que el servicio de tarifas FHM está disponible, cuando el sistema lo consulta, entonces la respuesta se recibe en un tiempo aceptable.
- Dado que se produce un error en la consulta de tarifas FHM, cuando el sistema lo detecta, entonces notifica al módulo de cálculo y registra el evento.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de tarifas
- Repositorio de tarifas

**Notas de Implementación**:
La complejidad de las tarifas FHM puede requerir un mapeo detallado.

**Estado**: Backlog

---
### HU-083: Consultar Factores Técnicos de Equipo Electrónico

**Descripción**:
Como sistema,
Quiero consultar los factores técnicos para equipo electrónico desde el servicio `Plataforma-core-ohs` (o su mock),
Para aplicar los cálculos específicos para este tipo de cobertura.

**Criterios de Aceptación**:
- Dado que se está cotizando equipo electrónico, cuando el sistema necesita los factores técnicos, entonces consulta el servicio externo.
- Dado que la consulta de factores técnicos es exitosa, cuando los datos se reciben, entonces el módulo de cálculo puede aplicarlos.
- Dado que el servicio no devuelve factores para una clase específica de equipo electrónico, cuando el sistema lo procesa, entonces se utiliza un factor por defecto o se marca una alerta.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068

**Componentes Técnicos**:
- Cliente API REST
- Módulo de consulta de factores
- Repositorio de factores

**Notas de Implementación**:
Los factores pueden variar según la clase y el nivel de zona del equipo.

**Estado**: Backlog

---
### HU-084: Mapear Tarifas y Factores Técnicos

**Descripción**:
Como sistema,
Quiero mapear las tarifas (incendio, CAT, FHM) y factores técnicos a mi modelo interno,
Para su correcta aplicación en la lógica de cálculo de primas.

**Criterios de Aceptación**:
- Dado que se reciben datos de tarifas, cuando el sistema los procesa, entonces se transforman al formato esperado por el módulo de cálculo.
- Dado que se reciben datos de factores técnicos, cuando el sistema los procesa, entonces se transforman al formato esperado por el módulo de cálculo.
- Dado que el mapeo es exitoso, cuando el módulo de cálculo solicita una tarifa, entonces recibe un objeto con los atributos correctos.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-080, HU-081, HU-082, HU-083

**Componentes Técnicos**:
- Capa de mapeo de datos
- Modelos de datos de tarifas/factores

**Notas de Implementación**:
La complejidad de las tarifas puede requerir un modelo de datos interno flexible.

**Estado**: Backlog

---
### HU-085: Manejar Errores en Consulta de Tarifas y Factores Técnicos

**Descripción**:
Como sistema,
Quiero manejar errores de conexión o la ausencia/inconsistencia de datos al consultar tarifas y factores técnicos,
Para notificar al sistema o usuario y asegurar la integridad del cálculo.

**Criterios de Aceptación**:
- Dado que el servicio de tarifas devuelve un error de validación, cuando el sistema lo recibe, entonces registra el error y notifica al módulo de cálculo que no se pudieron obtener las tarifas.
- Dado que el servicio de tarifas está inaccesible, cuando el sistema lo consulta, entonces se aplica el mecanismo de reintento y, si falla, se informa al usuario.
- Dado que se detecta una inconsistencia crítica en los datos de tarifas recibidos, cuando el sistema los procesa, entonces se impide el cálculo y se registra la inconsistencia.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia
**Dependencias**:
- HU-080, HU-081, HU-082, HU-083

**Componentes Técnicos**:
- Módulo de manejo de excepciones
- Estrategia de reintentos
- Servicio de logging/notificación

**Notas de Implementación**:
Es crucial definir qué errores son críticos y detienen el cálculo, y cuáles pueden tener un fallback.

**Estado**: Backlog---
