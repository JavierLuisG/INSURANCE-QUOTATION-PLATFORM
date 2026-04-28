package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.CatalogsClient;
import com.plataformas_danos_back.repository.CorrectionRuleRepository;
import com.plataformas_danos_back.repository.DataInconsistencyRepository;
import com.plataformas_danos_back.repository.ValidationRuleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class CatalogsServiceImplCacheTest {

    @MockitoBean
    private CatalogsClient catalogsClient;

    @MockitoBean
    private ValidationRuleRepository validationRuleRepository;

    @MockitoBean
    private CorrectionRuleRepository correctionRuleRepository;

    @MockitoBean
    private DataInconsistencyRepository dataInconsistencyRepository;

    @Autowired
    private CatalogsService catalogsService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void setUp() {
        when(validationRuleRepository.findByDataTypeAndEnabled(any(), anyBoolean())).thenReturn(List.of());
        when(correctionRuleRepository.findByDataTypeAndFieldNameAndEnabled(any(), any(), anyBoolean()))
                .thenReturn(Optional.empty());
        Stream.of("catalogs-subscribers", "catalogs-agents", "catalogs-business-lines",
                        "catalogs-risk-classifications", "catalogs-guarantees")
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(org.springframework.cache.Cache::clear);
    }

    // ── getSubscribers ────────────────────────────────────────────────────────

    @Test
    void getSubscribers_secondCall_doesNotInvokeCatalogsClient() {
        // GIVEN
        when(catalogsClient.getSubscribers()).thenReturn(List.of());

        // WHEN
        catalogsService.getSubscribers();
        catalogsService.getSubscribers();

        // THEN — client invoked exactly once; second call served from cache
        verify(catalogsClient, times(1)).getSubscribers();
    }

    // ── getAgents ─────────────────────────────────────────────────────────────

    @Test
    void getAgents_secondCall_doesNotInvokeClient() {
        // GIVEN
        when(catalogsClient.getAgents()).thenReturn(List.of());

        // WHEN
        catalogsService.getAgents();
        catalogsService.getAgents();

        // THEN
        verify(catalogsClient, times(1)).getAgents();
    }

    // ── getBusinessLines ──────────────────────────────────────────────────────

    @Test
    void getBusinessLines_secondCall_doesNotInvokeClient() {
        // GIVEN
        when(catalogsClient.getBusinessLines()).thenReturn(List.of());

        // WHEN
        catalogsService.getBusinessLines();
        catalogsService.getBusinessLines();

        // THEN
        verify(catalogsClient, times(1)).getBusinessLines();
    }

    // ── getRiskClassifications ────────────────────────────────────────────────

    @Test
    void getRiskClassifications_secondCall_doesNotInvokeClient() {
        // GIVEN
        when(catalogsClient.getRiskClassifications()).thenReturn(List.of());

        // WHEN
        catalogsService.getRiskClassifications();
        catalogsService.getRiskClassifications();

        // THEN
        verify(catalogsClient, times(1)).getRiskClassifications();
    }

    // ── getGuarantees ─────────────────────────────────────────────────────────

    @Test
    void getGuarantees_secondCall_doesNotInvokeClient() {
        // GIVEN
        when(catalogsClient.getGuarantees()).thenReturn(List.of());

        // WHEN
        catalogsService.getGuarantees();
        catalogsService.getGuarantees();

        // THEN
        verify(catalogsClient, times(1)).getGuarantees();
    }
}
