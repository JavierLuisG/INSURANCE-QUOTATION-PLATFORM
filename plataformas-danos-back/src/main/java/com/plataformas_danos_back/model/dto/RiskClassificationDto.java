package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class RiskClassificationDto {
    private String id;
    private String nombre;
    private String descripcion;
    private String dataStatus;

    public RiskClassificationDto() {}

    public RiskClassificationDto(String id, String nombre, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
    }
}
