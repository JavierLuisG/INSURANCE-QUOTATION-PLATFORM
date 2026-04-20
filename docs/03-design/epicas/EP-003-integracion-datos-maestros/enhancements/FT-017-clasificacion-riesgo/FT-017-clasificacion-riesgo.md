## FT-017: Integración de Catálogos de Clasificación de Riesgo y Garantías

### HU-185: Recuperar Catálogos de Clasificación de Riesgo y Garantías
**Descripción**:
Como sistema,
Quiero poder recuperar los catálogos de clasificación de riesgo y garantías desde `Plataforma-core-ohs` (o su mock),
Para ofrecer opciones detalladas de configuración de coberturas.

**Criterios de Aceptación**:
- Dado que el sistema requiere estos catálogos, cuando los consulta, entonces obtiene los datos de clasificación de riesgo y garantías del servicio externo.
- Dado que la consulta es exitosa, cuando se completa, entonces los datos están disponibles para el cotizador.
- Dado que el servicio externo no está disponible, cuando se consulta, entonces el sistema registra el error y lo maneja.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-189 (Simulación de Servicio `Plataforma-core-ohs`)

**Componentes Técnicos**: Backend (Cliente API REST para `Plataforma-core-ohs`).

**Notas de Implementación**: Estos catálogos suelen ser de menor volumen y actualización menos frecuente.

**Estado**: Backlog

---
### HU-186: Mapear Datos de Catálogos de Riesgo y Garantías
**Descripción**:
Como sistema,
Quiero que los datos de los catálogos de clasificación de riesgo y garantías se mapeen correctamente y estén disponibles para la interfaz de usuario y la lógica de negocio,
Para permitir su selección y uso en la configuración de coberturas.

**Criterios de Aceptación**:
- Dado que se reciben datos de clasificación de riesgo o garantías, cuando se procesan, entonces se mapean a las estructuras de datos internas.
- Dado que el mapeo es exitoso, cuando se completa, entonces la interfaz de usuario puede presentar estas opciones al usuario.
- Dado que la lógica de negocio requiere estos datos, cuando los consulta, entonces están en el formato esperado.

**Prioridad**: Media

**Estimación**: 2 puntos de historia

**Dependencias**: HU-185 (Recuperar Catálogos de Clasificación de Riesgo y Garantías)

**Componentes Técnicos**: Backend (Capa de Mapeo de Catálogos Específicos).

**Notas de Implementación**: Asegurar que los datos mapeados sean consistentes con los requisitos de la UI.

**Estado**: Backlog

---
### HU-187: Reflejar Cambios de Catálogos en Cotizador
**Descripción**:
Como sistema,
Quiero que los cambios realizados en los catálogos de clasificación de riesgo y garantías en el sistema de origen se reflejen de manera consistente en el cotizador,
Para asegurar que la información siempre esté actualizada.

**Criterios de Aceptación**:
- Dado que un elemento en un catálogo de origen se modifica o añade, cuando el sistema consulta el catálogo, entonces el cambio se refleja en los datos obtenidos.
- Dado que un elemento en un catálogo de origen se elimina, cuando el sistema consulta el catálogo, entonces el elemento ya no está disponible.
- Dado que los cambios se reflejan, cuando se consulta la interfaz de usuario, entonces las opciones presentadas están actualizadas.

**Prioridad**: Media

**Estimación**: 1 punto de historia

**Dependencias**: HU-185 (Recuperar Catálogos de Clasificación de Riesgo y Garantías)

**Componentes Técnicos**: Backend (Mecanismo de Sincronización de Catálogos, si aplica).

**Notas de Implementación**: Se puede usar una estrategia de caché con TTL corto o invalidación forzada.

**Estado**: Backlog

---
