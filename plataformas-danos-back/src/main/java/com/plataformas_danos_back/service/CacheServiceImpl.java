package com.plataformas_danos_back.service;

import com.github.benmanes.caffeine.cache.stats.CacheStats;
import com.plataformas_danos_back.config.CacheProperties;
import com.plataformas_danos_back.exception.CacheNotFoundException;
import com.plataformas_danos_back.model.dto.CacheStatsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final CacheManager cacheManager;
    private final CacheProperties cacheProperties;

    @Override
    @SuppressWarnings("unchecked")
    public List<CacheStatsResponse> getStats() {
        return cacheManager.getCacheNames().stream()
                .map(name -> {
                    Cache springCache = cacheManager.getCache(name);
                    com.github.benmanes.caffeine.cache.Cache<Object, Object> nativeCache =
                            (com.github.benmanes.caffeine.cache.Cache<Object, Object>) springCache.getNativeCache();
                    CacheStats stats = nativeCache.stats();
                    long ttl = cacheProperties.getTtl().getOrDefault(name, 3600L);
                    return CacheStatsResponse.builder()
                            .name(name)
                            .estimatedSize(nativeCache.estimatedSize())
                            .hitCount(stats.hitCount())
                            .missCount(stats.missCount())
                            .ttlSeconds(ttl)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    public void evict(String cacheName) {
        Cache cache = cacheManager.getCache(cacheName);
        if (cache == null) {
            throw new CacheNotFoundException("Caché '" + cacheName + "' no encontrada");
        }
        cache.clear();
        log.info("Cache evicted: name={}", cacheName);
    }

    @Override
    public void evictAll() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        log.info("All caches evicted");
    }
}
