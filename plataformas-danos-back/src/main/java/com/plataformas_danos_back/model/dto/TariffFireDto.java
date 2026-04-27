package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffFireDto {
    private String zonaRiesgo;
    private String tipoConstructivo;
    private Double tasaBase;
    private Double factorRecargo;
}
