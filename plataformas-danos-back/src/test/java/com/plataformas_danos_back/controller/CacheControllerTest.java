package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.CacheNotFoundException;
import com.plataformas_danos_back.model.dto.CacheStatsResponse;
import com.plataformas_danos_back.service.CacheService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheControllerTest {

    @Mock
    private CacheService cacheService;

    @InjectMocks
    private CacheController controller;

    // ── GET /api/v1/cache/stats ───────────────────────────────────────────────

    @Test
    void getStats_returns200WithList() {
        // GIVEN
        var stats = List.of(
                CacheStatsResponse.builder()
                        .name("catalogs-subscribers")
                        .estimatedSize(1L)
                        .hitCount(245L)
                        .missCount(3L)
                        .ttlSeconds(43200L)
                        .build(),
                CacheStatsResponse.builder()
                        .name("tariffs-cat")
                        .estimatedSize(5L)
                        .hitCount(1820L)
                        .missCount(5L)
                        .ttlSeconds(3600L)
                        .build()
        );
        when(cacheService.getStats()).thenReturn(stats);

        // WHEN
        var response = controller.getStats();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getName()).isEqualTo("catalogs-subscribers");
        assertThat(response.getBody().get(0).getHitCount()).isEqualTo(245L);
        assertThat(response.getBody().get(0).getMissCount()).isEqualTo(3L);
        assertThat(response.getBody().get(0).getTtlSeconds()).isEqualTo(43200L);
    }

    @Test
    void getStats_emptyCaches_returns200WithEmptyList() {
        // GIVEN
        when(cacheService.getStats()).thenReturn(List.of());

        // WHEN
        var response = controller.getStats();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    // ── DELETE /api/v1/cache/{cacheName} ─────────────────────────────────────

    @Test
    void evictByName_knownCache_returns204() {
        // GIVEN — cacheService.evict completes without exception

        // WHEN
        var response = controller.evict("catalogs-subscribers");

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(cacheService).evict("catalogs-subscribers");
    }

    @Test
    void evictByName_notFound_propagatesCacheNotFoundException() {
        // GIVEN — GlobalExceptionHandler maps this to 404; controller propagates unchanged
        doThrow(new CacheNotFoundException("Caché 'cache-inexistente' no encontrada"))
                .when(cacheService).evict("cache-inexistente");

        // WHEN / THEN
        assertThatThrownBy(() -> controller.evict("cache-inexistente"))
                .isInstanceOf(CacheNotFoundException.class)
                .hasMessageContaining("cache-inexistente");
    }

    // ── DELETE /api/v1/cache ──────────────────────────────────────────────────

    @Test
    void evictAll_returns204() {
        // GIVEN — cacheService.evictAll completes without exception

        // WHEN
        var response = controller.evictAll();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(cacheService).evictAll();
    }
}
