package com.plataformas_danos_back.model.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class SubscriberDto {
    private String id;
    private String nombre;
    private String clave;
    private Boolean activo;
    private String dataStatus;

    public SubscriberDto() {}

    public SubscriberDto(String id, String nombre, String clave, Boolean activo) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.activo = activo;
    }
}
