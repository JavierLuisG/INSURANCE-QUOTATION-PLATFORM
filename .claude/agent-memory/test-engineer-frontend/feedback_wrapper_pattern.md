---
name: Patrón wrapper para componentes con React Hook Form
description: Componentes que reciben control/errors de RHF requieren un wrapper de prueba con useForm
type: feedback
---

Los componentes `AseguradoFields`, `ParametrosSelector` y `VigenciaFields` reciben `control` y `errors` desde el componente padre vía React Hook Form. No se pueden renderizar solos en tests sin un wrapper que instancie `useForm`.

**Why:** intentar renderizar directamente lanzaría errores de contexto de RHF.

**How to apply:** crear siempre un `<ComponenteWrapper>` local en el archivo de test que use `useForm` con `zodResolver(datosGeneralesSchema)` y valores por defecto, luego pase `control` y `errors` al componente bajo prueba. El wrapper puede aceptar un prop `onSubmit` para capturar las llamadas al servicio.
