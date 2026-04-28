package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.TariffsClient;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest
class TariffsServiceImplCacheTest {

    @MockitoBean
    private TariffsClient tariffsClient;

    @Autowired
    private TariffsService tariffsService;

    @Autowired
    private CacheManager cacheManager;

    @BeforeEach
    void clearCaches() {
        Stream.of("tariffs-fire", "tariffs-cat", "tariffs-electronic-equipment")
                .map(cacheManager::getCache)
                .filter(Objects::nonNull)
                .forEach(org.springframework.cache.Cache::clear);
    }

    // ── getTariffsFire ────────────────────────────────────────────────────────

    @Test
    void getTariffsFire_secondCall_doesNotInvokeClient() {
        // GIVEN
        when(tariffsClient.getTariffsFire()).thenReturn(List.of());

        // WHEN
        tariffsService.getTariffsFire();
        tariffsService.getTariffsFire();

        // THEN — client invoked exactly once; second call served from cache
        verify(tariffsClient, times(1)).getTariffsFire();
    }

    // ── getTariffsElectronicEquipment ─────────────────────────────────────────

    @Test
    void getTariffsElectronicEquipment_secondCall_doesNotInvokeClient() {
        // GIVEN
        when(tariffsClient.getTariffsElectronicEquipment()).thenReturn(List.of());

        // WHEN
        tariffsService.getTariffsElectronicEquipment();
        tariffsService.getTariffsElectronicEquipment();

        // THEN
        verify(tariffsClient, times(1)).getTariffsElectronicEquipment();
    }

    // ── getTariffCat — same zona ───────────────────────────────────────────────

    @Test
    void getTariffCat_sameZona_secondCall_doesNotInvokeClient() {
        // GIVEN
        var dto = new TariffCatDto("A", 1.5, 2.0);
        when(tariffsClient.getTariffCat("A")).thenReturn(dto);

        // WHEN
        tariffsService.getTariffCat("A");
        tariffsService.getTariffCat("A");

        // THEN — same key → cache hit on second call
        verify(tariffsClient, times(1)).getTariffCat("A");
    }

    // ── getTariffCat — different zona ─────────────────────────────────────────

    @Test
    void getTariffCat_differentZona_callsClientEachTime() {
        // GIVEN — two distinct zones; each has its own cache entry
        when(tariffsClient.getTariffCat("A")).thenReturn(new TariffCatDto("A", 1.5, 2.0));
        when(tariffsClient.getTariffCat("B")).thenReturn(new TariffCatDto("B", 3.0, 4.0));

        // WHEN
        tariffsService.getTariffCat("A");
        tariffsService.getTariffCat("B");

        // THEN — zone "A" and zone "B" are different keys → each triggers a client call
        verify(tariffsClient, times(1)).getTariffCat("A");
        verify(tariffsClient, times(1)).getTariffCat("B");
    }
}
