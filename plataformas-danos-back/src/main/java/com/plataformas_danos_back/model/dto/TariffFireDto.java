package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TariffFireDto {
    private String zonaRiesgo;
    private String tipoConstructivo;
    private Double tasaBase;
    private Double factorRecargo;
    private String dataStatus;

    public TariffFireDto() {}

    public TariffFireDto(String zonaRiesgo, String tipoConstructivo, Double tasaBase, Double factorRecargo) {
        this.zonaRiesgo = zonaRiesgo;
        this.tipoConstructivo = tipoConstructivo;
        this.tasaBase = tasaBase;
        this.factorRecargo = factorRecargo;
    }
}
