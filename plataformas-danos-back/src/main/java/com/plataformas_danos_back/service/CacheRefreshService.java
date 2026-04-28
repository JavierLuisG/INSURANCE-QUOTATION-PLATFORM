package com.plataformas_danos_back.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.function.Supplier;

@Slf4j
@Service
@RequiredArgsConstructor
public class CacheRefreshService {

    private final CatalogsService catalogsService;
    private final TariffsService tariffsService;
    private final CacheManager cacheManager;

    @Scheduled(cron = "${cache.refresh.cron:0 0 */6 * * *}")
    public void refreshStaticCaches() {
        log.info("Scheduled cache refresh started");
        boolean anyFailed = false;

        anyFailed |= !refreshSafely("catalogs-subscribers", catalogsService::getSubscribers);
        anyFailed |= !refreshSafely("catalogs-agents", catalogsService::getAgents);
        anyFailed |= !refreshSafely("catalogs-business-lines", catalogsService::getBusinessLines);
        anyFailed |= !refreshSafely("catalogs-risk-classifications", catalogsService::getRiskClassifications);
        anyFailed |= !refreshSafely("catalogs-guarantees", catalogsService::getGuarantees);
        anyFailed |= !refreshSafely("tariffs-fire", tariffsService::getTariffsFire);
        anyFailed |= !refreshSafely("tariffs-electronic-equipment", tariffsService::getTariffsElectronicEquipment);

        if (anyFailed) {
            log.error("Scheduled cache refresh completed with status FAILED — one or more caches could not be refreshed");
        } else {
            log.info("Scheduled cache refresh completed with status SUCCESS");
        }
    }

    private boolean refreshSafely(String cacheName, Supplier<?> loader) {
        try {
            Cache cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
            }
            loader.get();
            log.info("Cache refreshed: name={}", cacheName);
            return true;
        } catch (Exception ex) {
            log.error("Cache refresh FAILED: name={}, error={}", cacheName, ex.getMessage());
            return false;
        }
    }
}
