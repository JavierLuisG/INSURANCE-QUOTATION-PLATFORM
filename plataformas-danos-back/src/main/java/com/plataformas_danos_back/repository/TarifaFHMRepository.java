package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.TarifaFHM;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TarifaFHMRepository extends MongoRepository<TarifaFHM, String> {

    Optional<TarifaFHM> findFirstByOrderByCreatedAtDesc();
}
