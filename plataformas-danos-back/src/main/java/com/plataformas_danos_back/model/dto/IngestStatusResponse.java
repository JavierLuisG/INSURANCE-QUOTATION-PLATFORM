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
public class IngestStatusResponse {

    private String requestId;
    private String status;
    private String mensaje;
    private Instant timestamp;
}
