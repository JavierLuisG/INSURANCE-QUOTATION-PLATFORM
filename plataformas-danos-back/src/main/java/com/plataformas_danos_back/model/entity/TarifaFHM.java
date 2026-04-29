package com.plataformas_danos_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tarifas_fhm")
public class TarifaFHM {

    @Id
    private String id;

    private BigDecimal tarifaFHM;

    private BigDecimal factorEquipoElectronico;

    private LocalDate fechaVigenciaInicio;

    private LocalDate fechaVigenciaFin;

    private Instant createdAt;

    private Instant updatedAt;

    private String origen;
}
