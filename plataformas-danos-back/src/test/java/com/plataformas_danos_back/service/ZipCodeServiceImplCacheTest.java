package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.ZipCodeClient;
import com.plataformas_danos_back.model.dto.ZipCodeDto;
import com.plataformas_danos_back.repository.CorrectionRuleRepository;
import com.plataformas_danos_back.repository.DataInconsistencyRepository;
import com.plataformas_danos_back.repository.ValidationRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
@ActiveProfiles("test")
class ZipCodeServiceImplCacheTest {

    @MockitoBean
    private ZipCodeClient zipCodeClient;

    @MockitoBean
    private ValidationRuleRepository validationRuleRepository;

    @MockitoBean
    private CorrectionRuleRepository correctionRuleRepository;

    @MockitoBean
    private DataInconsistencyRepository dataInconsistencyRepository;

    @Autowired
    private ZipCodeService zipCodeService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        when(validationRuleRepository.findByDataTypeAndEnabled(any(), anyBoolean())).thenReturn(List.of());
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled(any(), any(), anyBoolean()))
                .thenReturn(Optional.empty());
        Cache cache = cacheManager.getCache("zip-codes");
        if (cache != null) cache.clear();
    }

    // ── getByZipCode ──────────────────────────────────────────────────────────

    @Test
    void getByZipCode_sameZip_secondCall_doesNotInvokeClient() {
        // GIVEN
        var dto = new ZipCodeDto("12345", "ZONA_A", "NIVEL_1", "CDMX", "BJ", "Ciudad de Mexico");
        when(zipCodeClient.getByZipCode("12345")).thenReturn(dto);

        // WHEN
        zipCodeService.getByZipCode("12345");
        zipCodeService.getByZipCode("12345");

        // THEN — same zip → cache hit on second call; client called once
        verify(zipCodeClient, times(1)).getByZipCode("12345");
    }
}
