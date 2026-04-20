## FT-016: Integración de Catálogo de Códigos Postales y Zonas

### HU-182: Consultar Códigos Postales y Zonas desde Servicio Externo
**Descripción**:
Como sistema,
Quiero poder consultar códigos postales y obtener su información de zona (CAT, nivel técnico) desde `Plataforma-core-ohs` (o su mock),
Para realizar validaciones de dirección y obtener factores de riesgo.

**Criterios de Aceptación**:
- Dado que se requiere la información de un código postal, cuando el sistema lo consulta, entonces obtiene los datos de zona asociados a ese CP del servicio externo.
- Dado que el código postal no existe o es inválido en el servicio externo, cuando se consulta, entonces el servicio devuelve una respuesta indicando la ausencia o invalidez.
- Dado que la consulta es exitosa, cuando se completa, entonces la información de zona está disponible para el cotizador.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Cliente API REST para `Plataforma-core-ohs`), Módulo de Consulta de CP.

**Notas de Implementación**: La consulta debe ser eficiente para volúmenes altos.

**Estado**: Backlog

---
### HU-183: Validar Códigos Postales y Notificar al Usuario
**Descripción**:
Como usuario,
Quiero que la validación de códigos postales se realice correctamente, informándome si un CP es inválido o no encontrado,
Para corregir la dirección y asegurar la precisión de la ubicación de riesgo.

**Criterios de Aceptación**:
- Dado que ingreso un código postal, cuando se valida contra el servicio externo, entonces el sistema verifica su validez.
- Dado que el código postal es inválido o no encontrado, cuando se valida, entonces la interfaz muestra un mensaje de error claro al usuario.
- Dado que el código postal es válido, cuando se valida, entonces no se muestra ningún error y la información de zona se carga.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-182 (Consultar Códigos Postales y Zonas)

**Componentes Técnicos**: Frontend (Campo de Código Postal, Mensajes de Error), Backend (Servicio de Validación de CP).

**Notas de Implementación**: La validación puede ser en tiempo real o al guardar.

**Estado**: Backlog

---
### HU-184: Mapear Información de Zonas para Cálculo de Primas
**Descripción**:
Como sistema,
Quiero que la información de zonas (CAT, nivel técnico) obtenida del servicio de códigos postales se mapee y esté disponible para la lógica de cálculo de primas por ubicación,
Para aplicar correctamente los factores de riesgo.

**Criterios de Aceptación**:
- Dado que la información de zona se recibe del servicio externo, cuando se procesa, entonces se mapea a la estructura de datos interna utilizada por el motor de cálculo.
- Dado que el mapeo es exitoso, cuando se completa, entonces el motor de cálculo puede acceder a los valores de zona para aplicar los factores CAT y FHM.
- Dado que la información de zona es inconsistente o incompleta, cuando se mapea, entonces el sistema lo detecta y registra el problema.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-182 (Consultar Códigos Postales y Zonas)

**Componentes Técnicos**: Backend (Módulo de Mapeo de Zonas, Motor Central de Cálculo de Primas).

**Notas de Implementación**: Asegurar que el formato de los datos de zona sea compatible con las fórmulas de cálculo.

**Estado**: Backlog

---
