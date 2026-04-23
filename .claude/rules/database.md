---
description: Reglas de acceso a datos para este proyecto (MongoDB + Spring Data MongoDB).
paths:
  - "**/model/entity/**"
  - "**/repository/**"
  - "**/repositories/**"
  - "**/entity/**"
  - "**/entities/**"
  - "**/migration/**"
---

# Reglas de Base de Datos — MongoDB + Spring Data MongoDB

## Stack aprobado

- **MongoDB** — base de datos principal (única, sin persistencia relacional)
- **Spring Data MongoDB** (`spring-boot-starter-data-mongodb`) — ÚNICO cliente aprobado
- **MongoRepository** — interfaz base para operaciones CRUD
- **Testcontainers MongoDB** — tests de integración con BD real

**Prohibido:** PyMongo, Motor async, Pydantic, SQLAlchemy, bases de datos relacionales (PostgreSQL, MySQL, SQLite), acceso directo al driver de MongoDB fuera de repositorios.

## Convenciones de MongoDB

- Anotación `@Document(collection = "<nombre>")` en toda entidad
- Nombres de colecciones en camelCase o kebab-case (definidos explícitamente en `@Document`)
- Campo `@Id` de tipo `String` — exponer un ID de negocio explícito en la API, nunca el `_id` raw
- Timestamps: `Instant createdAt` / `Instant updatedAt` gestionados en la app
- Versionado optimista: campo `@Version Long version` cuando aplique
- Paginación via `Pageable` de Spring Data

## Separación de Modelos (obligatorio)

| Modelo | Propósito | Contiene |
|--------|-----------|----------|
| **Entidad** (`model/entity/`) | Documento interno de MongoDB | `@Document`, `@Id`, todos los campos, timestamps |
| **Request DTO** (`model/dto/`) | Datos que el cliente envía | Solo campos que el cliente provee, `@Valid` |
| **Response DTO** (`model/dto/`) | Lo que la API retorna | Campos seguros para exponer (sin IDs internos) |

## Patrón de Repositorio (obligatorio)

```java
// repository/QuotationRepository.java
public interface QuotationRepository extends MongoRepository<Quotation, String> {

    Optional<Quotation> findByFolio(String folio);

    List<Quotation> findByClientIdAndEstadoValidacion(String clientId, EstadoValidacion estado);
}
```

- Usar métodos derivados de Spring Data cuando sea posible
- Para queries complejas, usar `@Query` con expresiones MongoDB (no concatenación de strings)
- `MongoTemplate` solo para operaciones que no soporta `MongoRepository`

## Reglas de Diseño

- **IDs de negocio como strings** — exponer folio (`COT-AAAA-NNNNNN`) u otro ID explícito en API, nunca `_id` de Mongo
- **Timestamps UTC** — `Instant.now()` en la capa de service, nunca en el cliente
- **Índices justificados** — solo crear con `@Indexed` o `@CompoundIndex` si hay un caso de uso documentado en spec
- **Sin datos sensibles en texto plano** — cifrado AES-256 para campos sensibles en reposo
- **Repositorio como única puerta de acceso a MongoDB** — services no tocan `MongoTemplate` directamente
- **Versionado optimista** — campo `@Version` en entidades sujetas a actualizaciones concurrentes

## Migraciones

- Cambios estructurales de datos documentados en scripts de migración versionados
- Toda migración debe ser reversible (UP + DOWN)
- No eliminar campos sin período de deprecación

## Anti-patrones Prohibidos

- Queries MongoDB en services (van en repositorios)
- Lógica de negocio en repositorios
- `_id` de MongoDB en respuestas API
- Concatenación de strings en `@Query` (NoSQL injection)
- Estado de conexión global mutable
- Acceso directo a `MongoTemplate` desde controllers o services sin pasar por repository
- Queries N+1 (iterar llamadas a DB en un bucle — usar `$in` o `findAll`)
