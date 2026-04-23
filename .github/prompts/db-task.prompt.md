---
description: 'Ejecuta el Database Agent para diseñar esquemas de datos, entidades Spring Data MongoDB, índices y migraciones a partir de la spec aprobada.'
agent: Database Agent
---

Ejecuta el Database Agent para diseñar y gestionar el modelo de persistencia del feature.

**Feature**: ${input:featureName:nombre del feature en kebab-case}

**Instrucciones para @Database Agent:**

1. Lee `.github/instructions/backend.instructions.md` — confirma el motor de BD aprobado (MongoDB + Spring Data)
2. Lee `.github/docs/lineamientos/dev-guidelines.md`
3. Lee la **Sección 2 — DISEÑO — Modelos de Datos** de `.github/specs/${input:featureName}.spec.md`
4. Escanea entidades y repositorios existentes en `plataformas-danos-back/src/main/java/model/entity/` y `repository/`
5. Ejecuta el flujo completo:
   - Diseña o actualiza la entidad con `@Document`: campos, tipos Java, validaciones
   - Genera Request/Response DTOs en `model/dto/`
   - Define índices con `@Indexed` o `@CompoundIndex` justificados en la spec
   - Genera seeder o script de datos de prueba sintéticos si aplica
   - Registra ADR si hay decisiones de diseño relevantes
6. Presenta reporte consolidado de cambios al modelo de datos

**Prerequisito:** Debe existir `.github/specs/${input:featureName}.spec.md` con estado APPROVED y Sección 2 completa. Si no, ejecutar `/generate-spec` primero.

**Nota:** Ejecutar ANTES o en paralelo con el Backend Developer para que los contratos de persistencia estén definidos antes de implementar los repositorios.
