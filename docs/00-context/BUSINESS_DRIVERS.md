# Análisis de Business Drivers

### Descripción del Cliente

El cliente es una entidad en el sector de **seguros de daños**, que busca optimizar y digitalizar el proceso de emisión de cotizaciones. Su operación se centra en la gestión de pólizas que cubren diversos riesgos y ubicaciones. Aunque no se especifica un enfoque geográfico particular, la mención de "Plataforma-core-ohs" y la necesidad de integrar con servicios corporativos existentes sugiere un entorno empresarial establecido. El sistema debe considerar la necesidad de **precisión en los cálculos** y la **trazabilidad de la información**, lo que implica un entorno donde la exactitud y la auditabilidad son factores críticos, posiblemente influenciados por requisitos regulatorios o de cumplimiento interno.

### Objetivo Estratégico

El objetivo estratégico principal es **mejorar la eficiencia y la precisión en el proceso de cotización de seguros de daños**, lo que se desglosa en los siguientes objetivos cuantificables y con plazos implícitos en el ciclo del proyecto:

*   **Reducir el tiempo promedio de creación de una cotización completa a menos de 10 minutos.** (Extraído de EP-001)
*   **Incrementar la satisfacción del usuario con el proceso de cotización.** (Extraído de EP-001, medido por SUS > 80)
*   **Garantizar el 100% de exactitud en los cálculos de primas** según las fórmulas simplificadas y documentadas. (Extraído de EP-002)
*   **Reducir el esfuerzo manual en la gestión de datos de referencia** (catálogos y tarifas), que actualmente se realiza mediante procesos manuales. (Extraído de EP-003)

### Contexto Actual y Problema

El problema principal que el proyecto busca resolver es la **ineficiencia y la propensión a errores en los procesos actuales de cotización y gestión de datos de referencia**. Específicamente:

*   **Procesos manuales de gestión de datos maestros:** La actualización de catálogos de suscriptores, agentes, giros y tarifas se realiza actualmente mediante carga manual de archivos o ingreso directo, lo que es ineficiente, consume tiempo y es propenso a errores humanos.
*   **Falta de un sistema integral de cotización:** La ausencia de una plataforma intuitiva y con control total sobre el proceso de cotización dificulta la eficiencia y precisión para Agentes de Seguros y Usuarios Finales.
*   **Riesgos de inconsistencia de datos:** La gestión manual y la falta de un sistema robusto para la aplicación de reglas de negocio y cálculo de primas aumentan el riesgo de errores e inconsistencias en las cotizaciones emitidas.

### Desafíos de Negocio

La solución debe superar los siguientes desafíos estratégicos y operativos:

*   **Asegurar la precisión y consistencia del cálculo de primas:** Implementar un motor de cálculo que garantice el 100% de exactitud según fórmulas simplificadas y la aplicación consistente de reglas de negocio.
*   **Garantizar la disponibilidad y frescura de los datos de referencia:** Asegurar que catálogos y tarifas estén actualizados y accesibles en todo momento para el proceso de cotización.
*   **Proporcionar una experiencia de usuario altamente eficiente:** Ofrecer una interfaz intuitiva que permita la creación de cotizaciones completas en menos de 10 minutos y con alta usabilidad (SUS > 80).
*   **Soportar alta concurrencia y escalabilidad:** Diseñar un sistema capaz de manejar hasta 500 usuarios concurrentes sin degradación del rendimiento, manteniendo tiempos de respuesta rápidos para operaciones CRUD (< 1.5s) y cálculo de primas (< 3s).
*   **Proteger la información sensible:** Implementar medidas de seguridad robustas para el cifrado de datos en tránsito y en reposo, así como una autenticación y autorización rigurosas.
*   **Mantener la integridad de los datos:** Prevenir la pérdida o corrupción de datos debido a ediciones concurrentes y asegurar la unicidad de los folios generados.
*   **Garantizar la resiliencia operativa:** Minimizar el impacto en la operación del cotizador ante fallos o latencias del servicio externo `Plataforma-core-ohs`, implementando mecanismos de reintento y degradación controlada de la funcionalidad.

### Matriz de Roles


| role | poder | interes | descripcion |
|---|---|---|---|
| Agente de Seguros | MEDIO | ALTO | Interesado en la eficiencia, facilidad de uso y rapidez del sistema para generar cotizaciones precisas para sus clientes, mejorando su productividad y experiencia. |
| Usuario Final/Asegurado | BAJO | ALTO | Interesado en la transparencia, rapidez y precisión de la cotización, así como en una interfaz intuitiva para la captura de datos y visualización de resultados. |
| Analista Funcional | ALTO | ALTO | Responsable de la correcta definición e implementación de los flujos de negocio, reglas de cálculo, validaciones y modelos de datos. Clave en la trazabilidad y precisión. |
| Desarrollador / Participante del Reto | MEDIO | ALTO | Encargado de la implementación técnica del frontend, backend, persistencia y pruebas. Interesado en la robustez, escalabilidad y mantenibilidad del código. |
| Evaluador Técnico | ALTO | ALTO | Revisa y valida la calidad del código, la arquitectura, la cobertura de pruebas y la documentación técnica del sistema. Es el garante de la calidad técnica. |
| Administrador de Parámetros | MEDIO | ALTO | Responsable de la gestión y actualización de catálogos, tarifas y factores técnicos. Busca eficiencia en la ingestión y disponibilidad de datos de referencia. |
| Auditor | ALTO | MEDIO | Interesado en la trazabilidad de los cálculos, la integridad de los datos y el cumplimiento de las políticas y regulaciones. Requiere acceso a historiales y documentación. |


### Restricciones de Negocio


| id | titulo | razonamiento | flexibilidad | alternativa | stakeholder |
|---|---|---|---|---|---|
| BC-001 | No replicación de lógica actuarial compleja | El proyecto se enfoca en implementar fórmulas simplificadas y documentadas para el cálculo de primas, sin replicar una fórmula actuarial real completa si no fue entregada. La complejidad actuarial no está en el alcance inicial. | BAJA | Si se intenta implementar lógica actuarial compleja sin especificación, se excederá el alcance, el tiempo y el presupuesto. El cálculo debe ser consistente y trazable, pero no necesariamente una simulación actuarial completa. | Analista Funcional, Evaluador Técnico |
| BC-002 | Uso obligatorio de MongoDB para persistencia | La persistencia de la información de cotizaciones se realizará preferentemente en MongoDB, lo que puede influir en el diseño del modelo de datos y la gestión de transacciones. | BAJA | Utilizar otra base de datos requeriría un cambio significativo en la tecnología de persistencia y en las decisiones de diseño arquitectónico asociadas, lo que no está permitido por el momento. | Desarrollador, Evaluador Técnico |
| BC-003 | Dependencia del servicio Plataforma-core-ohs (o su simulación) | El cotizador depende de un servicio externo (`Plataforma-core-ohs`) para catálogos y tarifas. Para el desarrollo y pruebas, se acepta un stub, mock server o fixtures versionadas en lugar del servicio real. | MEDIA | La ausencia de este servicio (real o simulado) bloquearía el desarrollo y las pruebas. No tener una simulación robusta impediría el progreso del proyecto. | Desarrollador, Analista Funcional |
| BC-004 | Fecha máxima de entrega del proyecto | Existe una fecha límite estricta para la entrega del proyecto: Lunes 30 de marzo de 2026 a las 6:00 p.m. | BAJA | El incumplimiento de la fecha de entrega podría resultar en la pérdida de la oportunidad de mercado o en penalizaciones contractuales (si aplica). | Todos los Stakeholders, Gerencia de Proyecto |
| BC-005 | Limitación de ubicaciones por cotización | Una cotización está limitada a un máximo de 10 ubicaciones de riesgo. Esto afecta el diseño de la interfaz y la lógica de cálculo. | BAJA | Permitir más de 10 ubicaciones sin una reevaluación podría impactar el rendimiento del cálculo y la usabilidad de la interfaz, además de exceder el requisito de negocio actual. | Analista Funcional, Desarrollador |
| BC-006 | Caché de datos maestros solo por TTL, sin invalidación por eventos | La estrategia de caché para datos maestros (catálogos, tarifas) se basará únicamente en un Time To Live (TTL) configurable, sin implementar invalidación por eventos en la primera versión. | BAJA | Implementar invalidación por eventos en la primera versión implicaría una complejidad adicional y un esfuerzo de desarrollo no contemplado inicialmente. La consistencia de datos se gestionará a través de TTL. | Desarrollador, Analista Funcional |
| BC-007 | Autenticación interna con gestión de usuarios propia | El sistema debe requerir autenticación interna con gestión de usuarios propia basada en credenciales (ej. usuario/contraseña), sin integrar un SSO corporativo existente o sistemas de identidad externos en esta fase. | BAJA | Integrar un SSO corporativo o un sistema de identidad externo requeriría un esfuerzo de desarrollo adicional y podría introducir dependencias no planificadas. | Evaluador Técnico, Desarrollador |


### Puntos de Dolor

Estos son los problemas específicos y frustraciones que el nuevo sistema busca aliviar:

*   **Esfuerzo manual excesivo en la gestión de datos maestros:** La actualización de catálogos y tarifas por carga manual o ingreso directo es lenta, tediosa y propensa a errores, impactando la frescura y consistencia de los datos en las cotizaciones.
*   **Inconsistencias y errores en los cálculos de primas:** La falta de un motor de cálculo automatizado y validado lleva a errores en las primas y a una aplicación inconsistente de las reglas de negocio, generando retrabajos y desconfianza.
*   **Dificultad para obtener cotizaciones rápidas y precisas:** Los Agentes de Seguros y Usuarios Finales experimentan frustración debido a la lentitud o complejidad para generar cotizaciones completas, afectando su productividad y la experiencia del cliente.
*   **Pérdida de datos por ediciones concurrentes:** En el proceso actual (o en la ausencia de un control robusto), existe el riesgo de que las modificaciones simultáneas de cotizaciones por diferentes usuarios resulten en la sobrescritura de datos o inconsistencias.
*   **Falta de trazabilidad y auditabilidad de las cotizaciones:** Dificultad para rastrear el historial de cambios, los parámetros utilizados en los cálculos o las reglas de negocio aplicadas, lo que complica las auditorías y el soporte.
*   **Dependencia y vulnerabilidad a fallos del sistema core:** Si el sistema actual tiene una alta dependencia de un 'core' sin mecanismos de resiliencia, sus fallos pueden paralizar la operación de cotización, afectando la continuidad del negocio.

### Riesgos de Negocio

Estos son los impactos negativos cuantificables si el proyecto falla o los problemas persisten:

*   **Riesgo Financiero por Cálculos Incorrectos:** Cálculos de prima erróneos pueden llevar a pérdidas financieras por pólizas mal tarifadas (subcotizadas) o pérdida de clientes por primas excesivas (sobrecotizadas), afectando la rentabilidad de la compañía.
*   **Riesgo Operacional por Ineficiencia y Errores:** La lentitud en la generación de cotizaciones y los errores en la captura de datos o cálculos resultan en una baja productividad de los agentes, retrabajos, mayores costos operativos y una experiencia del cliente deficiente.
*   **Riesgo de Reputación y Satisfacción del Cliente:** La insatisfacción del cliente debido a demoras en la obtención de cotizaciones, errores en los precios o una interfaz de usuario poco amigable puede dañar la imagen de la compañía y aumentar la tasa de abandono de clientes.
*   **Riesgo de Cumplimiento y Seguridad:** La falta de cifrado de datos sensibles, controles de acceso débiles o errores en la gestión de datos pueden resultar en incumplimiento de normativas de protección de datos, acarreando multas significativas y daño reputacional.
*   **Riesgo de Continuidad del Negocio por Fallos del Sistema:** La inestabilidad del sistema o la falta de resiliencia ante fallos de servicios externos críticos (como `Plataforma-core-ohs`) puede paralizar completamente el proceso de cotización, impidiendo la venta de seguros.
*   **Riesgo de Integridad de Datos:** La ausencia de mecanismos robustos de control de concurrencia y gestión de versiones puede llevar a la corrupción de datos, pérdida de información crítica y cotizaciones inconsistentes, generando disputas y problemas legales.
*   **Riesgo de Retraso en la Entrega:** El incumplimiento de la fecha límite de entrega (30 de marzo de 2026) puede generar penalizaciones contractuales (si aplica), pérdida de oportunidad de mercado y un impacto negativo en la planificación estratégica de la empresa.