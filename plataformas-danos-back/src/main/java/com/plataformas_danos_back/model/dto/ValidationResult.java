package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationResult {

    private int totalRecords;
    private int validRecords;
    private int inconsistentRecords;
    private List<RecordValidationResult> results;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecordValidationResult {
        private String id;
        private String status;
        private List<ValidationErrorDetail> errors;
        @Builder.Default
        private boolean correctionApplied = false;
    }
}
