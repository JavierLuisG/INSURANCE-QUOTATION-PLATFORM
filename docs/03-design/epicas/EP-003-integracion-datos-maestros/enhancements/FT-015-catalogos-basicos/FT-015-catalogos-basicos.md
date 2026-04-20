## FT-015: Conectividad y Consumo de Catálogos Básicos (Suscriptores, Agentes, Giros)

### HU-179: Conectar a Servicio de Catálogos Básicos
**Descripción**:
Como sistema,
Quiero poder conectarme al servicio `Plataforma-core-ohs` (o su mock) para obtener los catálogos de suscriptores, agentes y giros,
Para acceder a la información de referencia necesaria.

**Criterios de Aceptación**:
- Dado que el sistema requiere un catálogo, cuando intenta conectarse, entonces establece una conexión exitosa con el endpoint correspondiente de `Plataforma-core-ohs`.
- Dado que la conexión falla, cuando se intenta, entonces el sistema registra el error de conectividad y lo maneja.
- Dado que la conexión es exitosa, cuando se realiza, entonces el sistema está listo para consumir los datos.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Cliente API REST).

**Notas de Implementación**: Utilizar un cliente HTTP robusto con manejo de reintentos.

**Estado**: Backlog

---
### HU-180: Mapear y Transformar Datos de Catálogos Básicos
**Descripción**:
Como sistema,
Quiero que los datos de los catálogos de suscriptores, agentes y giros se recuperen, mapeen y transformen correctamente al modelo de datos interno del cotizador,
Para que puedan ser utilizados por la lógica de negocio y la interfaz de usuario.

**Criterios de Aceptación**:
- Dado que se reciben datos de un catálogo (ej. suscriptores) del servicio externo, cuando se procesan, entonces se mapean a la estructura de datos interna correspondiente.
- Dado que los datos externos requieren transformación (ej. formato de fecha, tipos de datos), cuando se procesan, entonces se aplican las transformaciones necesarias.
- Dado que el mapeo es exitoso, cuando se completa, entonces los datos están disponibles en el formato esperado por el cotizador.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-179 (Conectar a Servicio de Catálogos Básicos)

**Componentes Técnicos**: Backend (Capa de Mapeo de Datos, Repositorio de Catálogos).

**Notas de Implementación**: Definir los DTOs de entrada y las entidades de dominio para cada catálogo.

**Estado**: Backlog

---
### HU-181: Manejar Errores y Reintentos en Consumo de Catálogos Básicos
**Descripción**:
Como sistema,
Quiero implementar un mecanismo robusto para el manejo de errores y reintentos ante fallos de conectividad o datos inconsistentes del servicio externo de catálogos básicos,
Para asegurar la disponibilidad y resiliencia del sistema.

**Criterios de Aceptación**:
- Dado que el servicio de catálogos externos falla temporalmente, cuando el sistema intenta consultarlo, entonces aplica una estrategia de reintentos configurable.
- Dado que los datos recibidos del servicio externo son inconsistentes o inválidos, cuando se procesan, entonces el sistema los detecta y registra la inconsistencia.
- Dado que los reintentos fallan persistentemente, cuando se agotan, entonces el sistema registra un error crítico y notifica para intervención.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-179 (Conectar a Servicio de Catálogos Básicos), HU-180 (Mapear y Transformar Datos de Catálogos Básicos)

**Componentes Técnicos**: Backend (Módulo de Manejo de Errores, Circuit Breakers, Reintentos).

**Notas de Implementación**: Configurar políticas de reintento con backoff exponencial.

**Estado**: Backlog

---
