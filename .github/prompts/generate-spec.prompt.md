---
name: generate-spec
description: Genera una especificación técnica ASDD para un nuevo feature. Usa este comando con el nombre e descripción del feature.
argument-hint: "<nombre-feature>: <descripción del requerimiento>"
agent: Spec Generator
tools:
  - edit/createFile
  - read/readFile
  - search/listDirectory
  - search
---

Genera una especificación técnica completa en `.github/specs/` para el siguiente requerimiento.

**Feature**: ${input:featureName:nombre del feature en kebab-case}
**Requerimiento**: ${input:requirement:descripción del requerimiento — o "ver requirements" para cargar desde .github/requirements/}

## Pasos a seguir:

1. **Si el requerimiento no se proporcionó**, busca en `.github/requirements/${input:featureName}.md`. Si existe, úsalo como contexto inicial.
2. Lee el stack: `.github/instructions/backend.instructions.md` y `frontend.instructions.md`.
3. **Consulta la documentación de diseño existente** (fuente primaria de criterios):
   - Lee `docs/02-analysis/FEATURES.md` para localizar el feature (FT-XXX) y su épica (EP-XXX).
   - Si existe, lee `docs/03-design/epicas/{EP-XXX}/core/{FT-XXX}/` — los HU files contienen criterios detallados que deben reflejarse en la spec.
   - Lee `docs/01-requirements/RNF.md` para incorporar SLOs de performance como criterios de aceptación no funcionales.
4. Explora el código existente para identificar patrones, modelos y rutas relacionadas.
5. Genera la spec usando la plantilla en `.github/skills/generate-spec/spec-template.md`.
6. Guarda el archivo como `.github/specs/${input:featureName}.spec.md` con estado `DRAFT`.
7. Confirma la creación con un resumen de la spec al usuario.

## La spec debe cubrir:
- Historias de usuario con criterios de aceptación en Gherkin
- Modelos de datos (entidad `@Document` + DTOs Java con Bean Validation)
- Endpoints de API con request/response y errores (Spring Boot HTTP codes)
- Diseño Frontend (páginas App Router, componentes Tailwind, hooks, stores Zustand, services Axios)
- Plan de pruebas (backlog de tasks Backend + Frontend + QA)
