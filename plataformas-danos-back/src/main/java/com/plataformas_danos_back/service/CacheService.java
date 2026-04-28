package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.CacheStatsResponse;

import java.util.List;

public interface CacheService {
    List<CacheStatsResponse> getStats();
    void evict(String cacheName);
    void evictAll();
}
