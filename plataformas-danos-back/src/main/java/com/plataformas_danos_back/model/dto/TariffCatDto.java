package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TariffCatDto {
    private String zona;
    private Double factorTEV;
    private Double factorFHM;
    private String dataStatus;

    public TariffCatDto() {}

    public TariffCatDto(String zona, Double factorTEV, Double factorFHM) {
        this.zona = zona;
        this.factorTEV = factorTEV;
        this.factorFHM = factorFHM;
    }
}
