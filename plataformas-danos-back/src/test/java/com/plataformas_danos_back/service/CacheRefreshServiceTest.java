package com.plataformas_danos_back.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheRefreshServiceTest {

    @Mock
    private CatalogsService catalogsService;

    @Mock
    private TariffsService tariffsService;

    @Mock
    private CacheManager cacheManager;

    @InjectMocks
    private CacheRefreshService cacheRefreshService;

    // ── scheduledTask ─────────────────────────────────────────────────────────

    @Test
    void refreshStaticCaches_scheduledTask_evictsAndReloadsSubscribers() {
        // GIVEN
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(catalogsService.getSubscribers()).thenReturn(List.of());
        when(catalogsService.getAgents()).thenReturn(List.of());
        when(catalogsService.getBusinessLines()).thenReturn(List.of());
        when(catalogsService.getRiskClassifications()).thenReturn(List.of());
        when(catalogsService.getGuarantees()).thenReturn(List.of());
        when(tariffsService.getTariffsFire()).thenReturn(List.of());
        when(tariffsService.getTariffsElectronicEquipment()).thenReturn(List.of());

        // WHEN
        cacheRefreshService.refreshStaticCaches();

        // THEN — subscribers cache was evicted and service method called to reload
        verify(cacheManager).getCache("catalogs-subscribers");
        verify(cache, times(7)).clear(); // one clear() per static cache (7 total)
        verify(catalogsService).getSubscribers();
    }

    @Test
    void refreshStaticCaches_allServicesSucceed_callsAllServiceMethods() {
        // GIVEN
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(catalogsService.getSubscribers()).thenReturn(List.of());
        when(catalogsService.getAgents()).thenReturn(List.of());
        when(catalogsService.getBusinessLines()).thenReturn(List.of());
        when(catalogsService.getRiskClassifications()).thenReturn(List.of());
        when(catalogsService.getGuarantees()).thenReturn(List.of());
        when(tariffsService.getTariffsFire()).thenReturn(List.of());
        when(tariffsService.getTariffsElectronicEquipment()).thenReturn(List.of());

        // WHEN
        cacheRefreshService.refreshStaticCaches();

        // THEN — every static cache loader is invoked
        verify(catalogsService).getSubscribers();
        verify(catalogsService).getAgents();
        verify(catalogsService).getBusinessLines();
        verify(catalogsService).getRiskClassifications();
        verify(catalogsService).getGuarantees();
        verify(tariffsService).getTariffsFire();
        verify(tariffsService).getTariffsElectronicEquipment();
    }

    @Test
    void refreshStaticCaches_externalServiceFails_doesNotPropagate() {
        // GIVEN — subscribers service throws; remaining services succeed
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(catalogsService.getSubscribers()).thenThrow(new RuntimeException("Service down"));
        when(catalogsService.getAgents()).thenReturn(List.of());
        when(catalogsService.getBusinessLines()).thenReturn(List.of());
        when(catalogsService.getRiskClassifications()).thenReturn(List.of());
        when(catalogsService.getGuarantees()).thenReturn(List.of());
        when(tariffsService.getTariffsFire()).thenReturn(List.of());
        when(tariffsService.getTariffsElectronicEquipment()).thenReturn(List.of());

        // WHEN / THEN — exception must not propagate out of the scheduler
        assertThatNoException().isThrownBy(() -> cacheRefreshService.refreshStaticCaches());
    }

    @Test
    void refreshStaticCaches_allServicesFail_doesNotPropagate() {
        // GIVEN — every service throws
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache(anyString())).thenReturn(cache);
        when(catalogsService.getSubscribers()).thenThrow(new RuntimeException("Catalogs down"));
        when(catalogsService.getAgents()).thenThrow(new RuntimeException("Catalogs down"));
        when(catalogsService.getBusinessLines()).thenThrow(new RuntimeException("Catalogs down"));
        when(catalogsService.getRiskClassifications()).thenThrow(new RuntimeException("Catalogs down"));
        when(catalogsService.getGuarantees()).thenThrow(new RuntimeException("Catalogs down"));
        when(tariffsService.getTariffsFire()).thenThrow(new RuntimeException("Tariffs down"));
        when(tariffsService.getTariffsElectronicEquipment()).thenThrow(new RuntimeException("Tariffs down"));

        // WHEN / THEN
        assertThatNoException().isThrownBy(() -> cacheRefreshService.refreshStaticCaches());
    }

    @Test
    void refreshStaticCaches_cacheNotPresentInManager_stillCallsLoader() {
        // GIVEN — CacheManager returns null (cache not registered); should still call loader
        when(cacheManager.getCache(anyString())).thenReturn(null);
        when(catalogsService.getSubscribers()).thenReturn(List.of());
        when(catalogsService.getAgents()).thenReturn(List.of());
        when(catalogsService.getBusinessLines()).thenReturn(List.of());
        when(catalogsService.getRiskClassifications()).thenReturn(List.of());
        when(catalogsService.getGuarantees()).thenReturn(List.of());
        when(tariffsService.getTariffsFire()).thenReturn(List.of());
        when(tariffsService.getTariffsElectronicEquipment()).thenReturn(List.of());

        // WHEN / THEN
        assertThatNoException().isThrownBy(() -> cacheRefreshService.refreshStaticCaches());
        verify(catalogsService).getSubscribers();
    }
}
