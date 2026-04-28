package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class GuaranteeDto {
    private String id;
    private String nombre;
    private String claveIncendio;
    private Boolean tarifable;
    private String dataStatus;

    public GuaranteeDto() {}

    public GuaranteeDto(String id, String nombre, String claveIncendio, Boolean tarifable) {
        this.id = id;
        this.nombre = nombre;
        this.claveIncendio = claveIncendio;
        this.tarifable = tarifable;
    }
}
