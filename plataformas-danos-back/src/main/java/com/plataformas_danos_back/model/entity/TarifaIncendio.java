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
@Document(collection = "tarifas_incendio")
@CompoundIndexes({
        @CompoundIndex(
                name = "idx_zona_tipo_vigencia",
                def = "{'zonaGeografica': 1, 'tipoInmueble': 1, 'fechaVigenciaInicio': 1}"
        )
})
public class TarifaIncendio {

    @Id
    private String id;

    private String zonaGeografica;

    private String tipoInmueble;

    private BigDecimal tarifaBase;

    private LocalDate fechaVigenciaInicio;

    private LocalDate fechaVigenciaFin;

    private Instant createdAt;

    private Instant updatedAt;

    private String origen;
}
