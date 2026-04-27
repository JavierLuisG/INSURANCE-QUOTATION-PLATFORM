package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TariffElectronicEquipmentDto {
    private String clase;
    private String nivelZona;
    private Double factor;
}
