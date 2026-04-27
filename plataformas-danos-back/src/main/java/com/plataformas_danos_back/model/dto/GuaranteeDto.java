package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GuaranteeDto {
    private String id;
    private String nombre;
    private String claveIncendio;
    private Boolean tarifable;
}
