---
description: Principios de testing. Aplica a cualquier framework. Backend: JUnit 5 + Mockito + Testcontainers. Frontend: Vitest + Testing Library.
paths:
  - "**/tests/**"
  - "**/__tests__/**"
  - "**/*.test.*"
  - "**/*.spec.*"
  - "**/*Test.java"
  - "**/*Tests.java"
---

# Reglas de Testing

## Stack por módulo

| Módulo | Framework | Herramientas |
|--------|-----------|--------------|
| **Backend** (`plataformas-danos-back`) | JUnit 5 | Mockito, AssertJ, Testcontainers (MongoDB), JaCoCo |
| **Frontend** (`cotizador-danos-web`) | Vitest 3.x | @testing-library/react, @testing-library/user-event, MSW 2.x, jsdom |

## Cobertura mínima (quality gate bloqueante en CI)

| Alcance | Umbral |
|---------|--------|
| Global (backend + frontend) | ≥ 80% |
| Módulo `calc-engine` (backend) | ≥ 90% |

## Principios Universales (independiente del framework)

### Estructura AAA obligatoria
```
// GIVEN — preparar datos y contexto
// WHEN  — ejecutar la acción bajo prueba
// THEN  — verificar el resultado esperado
```

### Pirámide de Testing
| Nivel | % recomendado | Qué cubre |
|-------|--------------|-----------|
| **Unitarios** | ~70% | Lógica de negocio aislada con mocks |
| **Integración** | ~20% | Flujos entre capas, endpoints HTTP |
| **E2E** | ~10% | Flujos críticos de usuario |

### Reglas de Oro del Testing
- **Independencia** — cada test se puede ejecutar solo, en cualquier orden
- **Aislamiento** — mockear SIEMPRE dependencias externas (DB, APIs, auth, tiempo)
- **Determinismo** — sin `Thread.sleep()` / `sleep()`, sin dependencia de fechas reales, sin datos de producción
- **Cobertura mínima ≥ 80%** en lógica de negocio; ≥ 90% en `calc-engine`
- **Nombres descriptivos** — `<método>_<escenario>_<resultadoEsperado>` (Java) / `<función> <escenario> <resultado>` (JS/TS)
- **Un assert lógico por test** — si necesitas varios, separar en tests distintos

### Por cada unidad cubrir
- ✅ Happy path — datos válidos, flujo exitoso
- ❌ Error path — excepción esperada, respuesta de error
- 🔲 Edge case — vacío, duplicado, límites, permisos

## Patrones por módulo

### Backend — JUnit 5 + Mockito

```java
@ExtendWith(MockitoExtension.class)
class QuotationServiceTest {

    @Mock
    private QuotationRepository repository;

    @InjectMocks
    private QuotationServiceImpl service;

    @Test
    void create_validRequest_returnsSavedQuotation() {
        // GIVEN
        var request = new QuotationRequest(...);
        var saved = new Quotation(...);
        when(repository.save(any())).thenReturn(saved);

        // WHEN
        var result = service.create(request);

        // THEN
        assertThat(result.getFolio()).startsWith("COT-");
        verify(repository).save(any());
    }
}
```

Tests de integración con Testcontainers:

```java
@SpringBootTest
@Testcontainers
class QuotationRepositoryIT {

    @Container
    static MongoDBContainer mongo = new MongoDBContainer("mongo:7");

    @DynamicPropertySource
    static void mongoProps(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
    }
}
```

### Frontend — Vitest + Testing Library

```typescript
describe('CoverageForm', () => {
  it('shows error when coverage limit is empty', async () => {
    // GIVEN
    render(<CoverageForm />);

    // WHEN
    await userEvent.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN
    expect(screen.getByText(/límite requerido/i)).toBeInTheDocument();
  });
});
```

## Anti-patrones Prohibidos
- Tests que dependen del orden de ejecución
- Llamadas reales a servicios externos (DB, APIs, auth)
- `console.log` / `System.out.println` permanentes en tests
- Lógica condicional dentro de un test (`if`/`else`)
- Datos de producción real en fixtures

## Estrategia de Regresión
- **Smoke suite** (`@Tag("smoke")` / `@smoke`): happy paths críticos → corre en cada PR
- **Regresión completa** (`@Tag("regression")` / `@regression`): todo → corre nightly o pre-release
- Un test con `@Tag("critico")` entra automáticamente al smoke suite
