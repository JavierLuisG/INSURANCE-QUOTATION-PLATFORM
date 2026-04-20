## FT-017: Integración de Catálogos de Clasificación de Riesgo y Garantías

### HU-077: Recuperar Catálogo de Clasificación de Riesgo

**Descripción**:
Como sistema,
Quiero recuperar el catálogo de clasificación de riesgo desde el servicio `Plataforma-core-ohs` (o su mock),
Para ofrecer opciones actualizadas en la definición de coberturas de la cotización.

**Criterios de Aceptación**:
- Dado que el usuario está configurando coberturas, cuando el sistema necesita el catálogo de riesgo, entonces lo consulta al servicio externo y lo muestra.
- Dado que el catálogo de riesgo se carga exitosamente, cuando el usuario selecciona una clasificación, entonces esta se asocia correctamente a la ubicación o cobertura.
- Dado que la recuperación del catálogo de riesgo falla, cuando el sistema lo intenta, entonces se registra el error y se muestra un mensaje adecuado al usuario.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068 (Conectividad básica)
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Capa de mapeo de datos
- Repositorio de catálogos

**Notas de Implementación**:
El catálogo puede influir en las primas y condiciones de suscripción.

**Estado**: Backlog

---
### HU-078: Recuperar Catálogo de Garantías

**Descripción**:
Como sistema,
Quiero recuperar el catálogo de garantías desde el servicio `Plataforma-core-ohs` (o su mock),
Para ofrecer opciones actualizadas y completas en la configuración de coberturas de la cotización.

**Criterios de Aceptación**:
- Dado que el usuario está definiendo las coberturas, cuando el sistema necesita el catálogo de garantías, entonces lo consulta y lo presenta en la interfaz de usuario.
- Dado que el catálogo de garantías se carga, cuando el usuario selecciona una o varias garantías, entonces estas se asocian a la cobertura.
- Dado que el servicio devuelve un catálogo de garantías vacío, cuando el sistema lo procesa, entonces no se muestran opciones de garantía al usuario.

**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-068 (Conectividad básica)
- FT-020 (Simulación de Servicio Plataforma-core-ohs)

**Componentes Técnicos**:
- Cliente API REST
- Capa de mapeo de datos
- Repositorio de catálogos

**Notas de Implementación**:
Las garantías pueden tener reglas de negocio complejas para su combinación o exclusión.

**Estado**: Backlog

---
### HU-079: Mapear Datos de Clasificación de Riesgo y Garantías

**Descripción**:
Como sistema,
Quiero mapear los datos de clasificación de riesgo y garantías del servicio externo a mi modelo interno,
Para su correcta utilización en la lógica de negocio y la interfaz de usuario.

**Criterios de Aceptación**:
- Dado que se reciben datos de clasificación de riesgo, cuando el sistema los mapea, entonces se transforman al formato del modelo de datos interno.
- Dado que se reciben datos de garantías, cuando el sistema los mapea, entonces se transforman al formato del modelo de datos interno, incluyendo atributos como ID, nombre y descripción.
- Dado que el mapeo es exitoso, cuando los datos se utilizan, entonces los valores son consistentes con las expectativas de la lógica de negocio.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-077, HU-078

**Componentes Técnicos**:
- Capa de mapeo de datos
- Modelos de datos internos

**Notas de Implementación**:
Asegurar que los identificadores de riesgo y garantía sean únicos y estables.

**Estado**: Backlog

---
