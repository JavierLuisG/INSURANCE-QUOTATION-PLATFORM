package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class TariffElectronicEquipmentDto {
    private String clase;
    private String nivelZona;
    private Double factor;
    private String dataStatus;

    public TariffElectronicEquipmentDto() {}

    public TariffElectronicEquipmentDto(String clase, String nivelZona, Double factor) {
        this.clase = clase;
        this.nivelZona = nivelZona;
        this.factor = factor;
    }
}
