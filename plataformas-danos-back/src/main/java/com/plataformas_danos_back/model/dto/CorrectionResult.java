package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CorrectionResult {

    private boolean corrected;
    private Object originalValue;
    private Object correctedValue;
    private String ruleApplied;
}
