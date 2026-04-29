package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.CatalogoCPZonas;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CatalogoCPZonasRepository extends MongoRepository<CatalogoCPZonas, String> {

    Optional<CatalogoCPZonas> findByCodigoPostal(String codigoPostal);

    List<CatalogoCPZonas> findByZonaCAT(String zonaCAT);
}
