## FT-002: Gestión Dinámica de Ubicaciones de Riesgo

### HU-006: Agregar una nueva ubicación de riesgo a la cotizaciónComo usuario, quiero agregar una nueva ubicación de riesgo a la cotización, para detallar los diferentes lugares a asegurar.

**Criterios de Aceptación**:
- Dado que estoy editando una cotización, cuando hago clic en "Agregar Ubicación", entonces se crea una nueva sección o pestaña para una ubicación.
- Dado que he agregado una ubicación, cuando la guardo, entonces se asocia correctamente a la cotización padre.
- Dado que se ha alcanzado el límite de 10 ubicaciones, cuando intento agregar otra, entonces el sistema me notifica que no puedo añadir más.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-001: Iniciar nueva cotización con folio automático

**Componentes Técnicos**:
- Frontend: Botón "Agregar Ubicación", Interfaz de gestión de ubicaciones (maestro-detalle/pestañas).
- Backend: API de cotizaciones (endpoint de adición de ubicaciones).

**Notas de Implementación**:
- La interfaz debe permitir una navegación clara entre las ubicaciones.
- Se debe manejar el límite de ubicaciones configurado (e.g., 10).

**Estado**: Backlog

---
### HU-007: Editar detalles específicos de una ubicación de riesgo

Como usuario, quiero modificar los datos de una ubicación de riesgo existente, para corregir o actualizar su información.

**Criterios de Aceptación**:
- Dado que he seleccionado una ubicación, cuando edito sus campos de dirección, uso o características, entonces los cambios se guardan.
- Dado que un campo obligatorio de la ubicación se deja vacío, cuando intento guardar, entonces el sistema muestra un mensaje de validación.
- Dado que los datos de la ubicación se han guardado, cuando consulto la cotización, entonces los datos actualizados de la ubicación se muestran correctamente.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- HU-006: Agregar una nueva ubicación de riesgo a la cotización

**Componentes Técnicos**:
- Frontend: Formulario de detalles de ubicación.
- Backend: API de cotizaciones (endpoint de actualización de ubicaciones).

**Notas de Implementación**:
- Asegurar que la actualización parcial de ubicaciones sea posible sin afectar otras ubicaciones o datos generales.
- Las validaciones deben ser específicas para cada campo de la ubicación.

**Estado**: Backlog

---
### HU-008: Eliminar una ubicación de riesgo de la cotización

Como usuario, quiero eliminar una ubicación de riesgo que ya no es necesaria, para mantener la cotización actualizada y precisa.

**Criterios de Aceptación**:
- Dado que tengo múltiples ubicaciones en una cotización, cuando selecciono una ubicación y confirmo su eliminación, entonces la ubicación desaparece de la cotización.
- Dado que intento eliminar la única ubicación de una cotización, cuando confirmo la acción, entonces el sistema permite la eliminación o advierte si se requiere al menos una ubicación.
- Dado que se ha eliminado una ubicación, cuando consulto la cotización, entonces la ubicación eliminada ya no aparece.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-006: Agregar una nueva ubicación de riesgo a la cotización

**Componentes Técnicos**:
- Frontend: Botón "Eliminar Ubicación", Diálogo de confirmación.
- Backend: API de cotizaciones (endpoint de eliminación de ubicaciones).

**Notas de Implementación**:
- Implementar una confirmación de eliminación para evitar borrados accidentales.
- Considerar las implicaciones en los cálculos si se elimina una ubicación.

**Estado**: Backlog

---
### HU-009: Consultar y validar código postal de ubicación

Como usuario, quiero introducir el código postal de una ubicación y que se valide contra un catálogo, para asegurar la exactitud de la dirección.

**Criterios de Aceptación**:
- Dado que introduzco un código postal en el campo de la ubicación, cuando el sistema lo valida, entonces se verifica contra el catálogo de CP.
- Dado que introduzco un código postal válido, cuando el sistema lo valida, entonces se autocompletan o sugieren datos relacionados (e.g., estado, ciudad, colonia).
- Dado que introduzco un código postal inválido o inexistente, cuando el sistema lo valida, entonces se muestra un mensaje de error.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-007: Integración con Servicios de Referencia (Catálogos de CP)
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Campo de texto para Código Postal, Sugerencias/Autocompletado.
- Backend: API de cotizaciones (validación de CP), Integración con `Plataforma-core-ohs` (catálogo de CP).

**Notas de Implementación**:
- La integración con el servicio de CP debe ser eficiente para no ralentizar la captura.
- Se debe manejar la latencia del servicio externo y mostrar un indicador de carga.

**Estado**: Backlog

---
### HU-010: Visualizar alertas por datos incompletos en ubicaciones

Como usuario, quiero ver alertas visuales si una ubicación tiene datos incompletos o inválidos, para saber qué información necesita ser corregida.

**Criterios de Aceptación**:
- Dado que una ubicación tiene campos obligatorios sin completar, cuando estoy en la vista de ubicaciones, entonces se muestra un indicador visual (e.g., icono, color) en la pestaña o sección de esa ubicación.
- Dado que una ubicación tiene errores de validación, cuando intento guardar la cotización, entonces se me redirige o se resalta la ubicación con errores.
- Dado que completo todos los datos requeridos y válidos de una ubicación, cuando la guardo, entonces el indicador de alerta desaparece.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Frontend: Lógica de visualización de alertas, Componentes UI para indicadores.
- Backend: API de cotizaciones (respuestas de validación).

**Notas de Implementación**:
- La retroalimentación visual debe ser clara y no intrusiva.
- Las validaciones deben ser ejecutadas en tiempo real o al intentar guardar.

**Estado**: Backlog

---
