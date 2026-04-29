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
public class ParametrosStatusResponse {

    private TarifasStatus tarifasIncendio;
    private TarifasStatus tarifasCAT;
    private TarifaFHMStatus tarifasFHM;
    private TarifasStatus catalogoCPZonas;
    private String circuitBreakerStatus;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TarifasStatus {
        private boolean disponible;
        private Instant ultimaActualizacion;
        private long cantidad;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TarifaFHMStatus {
        private boolean disponible;
        private Instant ultimaActualizacion;
    }
}
