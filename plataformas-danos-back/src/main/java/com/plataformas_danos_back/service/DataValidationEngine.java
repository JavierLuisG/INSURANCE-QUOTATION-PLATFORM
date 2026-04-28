package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.ValidationErrorDetail;
import com.plataformas_danos_back.model.entity.ValidationRule;

import java.util.List;

public interface DataValidationEngine {

    List<ValidationRule> getRulesForDataType(String dataType);

    ValidationErrorDetail applyRule(ValidationRule rule, Object value);
}
