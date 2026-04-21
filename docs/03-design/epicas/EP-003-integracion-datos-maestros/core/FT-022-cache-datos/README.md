## FT-022: Gestión de Caché y Estrategia de Actualización de Datos Maestros

### 1. Descripción

Esta feature implementa una capa de caché para datos maestros (catálogos, tarifas y factores técnicos), junto con estrategias de invalidación y actualización, con el objetivo de optimizar el rendimiento, reducir la latencia y minimizar la dependencia directa de servicios externos.

---

### 2. Objetivo de Negocio

Mejorar los tiempos de respuesta del sistema y garantizar la disponibilidad de datos maestros, manteniendo un equilibrio entre rendimiento y consistencia de la información utilizada en el cotizador.

---

### 3. Alcance Funcional

Incluye:

* Almacenamiento de datos maestros en caché
* Recuperación de datos desde caché
* Invalidación basada en TTL (Time To Live)
* Actualización programada de caché
* Invalidación bajo demanda (eventos o manual)
* Monitoreo de rendimiento y consistencia del caché

No incluye:

* Consumo directo de servicios externos (FT-015 a FT-018)
* Validación de datos maestros (FT-021)

---

### 4. Historias de Usuario

| HU     | Nombre                    | Descripción corta               |
| ------ | ------------------------- | ------------------------------- |
| HU-105 | Almacenar en caché        | Guarda datos maestros           |
| HU-106 | TTL de caché              | Define expiración por tiempo    |
| HU-107 | Actualización programada  | Refresca caché periódicamente   |
| HU-108 | Invalidación bajo demanda | Limpia caché por evento/manual  |
| HU-109 | Monitoreo de caché        | Mide rendimiento y consistencia |

---

### 5. Flujo Funcional

1. Sistema consulta datos maestros
2. Se verifica si existen en caché (HU-105)

   * Si existen → se retornan desde caché
   * Si no existen → se consulta fuente externa y se almacenan
3. Cada entrada tiene un TTL configurable (HU-106)
4. Al expirar TTL → se invalida y se vuelve a consultar
5. Paralelamente:

   * Scheduler refresca datos periódicamente (HU-107)
   * Eventos o acciones manuales pueden invalidar caché (HU-108)
6. Sistema monitorea métricas de uso y consistencia (HU-109)

---

### 6. Dependencias Técnicas

* Framework de caché (Caffeine, Redis u otro)
* Servicios de datos maestros (FT-015 a FT-018)
* Scheduler de tareas (Spring Scheduler, Quartz)
* Sistema de monitoreo (Prometheus, Grafana u otro)
* API interna para gestión de caché

---

### 7. Consideraciones Técnicas

* Definir estrategia de caché: local (in-memory) vs distribuido según escalabilidad
* Configurar TTLs diferenciados por tipo de dato (tarifas vs catálogos)
* Implementar patrones como Cache-Aside o Read-Through
* Garantizar consistencia eventual con mecanismos de refresco
* Invalidación granular para evitar impactos globales
* Incorporar métricas clave: hit ratio, latency, eviction rate
* Diseñar fallback en caso de fallo del caché (resiliencia)
* Evitar “cache stampede” mediante locking o refresh anticipado
