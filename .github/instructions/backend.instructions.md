---
applyTo: "plataformas-danos-back/src/main/java/**/*.java"
---

> **Scope**: Se aplica al módulo `plataformas-danos-back`. Stack: Java 21 + Spring Boot 4.0.4 + Spring Data MongoDB + Spring Security + JJWT.

# Instrucciones para Archivos de Backend (Java / Spring Boot)

## Arquitectura en Capas

Siempre sigue la arquitectura en capas del proyecto:

```
Controller → Service → Repository → MongoDB
```

- **`controller/`**: Solo parsear HTTP + delegar al service. Sin lógica de negocio.
- **`service/`**: Solo lógica de negocio y validaciones de dominio. Orquesta repositorios.
- **`repository/`**: Único lugar con acceso a MongoDB vía `MongoRepository`.
- **`model/entity/`**: Documentos MongoDB anotados con `@Document`.
- **`model/dto/`**: Request y Response DTOs con validaciones Bean Validation.

## Inyección de Dependencias (patrón obligatorio)

Usar **inyección por constructor** con `@RequiredArgsConstructor` de Lombok. NUNCA field injection (`@Autowired` en campo).

```java
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/quotations")
public class QuotationController {

    private final QuotationService quotationService;

    @PostMapping
    public ResponseEntity<QuotationResponse> create(@Valid @RequestBody QuotationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(quotationService.create(request));
    }
}
```

## Convenciones de Código

- `PascalCase` para clases; `camelCase` para métodos, variables y campos.
- `snake_case` NUNCA para nombres Java — solo para colecciones MongoDB en `@Document`.
- API versionada: `/api/v1/...`
- Folio de cotización: formato `COT-AAAA-NNNNNN`.
- Valores válidos `estadoValidacion`: `COMPLETA` | `INCOMPLETA` | `INACTIVA`.
- Timestamps: `Instant createdAt` / `Instant updatedAt` generados en la app con `Instant.now()`.
- Logs en JSON estructurado con `correlation-id`.
- Errores: `{ "message": "<mensaje>", "code": "<código>" }` — sin stack traces al cliente.

## Nuevos Endpoints / Controllers

Para agregar un nuevo endpoint:
1. Crear el `@RestController` en `controller/`
2. Crear la interfaz `@Service` y su implementación en `service/`
3. Crear el `@Repository` extendiendo `MongoRepository` en `repository/`
4. Registrar el controller como `@Component` — Spring lo detecta automáticamente
5. Exponer en `SecurityFilterChain` si el endpoint requiere auth o es público

## Autenticación JWT

- Los endpoints protegidos requieren `Authorization: Bearer <token>` en el header.
- La validación del token la realiza el filtro de Spring Security (`JwtAuthenticationFilter`).
- NUNCA extraer ni validar el token manualmente en controllers o services.

## Nunca hacer

- Lógica de negocio en controllers.
- Queries MongoDB directas en services (van en repositories).
- Field injection con `@Autowired`.
- Credenciales hardcodeadas.
- Retornar `_id` de MongoDB en respuestas API — mapear a campo de negocio explícito.

---

> Para estándares de Clean Code, SOLID, API REST, seguridad y observabilidad, ver `.github/docs/lineamientos/dev-guidelines.md`.
