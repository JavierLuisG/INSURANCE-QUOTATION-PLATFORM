package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.ValidationRequest;
import com.plataformas_danos_back.model.dto.ValidationResult;
import com.plataformas_danos_back.model.entity.DataInconsistency;
import com.plataformas_danos_back.model.entity.ValidationRule;
import com.plataformas_danos_back.repository.ValidationRuleRepository;
import com.plataformas_danos_back.service.DataValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataValidationControllerTest {

    @Mock
    private DataValidationService dataValidationService;

    @Mock
    private ValidationRuleRepository validationRuleRepository;

    @InjectMocks
    private DataValidationController controller;

    // ── POST /validate — 200 OK (todos válidos) ───────────────────────────

    @Test
    void validate_allRecordsValid_returns200() {
        // GIVEN
        var request = ValidationRequest.builder()
                .dataType("SUBSCRIBER")
                .records(List.of(Map.of("id", "sub-001", "nombre", "Aseg ABC")))
                .correlationId("corr-001")
                .build();
        var validResult = ValidationResult.builder()
                .totalRecords(1)
                .validRecords(1)
                .inconsistentRecords(0)
                .results(List.of(
                        ValidationResult.RecordValidationResult.builder()
                                .id("sub-001")
                                .status("VALIDATED")
                                .errors(List.of())
                                .build()
                ))
                .build();
        when(dataValidationService.validateBatch("SUBSCRIBER", request.getRecords(), "corr-001"))
                .thenReturn(validResult);

        // WHEN
        var response = controller.validate(request);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInconsistentRecords()).isEqualTo(0);
        assertThat(response.getBody().getValidRecords()).isEqualTo(1);
    }

    @Test
    void validate_allRecordsValid_delegatesToService() {
        // GIVEN
        var request = ValidationRequest.builder()
                .dataType("TARIFF_CAT")
                .records(List.of(Map.of("zona", "ZONA_A", "factorTEV", 0.0015)))
                .correlationId("corr-002")
                .build();
        var result = ValidationResult.builder()
                .totalRecords(1).validRecords(1).inconsistentRecords(0).results(List.of()).build();
        when(dataValidationService.validateBatch(anyString(), any(), anyString())).thenReturn(result);

        // WHEN
        controller.validate(request);

        // THEN
        verify(dataValidationService).validateBatch("TARIFF_CAT", request.getRecords(), "corr-002");
    }

    // ── POST /validate — 207 Multi-Status (parcialmente inválidos) ────────

    @Test
    void validate_someRecordsInvalid_returns207() {
        // GIVEN
        var request = ValidationRequest.builder()
                .dataType("SUBSCRIBER")
                .records(List.of(
                        Map.of("id", "sub-001", "nombre", "Aseg ABC"),
                        Map.<String, Object>of("id", "sub-002")
                ))
                .correlationId("corr-003")
                .build();
        var mixedResult = ValidationResult.builder()
                .totalRecords(2)
                .validRecords(1)
                .inconsistentRecords(1)
                .results(List.of())
                .build();
        when(dataValidationService.validateBatch(anyString(), any(), anyString()))
                .thenReturn(mixedResult);

        // WHEN
        var response = controller.validate(request);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MULTI_STATUS);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getInconsistentRecords()).isEqualTo(1);
    }

    @Test
    void validate_allRecordsInvalid_returns207() {
        // GIVEN
        var request = ValidationRequest.builder()
                .dataType("SUBSCRIBER")
                .records(List.of(Map.<String, Object>of("id", "sub-001")))
                .correlationId("corr-004")
                .build();
        var invalidResult = ValidationResult.builder()
                .totalRecords(1)
                .validRecords(0)
                .inconsistentRecords(1)
                .results(List.of())
                .build();
        when(dataValidationService.validateBatch(anyString(), any(), anyString()))
                .thenReturn(invalidResult);

        // WHEN
        var response = controller.validate(request);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.MULTI_STATUS);
    }

    // ── GET /inconsistencies ──────────────────────────────────────────────

    @Test
    void getInconsistencies_noFilters_returns200WithPage() {
        // GIVEN
        var inconsistency = DataInconsistency.builder()
                .id("uuid-001")
                .dataType("SUBSCRIBER")
                .dataId("sub-002")
                .status("CRITICAL")
                .createdAt(Instant.now())
                .build();
        var page = new PageImpl<>(List.of(inconsistency));
        when(dataValidationService.getInconsistencies(eq(null), eq(null), any()))
                .thenReturn(page);

        // WHEN
        var response = controller.getInconsistencies(null, null, 0, 20);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getContent()).hasSize(1);
    }

    @Test
    void getInconsistencies_withFilters_delegatesFiltersToService() {
        // GIVEN
        var page = new PageImpl<DataInconsistency>(List.of());
        when(dataValidationService.getInconsistencies(eq("SUBSCRIBER"), eq("CRITICAL"), any()))
                .thenReturn(page);

        // WHEN
        controller.getInconsistencies("SUBSCRIBER", "CRITICAL", 0, 20);

        // THEN
        verify(dataValidationService).getInconsistencies(eq("SUBSCRIBER"), eq("CRITICAL"), any());
    }

    @Test
    void getInconsistencies_sizeExceedsMax_clampsTo100() {
        // GIVEN
        var page = new PageImpl<DataInconsistency>(List.of());
        when(dataValidationService.getInconsistencies(any(), any(), any())).thenReturn(page);

        // WHEN
        controller.getInconsistencies(null, null, 0, 500);

        // THEN — tamaño se limita a 100
        verify(dataValidationService).getInconsistencies(
                any(), any(), eq(PageRequest.of(0, 100)));
    }

    // ── GET /rules ────────────────────────────────────────────────────────

    @Test
    void getRules_dataTypeAndEnabled_callsCorrectRepositoryMethod() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "id", "NOT_NULL");
        when(validationRuleRepository.findByDataTypeAndEnabled("SUBSCRIBER", true))
                .thenReturn(List.of(rule));

        // WHEN
        var response = controller.getRules("SUBSCRIBER", true);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getDataType()).isEqualTo("SUBSCRIBER");
        verify(validationRuleRepository).findByDataTypeAndEnabled("SUBSCRIBER", true);
    }

    @Test
    void getRules_onlyDataType_callsFindByDataType() {
        // GIVEN
        when(validationRuleRepository.findByDataType("TARIFF_CAT")).thenReturn(List.of());

        // WHEN
        controller.getRules("TARIFF_CAT", null);

        // THEN
        verify(validationRuleRepository).findByDataType("TARIFF_CAT");
    }

    @Test
    void getRules_onlyEnabled_callsFindByEnabled() {
        // GIVEN
        when(validationRuleRepository.findByEnabled(true)).thenReturn(List.of());

        // WHEN
        controller.getRules(null, true);

        // THEN
        verify(validationRuleRepository).findByEnabled(true);
    }

    @Test
    void getRules_noFilters_callsFindAll() {
        // GIVEN
        when(validationRuleRepository.findAll()).thenReturn(List.of());

        // WHEN
        var response = controller.getRules(null, null);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(validationRuleRepository).findAll();
    }

    @Test
    void getRules_returns200WithRuleList() {
        // GIVEN
        var rules = List.of(
                buildRule("SUBSCRIBER", "id", "NOT_NULL"),
                buildRule("SUBSCRIBER", "nombre", "NOT_EMPTY")
        );
        when(validationRuleRepository.findByDataType("SUBSCRIBER")).thenReturn(rules);

        // WHEN
        var response = controller.getRules("SUBSCRIBER", null);

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private ValidationRule buildRule(String dataType, String fieldName, String ruleType) {
        return ValidationRule.builder()
                .dataType(dataType)
                .fieldName(fieldName)
                .ruleType(ruleType)
                .errorMessage("Error en campo '" + fieldName + "'")
                .severity("CRITICAL")
                .enabled(true)
                .build();
    }
}
