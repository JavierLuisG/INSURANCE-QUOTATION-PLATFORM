package com.plataformas_danos_back.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "catalogo_cp_zonas")
public class CatalogoCPZonas {

    @Id
    private String id;

    @Indexed(unique = true)
    private String codigoPostal;

    @Indexed
    private String zonaCAT;

    private String nivelTecnico;

    private Instant fechaCarga;

    private String origen;
}
