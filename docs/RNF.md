## Requerimientos No Funcionales - Sistema Completo

### RNF-001: Tiempo de Respuesta de Interfaz de Usuario
**Categoría**: Rendimiento
**Descripción**: La interfaz web (SPA) debe cargar y responder a las interacciones del usuario de manera fluida y rápida.
**Objetivo**: Proporcionar una experiencia de usuario eficiente y sin frustraciones a los usuarios finales y agentes de seguros.
**Métrica**: El 90% de las interacciones de usuario (clics, cambios de campo, navegación) deben responder en menos de 500 milisegundos. La carga inicial de la página principal del cotizador debe completarse en menos de 2 segundos.
**Criterio de Aceptación**: Pruebas de rendimiento de frontend demuestran cumplimiento con los tiempos especificados en las dos últimas versiones estables de los navegadores Chrome, Firefox, Edge y Safari.
**Prioridad**: Alta
**Impacto**: Baja satisfacción del usuario, pérdida de productividad para agentes y usuarios finales, y posible abandono del sistema.

### RNF-002: Tiempo de Respuesta de Operaciones CRUD
**Categoría**: Rendimiento
**Descripción**: Las operaciones de creación, consulta, edición y persistencia de folios y ubicaciones en el backend deben ejecutarse rápidamente.
**Objetivo**: Garantizar la eficiencia en la gestión de datos de cotizaciones, permitiendo a los usuarios trabajar sin demoras perceptibles.
**Métrica**: Las operaciones de guardar, consultar y editar folios y ubicaciones deben completarse en menos de 1.5 segundos para el 95% de las peticiones.
**Criterio de Aceptación**: Las pruebas de carga y rendimiento del backend confirman los tiempos de respuesta para las operaciones CRUD bajo una carga de 500 usuarios concurrentes.
**Prioridad**: Alta
**Impacto**: Retrasos en el proceso de cotización, afectando la productividad de los usuarios y la percepción de la eficiencia del sistema.

### RNF-003: Tiempo de Cálculo de Prima
**Categoría**: Rendimiento
**Descripción**: El proceso de cálculo de la prima neta, prima comercial y el desglose por ubicación debe ser rápido y eficiente.
**Objetivo**: Ofrecer resultados de cotización de forma casi instantánea a los usuarios, facilitando la toma de decisiones.
**Métrica**: El cálculo completo de la prima para una cotización con hasta 10 ubicaciones debe finalizar en menos de 3 segundos para el 98% de las solicitudes, incluyendo la latencia de servicios externos como `Plataforma-core-ohs` con una tolerancia definida.
**Criterio de Aceptación**: Pruebas de rendimiento del módulo de cálculo demuestran cumplimiento con el tiempo especificado bajo una carga de 100 cálculos concurrentes.
**Prioridad**: Alta
**Impacto**: Demoras significativas en la obtención de cotizaciones, afectando la toma de decisiones, la competitividad y la experiencia del usuario.

### RNF-004: Soporte de Usuarios Concurrentes
**Categoría**: Escalabilidad
**Descripción**: El sistema debe ser capaz de soportar un número creciente de usuarios interactuando simultáneamente sin degradación del rendimiento.
**Objetivo**: Asegurar que el sistema pueda manejar picos de demanda y el crecimiento futuro de la base de usuarios sin afectar la calidad del servicio.
**Métrica**: El sistema debe soportar hasta 500 usuarios concurrentes realizando operaciones de captura y cálculo sin que los tiempos de respuesta excedan los umbrales definidos en RNF-001, RNF-002 y RNF-003.
**Criterio de Aceptación**: Pruebas de carga demuestran que el sistema mantiene el rendimiento objetivo con 500 usuarios concurrentes durante al menos 1 hora. El perfil de carga específico para estas pruebas será definido por el equipo de desarrollo.
**Prioridad**: Media
**Impacto**: Degradación severa del rendimiento o fallos del sistema bajo alta demanda, resultando en interrupciones del servicio y frustración del usuario.

### RNF-005: Cifrado de Datos Sensibles en Tránsito
**Categoría**: Seguridad
**Descripción**: Toda la comunicación de datos sensibles (ej. información de asegurados, detalles de ubicaciones de riesgo) entre el frontend, el backend y los servicios externos debe estar cifrada. Se proporcionará una lista detallada de los campos considerados sensibles.
**Objetivo**: Proteger la confidencialidad e integridad de la información durante su transmisión a través de redes.
**Métrica**: Todas las comunicaciones entre componentes del sistema y con servicios externos (e.g., `Plataforma-core-ohs`) deben utilizar TLS 1.2 o superior.
**Criterio de Aceptación**: Auditorías de seguridad y análisis de tráfico de red confirman el uso de TLS para todas las comunicaciones que contengan datos sensibles.
**Prioridad**: Alta
**Impacto**: Riesgo de intercepción de datos sensibles, comprometiendo la privacidad, la seguridad de la información y el cumplimiento normativo.

### RNF-006: Cifrado de Datos Sensibles en Reposo
**Categoría**: Seguridad
**Descripción**: Los datos sensibles almacenados en la base de datos deben estar protegidos mediante cifrado. Se proporcionará una lista detallada de los campos considerados sensibles.
**Objetivo**: Prevenir el acceso no autorizado a información confidencial en caso de una brecha de seguridad en la capa de persistencia.
**Métrica**: Los datos personales y financieros almacenados en las colecciones de la base de datos deben ser cifrados utilizando algoritmos estándar de la industria (e.g., AES-256).
**Criterio de Aceptación**: Auditorías de seguridad de la base de datos verifican la implementación y configuración del cifrado para los datos designados como sensibles.
**Prioridad**: Alta
**Impacto**: Exposición de información confidencial en caso de acceso no autorizado a la base de datos, con graves consecuencias legales y de reputación.

### RNF-007: Autenticación y Autorización de Usuarios
**Categoría**: Seguridad
**Descripción**: El sistema debe implementar mecanismos robustos de autenticación para verificar la identidad de los usuarios y de autorización para controlar su acceso a funcionalidades y datos.
**Objetivo**: Restringir el acceso al sistema solo a usuarios legítimos y asegurar que cada usuario solo pueda realizar acciones para las que tiene permiso.
**Métrica**: El sistema debe requerir autenticación interna con gestión de usuarios propia basada en credenciales (ej. usuario/contraseña) y utilizar un mecanismo de autorización basado en roles para el acceso a las funcionalidades del backend.
**Criterio de Aceptación**: Pruebas de penetración y verificación de controles de acceso confirman que solo usuarios autenticados y autorizados pueden acceder a las funciones y datos correspondientes a sus roles.
**Prioridad**: Alta
**Impacto**: Acceso no autorizado a información o funcionalidades, resultando en manipulación de datos, exposición de información confidencial o acciones maliciosas.

### RNF-008: Disponibilidad del Sistema
**Categoría**: Disponibilidad
**Descripción**: El sistema debe estar disponible para los usuarios la mayor parte del tiempo, minimizando las interrupciones no planificadas.
**Objetivo**: Asegurar la continuidad del negocio y la capacidad de los usuarios para acceder al cotizador cuando lo necesiten.
**Métrica**: El sistema (backend y frontend) debe tener un tiempo de actividad (uptime) del 99.5% durante las horas de operación definidas (horario a formalizar), excluyendo las ventanas de mantenimiento planificado. Esto equivale a un máximo de 2.19 horas de inactividad al mes.
**Criterio de Aceptación**: El monitoreo del sistema registra un uptime del 99.5% o superior durante un período de 30 días en un entorno de pre-producción o producción simulada.
**Prioridad**: Alta
**Impacto**: Pérdida de oportunidades de negocio, insatisfacción del cliente y daño a la reputación debido a la inaccesibilidad del servicio.

### RNF-009: Facilidad de Uso y Experiencia de Usuario
**Categoría**: Usabilidad
**Descripción**: La interfaz de usuario debe ser intuitiva, fácil de aprender y eficiente para la captura y visualización de cotizaciones.
**Objetivo**: Minimizar la curva de aprendizaje para los nuevos usuarios y maximizar la productividad de los usuarios experimentados.
**Métrica**: Los usuarios deben poder completar el proceso de creación de una cotización con una ubicación en menos de 5 minutos en su primera interacción sin asistencia.
**Criterio de Aceptación**: Pruebas de usuario con un grupo representativo (ej. 5 agentes de seguros) demuestran que el 80% de los participantes pueden completar la tarea en el tiempo especificado.
**Prioridad**: Media
**Impacto**: Baja adopción del sistema, errores frecuentes por parte del usuario, aumento de la necesidad de capacitación y soporte.

### RNF-010: Cobertura de Pruebas Unitarias
**Categoría**: Mantenibilidad
**Descripción**: El código fuente del backend y frontend debe tener una alta cobertura de pruebas unitarias para asegurar la calidad y facilitar el mantenimiento y la evolución.
**Objetivo**: Reducir la probabilidad de introducir errores en nuevas versiones, facilitar la refactorización y asegurar la robustez del código.
**Métrica**: El código del backend y frontend debe alcanzar una cobertura mínima del 80% en pruebas unitarias.
**Criterio de Aceptación**: Herramientas de análisis de cobertura de código (ej. JaCoCo, Istanbul) reportan una cobertura del 80% o superior para el código entregado, excluyendo código generado o de terceros.
**Prioridad**: Alta
**Impacto**: Mayor riesgo de bugs, dificultad para realizar cambios, aumento del tiempo y costo de mantenimiento, y menor confianza en la estabilidad del sistema.

### RNF-011: Cobertura de Pruebas Automatizadas de Flujos Críticos
**Categoría**: Mantenibilidad
**Descripción**: Se deben implementar pruebas automatizadas para los flujos de negocio más importantes del sistema.
**Objetivo**: Validar la funcionalidad end-to-end de las operaciones críticas y asegurar que no se introduzcan regresiones con nuevos desarrollos.
**Métrica**: Deben existir al menos 3 flujos críticos de negocio cubiertos por pruebas automatizadas de integración o end-to-end. La definición de estos flujos críticos será responsabilidad del equipo de QA/desarrollo.
**Criterio de Aceptación**: Las pruebas automatizadas para los flujos críticos se ejecutan con éxito en cada ciclo de desarrollo y antes de cada despliegue a entornos de prueba.
**Prioridad**: Alta
**Impacto**: Riesgo de fallos en funcionalidades clave después de despliegues, lo que requiere costosas correcciones urgentes y afecta la reputación del sistema.

### RNF-012: Documentación Técnica Completa
**Categoría**: Mantenibilidad
**Descripción**: El proyecto debe contar con documentación técnica exhaustiva y actualizada que describa su diseño, arquitectura y funcionamiento.
**Objetivo**: Facilitar la comprensión, el mantenimiento y la futura evolución del sistema por parte de desarrolladores, arquitectos y evaluadores técnicos.
**Métrica**: Se deben entregar especificaciones ASSD, diagramas de arquitectura (ej. C4 Model), modelo de datos, y descripción detallada de la lógica de cálculo, todos actualizados y coherentes con la implementación.
**Criterio de Aceptación**: La documentación entregada cumple con los requisitos de la metodología ASSD y es revisada y aprobada por el evaluador técnico como completa y clara.
**Prioridad**: Alta
**Impacto**: Dificultad para entender el sistema, lo que ralentiza el desarrollo, aumenta el riesgo de errores y complica la incorporación de nuevos miembros al equipo.

### RNF-013: Compatibilidad con Navegadores Web
**Categoría**: Compatibilidad
**Descripción**: La interfaz de usuario web debe funcionar correctamente en los navegadores más utilizados por los usuarios finales y agentes.
**Objetivo**: Asegurar que todos los usuarios puedan acceder y utilizar el sistema independientemente del navegador que prefieran, sin problemas de visualización o funcionalidad.
**Métrica**: El frontend debe ser compatible con las dos últimas versiones estables de Google Chrome, Mozilla Firefox, Microsoft Edge y Apple Safari.
**Criterio de Aceptación**: Pruebas de compatibilidad en los navegadores especificados demuestran funcionalidad completa y sin errores visuales o de interacción.
**Prioridad**: Media
**Impacto**: Inaccesibilidad del sistema para algunos usuarios, degradación de la experiencia de usuario en navegadores no soportados, lo que puede requerir soporte adicional.

### RNF-014: Integridad de Datos con Versionado Optimista
**Categoría**: Confiabilidad / Integridad de Datos**Descripción**: El sistema debe asegurar la integridad de los datos de cotización, especialmente durante actualizaciones concurrentes, utilizando un mecanismo de control de concurrencia.
**Objetivo**: Prevenir la pérdida de actualizaciones o la corrupción de datos debido a modificaciones simultáneas por diferentes usuarios o procesos.
**Métrica**: Todas las operaciones de edición en folios y ubicaciones deben implementar versionado optimista, rechazando actualizaciones si la versión de los datos ha cambiado desde la última lectura.
**Criterio de Aceptación**: Pruebas de concurrencia demuestran que las actualizaciones concurrentes son gestionadas correctamente, manteniendo la integridad de los datos y notificando al usuario en caso de conflicto con un mensaje específico que sugiera recargar los datos más recientes y re-aplicar sus cambios.
**Prioridad**: Alta
**Impacto**: Corrupción de datos, pérdida de información, inconsistencias en las cotizaciones y resultados financieros incorrectos, lo que puede llevar a disputas y errores operativos.

### RNF-015: Precisión y Trazabilidad de la Lógica de Cálculo
**Categoría**: Precisión
**Descripción**: La lógica de cálculo de primas debe ser consistente, precisa y sus resultados deben ser trazables.
**Objetivo**: Garantizar que las primas calculadas son correctas y que la metodología de cálculo puede ser auditada, comprendida y validada.
**Métrica**: La lógica de cálculo debe producir resultados idénticos para el mismo conjunto de entradas. La documentación debe detallar los parámetros, factores y fórmulas utilizadas, permitiendo la reproducción manual del cálculo.**Criterio de Aceptación**: Pruebas de regresión con datos de entrada conocidos y resultados esperados verifican la precisión del cálculo. La documentación de la lógica de cálculo es completa y validada por un analista funcional.
**Prioridad**: Alta
**Impacto**: Cálculos de prima incorrectos, lo que lleva a errores financieros, insatisfacción del cliente, problemas regulatorios y pérdida de credibilidad.

### RNF-016: Idempotencia en la Creación de Folios
**Categoría**: Confiabilidad
**Descripción**: La operación de creación de folios debe ser idempotente para evitar la duplicación accidental de folios si una solicitud se reintenta (ej. por un problema de red).
**Objetivo**: Asegurar la unicidad de los folios creados, incluso ante fallos de red o reintentos de solicitudes de parte del frontend.
**Métrica**: Múltiples solicitudes de creación del mismo folio (con el mismo identificador de idempotencia) deben resultar en la creación de un único folio.
**Criterio de Aceptación**: Pruebas de idempotencia confirman que los reintentos de creación de folios no generan duplicados en la base de datos.
**Prioridad**: Alta
**Impacto**: Creación de folios duplicados, lo que genera inconsistencias en los datos, problemas operativos y dificultades en la gestión de cotizaciones.

### RNF-017: Resiliencia ante Fallos del Servicio de Referencia
**Categoría**: Disponibilidad / Confiabilidad
**Descripción**: El backend debe ser capaz de manejar interrupciones o latencias elevadas del servicio externo `Plataforma-core-ohs` de manera controlada.
**Objetivo**: Minimizar el impacto en la operación del cotizador si el servicio externo no está disponible o responde lentamente, evitando cascadas de fallos.
**Métrica**: El sistema debe implementar mecanismos de reintento con backoff y/o circuit breaker para las llamadas al `Plataforma-core-ohs`. En caso de fallo prolongado del servicio externo, el sistema debe ofrecer funcionalidad degradada (ej. permitir captura de folio y datos básicos, pero deshabilitar cálculo o consulta de catálogos dinámicos) y mostrar un mensaje de error amigable al usuario en menos de 5 segundos.
**Criterio de Aceptación**: Pruebas de caos o simulación de fallos del servicio `Plataforma-core-ohs` demuestran que el sistema degrada su funcionalidad de manera controlada (ej., muestra mensaje de error amigable, no bloquea todo el sistema) y se recupera automáticamente cuando el servicio vuelve a estar disponible.**Prioridad**: Media
**Impacto**: Fallo completo del cotizador o bloqueo de la interfaz de usuario si el servicio externo no responde, impidiendo la creación o cálculo de cotizaciones.

### RNF-018: Monitoreo y Trazabilidad de Errores
**Categoría**: Mantenibilidad / Operabilidad
**Descripción**: El sistema debe registrar y permitir la trazabilidad de errores y eventos importantes para facilitar la depuración, el monitoreo y la resolución de incidentes.
**Objetivo**: Permitir la rápida identificación y resolución de problemas en producción, minimizando el tiempo de inactividad.
**Métrica**: Todos los errores de aplicación y excepciones no controladas deben ser registrados con detalles suficientes (stack trace, contexto, identificador de usuario) y un identificador de correlación para transacciones. Se implementará un sistema de logging centralizado (ej. ELK Stack, Splunk) para que los logs sean accesibles y consultables.
**Criterio de Aceptación**: El sistema de logs registra los errores de manera consistente y un desarrollador puede usar los logs para identificar la causa raíz de un problema en menos de 15 minutos.
**Prioridad**: Media
**Impacto**: Dificultad para diagnosticar y solucionar problemas en producción, lo que aumenta el tiempo de inactividad, los costos de soporte y afecta la disponibilidad del sistema.

### RNF-019: Gestión de Versiones de Datos de Cotización
**Categoría**: Mantenibilidad / Auditabilidad
**Descripción**: El sistema debe mantener un registro de las versiones de las cotizaciones y sus actualizaciones para permitir la auditoría y el seguimiento del historial.
**Objetivo**: Permitir la auditoría de los cambios realizados en las cotizaciones a lo largo del tiempo y facilitar la reversión o comparación de estados anteriores.
**Métrica**: Cada operación de edición exitosa en una cotización debe incrementar un número de versión (`version`) y actualizar un campo de fecha y hora (`fechaUltimaActualizacion`). Adicionalmente, se considerará la implementación de un historial de cambios para campos críticos.
**Criterio de Aceptación**: Las operaciones de edición de cotizaciones demuestran que los campos de versión y fecha de actualización se actualizan correctamente en la base de datos después de cada modificación.
**Prioridad**: Media
**Impacto**: Dificultad para rastrear el historial de cambios de una cotización, lo que puede generar disputas, problemas de auditoría o incapacidad para recuperar estados anteriores.

## Requerimientos No Funcionales - Sistema Completo

### RNF-001: Tiempo de Respuesta de Interfaz de Usuario
**Categoría**: Rendimiento
**Descripción**: La interfaz web (SPA) debe cargar y responder a las interacciones del usuario de manera fluida y rápida.
**Objetivo**: Proporcionar una experiencia de usuario eficiente y sin frustraciones a los usuarios finales y agentes de seguros.
**Métrica**: El 90% de las interacciones de usuario (clics, cambios de campo, navegación) deben responder en menos de 500 milisegundos. La carga inicial de la página principal del cotizador debe completarse en menos de 2 segundos.
**Criterio de Aceptación**: Pruebas de rendimiento de frontend demuestran cumplimiento con los tiempos especificados en las dos últimas versiones estables de los navegadores Chrome, Firefox, Edge y Safari.
**Prioridad**: Alta
**Impacto**: Baja satisfacción del usuario, pérdida de productividad para agentes y usuarios finales, y posible abandono del sistema.

### RNF-002: Tiempo de Respuesta de Operaciones CRUD
**Categoría**: Rendimiento
**Descripción**: Las operaciones de creación, consulta, edición y persistencia de folios y ubicaciones en el backend deben ejecutarse rápidamente.
**Objetivo**: Garantizar la eficiencia en la gestión de datos de cotizaciones, permitiendo a los usuarios trabajar sin demoras perceptibles.
**Métrica**: Las operaciones de guardar, consultar y editar folios y ubicaciones deben completarse en menos de 1.5 segundos para el 95% de las peticiones.
**Criterio de Aceptación**: Las pruebas de carga y rendimiento del backend confirman los tiempos de respuesta para las operaciones CRUD bajo una carga de 500 usuarios concurrentes.
**Prioridad**: Alta
**Impacto**: Retrasos en el proceso de cotización, afectando la productividad de los usuarios y la percepción de la eficiencia del sistema.

### RNF-003: Tiempo de Cálculo de Prima
**Categoría**: Rendimiento
**Descripción**: El proceso de cálculo de la prima neta, prima comercial y el desglose por ubicación debe ser rápido y eficiente.
**Objetivo**: Ofrecer resultados de cotización de forma casi instantánea a los usuarios, facilitando la toma de decisiones.
**Métrica**: El cálculo completo de la prima para una cotización con hasta 10 ubicaciones debe finalizar en menos de 3 segundos para el 98% de las solicitudes, incluyendo la latencia de servicios externos como `Plataforma-core-ohs` con una tolerancia definida.
**Criterio de Aceptación**: Pruebas de rendimiento del módulo de cálculo demuestran cumplimiento con el tiempo especificado bajo una carga de 100 cálculos concurrentes.
**Prioridad**: Alta
**Impacto**: Demoras significativas en la obtención de cotizaciones, afectando la toma de decisiones, la competitividad y la experiencia del usuario.

### RNF-004: Soporte de Usuarios Concurrentes
**Categoría**: Escalabilidad
**Descripción**: El sistema debe ser capaz de soportar un número creciente de usuarios interactuando simultáneamente sin degradación del rendimiento.
**Objetivo**: Asegurar que el sistema pueda manejar picos de demanda y el crecimiento futuro de la base de usuarios sin afectar la calidad del servicio.
**Métrica**: El sistema debe soportar hasta 500 usuarios concurrentes realizando operaciones de captura y cálculo sin que los tiempos de respuesta excedan los umbrales definidos en RNF-001, RNF-002 y RNF-003.
**Criterio de Aceptación**: Pruebas de carga demuestran que el sistema mantiene el rendimiento objetivo con 500 usuarios concurrentes durante al menos 1 hora. El perfil de carga específico para estas pruebas será definido por el equipo de desarrollo.
**Prioridad**: Media
**Impacto**: Degradación severa del rendimiento o fallos del sistema bajo alta demanda, resultando en interrupciones del servicio y frustración del usuario.

### RNF-005: Cifrado de Datos Sensibles en Tránsito
**Categoría**: Seguridad
**Descripción**: Toda la comunicación de datos sensibles (ej. información de asegurados, detalles de ubicaciones de riesgo) entre el frontend, el backend y los servicios externos debe estar cifrada. Se proporcionará una lista detallada de los campos considerados sensibles.
**Objetivo**: Proteger la confidencialidad e integridad de la información durante su transmisión a través de redes.
**Métrica**: Todas las comunicaciones entre componentes del sistema y con servicios externos (e.g., `Plataforma-core-ohs`) deben utilizar TLS 1.2 o superior.
**Criterio de Aceptación**: Auditorías de seguridad y análisis de tráfico de red confirman el uso de TLS para todas las comunicaciones que contengan datos sensibles.
**Prioridad**: Alta
**Impacto**: Riesgo de intercepción de datos sensibles, comprometiendo la privacidad, la seguridad de la información y el cumplimiento normativo.

### RNF-006: Cifrado de Datos Sensibles en Reposo
**Categoría**: Seguridad
**Descripción**: Los datos sensibles almacenados en la base de datos deben estar protegidos mediante cifrado. Se proporcionará una lista detallada de los campos considerados sensibles.
**Objetivo**: Prevenir el acceso no autorizado a información confidencial en caso de una brecha de seguridad en la capa de persistencia.
**Métrica**: Los datos personales y financieros almacenados en las colecciones de la base de datos deben ser cifrados utilizando algoritmos estándar de la industria (e.g., AES-256).
**Criterio de Aceptación**: Auditorías de seguridad de la base de datos verifican la implementación y configuración del cifrado para los datos designados como sensibles.
**Prioridad**: Alta
**Impacto**: Exposición de información confidencial en caso de acceso no autorizado a la base de datos, con graves consecuencias legales y de reputación.

### RNF-007: Autenticación y Autorización de Usuarios
**Categoría**: Seguridad
**Descripción**: El sistema debe implementar mecanismos robustos de autenticación para verificar la identidad de los usuarios y de autorización para controlar su acceso a funcionalidades y datos.
**Objetivo**: Restringir el acceso al sistema solo a usuarios legítimos y asegurar que cada usuario solo pueda realizar acciones para las que tiene permiso.
**Métrica**: El sistema debe requerir autenticación interna con gestión de usuarios propia basada en credenciales (ej. usuario/contraseña) y utilizar un mecanismo de autorización basado en roles para el acceso a las funcionalidades del backend.
**Criterio de Aceptación**: Pruebas de penetración y verificación de controles de acceso confirman que solo usuarios autenticados y autorizados pueden acceder a las funciones y datos correspondientes a sus roles.
**Prioridad**: Alta
**Impacto**: Acceso no autorizado a información o funcionalidades, resultando en manipulación de datos, exposición de información confidencial o acciones maliciosas.

### RNF-008: Disponibilidad del Sistema
**Categoría**: Disponibilidad
**Descripción**: El sistema debe estar disponible para los usuarios la mayor parte del tiempo, minimizando las interrupciones no planificadas.
**Objetivo**: Asegurar la continuidad del negocio y la capacidad de los usuarios para acceder al cotizador cuando lo necesiten.
**Métrica**: El sistema (backend y frontend) debe tener un tiempo de actividad (uptime) del 99.5% durante las horas de operación definidas (horario a formalizar), excluyendo las ventanas de mantenimiento planificado. Esto equivale a un máximo de 2.19 horas de inactividad al mes.
**Criterio de Aceptación**: El monitoreo del sistema registra un uptime del 99.5% o superior durante un período de 30 días en un entorno de pre-producción o producción simulada.
**Prioridad**: Alta
**Impacto**: Pérdida de oportunidades de negocio, insatisfacción del cliente y daño a la reputación debido a la inaccesibilidad del servicio.

### RNF-009: Facilidad de Uso y Experiencia de Usuario
**Categoría**: Usabilidad
**Descripción**: La interfaz de usuario debe ser intuitiva, fácil de aprender y eficiente para la captura y visualización de cotizaciones.
**Objetivo**: Minimizar la curva de aprendizaje para los nuevos usuarios y maximizar la productividad de los usuarios experimentados.
**Métrica**: Los usuarios deben poder completar el proceso de creación de una cotización con una ubicación en menos de 5 minutos en su primera interacción sin asistencia.
**Criterio de Aceptación**: Pruebas de usuario con un grupo representativo (ej. 5 agentes de seguros) demuestran que el 80% de los participantes pueden completar la tarea en el tiempo especificado.
**Prioridad**: Media
**Impacto**: Baja adopción del sistema, errores frecuentes por parte del usuario, aumento de la necesidad de capacitación y soporte.

### RNF-010: Cobertura de Pruebas Unitarias
**Categoría**: Mantenibilidad
**Descripción**: El código fuente del backend y frontend debe tener una alta cobertura de pruebas unitarias para asegurar la calidad y facilitar el mantenimiento y la evolución.
**Objetivo**: Reducir la probabilidad de introducir errores en nuevas versiones, facilitar la refactorización y asegurar la robustez del código.
**Métrica**: El código del backend y frontend debe alcanzar una cobertura mínima del 80% en pruebas unitarias.
**Criterio de Aceptación**: Herramientas de análisis de cobertura de código (ej. JaCoCo, Istanbul) reportan una cobertura del 80% o superior para el código entregado, excluyendo código generado o de terceros.
**Prioridad**: Alta
**Impacto**: Mayor riesgo de bugs, dificultad para realizar cambios, aumento del tiempo y costo de mantenimiento, y menor confianza en la estabilidad del sistema.

### RNF-011: Cobertura de Pruebas Automatizadas de Flujos Críticos
**Categoría**: Mantenibilidad
**Descripción**: Se deben implementar pruebas automatizadas para los flujos de negocio más importantes del sistema.
**Objetivo**: Validar la funcionalidad end-to-end de las operaciones críticas y asegurar que no se introduzcan regresiones con nuevos desarrollos.
**Métrica**: Deben existir al menos 3 flujos críticos de negocio cubiertos por pruebas automatizadas de integración o end-to-end. La definición de estos flujos críticos será responsabilidad del equipo de QA/desarrollo.
**Criterio de Aceptación**: Las pruebas automatizadas para los flujos críticos se ejecutan con éxito en cada ciclo de desarrollo y antes de cada despliegue a entornos de prueba.
**Prioridad**: Alta
**Impacto**: Riesgo de fallos en funcionalidades clave después de despliegues, lo que requiere costosas correcciones urgentes y afecta la reputación del sistema.

### RNF-012: Documentación Técnica Completa
**Categoría**: Mantenibilidad
**Descripción**: El proyecto debe contar con documentación técnica exhaustiva y actualizada que describa su diseño, arquitectura y funcionamiento.
**Objetivo**: Facilitar la comprensión, el mantenimiento y la futura evolución del sistema por parte de desarrolladores, arquitectos y evaluadores técnicos.
**Métrica**: Se deben entregar especificaciones ASSD, diagramas de arquitectura (ej. C4 Model), modelo de datos, y descripción detallada de la lógica de cálculo, todos actualizados y coherentes con la implementación.
**Criterio de Aceptación**: La documentación entregada cumple con los requisitos de la metodología ASSD y es revisada y aprobada por el evaluador técnico como completa y clara.
**Prioridad**: Alta
**Impacto**: Dificultad para entender el sistema, lo que ralentiza el desarrollo, aumenta el riesgo de errores y complica la incorporación de nuevos miembros al equipo.

### RNF-013: Compatibilidad con Navegadores Web
**Categoría**: Compatibilidad
**Descripción**: La interfaz de usuario web debe funcionar correctamente en los navegadores más utilizados por los usuarios finales y agentes.
**Objetivo**: Asegurar que todos los usuarios puedan acceder y utilizar el sistema independientemente del navegador que prefieran, sin problemas de visualización o funcionalidad.
**Métrica**: El frontend debe ser compatible con las dos últimas versiones estables de Google Chrome, Mozilla Firefox, Microsoft Edge y Apple Safari.
**Criterio de Aceptación**: Pruebas de compatibilidad en los navegadores especificados demuestran funcionalidad completa y sin errores visuales o de interacción.
**Prioridad**: Media
**Impacto**: Inaccesibilidad del sistema para algunos usuarios, degradación de la experiencia de usuario en navegadores no soportados, lo que puede requerir soporte adicional.

### RNF-014: Integridad de Datos con Versionado Optimista
**Categoría**: Confiabilidad / Integridad de Datos**Descripción**: El sistema debe asegurar la integridad de los datos de cotización, especialmente durante actualizaciones concurrentes, utilizando un mecanismo de control de concurrencia.
**Objetivo**: Prevenir la pérdida de actualizaciones o la corrupción de datos debido a modificaciones simultáneas por diferentes usuarios o procesos.
**Métrica**: Todas las operaciones de edición en folios y ubicaciones deben implementar versionado optimista, rechazando actualizaciones si la versión de los datos ha cambiado desde la última lectura.
**Criterio de Aceptación**: Pruebas de concurrencia demuestran que las actualizaciones concurrentes son gestionadas correctamente, manteniendo la integridad de los datos y notificando al usuario en caso de conflicto con un mensaje específico que sugiera recargar los datos más recientes y re-aplicar sus cambios.
**Prioridad**: Alta
**Impacto**: Corrupción de datos, pérdida de información, inconsistencias en las cotizaciones y resultados financieros incorrectos, lo que puede llevar a disputas y errores operativos.

### RNF-015: Precisión y Trazabilidad de la Lógica de Cálculo
**Categoría**: Precisión
**Descripción**: La lógica de cálculo de primas debe ser consistente, precisa y sus resultados deben ser trazables.
**Objetivo**: Garantizar que las primas calculadas son correctas y que la metodología de cálculo puede ser auditada, comprendida y validada.
**Métrica**: La lógica de cálculo debe producir resultados idénticos para el mismo conjunto de entradas. La documentación debe detallar los parámetros, factores y fórmulas utilizadas, permitiendo la reproducción manual del cálculo.**Criterio de Aceptación**: Pruebas de regresión con datos de entrada conocidos y resultados esperados verifican la precisión del cálculo. La documentación de la lógica de cálculo es completa y validada por un analista funcional.
**Prioridad**: Alta
**Impacto**: Cálculos de prima incorrectos, lo que lleva a errores financieros, insatisfacción del cliente, problemas regulatorios y pérdida de credibilidad.

### RNF-016: Idempotencia en la Creación de Folios
**Categoría**: Confiabilidad
**Descripción**: La operación de creación de folios debe ser idempotente para evitar la duplicación accidental de folios si una solicitud se reintenta (ej. por un problema de red).
**Objetivo**: Asegurar la unicidad de los folios creados, incluso ante fallos de red o reintentos de solicitudes de parte del frontend.
**Métrica**: Múltiples solicitudes de creación del mismo folio (con el mismo identificador de idempotencia) deben resultar en la creación de un único folio.
**Criterio de Aceptación**: Pruebas de idempotencia confirman que los reintentos de creación de folios no generan duplicados en la base de datos.
**Prioridad**: Alta
**Impacto**: Creación de folios duplicados, lo que genera inconsistencias en los datos, problemas operativos y dificultades en la gestión de cotizaciones.

### RNF-017: Resiliencia ante Fallos del Servicio de Referencia
**Categoría**: Disponibilidad / Confiabilidad
**Descripción**: El backend debe ser capaz de manejar interrupciones o latencias elevadas del servicio externo `Plataforma-core-ohs` de manera controlada.
**Objetivo**: Minimizar el impacto en la operación del cotizador si el servicio externo no está disponible o responde lentamente, evitando cascadas de fallos.
**Métrica**: El sistema debe implementar mecanismos de reintento con backoff y/o circuit breaker para las llamadas al `Plataforma-core-ohs`. En caso de fallo prolongado del servicio externo, el sistema debe ofrecer funcionalidad degradada (ej. permitir captura de folio y datos básicos, pero deshabilitar cálculo o consulta de catálogos dinámicos) y mostrar un mensaje de error amigable al usuario en menos de 5 segundos.
**Criterio de Aceptación**: Pruebas de caos o simulación de fallos del servicio `Plataforma-core-ohs` demuestran que el sistema degrada su funcionalidad de manera controlada (ej., muestra mensaje de error amigable, no bloquea todo el sistema) y se recupera automáticamente cuando el servicio vuelve a estar disponible.**Prioridad**: Media
**Impacto**: Fallo completo del cotizador o bloqueo de la interfaz de usuario si el servicio externo no responde, impidiendo la creación o cálculo de cotizaciones.

### RNF-018: Monitoreo y Trazabilidad de Errores
**Categoría**: Mantenibilidad / Operabilidad
**Descripción**: El sistema debe registrar y permitir la trazabilidad de errores y eventos importantes para facilitar la depuración, el monitoreo y la resolución de incidentes.
**Objetivo**: Permitir la rápida identificación y resolución de problemas en producción, minimizando el tiempo de inactividad.
**Métrica**: Todos los errores de aplicación y excepciones no controladas deben ser registrados con detalles suficientes (stack trace, contexto, identificador de usuario) y un identificador de correlación para transacciones. Se implementará un sistema de logging centralizado (ej. ELK Stack, Splunk) para que los logs sean accesibles y consultables.
**Criterio de Aceptación**: El sistema de logs registra los errores de manera consistente y un desarrollador puede usar los logs para identificar la causa raíz de un problema en menos de 15 minutos.
**Prioridad**: Media
**Impacto**: Dificultad para diagnosticar y solucionar problemas en producción, lo que aumenta el tiempo de inactividad, los costos de soporte y afecta la disponibilidad del sistema.

### RNF-019: Gestión de Versiones de Datos de Cotización
**Categoría**: Mantenibilidad / Auditabilidad
**Descripción**: El sistema debe mantener un registro de las versiones de las cotizaciones y sus actualizaciones para permitir la auditoría y el seguimiento del historial.
**Objetivo**: Permitir la auditoría de los cambios realizados en las cotizaciones a lo largo del tiempo y facilitar la reversión o comparación de estados anteriores.
**Métrica**: Cada operación de edición exitosa en una cotización debe incrementar un número de versión (`version`) y actualizar un campo de fecha y hora (`fechaUltimaActualizacion`). Adicionalmente, se considerará la implementación de un historial de cambios para campos críticos.
**Criterio de Aceptación**: Las operaciones de edición de cotizaciones demuestran que los campos de versión y fecha de actualización se actualizan correctamente en la base de datos después de cada modificación.
**Prioridad**: Media
**Impacto**: Dificultad para rastrear el historial de cambios de una cotización, lo que puede generar disputas, problemas de auditoría o incapacidad para recuperar estados anteriores.