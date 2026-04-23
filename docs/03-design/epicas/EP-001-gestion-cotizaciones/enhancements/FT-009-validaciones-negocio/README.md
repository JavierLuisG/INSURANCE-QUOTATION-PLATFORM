## FT-009: Implementación de Reglas de Negocio y Validaciones

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: enhancements

---

### 1. Descripción

Esta feature establece un conjunto de reglas de validación y lógica de negocio dentro del sistema de cotización, asegurando consistencia, integridad de datos y aplicación correcta de políticas técnicas y comerciales durante la captura y cálculo de información.

---

### 2. Objetivo de Negocio

Garantizar que toda la información ingresada y procesada por el sistema cumpla con reglas de consistencia, validación y lógica de negocio definidas, asegurando resultados correctos, auditables y alineados con políticas de suscripción.

---

### 3. Alcance Funcional

Incluye:

* Validación de datos generales de cotización

  * RFC
  * Vigencia (fechas)
  * Campos obligatorios
* Validación de datos de ubicaciones de riesgo

  * Valor del bien
  * Año de construcción
  * Campos requeridos
* Motor de reglas de negocio para cálculo de primas

  * Recargos
  * Descuentos
  * Factores técnicos
* Generación de mensajes de error

  * Validaciones simples y múltiples
  * Mensajes estructurados y claros
* Trazabilidad de reglas de negocio

  * Registro de reglas aplicadas
  * Soporte para auditoría y mantenimiento

No incluye:

* Persistencia de datos (FT-008)
* Integraciones externas (FT-007)
* UI completa de flujo de cotización
* Definición de tarifas externas (solo consumo lógico)

---

### 4. Historias de Usuario

| HU     | Nombre                         | Descripción corta                              |
| ------ | ------------------------------ | ---------------------------------------------- |
| HU-154 | Validación datos generales     | Validar RFC, vigencia y campos obligatorios    |
| HU-155 | Validación ubicación de riesgo | Validar datos técnicos de cada ubicación       |
| HU-156 | Reglas de cálculo de primas    | Aplicar lógica de negocio en cálculo de primas |
| HU-157 | Mensajes de error              | Generar errores claros y accionables           |
| HU-158 | Trazabilidad de reglas         | Documentar y auditar reglas de negocio         |

---

### 5. Flujo Funcional

1. El usuario ingresa datos de cotización (general y ubicaciones)
2. El sistema ejecuta validaciones en dos niveles:

   * Validaciones de datos generales (RFC, vigencia, obligatorios)
   * Validaciones de ubicaciones (valor, año, consistencia)
3. Si existen errores:

   * Se agregan a un contenedor estructurado de validaciones
   * Se transforman en mensajes claros para el usuario
   * Se detiene el flujo de persistencia o cálculo
4. Si los datos son válidos:

   * Se ejecuta el motor de reglas de negocio
   * Se aplican recargos, descuentos y factores técnicos
5. Durante el cálculo:

   * Se registran reglas aplicadas y parámetros utilizados
6. El sistema retorna:

   * Resultado de prima calculada
   * Evidencia de reglas aplicadas
   * Advertencias o mensajes informativos si aplica

---

### 6. Dependencias Técnicas

* Motor de reglas de negocio (rule engine o implementación propia)
* Capa de validación backend (services o domain validators)
* DTOs de entrada/salida con soporte de errores estructurados
* Sistema de mapeo de errores (error catalog / error factory)
* Módulo de cálculo de primas (FT-004 / FT-006 implícito)
* Sistema de logging para trazabilidad de reglas
* Contratos frontend-backend para validaciones consistentes

---

### 7. Consideraciones Técnicas

* Validaciones deben ejecutarse en **backend como fuente de verdad**
* Frontend debe replicar reglas para UX, pero no sustituir backend
* Separación clara entre:

  * Validación (input correctness)
  * Reglas de negocio (decisión de cálculo)
* Diseño de reglas debe ser:

  * Declarativo o componible (evitar lógica acoplada)
* Mensajes de error deben ser:

  * Estructurados (campo + causa + severidad)
* Motor de reglas debe permitir:

  * Extensibilidad sin modificar lógica central
* Trazabilidad:

  * Cada regla aplicada debe registrarse con contexto (inputs y resultado)
* Evitar “if-else hell” en cálculo de primas:

  * Preferible estrategia / specification pattern
* Mantener separación estricta entre validación, dominio y presentación

---
