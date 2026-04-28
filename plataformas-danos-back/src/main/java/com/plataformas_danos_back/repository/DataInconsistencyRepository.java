package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.DataInconsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DataInconsistencyRepository extends MongoRepository<DataInconsistency, String> {

    List<DataInconsistency> findByDataType(String dataType);

    List<DataInconsistency> findByStatus(String status);

    List<DataInconsistency> findByDataTypeAndStatus(String dataType, String status);

    Page<DataInconsistency> findAll(Pageable pageable);

    Page<DataInconsistency> findByDataType(String dataType, Pageable pageable);

    Page<DataInconsistency> findByStatus(String status, Pageable pageable);

    Page<DataInconsistency> findByDataTypeAndStatus(String dataType, String status, Pageable pageable);
}
