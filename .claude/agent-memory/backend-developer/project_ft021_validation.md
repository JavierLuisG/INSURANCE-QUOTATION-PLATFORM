---
name: FT-021 — Validación y Gestión de Inconsistencias implementada
description: Capa de validación de datos maestros en plataformas-danos-back, paquetes nuevos creados y artefactos añadidos a paquetes existentes
type: project
---

FT-021 implementado en `plataformas-danos-back`. Status spec: IN_PROGRESS.

**Nuevos paquetes creados:**
- `model/entity/` — DataInconsistency, ValidationRule, CorrectionRule
- `repository/` — DataInconsistencyRepository, ValidationRuleRepository, CorrectionRuleRepository

**Archivos añadidos a paquetes existentes:**
- `model/dto/` — ValidationRequest, ValidationResult, ValidationErrorDetail, CorrectionResult, DataInconsistencyResponse
- `service/` — DataValidationEngine/Impl, DataCorrectionService/Impl, InconsistencyNotificationService/Impl, DataValidationService/Impl
- `config/` — ValidationProperties, ValidationRulesConfig (CommandLineRunner seed)
- `controller/` — DataValidationController (`/api/v1/data-validation`)
- `exception/` — ValidationException, InvalidRuleException, InconsistencyRecordException

**Reglas de negocio implementadas:** NOT_NULL, NOT_EMPTY, POSITIVE_NUMBER, FORMAT_REGEX para SUBSCRIBER, AGENT, BUSINESS_LINE, ZIP_CODE, TARIFF_CAT, TARIFF_FIRE, TARIFF_ELECTRONIC.

**Corrección automática:** TRIM en campos descriptivos de nombre/ciudad/estado, DEFAULT_VALUE y NORMALIZE_CASE disponibles.

**application.yaml:** propiedad `validation.inconsistency-threshold-percent` añadida (default 10).

**Pendiente (fuera de scope de esta tarea):**
- Integración en CatalogsServiceImpl, TariffsServiceImpl, ZipCodeServiceImpl
- Extensión de DTOs existentes con campo dataStatus
- Caché Caffeine explícita para ValidationRule

**Why:** Spec SPEC-007 aprobada, implementación síncrona por decisión de diseño.
**How to apply:** Al tocar validaciones o agregar nuevos tipos de datos maestros, agregar reglas en ValidationRulesConfig y seguir el mismo patrón de servicio.
