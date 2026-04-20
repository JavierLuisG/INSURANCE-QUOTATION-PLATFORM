## FT-008: Gestión de Persistencia Avanzada y Versionado Optimista

### HU-141: Consumir Datos de Códigos Postales y Zonas de Riesgo
**Descripción**:
Como sistema,
Quiero consultar y validar información de códigos postales y sus zonas de riesgo desde `Plataforma-core-ohs` (o su simulación),
Para asegurar la correcta evaluación del riesgo por ubicación.

**Criterios de Aceptación**:
- Dado que el sistema requiere validar un código postal, cuando realiza la consulta, entonces obtiene la información de zona (CAT, nivel técnico) asociada al CP.
- Dado que el código postal es inválido o no encontrado en el catálogo, cuando se realiza la consulta, entonces el servicio devuelve una indicación de error.
- Dado que los datos de zona se reciben, cuando se procesan, entonces están disponibles para la lógica de cálculo de primas.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Clientes API REST para `Plataforma-core-ohs`), Servicio de Validación de CP.
**Notas de Implementación**: Optimizar la consulta de CP para grandes volúmenes.

**Estado**: Backlog

---
### HU-142: Consumir Catálogos de Clasificación de Riesgo y Garantías
**Descripción**:
Como sistema,
Quiero obtener los catálogos de clasificación de riesgo y garantías desde `Plataforma-core-ohs` (o su simulación),
Para permitir la configuración detallada de coberturas y la evaluación de riesgo.

**Criterios de Aceptación**:
- Dado que el sistema requiere los catálogos, cuando realiza la consulta, entonces obtiene los datos de clasificación de riesgo y garantías del servicio externo.
- Dado que los datos son recibidos, cuando se procesan, entonces se mapean correctamente al modelo interno del cotizador.
- Dado que los catálogos se actualizan en el sistema de origen, cuando se consultan, entonces los cambios se reflejan en el cotizador.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Clientes API REST para `Plataforma-core-ohs`), Capa de Integración.

**Notas de Implementación**: Estos catálogos suelen ser menos voluminosos y de actualización menos frecuente.

**Estado**: Backlog

---
### HU-143: Consumir Tarifas y Factores Técnicos
**Descripción**:
Como sistema,
Quiero consultar las tarifas (incendio, CAT, FHM) y factores técnicos (equipo electrónico) desde `Plataforma-core-ohs` (o su simulación),
Para utilizarlos en el cálculo preciso de las primas.

**Criterios de Aceptación**:
- Dado que el sistema requiere tarifas o factores, cuando realiza la consulta, entonces obtiene los datos necesarios del servicio externo.
- Dado que los datos son recibidos, cuando se procesan, entonces se mapean correctamente para ser utilizados en la lógica de cálculo.
- Dado que el servicio externo no responde o devuelve datos inconsistentes, cuando se realiza la consulta, entonces el sistema maneja el error y notifica.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Clientes API REST para `Plataforma-core-ohs`), Módulo de Adaptación de Tarifas.

**Notas de Implementación**: La estructura de tarifas y factores puede ser compleja y requiere mapeo cuidadoso.

**Estado**: Backlog

---
### HU-144: Manejo de Errores de Comunicación con Servicio Externo
**Descripción**:
Como sistema,
Quiero manejar posibles errores de comunicación con el servicio `Plataforma-core-ohs`,
Para asegurar la robustez de la integración y evitar fallos en el cotizador.

**Criterios de Aceptación**:
- Dado que `Plataforma-core-ohs` no está disponible, cuando el sistema intenta consultarlo, entonces se registra un error y se notifica al usuario o sistema.
- Dado que la respuesta de `Plataforma-core-ohs` es inconsistente, cuando el sistema la recibe, entonces se maneja la inconsistencia y se registra.
- Dado que hay un error de comunicación, cuando se intenta una operación dependiente, entonces el sistema puede reintentar la operación o usar datos en caché (si aplica).

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-140, HU-141, HU-142, HU-143

**Componentes Técnicos**: Capa de Integración Backend (Manejo de Excepciones, Circuit Breakers, Reintentos).

**Notas de Implementación**: Implementar patrones de resiliencia como Circuit Breaker y Retry.

**Estado**: Backlog

---
