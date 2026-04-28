package com.plataformas_danos_back.service;

import com.plataformas_danos_back.config.ValidationProperties;
import com.plataformas_danos_back.model.entity.DataInconsistency;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InconsistencyNotificationServiceImpl implements InconsistencyNotificationService {

    private final ValidationProperties validationProperties;

    @Override
    public void notifyCritical(DataInconsistency inconsistency) {
        try {
            String field = inconsistency.getValidationError() != null
                    ? inconsistency.getValidationError().getField()
                    : "desconocido";
            String rule = inconsistency.getValidationError() != null
                    ? inconsistency.getValidationError().getRule()
                    : "desconocido";

            log.error(
                    "{{\"event\":\"CRITICAL_INCONSISTENCY\","
                    + "\"inconsistencyId\":\"{}\","
                    + "\"dataType\":\"{}\","
                    + "\"dataId\":\"{}\","
                    + "\"field\":\"{}\","
                    + "\"rule\":\"{}\","
                    + "\"correlationId\":\"{}\"}}",
                    inconsistency.getId(),
                    inconsistency.getDataType(),
                    inconsistency.getDataId(),
                    field,
                    rule,
                    inconsistency.getCorrelationId()
            );
        } catch (Exception ex) {
            log.error("Fallo al notificar inconsistencia crítica id={}: {}", inconsistency.getId(), ex.getMessage());
        }
    }

    @Override
    public void notifyBatchThresholdExceeded(int total, int inconsistentCount, String dataType) {
        try {
            double percent = total > 0 ? (inconsistentCount * 100.0) / total : 0.0;
            String percentFormatted = String.format("%.2f", percent);
            log.error(
                    "{{\"event\":\"BATCH_THRESHOLD_EXCEEDED\","
                    + "\"dataType\":\"{}\","
                    + "\"totalRecords\":{},\"inconsistentRecords\":{},"
                    + "\"inconsistencyPercent\":{},"
                    + "\"thresholdPercent\":{}}}",
                    dataType,
                    total,
                    inconsistentCount,
                    percentFormatted,
                    validationProperties.getInconsistencyThresholdPercent()
            );
        } catch (Exception ex) {
            log.error("Fallo al notificar umbral de lote dataType={}: {}", dataType, ex.getMessage());
        }
    }
}
