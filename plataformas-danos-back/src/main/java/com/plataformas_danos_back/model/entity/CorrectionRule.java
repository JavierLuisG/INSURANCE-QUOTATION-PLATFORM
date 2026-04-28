package com.plataformas_danos_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "correction-rules")
@CompoundIndexes({
        @CompoundIndex(name = "idx_dataType_fieldName_enabled", def = "{'dataType': 1, 'fieldName': 1, 'enabled': 1}")
})
public class CorrectionRule {

    @Id
    private String id;

    private String dataType;

    private String fieldName;

    private String correctionType;

    private Object defaultValue;

    @Builder.Default
    private boolean enabled = true;
}
