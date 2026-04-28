package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.CorrectionResult;
import com.plataformas_danos_back.model.entity.CorrectionRule;
import com.plataformas_danos_back.repository.CorrectionRuleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCorrectionServiceImpl implements DataCorrectionService {

    private final CorrectionRuleRepository correctionRuleRepository;
    private final ApplicationContext applicationContext;

    @Override
    public CorrectionResult applyCorrection(String dataType, String fieldName, Object value) {
        Optional<CorrectionRule> ruleOpt = self().findCorrectionRule(dataType, fieldName);

        if (ruleOpt.isEmpty()) {
            return CorrectionResult.builder()
                    .corrected(false)
                    .originalValue(value)
                    .build();
        }

        CorrectionRule rule = ruleOpt.get();
        Object correctedValue = applyCorrectionType(rule, value);

        return CorrectionResult.builder()
                .corrected(true)
                .originalValue(value)
                .correctedValue(correctedValue)
                .ruleApplied(rule.getCorrectionType())
                .build();
    }

    @Override
    public boolean hasCorrectionRule(String dataType, String fieldName) {
        return self().findCorrectionRule(dataType, fieldName).isPresent();
    }

    @Override
    @Cacheable(value = "correction-rules", key = "#dataType + ':' + #fieldName")
    public Optional<CorrectionRule> findCorrectionRule(String dataType, String fieldName) {
        return correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled(dataType, fieldName, true);
    }

    private DataCorrectionService self() {
        return applicationContext.getBean(DataCorrectionService.class);
    }

    private Object applyCorrectionType(CorrectionRule rule, Object value) {
        return switch (rule.getCorrectionType()) {
            case "TRIM" -> value != null ? value.toString().trim() : null;
            case "DEFAULT_VALUE" -> rule.getDefaultValue();
            case "NORMALIZE_CASE" -> value != null ? value.toString().toUpperCase() : null;
            default -> {
                log.warn("Tipo de corrección no reconocido: {}", rule.getCorrectionType());
                yield value;
            }
        };
    }
}
