package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class BusinessLineDto {
    private String id;
    private String descripcion;
    private String claveIncendio;
    private Boolean activo;
    private String dataStatus;

    public BusinessLineDto() {}

    public BusinessLineDto(String id, String descripcion, String claveIncendio, Boolean activo) {
        this.id = id;
        this.descripcion = descripcion;
        this.claveIncendio = claveIncendio;
        this.activo = activo;
    }
}
