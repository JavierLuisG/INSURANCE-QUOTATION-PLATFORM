package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.ValidationErrorDetail;
import com.plataformas_danos_back.model.entity.ValidationRule;
import com.plataformas_danos_back.repository.ValidationRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataValidationEngineImpl implements DataValidationEngine {

    private final ValidationRuleRepository validationRuleRepository;

    @Override
    public List<ValidationRule> getRulesForDataType(String dataType) {
        return validationRuleRepository.findByDataTypeAndEnabled(dataType, true);
    }

    @Override
    public ValidationErrorDetail applyRule(ValidationRule rule, Object value) {
        boolean violated = switch (rule.getRuleType()) {
            case "NOT_NULL" -> value == null;
            case "NOT_EMPTY" -> value == null || value.toString().isBlank();
            case "POSITIVE_NUMBER" -> isNotPositiveNumber(rule, value);
            case "FORMAT_REGEX" -> isNotMatchingRegex(rule, value);
            default -> {
                log.warn("Tipo de regla no reconocido: {}", rule.getRuleType());
                yield false;
            }
        };

        if (!violated) {
            return null;
        }

        return ValidationErrorDetail.builder()
                .field(rule.getFieldName())
                .rule(rule.getRuleType())
                .message(rule.getErrorMessage())
                .severity(rule.getSeverity())
                .build();
    }

    private boolean isNotPositiveNumber(ValidationRule rule, Object value) {
        if (value == null) {
            return true;
        }
        try {
            double number = Double.parseDouble(value.toString());
            return number <= 0;
        } catch (NumberFormatException ex) {
            log.warn("Error de esquema en campo '{}': se esperaba numérico, se recibió '{}'",
                    rule.getFieldName(), value);
            return true;
        }
    }

    private boolean isNotMatchingRegex(ValidationRule rule, Object value) {
        if (value == null) {
            return true;
        }
        Object patternObj = rule.getParameters() != null ? rule.getParameters().get("pattern") : null;
        if (patternObj == null) {
            log.warn("Regla FORMAT_REGEX para campo '{}' no tiene parámetro 'pattern'", rule.getFieldName());
            return false;
        }
        return !value.toString().matches(patternObj.toString());
    }
}
