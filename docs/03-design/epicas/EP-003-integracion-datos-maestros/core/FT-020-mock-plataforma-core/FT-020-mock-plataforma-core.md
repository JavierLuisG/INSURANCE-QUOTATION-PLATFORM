## FT-020: Simulación de Servicio `Plataforma-core-ohs` (Mock Server)

### HU-092: Configurar Mock Server Base

**Descripción**:
Como desarrollador,
Quiero un mock server operativo y accesible para el cotizador,
Para simular las respuestas del servicio `Plataforma-core-ohs` y desarrollar de forma independiente.

**Criterios de Aceptación**:
- Dado que inicio el mock server, cuando accedo a su URL base, entonces recibo una respuesta indicando que está activo.
- Dado que el mock server está configurado, cuando el cotizador intenta conectarse, entonces la conexión se establece correctamente.
- Dado que la configuración del mock server es flexible, cuando necesito cambiar un puerto o una URL, entonces puedo hacerlo fácilmente.

**Prioridad**: Crítica

**Estimación**: 3 puntos de historia

**Dependencias**:
- Ninguna

**Componentes Técnicos**:
- Framework de mock server (ej. WireMock, Mountebank)
- Contenedor (ej. Docker)

**Notas de Implementación**:
Debe ser fácil de desplegar y configurar en entornos de desarrollo local y CI.

**Estado**: Backlog

---
### HU-093: Simular Endpoints de Catálogos Básicos

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para catálogos de suscriptores, agentes y giros,
Para que el cotizador pueda consumirlos y probar la funcionalidad de selección de catálogos.
**Criterios de Aceptación**:
- Dado que el cotizador solicita el catálogo de suscriptores, cuando el mock server lo recibe, entonces devuelve una lista de suscriptores predefinida.
- Dado que el cotizador solicita el catálogo de agentes, cuando el mock server lo recibe, entonces devuelve una lista de agentes predefinida.
- Dado que el cotizador solicita el catálogo de giros, cuando el mock server lo recibe, entonces devuelve una lista de giros predefinida.
- Dado que el mock server simula un error en la obtención de un catálogo, cuando el cotizador lo consulta, entonces recibe el código de error esperado.

**Prioridad**: Crítica

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para catálogos

**Notas de Implementación**:Los datos de prueba deben ser representativos y variados.

**Estado**: Backlog

---
### HU-094: Simular Endpoints de Códigos Postales y Zonas

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para códigos postales y sus zonas (CAT, nivel técnico),
Para probar la lógica de validación y aplicación de tarifas por ubicación en el cotizador.

**Criterios de Aceptación**:
- Dado que el cotizador consulta un código postal válido, cuando el mock server lo recibe, entonces devuelve la información de zona CAT y nivel técnico asociada.
- Dado que el cotizador consulta un código postal no existente, cuando el mock server lo recibe, entonces devuelve una respuesta indicando que no se encontró el CP.
- Dado que se simula una falla de servicio, cuando el cotizador consulta un CP, entonces recibe un error HTTP (ej. 500).
**Prioridad**: Crítica

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para CP y zonas

**Notas de Implementación**:
Incluir casos de éxito, no encontrado y errores de servicio.

**Estado**: Backlog

---
### HU-095: Simular Endpoints de Clasificación de Riesgo y Garantías

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para catálogos de clasificación de riesgo y garantías,
Para probar la configuración de coberturas y la evaluación del riesgo asociado.

**Criterios de Aceptación**:
- Dado que el cotizador solicita el catálogo de clasificación de riesgo, cuando el mock server lo recibe, entonces devuelve una lista predefinida de clasificaciones.
- Dado que el cotizador solicita el catálogo de garantías, cuando el mock server lo recibe, entonces devuelve una lista predefinida de garantías.
- Dado que se simula un catálogo vacío, cuando el cotizador lo consulta, entonces recibe una respuesta con una lista vacía.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para riesgo y garantías

**Notas de Implementación**:
Asegurar que los IDs y nombres sean consistentes con los esperados por el cotizador.

**Estado**: Backlog

---
### HU-096: Simular Endpoints de Tarifas y Factores Técnicos

**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints para tarifas (incendio, CAT, FHM) y factores técnicos,Para probar la lógica de cálculo de primas en el cotizador con datos consistentes.

**Criterios de Aceptación**:
- Dado que el cotizador solicita tarifas de incendio, cuando el mock server lo recibe, entonces devuelve las tasas y factores esperados.
- Dado que el cotizador solicita tarifas CAT para una zona específica, cuando el mock server lo recibe, entonces devuelve el factor CAT correspondiente.
- Dado que el cotizador solicita factores técnicos de equipo electrónico, cuando el mock server lo recibe, entonces devuelve los factores por clase y nivel de zona.
- Dado que se simula una tarifa no encontrada, cuando el cotizador la consulta, entonces recibe una respuesta que indica su ausencia.

**Prioridad**: Crítica

**Estimación**: 5 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server
- JSON de datos de prueba para tarifas y factores
**Notas de Implementación**:
Los datos de tarifas pueden ser complejos; el mock debe poder manejar diferentes escenarios de consulta.

**Estado**: Backlog

---
### HU-097: Poblar Base de Datos del Mock con Migraciones

**Descripción**:
Como desarrollador,
Quiero que la base de datos (MongoDB) del mock server se pueble y actualice mediante migraciones (Flyway),
Para mantener datos de prueba consistentes y versionados a lo largo del evolución del proyecto.

**Criterios de Aceptación**:
- Dado que inicio el mock server con una base de datos vacía, cuando se ejecuta, entonces las migraciones (Flyway) se aplican y la DB se puebla con datos iniciales.
- Dado que se añade una nueva migración con datos actualizados, cuando el mock server se reinicia, entonces la DB se actualiza a la nueva versión de datos.
- Dado que los datos de prueba son versionados, cuando el mock server se usa en diferentes ramas de desarrollo, entonces cada rama puede tener su conjunto de datos consistente.

**Prioridad**: Crítica

**Estimación**: 5 puntos de historia
**Dependencias**:
- HU-092

**Componentes Técnicos**:
- MongoDB
- Flyway (o similar para NoSQL)
- Scripts de datos de prueba

**Notas de Implementación**:
Definir un esquema de versionado claro para las migraciones de datos.

**Estado**: Backlog

---
### HU-098: Configurar Respuestas Dinámicas y Errores en el Mock

**Descripción**:
Como desarrollador,
Quiero poder configurar respuestas dinámicas y escenarios de error controlados en el mock server,
Para facilitar pruebas de resiliencia y diferentes comportamientos del servicio externo.

**Criterios de Aceptación**:
- Dado que el mock server está configurado, cuando el cotizador consulta un catálogo, entonces puedo configurar que devuelva una respuesta con un retraso específico.
- Dado que quiero probar un escenario de fallo, cuando configuro el mock server, entonces puedo hacer que un endpoint devuelva un error HTTP 500.
- Dado que necesito probar la validación de datos, cuando configuro el mock server, entonces puedo hacer que devuelva datos malformados para un catálogo específico.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- HU-092

**Componentes Técnicos**:
- Framework de mock server (capacidades de escenarios y retrasos)

**Notas de Implementación**:
La interfaz del mock server debe permitir una fácil configuración de estos escenarios.

**Estado**: Backlog

---
### HU-099: Validar Estabilidad del Mock Server

**Descripción**:
Como desarrollador,
Quiero realizar pruebas de carga simuladas en el mock server,
Para validar su estabilidad y disponibilidad bajo diferentes niveles de concurrencia y asegurar que es un reemplazo fiable.

**Criterios de Aceptación**:
- Dado que el mock server está operativo, cuando se ejecutan pruebas de carga con N solicitudes por segundo, entonces el mock server responde consistentemente sin caídas.
- Dado que el mock server está bajo carga, cuando el cotizador lo consulta, entonces los tiempos de respuesta del mock se mantienen dentro de los límites aceptables.
- Dado que se detectan problemas de rendimiento o estabilidad durante las pruebas de carga, cuando el mock server se analiza, entonces se identifican y corrigen los cuellos de botella.

**Prioridad**: Media

**Estimación**: 5 puntos de historia

**Dependencias**:
- HU-092, HU-093, HU-094, HU-095, HU-096

**Componentes Técnicos**:
- Herramienta de pruebas de carga (ej. JMeter, Gatling)
- Entorno de ejecución del mock server

**Notas de Implementación**:
Las pruebas de carga deben reflejar el uso esperado del mock por el cotizador.

**Estado**: Backlog

---
