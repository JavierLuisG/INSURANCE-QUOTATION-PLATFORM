---
name: generate-tests
description: Genera pruebas unitarias para backend (JUnit 5) y/o frontend (Vitest) en paralelo, basadas en la spec ASDD y el código implementado.
argument-hint: "<nombre-feature> [--backend] [--frontend] (por defecto genera ambos en paralelo)"
agent: Orchestrator
tools:
  - edit/createFile
  - edit/editFiles
  - read/readFile
  - search/listDirectory
  - search
  - execute/runInTerminal
---

Genera pruebas unitarias completas para el feature especificado.

**Feature**: ${input:featureName:nombre del feature en kebab-case}
**Scope**: ${input:scope:backend, frontend, o ambos en paralelo (default)}

## Pasos obligatorios:

1. **Lee la spec** en `.github/specs/${input:featureName:nombre-feature}.spec.md` — sección "Plan de Pruebas Unitarias".
2. **Si scope es "ambos"**: lanza en paralelo `Test Engineer Backend` + `Test Engineer Frontend`.
3. **Si scope es "backend"**: delega a `Test Engineer Backend`:
   - `plataformas-danos-back/src/test/java/controller/${input:featureName}ControllerTest.java`
   - `plataformas-danos-back/src/test/java/service/${input:featureName}ServiceTest.java`
   - `plataformas-danos-back/src/test/java/repository/${input:featureName}RepositoryIT.java`
4. **Si scope es "frontend"**: delega a `Test Engineer Frontend`:
   - `cotizador-danos-web/__tests__/components/[Feature].test.tsx`
   - `cotizador-danos-web/__tests__/hooks/use[Feature].test.ts`
   - `cotizador-danos-web/__tests__/pages/[Feature]Page.test.tsx`
5. **Verifica** que los tests corren:
   - Backend: `cd plataformas-danos-back && mvn test -pl . -Dtest=*Test,*IT`
   - Frontend: `cd cotizador-danos-web && npx vitest run`

## Cobertura obligatoria por test:
- ✅ Happy path (flujo exitoso)
- ❌ Error path (excepciones, errores de red, datos inválidos)
- 🔲 Edge cases (campos vacíos, duplicados, permisos)

## Restricciones:
- Cada test debe ser independiente (no compartir estado).
- Backend unitarios: mockear SIEMPRE con Mockito — nunca DB real.
- Backend integración: usar Testcontainers con MongoDB real.
- Frontend: mockear servicios con `vi.mock()` y/o MSW — nunca llamadas HTTP reales.
- Cobertura mínima ≥ 80% global; ≥ 90% en módulo `calc-engine`.
