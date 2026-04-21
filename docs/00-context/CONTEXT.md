# Análisis y Síntesis de Contexto

## Resumen Ejecutivo
El proyecto busca desarrollar un cotizador de seguros de daños integral, compuesto por una interfaz web (SPA), un backend robusto y una integración con servicios de referencia. La solución permitirá a los usuarios capturar folios, gestionar información general y el layout de ubicaciones, registrar y editar ubicaciones de riesgo, calcular primas netas y comerciales con desglose por los 14 componentes técnicos, y visualizar los resultados financieros de manera detallada. El objetivo es entregar una solución funcional, documentada y con cobertura de pruebas.

## Objetivos del Proyecto
- Construir una solución funcional para un cotizador de daños con interfaz web.
- Permitir la captura de folios y el registro de información general de cotizaciones.
- Administrar el layout de ubicaciones y las ubicaciones de riesgo con sus configuraciones y datos específicos del dominio.
- Calcular la prima neta comercial con desglose de los 14 componentes técnicos por ubicación.
- Integrar o simular la consulta de catálogos, tarifas y datos maestros desde un servicio core.
- Entregar una solución ejecutable y documentada, demostrando capacidad técnica en backend, frontend, persistencia y pruebas.

## Alcance Identificado

### Dentro del Alcance
- Desarrollo de una SPA (Single Page Application) para captura y consulta de cotizaciones.
- Desarrollo de un backend principal para administrar el ciclo de vida de la cotización.
- Creación y gestión de folios con idempotencia (patrón `COT-AAAA-NNNNNN`).
- Consulta y configuración del layout de ubicaciones (`configuracionLayout`).
- Captura, consulta, edición y persistencia de datos generales y configuraciones de layout de ubicaciones.
- Registro, consulta y edición de múltiples ubicaciones de riesgo (sin eliminación; solo marcado como inactiva).
- Consulta de estado y opciones de cobertura de la cotización.
- Ejecución y persistencia del cálculo de prima neta, prima comercial y primas por ubicación para los 14 componentes técnicos activos.
- Manejo de versionado optimista y actualización de metadatos en ediciones.
- Implementación de reglas de negocio: ubicaciones incompletas generan `alertasBloqueantes` y se excluyen del cálculo individualmente, **sin bloquear el cálculo de las ubicaciones válidas**.
- Consumo o simulación de servicios de referencia para catálogos (suscriptores, agentes, giros, CP, clasificación de riesgo, garantías) y tarifas.
- Implementación de lógica de cálculo para los 14 componentes técnicos de prima por ubicación y consolidación total.
- Persistencia de datos en colecciones específicas para cotizaciones, parámetros y tarifas.
- Cobertura de pruebas unitarias (mínimo 80%) y pruebas automatizadas (mínimo 3 flujos críticos).
- Generación de documentación técnica completa y entregables específicos (ASSD specs, video, repositorio).

### Fuera del Alcance
- Replicación exacta de una fórmula actuarial real; se requiere lógica consistente, trazable y documentada.
- Implementación de un servicio real adicional para `Plataforma-core-ohs`; se acepta un stub, mock server o fixtures versionados siempre que el contrato quede documentado.
- Desarrollo de un pipeline de CI o `docker-compose.yml` como entregables obligatorios (son opcionales).
- Exportación de resultados a PDF/Excel.
- Auto-guardado local complejo con recuperación de sesión.

## Módulos Potenciales Identificados

### Módulo: cotizador-danos-web (Frontend SPA)
**Descripción**: Interfaz de usuario para la interacción con el sistema de cotización de daños, permitiendo la captura de datos, visualización de estados y resultados.
**Funcionalidades Principales**:
- Crear o abrir un folio existente.
- Capturar datos generales de la cotización.
- Consultar y configurar el layout de ubicaciones.
- Consultar catálogos (suscriptores, agentes, giros, códigos postales).
- Capturar y editar una o varias ubicaciones con todos los campos del dominio.
- Visualizar el progreso y estado del folio, incluyendo alertas por ubicaciones incompletas.
- Configurar opciones de cobertura.
- Ejecutar el cálculo de la prima.
- Mostrar la prima neta, prima comercial y desglose por ubicación.
- Mostrar alertas de ubicaciones excluidas del cálculo sin bloquear el resultado total.
- Vista de información técnica detallada por componente de cálculo.
- Vista de términos y condiciones previo a aprobación.
**Rutas funcionales**:
- `/cotizador`
- `/quotes/{folio}/general-info`
- `/quotes/{folio}/locations`
- `/quotes/{folio}/technical-info`
- `/quotes/{folio}/terms-and-conditions`
**Estimación**: ~[25] RF, ~[5] RNF

### Módulo: plataformas-danos-back (Backend Principal)
**Descripción**: Core del sistema que gestiona la lógica de negocio, la persistencia de datos de cotizaciones y la integración con servicios de referencia.
**Funcionalidades Principales**:
- Crear folios con idempotencia.
- Consultar, guardar y editar datos generales de cotizaciones.
- Gestionar la configuración de layout de ubicaciones (`GET/PUT /v1/quotes/{folio}/locations/layout`).
- Registrar, consultar, editar y resumir ubicaciones.
- Marcar ubicaciones como inactivas (no eliminarlas).
- Consultar el estado y opciones de cobertura de una cotización.
- Ejecutar el cálculo de la prima aplicando los 14 componentes técnicos para ubicaciones calculables; excluir con alerta las incompletas.
- Persistir el resultado financiero de forma atómica.
- Manejar versionado optimista en operaciones de edición.
- Aplicar reglas de negocio: validar que cada ubicación tenga CP válido, `giro.claveIncendio` y garantías tarifables para ser calculable.
- Integración con el servicio `Plataforma-core-ohs` para obtener datos de referencia.
**Endpoints mínimos**:
- `PUT /v1/quotes/{folio}/general-info`
- `GET /v1/quotes/{folio}/locations/layout`
- `PUT /v1/quotes/{folio}/locations/layout`
- `GET /v1/quotes/{folio}/locations`
- `PUT /v1/quotes/{folio}/locations`
- `PATCH /v1/quotes/{folio}/locations/{índice}`
- `GET /v1/quotes/{folio}/locations/summary`
- `GET /v1/quotes/{folio}/state`
- `GET /v1/quotes/{folio}/coverage-options`
- `PUT /v1/quotes/{folio}/coverage-options`
- `POST /v1/quotes/{folio}/calculate`
**Estimación**: ~[35] RF, ~[10] RNF

### Módulo: Plataforma-core-ohs (Servicio de Referencia)
**Descripción**: Servicio externo (o simulado) que provee catálogos, tarifas, agentes, códigos postales y la generación de folios, esencial para el funcionamiento del cotizador.
**Funcionalidades Principales**:
- Proporcionar catálogo de suscriptores, agentes y giros.
- Validar y consultar información de códigos postales y zona CAT.
- Generar folios secuenciales.
- Suministrar catálogos de clasificación de riesgo y garantías.
- Consultar tarifas y factores técnicos para el cálculo de los 14 componentes.
**Endpoints de referencia**:
- `GET /v1/subscribers`
- `GET /v1/agents`
- `GET /v1/business-lines`
- `GET /v1/zip-codes/{zipCode}`
- `POST /v1/zip-codes/validate`
- `GET /v1/folios`
- `GET /v1/catalogs/risk-classification`
- `GET /v1/catalogs/guarantees`
- `GET|PUT /v1/tariffs/...`
**Estimación**: ~[10] RF, ~[3] RNF (considerando su simulación o consumo)

## Dominio Mínimo

### Cotización
- `numeroFolio`
- `estadoCotizacion` (Borrador | Pendiente de Cálculo | Calculada | Aprobada | Rechazada | Emitida)
- `datosAsegurado`
- `datosConduccion.codigoAgente`
- `clasificacionRiesgo`
- `tipoNegocio`
- `configuracionLayout`
- `opcionesCobertura`
- `ubicaciones[]`
- `primaNeta`
- `primaComercial`
- `primasPorUbicacion[]`
- `version`
- `metadatos` (fechaUltimaActualizacion, etc.)

### Ubicación
Cada ubicación debe incluir al menos:
- `índice`
- `nombreUbicacion`
- `direccion`
- `codigoPostal`
- `estado`
- `municipio`
- `colonia`
- `ciudad`
- `tipoConstructivo`
- `nivel`
- `anioConstruccion`
- `giro`
- `giro.claveIncendio`
- `garantías[]`
- `zonaCatastrofica`
- `alertasBloqueantes`
- `estadoValidacion` (COMPLETA | INCOMPLETA | INACTIVA)

### Regla de Calculabilidad de Ubicación
Una ubicación es calculable (`estadoValidacion: COMPLETA`) solo si cumple los tres criterios:
1. Tiene un `codigoPostal` válido en el catálogo `catalogo_cp_zonas`.
2. Tiene `giro.claveIncendio` presente.
3. Tiene al menos una garantía tarifable en `garantías[]`.

### Componentes Técnicos del Cálculo (14)
El motor de cálculo debe contemplar los siguientes componentes, aplicando solo los activos según las coberturas configuradas:
1. Incendio edificios
2. Incendio contenidos
3. Extensión de cobertura
4. CAT TEV
5. CAT FHM
6. Remoción de escombros
7. Gastos extraordinarios
8. Pérdida de rentas
9. BI (Business Interruption)
10. Equipo electrónico
11. Robo
12. Dinero y valores
13. Vidrios
14. Anuncios luminosos

## Stakeholders y Usuarios
- **Usuario Final/Asegurado**: Persona que interactúa con la SPA para crear, consultar y calcular cotizaciones de daños. Necesita una interfaz intuitiva para la captura de datos y visualización de resultados.
- **Agente de Seguros**: Rol que podría usar el sistema para generar cotizaciones para clientes. Necesita acceso a catálogos y cálculo preciso.
- **Analista Funcional/Arquitecto**: Responsable del diseño, modelado de datos y definición de reglas de negocio.
- **Desarrollador/Participante del Reto**: Encargado de la implementación técnica del backend y frontend, pruebas y documentación.
- **Evaluador Técnico**: Revisa y valida la calidad del código, la arquitectura, las pruebas y la documentación.

## Dependencias Técnicas Identificadas
- **Plataforma-core-ohs (API REST)**: Servicio externo (o simulado) para catálogos de suscriptores, agentes, giros, códigos postales, generación de folios, clasificación de riesgo, garantías y tarifas.
- **Base de Datos/Colecciones**:
    - `cotizaciones_danos`: Para captura operativa, ubicaciones, coberturas y resultado financiero.
    - `parametros_calculo`: Parámetros globales para convertir prima técnica a comercial.
    - `tarifas_incendio`: Tasas base y metadatos técnicos por giro.
    - `tarifas_cat`: Factores CAT por zona.
    - `tarifa_fhm`: Cuotas FHM por grupo, zona y condición.
    - `factores_equipo_electronico`: Factor técnico por clase y nivel de zona.
    - `catalogo_cp_zonas`: Relación entre código postal, zona CAT y nivel técnico.
    - `dim_zona_tev`: Catálogo de apoyo para normalización de zona TEV.
    - `dim_zona_fhm`: Catálogo de apoyo para normalización de zona FHM.

## Restricciones Identificadas
**Técnicas**:
- Persistencia de la cotización como agregado principal en MongoDB.
- Escrituras por actualización parcial.
- Incremento de versión y actualización de `fechaUltimaActualizacion` en ediciones.
- Manejo de versionado optimista.
- El cálculo debe guardar `primaNeta`, `primaComercial` y `primasPorUbicacion` en una misma operación lógica atómica.
- **Las ubicaciones no se eliminan físicamente; solo se marcan como inactivas** (`estadoValidacion: INACTIVA`).
- **Una ubicación incompleta genera `alertasBloqueantes` y se excluye del cálculo, pero no impide el cálculo de las demás ubicaciones válidas.** El cálculo solo se bloquea si no existe ninguna ubicación con `estadoValidacion: COMPLETA`.
- La lógica de cálculo debe ser consistente, trazable y documentada para los 14 componentes técnicos.
- Cobertura mínima del 80% en pruebas unitarias.
- Implementación de al menos 3 flujos críticos en pruebas automatizadas.
- Uso obligatorio de la metodología ASSD para los entregables.
**Temporales**:
- Fecha máxima de entrega: Lunes 30 de marzo de 2026 a las 6:00 p.m.
**Presupuestarias**: No especificado
**Normativas**: No especificado
