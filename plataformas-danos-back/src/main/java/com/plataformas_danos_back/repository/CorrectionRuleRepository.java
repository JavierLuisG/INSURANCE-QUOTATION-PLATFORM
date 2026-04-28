package com.plataformas_danos_back.repository;

import com.plataformas_danos_back.model.entity.CorrectionRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface CorrectionRuleRepository extends MongoRepository<CorrectionRule, String> {

    Optional<CorrectionRule> findByDataTypeAndFieldNameAndEnabled(String dataType, String fieldName, boolean enabled);

    List<CorrectionRule> findByDataTypeAndEnabled(String dataType, boolean enabled);
}
