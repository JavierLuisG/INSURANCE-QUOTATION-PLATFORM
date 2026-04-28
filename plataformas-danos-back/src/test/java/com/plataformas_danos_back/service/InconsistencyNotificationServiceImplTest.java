package com.plataformas_danos_back.service;

import com.plataformas_danos_back.config.ValidationProperties;
import com.plataformas_danos_back.model.entity.DataInconsistency;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InconsistencyNotificationServiceImplTest {

    @Mock
    private ValidationProperties validationProperties;

    @InjectMocks
    private InconsistencyNotificationServiceImpl service;

    // ── notifyCritical ────────────────────────────────────────────────────

    @Test
    void notifyCritical_validInconsistency_doesNotThrow() {
        // GIVEN
        var validationError = DataInconsistency.ValidationErrorDetail.builder()
                .field("nombre")
                .rule("NOT_NULL")
                .message("Campo 'nombre' es obligatorio")
                .severity("CRITICAL")
                .build();
        var inconsistency = DataInconsistency.builder()
                .id("uuid-001")
                .dataType("SUBSCRIBER")
                .dataId("sub-002")
                .validationError(validationError)
                .correlationId("corr-123")
                .createdAt(Instant.now())
                .build();

        // WHEN / THEN — nunca propaga excepción
        assertThatNoException().isThrownBy(() -> service.notifyCritical(inconsistency));
    }

    @Test
    void notifyCritical_nullValidationError_doesNotThrow() {
        // GIVEN — inconsistencia sin detalle de error (edge case)
        var inconsistency = DataInconsistency.builder()
                .id("uuid-002")
                .dataType("TARIFF_CAT")
                .dataId("zona-A")
                .validationError(null)
                .correlationId("corr-456")
                .createdAt(Instant.now())
                .build();

        // WHEN / THEN — campo 'desconocido' se usa como fallback, sin excepción
        assertThatNoException().isThrownBy(() -> service.notifyCritical(inconsistency));
    }

    @Test
    void notifyCritical_nullId_doesNotThrow() {
        // GIVEN
        var inconsistency = DataInconsistency.builder()
                .id(null)
                .dataType("ZIP_CODE")
                .dataId(null)
                .correlationId(null)
                .createdAt(Instant.now())
                .build();

        // WHEN / THEN
        assertThatNoException().isThrownBy(() -> service.notifyCritical(inconsistency));
    }

    // ── notifyBatchThresholdExceeded ──────────────────────────────────────

    @Test
    void notifyBatchThresholdExceeded_aboveThreshold_doesNotThrow() {
        // GIVEN
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        // WHEN / THEN — 6 inconsistentes de 10 = 60%, por encima del umbral 10%
        assertThatNoException().isThrownBy(
                () -> service.notifyBatchThresholdExceeded(10, 6, "SUBSCRIBER"));
    }

    @Test
    void notifyBatchThresholdExceeded_zeroTotal_doesNotThrowArithmeticException() {
        // GIVEN — total=0 debería manejar división por cero
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        // WHEN / THEN
        assertThatNoException().isThrownBy(
                () -> service.notifyBatchThresholdExceeded(0, 0, "TARIFF_CAT"));
    }

    @Test
    void notifyBatchThresholdExceeded_exactlyThreshold_doesNotThrow() {
        // GIVEN — 1 de 10 = 10%, exactamente igual al umbral
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        // WHEN / THEN
        assertThatNoException().isThrownBy(
                () -> service.notifyBatchThresholdExceeded(10, 1, "AGENT"));
    }

    @Test
    void notifyBatchThresholdExceeded_logsDataType() {
        // GIVEN
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        // WHEN / THEN — verifica que no falla con cualquier tipo de dato
        assertThatNoException().isThrownBy(
                () -> service.notifyBatchThresholdExceeded(100, 50, "TARIFF_ELECTRONIC"));
    }
}
