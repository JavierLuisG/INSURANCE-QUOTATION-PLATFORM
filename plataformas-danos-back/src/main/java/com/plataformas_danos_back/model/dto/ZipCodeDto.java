package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZipCodeDto {
    private String codigoPostal;
    private String zonaCAT;
    private String nivelTecnico;
    private String estado;
    private String municipio;
    private String ciudad;
}
