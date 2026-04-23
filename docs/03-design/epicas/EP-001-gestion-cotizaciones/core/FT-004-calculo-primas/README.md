## FT-004: Ejecución y Persistencia del Cálculo de Primas

**Épica Padre**: EP-001 — Gestión Integral de Cotizaciones de Daños
**Capa**: core

### 1. Descripción

Esta feature orquesta la ejecución del cálculo de primas para una cotización, incluyendo el procesamiento parcial de ubicaciones válidas, la aplicación de factores técnicos y reglas de negocio, así como la persistencia de los resultados obtenidos.

---

### 2. Objetivo de Negocio

Permitir al usuario obtener resultados financieros precisos de una cotización, asegurando que el cálculo sea resiliente, parcial (cuando aplique), consistente y persistente para su posterior consulta.

---

### 3. Alcance Funcional

Incluye:

* Inicio del proceso de cálculo de primas
* Procesamiento parcial de ubicaciones válidas
* Cálculo de prima neta y comercial total
* Cálculo desglosado por ubicación
* Aplicación de factores técnicos y reglas de negocio
* Persistencia de resultados del cálculo

No incluye:

* Definición de reglas de negocio (FT-009)
* Gestión de parámetros técnicos (FT-007 / FT-010)
* Validaciones estructurales previas (FT-002, FT-003)

---

### 4. Historias de Usuario

| HU     | Nombre              | Descripción corta                  |
| ------ | ------------------- | ---------------------------------- |
| HU-015 | Iniciar cálculo     | Ejecuta el proceso de cálculo      |
| HU-016 | Prima total         | Calcula prima neta y comercial     |
| HU-017 | Prima por ubicación | Desglose por ubicación             |
| HU-018 | Persistencia        | Guarda resultados del cálculo      |
| HU-019 | Factores y reglas   | Aplica lógica técnica y de negocio |

---

### 5. Flujo Funcional

1. Usuario inicia el cálculo de prima (HU-015)
2. Sistema valida ubicaciones calculables
3. Se excluyen ubicaciones inválidas y se notifican
4. Motor calcula prima por ubicación (HU-017)
5. Se aplican factores técnicos y reglas de negocio (HU-019)
6. Se consolida prima total (HU-016)
7. Se devuelven resultados al frontend
8. Se persisten resultados en la cotización (HU-018)

---

### 6. Dependencias Técnicas

* FT-001: Datos generales de cotización

* FT-002: Gestión de ubicaciones

* FT-003: Configuración de coberturas

* FT-007: Servicios de tarifas

* FT-008: Persistencia y versionado

* FT-009: Validaciones de negocio

* Motor de cálculo de primas

* Motor de reglas de negocio

* Repositorio de cotizaciones

* API REST (`POST /v1/quotes/{folio}/calculate`)

---

### 7. Consideraciones Técnicas

* El cálculo debe soportar **procesamiento parcial**, excluyendo únicamente ubicaciones inválidas
* El sistema debe distinguir entre:

  * ❌ Ninguna ubicación válida → no calcular
  * ⚠️ Algunas inválidas → calcular parcialmente
* El cálculo debe ser **idempotente** para evitar inconsistencias en reintentos
* Separación clara entre:

  * Orquestador de cálculo
  * Motor de cálculo (lógica pura)
  * Motor de reglas
* Uso de **Value Objects** para representar resultados monetarios
* Precisión decimal controlada (BigDecimal, escala fija)
* Persistencia debe ser **transaccional**
* Resultados deben poder ser:

  * Sobrescritos (último cálculo)
  * Versionados (si se requiere auditoría)
* Se debe incluir trazabilidad de:

  * Factores aplicados
  * Reglas ejecutadas
* Manejo de estados:

  * CALCULANDO
  * CALCULADO
  * ERROR
* UI debe reflejar:

  * Indicador de carga
  * Alertas por exclusión de ubicaciones
* Preparado para escalabilidad (posible desacople futuro a procesamiento async)

---
