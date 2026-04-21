## FT-021: Capa de Validación y Gestión de Inconsistencias de Datos Maestros

### 1. Descripción

Esta feature implementa una capa transversal encargada de validar, detectar, registrar y gestionar inconsistencias en los datos maestros provenientes de servicios externos (`Plataforma-core-ohs` o su mock), asegurando su calidad antes de ser utilizados por el sistema.

---

### 2. Objetivo de Negocio

Garantizar la integridad, consistencia y confiabilidad de los datos maestros (catálogos, tarifas, códigos postales, etc.), evitando que información incorrecta impacte negativamente en el cálculo de primas y la operación del cotizador.

---

### 3. Alcance Funcional

Incluye:

* Validación de datos maestros contra reglas de negocio
* Detección de inconsistencias
* Registro estructurado de inconsistencias
* Corrección automática de inconsistencias simples
* Notificación de inconsistencias críticas
* Definición y documentación de reglas de validación

No incluye:

* Consumo directo de servicios externos (FT-015 a FT-018)
* Ejecución del cálculo de primas (FT-012)

---

### 4. Historias de Usuario

| HU     | Nombre                    | Descripción corta              |
| ------ | ------------------------- | ------------------------------ |
| HU-100 | Validar datos maestros    | Aplica reglas de validación    |
| HU-101 | Registrar inconsistencias | Guarda errores detectados      |
| HU-102 | Corrección automática     | Ajusta inconsistencias simples |
| HU-103 | Notificar inconsistencias | Alerta errores críticos        |
| HU-104 | Definir reglas            | Documenta reglas con analistas |

---

### 5. Flujo Funcional

1. Sistema recibe datos maestros desde integraciones externas (FT-015–FT-018)
2. Se ejecutan reglas de validación sobre los datos (HU-100)
3. Si se detectan inconsistencias:

   * Se registran en repositorio/log (HU-101)
   * Se intenta corrección automática si aplica (HU-102)
4. Si la inconsistencia es crítica o no corregible:

   * Se genera notificación/alerta (HU-103)
5. Reglas de validación y corrección son definidas y mantenidas (HU-104)

---

### 6. Dependencias Técnicas

* Datos provenientes de FT-015, FT-016, FT-017, FT-018
* Módulo de validación de datos
* Repositorio/log de inconsistencias (MongoDB u otro)
* Servicio de notificaciones (email, Slack, etc.)
* Documentación de reglas de negocio

---

### 7. Consideraciones Técnicas

* Implementar validaciones mediante un motor de reglas configurable (rule engine o strategy pattern)
* Separar claramente validación, corrección y notificación (principio de responsabilidad única)
* Uso de logs estructurados (JSON) para facilitar auditoría y observabilidad
* Definir niveles de severidad de inconsistencias (warning vs error crítico)
* Evitar correcciones agresivas que puedan alterar semántica del dato
* Diseñar la solución como una capa transversal reutilizable en toda la arquitectura (enfoque tipo middleware o pipeline)
