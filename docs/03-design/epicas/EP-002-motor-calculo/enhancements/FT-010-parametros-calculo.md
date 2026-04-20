## FT-010: Configuración y Gestión de Parámetros de Cálculo

### HU-150: Validar Datos Generales de la Cotización
**Descripción**:
Como sistema,
Quiero implementar reglas de validación para los datos generales de la cotización (ej., formato RFC, rangos de vigencia),
Para asegurar la consistencia y corrección de la información inicial.

**Criterios de Aceptación**:
- Dado que el usuario ingresa un RFC, cuando se valida, entonces se comprueba que cumple con el formato requerido.
- Dado que el usuario selecciona una vigencia, cuando se valida, entonces se comprueba que las fechas de inicio y fin son coherentes y dentro de rangos válidos.
- Dado que un campo obligatorio está vacío, cuando se guarda la cotización, entonces el sistema muestra un mensaje de error claro y no permite guardar.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-111 (Editar Datos Generales de la Cotización)

**Componentes Técnicos**: Backend (Módulo de Validación de Datos Generales).

**Notas de Implementación**: Las validaciones deben ejecutarse tanto en frontend como en backend.

**Estado**: Backlog

---
### HU-151: Validar Datos Específicos de Ubicación de Riesgo
**Descripción**:
Como sistema,
Quiero implementar reglas de validación para los datos específicos de cada ubicación de riesgo (ej., valor del bien, año de construcción),
Para asegurar la integridad de la información utilizada en la evaluación del riesgo.

**Criterios de Aceptación**:
- Dado que el usuario ingresa el valor de un bien, cuando se valida, entonces se comprueba que está dentro de los rangos permitidos.
- Dado que el usuario ingresa el año de construcción, cuando se valida, entonces se comprueba que es un año válido y consistente.
- Dado que un campo obligatorio de una ubicación está vacío, cuando se guarda la ubicación, entonces el sistema muestra un mensaje de error claro.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-114 (Editar Detalles de Ubicación de Riesgo)

**Componentes Técnicos**: Backend (Módulo de Validación de Datos de Ubicación).

**Notas de Implementación**: Las reglas de validación deben ser configurables y extensibles.

**Estado**: Backlog

---
### HU-152: Aplicar Lógica de Negocio en Cálculo de Primas
**Descripción**:
Como sistema,
Quiero que la lógica de cálculo de primas incorpore las reglas de negocio y factores técnicos definidos (ej., aplicación de recargos, descuentos),
Para asegurar que la prima final sea correcta y consistente con las políticas de suscripción.

**Criterios de Aceptación**:
- Dado que se cumplen ciertas condiciones (ej. tipo de cliente, canal de venta), cuando se calcula la prima, entonces se aplican los recargos o descuentos correspondientes.
- Dado que se utilizan factores técnicos, cuando se aplican en el cálculo, entonces se hace según las especificaciones de las tarifas.
- Dado que una regla de negocio impacta el cálculo, cuando se ejecuta, entonces el resultado final de la prima lo refleja.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**: HU-127 (Aplicar Reglas de Negocio y Factores Técnicos en Cálculo)

**Componentes Técnicos**: Backend (Motor Central de Cálculo de Primas, Módulo de Reglas de Negocio).

**Notas de Implementación**: La lógica de negocio debe ser trazable y documentada.

**Estado**: Backlog

---
### HU-153: Proporcionar Mensajes de Error Claros
**Descripción**:
Como sistema,
Quiero proporcionar mensajes de error claros y útiles cuando las validaciones fallan,
Para guiar al usuario en la corrección de los datos.

**Criterios de Aceptación**:
- Dado que una validación falla, cuando se muestra el error, entonces el mensaje indica claramente qué campo es erróneo y por qué.
- Dado que múltiples validaciones fallan, cuando se muestran los errores, entonces se presentan de forma organizada y fácil de entender.
- Dado que un error es crítico, cuando se muestra, entonces se destaca para la acción inmediata del usuario.

**Prioridad**: Alta

**Estimación**: 2 puntos de historia

**Dependencias**: HU-150 (Validar Datos Generales), HU-151 (Validar Datos Específicos de Ubicación)

**Componentes Técnicos**: Frontend (Sistema de Notificaciones/Validaciones de UI), Backend (Servicio de Mensajes de Error).

**Notas de Implementación**: Los mensajes deben ser amigables y orientados a la solución.

**Estado**: Backlog

---
### HU-154: Asegurar Trazabilidad de Reglas de Negocio
**Descripción**:
Como sistema,
Quiero que las reglas de negocio implementadas sean trazables y documentadas,
Para facilitar la auditoría, mantenimiento y comprensión de la lógica del sistema.

**Criterios de Aceptación**:
- Dado que se implementa una regla de negocio, cuando se realiza, entonces existe documentación que describe su propósito, condiciones y efectos.
- Dado que se necesita auditar un cálculo, cuando se consulta, entonces es posible identificar qué reglas de negocio fueron aplicadas y con qué parámetros.
- Dado que se requiere modificar una regla, cuando se busca, entonces su implementación es fácilmente identificable en el código.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-152 (Aplicar Lógica de Negocio en Cálculo de Primas)

**Componentes Técnicos**: Backend (Módulo de Reglas de Negocio, Herramientas de Documentación).

**Notas de Implementación**: Considerar el uso de un motor de reglas o un enfoque basado en especificaciones.

**Estado**: Backlog

---
### HU-155: Consumir Tarifas de Incendio
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `tarifas_incendio` del servicio `Plataforma-core-ohs`,
Para obtener las tasas base necesarias para el cálculo de primas.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere tarifas de incendio, cuando las consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que las tarifas se reciben, cuando se procesan, entonces se mapean correctamente y están disponibles para el cálculo.
- Dado que el servicio de tarifas no está disponible, cuando se realiza la consulta, entonces el sistema maneja la excepción y registra el error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.
**Notas de Implementación**: La simulación debe ser fiel a los contratos de la API real.

**Estado**: Backlog

---
### HU-156: Consumir Tarifas de Catástrofe (CAT)
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `tarifas_cat` del servicio `Plataforma-core-ohs`,
Para aplicar los factores de catástrofe según la zona de riesgo.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere tarifas CAT, cuando las consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que las tarifas se reciben, cuando se procesan, entonces se mapean correctamente y están disponibles para el cálculo.
- Dado que se consulta una zona de riesgo, cuando se aplican las tarifas CAT, entonces se utiliza el factor correcto para esa zona.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.

**Notas de Implementación**: Se debe considerar cómo se relaciona la zona con la tarifa CAT.

**Estado**: Backlog

---
### HU-157: Consumir Tarifa FHM
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `tarifa_fhm` del servicio `Plataforma-core-ohs`,
Para aplicar las cuotas de Fenómenos Hidrometeorológicos (FHM) en el cálculo.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere tarifa FHM, cuando la consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que la tarifa se recibe, cuando se procesa, entonces se mapea correctamente y está disponible para el cálculo.
- Dado que se consulta una ubicación, cuando se aplica la tarifa FHM, entonces se utiliza la cuota correcta según el grupo, zona y condición.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.

**Notas de Implementación**: La tarifa FHM puede tener múltiples criterios de aplicación.

**Estado**: Backlog

---
### HU-158: Consumir Factores de Equipo Electrónico
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `factores_equipo_electronico` del servicio `Plataforma-core-ohs`,
Para aplicar el factor técnico de equipo electrónico en el cálculo de primas.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere factores de equipo electrónico, cuando los consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que los factores se reciben, cuando se procesan, entonces se mapean correctamente y están disponibles para el cálculo.
- Dado que se consulta una ubicación con equipo electrónico, cuando se aplica el factor, entonces se utiliza el valor correcto según la clase y nivel de zona.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Repositorio de Parámetros.

**Notas de Implementación**: Definir cómo se clasifica el equipo electrónico y su relación con las zonas.

**Estado**: Backlog

---
### HU-159: Consumir Catálogo de Códigos Postales y Zonas (EP-002)
**Descripción**:
Como sistema,
Quiero consumir o simular la consulta de `catalogo_cp_zonas` del servicio `Plataforma-core-ohs` y mapear zonas,
Para obtener la relación entre códigos postales y sus zonas (CAT, nivel técnico) para el cálculo.

**Criterios de Aceptación**:
- Dado que el motor de cálculo requiere el catálogo CP-Zonas, cuando lo consulta, entonces obtiene los datos del servicio `Plataforma-core-ohs` o del mock.
- Dado que el catálogo se recibe, cuando se procesa, entonces los códigos postales se mapean correctamente a sus zonas CAT y nivel técnico.
- Dado que se consulta un código postal, cuando se obtienen sus zonas, entonces la información es precisa y completa.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Adaptador para `Plataforma-core-ohs`), Mapeador de Datos.

**Notas de Implementación**: La gestión de versionado o fechas de vigencia para estos parámetros es una mejora futura.

**Estado**: Backlog

---
### HU-160: Parámetros Disponibles para Motores de Cálculo y Validación
**Descripción**:
Como sistema,
Quiero que todos los parámetros y tarifas cargados estén disponibles para el Motor de Validación y el Motor Central de Cálculo,
Para asegurar que ambos motores operan con la información más reciente y correcta.

**Criterios de Aceptación**:
- Dado que los parámetros y tarifas han sido cargados exitosamente, cuando el Motor de Validación los requiere, entonces tiene acceso a ellos.
- Dado que los parámetros y tarifas han sido cargados exitosamente, cuando el Motor Central de Cálculo los requiere, entonces tiene acceso a ellos.
- Dado que un parámetro se actualiza, cuando se consulta, entonces ambos motores reciben el valor actualizado.
**Prioridad**: Alta

**Estimación**: 1 punto de historia

**Dependencias**: HU-155, HU-156, HU-157, HU-158, HU-159

**Componentes Técnicos**: Backend (Repositorios de Parámetros, Servicios de Consulta de Parámetros).

**Notas de Implementación**: Implementar una interfaz común para acceder a los parámetros.

**Estado**: Backlog

---
