package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.CatalogsClient;
import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
import com.plataformas_danos_back.model.dto.SubscriberDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogsServiceImpl implements CatalogsService {

    private final CatalogsClient catalogsClient;

    @Override
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "subscribersFallback")
    public List<SubscriberDto> getSubscribers() {
        return filterValidSubscribers(catalogsClient.getSubscribers());
    }

    @Override
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "agentsFallback")
    public List<AgentDto> getAgents() {
        return filterValidAgents(catalogsClient.getAgents());
    }

    @Override
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "businessLinesFallback")
    public List<BusinessLineDto> getBusinessLines() {
        return filterValidBusinessLines(catalogsClient.getBusinessLines());
    }

    public List<SubscriberDto> subscribersFallback(Exception ex) {
        log.error("CRITICAL: Catalog service unavailable after retries — subscribers. Error: {}", ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    public List<AgentDto> agentsFallback(Exception ex) {
        log.error("CRITICAL: Catalog service unavailable after retries — agents. Error: {}", ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    public List<BusinessLineDto> businessLinesFallback(Exception ex) {
        log.error("CRITICAL: Catalog service unavailable after retries — business-lines. Error: {}", ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    private List<SubscriberDto> filterValidSubscribers(List<SubscriberDto> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(s -> {
                    if (s.getId() == null || s.getId().isBlank()) {
                        log.warn("Subscriber record dropped: missing required field 'id'");
                        return false;
                    }
                    if (s.getNombre() == null || s.getNombre().isBlank()) {
                        log.warn("Subscriber record dropped: missing required field 'nombre', id={}", s.getId());
                        return false;
                    }
                    return true;
                })
                .toList();
    }

    private List<AgentDto> filterValidAgents(List<AgentDto> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(a -> {
                    if (a.getId() == null || a.getId().isBlank()) {
                        log.warn("Agent record dropped: missing required field 'id'");
                        return false;
                    }
                    if (a.getNombre() == null || a.getNombre().isBlank()) {
                        log.warn("Agent record dropped: missing required field 'nombre', id={}", a.getId());
                        return false;
                    }
                    return true;
                })
                .toList();
    }

    private List<BusinessLineDto> filterValidBusinessLines(List<BusinessLineDto> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(b -> {
                    if (b.getId() == null || b.getId().isBlank()) {
                        log.warn("BusinessLine record dropped: missing required field 'id'");
                        return false;
                    }
                    if (b.getDescripcion() == null || b.getDescripcion().isBlank()) {
                        log.warn("BusinessLine record dropped: missing required field 'descripcion', id={}", b.getId());
                        return false;
                    }
                    return true;
                })
                .toList();
    }
}
