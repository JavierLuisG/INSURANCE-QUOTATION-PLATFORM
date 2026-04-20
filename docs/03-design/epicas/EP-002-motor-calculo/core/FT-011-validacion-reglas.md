## FT-011: Motor de Validación de Reglas de Negocio

### HU-049: Validación de Rangos de Suma Asegurada
**Descripción**:
Como usuario,
Quiero que el sistema valide que las sumas aseguradas de cada ubicación estén dentro de los rangos predefinidos,
Para evitar errores en la cotización y asegurar que los montos sean coherentes con las políticas de suscripción.

**Criterios de Aceptación**:
- Dado que ingreso una suma asegurada dentro del rango permitido, cuando se ejecuta la validación, entonces la suma asegurada se considera válida.
- Dado que ingreso una suma asegurada por debajo del mínimo, cuando se ejecuta la validación, entonces el sistema reporta un error específico para esa suma asegurada.
- Dado que ingreso una suma asegurada por encima del máximo, cuando se ejecuta la validación, entonces el sistema reporta un error específico para esa suma asegurada.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-010 (para obtener rangos de validación si son dinámicos)

**Componentes Técnicos**:
- Módulo de reglas de validación
- Componente de reporte de errores

**Notas de Implementación**:
Los rangos deben ser configurables y posiblemente depender de otros factores como el tipo de riesgo o la zona.

**Estado**: Backlog

---
### HU-050: Validación de Código Postal y Zona
**Descripción**:
Como usuario,
Quiero que el sistema valide los códigos postales de las ubicaciones contra el `catalogo_cp_zonas`,
Para asegurar la correcta clasificación geográfica del riesgo y la aplicación de factores específicos.

**Criterios de Aceptación**:
- Dado que ingreso un código postal existente en el `catalogo_cp_zonas`, cuando se ejecuta la validación, entonces el código postal se considera válido y se asigna la zona correspondiente.
- Dado que ingreso un código postal no existente en el `catalogo_cp_zonas`, cuando se ejecuta la validación, entonces el sistema reporta un error indicando un código postal inválido.
- Dado que el `catalogo_cp_zonas` no está disponible, cuando se intenta validar un código postal, entonces el sistema maneja la situación (e.g., error de sistema o validación predeterminada).

**Prioridad**: Alta
**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-047 (para la disponibilidad del `catalogo_cp_zonas`)

**Componentes Técnicos**:
- Módulo de reglas de validación
- Servicio de consulta de catálogos (CP)
- Componente de reporte de errores

**Notas de Implementación**:
La validación debe ser performante, especialmente si hay muchas ubicaciones.

**Estado**: Backlog

---
### HU-051: Verificación de Datos Mínimos por Ubicación
**Descripción**:
Como usuario,
Quiero que el sistema verifique que todas las ubicaciones tengan los datos mínimos requeridos para el cálculo de prima,
Para garantizar que el cálculo pueda proceder correctamente y evitar resultados incompletos.

**Criterios de Aceptación**:
- Dado que todos los campos obligatorios de una ubicación están completos, cuando se ejecuta la validación, entonces la ubicación se considera completa.
- Dado que falta un campo obligatorio en una ubicación, cuando se ejecuta la validación, entonces el sistema reporta un error específico para el campo faltante.
- Dado que se intenta calcular con una ubicación incompleta, cuando se ejecuta la validación, entonces el cálculo es bloqueado y se muestran los errores.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- Ninguna específica de otra HU, pero depende del modelo de datos de la cotización.

**Componentes Técnicos**:
- Módulo de reglas de validación
- Componente de reporte de errores

**Notas de Implementación**:
La definición de "datos mínimos" debe ser clara y estar documentada en el modelo de dominio.

**Estado**: Backlog

---
### HU-052: Generación de Mensajes de Error Claros
**Descripción**:
Como usuario,
Quiero que el sistema me muestre mensajes de error claros y específicos cuando una validación falle,
Para entender rápidamente qué debo corregir y cómo proceder.

**Criterios de Aceptación**:
- Dado que una validación falla (e.g., suma asegurada fuera de rango), cuando se muestra el error, entonces el mensaje es descriptivo e indica la acción correctiva.
- Dado que múltiples validaciones fallan en una misma ubicación, cuando se muestran los errores, entonces cada error es listado individualmente y es comprensible.
- Dado que un mensaje de error se genera, cuando el usuario lo ve, entonces puede identificar fácilmente el campo o la regla que causó el problema.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-049, HU-050, HU-051

**Componentes Técnicos**:
- Componente de reporte de errores
- Módulo de mensajes de usuario

**Notas de Implementación**:
Los mensajes deben ser localizables y consistentes en su tono y formato.

**Estado**: Backlog---
### HU-053: Bloqueo de Cálculo por Errores de Validación
**Descripción**:
Como usuario,
Quiero que el sistema impida el cálculo de la prima si existen errores de validación activos en la cotización o sus ubicaciones,
Para asegurar que solo se realicen cálculos con datos válidos y completos.

**Criterios de Aceptación**:
- Dado que existen errores de validación en la cotización o en alguna de sus ubicaciones, cuando se intenta ejecutar el cálculo, entonces el cálculo es abortado.
- Dado que el cálculo es abortado por errores de validación, cuando el usuario es notificado, entonces se le redirige o se le indica que resuelva los errores primero.
- Dado que no hay errores de validación activos, cuando se intenta ejecutar el cálculo, entonces el cálculo puede proceder normalmente.

**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**:
- HU-049, HU-050, HU-051, HU-052

**Componentes Técnicos**:
- Módulo de orquestación de cálculo
- Componente de control de flujo

**Notas de Implementación**:
La lógica de bloqueo debe ser una pre-condición estricta para la ejecución del Motor Central de Cálculo.

**Estado**: Backlog

---
