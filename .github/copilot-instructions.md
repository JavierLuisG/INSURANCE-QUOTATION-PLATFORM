# Copilot Instructions

## ASDD Workflow (Agent Spec Software Development)

Este repositorio sigue el flujo **ASDD**: toda funcionalidad nueva se ejecuta en cuatro fases orquestadas por agentes especializados.

```
[Orchestrator] → [Spec Generator] → [Backend ∥ Frontend ∥ DB] → [Tests BE ∥ Tests FE] → [QA] → [Doc]
```

### Fases del flujo ASDD
1. **Spec**: El agente `spec-generator` genera la spec en `.github/specs/<feature>.spec.md`.
2. **Implementación (paralelo)**: `backend-developer` + `frontend-developer` + `database-agent` (si hay cambios de DB).
3. **Tests (paralelo)**: `test-engineer-backend` + `test-engineer-frontend`.
4. **QA**: `qa-agent` genera estrategia, Gherkin, riesgos y análisis de performance.
5. **Doc (opcional)**: `documentation-agent` genera README updates, API docs y ADRs.

### Skills disponibles (slash commands):
- `/asdd-orchestrate` — orquesta el flujo completo ASDD o consulta estado
- `/generate-spec` — genera spec técnica en `.github/specs/`
- `/implement-backend` — implementa feature completo en el backend
- `/implement-frontend` — implementa feature completo en el frontend
- `/unit-testing` — genera suite de tests (backend + frontend)
- `/gherkin-case-generator` — casos Given-When-Then + datos de prueba
- `/risk-identifier` — clasificación de riesgos ASD (Alto/Medio/Bajo)
- `/automation-flow-proposer` — propuesta de automatización con ROI
- `/performance-analyzer` — planificación de pruebas de performance

### Requerimientos y Specs
- Los requerimientos de negocio viven en `.github/requirements/`. Son la entrada al pipeline ASDD.
- Las specs técnicas viven en `.github/specs/`. Cada spec es la fuente de verdad para implementar.
- Antes de implementar cualquier desarrollo, debe existir una spec aprobada en `.github/specs/`.
- Flujo: `requirements/<feature>.md` → `/generate-spec` → `specs/<feature>.spec.md` (APPROVED)

---

## Mapa de Archivos ASDD

### Agentes
| Agente | Fase | Ruta |
|---|---|---|
| Orchestrator | Entry point | `.github/agents/orchestrator.agent.md` |
| Spec Generator | Fase 1 | `.github/agents/spec-generator.agent.md` |
| Backend Developer | Fase 2 | `.github/agents/backend-developer.agent.md` |
| Frontend Developer | Fase 2 | `.github/agents/frontend-developer.agent.md` |
| Database Agent | Fase 2 | `.github/agents/database.agent.md` |
| Test Engineer Backend | Fase 3 | `.github/agents/test-engineer-backend.agent.md` |
| Test Engineer Frontend | Fase 3 | `.github/agents/test-engineer-frontend.agent.md` |
| QA Agent | Fase 4 | `.github/agents/qa.agent.md` |
| Documentation Agent | Fase 5 | `.github/agents/documentation.agent.md` |

### Skills
| Skill | Agente | Ruta |
|---|---|---|
| `/asdd-orchestrate` | Orchestrator | `.github/skills/asdd-orchestrate/SKILL.md` |
| `/generate-spec` | Spec Generator | `.github/skills/generate-spec/SKILL.md` |
| `/implement-backend` | Backend Developer | `.github/skills/implement-backend/SKILL.md` |
| `/implement-frontend` | Frontend Developer | `.github/skills/implement-frontend/SKILL.md` |
| `/unit-testing` | Test Engineer Backend + Frontend | `.github/skills/unit-testing/SKILL.md` |
| `/gherkin-case-generator` | QA Agent | `.github/skills/gherkin-case-generator/SKILL.md` |
| `/risk-identifier` | QA Agent | `.github/skills/risk-identifier/SKILL.md` |
| `/automation-flow-proposer` | QA Agent | `.github/skills/automation-flow-proposer/SKILL.md` |
| `/performance-analyzer` | QA Agent | `.github/skills/performance-analyzer/SKILL.md` |

### Instructions (path-scoped)
| Scope | Ruta | Se aplica a |
|---|---|---|
| Backend | `.github/instructions/backend.instructions.md` | `plataformas-danos-back/src/main/java/**/*.java` |
| Frontend | `.github/instructions/frontend.instructions.md` | `cotizador-danos-web/**/*.{ts,tsx}` |
| Tests | `.github/instructions/tests.instructions.md` | `plataformas-danos-back/src/test/java/**/*.java` · `cotizador-danos-web/**/*.{test,spec}.{ts,tsx}` |

### Lineamientos y Contexto
| Documento | Ruta |
|---|---|
| Lineamientos de Desarrollo | `.github/docs/lineamientos/dev-guidelines.md` |
| Lineamientos QA | `.github/docs/lineamientos/qa-guidelines.md` |
| Stack Backend + Arquitectura + Naming | `.github/instructions/backend.instructions.md` |
| Stack Frontend + Naming | `.github/instructions/frontend.instructions.md` |

### Lineamientos generales para todos los agentes
- **Reglas de Oro**: ver `.github/AGENTS.md` — rigen TODAS las interacciones.
- **Specs activas**: `.github/specs/` — consultar siempre antes de implementar.

---

## Reglas de Oro

> Principio rector: todas las contribuciones de la IA deben ser seguras, transparentes, con propósito definido y alineadas con las instrucciones explícitas del usuario.

### I. Integridad del Código y del Sistema
- **No código no autorizado**: no escribir, generar ni sugerir código nuevo a menos que el usuario lo solicite explícitamente.
- **No modificaciones no autorizadas**: no modificar, refactorizar ni eliminar código, archivos o estructuras existentes sin aprobación explícita.
- **Preservar la lógica existente**: respetar los patrones arquitectónicos, el estilo de codificación y la lógica operativa existentes del proyecto.

### II. Clarificación de Requisitos
- **Clarificación obligatoria**: si la solicitud es ambigua, incompleta o poco clara, detenerse y solicitar clarificación antes de proceder.
- **No realizar suposiciones**: basar todas las acciones estrictamente en información explícita provista por el usuario.

### III. Transparencia Operativa
- **Explicar antes de actuar**: antes de cualquier acción, explicar qué se hará y posibles implicaciones.
- **Detención ante la incertidumbre**: si surge inseguridad o conflicto con estas reglas, detenerse y consultar al usuario.
- **Acciones orientadas a un propósito**: cada acción debe ser directamente relevante para la solicitud explícita.

---

## Diccionario de Dominio

Términos canónicos a usar en specs, código y mensajes:

| Término | Definición | Sinónimos rechazados |
|---------|-----------|---------------------|
| **Cotización** (`quotation`) | Solicitud formal de precio para una póliza de seguro de daños | Presupuesto, oferta, propuesta |
| **Folio** (`folio`) | Identificador único de cotización con formato `COT-AAAA-NNNNNN` | ID, número, código, `_id` |
| **Cobertura** (`coverage`) | Garantía específica incluida en la cotización | Protección, garantía, amparo |
| **Prima** (`premium`) | Monto a pagar por la póliza de seguro | Costo, precio, tarifa |
| **Riesgo** (`risk`) | Bien o situación objeto de aseguramiento | Activo, objeto, propiedad |
| **Cliente** (`client`) | Persona o empresa que solicita la cotización | Persona, tomador, asegurado |
| **Estado de validación** (`estadoValidacion`) | Estado del proceso: `COMPLETA` \| `INCOMPLETA` \| `INACTIVA` | Estado, status, etapa |
| **Token JWT** (`token`) | Token de autenticación en header `Authorization: Bearer` | Contraseña, sesión, idToken |
| **Simulador** (`plataforma-core-ohs`) | Mock server de Plataforma-core-ohs para dev/test | Mock, stub, fake API |
| `createdAt` | Timestamp de creación en UTC (camelCase Java/JSON) | `created_at`, fecha alta |
| `updatedAt` | Timestamp de última actualización en UTC | `updated_at`, fecha modificación |

**Reglas:** `folio` = identificador de negocio público (nunca exponer `_id` de MongoDB). `estadoValidacion` solo acepta los 3 valores definidos. Timestamps en camelCase (Java). Auth siempre via JWT Bearer.

---

## Mapa de Documentación del Proyecto

La documentación de análisis y diseño vive en `docs/` (raíz del proyecto). **Consultar antes de generar specs o implementar** — es la fuente de verdad de dominio, diseño y restricciones de calidad.

| Directorio | Contenido | Cuándo leer |
|---|---|---|
| `docs/00-context/` | Contexto estratégico, drivers arquitectónicos, dependencias entre módulos | Antes de diseñar arquitectura o resolver integración con `plataforma-core-ohs` |
| `docs/01-requirements/` | RF (25+ requisitos funcionales), RNF (11 requisitos no funcionales), Matriz de riesgos | Para validar criterios de aceptación y SLOs de performance |
| `docs/02-analysis/` | Épicas (`EPICAS.md`), Features (`FEATURES.md`), Historias de usuario (`HU.md`) | Antes de generar specs — identificar el FT-XXX, EP-XXX y HUs relacionadas |
| `docs/03-design/` | Specs detalladas por épica/feature/HU (`03-design/epicas/{EP}/{FT}/{HU}`) | Fuente primaria para spec-generator e implementación |

### Archivos de referencia críticos

| Archivo | Propósito |
|---|---|
| `docs/00-context/DRIVERS_ARQUITECTURA.md` | Atributos de calidad QAS, escenarios arquitectónicos, restricciones técnicas |
| `docs/00-context/DEPENDENCIAS.md` | Integración con `plataforma-core-ohs`, cascade scenarios, SLOs, anti-patterns resueltos |
| `docs/01-requirements/RNF.md` | SLOs: UI <500ms · CRUD <1.5s · cálculo <3s · cobertura ≥80% global / ≥90% `calc-engine` |
| `docs/02-analysis/FEATURES.md` | 22 features (FT-001..FT-022) mapeadas a épicas — usar para identificar el FT antes de generar |
| `TECH_STACK.md` | **Versiones exactas** de todas las dependencias del proyecto (fuente de verdad para versiones) |

---

## Project Overview

> Ver `README.md` en la raíz del proyecto.
