# Análisis y Síntesis de Contexto

## Resumen Ejecutivo
El proyecto busca desarrollar un cotizador de seguros de daños integral, compuesto por una interfaz web, un backend robusto y una integración con servicios de referencia. La solución permitirá a los usuarios capturar folios, gestionar información general y ubicaciones de riesgo, calcular primas netas y comerciales, y visualizar los resultados financieros de manera detallada. El objetivo es entregar una solución funcional, documentada y con cobertura de pruebas.

## Objetivos del Proyecto
- Construir una solución funcional para un cotizador de daños con interfaz web.
- Permitir la captura de folios y el registro de información general de cotizaciones.
- Administrar ubicaciones de riesgo con sus configuraciones y datos específicos.
- Calcular la prima neta comercial y el desglose por ubicación.
- Integrar o simular la consulta de catálogos, tarifas y datos maestros desde un servicio core.
- Entregar una solución ejecutable y documentada, demostrando capacidad técnica en backend, frontend, persistencia y pruebas.

## Alcance Identificado

### Dentro del Alcance
- Desarrollo de una SPA (Single Page Application) para captura y consulta de cotizaciones.
- Desarrollo de un backend principal para administrar el ciclo de vida de la cotización.
- Creación y gestión de folios con idempotencia.
- Captura, consulta, edición y persistencia de datos generales y configuraciones de layout de ubicaciones.
- Registro, consulta y edición de múltiples ubicaciones de riesgo.
- Consulta de estado y opciones de cobertura de la cotización.
- Ejecución y persistencia del cálculo de prima neta, prima comercial y primas por ubicación.
- Manejo de versionado optimista y actualización de metadatos en ediciones.
- Implementación de reglas de negocio para cálculo y validación de ubicaciones.
- Consumo o simulación de servicios de referencia para catálogos (suscriptores, agentes, giros, CP, clasificación de riesgo, garantías) y tarifas.
- Implementación de lógica de cálculo de prima por ubicación y consolidación total.
- Persistencia de datos en colecciones específicas para cotizaciones, parámetros y tarifas.
- Cobertura de pruebas unitarias (mínimo 80%) y pruebas automatizadas (mínimo 3 flujos críticos).
- Generación de documentación técnica completa y entregables específicos (ASSD specs, video, repositorio).

### Fuera del Alcance
- Replicación exacta de una fórmula actuarial real si no fue entregada, aunque se requiere lógica consistente y trazable.
- Implementación de un servicio real adicional para `Plataforma-core-ohs`; se acepta un stub, mock server o fixtures versionados.
- Desarrollo de un pipeline de CI o `docker-compose.yml` como entregables obligatorios (son opcionales).
- Cobertura de pruebas completa (solo se requiere mínimo 80% unitarias y 3 flujos automatizados).

## Módulos Potenciales Identificados

### Módulo: cotizador-danos-web (Frontend SPA)
**Descripción**: Interfaz de usuario para la interacción con el sistema de cotización de daños, permitiendo la captura de datos, visualización de estados y resultados.
**Funcionalidades Principales**:
- Crear o abrir un folio existente.
- Capturar datos generales de la cotización.
- Consultar catálogos (suscriptores, agentes, giros, códigos postales).
- Capturar y editar una o varias ubicaciones.- Visualizar el progreso y estado del folio, incluyendo alertas por ubicaciones incompletas.
- Configurar opciones de cobertura.
- Ejecutar el cálculo de la prima.
- Mostrar la prima neta, prima comercial y desglose por ubicación.
**Estimación**: ~[25] RF, ~[5] RNF

### Módulo: plataformas-danos-back (Backend Principal)
**Descripción**: Core del sistema que gestiona la lógica de negocio, la persistencia de datos de cotizaciones y la integración con servicios de referencia.
**Funcionalidades Principales**:
- Crear folios con idempotencia.
- Consultar, guardar y editar datos generales de cotizaciones y configuración de layouts.
- Registrar, consultar, editar y resumir ubicaciones.
- Consultar el estado y opciones de cobertura de una cotización.
- Ejecutar el cálculo de la prima y persistir los resultados financieros.- Manejar versionado optimista en operaciones de edición.
- Aplicar reglas de negocio para el cálculo y validación de ubicaciones.
- Integración con el servicio `Plataforma-core-ohs` para obtener datos de referencia.
**Estimación**: ~[35] RF, ~[10] RNF

### Módulo: Plataforma-core-ohs (Servicio de Referencia)
**Descripción**: Servicio externo (o simulado) que provee catálogos, tarifas, agentes, códigos postales y la generación de folios, esencial para el funcionamiento del cotizador.
**Funcionalidades Principales**:
- Proporcionar catálogo de suscriptores, agentes y giros.
- Validar y consultar información de códigos postales.
- Generar folios secuenciales.
- Suministrar catálogos de clasificación de riesgo y garantías.
- Consultar tarifas y factores técnicos para el cálculo de primas.
**Estimación**: ~[10] RF, ~[3] RNF (considerando su simulación o consumo)

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
    - `parametros_calculo`: Parámetros globales para conversión de prima.
    - `tarifas_incendio`: Tasas base y metadatos técnicos.
    - `tarifas_cat`: Factores CAT por zona.
    - `tarifa_fhm`: Cuotas FHM por grupo, zona y condición.
    - `factores_equipo_electronico`: Factor técnico por clase y nivel de zona.
    - `catalogo_cp_zonas`: Relación entre código postal, zona CAT y nivel técnico.
    - `dim_zona_tev`, `dim_zona_fhm`: Catálogos de apoyo para normalización.

## Restricciones Identificadas
**Técnicas**:
- Persistencia de la cotización como agregado principal.
- Escrituras por actualización parcial.- Incremento de versión y actualización de `fechaUltimaActualizacion` en ediciones.
- Manejo de versionado optimista.
- El cálculo debe guardar prima neta, comercial y por ubicación en una misma operación lógica.- La lógica de cálculo debe ser consistente, trazable y documentada.
- Cobertura mínima del 80% en pruebas unitarias.
- Implementación de al menos 3 flujos críticos en pruebas automatizadas.
- Uso obligatorio de la metodología ASSD para los entregables.
**Temporales**:
- Fecha máxima de entrega: Lunes 30 de marzo de 2026 a las 6:00 p.m.
**Presupuestarias**: No especificado
**Normativas**: No especificado