## FT-007: Integración con Servicios de Referencia (Catálogos y Tarifas)

### 1. Descripción

Esta feature permite la integración con servicios externos para obtener información de referencia crítica, como catálogos, códigos postales, zonas de riesgo, tarifas y factores técnicos necesarios para la operación del cotizador.

---

### 2. Objetivo de Negocio

Garantizar que el cotizador opere con información confiable, actualizada y centralizada, asegurando consistencia en datos de entrada y precisión en los cálculos.

---

### 3. Alcance Funcional

Incluye:

* Consumo de catálogos (suscriptores, agentes, giros)
* Validación de códigos postales y zonas de riesgo
* Obtención de clasificaciones de riesgo
* Consulta de tarifas y factores técnicos
* Manejo resiliente de integraciones externas
* Simulación de servicios para desarrollo/testing

No incluye:

* Gestión interna de catálogos
* Definición de reglas de negocio propias de tarifas

---

### 4. Historias de Usuario

| HU     | Nombre                  | Descripción corta            |
| ------ | ----------------------- | ---------------------------- |
| HU-029 | Consumo de catálogos    | Suscriptores, agentes, giros |
| HU-030 | Validación de ubicación | CP y zonas de riesgo         |
| HU-031 | Clasificación de riesgo | Catálogos técnicos           |
| HU-032 | Tarifas y factores      | Datos para cálculo           |
| HU-033 | Robustez integración    | Manejo de fallos             |
| HU-034 | Simulación servicios    | Mock para desarrollo         |

---

### 5. Flujo Funcional

1. Sistema requiere datos de referencia
2. Consulta servicios externos
3. Recibe y transforma datos
4. Expone datos al frontend o módulos internos
5. Aplica manejo de errores o fallback si falla integración

---

### 6. Dependencias Técnicas

* `Plataforma-core-ohs` (servicio externo principal)
* API de cotizaciones (orquestador)
* Mecanismos de resiliencia (retry, circuit breaker)
* Mocks / stubs para ambientes locales

---

### 7. Consideraciones Técnicas

* Implementar timeouts y retries con backoff exponencial
* Circuit breaker para evitar cascada de fallos
* Cacheo estratégico para catálogos estáticos
* Contratos claros (DTOs) para desacoplar dependencias externas
* Uso de adaptadores (hexagonal architecture) para aislar integraciones externas
