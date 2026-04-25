---
name: plataforma-core-ohs mock server implementado
description: Mock Node.js/Express para EP-003 FT-020, estructura src/ completa con 10 modelos, 9 migraciones y 14 endpoints
type: project
---

El módulo `plataforma-core-ohs/` fue implementado completamente como mock server para simular el servicio externo `Plataforma-core-ohs`.

**Why:** Permite desarrollo paralelo de FT-015 a FT-019 y EP-001/EP-002 sin depender del servicio real.

**How to apply:** Al trabajar en features del backend Spring Boot que consumen Plataforma-core-ohs, apuntar a `http://localhost:3001` en desarrollo local.

Estructura implementada:
- `src/config/database.js` — conexión Mongoose
- `src/models/` — 10 modelos: Subscriber, Agent, BusinessLine, ZipCode, RiskClassification, Guarantee, TariffFire, TariffCat, TariffElectronicEquipment, MockScenario
- `src/middleware/mockScenarioInterceptor.js` — intercepta DELAY/HTTP_ERROR/MALFORMED_DATA/NORMAL
- `src/routes/` — 8 routers cubriendo 14 endpoints bajo `/v1/` y `/_mock/`
- `src/migrations/` — migrate-mongo-config.js + V1 a V9
- `src/index.js` — entry point: conecta DB, corre migraciones, levanta Express en PORT=3001
- `.env.example` — PORT=3001, MONGODB_URI, NODE_ENV

Folio pattern: `COT-AAAA-NNNNNN` vía contador atómico `findOneAndUpdate` + `$inc` en colección `folioCounters`.
