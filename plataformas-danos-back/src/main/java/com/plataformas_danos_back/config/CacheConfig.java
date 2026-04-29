package com.plataformas_danos_back.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Map;

@Configuration
@EnableCaching
@RequiredArgsConstructor
public class CacheConfig {

    private static final Map<String, Integer> CACHE_MAX_SIZES = Map.ofEntries(
            Map.entry("catalogs-subscribers", 10),
            Map.entry("catalogs-agents", 10),
            Map.entry("catalogs-business-lines", 10),
            Map.entry("catalogs-risk-classifications", 10),
            Map.entry("catalogs-guarantees", 10),
            Map.entry("tariffs-fire", 10),
            Map.entry("tariffs-electronic-equipment", 10),
            Map.entry("tariffs-cat", 100),
            Map.entry("zip-codes", 500),
            Map.entry("validation-rules", 50),
            Map.entry("correction-rules", 200),
            Map.entry("parameters-tarifas-incendio", 1000),
            Map.entry("parameters-tarifas-cat", 500),
            Map.entry("parameters-tarifas-fhm", 10),
            Map.entry("parameters-cp-zonas", 100000)
    );

    private final CacheProperties cacheProperties;

    @Bean
    public CacheManager cacheManager() {
        SimpleCacheManager manager = new SimpleCacheManager();
        manager.setCaches(
                CACHE_MAX_SIZES.entrySet().stream()
                        .map(e -> buildCache(e.getKey(), e.getValue()))
                        .toList()
        );
        return manager;
    }

    private CaffeineCache buildCache(String name, int maxSize) {
        long ttlSeconds = cacheProperties.getTtl().getOrDefault(name, 3600L);
        return new CaffeineCache(name, Caffeine.newBuilder()
                .maximumSize(maxSize)
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .recordStats()
                .build());
    }
}
