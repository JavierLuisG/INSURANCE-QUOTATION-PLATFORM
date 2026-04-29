---
name: Controller test pattern — plain MockitoExtension vs WebMvcTest
description: This project uses @ExtendWith(MockitoExtension.class)+@InjectMocks for all controller tests; @WebMvcTest tests are kept in separate *MockMvcTest.java files
type: feedback
---

This codebase uses `@ExtendWith(MockitoExtension.class)` + `@InjectMocks` for all controller tests (verifying HTTP status via `ResponseEntity` return value, not MockMvc). When the spec requires `@WebMvcTest` tests with JSON body assertions, create **separate** `*MockMvcTest.java` files alongside the existing ones — do NOT replace or modify existing controller tests.

**Why:** The existing tests were accepted and passing; rewriting them as WebMvcTest would break existing green test suite and require loading the full Spring MVC context unnecessarily for tests that already cover behaviour.

**How to apply:** When a spec mandates WebMvcTest tests, create `<ControllerName>MockMvcTest.java` in the same package. Use `@WebMvcTest(<Controller>.class)` + `@Import({SecurityConfig.class, GlobalExceptionHandler.class})` + `@MockitoBean` (not `@MockBean`) — the latter is the Spring Boot 4.x API.
