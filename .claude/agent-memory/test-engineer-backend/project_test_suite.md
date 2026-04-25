---
name: plataforma-core-ohs test suite
description: Test suite written for the mock Node.js/Express server — patterns, caveats, and file locations
type: project
---

Test suite lives in `plataforma-core-ohs/tests/` covering all routes and middleware.

**Stack:** Jest 30 + supertest, CommonJS, no real MongoDB connection.

**Key pattern — FolioCounter mock:** `src/routes/folios.js` defines and registers `FolioCounter` inline using `mongoose.models` / `mongoose.model`. To mock it, jest.mock('mongoose') and expose `_findOneAndUpdateMock` on the returned factory — the route module picks up the mock model at require-time. See `tests/routes/folios.test.js`.

**Key pattern — mockScenarioInterceptor bypass:** Every route test mocks the interceptor as `jest.mock('../../src/middleware/mockScenarioInterceptor', () => (req, res, next) => next())` to prevent DB calls from leaking through middleware.

**Key pattern — DELAY scenario test:** Use `jest.useFakeTimers()` + `jest.runAllTimers()` inside the same test to trigger `setTimeout` synchronously. The interceptor returns the setTimeout result directly so there is no promise to await — just advance timers after calling the middleware.

**Why:** No real MongoDB in CI; all Mongoose model methods are mocked with jest.fn().
**How to apply:** Always mock models at the top of each test file before requiring the router under test.
