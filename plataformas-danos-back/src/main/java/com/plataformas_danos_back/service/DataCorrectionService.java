package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.CorrectionResult;

public interface DataCorrectionService {

    CorrectionResult applyCorrection(String dataType, String fieldName, Object value);

    boolean hasCorrectionRule(String dataType, String fieldName);
}
