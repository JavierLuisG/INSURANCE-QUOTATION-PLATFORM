/*
 * Plantilla para tests de Services con JUnit 5 + Mockito.
 * Copiar a: plataformas-danos-back/src/test/java/service/<Feature>ServiceTest.java
 */
package com.sofka.insurance.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// import com.sofka.insurance.repository.FeatureRepository;
// import com.sofka.insurance.model.dto.FeatureRequest;
// import com.sofka.insurance.model.entity.Feature;

@ExtendWith(MockitoExtension.class)
class FeatureServiceTest {

    @Mock
    private FeatureRepository repository;

    @InjectMocks
    private FeatureServiceImpl service;

    // ─── Happy Path ─────────────────────────────────────────────────────────

    @Test
    void create_validRequest_returnsSavedEntity() {
        // GIVEN
        // var request = new FeatureRequest("clientId-123", "COMPLETA");
        // var saved = new Feature("folio-001", "clientId-123", "COMPLETA");
        // when(repository.save(any())).thenReturn(saved);

        // WHEN
        // var result = service.create(request);

        // THEN
        // assertThat(result.getFolio()).startsWith("COT-");
        // verify(repository).save(any());
    }

    // ─── Error Path ─────────────────────────────────────────────────────────

    @Test
    void create_repositoryThrows_propagatesException() {
        // GIVEN
        // when(repository.save(any())).thenThrow(new RuntimeException("DB error"));

        // WHEN / THEN
        // assertThatThrownBy(() -> service.create(new FeatureRequest()))
        //     .isInstanceOf(RuntimeException.class)
        //     .hasMessageContaining("DB error");
    }

    // ─── Edge Cases ─────────────────────────────────────────────────────────

    @Test
    void getByFolio_nonExistentFolio_throwsNotFoundException() {
        // GIVEN
        // when(repository.findByFolio("COT-0000-000000")).thenReturn(Optional.empty());

        // WHEN / THEN
        // assertThatThrownBy(() -> service.getByFolio("COT-0000-000000"))
        //     .isInstanceOf(EntityNotFoundException.class);
    }
}
