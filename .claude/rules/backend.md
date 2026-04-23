---
description: Reglas de backend para este proyecto (Java 21 + Spring Boot 4.0.4 + MongoDB). Se aplica automáticamente a archivos backend.
paths:
  - "plataformas-danos-back/**"
---

# Reglas de Backend — Java 21 + Spring Boot 4.0.4

## Stack aprobado

- **Java 21 LTS** + **Spring Boot 4.0.4** (Maven, JAR)
- **Spring Data MongoDB** — repositorios NoSQL de documentos
- **Spring Security** + **JJWT 0.12.6** — autenticación y autorización JWT
- **Resilience4j 2.3.0** — Circuit Breaker y Retry
- **Caffeine 3.2.0** — caché en memoria con TTL configurable (`spring-boot-starter-cache`)
- **Spring AOP** — resiliencia y auditoría transversal
- **Lombok** — reducción de boilerplate
- **Bean Validation** — validaciones de entrada (`@Valid`, `@NotNull`, etc.)

**Prohibido:** FastAPI, Python, PyMongo, SQLAlchemy, bases de datos relacionales (PostgreSQL, MySQL, SQLite), Firebase Admin SDK.

## Arquitectura en Capas

```
Controller → Service → Repository → MongoDB
```

| Capa | Responsabilidad | Prohibido |
|------|----------------|-----------|
| `controller/` | Parsear HTTP, delegar al service, manejar respuestas | Lógica de negocio, queries a MongoDB |
| `service/` | Reglas de negocio, validaciones de dominio, orquestar repos | Queries MongoDB directas |
| `repository/` | Queries MongoDB via Spring Data (`MongoRepository`) | Lógica de negocio |
| `model/entity/` | Documentos MongoDB (`@Document`) | Lógica de negocio, acceso a DB |
| `model/dto/` | Request / Response DTOs (Lombok + Bean Validation) | Lógica de negocio, acceso a DB |
| `config/` | Beans de configuración (caché, seguridad, Resilience4j) | Lógica de negocio |
| `security/` | Filtros JWT, `UserDetailsService`, `SecurityFilterChain` | Lógica de negocio |

## Inyección de Dependencias (patrón obligatorio)

Usar **inyección por constructor** — es la única forma aprobada. Lombok `@RequiredArgsConstructor` simplifica el boilerplate.

```java
// Correcto — constructor injection con Lombok
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationResponse> create(@Valid @RequestBody QuotationRequest request) {
        return ResponseEntity.ok(quotationService.create(request));
    }
}
```

NUNCA usar `@Autowired` en campo (field injection). NUNCA instanciar services/repositories con `new` fuera del contexto de Spring.

## Convenciones de Código

- `PascalCase` para clases; `camelCase` para métodos, variables y campos
- API versionada: `/api/v1/...`
- Colecciones MongoDB en camelCase o kebab-case según `@Document(collection = "...")`
- Timestamps: `Instant createdAt` / `Instant updatedAt` gestionados en la app
- Folio de cotización: formato `COT-AAAA-NNNNNN`
- Valores válidos `estadoValidacion`: `COMPLETA` | `INCOMPLETA` | `INACTIVA`
- Formato de log: JSON estructurado con `correlation-id`
- Formato de error consistente: `{ "message": "<mensaje>", "code": "<código>" }`

## Nomenclatura de Archivos

| Artefacto | Convención | Ejemplo |
|-----------|-----------|---------|
| Controller | `<Feature>Controller.java` | `QuotationController.java` |
| Service (interfaz) | `<Feature>Service.java` | `QuotationService.java` |
| Service (impl) | `<Feature>ServiceImpl.java` | `QuotationServiceImpl.java` |
| Repository | `<Feature>Repository.java` | `QuotationRepository.java` |
| Entidad | `<Feature>.java` en `model/entity/` | `Quotation.java` |
| DTO Request | `<Feature>Request.java` | `QuotationRequest.java` |
| DTO Response | `<Feature>Response.java` | `QuotationResponse.java` |
| Test | `<Feature><Layer>Test.java` | `QuotationServiceTest.java` |

## Cobertura y Tests

- **JUnit 5** + **Mockito** + **AssertJ** — tests unitarios
- **Testcontainers** (`testcontainers-bom:1.20.4` + `mongodb`) — tests de integración con MongoDB real
- **JaCoCo** — quality gate: ≥ 80% global, ≥ 90% módulo `calc-engine`

## Anti-patrones Prohibidos

- Lógica de negocio en controllers
- Queries MongoDB en services (van en repositories)
- Field injection con `@Autowired`
- Singletons globales con estado mutable
- Exposición de stack traces en respuestas públicas
- IDs internos de MongoDB (`_id`) en respuestas API (mapear a campo explícito)
- Credenciales hardcodeadas en código
- Acceso síncrono bloqueante en contextos reactivos

## Lineamientos completos

`.claude/docs/lineamientos/dev-guidelines.md` — Clean Code, SOLID, API REST, Seguridad, Observabilidad.
