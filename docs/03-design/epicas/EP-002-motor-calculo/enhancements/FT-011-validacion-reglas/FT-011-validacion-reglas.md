## FT-011: Motor de Validación de Reglas de Negocio

### HU-161: Validar Rangos de Sumas Aseguradas
**Descripción**:
Como sistema,
Quiero validar que las sumas aseguradas de las coberturas estén dentro de los rangos predefinidos,
Para asegurar que los valores son coherentes con las políticas de suscripción.

**Criterios de Aceptación**:
- Dado que el usuario ingresa una suma asegurada, cuando se valida, entonces se comprueba que está entre el mínimo y el máximo permitido.
- Dado que la suma asegurada está fuera de rango, cuando se valida, entonces el sistema genera un error de validación claro.
- Dado que la suma asegurada está dentro de rango, cuando se valida, entonces no se genera ningún error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-121 (Configurar Parámetros Específicos de Cobertura), HU-160 (Parámetros Disponibles para Motores)

**Componentes Técnicos**: Backend (Módulo de Reglas de Validación).

**Notas de Implementación**: Los rangos deben ser configurables y accesibles desde los parámetros de cálculo.

**Estado**: Backlog

---
### HU-162: Validar Códigos Postales Contra Catálogo
**Descripción**:
Como sistema,
Quiero validar los códigos postales de las ubicaciones contra el `catalogo_cp_zonas` provisto,
Para asegurar que solo se usan códigos postales válidos y asociados a zonas de riesgo.

**Criterios de Aceptación**:
- Dado que una ubicación tiene un código postal, cuando se valida, entonces se verifica su existencia en el `catalogo_cp_zonas`.
- Dado que el código postal no se encuentra en el catálogo, cuando se valida, entonces el sistema genera un error de validación.
- Dado que el código postal es válido y existe en el catálogo, cuando se valida, entonces no se genera ningún error.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-117 (Validar Código Postal de Ubicación), HU-159 (Consumir Catálogo de Códigos Postales y Zonas)

**Componentes Técnicos**: Backend (Módulo de Reglas de Validación, Servicio de Consulta de Catálogos).

**Notas de Implementación**: La validación debe ser eficiente, especialmente con grandes catálogos de CP.

**Estado**: Backlog

---
### HU-163: Verificar Datos Mínimos Requeridos por Ubicación
**Descripción**:
Como sistema,
Quiero verificar que todas las ubicaciones tengan los datos mínimos requeridos para el cálculo,
Para asegurar que el motor de cálculo recibe información completa.

**Criterios de Aceptación**:
- Dado que una ubicación es procesada para cálculo, cuando se valida, entonces se comprueba que todos los campos obligatorios están completos.
- Dado que faltan datos mínimos en una ubicación, cuando se valida, entonces el sistema genera un error de validación específico para esa ubicación.
- Dado que todos los datos mínimos están presentes, cuando se valida, entonces no se genera ningún error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-114 (Editar Detalles de Ubicación de Riesgo)

**Componentes Técnicos**: Backend (Módulo de Reglas de Validación).

**Notas de Implementación**: La definición de "datos mínimos" debe ser clara y documentada.

**Estado**: Backlog

---
### HU-164: Proporcionar Mensajes de Error Específicos de Validación
**Descripción**:
Como sistema,
Quiero que el motor de validación proporcione mensajes de error claros y específicos para cada regla incumplida,
Para facilitar la identificación y corrección de los problemas por parte del usuario.

**Criterios de Aceptación**:
- Dado que una regla de validación falla, cuando se reporta el error, entonces el mensaje identifica la regla específica y el campo afectado.
- Dado que múltiples reglas fallan, cuando se reportan los errores, entonces se agrupan o listan de forma comprensible.
- Dado que un error es de alta prioridad, cuando se reporta, entonces se destaca visualmente en la interfaz de usuario.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-161, HU-162, HU-163

**Componentes Técnicos**: Backend (Componente de Reporte de Errores), Frontend (Interfaz de Mensajes de Error).

**Notas de Implementación**: Los mensajes deben ser orientados al usuario final.

**Estado**: Backlog

---
### HU-165: Prevenir Ejecución de Cálculo con Errores de Validación
**Descripción**:
Como sistema,
Quiero que el cálculo de prima no se ejecute si existen errores de validación activos en la cotización o sus ubicaciones,
Para evitar cálculos incorrectos y reprocesos.

**Criterios de Aceptación**:
- Dado que la cotización o alguna de sus ubicaciones tiene errores de validación, cuando se intenta iniciar el cálculo, entonces el sistema lo impide y notifica los errores.
- Dado que todos los errores de validación han sido resueltos, cuando se intenta iniciar el cálculo, entonces el sistema permite la ejecución.
- Dado que el cálculo es impedido, cuando se notifica, entonces se indica claramente que la causa son los errores de validación pendientes.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-123 (Iniciar Proceso de Cálculo de Prima), HU-164 (Proporcionar Mensajes de Error Específicos de Validación)

**Componentes Técnicos**: Backend (Motor de Validación de Reglas de Negocio, Endpoint de Cálculo).

**Notas de Implementación**: El motor de validación debe ser invocado antes del motor de cálculo.

**Estado**: Backlog

---
