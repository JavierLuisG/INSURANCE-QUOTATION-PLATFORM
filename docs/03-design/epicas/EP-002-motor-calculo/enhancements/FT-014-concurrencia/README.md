## FT-014: Gestión de Concurrencia y Versionado Optimista

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: enhancements

---

### 1. Descripción

Esta feature implementa un mecanismo de control de concurrencia basado en versionado optimista para la gestión de cotizaciones, asegurando consistencia de datos en escenarios de edición simultánea y evitando sobrescrituras accidentales.

---

### 2. Objetivo de Negocio

Garantizar la integridad de la información de cotizaciones en entornos multiusuario, evitando pérdidas de datos por modificaciones concurrentes y habilitando mecanismos claros de detección, notificación y resolución de conflictos.

---

### 3. Alcance Funcional

Incluye:

* Implementación de campo de versión incremental en cotizaciones
* Inicialización automática de versión en creación (valor inicial 1)
* Incremento de versión en cada actualización persistida
* Comparación de versiones entre cliente y base de datos
* Detección de conflictos de concurrencia
* Notificación de conflictos al usuario final
* Recarga de cotización con última versión disponible
* Soporte para reintento de operación tras resolución de conflicto

No incluye:

* Motor de cálculo de primas (FT-012)
* Validaciones de negocio (FT-011)
* Persistencia de resultados financieros (FT-013)
* Integración con servicios externos (FT-007 / FT-010)
* Reglas de negocio del dominio
* UI completa de edición de cotización

---

### 4. Historias de Usuario

| HU     | Nombre                       | Descripción corta                               |
| ------ | ---------------------------- | ----------------------------------------------- |
| HU-180 | Campo de versión incremental | Implementar versionado numérico en cotizaciones |
| HU-181 | Comparación de versiones     | Validar versión antes de guardar                |
| HU-182 | Detección de conflicto       | Identificar concurrencia en persistencia        |
| HU-183 | Notificación de conflicto    | Informar al usuario sobre versión más reciente  |
| HU-184 | Recarga de cotización        | Permitir actualizar con última versión          |

---

### 5. Flujo Funcional

1. Creación de cotización:

   * Se inicializa `version = 1`
2. Edición de cotización:

   * El cliente envía versión actual en memoria
3. Inicio de guardado:

   * El sistema consulta versión en base de datos
4. Comparación:

   * Si versiones coinciden → continuar guardado
   * Si difieren → detectar conflicto
5. En caso de conflicto:

   * Se genera error de concurrencia
   * Se notifica al usuario
6. Resolución:

   * Usuario puede recargar la cotización
   * Se obtiene la última versión desde base de datos
7. Reintento:

   * Usuario puede volver a editar y guardar con versión actualizada
8. Persistencia exitosa:

   * Se incrementa `version`
   * Se guarda cotización actualizada

---

### 6. Dependencias Técnicas

* Modelo de cotización con campo `version`
* Capa de persistencia (MongoDB o equivalente)
* Lógica de comparación de versiones en backend
* Servicio de gestión de concurrencia
* Sistema de errores estructurados (conflict handling)
* API de cotización (GET/PUT/PATCH)
* Frontend de edición de cotización
* Mecanismo de recarga de datos desde backend
* Integración con flujo de guardado transaccional (FT-008 / FT-013 indirectamente)

---

### 7. Consideraciones Técnicas

* El versionado debe ser **fuente única de control de concurrencia**
* Diseño basado en **optimistic locking**
* La comparación de versiones debe ocurrir:

  * Antes de persistencia
  * Dentro del flujo transaccional lógico
* El incremento de versión debe ser:

  * Automático en backend
  * No manipulable por cliente
* Detección de conflicto debe ser:

  * Explícita (error tipado o código semántico)
* Notificación al usuario:

  * Debe ser accionable (no solo informativa)
* Recarga de cotización:

  * Debe invalidar estado local completamente
* Evitar estados inconsistentes en frontend:

  * Siempre sincronizar desde backend tras conflicto
* Reintentos deben operar sobre versión actualizada
* El diseño debe evitar race conditions en:

  * Guardado concurrente
  * Lectura-escritura simultánea
* Mantener separación clara entre:

  * Control de concurrencia
  * Lógica de negocio
  * Persistencia de datos

---
