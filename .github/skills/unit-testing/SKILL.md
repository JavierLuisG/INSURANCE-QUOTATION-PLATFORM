---
name: unit-testing
description: Genera tests unitarios e integración para backend y/o frontend. Lee la spec y el código implementado. Requiere spec APPROVED e implementación completa.
argument-hint: "<nombre-feature> [backend|frontend|ambos]"
---

# Unit Testing

## Definition of Done — verificar al completar

- [ ] Cobertura ≥ 80% en lógica de negocio (quality gate bloqueante)
- [ ] Tests aislados — sin conexión a DB real ni Firebase (siempre mocks)
- [ ] Escenario feliz + errores de negocio + validaciones de entrada cubiertos
- [ ] Los cambios no rompen contratos existentes del módulo

## Prerequisito — Lee en paralelo

```
.github/specs/<feature>.spec.md        (criterios de aceptación)
código implementado en plataformas-danos-back/ y/o cotizador-danos-web/
.github/instructions/tests.instructions.md   (JUnit 5 + Mockito + Vitest + Testing Library)
```

## Output por scope

### Backend → `plataformas-danos-back/src/test/java/`

| Archivo | Cubre |
|---------|-------|
| `controller/<Feature>ControllerTest.java` | Endpoints: 200/201, 400, 401, 404 via MockMvc |
| `service/<Feature>ServiceTest.java` | Lógica: happy path + errores de negocio con Mockito |
| `repository/<Feature>RepositoryIT.java` | Queries con Testcontainers MongoDB real |

### Frontend → `cotizador-danos-web/__tests__/`

| Archivo | Cubre |
|---------|-------|
| `components/<Feature>.test.tsx` | Render + interacciones (click, submit) |
| `hooks/use<Feature>.test.ts` | Estado inicial + respuesta API + error handling |
| `pages/<Feature>Page.test.tsx` | Render completo con providers |

## Patrones core

```java
// Backend — AAA con JUnit 5 + Mockito
@ExtendWith(MockitoExtension.class)
class QuotationServiceTest {
    @Mock QuotationRepository repository;
    @InjectMocks QuotationServiceImpl service;

    @Test
    void create_validRequest_returnsSavedQuotation() {
        // GIVEN
        when(repository.save(any())).thenReturn(new Quotation());
        // WHEN
        var result = service.create(new QuotationRequest());
        // THEN
        assertThat(result.getFolio()).startsWith("COT-");
    }
}
```

```typescript
// Frontend — mock service + renderHook (Vitest + Testing Library)
vi.mock('../../lib/services/quotationService');
vi.mocked(getQuotations).mockResolvedValue([{ folio: 'COT-2026-000001' }]);
const { result } = renderHook(() => useQuotation());
await waitFor(() => expect(result.current.data).toHaveLength(1));
```

## Restricciones

- Solo `tests/` o `__tests__/`. No modificar código fuente.
- Nunca conectar a DB real ni Firebase — siempre mocks.
- Cobertura mínima ≥ 80% en lógica de negocio.
