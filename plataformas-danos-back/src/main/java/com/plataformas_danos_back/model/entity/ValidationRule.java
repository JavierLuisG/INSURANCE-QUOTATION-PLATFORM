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
@Document(collection = "validation-rules")
@CompoundIndexes({
        @CompoundIndex(name = "idx_dataType_fieldName_unique", def = "{'dataType': 1, 'fieldName': 1}", unique = true)
})
public class ValidationRule {

    @Id
    private String id;

    private String dataType;

    private String fieldName;

    private String ruleType;

    private Map<String, Object> parameters;

    private String errorMessage;

    private String severity;

    @Indexed
    @Builder.Default
    private boolean enabled = true;

    private Instant createdAt;

    private Instant updatedAt;

    private String createdBy;
}
