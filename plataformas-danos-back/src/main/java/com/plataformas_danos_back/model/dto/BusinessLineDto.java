package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BusinessLineDto {
    private String id;
    private String descripcion;
    private String claveIncendio;
    private Boolean activo;
}
