---
name: Fallback exception types — each service throws its own specific exception
description: ZipCodeServiceImpl fallback throws ZipCodeServiceUnavailableException; TariffsServiceImpl fallback throws TariffServiceUnavailableException — NOT CatalogServiceUnavailableException
type: feedback
---

Each service has its own "unavailable" exception class:
- `CatalogsServiceImpl` → `CatalogServiceUnavailableException`
- `ZipCodeServiceImpl` → `ZipCodeServiceUnavailableException`
- `TariffsServiceImpl` → `TariffServiceUnavailableException`

**Why:** The GlobalExceptionHandler maps each to 503 with a distinct `code` field in the JSON body. Asserting the wrong exception type in tests would let real bugs slip through.

**How to apply:** Always check the actual fallback method implementation to confirm the exception type thrown. Do not assume all fallbacks throw `CatalogServiceUnavailableException`.
