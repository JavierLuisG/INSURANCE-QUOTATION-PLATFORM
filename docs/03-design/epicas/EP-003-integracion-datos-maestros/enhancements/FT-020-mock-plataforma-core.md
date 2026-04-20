## FT-020: Simulación de Servicio `Plataforma-core-ohs` (Mock Server)

### HU-195: Mock Server Operativo y Accesible
**Descripción**:
Como desarrollador,
Quiero que el mock server de `Plataforma-core-ohs` esté operativo y accesible para el cotizador,
Para poder desarrollar y probar el sistema sin depender del servicio real.

**Criterios de Aceptación**:
- Dado que el mock server está desplegado, cuando el cotizador intenta conectarse, entonces establece una conexión exitosa.
- Dado que el mock server está operativo, cuando se realizan peticiones a sus endpoints, entonces responde de manera esperada.
- Dado que el mock server es accesible, cuando se realizan pruebas de integración, entonces no hay errores de conectividad.

**Prioridad**: Crítica

**Estimación**: 3 puntos de historia

**Dependencias**: Ninguna (es una dependencia crítica para el desarrollo)

**Componentes Técnicos**: Mock Server (Framework de Mock Server).

**Notas de Implementación**: El mock server debe ser fácil de iniciar y detener.

**Estado**: Backlog

---
### HU-196: Simular Endpoints de Catálogos y Tarifas
**Descripción**:
Como desarrollador,
Quiero que el mock server simule fielmente los endpoints de los catálogos (suscriptores, agentes, giros, CP, riesgo, garantías) y tarifas de `Plataforma-core-ohs`,
Para replicar el comportamiento del servicio real durante el desarrollo y las pruebas.

**Criterios de Aceptación**:
- Dado que el cotizador realiza una petición a un endpoint de catálogo (ej. suscriptores), cuando el mock server la recibe, entonces devuelve una respuesta con la estructura de datos esperada.
- Dado que el cotizador realiza una petición a un endpoint de tarifas, cuando el mock server la recibe, entonces devuelve una respuesta con los valores de tarifas esperados.
- Dado que los contratos de la API real se definen, cuando el mock server los simula, entonces cumple con esos contratos.

**Prioridad**: Crítica

**Estimación**: 4 puntos de historia

**Dependencias**: HU-195 (Mock Server Operativo y Accesible)

**Componentes Técnicos**: Mock Server (Configuración de Endpoints y Respuestas).

**Notas de Implementación**: Utilizar contratos de ejemplo y refinarlos de forma iterativa.

**Estado**: Backlog

---
### HU-197: Configurar Respuestas Dinámicas y Escenarios de Error
**Descripción**:
Como desarrollador,
Quiero que el mock server permita configurar respuestas dinámicas y escenarios de error controlados para pruebas,
Para simular diversos comportamientos del servicio externo y probar la resiliencia del cotizador.

**Criterios de Aceptación**:
- Dado que se configura una respuesta dinámica (ej. devolver un catálogo filtrado), cuando el cotizador lo solicita, entonces el mock server devuelve la respuesta configurada.
- Dado que se configura un escenario de error (ej. HTTP 500, timeout), cuando el cotizador lo solicita, entonces el mock server simula el error.
- Dado que se simula un error, cuando el cotizador lo maneja, entonces se puede verificar su comportamiento ante fallos.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-196 (Simular Endpoints de Catálogos y Tarifas)

**Componentes Técnicos**: Mock Server (Funcionalidades de Respuestas Dinámicas y Manejo de Errores).

**Notas de Implementación**: El framework de mock server debe soportar estas funcionalidades.

**Estado**: Backlog

---
### HU-198: Poblar Base de Datos del Mock con Migraciones
**Descripción**:
Como desarrollador,
Quiero que la base de datos (MongoDB) del mock server se pueble y actualice mediante migraciones (ej. Flyway),
Para mantener datos de prueba consistentes y versionados.

**Criterios de Aceptación**:
- Dado que se inicia el mock server, cuando se ejecuta, entonces las migraciones de Flyway se aplican a la base de datos de MongoDB.
- Dado que las migraciones se aplican, cuando se completan, entonces la base de datos del mock contiene los datos de prueba iniciales (catálogos, tarifas).
- Dado que se requiere actualizar los datos de prueba, cuando se crea una nueva migración, entonces se aplica correctamente al iniciar el mock server.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-195 (Mock Server Operativo y Accesible)

**Componentes Técnicos**: Mock Server (MongoDB, Flyway, Scripts de Datos de Prueba).

**Notas de Implementación**: Definir una estrategia de versionado para los datos de prueba.

**Estado**: Backlog

---
### HU-199: Validar Estabilidad del Servicio Simulado con Pruebas de Carga
**Descripción**:
Como desarrollador,
Quiero que la disponibilidad del servicio simulado se garantice mediante pruebas de carga simuladas,
Para validar su estabilidad y rendimiento bajo demanda.

**Criterios de Aceptación**:
- Dado que se ejecutan pruebas de carga sobre el mock server, cuando se completan, entonces el mock server mantiene su disponibilidad y responde dentro de los tiempos esperados.
- Dado que el mock server está bajo carga, cuando se monitorea, entonces su consumo de recursos (CPU, memoria) es razonable.
- Dado que las pruebas de carga son exitosas, cuando se completan, entonces confirman que el mock server puede soportar las demandas del cotizador.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**: HU-195 (Mock Server Operativo y Accesible)

**Componentes Técnicos**: Herramientas de Pruebas de Carga (ej. JMeter, Gatling).

**Notas de Implementación**: Las pruebas de carga pueden ser básicas para validar la estabilidad inicial.

**Estado**: Backlog

---
