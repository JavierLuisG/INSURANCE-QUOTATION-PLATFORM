## FT-006: Gestión del Ciclo de Vida y Estados de la Cotización

---

### 1. Descripción

Esta feature define los estados de la cotización y las reglas de transición entre ellos, asegurando que el flujo de creación, cálculo, aprobación y emisión sea controlado y consistente.

---

### 2. Objetivo de Negocio

Garantizar que una cotización avance correctamente en su ciclo de vida, evitando acciones inválidas (como aprobar sin calcular) y asegurando que la información financiera siempre sea confiable.

---

### 3. Alcance Funcional

Incluye:

* Inicialización en estado **Borrador**
* Cambio automático a **Calculada**
* Transiciones manuales a:

  * Aprobada
  * Rechazada
  * Emitida
* Validaciones para permitir o bloquear acciones
* Invalidación del cálculo al modificar datos
* Visualización del estado actual
* Flujo de términos y condiciones previo a aprobación

No incluye:

* Cálculo de primas (FT-004)
* Validaciones de datos específicas (FT-009)
* Gestión de concurrencia (FT-014)

---

### 4. Historias de Usuario

| HU     | Nombre                   | Descripción corta                       |
| ------ | ------------------------ | --------------------------------------- |
| HU-135 | Estado inicial           | Cotización inicia en borrador           |
| HU-136 | Estado calculada         | Cambio automático post-cálculo          |
| HU-137 | Aprobar/Rechazar         | Cambio manual desde calculada           |
| HU-138 | Validación de cálculo    | Impide calcular sin ubicaciones válidas |
| HU-139 | Validación de aprobación | Impide aprobar sin cálculo              |
| HU-140 | Invalidar cálculo        | Cambios regresan a borrador             |
| HU-141 | Emitir cotización        | Estado final emitida                    |
| HU-142 | Visualizar estado        | Mostrar estado actual                   |
| HU-143 | Términos y condiciones   | Flujo previo a aprobación               |

---

### 5. Flujo Funcional

1. Usuario crea cotización → estado **Borrador**
2. Usuario captura información
3. Usuario intenta calcular:

   * Si no hay ubicaciones válidas → se bloquea
   * Si hay → se ejecuta cálculo
4. Cálculo exitoso → estado **Calculada**
5. Usuario puede:

   * Aprobar → **Aprobada**
   * Rechazar → **Rechazada**
6. Desde aprobada → puede pasar a **Emitida**
7. Si se modifica la cotización:

   * Regresa a **Borrador**
   * Se invalida el cálculo

---

### 6. Dependencias Técnicas

* API de cotizaciones (estado y cálculo)
* Motor de cálculo de primas
* Validaciones de ubicaciones y coberturas
* Frontend:

  * Botones de acción (calcular, aprobar, emitir)
  * Indicador de estado
  * Ruta `/terms-and-conditions`

---

### 7. Consideraciones Técnicas

* Estados controlados mediante enum o máquina de estados
* Validación estricta de transiciones en backend
* Invalidación automática del cálculo ante cambios
* Estado **Emitida** como terminal (sin modificaciones)
* Mensajes claros cuando una acción no es permitida
* Posible uso de flag para aceptación de términos
* Integración con versionado optimista para evitar inconsistencias

---