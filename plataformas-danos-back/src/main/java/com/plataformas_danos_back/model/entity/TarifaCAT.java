package com.plataformas_danos_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tarifas_cat")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_zonacat_vigencia",
                def = "{'zonaCAT': 1, 'fechaVigenciaInicio': 1}"
        )
})
public class TarifaCAT {

    @Id
    private String id;

    private String zonaCAT;

    private BigDecimal factorCAT;

    private LocalDate fechaVigenciaInicio;

    private LocalDate fechaVigenciaFin;

    private Instant createdAt;

    private Instant updatedAt;

    private String origen;
}
