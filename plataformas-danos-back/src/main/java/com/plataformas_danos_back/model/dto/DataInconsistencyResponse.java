package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataInconsistencyResponse {

    private String id;
    private String dataType;
    private String dataId;
    private ValidationErrorDetail validationError;
    private String status;
    private String correlationId;
    private Instant createdAt;
    private Instant resolvedAt;
    private String resolution;
    private boolean correctionApplied;
}
