package com.plataformas_danos_back.model.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CotizacionRequest {

    @Size(max = 255, message = "El nombre del asegurado no puede exceder 255 caracteres")
    private String nombreAsegurado;

    private String rfcAsegurado;

    private String tipoSeguroId;

    private String monedaId;

    private String canalVentaId;

    private String fechaInicioVigencia;

    private String fechaFinVigencia;

    private Long version;
}
