package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ZipCodeDto {
    private String codigoPostal;
    private String zonaCAT;
    private String nivelTecnico;
    private String estado;
    private String municipio;
    private String ciudad;
    private String dataStatus;

    public ZipCodeDto() {}

    public ZipCodeDto(String codigoPostal, String zonaCAT, String nivelTecnico,
                      String estado, String municipio, String ciudad) {
        this.codigoPostal = codigoPostal;
        this.zonaCAT = zonaCAT;
        this.nivelTecnico = nivelTecnico;
        this.estado = estado;
        this.municipio = municipio;
        this.ciudad = ciudad;
    }
}
