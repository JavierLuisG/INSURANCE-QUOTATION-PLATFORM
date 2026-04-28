package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.ValidationRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface ValidationRuleRepository extends MongoRepository<ValidationRule, String> {

    List<ValidationRule> findByDataTypeAndEnabled(String dataType, boolean enabled);

    Optional<ValidationRule> findByDataTypeAndFieldName(String dataType, String fieldName);

    List<ValidationRule> findByEnabled(boolean enabled);

    List<ValidationRule> findByDataType(String dataType);
}
