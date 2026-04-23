## FT-010: Configuración y Gestión de Parámetros de Cálculo

**Épica Padre**: EP-002 — Motor de Cálculo y Reglas de Negocio
**Capa**: core

### 1. Descripción

Esta feature gestiona la ingestión, almacenamiento y disponibilidad de parámetros técnicos (tarifas, factores, catálogos) necesarios para el cálculo de primas, asegurando que el motor de cálculo opere con información actualizada y consistente.

---

### 2. Objetivo de Negocio

Garantizar que los cálculos de primas se realicen con datos técnicos correctos y vigentes, permitiendo precisión en precios y adaptabilidad ante cambios en tarifas o factores.

---

### 3. Alcance Funcional

Incluye:

* Ingestión de tarifas (incendio, CAT, FHM, equipo electrónico)
* Ingestión de catálogos de códigos postales y zonas
* Mapeo de datos técnicos (CP → zona)
* Almacenamiento de parámetros de cálculo
* Exposición de parámetros a motores internos

No incluye:

* Definición de tarifas (solo consumo)
* Edición manual de parámetros desde UI

---

### 4. Historias de Usuario

| HU     | Nombre                    | Descripción corta        |
| ------ | ------------------------- | ------------------------ |
| HU-044 | Tarifas incendio          | Ingestión desde servicio |
| HU-045 | Tarifas CAT               | Factores por zona        |
| HU-046 | Tarifas FHM               | Equipo electrónico       |
| HU-047 | Catálogo CP-Zonas         | Mapeo geográfico         |
| HU-048 | Disponibilidad parámetros | Acceso para motores      |

---

### 5. Flujo Funcional

1. Sistema solicita parámetros a servicio externo
2. Se ingesta y transforma la información
3. Se almacena en repositorio interno
4. Motores (validación/cálculo) consultan parámetros
5. Se aplican en ejecución de reglas y cálculos
6. Se refrescan periódicamente o bajo demanda

---

### 6. Dependencias Técnicas

* `Plataforma-core-ohs` (fuente de datos)
* Adaptadores de integración (arquitectura hexagonal)
* Repositorio de parámetros/catálogos
* Motor de cálculo (FT-004)
* Motor de reglas (FT-009)

---

### 7. Consideraciones Técnicas

* Implementar cache para reducir latencia
* Manejo de vigencias de tarifas
* Estrategias de fallback ante fallos del servicio externo
* Validación y saneamiento de datos ingeridos
* Estructuras optimizadas para consultas frecuentes (lookup rápido)
* Separación clara entre ingestión, almacenamiento y consumo (principio SRP)
