## FT-022: Gestión de Caché y Estrategia de Actualización de Datos Maestros

### HU-204: Almacenar Datos Maestros Clave en Caché
**Descripción**:
Como sistema,
Quiero que los datos maestros clave (catálogos, tarifas) se almacenen en caché de forma eficiente,
Para optimizar el acceso y reducir la latencia de consulta.

**Criterios de Aceptación**:
- Dado que se consulta un dato maestro por primera vez, cuando se obtiene del servicio externo, entonces se almacena en el caché.
- Dado que se consulta un dato maestro que ya está en caché, cuando se realiza, entonces se recupera directamente del caché.
- Dado que el caché está operativo, cuando se utiliza, entonces el acceso a los datos es rápido.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: FT-015, FT-016, FT-017, FT-018 (Todas las integraciones de datos maestros)

**Componentes Técnicos**: Backend (Framework de Caché, Repositorios de Datos Maestros).

**Notas de Implementación**: Elegir una solución de caché adecuada (ej. Caffeine para caché local).

**Estado**: Backlog

---
### HU-205: Asegurar Acceso Más Rápido a Datos en Caché
**Descripción**:
Como sistema,
Quiero que el acceso a los datos en caché sea más rápido que la consulta directa al servicio externo, cumpliendo los SLAs de tiempo de respuesta,
Para mejorar el rendimiento general del cotizador.

**Criterios de Aceptación**:
- Dado que se consulta un dato maestro desde el caché, cuando se mide el tiempo de respuesta, entonces es significativamente menor que el tiempo de consulta al servicio externo.
- Dado que el sistema está bajo carga, cuando se accede a datos maestros en caché, entonces el rendimiento se mantiene dentro de los SLAs definidos.
- Dado que el caché está optimizado, cuando se utiliza, entonces la latencia de las operaciones de consulta de datos maestros se reduce.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-204 (Almacenar Datos Maestros Clave en Caché)

**Componentes Técnicos**: Backend (Framework de Caché, Métricas de Rendimiento).

**Notas de Implementación**: Realizar pruebas de rendimiento para validar la mejora.

**Estado**: Backlog

---
### HU-206: Implementar Mecanismo de Invalidación/Actualización de Caché (TTL)
**Descripción**:
Como sistema,
Quiero que exista un mecanismo configurable para invalidar o actualizar los datos en caché (ej. TTL basado en tiempo),
Para asegurar que la información en el cotizador esté siempre fresca y consistente.

**Criterios de Aceptación**:
- Dado que un dato maestro se almacena en caché con un TTL (Time To Live), cuando expira el TTL, entonces el dato se invalida y se vuelve a cargar del servicio externo en la siguiente consulta.
- Dado que el TTL es configurable, cuando se ajusta, entonces la política de frescura de datos se adapta.
- Dado que se requiere una invalidación manual (ej. por un evento), cuando se activa, entonces el dato en caché se invalida inmediatamente.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-204 (Almacenar Datos Maestros Clave en Caché)

**Componentes Técnicos**: Backend (Framework de Caché, Módulo de Sincronización de Datos).

**Notas de Implementación**: La estrategia de caché debe ser basada en TTL configurable (Catálogos estáticos: 12–24 horas; Tarifas/factores: 1–6 horas), con estrategia de desalojo LRU y tamaño limitado por número de entradas.

**Estado**: Backlog

---
### HU-207: Mantener Consistencia de Datos en Caché
**Descripción**:
Como sistema,
Quiero que la consistencia de los datos en caché con la fuente original se mantenga según la política definida,
Para evitar que el cotizador opere con información desactualizada o incorrecta.

**Criterios de Aceptación**:
- Dado que la política de caché establece una frecuencia de actualización, cuando se cumple, entonces los datos en caché se refrescan del servicio externo.
- Dado que el servicio externo actualiza un dato maestro, cuando se aplica la política de caché, entonces el dato en caché se actualiza para reflejar el cambio dentro del tiempo definido.
- Dado que la consistencia se mantiene, cuando se consulta el cotizador, entonces los datos maestros reflejan la información actual de la fuente.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-206 (Implementar Mecanismo de Invalidación/Actualización de Caché (TTL))

**Componentes Técnicos**: Backend (Módulo de Sincronización de Datos).

**Notas de Implementación**: No se implementará invalidación por eventos en la primera versión, solo TTL.

**Estado**: Backlog