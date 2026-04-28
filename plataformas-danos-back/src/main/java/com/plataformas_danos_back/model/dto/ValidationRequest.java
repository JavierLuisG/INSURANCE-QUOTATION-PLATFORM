package com.plataformas_danos_back.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationRequest {

    @NotBlank(message = "El campo dataType es obligatorio")
    private String dataType;

    @NotEmpty(message = "La lista de registros no puede estar vacía")
    private List<Map<String, Object>> records;

    private String correlationId;
}
