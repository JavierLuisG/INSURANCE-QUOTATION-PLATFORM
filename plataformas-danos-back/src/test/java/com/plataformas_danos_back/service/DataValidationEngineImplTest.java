package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.ValidationErrorDetail;
import com.plataformas_danos_back.model.entity.ValidationRule;
import com.plataformas_danos_back.repository.ValidationRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataValidationEngineImplTest {

    @Mock
    private ValidationRuleRepository validationRuleRepository;

    @InjectMocks
    private DataValidationEngineImpl engine;

    // ── getRulesForDataType ───────────────────────────────────────────────

    @Test
    void getRulesForDataType_existingType_returnsEnabledRules() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "id", "NOT_NULL", null, "CRITICAL");
        when(validationRuleRepository.findByDataTypeAndEnabled("SUBSCRIBER", true))
                .thenReturn(List.of(rule));

        // WHEN
        var result = engine.getRulesForDataType("SUBSCRIBER");

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getDataType()).isEqualTo("SUBSCRIBER");
        verify(validationRuleRepository).findByDataTypeAndEnabled("SUBSCRIBER", true);
    }

    @Test
    void getRulesForDataType_unknownType_returnsEmptyList() {
        // GIVEN
        when(validationRuleRepository.findByDataTypeAndEnabled("UNKNOWN", true))
                .thenReturn(List.of());

        // WHEN
        var result = engine.getRulesForDataType("UNKNOWN");

        // THEN
        assertThat(result).isEmpty();
    }

    // ── applyRule — NOT_NULL ──────────────────────────────────────────────

    @Test
    void applyRule_notNull_nullValue_returnsError() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "id", "NOT_NULL", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, null);

        // THEN
        assertThat(error).isNotNull();
        assertThat(error.getField()).isEqualTo("id");
        assertThat(error.getRule()).isEqualTo("NOT_NULL");
        assertThat(error.getSeverity()).isEqualTo("CRITICAL");
    }

    @Test
    void applyRule_notNull_validValue_returnsNull() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "id", "NOT_NULL", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "sub-001");

        // THEN
        assertThat(error).isNull();
    }

    // ── applyRule — NOT_EMPTY ─────────────────────────────────────────────

    @Test
    void applyRule_notEmpty_nullValue_returnsError() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_EMPTY", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, null);

        // THEN
        assertThat(error).isNotNull();
        assertThat(error.getField()).isEqualTo("nombre");
        assertThat(error.getRule()).isEqualTo("NOT_EMPTY");
    }

    @Test
    void applyRule_notEmpty_blankString_returnsError() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_EMPTY", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "   ");

        // THEN
        assertThat(error).isNotNull();
        assertThat(error.getRule()).isEqualTo("NOT_EMPTY");
    }

    @Test
    void applyRule_notEmpty_emptyString_returnsError() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_EMPTY", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "");

        // THEN
        assertThat(error).isNotNull();
    }

    @Test
    void applyRule_notEmpty_validString_returnsNull() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "NOT_EMPTY", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "Aseguradora XYZ");

        // THEN
        assertThat(error).isNull();
    }

    // ── applyRule — POSITIVE_NUMBER ───────────────────────────────────────

    @Test
    void applyRule_positiveNumber_nullValue_returnsError() {
        // GIVEN
        var rule = buildRule("TARIFF_CAT", "factorTEV", "POSITIVE_NUMBER", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, null);

        // THEN
        assertThat(error).isNotNull();
        assertThat(error.getRule()).isEqualTo("POSITIVE_NUMBER");
    }

    @Test
    void applyRule_positiveNumber_zeroValue_returnsError() {
        // GIVEN
        var rule = buildRule("TARIFF_CAT", "factorTEV", "POSITIVE_NUMBER", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, 0.0);

        // THEN
        assertThat(error).isNotNull();
    }

    @Test
    void applyRule_positiveNumber_negativeValue_returnsError() {
        // GIVEN
        var rule = buildRule("TARIFF_CAT", "factorTEV", "POSITIVE_NUMBER", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, -1.5);

        // THEN
        assertThat(error).isNotNull();
    }

    @Test
    void applyRule_positiveNumber_nonNumericString_returnsError() {
        // GIVEN
        var rule = buildRule("TARIFF_CAT", "factorTEV", "POSITIVE_NUMBER", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "no-es-numero");

        // THEN
        assertThat(error).isNotNull();
    }

    @Test
    void applyRule_positiveNumber_validPositiveValue_returnsNull() {
        // GIVEN
        var rule = buildRule("TARIFF_CAT", "factorTEV", "POSITIVE_NUMBER", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, 0.0015);

        // THEN
        assertThat(error).isNull();
    }

    @Test
    void applyRule_positiveNumber_positiveStringValue_returnsNull() {
        // GIVEN
        var rule = buildRule("TARIFF_CAT", "factorFHM", "POSITIVE_NUMBER", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "0.0008");

        // THEN
        assertThat(error).isNull();
    }

    // ── applyRule — FORMAT_REGEX ──────────────────────────────────────────

    @Test
    void applyRule_formatRegex_nullValue_returnsError() {
        // GIVEN
        var rule = buildRule("ZIP_CODE", "codigo", "FORMAT_REGEX",
                Map.of("pattern", "^[0-9]{5}$"), "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, null);

        // THEN
        assertThat(error).isNotNull();
        assertThat(error.getRule()).isEqualTo("FORMAT_REGEX");
    }

    @Test
    void applyRule_formatRegex_invalidFormat_returnsError() {
        // GIVEN
        var rule = buildRule("ZIP_CODE", "codigo", "FORMAT_REGEX",
                Map.of("pattern", "^[0-9]{5}$"), "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "1234X");

        // THEN
        assertThat(error).isNotNull();
        assertThat(error.getField()).isEqualTo("codigo");
    }

    @Test
    void applyRule_formatRegex_tooShort_returnsError() {
        // GIVEN
        var rule = buildRule("ZIP_CODE", "codigo", "FORMAT_REGEX",
                Map.of("pattern", "^[0-9]{5}$"), "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "1234");

        // THEN
        assertThat(error).isNotNull();
    }

    @Test
    void applyRule_formatRegex_validFormat_returnsNull() {
        // GIVEN
        var rule = buildRule("ZIP_CODE", "codigo", "FORMAT_REGEX",
                Map.of("pattern", "^[0-9]{5}$"), "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "06600");

        // THEN
        assertThat(error).isNull();
    }

    @Test
    void applyRule_formatRegex_missingPatternParameter_returnsNull() {
        // GIVEN — regla FORMAT_REGEX sin parámetro 'pattern': se considera pasada por seguridad
        var rule = buildRule("ZIP_CODE", "codigo", "FORMAT_REGEX", null, "CRITICAL");

        // WHEN
        var error = engine.applyRule(rule, "06600");

        // THEN
        assertThat(error).isNull();
    }

    // ── applyRule — tipo desconocido ──────────────────────────────────────

    @Test
    void applyRule_unknownRuleType_returnsNull() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "UNKNOWN_TYPE", null, "WARNING");

        // WHEN
        var error = engine.applyRule(rule, "cualquier valor");

        // THEN — no reconocida: yield false → no violada → null
        assertThat(error).isNull();
    }

    // ── applyRule — mapeo de campos en ValidationErrorDetail ─────────────

    @Test
    void applyRule_violation_mapsAllFieldsInError() {
        // GIVEN
        var rule = ValidationRule.builder()
                .dataType("SUBSCRIBER")
                .fieldName("nombre")
                .ruleType("NOT_NULL")
                .errorMessage("Campo 'nombre' es obligatorio")
                .severity("CRITICAL")
                .enabled(true)
                .build();

        // WHEN
        ValidationErrorDetail error = engine.applyRule(rule, null);

        // THEN
        assertThat(error).isNotNull();
        assertThat(error.getField()).isEqualTo("nombre");
        assertThat(error.getRule()).isEqualTo("NOT_NULL");
        assertThat(error.getMessage()).isEqualTo("Campo 'nombre' es obligatorio");
        assertThat(error.getSeverity()).isEqualTo("CRITICAL");
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private ValidationRule buildRule(String dataType, String fieldName, String ruleType,
                                     Map<String, Object> parameters, String severity) {
        return ValidationRule.builder()
                .dataType(dataType)
                .fieldName(fieldName)
                .ruleType(ruleType)
                .parameters(parameters)
                .errorMessage("Error en campo '" + fieldName + "'")
                .severity(severity)
                .enabled(true)
                .build();
    }
}
