package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.entity.CorrectionRule;
import com.plataformas_danos_back.repository.CorrectionRuleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DataCorrectionServiceImplTest {

    @Mock
    private CorrectionRuleRepository correctionRuleRepository;

    @InjectMocks
    private DataCorrectionServiceImpl service;

    // ── applyCorrection — sin regla ───────────────────────────────────────

    @Test
    void applyCorrection_noRuleExists_returnsCorrectedFalse() {
        // GIVEN
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("SUBSCRIBER", "nombre", true))
                .thenReturn(Optional.empty());

        // WHEN
        var result = service.applyCorrection("SUBSCRIBER", "nombre", "  Valor  ");

        // THEN
        assertThat(result.isCorrected()).isFalse();
        assertThat(result.getOriginalValue()).isEqualTo("  Valor  ");
        assertThat(result.getCorrectedValue()).isNull();
        assertThat(result.getRuleApplied()).isNull();
    }

    // ── applyCorrection — TRIM ────────────────────────────────────────────

    @Test
    void applyCorrection_trimRule_removesLeadingTrailingSpaces() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "TRIM", null);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("SUBSCRIBER", "nombre", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        var result = service.applyCorrection("SUBSCRIBER", "nombre", "  Aseguradora ABC  ");

        // THEN
        assertThat(result.isCorrected()).isTrue();
        assertThat(result.getOriginalValue()).isEqualTo("  Aseguradora ABC  ");
        assertThat(result.getCorrectedValue()).isEqualTo("Aseguradora ABC");
        assertThat(result.getRuleApplied()).isEqualTo("TRIM");
    }

    @Test
    void applyCorrection_trimRule_nullValue_returnsNull() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "TRIM", null);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("SUBSCRIBER", "nombre", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        var result = service.applyCorrection("SUBSCRIBER", "nombre", null);

        // THEN
        assertThat(result.isCorrected()).isTrue();
        assertThat(result.getCorrectedValue()).isNull();
    }

    // ── applyCorrection — DEFAULT_VALUE ───────────────────────────────────

    @Test
    void applyCorrection_defaultValueRule_assignsConfiguredDefault() {
        // GIVEN
        var rule = buildRule("AGENT", "activo", "DEFAULT_VALUE", true);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("AGENT", "activo", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        var result = service.applyCorrection("AGENT", "activo", null);

        // THEN
        assertThat(result.isCorrected()).isTrue();
        assertThat(result.getOriginalValue()).isNull();
        assertThat(result.getCorrectedValue()).isEqualTo(true);
        assertThat(result.getRuleApplied()).isEqualTo("DEFAULT_VALUE");
    }

    // ── applyCorrection — NORMALIZE_CASE ──────────────────────────────────

    @Test
    void applyCorrection_normalizeCaseRule_convertsToUpperCase() {
        // GIVEN
        var rule = buildRule("BUSINESS_LINE", "nombre", "NORMALIZE_CASE", null);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("BUSINESS_LINE", "nombre", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        var result = service.applyCorrection("BUSINESS_LINE", "nombre", "comercial minorista");

        // THEN
        assertThat(result.isCorrected()).isTrue();
        assertThat(result.getOriginalValue()).isEqualTo("comercial minorista");
        assertThat(result.getCorrectedValue()).isEqualTo("COMERCIAL MINORISTA");
        assertThat(result.getRuleApplied()).isEqualTo("NORMALIZE_CASE");
    }

    @Test
    void applyCorrection_normalizeCaseRule_nullValue_returnsNull() {
        // GIVEN
        var rule = buildRule("BUSINESS_LINE", "nombre", "NORMALIZE_CASE", null);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("BUSINESS_LINE", "nombre", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        var result = service.applyCorrection("BUSINESS_LINE", "nombre", null);

        // THEN
        assertThat(result.isCorrected()).isTrue();
        assertThat(result.getCorrectedValue()).isNull();
    }

    // ── applyCorrection — preserva valorOriginal ──────────────────────────

    @Test
    void applyCorrection_trimRule_preservesOriginalValue() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "TRIM", null);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("SUBSCRIBER", "nombre", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        var result = service.applyCorrection("SUBSCRIBER", "nombre", "  Original  ");

        // THEN
        assertThat(result.getOriginalValue()).isEqualTo("  Original  ");
        assertThat(result.getCorrectedValue()).isEqualTo("Original");
    }

    // ── hasCorrectionRule ─────────────────────────────────────────────────

    @Test
    void hasCorrectionRule_ruleExists_returnsTrue() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "TRIM", null);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("SUBSCRIBER", "nombre", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        boolean exists = service.hasCorrectionRule("SUBSCRIBER", "nombre");

        // THEN
        assertThat(exists).isTrue();
        verify(correctionRuleRepository).findByDataTypeAndFieldNameAndEnabled("SUBSCRIBER", "nombre", true);
    }

    @Test
    void hasCorrectionRule_ruleNotExists_returnsFalse() {
        // GIVEN
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("TARIFF_CAT", "zona", true))
                .thenReturn(Optional.empty());

        // WHEN
        boolean exists = service.hasCorrectionRule("TARIFF_CAT", "zona");

        // THEN
        assertThat(exists).isFalse();
    }

    // ── applyCorrection — tipo de corrección desconocido ─────────────────

    @Test
    void applyCorrection_unknownCorrectionType_returnsSameValue() {
        // GIVEN
        var rule = buildRule("SUBSCRIBER", "nombre", "TIPO_DESCONOCIDO", null);
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled("SUBSCRIBER", "nombre", true))
                .thenReturn(Optional.of(rule));

        // WHEN
        var result = service.applyCorrection("SUBSCRIBER", "nombre", "valor");

        // THEN
        assertThat(result.isCorrected()).isTrue();
        assertThat(result.getCorrectedValue()).isEqualTo("valor");
    }

    // ── helpers ───────────────────────────────────────────────────────────

    private CorrectionRule buildRule(String dataType, String fieldName, String correctionType, Object defaultValue) {
        return CorrectionRule.builder()
                .dataType(dataType)
                .fieldName(fieldName)
                .correctionType(correctionType)
                .defaultValue(defaultValue)
                .enabled(true)
                .build();
    }
}
