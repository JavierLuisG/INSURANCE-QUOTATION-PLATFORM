package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.TarifaCAT;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface TarifaCATRepository extends MongoRepository<TarifaCAT, String> {

    List<TarifaCAT> findByZonaCAT(String zonaCAT);

    List<TarifaCAT> findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(LocalDate fecha);
}
