# Contenido general de Épicas

- **EP-001**: Gestión Integral de Cotizaciones de Daños
- **EP-002**: Motor de Cálculo y Reglas de Negocio
- **EP-003**: Integración y Gestión de Datos Maestros

---

## EP-001: Gestión Integral de Cotizaciones de Daños

**Descripción Breve**: Permite a los usuarios crear, capturar, editar y visualizar el ciclo completo de una cotización de seguros de daños (Borrador, Pendiente de Cálculo, Calculada, Aprobada, Rechazada, Emitida), desde la información general (incluyendo campos como Nombre del Asegurado, RFC, Tipo de Seguro, Moneda, Vigencia (fecha inicio/fin), y Canal de Venta) hasta las ubicaciones de riesgo y los resultados del cálculo de primas.

**Narrativa de Valor**: Para los **Agentes de Seguros y Usuarios Finales**, quienes quieren **obtener cotizaciones de seguros de daños de manera eficiente y precisa**, la Épica "Gestión Integral de Cotizaciones de Daños" es una **plataforma intuitiva** que proporciona **control total sobre el proceso de cotización, desde la entrada de datos hasta la visualización de resultados financieros detallados**.

**Objetivos de Negocio**:
- Asegurar que el tiempo promedio de creación de una cotización completa sea menor a 10 minutos.
- Incrementar la satisfacción del usuario con el proceso de cotización.
- Asegurar la captura completa y correcta de la información de riesgo.

**Criterios de Éxito** (KPIs):- Tiempo promedio para generar una cotización completa: < 10 minutos
- Tasa de error en la captura de datos: < 5%
- Calificación de usabilidad (SUS): > 80

**Stakeholders Principales**:
Agente de Seguros: Interesado en la eficiencia y facilidad de uso para sus clientes.
Usuario Final/Asegurado: Interesado en la transparencia y rapidez de la cotización.
Analista Funcional: Interesado en la correcta implementación de los flujos de negocio.

**Alcance Estimado**:
**Features Estimadas**: 9 features (FT-001 a FT-009)
**Complejidad**: Alta
**Duración Estimada**: 4-6 meses

**Dependencias**: Motor de Cálculo y Reglas de Negocio, Integración y Gestión de Datos Maestros.

**Prioridad**: Alta

**Estado**: Propuesto

**Notas Adicionales**: Requiere especial atención al diseño UX/UI para asegurar la facilidad de uso y la correcta gestión de múltiples ubicaciones de riesgo, utilizando una interfaz con un patrón de 'maestro-detalle' o 'pestañas' para cada ubicación, permitiendo agregar/eliminar dinámicamente y con un límite de 10 ubicaciones por cotización. Las opciones de cobertura son predefinidas y se seleccionan de un catálogo, cada una con un impacto directo en la prima a través de factores técnicos y reglas de negocio.

---
## EP-002: Motor de Cálculo y Reglas de Negocio

**Descripción Breve**: Implementa la lógica de negocio central para el cálculo de primas (neta, comercial, por ubicación) y la aplicación de reglas de validación (ej. validaciones de rangos para sumas aseguradas, validación de códigos postales contra catálogos, y requisitos mínimos de datos para cada ubicación antes del cálculo) y persistencia de cotizaciones, asegurando precisión y trazabilidad.

**Narrativa de Valor**: Para los **Analistas Funcionales y Evaluadores Técnicos**, quienes necesitan **asegurar la precisión y consistencia en el cálculo de primas de seguros de daños**, la Épica "Motor de Cálculo y Reglas de Negocio" es un **sistema robusto de backend** que proporciona **una lógica de cálculo trazable, validaciones de negocio rigurosas y persistencia confiable de los resultados financieros**.

**Objetivos de Negocio**:
- Garantizar la exactitud de los cálculos de primas en un 100% **según las fórmulas simplificadas y documentadas (no actuariales)**.
- Asegurar la aplicación consistente de las reglas de negocio en todas las cotizaciones.
- Mantener la integridad y el versionado de los datos de cotización en cada edición.

**Criterios de Éxito** (KPIs):
- Precisión de cálculo: 100% de coincidencia con **las fórmulas simplificadas** definidas.
- Cobertura de pruebas unitarias en lógica de cálculo: > 90%
- Tiempo de respuesta del cálculo de prima (desde la solicitud frontend hasta la persistencia backend): < 2 segundos

**Stakeholders Principales**:
Analista Funcional: Interesado en la correcta implementación de reglas y fórmulas.
Desarrollador: Interesado en la robustez, escalabilidad y mantenibilidad del código.
Evaluador Técnico: Interesado en la calidad y cobertura de pruebas de la lógica central.

**Alcance Estimado**:
**Features Estimadas**: 5 features (FT-010 a FT-014)
**Complejidad**: Alta
**Duración Estimada**: 3-5 meses

**Dependencias**: Integración y Gestión de Datos Maestros (para tarifas y parámetros), Gestión Integral de Cotizaciones de Daños (para disparar el cálculo).

**Prioridad**: Alta

**Estado**: Propuesto

**Notas Adicionales**: La simulación o consumo del servicio `Plataforma-core-ohs` para tarifas y catálogos es crítica para el desarrollo y pruebas de este motor. En caso de conflictos de concurrencia durante la edición, se notificará al usuario y se le pedirá recargar la cotización para ver la última versión antes de reintentar la edición. La persistencia de la información de cotizaciones se realizará preferentemente en MongoDB.

---
## EP-003: Integración y Gestión de Datos Maestros

**Descripción Breve**: Establece la conectividad y el manejo de datos de referencia (catálogos, tarifas, y folios alfanuméricos con un prefijo fijo y una secuencia numérica, ej. 'COT-202X-000001', cuya generación reintentará en caso de fallo o notificará al usuario para acción manual) provenientes de sistemas externos o simulados, asegurando que el cotizador opere con información actualizada y consistente.

**Narrativa de Valor**: Para los **Desarrolladores y Analistas Funcionales**, quienes necesitan **acceder a información de referencia actualizada y consistente para el cálculo de cotizaciones**, la Épica "Integración y Gestión de Datos Maestros" es un **componente de integración** que proporciona **acceso confiable a catálogos, tarifas y la generación de folios, minimizando errores y facilitando la operación del cotizador**.

**Objetivos de Negocio**:
- Asegurar la disponibilidad de catálogos y tarifas actualizadas en todo momento.
- Reducir el esfuerzo manual en la gestión de datos de referencia (ej. actualización de catálogos de suscriptores, agentes, giros y tarifas, que actualmente se realiza mediante carga manual de archivos o ingreso directo).
- Garantizar la trazabilidad de los datos de origen para auditorías.

**Criterios de Éxito** (KPIs):- Disponibilidad del servicio de catálogos y tarifas: > 99.9%
- Tiempo de respuesta de consulta de catálogos: < 500 ms
- Número de errores de inconsistencia de datos por origen: 0 (se implementarán validaciones a nivel de la capa de integración, registrando inconsistencias para revisión manual o corrección automática si es posible)

**Stakeholders Principales**:
Desarrollador: Interesado en la estabilidad y claridad de los contratos de integración.
Analista Funcional: Interesado en la completitud y corrección de los datos de referencia.
Evaluador Técnico: Interesado en la robustez de la simulación o integración real.

**Alcance Estimado**:
**Features Estimadas**: 8 features (FT-015 a FT-022)
**Complejidad**: Media
**Duración Estimada**: 2-4 meses

**Dependencias**: Ninguna (es una dependencia para las otras épicas).

**Prioridad**: Alta

**Estado**: Propuesto

**Notas Adicionales**: La robustez y versionado de la simulación del servicio `Plataforma-core-ohs` es fundamental para el progreso de las demás épicas. Esta simulación se realizará mediante un mock server que replicará los contratos de la API REST de Plataforma-core-ohs, soportado por una base de datos (preferentemente MongoDB) poblada con migraciones (ej. Flyway), lo que permitirá generar respuestas dinámicas, controlar escenarios de prueba y garantizar consistencia en los datos de catálogos y tarifas. La disponibilidad del servicio simulado se garantizará mediante la robustez del mock server y pruebas de carga simuladas para validar su estabilidad.
