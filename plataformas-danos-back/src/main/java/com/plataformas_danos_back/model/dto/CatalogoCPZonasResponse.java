package com.plataformas_danos_back.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CatalogoCPZonasResponse {

    private String codigoPostal;
    private String zonaCAT;
    private String nivelTecnico;
    private Instant fechaCarga;
}
