package com.plataformas_danos_back.service;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.plataformas_danos_back.config.CacheProperties;
import com.plataformas_danos_back.exception.CacheNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CacheServiceImplTest {

    @Mock
    private CacheManager cacheManager;

    @Mock
    private CacheProperties cacheProperties;

    @InjectMocks
    private CacheServiceImpl cacheService;

    // ── getStats ─────────────────────────────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void getStats_returnsAllCacheNames() {
        // GIVEN
        Cache springCache = mock(Cache.class);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                mock(com.github.benmanes.caffeine.cache.Cache.class);
        CacheStats stats = CacheStats.of(10L, 2L, 0L, 0L, 0L, 0L, 0L);

        when(cacheManager.getCacheNames()).thenReturn(List.of("catalogs-subscribers", "tariffs-fire"));
        when(cacheManager.getCache("catalogs-subscribers")).thenReturn(springCache);
        when(cacheManager.getCache("tariffs-fire")).thenReturn(springCache);
        when(springCache.getNativeCache()).thenReturn(nativeCache);
        when(nativeCache.stats()).thenReturn(stats);
        when(nativeCache.estimatedSize()).thenReturn(1L);
        when(cacheProperties.getTtl()).thenReturn(
                Map.of("catalogs-subscribers", 43200L, "tariffs-fire", 21600L));

        // WHEN
        var result = cacheService.getStats();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result).extracting("name")
                .containsExactlyInAnyOrder("catalogs-subscribers", "tariffs-fire");
    }

    @Test
    @SuppressWarnings("unchecked")
    void getStats_returnsCorrectHitAndMissCount() {
        // GIVEN
        Cache springCache = mock(Cache.class);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                mock(com.github.benmanes.caffeine.cache.Cache.class);
        CacheStats stats = CacheStats.of(245L, 3L, 0L, 0L, 0L, 0L, 0L);

        when(cacheManager.getCacheNames()).thenReturn(List.of("catalogs-subscribers"));
        when(cacheManager.getCache("catalogs-subscribers")).thenReturn(springCache);
        when(springCache.getNativeCache()).thenReturn(nativeCache);
        when(nativeCache.stats()).thenReturn(stats);
        when(nativeCache.estimatedSize()).thenReturn(5L);
        when(cacheProperties.getTtl()).thenReturn(Map.of("catalogs-subscribers", 43200L));

        // WHEN
        var result = cacheService.getStats();

        // THEN
        var dto = result.get(0);
        assertThat(dto.getName()).isEqualTo("catalogs-subscribers");
        assertThat(dto.getEstimatedSize()).isEqualTo(5L);
        assertThat(dto.getHitCount()).isEqualTo(245L);
        assertThat(dto.getMissCount()).isEqualTo(3L);
        assertThat(dto.getTtlSeconds()).isEqualTo(43200L);
    }

    @Test
    @SuppressWarnings("unchecked")
    void getStats_unknownCacheName_usesTtlDefault3600() {
        // GIVEN — TTL map is empty; cache name not registered
        Cache springCache = mock(Cache.class);
        com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                mock(com.github.benmanes.caffeine.cache.Cache.class);
        CacheStats stats = CacheStats.of(0L, 0L, 0L, 0L, 0L, 0L, 0L);

        when(cacheManager.getCacheNames()).thenReturn(List.of("unknown-cache"));
        when(cacheManager.getCache("unknown-cache")).thenReturn(springCache);
        when(springCache.getNativeCache()).thenReturn(nativeCache);
        when(nativeCache.stats()).thenReturn(stats);
        when(nativeCache.estimatedSize()).thenReturn(0L);
        when(cacheProperties.getTtl()).thenReturn(Map.of());

        // WHEN
        var result = cacheService.getStats();

        // THEN
        assertThat(result.get(0).getTtlSeconds()).isEqualTo(3600L);
    }

    @Test
    void getStats_emptyCacheNames_returnsEmptyList() {
        // GIVEN
        when(cacheManager.getCacheNames()).thenReturn(List.of());

        // WHEN
        var result = cacheService.getStats();

        // THEN
        assertThat(result).isEmpty();
    }

    // ── evict ─────────────────────────────────────────────────────────────────

    @Test
    void evict_knownCache_clearsEntries() {
        // GIVEN
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("catalogs-subscribers")).thenReturn(cache);

        // WHEN
        cacheService.evict("catalogs-subscribers");

        // THEN
        verify(cache).clear();
    }

    @Test
    void evict_unknownCache_throwsCacheNotFoundException() {
        // GIVEN
        when(cacheManager.getCache("cache-inexistente")).thenReturn(null);

        // WHEN / THEN
        assertThatThrownBy(() -> cacheService.evict("cache-inexistente"))
                .isInstanceOf(CacheNotFoundException.class)
                .hasMessageContaining("cache-inexistente");
    }

    @Test
    void evict_unknownCache_doesNotCallClear() {
        // GIVEN
        Cache cache = mock(Cache.class);
        when(cacheManager.getCache("cache-inexistente")).thenReturn(null);

        // WHEN
        try {
            cacheService.evict("cache-inexistente");
        } catch (CacheNotFoundException ignored) {
        }

        // THEN — clear must never be called when cache is not found
        verify(cache, never()).clear();
    }

    // ── evictAll ──────────────────────────────────────────────────────────────

    @Test
    void evictAll_clearsAllRegisteredCaches() {
        // GIVEN
        Cache cache1 = mock(Cache.class);
        Cache cache2 = mock(Cache.class);
        when(cacheManager.getCacheNames()).thenReturn(List.of("catalogs-subscribers", "tariffs-fire"));
        when(cacheManager.getCache("catalogs-subscribers")).thenReturn(cache1);
        when(cacheManager.getCache("tariffs-fire")).thenReturn(cache2);

        // WHEN
        cacheService.evictAll();

        // THEN
        verify(cache1).clear();
        verify(cache2).clear();
    }

    @Test
    void evictAll_nullCacheEntry_isSkippedSafely() {
        // GIVEN — one cache entry returns null (e.g. concurrent removal)
        Cache cache1 = mock(Cache.class);
        when(cacheManager.getCacheNames()).thenReturn(List.of("catalogs-subscribers", "ghost-cache"));
        when(cacheManager.getCache("catalogs-subscribers")).thenReturn(cache1);
        when(cacheManager.getCache("ghost-cache")).thenReturn(null);

        // WHEN — should not throw
        cacheService.evictAll();

        // THEN — only the non-null cache is cleared
        verify(cache1).clear();
    }
}
