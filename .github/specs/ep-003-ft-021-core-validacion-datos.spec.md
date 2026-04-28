---
id: SPEC-007
status: IN_PROGRESS
feature: ep-003-ft-021-core-validacion-datos
created: 2026-04-27
updated: 2026-04-27
author: spec-generator
version: "1.0"
related-specs:
  - SPEC-001
  - SPEC-003
  - SPEC-004
  - SPEC-005
  - SPEC-006
---

# Spec: FT-021 — Validación y Gestión de Inconsistencias en Datos Maestros

> **Estado:** `DRAFT` → aprobar con `status: APPROVED` antes de iniciar implementación.
> **Ciclo de vida:** DRAFT → APPROVED → IN_PROGRESS → IMPLEMENTED → DEPRECATED

---

## 1. REQUERIMIENTOS

### Descripción

Esta feature implementa una capa transversal de validación en `plataformas-danos-back` encargada de validar, detectar, registrar y gestionar inconsistencias en los datos maestros provenientes de servicios externos (`Plataforma-core-ohs` o su mock), asegurando su calidad e integridad antes de ser utilizados por el sistema para cálculos de primas y operaciones de cotización.

### Requerimiento de Negocio

Garantizar la integridad, consistencia y confiabilidad de los datos maestros (catálogos de suscriptores, agentes, giros, códigos postales, tarifas, clasificaciones de riesgo) mediante validaciones automáticas de campos obligatorios, formato y valor, detectando inconsistencias y permitiendo su gestión (rechazo, notificación o corrección), minimizando el riesgo de errores en el cálculo de primas y operaciones del cotizador.

### Historias de Usuario

#### HU-100: Validar campos obligatorios y formato en datos maestros (HU-100)

```
Como:        Sistema (plataformas-danos-back)
Quiero:      Validar que los datos maestros cumplan con reglas de negocio (nulos, formato, valor)
Para:        Asegurar que los datos recibidos de plataforma-core-ohs son consistentes y válidos

Prioridad:   Alta
Estimación:  M (5 story points)
Dependencias: SPEC-001, SPEC-003, SPEC-004, SPEC-005, SPEC-006 (datos maestros disponibles)
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-100

**Happy Path**

```gherkin
CRITERIO-100.1: Validación exitosa de catálogo de suscriptores válido
  Dado que:  se recibe un catálogo de suscriptores con id y nombre válidos (no nulos, no vacíos)
  Cuando:    el módulo de validación procesa el registro
  Entonces:  el estado del registro cambia a VALIDATED
             y no se generan errores de validación

CRITERIO-100.2: Validación exitosa de tarifa CAT con factor positivo
  Dado que:  se recibe una tarifa CAT con factores numéricos positivos (>0)
  Cuando:    el módulo de validación procesa el registro
  Entonces:  el estado del registro cambia a VALIDATED
             y se permite su uso en cálculos de primas

CRITERIO-100.3: Validación exitosa de código postal con formato correcto
  Dado que:  se recibe un código postal con 5 dígitos numéricos (patrón /^[0-9]{5}$/)
  Cuando:    el módulo de validación procesa el registro
  Entonces:  el estado del registro cambia a VALIDATED
             y el código postal es aceptado
```

**Error Path**

```gherkin
CRITERIO-100.4: Detección de campo obligatorio nulo en suscriptor
  Dado que:  se recibe un catálogo de suscriptores con el campo 'id' o 'nombre' nulo
  Cuando:    el módulo de validación procesa el registro
  Entonces:  el estado cambia a INCONSISTENT
             y se registra mensaje de error: "Campo 'id' es obligatorio y no puede ser nulo"
             y se genera un log de validación con tipo de error y campo afectado

CRITERIO-100.5: Detección de factor de tarifa negativo o cero
  Dado que:  se recibe una tarifa CAT con factor <= 0
  Cuando:    el módulo de validación procesa el registro
  Entonces:  el estado cambia a INCONSISTENT
             y se registra mensaje: "Campo 'factor' debe ser un número positivo (>0)"
             y el registro es rechazado para su uso

CRITERIO-100.6: Detección de código postal con formato incorrecto
  Dado que:  se recibe un código postal que no cumple el patrón /^[0-9]{5}$/
  Cuando:    el módulo de validación procesa el registro
  Entonces:  el estado cambia a INCONSISTENT
             y se registra mensaje: "Código postal debe contener exactamente 5 dígitos numéricos"
             y el registro es marcado como inválido
```

**Edge Case**

```gherkin
CRITERIO-100.7: Validación de campo con valor vacío (cadena vacía)
  Dado que:  se recibe un registro con un campo obligatorio como cadena vacía ""
  Cuando:    el módulo de validación procesa el registro
  Entonces:  es tratado igual que un nulo
             y el estado cambia a INCONSISTENT

CRITERIO-100.8: Manejo de esquema inesperado
  Dado que:  se recibe un dato maestro con un tipo de dato inesperado (ej. string en lugar de número)
  Cuando:    el módulo de validación intenta aplicar las reglas
  Entonces:  el estado cambia a INCONSISTENT
             y se registra: "Error de esquema: Campo 'factor' debe ser numérico"
             y se genera un log de error con el tipo de dato recibido
```

---

#### HU-101: Registrar inconsistencias detectadas (HU-101)

```
Como:        Administrador / Sistema (auditor)
Quiero:      Que todas las inconsistencias se registren persistentemente para auditoría y análisis
Para:        Tener trazabilidad de qué datos fueron rechazados, cuándo y por qué

Prioridad:   Alta
Estimación:  S (3 story points)
Dependencias: HU-100 (validación ejecutada)
Capa:        Backend (Java/Spring Boot + MongoDB)
```

#### Criterios de Aceptación — HU-101

**Happy Path**

```gherkin
CRITERIO-101.1: Registro de inconsistencia en MongoDB
  Dado que:  se detecta una inconsistencia en un dato maestro
  Cuando:    se intenta registrar en la colección "data-inconsistencies"
  Entonces:  se crea un documento con campos: id, dataType, value, validationError, status, timestamp
             y el registro se persiste en MongoDB
             y se retorna un ID único del registro de inconsistencia

CRITERIO-101.2: Estructura JSON de log estructurado
  Dado que:  se registra una inconsistencia
  Cuando:    se genera el log estructurado
  Entonces:  el formato es JSON con campos: correlationId, timestamp, dataType, error, severity
             y es compatible con herramientas de análisis de logs
```

**Error Path**

```gherkin
CRITERIO-101.3: Fallo en persistencia de inconsistencia
  Dado que:  MongoDB no está disponible
  Cuando:    se intenta registrar una inconsistencia
  Entonces:  se registra un log de error con código "PERSISTENCE_ERROR"
             y se notifica al sistema de la falla
             y no falla el procesamiento general (no bloquea validación)
```

---

#### HU-102: Aplicar Corrección Automática de Inconsistencias (HU-102)

```
Como:        Sistema (plataformas-danos-back)
Quiero:      Aplicar reglas de corrección automática para tipos de inconsistencias predefinidos
Para:        Mantener la calidad del dato sin intervención manual en casos simples

Prioridad:   Media
Estimación:  M (4 story points)
Dependencias: HU-100, HU-101
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-102

**Happy Path**

```gherkin
CRITERIO-102.1: Corrección automática de inconsistencia de formato menor
  Dado que:  se detecta una inconsistencia de formato menor (ej. espacios extra en un campo de texto)
  Cuando:    el módulo de corrección automática la procesa
  Entonces:  aplica la regla de limpieza (trim) y corrige el dato automáticamente
             y el dato queda en estado VALIDATED con corrección aplicada

CRITERIO-102.2: Asignación de valor por defecto en campo opcional nulo
  Dado que:  se detecta un dato nulo en un campo opcional con valor por defecto definido
  Cuando:    el módulo de corrección lo procesa
  Entonces:  le asigna el valor por defecto predefinido para ese campo
             y registra que se aplicó corrección automática
```

**Edge Case**

```gherkin
CRITERIO-102.3: Registro del valor original al aplicar corrección
  Dado que:  se aplica una corrección automática sobre un dato
  Cuando:    el sistema registra la corrección
  Entonces:  persiste el valor original del dato antes de la corrección
             y el log de inconsistencia incluye: valorOriginal, valorCorregido, reglaAplicada

CRITERIO-102.4: Inconsistencia no corregible automáticamente
  Dado que:  se detecta una inconsistencia que no tiene regla de corrección automática definida
  Cuando:    el módulo de corrección la evalúa
  Entonces:  el dato permanece en estado INCONSISTENT
             y se delega a HU-103 para notificación
```

---

#### HU-103: Notificar Inconsistencias que Requieren Intervención (HU-103)

```
Como:        Sistema (plataformas-danos-back)
Quiero:      Activar una notificación (log de nivel ERROR, alerta) cuando se detectan inconsistencias
             que requieren intervención manual
Para:        Asegurar su resolución oportuna y evitar que afecten el negocio

Prioridad:   Alta
Estimación:  S (3 story points)
Dependencias: HU-100, HU-101, HU-102
Capa:        Backend (Java/Spring Boot)
```

#### Criterios de Aceptación — HU-103

**Happy Path**

```gherkin
CRITERIO-103.1: Alerta por inconsistencia crítica no corregible automáticamente
  Dado que:  se detecta una inconsistencia crítica que no puede corregirse automáticamente
  Cuando:    el sistema finaliza el procesamiento del dato
  Entonces:  envía una alerta (log ERROR estructurado) a los administradores
             con detalles: tipo de dato, campo afectado, regla violada, ID del registro de inconsistencia

CRITERIO-103.2: Notificación incluye enlace al registro de inconsistencia
  Dado que:  se envía una notificación de inconsistencia crítica
  Cuando:    el sistema la genera
  Entonces:  incluye el ID del registro en la colección "data-inconsistencies"
             para que el administrador pueda acceder al detalle
```

**Edge Case**

```gherkin
CRITERIO-103.3: Alerta de alto nivel al superar umbral de inconsistencias
  Dado que:  se supera el umbral configurado de inconsistencias en un lote (ej. > 10% de registros)
  Cuando:    el sistema completa la validación del lote
  Entonces:  emite una alerta de alto nivel con el resumen: total, críticas, warnings
             diferenciada de las alertas individuales por registro

CRITERIO-103.4: No bloqueo del flujo por fallo en notificación
  Dado que:  el mecanismo de notificación (ej. email, Slack) no está disponible
  Cuando:    el sistema intenta enviar la alerta
  Entonces:  registra el fallo de notificación en el log de aplicación
             y continúa el procesamiento sin propagar la excepción
```

---

#### HU-104: Definir Reglas de Validación con Analistas Funcionales (HU-104)

```
Como:        Analista funcional
Quiero:      Definir las reglas de validación de datos maestros en conjunto con el equipo técnico
Para:        Asegurar que cubren los casos de negocio y las expectativas de calidad del dato

Prioridad:   Alta
Estimación:  2 días (análisis y documentación)
Dependencias: HU-100, HU-102
Capa:        Documentación + Backend (reglas configurables)
```

#### Criterios de Aceptación — HU-104

**Happy Path**

```gherkin
CRITERIO-104.1: Definición de reglas por campo en contratos de API
  Dado que:  se definen los contratos de API de los datos maestros
  Cuando:    los analistas funcionales revisan los campos
  Entonces:  cada campo relevante tiene documentada su regla de validación:
             tipo (NOT_NULL, POSITIVE_NUMBER, FORMAT_REGEX), severidad y mensaje de error

CRITERIO-104.2: Clasificación de inconsistencias entre corrección automática y notificación
  Dado que:  se identifican posibles inconsistencias en los datos maestros
  Cuando:    los analistas funcionales las revisan
  Entonces:  cada tipo de inconsistencia está clasificado como:
             "corrección automática" (HU-102) o "requiere intervención" (HU-103)

CRITERIO-104.3: Reglas implementadas cubren las definiciones documentadas
  Dado que:  las reglas de validación están documentadas en la especificación
  Cuando:    el equipo de desarrollo las implementa en ValidationRule
  Entonces:  existe un test por cada regla documentada que verifica su correcta aplicación
```

---

### Reglas de Negocio

#### BR-001: Validación de campos obligatorios
- **Descripción**: Todos los campos marcados como obligatorios en un tipo de dato maestro no pueden ser nulos o cadenas vacías.
- **Trigger**: Al procesar cualquier dato maestro desde catálogos, tarifas, códigos postales, etc.
- **Lógica**: `IF campo IS NULL OR campo == "" THEN estado = INCONSISTENT AND registrar error`
- **Aplicable a**: 
  - Suscriptores: `id`, `nombre`
  - Agentes: `id`, `nombre`
  - Giros: `id`, `nombre`
  - Códigos postales: `codigo`, `ciudad`, `estado`

#### BR-002: Validación de positividad en campos numéricos
- **Descripción**: Campos numéricos específicos (factores de tarifa) deben ser valores mayores que cero.
- **Trigger**: Al procesar datos maestros con factores (tarifas CAT, fire, electronic equipment).
- **Lógica**: `IF factor <= 0 THEN estado = INCONSISTENT AND registrar error "valor debe ser >0"`
- **Aplicable a**:
  - TariffCat: `factorTEV`, `factorFHM` (ambos > 0)
  - TariffFire: `factor` (> 0)
  - TariffElectronicEquipment: `factor` (> 0)

#### BR-003: Validación de formato de código postal
- **Descripción**: El campo `codigo` de un código postal debe coincidir con el patrón de 5 dígitos numéricos.
- **Trigger**: Al procesar datos maestros de códigos postales.
- **Lógica**: `IF codigo NOT MATCH /^[0-9]{5}$/ THEN estado = INCONSISTENT AND registrar error`
- **Validación adicional**: El código debe existir y estar activo en el catálogo de municipios/estados válidos (si aplica).

#### BR-004: Estados de validación
- **Valores permitidos**: `PENDING_VALIDATION` | `VALIDATED` | `INCONSISTENT`
- **Transiciones**:
  - Inicial: `PENDING_VALIDATION`
  - Después de validación exitosa: `VALIDATED`
  - Después de detectar inconsistencia: `INCONSISTENT` (irreversible)
- **Restricción**: Un registro con estado `INCONSISTENT` nunca cambia a `VALIDATED`.

#### BR-005: Niveles de severidad de inconsistencias
- **ERROR / CRÍTICO**: Falta campo obligatorio, valor está completamente fuera de rango, tipo de dato incorrecto.
- **WARNING**: Validación adicional fallida (ej. código postal válido pero no existe en catálogo de municipios).
- **INFO**: Validación pasada pero con recomendación (ej. campo con largo máximo cercano al límite).

#### BR-006: Registro de auditoría
- **Obligatorio registrar**: Timestamp (UTC), tipo de dato, ID del registro, campo afectado, regla violada, severidad.
- **Formato**: Logs JSON estructurados con `correlation-id` para trazabilidad de transacciones.
- **Retención**: Mínimo 90 días en MongoDB; logs de aplicación archivados diariamente.

#### BR-007: Reglas de corrección automática
- Solo se aplican correcciones automáticas a inconsistencias explícitamente definidas (no se crean correcciones implícitas).
- Correcciones permitidas: `TRIM` (espacios extra), `DEFAULT_VALUE` (null en campo opcional), `UPPERCASE/LOWERCASE` (normalización de casing).
- Las correcciones no aplican sobre campos clave (ID, zona, código) — solo sobre campos descriptivos o auxiliares.
- Toda corrección guarda el valor original antes de modificarlo (`valorOriginal`, `valorCorregido`, `reglaCorrección`).

#### BR-008: Umbral de alertas de inconsistencias
- Si más del 10% de los registros de un lote resultan INCONSISTENT, se emite una alerta de alto nivel (log ERROR).
- Los umbrales son configurables vía `application.yml` (`validation.inconsistency-threshold-percent`, default: 10).
- Inconsistencias de nivel CRITICAL activan notificación individual independientemente del umbral.

#### BR-009: Definición colaborativa de reglas
- Ninguna regla de validación se implementa sin la aprobación del analista funcional responsable.
- El catálogo de reglas (`ValidationRule`) es la fuente de verdad; cualquier cambio requiere actualizar spec y tests.

---

## 2. DISEÑO

### Modelos de Datos

#### Entidades afectadas

| Entidad | Almacén | Cambios | Descripción |
|---------|---------|---------|-------------|
| `DataInconsistency` | `data-inconsistencies` (MongoDB) | nueva | Registro de inconsistencias detectadas en datos maestros |
| `ValidationRule` | `validation-rules` (MongoDB) | nueva | Definiciones configurables de reglas de validación |
| `SubscriberDto` | memory/cache | extensión | Agregar campo `dataStatus` (PENDING_VALIDATION, VALIDATED, INCONSISTENT) |
| `TariffCatDto` | memory/cache | extensión | Agregar campo `dataStatus` |
| `ZipCodeDto` | memory/cache | extensión | Agregar campo `dataStatus` |

#### Campos del modelo — DataInconsistency

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | string | sí | auto-generado (UUID) | Identificador único del registro de inconsistencia |
| `dataType` | enum | sí | SUBSCRIBER, AGENT, BUSINESS_LINE, ZIP_CODE, TARIFF_CAT, TARIFF_FIRE, TARIFF_ELECTRONIC | Tipo de dato maestro validado |
| `dataId` | string | sí | referencia al id del dato original | ID del registro de datos maestros que falló validación |
| `value` | object | sí | JSON del registro original | Copia completa del dato que fue rechazado |
| `validationError` | object | sí | `{ field: string, rule: string, message: string, severity: string }` | Detalles del error de validación |
| `status` | string | sí | CRITICAL, WARNING, INFO | Nivel de severidad de la inconsistencia |
| `correlationId` | string | sí | heredado del contexto de request | ID de correlación para auditoría |
| `createdAt` | datetime (UTC) | sí | auto-generado | Timestamp de detección de inconsistencia |
| `resolvedAt` | datetime (UTC) | no | null hasta que se resuelva | Timestamp cuando se resuelve o archiva |
| `resolution` | string | no | null | Nota de cómo se resolvió (manual/automática) |

#### Campos del modelo — ValidationRule

| Campo | Tipo | Obligatorio | Validación | Descripción |
|-------|------|-------------|------------|-------------|
| `id` | string | sí | auto-generado (UUID) | ID de la regla |
| `dataType` | enum | sí | SUBSCRIBER, AGENT, BUSINESS_LINE, ZIP_CODE, TARIFF_CAT, ... | Tipo de dato a validar |
| `fieldName` | string | sí | max 100 chars | Nombre del campo a validar |
| `ruleType` | enum | sí | NOT_NULL, NOT_EMPTY, POSITIVE_NUMBER, FORMAT_REGEX, ENUM_VALUE, RANGE | Tipo de validación |
| `parameters` | object | no | JSON con configuración de la regla | `{ pattern: "^[0-9]{5}$", minValue: 0, maxValue: null }` |
| `errorMessage` | string | sí | max 500 chars | Mensaje de error personalizado |
| `severity` | enum | sí | CRITICAL, WARNING, INFO | Severidad si la regla falla |
| `enabled` | boolean | sí | true/false | Si la regla está activa |
| `createdAt` | datetime (UTC) | sí | auto-generado | Timestamp creación de la regla |
| `updatedAt` | datetime (UTC) | sí | auto-generado | Timestamp última actualización |
| `createdBy` | string | sí | usuario o sistema que creó | Auditoría |

#### Índices / Constraints

- `data-inconsistencies`:
  - Índice en `dataType, createdAt` (búsqueda de inconsistencias por tipo y fecha)
  - Índice en `dataId` (búsqueda de inconsistencias de un registro específico)
  - Índice en `status` (filtro por severidad)
  - TTL index en `createdAt` con expiración 90 días (auto-limpieza de registros antiguos)

- `validation-rules`:
  - Índice único en `dataType, fieldName` (evitar reglas duplicadas)
  - Índice en `enabled` (búsqueda de reglas activas)

---

### API Endpoints

#### POST /api/v1/data-validation/validate
- **Descripción**: Valida un lote de registros de datos maestros contra reglas de negocio
- **Auth requerida**: sí (token interno o sistema-a-sistema)
- **Request Body**:
  ```json
  {
    "dataType": "SUBSCRIBER",
    "records": [
      { "id": "sub-001", "nombre": "Aseguradora ABC", "clave": "ABC", "activo": true }
    ],
    "correlationId": "req-123456"
  }
  ```
- **Response 200**:
  ```json
  {
    "totalRecords": 1,
    "validRecords": 1,
    "inconsistentRecords": 0,
    "results": [
      {
        "id": "sub-001",
        "status": "VALIDATED",
        "errors": []
      }
    ]
  }
  ```
- **Response 400**: `dataType` inválido o formato de request incorrecto
- **Response 207 (Multi-Status)**: Validación parcial (algunos registros válidos, otros inválidos)
  ```json
  {
    "totalRecords": 2,
    "validRecords": 1,
    "inconsistentRecords": 1,
    "results": [
      { "id": "sub-001", "status": "VALIDATED", "errors": [] },
      { "id": "sub-002", "status": "INCONSISTENT", "errors": [{ "field": "nombre", "message": "Campo es obligatorio" }] }
    ]
  }
  ```
- **Response 500**: Error interno del servidor

#### GET /api/v1/data-validation/inconsistencies
- **Descripción**: Lista inconsistencias registradas con filtros opcionales
- **Auth requerida**: sí
- **Query Parameters**:
  - `dataType` (opcional): SUBSCRIBER, TARIFF_CAT, etc.
  - `status` (opcional): CRITICAL, WARNING, INFO
  - `createdAfter` (opcional): timestamp ISO para filtrar por fecha
  - `page` (opcional): número de página (default: 1)
  - `size` (opcional): registros por página (default: 20)
- **Response 200**:
  ```json
  {
    "totalCount": 45,
    "page": 1,
    "size": 20,
    "data": [
      {
        "id": "incons-uuid-001",
        "dataType": "SUBSCRIBER",
        "dataId": "sub-002",
        "validationError": {
          "field": "nombre",
          "rule": "NOT_NULL",
          "message": "Campo 'nombre' es obligatorio"
        },
        "status": "CRITICAL",
        "createdAt": "2026-04-27T14:30:00Z"
      }
    ]
  }
  ```
- **Response 401**: Sin autenticación

#### GET /api/v1/data-validation/rules
- **Descripción**: Lista todas las reglas de validación activas configuradas
- **Auth requerida**: sí
- **Query Parameters**:
  - `dataType` (opcional): filtrar por tipo
  - `enabled` (opcional): true/false
- **Response 200**: Array de objetos `ValidationRule`
- **Response 401**: Sin autenticación

#### POST /api/v1/data-validation/rules (Admin only)
- **Descripción**: Crea una nueva regla de validación
- **Auth requerida**: sí (requiere rol ADMIN)
- **Request Body**: Objeto `ValidationRule` (sin id, createdAt)
- **Response 201**: Regla creada con id asignado
- **Response 403**: Permiso denegado (no es admin)
- **Response 400**: Validación de parámetros fallida

#### PUT /api/v1/data-validation/rules/{ruleId} (Admin only)
- **Descripción**: Actualiza una regla de validación
- **Auth requerida**: sí (requiere rol ADMIN)
- **Request Body**: Campos a actualizar
- **Response 200**: Regla actualizada
- **Response 404**: Regla no encontrada

---

### Componentes de Backend

#### Service: `DataValidationService`

```java
public interface DataValidationService {
    
    // Validar un lote de registros
    ValidationResult validateBatch(String dataType, List<Map<String, Object>> records, String correlationId);
    
    // Validar un registro individual
    ValidationResult validateRecord(String dataType, Map<String, Object> record, String correlationId);
    
    // Obtener inconsistencias registradas
    Page<DataInconsistency> getInconsistencies(
        String dataType,
        String status,
        LocalDateTime createdAfter,
        Pageable pageable
    );
    
    // Resolver una inconsistencia manualmente
    void resolveInconsistency(String inconsistencyId, String resolution);
}
```

**Responsabilidades**:
- Orquestar validación usando reglas configurables
- Delegar a `DataValidationEngine` para aplicar reglas
- Llamar a `DataInconsistencyRepository` para registrar errores
- Registrar logs estructurados
- Retornar resultados de validación

#### Service: `DataValidationEngine`

```java
public interface DataValidationEngine {
    
    // Obtener reglas aplicables a un tipo de dato
    List<ValidationRule> getRulesForDataType(String dataType);
    
    // Ejecutar una regla contra un valor
    ValidationError applyRule(ValidationRule rule, String fieldName, Object value);
}
```

**Responsabilidades**:
- Aplicar reglas de validación específicas (NOT_NULL, POSITIVE_NUMBER, FORMAT_REGEX, etc.)
- Retornar detalles del error si la validación falla
- Ser altamente testeable y reutilizable

#### Service: `DataCorrectionService` (HU-102)

```java
public interface DataCorrectionService {
    
    // Aplicar correcciones automáticas a un registro con inconsistencias
    CorrectionResult applyCorrections(String dataType, Map<String, Object> record, List<ValidationError> errors);
    
    // Verificar si una inconsistencia tiene regla de corrección automática
    boolean hasCorrectionRule(String dataType, String fieldName, String ruleType);
}
```

**Responsabilidades**:
- Aplicar correcciones conservadoras (TRIM, DEFAULT_VALUE, casing) a datos con inconsistencias simples
- Preservar el valor original antes de corregir (`originalValue`)
- Registrar la corrección aplicada en el log de inconsistencia

#### Service: `InconsistencyNotificationService` (HU-103)

```java
public interface InconsistencyNotificationService {
    
    // Notificar una inconsistencia crítica individual
    void notifyCritical(DataInconsistency inconsistency);
    
    // Emitir alerta cuando se supera el umbral del lote
    void notifyBatchThresholdExceeded(int total, int inconsistentCount, String dataType);
}
```

**Responsabilidades**:
- Emitir log estructurado de nivel ERROR con detalles del registro de inconsistencia
- Calcular si el % de inconsistencias en el lote supera el umbral configurable
- No bloquear el flujo de procesamiento si la notificación falla (manejo de errores silencioso)

#### Repository: `DataInconsistencyRepository`

```java
public interface DataInconsistencyRepository extends MongoRepository<DataInconsistency, String> {
    
    List<DataInconsistency> findByDataType(String dataType, Pageable pageable);
    
    List<DataInconsistency> findByStatus(String status, Pageable pageable);
    
    List<DataInconsistency> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
}
```

#### Repository: `ValidationRuleRepository`

```java
public interface ValidationRuleRepository extends MongoRepository<ValidationRule, String> {
    
    List<ValidationRule> findByDataTypeAndEnabled(String dataType, boolean enabled);
    
    Optional<ValidationRule> findByDataTypeAndFieldName(String dataType, String fieldName);
}
```

#### Controller: `DataValidationController`

```java
@RestController
@RequestMapping("/api/v1/data-validation")
public class DataValidationController {
    
    @PostMapping("/validate")
    public ResponseEntity<ValidationResult> validate(@Valid @RequestBody ValidationRequest request) { ... }
    
    @GetMapping("/inconsistencies")
    public ResponseEntity<Page<DataInconsistency>> getInconsistencies(...) { ... }
    
    @GetMapping("/rules")
    public ResponseEntity<List<ValidationRule>> getRules(...) { ... }
    
    @PostMapping("/rules")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ValidationRule> createRule(@Valid @RequestBody ValidationRule rule) { ... }
    
    @PutMapping("/rules/{ruleId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ValidationRule> updateRule(@PathVariable String ruleId, ...) { ... }
}
```

#### DTOs

- `ValidationRequest`: Entrada para validar un lote
- `ValidationResult`: Resultado de validación con detalles por registro
- `ValidationError`: Detalle de un error de validación
- `DataInconsistencyResponse`: Representación de inconsistencia para API

#### Configuración: `ValidationRulesConfig`

- Beans para carga de reglas de validación desde MongoDB o archivo de configuración
- Caché en memoria (Caffeine) con TTL configurable (default: 1 hora) para reglas
- Invalidación manual de caché mediante endpoint admin

---

### Implementación de Validaciones

#### Reglas incluidas en SPEC-007

**Para Suscriptores (SUBSCRIBER)**:
- `id`: NOT_NULL, NOT_EMPTY
- `nombre`: NOT_NULL, NOT_EMPTY, max 255 chars
- `clave`: NOT_NULL, NOT_EMPTY
- `activo`: NOT_NULL

**Para Agentes (AGENT)**:
- `id`: NOT_NULL, NOT_EMPTY
- `nombre`: NOT_NULL, NOT_EMPTY, max 255 chars
- `clave`: NOT_NULL, NOT_EMPTY
- `activo`: NOT_NULL

**Para Giros (BUSINESS_LINE)**:
- `id`: NOT_NULL, NOT_EMPTY
- `nombre`: NOT_NULL, NOT_EMPTY, max 255 chars

**Para Códigos Postales (ZIP_CODE)**:
- `codigo`: NOT_NULL, FORMAT_REGEX (`^[0-9]{5}$`)
- `ciudad`: NOT_NULL, NOT_EMPTY
- `estado`: NOT_NULL, NOT_EMPTY

**Para Tarifas CAT (TARIFF_CAT)**:
- `zona`: NOT_NULL, NOT_EMPTY
- `factorTEV`: POSITIVE_NUMBER (> 0), NUMERIC
- `factorFHM`: POSITIVE_NUMBER (> 0), NUMERIC

**Para Tarifas Fire (TARIFF_FIRE)**:
- `factor`: POSITIVE_NUMBER (> 0), NUMERIC

**Para Tarifas Electronic Equipment (TARIFF_ELECTRONIC_EQUIPMENT)**:
- `factor`: POSITIVE_NUMBER (> 0), NUMERIC

---

### Arquitectura y Dependencias

#### Paquetes nuevos requeridos

```
com.plataformas_danos_back
├── validation/
│   ├── service/
│   │   ├── DataValidationService.java          (HU-100)
│   │   ├── DataValidationServiceImpl.java
│   │   ├── DataValidationEngine.java           (HU-100)
│   │   ├── DataValidationEngineImpl.java
│   │   ├── DataCorrectionService.java          (HU-102)
│   │   ├── DataCorrectionServiceImpl.java
│   │   ├── InconsistencyNotificationService.java  (HU-103)
│   │   └── InconsistencyNotificationServiceImpl.java
│   ├── controller/
│   │   └── DataValidationController.java
│   ├── repository/
│   │   ├── DataInconsistencyRepository.java
│   │   └── ValidationRuleRepository.java
│   ├── model/
│   │   ├── entity/
│   │   │   ├── DataInconsistency.java
│   │   │   ├── ValidationRule.java
│   │   │   └── CorrectionRule.java             (HU-102, reglas de corrección automática)
│   │   └── dto/
│   │       ├── ValidationRequest.java
│   │       ├── ValidationResult.java
│   │       ├── ValidationError.java
│   │       ├── CorrectionResult.java           (HU-102)
│   │       └── DataInconsistencyResponse.java
│   ├── config/
│   │   ├── ValidationRulesConfig.java
│   │   └── ValidationProperties.java          (HU-103, umbral configurable)
│   └── exception/
│       ├── ValidationException.java
│       ├── InvalidRuleException.java
│       └── InconsistencyRecordException.java
```

#### Dependencias Maven

- Ya existentes en el proyecto:
  - `spring-boot-starter-validation` (Bean Validation)
  - `spring-boot-starter-data-mongodb`
  - `spring-boot-starter-cache` (Caffeine)
  - Lombok

#### Integración en el ciclo de vida

1. **Después de FT-015 a FT-018**: Cuando se reciben catálogos, tarifas y códigos postales, se ejecutan validaciones
2. **Punto de integración**: En `CatalogsServiceImpl`, `TariffsServiceImpl`, `ZipCodeServiceImpl` — después de obtener datos de servicios externos, antes de exponer al frontend
3. **Patrón**: Usar interceptor o decorator en servicios para validar automáticamente

#### Ejemplo de integración

```java
@Service
@RequiredArgsConstructor
public class CatalogsServiceImpl implements CatalogsService {
    
    private final CatalogsClient catalogsClient;
    private final DataValidationService validationService;
    
    @Override
    public List<SubscriberDto> getSubscribers() {
        // Obtener desde servicio externo
        List<SubscriberDto> subscribers = catalogsClient.fetchSubscribers();
        
        // VALIDAR
        ValidationResult result = validationService.validateBatch(
            "SUBSCRIBER",
            subscribers.stream().map(ObjectMapper::convertValue).collect(toList()),
            MDC.get("correlationId")
        );
        
        // Retornar solo registros VALIDATED
        return subscribers.stream()
            .filter(sub -> result.isValid(sub.getId()))
            .collect(toList());
    }
}
```

---

### Notas de Implementación

> **Decisión de Diseño**: Las validaciones se ejecutan **de forma síncrona** inmediatamente después de recibir datos, no de forma asíncrona. Esto garantiza que los datos en caché del frontend siempre sean consistentes.

> **Caché de Reglas**: Las reglas de validación se cargan una sola vez al iniciar la aplicación y se cachean en memoria (Caffeine TTL: 1 hora). Cambios en reglas requieren reiniciar la app o invalidación manual.

> **Severidad vs. Bloqueo**: Inconsistencias de nivel `CRITICAL` o `ERROR` bloquean el uso del registro. Inconsistencias `WARNING` se registran pero permiten el procesamiento (según configuración).

> **Extensibilidad**: El diseño usa un patrón Strategy para tipos de validación. Agregar nuevas validaciones requiere:
>  1. Crear nueva clase implementando `ValidationRule`
>  2. Registrarla en `ValidationRulesConfig`
>  3. Agregar tests unitarios

> **Auditoría**: Todos los registros de inconsistencias incluyen `correlationId` para correlacionar con requests originales en logs distribuidos.

> **Impacto Frontal**: El frontend no verá datos inconsistentes. El backend filtra automáticamente registros con estado `INCONSISTENT` antes de retornarlos.

---

## 3. LISTA DE TAREAS

> Checklist accionable para todos los agentes. Marcar cada ítem (`[x]`) al completarlo.
> El Orchestrator monitorea este checklist para determinar el progreso.

### Backend

#### Implementación — HU-100: Validación

- [x] Crear modelo `DataInconsistency` con anotación `@Document` en `model/entity/`
- [x] Crear modelo `ValidationRule` con anotación `@Document` en `model/entity/`
- [ ] Extender DTOs existentes (`SubscriberDto`, `TariffCatDto`, `ZipCodeDto`, etc.) con campo `dataStatus`
- [x] Crear DTOs de Request/Response (`ValidationRequest`, `ValidationResult`, `ValidationError`, `DataInconsistencyResponse`) en `model/dto/`
- [x] Implementar `DataInconsistencyRepository` — métodos CRUD y búsqueda por tipo/status/fecha
- [x] Implementar `ValidationRuleRepository` — métodos de búsqueda y filtrado
- [x] Implementar `DataValidationEngine` — motor para aplicar reglas individuales (NOT_NULL, POSITIVE_NUMBER, FORMAT_REGEX, etc.)
- [x] Implementar `DataValidationService` — orquestador de validaciones y coordinador con repositorio
- [x] Implementar `DataValidationController` — endpoints REST para validación, consulta de inconsistencias y gestión de reglas
- [x] Crear `ValidationRulesConfig` — carga de reglas iniciales desde archivo o base de datos
- [x] Crear excepciones custom en `exception/` (ValidationException, InvalidRuleException, InconsistencyRecordException)
- [ ] Integrar validación en `CatalogsServiceImpl` — después de obtener suscriptores, agentes, giros
- [ ] Integrar validación en `TariffsServiceImpl` — después de obtener tarifas CAT, Fire, Electronic Equipment
- [ ] Integrar validación en `ZipCodeServiceImpl` — después de obtener códigos postales
- [x] Configurar índices en MongoDB para colecciones `data-inconsistencies` y `validation-rules`
- [x] Configurar TTL en `data-inconsistencies` (90 días)
- [ ] Configurar caché Caffeine para `ValidationRule` con TTL 1 hora
- [x] Registrar endpoint `/api/v1/data-validation/**` en punto de entrada de la app

#### Implementación — HU-102: Corrección Automática

- [x] Crear modelo `CorrectionRule` con anotación `@Document` en `model/entity/` — tipos: TRIM, DEFAULT_VALUE, NORMALIZE_CASE
- [x] Crear DTO `CorrectionResult` con campos: `corrected` (boolean), `originalValue`, `correctedValue`, `ruleApplied`
- [x] Implementar `DataCorrectionService` — aplica correcciones conservadoras (solo campos descriptivos/auxiliares, no clave)
- [x] Implementar lógica de `hasCorrectionRule()` — verifica si existe regla de corrección para el campo/tipo de inconsistencia
- [x] Guardar `originalValue` en el registro `DataInconsistency` cuando se aplica corrección
- [x] Agregar campo `correctionApplied` y `correctionDetail` al modelo `DataInconsistency`
- [x] Integrar `DataCorrectionService` en el flujo de `DataValidationService` (después de detectar inconsistencia, intentar corrección)

#### Implementación — HU-103: Notificación

- [x] Crear `ValidationProperties` — propiedades configurables en `application.yml`: `validation.inconsistency-threshold-percent` (default: 10)
- [x] Implementar `InconsistencyNotificationService` — emite log ERROR estructurado con detalles de la inconsistencia crítica
- [x] Implementar lógica de umbral de lote: si % inconsistencias > threshold → alerta de alto nivel
- [x] Manejar fallo de notificación silenciosamente (try/catch + log) — no bloquear procesamiento principal
- [x] Integrar `InconsistencyNotificationService` en `DataValidationService` al detectar inconsistencias CRITICAL

#### Implementación — HU-104: Definición de Reglas

- [x] Documentar catálogo inicial de `ValidationRule` en `ValidationRulesConfig` (reglas de HU-100 para todos los tipos de datos maestros)
- [x] Documentar catálogo inicial de `CorrectionRule` (reglas TRIM para campos de texto, DEFAULT_VALUE para opcionales)
- [x] Agregar endpoint `GET /api/v1/data-validation/rules` con filtro por `dataType` — para revisión por analistas
- [x] Agregar seed/migration de reglas iniciales en MongoDB (o archivo de configuración YAML)

#### Tests Backend

- [x] `test_validateSubscriber_validData_returnsValidated` — HU-100: suscriptor válido
- [x] `test_validateSubscriber_nullId_returnsInconsistent` — HU-100: id nulo
- [x] `test_validateSubscriber_emptyName_returnsInconsistent` — HU-100: nombre vacío
- [x] `test_validateTariffCat_validFactors_returnsValidated` — HU-100: tarifa válida
- [x] `test_validateTariffCat_negativeFactorTEV_returnsInconsistent` — HU-100: factor negativo
- [x] `test_validateTariffCat_zeroFactor_returnsInconsistent` — HU-100: factor = 0
- [x] `test_validateZipCode_validFormat_returnsValidated` — HU-100: código postal válido
- [x] `test_validateZipCode_invalidFormat_returnsInconsistent` — HU-100: formato incorrecto
- [x] `test_validateZipCode_nonNumeric_returnsInconsistent` — HU-100: caracteres no numéricos
- [x] `test_validateBatch_mixedRecords_returns207MultiStatus` — HU-100: lote parcial
- [x] `test_registerInconsistency_validData_persistsInMongoDB` — HU-101: persistencia
- [x] `test_getInconsistencies_byDataType_returnsFiltered` — HU-101: búsqueda por tipo
- [x] `test_getInconsistencies_byStatus_returnsFiltered` — HU-101: búsqueda por severidad
- [x] `test_dataValidationEngine_applyRule_NOT_NULL_detects` — HU-100: engine NOT_NULL
- [x] `test_dataValidationEngine_applyRule_POSITIVE_NUMBER_detects` — HU-100: engine positividad
- [x] `test_dataValidationEngine_applyRule_REGEX_detects` — HU-100: engine regex
- [x] `test_dataCorrectionService_trimSpaces_corrects` — HU-102: corrección TRIM
- [x] `test_dataCorrectionService_defaultValue_assignsDefault` — HU-102: DEFAULT_VALUE
- [x] `test_dataCorrectionService_preservesOriginalValue` — HU-102: auditoría valor original
- [x] `test_dataCorrectionService_noCorrectionRule_leavesInconsistent` — HU-102: sin regla aplicable
- [x] `test_notificationService_critical_emitsErrorLog` — HU-103: alerta inconsistencia crítica
- [x] `test_notificationService_batchThreshold_emitsHighLevelAlert` — HU-103: umbral de lote
- [x] `test_notificationService_failure_doesNotPropagateException` — HU-103: fallo silencioso
- [ ] `test_catalogsService_getSubscribers_validatesAndFilters` — integración catalogs (pendiente: integración no implementada)
- [ ] `test_tariffsService_getTariffsCat_validatesAndFilters` — integración tarifas (pendiente: integración no implementada)
- [ ] `test_zipCodeService_getZipCodes_validatesAndFilters` — integración códigos postales (pendiente: integración no implementada)
- [x] `test_controller_postValidate_returns200_success` — endpoint POST exitoso
- [x] `test_controller_postValidate_returns207_multiStatus` — endpoint POST con fallos parciales
- [x] `test_controller_getInconsistencies_returns200` — endpoint GET inconsistencias
- [x] `test_controller_getRules_returns200` — endpoint GET reglas
- [ ] `test_controller_postRule_adminOnly_requires_authorization` — autorización admin (endpoint no implementado aún)
- [ ] `test_validationRuleCache_expires_after_ttl` — invalidación caché (pendiente: cache no configurado)
- [ ] Cobertura global ≥ 80%, módulo validación ≥ 90%

### Frontend

#### Implementación

- [ ] *(Sin cambios requeridos en frontend para SPEC-007)* — Validaciones son transparentes, ocurren en backend
- [ ] *(Opcional para futuro)* Crear página `app/admin/validation-rules` para gestión de reglas (POST SPEC-007)
- [ ] *(Opcional para futuro)* Crear página `app/admin/data-inconsistencies` para auditoría (POST SPEC-007)

#### Tests Frontend

- [ ] *(Sin tests frontend requeridos para SPEC-007)*

### QA

- [ ] Ejecutar skill `/gherkin-case-generator` contra criterios CRITERIO-100.1 a 100.8, CRITERIO-101.1 a 101.3, CRITERIO-102.1 a 102.4, CRITERIO-103.1 a 103.4, CRITERIO-104.1 a 104.3
- [ ] Ejecutar skill `/risk-identifier` → clasificación ASD de riesgos de validación y consistencia
- [ ] Validar cobertura de tests contra todos los criterios de aceptación
- [ ] Verificar que todas las reglas de negocio (BR-001 a BR-006) están cubiertas en tests
- [ ] Validar que logs estructurados JSON contienen `correlationId`, `timestamp`, `dataType`, `severity`
- [ ] Pruebas de integración: Enviar datos inválidos vía mock server → verificar detección y registro
- [ ] Pruebas de volumen: Enviar lote de 10,000 registros → verificar performance < 10 minutos
- [ ] Validar que backend filtra registros inválidos antes de exponerlos al frontend
- [ ] Pruebas de caché: Modificar una regla en MongoDB → verificar que se refleja en validaciones (invalidar manualmente o esperar 1 hora)
- [ ] Validar retención de datos en MongoDB: Verificar que registros > 90 días se eliminan automáticamente (TTL)
- [ ] Pruebas de autorización: Intentar crear/actualizar reglas sin rol ADMIN → verificar 403
- [ ] Actualizar estado spec: `status: IMPLEMENTED` cuando todos los ítems estén marcados
