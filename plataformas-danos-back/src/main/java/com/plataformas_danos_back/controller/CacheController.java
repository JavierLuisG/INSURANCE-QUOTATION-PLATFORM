package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.model.dto.CacheStatsResponse;
import com.plataformas_danos_back.service.CacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/cache")
public class CacheController {

    private final CacheService cacheService;

    @GetMapping("/stats")
    public ResponseEntity<List<CacheStatsResponse>> getStats() {
        return ResponseEntity.ok(cacheService.getStats());
    }

    @DeleteMapping("/{cacheName}")
    public ResponseEntity<Void> evict(@PathVariable String cacheName) {
        cacheService.evict(cacheName);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping
    public ResponseEntity<Void> evictAll() {
        cacheService.evictAll();
        return ResponseEntity.noContent().build();
    }
}
