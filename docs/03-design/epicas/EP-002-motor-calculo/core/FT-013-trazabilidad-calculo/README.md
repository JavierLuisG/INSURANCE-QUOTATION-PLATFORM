## FT-013: Persistencia y Trazabilidad de Resultados de Cálculo

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: core

### 1. Descripción

Esta feature se encarga de almacenar de forma persistente los resultados del cálculo de primas dentro de la cotización, garantizando consistencia, atomicidad y trazabilidad de los datos financieros generados.

---

### 2. Objetivo de Negocio

Asegurar que los resultados del cálculo de primas no se pierdan, sean consultables en cualquier momento y permitan auditoría completa sobre cómo se obtuvo cada valor.

---

### 3. Alcance Funcional

Incluye:

* Persistencia de prima neta y comercial
* Almacenamiento de desglose por ubicación
* Garantía de atomicidad en la operación de guardado
* Actualización de metadatos (versión y fecha)
* Registro de parámetros utilizados en el cálculo

No incluye:

* Ejecución del cálculo (FT-012)
* Visualización de resultados (FT-005)
* Validaciones previas al cálculo (FT-011)

---

### 4. Historias de Usuario

| HU     | Nombre                     | Descripción corta           |
| ------ | -------------------------- | --------------------------- |
| HU-060 | Persistencia de resultados | Guarda primas y desglose    |
| HU-061 | Atomicidad                 | Consistencia all-or-nothing |
| HU-062 | Metadatos                  | Versión + fecha             |
| HU-063 | Trazabilidad               | Registro de parámetros      |

---

### 5. Flujo Funcional

1. Se reciben resultados del motor de cálculo (FT-012)
2. Se construye el modelo de persistencia
3. Se ejecuta guardado atómico (HU-061)
4. Se actualizan metadatos (HU-062)
5. Se registran parámetros de cálculo (HU-063)
6. Se confirma persistencia o se revierte en caso de error

---

### 6. Dependencias Técnicas

* FT-012: Motor de cálculo
* FT-010: Parámetros y tarifas
* Repositorio de cotizaciones (MongoDB)
* Estrategia de versionado optimista

---

### 7. Consideraciones Técnicas

* Uso de operaciones atómicas o transacciones en MongoDB
* Diseño eficiente del documento (evitar sobrecarga por trazabilidad)
* Versionado optimista para control de concurrencia
* Persistencia desacoplada del motor de cálculo
* Posible uso de snapshots de parámetros o versionado de catálogos
* Manejo robusto de errores y rollback

---