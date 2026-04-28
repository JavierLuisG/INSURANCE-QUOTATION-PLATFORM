package com.plataformas_danos_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "data-inconsistencies")
@CompoundIndexes({
        @CompoundIndex(name = "idx_dataType_createdAt", def = "{'dataType': 1, 'createdAt': -1}"),
        @CompoundIndex(name = "idx_dataType_status", def = "{'dataType': 1, 'status': 1}")
})
public class DataInconsistency {

    @Id
    private String id;

    private String dataType;

    @Indexed
    private String dataId;

    private Map<String, Object> value;

    private ValidationErrorDetail validationError;

    @Indexed
    private String status;

    private String correlationId;

    @Indexed(expireAfterSeconds = 7776000) // 90 días TTL
    private Instant createdAt;

    private Instant resolvedAt;

    private String resolution;

    @Builder.Default
    private boolean correctionApplied = false;

    private CorrectionDetail correctionDetail;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorDetail {
        private String field;
        private String rule;
        private String message;
        private String severity;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CorrectionDetail {
        private Object originalValue;
        private Object correctedValue;
        private String ruleApplied;
    }
}
