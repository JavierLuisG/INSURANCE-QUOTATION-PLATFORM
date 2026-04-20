## FT-009: Implementación de Reglas de Negocio y Validaciones

### HU-039: Validar datos generales de la cotización

Como usuario, quiero que los datos generales de la cotización se validen según reglas de negocio, para asegurar la calidad de la información.

**Criterios de Aceptación**:
- Dado que introduzco un RFC con formato incorrecto, cuando intento guardar, entonces el sistema muestra un mensaje de error de validación.
- Dado que la fecha de fin de vigencia es anterior a la fecha de inicio, cuando intento guardar, entonces el sistema muestra un error.
- Dado que un campo obligatorio (e.g., Nombre Asegurado) está vacío, cuando intento guardar, entonces el sistema me lo indica.

**Prioridad**: Alta

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-001: Creación y Edición de Datos Generales de la Cotización

**Componentes Técnicos**:
- Backend: Módulo de validación de datos generales.
- Frontend: Validaciones en el formulario.

**Notas de Implementación**:
- Implementar validaciones tanto en el frontend para una respuesta rápida como en el backend para seguridad.
- Los mensajes de error deben ser claros y orientar al usuario.

**Estado**: Backlog

---
### HU-040: Validar datos específicos de cada ubicación de riesgo

Como usuario, quiero que los datos específicos de cada ubicación de riesgo se validen, para asegurar la consistencia y corrección de la información de riesgo.

**Criterios de Aceptación**:
- Dado que introduzco un código postal inexistente, cuando intento guardar la ubicación, entonces el sistema muestra un error.
- Dado que el valor del bien excede un límite predefinido, cuando intento guardar, entonces el sistema muestra una advertencia o error.
- Dado que un campo obligatorio de la ubicación (e.g., dirección, uso) está vacío, cuando intento guardar, entonces el sistema me lo indica.

**Prioridad**: Alta

**Estimación**: 4 puntos de historia

**Dependencias**:
- FT-002: Gestión Dinámica de Ubicaciones de Riesgo
- HU-009: Consultar y validar código postal de ubicación

**Componentes Técnicos**:
- Backend: Módulo de validación de datos de ubicación.
- Frontend: Validaciones en el formulario de ubicación.

**Notas de Implementación**:
- Las reglas de validación deben ser configurables y escalables.
- Se debe diferenciar entre errores que impiden el guardado y advertencias.

**Estado**: Backlog

---
### HU-041: Aplicar reglas de negocio para el cálculo de primas

Como desarrollador, quiero que la lógica de cálculo de primas incorpore todas las reglas de negocio y factores técnicos definidos, para asegurar la precisión del precio.

**Criterios de Aceptación**:
- Dado que se aplica un recargo por una característica específica de la ubicación, cuando se ejecuta el cálculo, entonces el recargo se suma correctamente a la prima.
- Dado que existe un descuento por un canal de venta, cuando se ejecuta el cálculo, entonces el descuento se aplica a la prima comercial.
- Dado que una combinación de coberturas o riesgos requiere un factor especial, cuando se calcula, entonces ese factor se aplica según la regla definida.

**Prioridad**: Alta

**Estimación**: 5 puntos de historia

**Dependencias**:
- FT-004: Ejecución y Persistencia del Cálculo de Primas
- FT-007: Integración con Servicios de Referencia (Tarifas)

**Componentes Técnicos**:
- Backend: Motor de reglas de negocio, Módulo de cálculo de primas.

**Notas de Implementación**:
- Las reglas deben ser parametrizables y fáciles de mantener/actualizar.
- Se debe documentar cada regla de negocio y su impacto en el cálculo.

**Estado**: Backlog

---
### HU-042: Mostrar mensajes de error claros y útiles

Como usuario, quiero que el sistema me proporcione mensajes de error claros y útiles cuando las validaciones fallan, para saber cómo corregir los problemas.

**Criterios de Aceptación**:
- Dado que un campo no cumple con una validación, cuando intento guardar, entonces el mensaje de error indica específicamente qué campo y qué regla no se cumple.
- Dado que el sistema encuentra un error técnico, cuando me lo notifica, entonces el mensaje es amigable y sugiere una acción (e.g., "intente de nuevo").
- Dado que se muestran mensajes de error, cuando se corrigen los problemas, entonces los mensajes desaparecen.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**:
- HU-039: Validar datos generales de la cotización
- HU-040: Validar datos específicos de cada ubicación de riesgo

**Componentes Técnicos**:
- Frontend: Componentes de visualización de mensajes de error.
- Backend: API de cotizaciones (respuestas con mensajes de error estandarizados).

**Notas de Implementación**:
- Estandarizar el formato de los mensajes de error.
- Considerar la internacionalización si es necesario.

**Estado**: Backlog

---
### HU-043: Documentar y trazar las reglas de negocio

Como analista funcional, quiero que las reglas de negocio sean trazables y documentadas, para asegurar la transparencia y el mantenimiento del sistema.

**Criterios de Aceptación**:
- Dado que una regla de negocio está implementada, cuando se consulta la documentación técnica, entonces se describe la regla, su propósito y su implementación.
- Dado que una regla de negocio se aplica en el código, cuando se revisa, entonces está claramente identificada y mapeada con la documentación.
- Dado que se requiere modificar una regla, cuando se identifica, entonces la documentación y el código son fáciles de actualizar.
**Prioridad**: Media

**Estimación**: 3 puntos de historia

**Dependencias**:
- FT-009: Implementación de Reglas de Negocio y Validaciones

**Componentes Técnicos**:
- Backend: Código de reglas de negocio con comentarios/documentación interna.
- Documentación: Archivos de especificación de reglas de negocio.

**Notas de Implementación**:
- Utilizar herramientas de documentación (e.g., Swagger/OpenAPI para API, Confluence para reglas).
- Mantener la documentación sincronizada con el código.

**Estado**: Backlog

---
