package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.TarifaIncendio;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface TarifaIncendioRepository extends MongoRepository<TarifaIncendio, String> {

    List<TarifaIncendio> findByZonaGeograficaAndTipoInmueble(String zonaGeografica, String tipoInmueble);

    List<TarifaIncendio> findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(LocalDate fecha);
}
