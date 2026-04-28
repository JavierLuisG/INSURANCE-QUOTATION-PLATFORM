package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.CorrectionResult;
import com.plataformas_danos_back.model.entity.CorrectionRule;

import java.util.Optional;

public interface DataCorrectionService {

    CorrectionResult applyCorrection(String dataType, String fieldName, Object value);

    boolean hasCorrectionRule(String dataType, String fieldName);

    Optional<CorrectionRule> findCorrectionRule(String dataType, String fieldName);
}
