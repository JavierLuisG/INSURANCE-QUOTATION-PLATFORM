## FT-019: Generación y Gestión de Folios Alfanuméricos

### 1. Descripción

Esta feature permite la generación, gestión y control de folios alfanuméricos únicos para cada cotización, asegurando su unicidad, persistencia y consistencia incluso en escenarios de alta concurrencia.

---

### 2. Objetivo de Negocio

Garantizar la identificación única, trazable y robusta de cada cotización mediante un folio estructurado, evitando duplicidades y asegurando continuidad operativa.

---

### 3. Alcance Funcional

Incluye:

* Generación de folios con patrón configurable
* Persistencia de secuencia numérica
* Manejo de concurrencia en generación
* Implementación de idempotencia
* Estrategia de reintentos ante fallos
* Notificación de errores persistentes

No incluye:

* Gestión de cotización (FT-001)
* Persistencia de resultados de cálculo (FT-013)

---

### 4. Historias de Usuario

| HU     | Nombre                 | Descripción corta                          |
| ------ | ---------------------- | ------------------------------------------ |
| HU-086 | Generar folio          | Crea folio único con patrón                |
| HU-087 | Persistir secuencia    | Guarda secuencia numérica                  |
| HU-088 | Reintentos             | Maneja fallos en generación                |
| HU-089 | Notificación de fallos | Informa errores persistentes               |
| HU-090 | Idempotencia           | Evita duplicados por solicitudes repetidas |
| HU-091 | Concurrencia           | Maneja generación simultánea               |

---

### 5. Flujo Funcional

1. Sistema solicita generación de folio (HU-086)
2. Se consulta y actualiza secuencia persistida (HU-087)
3. Se construye folio con patrón definido
4. Se valida idempotencia de la solicitud (HU-090)
5. En caso de conflicto o error, se ejecutan reintentos (HU-088)
6. Si persiste el fallo, se notifica (HU-089)
7. Se garantiza unicidad bajo concurrencia (HU-091)
8. Folio se asocia a la cotización

---

### 6. Dependencias Técnicas

* Servicio de generación de folios
* Módulo de persistencia de secuencia (DB)
* Estrategia de concurrencia (bloqueos / atomicidad)
* Mecanismo de idempotencia (request-id / transaction-id)
* Sistema de logging y notificación

---

### 7. Consideraciones Técnicas

* Uso de operaciones atómicas para la secuencia (ej. `findAndModify` en MongoDB o secuencias en DB relacional)
* Estrategias de concurrencia: optimistic locking o locks distribuidos
* Implementación de idempotencia basada en identificadores únicos de solicitud
* Retry con backoff exponencial para fallos transitorios
* Configuración flexible del patrón de folio (prefijo, año, padding)
* Evitar cuellos de botella: considerar generadores distribuidos si escala el sistema
* Logging estructurado para auditoría de generación de folios
