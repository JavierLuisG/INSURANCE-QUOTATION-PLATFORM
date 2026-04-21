## FT-020: Simulación de Servicio `Plataforma-core-ohs` (Mock Server)

### 1. Descripción

Esta feature permite la construcción de un mock server que simula el comportamiento del servicio externo `Plataforma-core-ohs`, incluyendo catálogos, códigos postales, tarifas y factores técnicos, con el fin de desacoplar el desarrollo del cotizador y habilitar pruebas controladas.

---

### 2. Objetivo de Negocio

Permitir el desarrollo, pruebas e integración del cotizador de forma independiente a servicios externos reales, garantizando estabilidad, velocidad de desarrollo y control sobre los escenarios de prueba.

---

### 3. Alcance Funcional

Incluye:

* Configuración de mock server base
* Simulación de endpoints de catálogos básicos
* Simulación de códigos postales y zonas
* Simulación de catálogos de riesgo y garantías
* Simulación de tarifas y factores técnicos
* Poblamiento de datos mediante migraciones
* Configuración de escenarios dinámicos y errores
* Validación de estabilidad mediante pruebas de carga

No incluye:

* Integración real con `Plataforma-core-ohs`
* Lógica de negocio del cotizador

---

### 4. Historias de Usuario

| HU     | Nombre               | Descripción corta                   |
| ------ | -------------------- | ----------------------------------- |
| HU-092 | Mock server base     | Configura servidor accesible        |
| HU-093 | Catálogos básicos    | Simula suscriptores, agentes, giros |
| HU-094 | CP y zonas           | Simula códigos postales             |
| HU-095 | Riesgo y garantías   | Simula catálogos técnicos           |
| HU-096 | Tarifas y factores   | Simula tarifas técnicas             |
| HU-097 | Migraciones de datos | Versiona datos de prueba            |
| HU-098 | Respuestas dinámicas | Configura errores y delays          |
| HU-099 | Pruebas de carga     | Valida estabilidad del mock         |

---

### 5. Flujo Funcional

1. Se levanta el mock server (HU-092)
2. Se cargan datos iniciales mediante migraciones (HU-097)
3. El cotizador consume endpoints simulados:

   * Catálogos (HU-093, HU-095)
   * CP y zonas (HU-094)
   * Tarifas y factores (HU-096)
4. Se configuran escenarios dinámicos según necesidad (HU-098)
5. Se ejecutan pruebas de estabilidad y carga (HU-099)

---

### 6. Dependencias Técnicas

* Framework de mock server (WireMock, Mountebank)
* Contenedor (Docker)
* Base de datos (MongoDB)
* Herramienta de migraciones (Flyway o alternativa para NoSQL)
* Herramientas de testing de carga (JMeter, Gatling)

---

### 7. Consideraciones Técnicas

* Diseño orientado a contratos (contract-first) para replicar fielmente el servicio real
* Uso de datos de prueba representativos y versionados
* Soporte para escenarios configurables (latencia, errores, datos malformados)
* Aislamiento completo del entorno real (no dependencias externas)
* Estrategias de resiliencia probadas contra el mock
* Capacidad de ejecución en entornos locales y CI/CD
* Monitoreo básico del mock para detectar cuellos de botella en pruebas de carga
