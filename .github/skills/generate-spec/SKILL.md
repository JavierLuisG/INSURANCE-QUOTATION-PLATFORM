---
name: generate-spec
description: Genera una spec técnica ASDD en .github/specs/<feature>.spec.md. Obligatorio antes de cualquier implementación.
argument-hint: "<nombre-feature>: <descripción del requerimiento>"
---

# Generate Spec

## Definition of Ready — validar antes de generar

Una historia puede generar spec solo si cumple:

- [ ] Estructura **Como / Quiero / Para que** completa
- [ ] Términos canónicos del dominio (ver `CLAUDE.md` / `copilot-instructions.md` → Diccionario de Dominio)
- [ ] Criterios BDD: **Dado / Cuando / Entonces** (feliz + validaciones + errores)
- [ ] Contrato API explícito si aplica (método, ruta `/api/v1/...`, request, response, códigos HTTP)
- [ ] Alineada con arquitectura y stack (Java 21 + Spring Boot + MongoDB + Next.js 14 + Tailwind)
- [ ] Dependencias y riesgos identificados

Si el requerimiento no cumple el DoR → listar las preguntas pendientes antes de generar.

## Proceso

1. Busca requerimiento en `.github/requirements/<feature>.md` (si existe, úsalo como contexto inicial)
2. Lee el stack: `.github/instructions/backend.instructions.md`, `frontend.instructions.md`
3. **Consulta la documentación de diseño existente** (fuente primaria):
   - Lee `docs/02-analysis/FEATURES.md` — localiza el feature (FT-XXX) y su épica (EP-XXX)
   - Lee `docs/03-design/epicas/{EP-XXX}/core/{FT-XXX}/` — specs detalladas por HU: son la fuente de criterios de aceptación
   - Lee `docs/01-requirements/RNF.md` — incorpora los SLOs de performance como criterios de aceptación no funcionales
4. Explora código existente — no duplicar modelos ni endpoints existentes
5. Valida DoR (arriba) — si hay ambigüedades, lista preguntas antes de continuar
6. Usa plantilla: `.github/skills/generate-spec/spec-template.md` EXACTAMENTE
7. Guarda en `.github/specs/<nombre-en-kebab-case>.spec.md`

## Frontmatter obligatorio

```yaml
---
id: SPEC-###
status: DRAFT
feature: nombre-del-feature
created: YYYY-MM-DD
updated: YYYY-MM-DD
author: spec-generator
version: "1.0"
related-specs: []
---
```

## Secciones obligatorias

- `## 1. REQUERIMIENTOS` — HU (Como/Quiero/Para) + criterios Gherkin + reglas de negocio
- `## 2. DISEÑO` — modelos de datos, endpoints API (request/response/HTTP codes), frontend
- `## 3. LISTA DE TAREAS` — checklists backend `[ ]`, frontend `[ ]`, QA `[ ]`

## Restricciones

- Solo leer + crear. No modificar código existente.
- Status siempre `DRAFT`. El usuario aprueba antes de implementar.
