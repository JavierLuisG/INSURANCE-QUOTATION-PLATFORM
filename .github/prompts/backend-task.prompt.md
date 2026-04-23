---
name: backend-task
description: Implementa una funcionalidad en el backend Java/Spring Boot basada en una spec ASDD aprobada.
argument-hint: "<nombre-feature> (debe existir .github/specs/<nombre-feature>.spec.md)"
agent: Backend Developer
tools:
  - edit/createFile
  - edit/editFiles
  - read/readFile
  - search/listDirectory
  - search
  - execute/runInTerminal
---

Implementa el backend para el feature especificado, siguiendo la spec aprobada.

**Feature**: ${input:featureName:nombre del feature en kebab-case}

## Pasos obligatorios:

1. **Lee la spec** en `.github/specs/${input:featureName:nombre-feature}.spec.md` — si no existe, detente e informa al usuario.
2. **Revisa el código existente** en `plataformas-danos-back/src/main/java/` para entender patrones actuales.
3. **Implementa en orden**:
   - `model/entity/` — entidad con `@Document`
   - `model/dto/` — Request y Response DTOs con Bean Validation
   - `repository/` — interfaz `MongoRepository`
   - `service/` — interfaz + implementación con lógica de negocio
   - `controller/` — `@RestController` con `@RequiredArgsConstructor`
4. Spring detecta los `@Component` automáticamente — no hace falta registro manual.
5. **Verifica compilación** ejecutando: `cd plataformas-danos-back && mvn compile -q`

## Restricciones:
- Inyección por constructor con `@RequiredArgsConstructor` — NUNCA `@Autowired` en campo.
- NO inyectar repositorios directamente en controllers — siempre a través del service.
- Folio de cotización: formato `COT-AAAA-NNNNNN`.
- Valores válidos `estadoValidacion`: `COMPLETA` | `INCOMPLETA` | `INACTIVA`.
- Timestamps: `Instant.now()` en la app, nunca en el cliente.
