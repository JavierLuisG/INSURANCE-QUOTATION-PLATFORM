package com.plataformas_danos_back.service;

import com.plataformas_danos_back.repository.ValidationRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class DataValidationEngineCacheTest {

    @MockitoBean
    private ValidationRuleRepository validationRuleRepository;

    @Autowired
    private DataValidationEngine dataValidationEngine;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        Cache cache = cacheManager.getCache("validation-rules");
        if (cache != null) cache.clear();
    }

    // ── getRulesForDataType ───────────────────────────────────────────────────

    @Test
    void getRulesForDataType_sameDataType_secondCall_doesNotInvokeRepository() {
        // GIVEN
        when(validationRuleRepository.findByDataTypeAndEnabled("SUBSCRIBER", true))
                .thenReturn(List.of());

        // WHEN
        dataValidationEngine.getRulesForDataType("SUBSCRIBER");
        dataValidationEngine.getRulesForDataType("SUBSCRIBER");

        // THEN — same dataType → cache hit on second call; repository called once
        verify(validationRuleRepository, times(1)).findByDataTypeAndEnabled("SUBSCRIBER", true);
    }
}
