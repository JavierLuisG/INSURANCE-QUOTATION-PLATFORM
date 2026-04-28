package com.plataformas_danos_back.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CacheStatsResponse {
    private String name;
    private long estimatedSize;
    private long hitCount;
    private long missCount;
    private long ttlSeconds;
}
