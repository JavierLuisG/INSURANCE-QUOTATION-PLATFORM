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
public class CotizacionResponse {

    private String folio;

    private String estadoValidacion;

    private String nombreAsegurado;

    private String rfcAsegurado;

    private String tipoSeguroId;

    private String monedaId;

    private String canalVentaId;

    private String fechaInicioVigencia;

    private String fechaFinVigencia;

    private Instant createdAt;

    private Instant updatedAt;

    private Long version;
}
