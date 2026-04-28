package com.plataformas_danos_back.config;

import com.plataformas_danos_back.model.entity.CorrectionRule;
import com.plataformas_danos_back.model.entity.ValidationRule;
import com.plataformas_danos_back.repository.CorrectionRuleRepository;
import com.plataformas_danos_back.repository.ValidationRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class ValidationRulesConfig {

    private static final String SYSTEM = "system";
    private static final String CRITICAL = "CRITICAL";
    private static final String WARNING = "WARNING";

    private final ValidationRuleRepository validationRuleRepository;
    private final CorrectionRuleRepository correctionRuleRepository;

    @Bean
    public CommandLineRunner seedValidationRules() {
        return args -> {
            if (validationRuleRepository.count() == 0) {
                log.info("Cargando catálogo inicial de reglas de validación...");
                validationRuleRepository.saveAll(buildInitialValidationRules());
                log.info("Reglas de validación cargadas exitosamente.");
            }
            if (correctionRuleRepository.count() == 0) {
                log.info("Cargando catálogo inicial de reglas de corrección...");
                correctionRuleRepository.saveAll(buildInitialCorrectionRules());
                log.info("Reglas de corrección cargadas exitosamente.");
            }
        };
    }

    private List<ValidationRule> buildInitialValidationRules() {
        Instant now = Instant.now();
        return List.of(
                // ── SUBSCRIBER ──────────────────────────────────────────────
                rule("SUBSCRIBER", "id", "NOT_NULL", null,
                        "Campo 'id' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("SUBSCRIBER", "id", "NOT_EMPTY", null,
                        "Campo 'id' no puede estar vacío", CRITICAL, now),
                rule("SUBSCRIBER", "nombre", "NOT_NULL", null,
                        "Campo 'nombre' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("SUBSCRIBER", "nombre", "NOT_EMPTY", null,
                        "Campo 'nombre' no puede estar vacío", CRITICAL, now),
                rule("SUBSCRIBER", "clave", "NOT_NULL", null,
                        "Campo 'clave' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("SUBSCRIBER", "clave", "NOT_EMPTY", null,
                        "Campo 'clave' no puede estar vacío", CRITICAL, now),
                rule("SUBSCRIBER", "activo", "NOT_NULL", null,
                        "Campo 'activo' es obligatorio y no puede ser nulo", CRITICAL, now),

                // ── AGENT ────────────────────────────────────────────────────
                rule("AGENT", "id", "NOT_NULL", null,
                        "Campo 'id' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("AGENT", "id", "NOT_EMPTY", null,
                        "Campo 'id' no puede estar vacío", CRITICAL, now),
                rule("AGENT", "nombre", "NOT_NULL", null,
                        "Campo 'nombre' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("AGENT", "nombre", "NOT_EMPTY", null,
                        "Campo 'nombre' no puede estar vacío", CRITICAL, now),
                rule("AGENT", "clave", "NOT_NULL", null,
                        "Campo 'clave' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("AGENT", "clave", "NOT_EMPTY", null,
                        "Campo 'clave' no puede estar vacío", CRITICAL, now),
                rule("AGENT", "activo", "NOT_NULL", null,
                        "Campo 'activo' es obligatorio y no puede ser nulo", CRITICAL, now),

                // ── BUSINESS_LINE ────────────────────────────────────────────
                rule("BUSINESS_LINE", "id", "NOT_NULL", null,
                        "Campo 'id' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("BUSINESS_LINE", "id", "NOT_EMPTY", null,
                        "Campo 'id' no puede estar vacío", CRITICAL, now),
                rule("BUSINESS_LINE", "nombre", "NOT_NULL", null,
                        "Campo 'nombre' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("BUSINESS_LINE", "nombre", "NOT_EMPTY", null,
                        "Campo 'nombre' no puede estar vacío", CRITICAL, now),

                // ── ZIP_CODE ─────────────────────────────────────────────────
                rule("ZIP_CODE", "codigo", "NOT_NULL", null,
                        "Campo 'codigo' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("ZIP_CODE", "codigo", "FORMAT_REGEX",
                        Map.of("pattern", "^[0-9]{5}$"),
                        "Código postal debe contener exactamente 5 dígitos numéricos", CRITICAL, now),
                rule("ZIP_CODE", "ciudad", "NOT_NULL", null,
                        "Campo 'ciudad' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("ZIP_CODE", "ciudad", "NOT_EMPTY", null,
                        "Campo 'ciudad' no puede estar vacío", CRITICAL, now),
                rule("ZIP_CODE", "estado", "NOT_NULL", null,
                        "Campo 'estado' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("ZIP_CODE", "estado", "NOT_EMPTY", null,
                        "Campo 'estado' no puede estar vacío", CRITICAL, now),

                // ── TARIFF_CAT ───────────────────────────────────────────────
                rule("TARIFF_CAT", "zona", "NOT_NULL", null,
                        "Campo 'zona' es obligatorio y no puede ser nulo", CRITICAL, now),
                rule("TARIFF_CAT", "zona", "NOT_EMPTY", null,
                        "Campo 'zona' no puede estar vacío", CRITICAL, now),
                rule("TARIFF_CAT", "factorTEV", "POSITIVE_NUMBER", null,
                        "Campo 'factorTEV' debe ser un número positivo (>0)", CRITICAL, now),
                rule("TARIFF_CAT", "factorFHM", "POSITIVE_NUMBER", null,
                        "Campo 'factorFHM' debe ser un número positivo (>0)", CRITICAL, now),

                // ── TARIFF_FIRE ──────────────────────────────────────────────
                rule("TARIFF_FIRE", "factor", "POSITIVE_NUMBER", null,
                        "Campo 'factor' debe ser un número positivo (>0)", CRITICAL, now),

                // ── TARIFF_ELECTRONIC ────────────────────────────────────────
                rule("TARIFF_ELECTRONIC", "factor", "POSITIVE_NUMBER", null,
                        "Campo 'factor' debe ser un número positivo (>0)", CRITICAL, now)
        );
    }

    private List<CorrectionRule> buildInitialCorrectionRules() {
        return List.of(
                // TRIM en campos descriptivos de texto — no aplica sobre campos clave
                correctionRule("SUBSCRIBER", "nombre", "TRIM", null),
                correctionRule("AGENT", "nombre", "TRIM", null),
                correctionRule("BUSINESS_LINE", "nombre", "TRIM", null),
                correctionRule("ZIP_CODE", "ciudad", "TRIM", null),
                correctionRule("ZIP_CODE", "estado", "TRIM", null)
        );
    }

    private ValidationRule rule(String dataType, String fieldName, String ruleType,
                                 Map<String, Object> parameters, String errorMessage,
                                 String severity, Instant now) {
        return ValidationRule.builder()
                .dataType(dataType)
                .fieldName(fieldName)
                .ruleType(ruleType)
                .parameters(parameters)
                .errorMessage(errorMessage)
                .severity(severity)
                .enabled(true)
                .createdAt(now)
                .updatedAt(now)
                .createdBy(SYSTEM)
                .build();
    }

    private CorrectionRule correctionRule(String dataType, String fieldName,
                                           String correctionType, Object defaultValue) {
        return CorrectionRule.builder()
                .dataType(dataType)
                .fieldName(fieldName)
                .correctionType(correctionType)
                .defaultValue(defaultValue)
                .enabled(true)
                .build();
    }
}
