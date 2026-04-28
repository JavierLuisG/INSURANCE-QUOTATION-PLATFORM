package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.ValidationResult;
import com.plataformas_danos_back.model.entity.DataInconsistency;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DataValidationService {

    ValidationResult validateBatch(String dataType, List<Map<String, Object>> records, String correlationId);

    Page<DataInconsistency> getInconsistencies(String dataType, String status, Pageable pageable);
}
