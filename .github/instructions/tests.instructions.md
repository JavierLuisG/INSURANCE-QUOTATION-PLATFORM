---
applyTo: "plataformas-danos-back/src/test/java/**/*.java,cotizador-danos-web/**/*.{test,spec}.{ts,tsx}"
---

> **Scope**: Backend aplica a tests Java (JUnit 5 + Mockito + Testcontainers). Frontend aplica a tests TypeScript (Vitest + Testing Library). Mantener los principios (independencia, aislamiento, AAA, cobertura ≥ 80%) en ambos módulos.

# Instrucciones para Archivos de Pruebas Unitarias

## Principios

- **Independencia**: cada test es 100% independiente — sin estado compartido entre tests.
- **Aislamiento**: mockear SIEMPRE dependencias externas (DB, APIs externas, sistema de archivos).
- **Claridad**: nombre del test debe describir la función bajo prueba y el escenario.
- **Cobertura**: cubrir happy path, error path y edge cases para cada unidad.

## Cobertura mínima (quality gate bloqueante en CI)

| Alcance | Umbral |
|---------|--------|
| Global (backend + frontend) | ≥ 80% |
| Módulo `calc-engine` (backend) | ≥ 90% |

## Backend (JUnit 5 + Mockito + AssertJ)

### Estructura de archivos
```
plataformas-danos-back/src/test/java/
  controller/   QuotationControllerTest.java   ← MockMvc tests
  service/      QuotationServiceTest.java       ← unitarios con mocks de repo
  repository/   QuotationRepositoryIT.java      ← integración con Testcontainers
```

### Convenciones
- Nombre: `<método>_<escenario>_<resultadoEsperado>` (ej: `create_validRequest_returnsSavedQuotation`)
- Usar `@ExtendWith(MockitoExtension.class)` para tests unitarios.
- Usar `@SpringBootTest` + `@Testcontainers` para tests de integración con MongoDB real.
- Mockear repositorios en tests de services con `@Mock` + `@InjectMocks`.

```java
// Tests de Service con Mockito
@ExtendWith(MockitoExtension.class)
class QuotationServiceTest {

    @Mock
    private QuotationRepository repository;

    @InjectMocks
    private QuotationServiceImpl service;

    @Test
    void create_validRequest_returnsSavedQuotation() {
        // GIVEN
        var request = new QuotationRequest(/* ... */);
        when(repository.save(any())).thenReturn(new Quotation(/* ... */));

        // WHEN
        var result = service.create(request);

        // THEN
        assertThat(result.getFolio()).startsWith("COT-");
        verify(repository).save(any());
    }
}
```

```java
// Tests de integración con Testcontainers
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

## Frontend (Vitest + Testing Library)

### Estructura de archivos
```
cotizador-danos-web/
  __tests__/
    components/<Feature>.test.tsx
    hooks/use<Feature>.test.ts
    pages/<Feature>Page.test.tsx
```

### Convenciones
- Nombre del `describe`: nombre del componente/hook.
- Nombre del `it`/`test`: `[verbo] [qué hace] [condición]` (ej: `shows error when limit is empty`).
- Usar `vi.mock()` para mockear módulos externos (API services, Zustand stores).
- Siempre limpiar mocks con `beforeEach(() => vi.clearAllMocks())`.
- Usar MSW para interceptar llamadas HTTP en tests de integración frontend.

```typescript
// Ejemplo mínimo de test de componente
describe('CoverageForm', () => {
  it('shows validation error when required field is empty', async () => {
    // GIVEN
    render(<CoverageForm />);

    // WHEN
    await userEvent.click(screen.getByRole('button', { name: /guardar/i }));

    // THEN
    expect(screen.getByText(/requerido/i)).toBeInTheDocument();
  });
});
```

## Nunca hacer

- Tests que dependen del orden de ejecución.
- Llamadas reales a MongoDB, APIs externas o cualquier servicio externo.
- `console.log` / `System.out.println` permanentes en tests.
- Lógica condicional dentro de un test (`if`/`else`).
- `Thread.sleep()` / `sleep()` para sincronización temporal (cero tests "flaky").

---

### Estructura AAA obligatoria
```
// GIVEN — preparar datos y contexto
// WHEN  — ejecutar la acción bajo prueba
// THEN  — verificar el resultado esperado
```

### DoR de Automatización
Antes de automatizar un flujo, verificar:
- [ ] Caso ejecutado exitosamente en manual sin bugs críticos
- [ ] Caso de prueba detallado con datos identificados
- [ ] Viabilidad técnica comprobada
- [ ] Ambiente estable disponible
- [ ] Aprobación del equipo

### DoD de Automatización
Un script finaliza cuando:
- [ ] Código revisado por pares (pull request review)
- [ ] Datos desacoplados del código
- [ ] Integrado al pipeline de CI
- [ ] Con documentación y trazabilidad hacia la HU

> Para quality gates, pirámide de testing, TDD, CDC y nomenclatura Gherkin, ver `.github/docs/lineamientos/dev-guidelines.md` §7 y `.github/docs/lineamientos/qa-guidelines.md`.
