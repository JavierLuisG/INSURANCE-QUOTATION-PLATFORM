package com.plataformas_danos_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Version;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "cotizaciones")
public class Cotizacion {

    @Id
    private String id;

    @Indexed(unique = true)
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

    @Version
    private Long version;
}
