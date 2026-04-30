package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.Cotizacion;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface CotizacionRepository extends MongoRepository<Cotizacion, String> {

    Optional<Cotizacion> findByFolio(String folio);
}
