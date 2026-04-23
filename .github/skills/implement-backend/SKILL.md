---
name: implement-backend
description: Implementa un feature completo en el backend. Requiere spec con status APPROVED en .github/specs/.
argument-hint: "<nombre-feature>"
---

# Implement Backend

## Prerequisitos
1. Leer spec: `.github/specs/<feature>.spec.md` — sección 2 (modelos, endpoints)
2. Leer stack: `.github/instructions/backend.instructions.md`
3. Leer patrones de referencia: `.github/skills/implement-backend/patterns.java`

## Orden de implementación
```
entity → repository → service → controller → registrar en Spring context
```

| Capa | Responsabilidad |
|------|-----------------|
| **Entity / DTOs** | `@Document` MongoDB + Request/Response DTOs con Bean Validation |
| **Repository** | `MongoRepository` — queries CRUD sin lógica de negocio |
| **Service** | Lógica de negocio pura — orquesta repositorios |
| **Controller** | `@RestController` — parsing HTTP + delegar al service |

## Patrón de DI (obligatorio)
- Inyección por constructor con `@RequiredArgsConstructor` — NUNCA `@Autowired` en campo
- Spring detecta automáticamente el `@RestController` — no hace falta registro manual

Ver patrones específicos del stack en `.github/instructions/backend.instructions.md`.

## Reglas
Ver `.claude/rules/backend.md` — async, naming, errores, timestamps.

## Restricciones
- Solo directorio de backend del proyecto. No tocar frontend.
- No generar tests (responsabilidad de `test-engineer-backend`).
