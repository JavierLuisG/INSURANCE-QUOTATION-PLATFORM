package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RiskClassificationDto {
    private String id;
    private String nombre;
    private String descripcion;
}
