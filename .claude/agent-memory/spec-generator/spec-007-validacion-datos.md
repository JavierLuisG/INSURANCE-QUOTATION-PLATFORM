---
name: SPEC-007 FT-021 — Data Validation Layer
description: Generated 2026-04-27 — comprehensive validation framework for master data consistency
type: project
---

## Summary

Generated SPEC-007 for feature `ep-003-ft-021-core-validacion-datos` (FT-021 — Data Validation and Inconsistency Management). This spec covers a transversal validation layer for master data (catalogs, tarifas, zip codes) with:

- **Three HUs**: HU-100 (validation), HU-101 (inconsistency recording), HU-102 (critical alerts)
- **Six business rules**: BR-001 (NOT_NULL), BR-002 (positivity), BR-003 (postal format), BR-004 (states), BR-005 (severity levels), BR-006 (audit logging)
- **Two MongoDB collections**: `data-inconsistencies` (with 90-day TTL) and `validation-rules`
- **Four API endpoints**: POST /validate, GET /inconsistencies, GET /rules, POST/PUT /rules (admin)
- **Transversal architecture**: Integration points in CatalogsServiceImpl, TariffsServiceImpl, ZipCodeServiceImpl

**SPEC ID assigned**: SPEC-007 (following SPEC-001 through SPEC-006)

## Key Design Decisions

1. **Synchronous validation**: Validates immediately after receiving external data (not async) to ensure cache consistency
2. **Severity-based handling**: CRITICAL/ERROR blocks; WARNING logs but allows processing
3. **Cacheable rules**: ValidationRule cached in memory (Caffeine, 1h TTL) — changes require restart or manual invalidation
4. **Filtering at source**: Backend filters INCONSISTENT records before exposing to frontend (transparent to UI)
5. **Strategy pattern extensibility**: New validation types addable via new ValidationRule implementations

## Related Specs

- SPEC-001: Mock server base
- SPEC-003: Catalogs (subscribers, agents, lines)
- SPEC-004: Zip codes
- SPEC-005: Risk classification
- SPEC-006: Tariffs (CAT, Fire, Electronic Equipment)

FT-021 adds the validation/quality layer after all upstream data integrations.

## Implementation Scope

**Backend only** (for SPEC-007):
- DataValidationService + Engine
- DataInconsistency + ValidationRule repositories
- DataValidationController + DTOs
- Integration in existing catalog/tariff/zipcode services
- 25+ test cases targeting ≥90% coverage

**Frontend**: None (validations are transparent, backend filters results)
