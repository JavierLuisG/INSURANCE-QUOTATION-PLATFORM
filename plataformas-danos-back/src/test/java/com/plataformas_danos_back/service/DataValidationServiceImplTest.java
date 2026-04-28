package com.plataformas_danos_back.service;

import com.plataformas_danos_back.config.ValidationProperties;
import com.plataformas_danos_back.model.dto.CorrectionResult;
import com.plataformas_danos_back.model.dto.ValidationErrorDetail;
import com.plataformas_danos_back.model.dto.ValidationResult;
import com.plataformas_danos_back.model.entity.DataInconsistency;
import com.plataformas_danos_back.model.entity.ValidationRule;
import com.plataformas_danos_back.repository.DataInconsistencyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataValidationServiceImplTest {

    @Mock
    private DataValidationEngine dataValidationEngine;
    @Mock
    private DataCorrectionService dataCorrectionService;
    @Mock
    private InconsistencyNotificationService notificationService;
    @Mock
    private DataInconsistencyRepository inconsistencyRepository;
    @Mock
    private ValidationProperties validationProperties;

    @InjectMocks
    private DataValidationServiceImpl service;

    // ── validateBatch — Happy Path ────────────────────────────────────────

    @Test
    void validateBatch_allRecordsValid_returnsAllValidated() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_NULL", "CRITICAL");
        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(eq(rule), any())).thenReturn(null);
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(
                Map.<String, Object>of("id", "sub-001", "nombre", "Aseguradora ABC"),
                Map.<String, Object>of("id", "sub-002", "nombre", "Aseguradora XYZ")
        );

        // WHEN
        ValidationResult result = service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN
        assertThat(result.getTotalRecords()).isEqualTo(2);
        assertThat(result.getValidRecords()).isEqualTo(2);
        assertThat(result.getInconsistentRecords()).isEqualTo(0);
        assertThat(result.getResults()).allMatch(r -> "VALIDATED".equals(r.getStatus()));
    }

    @Test
    void validateBatch_allRecordsInvalid_returnsAllInconsistent() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_NULL", "CRITICAL");
        var errorDetail = buildError("nombre", "NOT_NULL", "CRITICAL");
        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(eq(rule), any())).thenReturn(errorDetail);
        when(dataCorrectionService.hasCorrectionRule("SUBSCRIBER", "nombre")).thenReturn(false);
        when(inconsistencyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(
                Map.<String, Object>of("id", "sub-001"),
                Map.<String, Object>of("id", "sub-002")
        );

        // WHEN
        ValidationResult result = service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN
        assertThat(result.getTotalRecords()).isEqualTo(2);
        assertThat(result.getValidRecords()).isEqualTo(0);
        assertThat(result.getInconsistentRecords()).isEqualTo(2);
        assertThat(result.getResults()).allMatch(r -> "INCONSISTENT".equals(r.getStatus()));
    }

    @Test
    void validateBatch_mixedRecords_returns207Counts() {
        // GIVEN — 1 válido, 1 inválido
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_NULL", "CRITICAL");
        var errorDetail = buildError("nombre", "NOT_NULL", "CRITICAL");
        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(eq(rule), eq("Aseguradora ABC"))).thenReturn(null);
        when(dataValidationEngine.applyRule(eq(rule), eq(null))).thenReturn(errorDetail);
        when(dataCorrectionService.hasCorrectionRule("SUBSCRIBER", "nombre")).thenReturn(false);
        when(inconsistencyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(
                Map.<String, Object>of("id", "sub-001", "nombre", "Aseguradora ABC"),
                Map.<String, Object>of("id", "sub-002")
        );

        // WHEN
        ValidationResult result = service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN
        assertThat(result.getTotalRecords()).isEqualTo(2);
        assertThat(result.getValidRecords()).isEqualTo(1);
        assertThat(result.getInconsistentRecords()).isEqualTo(1);
    }

    // ── validateBatch — Corrección automática ─────────────────────────────

    @Test
    void validateBatch_correctionApplied_returnsValidatedWithCorrectionFlag() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_EMPTY", "CRITICAL");
        var errorDetail = buildError("nombre", "NOT_EMPTY", "CRITICAL");
        var correction = CorrectionResult.builder()
                .corrected(true)
                .originalValue("  ")
                .correctedValue("Agencia Central")
                .ruleApplied("TRIM")
                .build();

        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(eq(rule), any())).thenReturn(errorDetail);
        when(dataCorrectionService.hasCorrectionRule("SUBSCRIBER", "nombre")).thenReturn(true);
        when(dataCorrectionService.applyCorrection("SUBSCRIBER", "nombre", "  ")).thenReturn(correction);
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        java.util.Map<String, Object> mutableRecord = new java.util.HashMap<>();
        mutableRecord.put("id", "sub-001");
        mutableRecord.put("nombre", "  ");
        List<java.util.Map<String, Object>> records = List.of(mutableRecord);

        // WHEN
        ValidationResult result = service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN
        assertThat(result.getValidRecords()).isEqualTo(1);
        assertThat(result.getInconsistentRecords()).isEqualTo(0);
        assertThat(result.getResults().get(0).isCorrectionApplied()).isTrue();
    }

    @Test
    void validateBatch_criticalInconsistency_notifiesCritical() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "id", "NOT_NULL", "CRITICAL");
        var errorDetail = buildError("id", "NOT_NULL", "CRITICAL");

        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(eq(rule), any())).thenReturn(errorDetail);
        when(dataCorrectionService.hasCorrectionRule("SUBSCRIBER", "id")).thenReturn(false);
        when(inconsistencyRepository.save(any())).thenAnswer(inv -> {
            DataInconsistency di = inv.getArgument(0);
            di.setId("uuid-saved");
            return di;
        });
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(Map.<String, Object>of("nombre", "Aseguradora"));

        // WHEN
        service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN — la notificación CRITICAL fue invocada
        verify(notificationService).notifyCritical(any(DataInconsistency.class));
    }

    @Test
    void validateBatch_warningInconsistency_doesNotNotifyCritical() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_EMPTY", "WARNING");
        var errorDetail = buildError("nombre", "NOT_EMPTY", "WARNING");

        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(eq(rule), any())).thenReturn(errorDetail);
        when(dataCorrectionService.hasCorrectionRule("SUBSCRIBER", "nombre")).thenReturn(false);
        when(inconsistencyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(Map.<String, Object>of("id", "sub-001"));

        // WHEN
        service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN — WARNING no activa notificación crítica individual
        verify(notificationService, never()).notifyCritical(any());
    }

    // ── validateBatch — Umbral de lote ────────────────────────────────────

    @Test
    void validateBatch_thresholdExceeded_notifiesBatchThreshold() {
        // GIVEN — 6 inconsistentes de 6 = 100%, umbral es 10%
        var rule = buildRule("SUBSCRIBER", "id", "NOT_NULL", "CRITICAL");
        var errorDetail = buildError("id", "NOT_NULL", "CRITICAL");

        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(any(), any())).thenReturn(errorDetail);
        when(dataCorrectionService.hasCorrectionRule(anyString(), anyString())).thenReturn(false);
        when(inconsistencyRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(
                Map.<String, Object>of("nombre", "A"),
                Map.<String, Object>of("nombre", "B"),
                Map.<String, Object>of("nombre", "C"),
                Map.<String, Object>of("nombre", "D"),
                Map.<String, Object>of("nombre", "E"),
                Map.<String, Object>of("nombre", "F")
        );

        // WHEN
        service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN
        verify(notificationService).notifyBatchThresholdExceeded(eq(6), eq(6), eq("SUBSCRIBER"));
    }

    @Test
    void validateBatch_thresholdNotExceeded_doesNotNotifyBatch() {
        // GIVEN — 1 inconsistente de 100 = 1%, umbral es 10%
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_NULL", "CRITICAL");
        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of(rule));
        when(dataValidationEngine.applyRule(eq(rule), any())).thenReturn(null);
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(Map.<String, Object>of("id", "sub-001", "nombre", "Aseg A"));

        // WHEN
        service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN
        verify(notificationService, never()).notifyBatchThresholdExceeded(anyInt(), anyInt(), anyString());
    }

    @Test
    void validateBatch_emptyRecords_returnsEmptyResult() {
        // GIVEN
        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of());
        // checkBatchThreshold retorna inmediatamente con total=0, sin consultar el umbral

        // WHEN
        ValidationResult result = service.validateBatch("SUBSCRIBER", List.of(), "corr-001");

        // THEN
        assertThat(result.getTotalRecords()).isEqualTo(0);
        assertThat(result.getValidRecords()).isEqualTo(0);
        assertThat(result.getInconsistentRecords()).isEqualTo(0);
    }

    // ── validateBatch — resolveRecordId ───────────────────────────────────

    @Test
    void validateBatch_recordWithIdField_usesIdAsRecordId() {
        // GIVEN
        when(dataValidationEngine.getRulesForDataType("SUBSCRIBER")).thenReturn(List.of());
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(Map.<String, Object>of("id", "sub-999", "nombre", "ABC"));

        // WHEN
        ValidationResult result = service.validateBatch("SUBSCRIBER", records, "corr-001");

        // THEN
        assertThat(result.getResults().get(0).getId()).isEqualTo("sub-999");
    }

    @Test
    void validateBatch_recordWithCodigoField_usesCodigoAsRecordId() {
        // GIVEN
        when(dataValidationEngine.getRulesForDataType("ZIP_CODE")).thenReturn(List.of());
        when(validationProperties.getInconsistencyThresholdPercent()).thenReturn(10);

        var records = List.of(Map.<String, Object>of("codigo", "06600", "ciudad", "CDMX"));

        // WHEN
        ValidationResult result = service.validateBatch("ZIP_CODE", records, "corr-001");

        // THEN
        assertThat(result.getResults().get(0).getId()).isEqualTo("06600");
    }

    // ── getInconsistencies ────────────────────────────────────────────────

    @Test
    void getInconsistencies_bothFilters_callsFindByDataTypeAndStatus() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 20);
        Page<DataInconsistency> page = new PageImpl<>(List.of());
        when(inconsistencyRepository.findByDataTypeAndStatus("SUBSCRIBER", "CRITICAL", pageable))
                .thenReturn(page);

        // WHEN
        var result = service.getInconsistencies("SUBSCRIBER", "CRITICAL", pageable);

        // THEN
        assertThat(result).isNotNull();
        verify(inconsistencyRepository).findByDataTypeAndStatus("SUBSCRIBER", "CRITICAL", pageable);
    }

    @Test
    void getInconsistencies_onlyDataType_callsFindByDataType() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 20);
        Page<DataInconsistency> page = new PageImpl<>(List.of());
        when(inconsistencyRepository.findByDataType("TARIFF_CAT", pageable)).thenReturn(page);

        // WHEN
        service.getInconsistencies("TARIFF_CAT", null, pageable);

        // THEN
        verify(inconsistencyRepository).findByDataType("TARIFF_CAT", pageable);
    }

    @Test
    void getInconsistencies_onlyStatus_callsFindByStatus() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 20);
        Page<DataInconsistency> page = new PageImpl<>(List.of());
        when(inconsistencyRepository.findByStatus("WARNING", pageable)).thenReturn(page);

        // WHEN
        service.getInconsistencies(null, "WARNING", pageable);

        // THEN
        verify(inconsistencyRepository).findByStatus("WARNING", pageable);
    }

    @Test
    void getInconsistencies_noFilters_callsFindAll() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 20);
        Page<DataInconsistency> page = new PageImpl<>(List.of());
        when(inconsistencyRepository.findAll(pageable)).thenReturn(page);

        // WHEN
        service.getInconsistencies(null, null, pageable);

        // THEN
        verify(inconsistencyRepository).findAll(pageable);
    }

    @Test
    void getInconsistencies_withData_returnsPageWithResults() {
        // GIVEN
        Pageable pageable = PageRequest.of(0, 20);
        var inconsistency = DataInconsistency.builder()
                .id("uuid-001")
                .dataType("SUBSCRIBER")
                .dataId("sub-002")
                .status("CRITICAL")
                .createdAt(Instant.now())
                .build();
        Page<DataInconsistency> page = new PageImpl<>(List.of(inconsistency));
        when(inconsistencyRepository.findByDataType("SUBSCRIBER", pageable)).thenReturn(page);

        // WHEN
        var result = service.getInconsistencies("SUBSCRIBER", null, pageable);

        // THEN
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getDataType()).isEqualTo("SUBSCRIBER");
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private ValidationRule buildRule(String dataType, String fieldName, String ruleType, String severity) {
        return ValidationRule.builder()
                .dataType(dataType)
                .fieldName(fieldName)
                .ruleType(ruleType)
                .errorMessage("Error en campo '" + fieldName + "'")
                .severity(severity)
                .enabled(true)
                .build();
    }

    private ValidationErrorDetail buildError(String field, String rule, String severity) {
        return ValidationErrorDetail.builder()
                .field(field)
                .rule(rule)
                .message("Error en campo '" + field + "'")
                .severity(severity)
                .build();
    }
}
