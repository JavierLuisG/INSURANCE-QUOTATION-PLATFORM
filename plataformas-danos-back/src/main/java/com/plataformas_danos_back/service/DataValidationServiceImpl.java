package com.plataformas_danos_back.service;

import com.plataformas_danos_back.config.ValidationProperties;
import com.plataformas_danos_back.model.dto.CorrectionResult;
import com.plataformas_danos_back.model.dto.ValidationErrorDetail;
import com.plataformas_danos_back.model.dto.ValidationResult;
import com.plataformas_danos_back.model.entity.DataInconsistency;
import com.plataformas_danos_back.model.entity.ValidationRule;
import com.plataformas_danos_back.repository.DataInconsistencyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataValidationServiceImpl implements DataValidationService {

    private static final String STATUS_VALIDATED = "VALIDATED";
    private static final String STATUS_INCONSISTENT = "INCONSISTENT";
    private static final String SEVERITY_CRITICAL = "CRITICAL";

    private final DataValidationEngine dataValidationEngine;
    private final DataCorrectionService dataCorrectionService;
    private final InconsistencyNotificationService notificationService;
    private final DataInconsistencyRepository inconsistencyRepository;
    private final ValidationProperties validationProperties;

    @Override
    public ValidationResult validateBatch(String dataType, List<Map<String, Object>> records, String correlationId) {
        List<ValidationResult.RecordValidationResult> results = new ArrayList<>();
        int inconsistentCount = 0;

        List<ValidationRule> rules = dataValidationEngine.getRulesForDataType(dataType);

        for (Map<String, Object> record : records) {
            String recordId = resolveRecordId(record);
            ValidationResult.RecordValidationResult result = processRecord(dataType, record, recordId, rules, correlationId);
            results.add(result);
            if (STATUS_INCONSISTENT.equals(result.getStatus())) {
                inconsistentCount++;
            }
        }

        int total = records.size();
        int validCount = total - inconsistentCount;

        checkBatchThreshold(total, inconsistentCount, dataType);

        return ValidationResult.builder()
                .totalRecords(total)
                .validRecords(validCount)
                .inconsistentRecords(inconsistentCount)
                .results(results)
                .build();
    }

    @Override
    public Page<DataInconsistency> getInconsistencies(String dataType, String status, Pageable pageable) {
        if (dataType != null && status != null) {
            return inconsistencyRepository.findByDataTypeAndStatus(dataType, status, pageable);
        }
        if (dataType != null) {
            return inconsistencyRepository.findByDataType(dataType, pageable);
        }
        if (status != null) {
            return inconsistencyRepository.findByStatus(status, pageable);
        }
        return inconsistencyRepository.findAll(pageable);
    }

    private ValidationResult.RecordValidationResult processRecord(
            String dataType,
            Map<String, Object> record,
            String recordId,
            List<ValidationRule> rules,
            String correlationId) {

        List<ValidationErrorDetail> errors = collectErrors(rules, record);

        if (errors.isEmpty()) {
            return ValidationResult.RecordValidationResult.builder()
                    .id(recordId)
                    .status(STATUS_VALIDATED)
                    .errors(List.of())
                    .build();
        }

        return attemptCorrection(dataType, record, recordId, errors, correlationId);
    }

    private List<ValidationErrorDetail> collectErrors(List<ValidationRule> rules, Map<String, Object> record) {
        List<ValidationErrorDetail> errors = new ArrayList<>();
        for (ValidationRule rule : rules) {
            Object fieldValue = record.get(rule.getFieldName());
            ValidationErrorDetail error = dataValidationEngine.applyRule(rule, fieldValue);
            if (error != null) {
                errors.add(error);
            }
        }
        return errors;
    }

    private ValidationResult.RecordValidationResult attemptCorrection(
            String dataType,
            Map<String, Object> record,
            String recordId,
            List<ValidationErrorDetail> errors,
            String correlationId) {

        List<ValidationErrorDetail> uncorrectedErrors = new ArrayList<>();
        DataInconsistency.CorrectionDetail correctionDetail = null;
        boolean anyCorrected = false;

        for (ValidationErrorDetail error : errors) {
            if (dataCorrectionService.hasCorrectionRule(dataType, error.getField())) {
                Object originalValue = record.get(error.getField());
                CorrectionResult correction = dataCorrectionService.applyCorrection(dataType, error.getField(), originalValue);
                if (correction.isCorrected()) {
                    record.put(error.getField(), correction.getCorrectedValue());
                    anyCorrected = true;
                    correctionDetail = DataInconsistency.CorrectionDetail.builder()
                            .originalValue(correction.getOriginalValue())
                            .correctedValue(correction.getCorrectedValue())
                            .ruleApplied(correction.getRuleApplied())
                            .build();
                    log.info("Corrección automática aplicada: dataType={}, field={}, rule={}",
                            dataType, error.getField(), correction.getRuleApplied());
                    continue;
                }
            }
            uncorrectedErrors.add(error);
        }

        if (uncorrectedErrors.isEmpty()) {
            return ValidationResult.RecordValidationResult.builder()
                    .id(recordId)
                    .status(STATUS_VALIDATED)
                    .errors(List.of())
                    .correctionApplied(true)
                    .build();
        }

        DataInconsistency saved = persistInconsistency(
                dataType, recordId, record, uncorrectedErrors.get(0), correlationId, anyCorrected, correctionDetail);

        if (SEVERITY_CRITICAL.equals(uncorrectedErrors.get(0).getSeverity())) {
            notificationService.notifyCritical(saved);
        }

        return ValidationResult.RecordValidationResult.builder()
                .id(recordId)
                .status(STATUS_INCONSISTENT)
                .errors(uncorrectedErrors)
                .correctionApplied(anyCorrected)
                .build();
    }

    private DataInconsistency persistInconsistency(
            String dataType,
            String dataId,
            Map<String, Object> record,
            ValidationErrorDetail primaryError,
            String correlationId,
            boolean correctionApplied,
            DataInconsistency.CorrectionDetail correctionDetail) {

        DataInconsistency inconsistency = DataInconsistency.builder()
                .id(UUID.randomUUID().toString())
                .dataType(dataType)
                .dataId(dataId)
                .value(record)
                .validationError(DataInconsistency.ValidationErrorDetail.builder()
                        .field(primaryError.getField())
                        .rule(primaryError.getRule())
                        .message(primaryError.getMessage())
                        .severity(primaryError.getSeverity())
                        .build())
                .status(primaryError.getSeverity())
                .correlationId(correlationId)
                .createdAt(Instant.now())
                .correctionApplied(correctionApplied)
                .correctionDetail(correctionDetail)
                .build();

        try {
            return inconsistencyRepository.save(inconsistency);
        } catch (Exception ex) {
            log.error("PERSISTENCE_ERROR al guardar inconsistencia dataType={} dataId={}: {}",
                    dataType, dataId, ex.getMessage());
            return inconsistency;
        }
    }

    private void checkBatchThreshold(int total, int inconsistentCount, String dataType) {
        if (total == 0) {
            return;
        }
        double percent = (inconsistentCount * 100.0) / total;
        if (percent > validationProperties.getInconsistencyThresholdPercent()) {
            notificationService.notifyBatchThresholdExceeded(total, inconsistentCount, dataType);
        }
    }

    private String resolveRecordId(Map<String, Object> record) {
        for (String candidate : List.of("id", "codigo", "zona", "clase")) {
            Object val = record.get(candidate);
            if (val != null && !val.toString().isBlank()) {
                return val.toString();
            }
        }
        return UUID.randomUUID().toString();
    }
}
