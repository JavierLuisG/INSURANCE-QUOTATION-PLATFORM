package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TarifaIncendioResponse {

    private String id;
    private String zonaGeografica;
    private String tipoInmueble;
    private BigDecimal tarifaBase;
    private LocalDate fechaVigenciaInicio;
    private LocalDate fechaVigenciaFin;
    private String origen;
}
