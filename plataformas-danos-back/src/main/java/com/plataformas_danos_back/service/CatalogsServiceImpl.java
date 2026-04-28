package com.plataformas_danos_back.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.plataformas_danos_back.client.CatalogsClient;
import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
import com.plataformas_danos_back.model.dto.GuaranteeDto;
import com.plataformas_danos_back.model.dto.RiskClassificationDto;
import com.plataformas_danos_back.model.dto.SubscriberDto;
import com.plataformas_danos_back.model.dto.ValidationResult;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CatalogsServiceImpl implements CatalogsService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final CatalogsClient catalogsClient;
    private final DataValidationService dataValidationService;

    @Override
    @Cacheable(value = "catalogs-subscribers", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "subscribersFallback")
    public List<SubscriberDto> getSubscribers() {
        List<SubscriberDto> filtered = filterValidSubscribers(catalogsClient.getSubscribers());
        return applyValidation(filtered, "SUBSCRIBER", SubscriberDto::getId);
    }

    @Override
    @Cacheable(value = "catalogs-agents", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "agentsFallback")
    public List<AgentDto> getAgents() {
        List<AgentDto> filtered = filterValidAgents(catalogsClient.getAgents());
        return applyValidation(filtered, "AGENT", AgentDto::getId);
    }

    @Override
    @Cacheable(value = "catalogs-business-lines", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "businessLinesFallback")
    public List<BusinessLineDto> getBusinessLines() {
        List<BusinessLineDto> filtered = filterValidBusinessLines(catalogsClient.getBusinessLines());
        return applyValidation(filtered, "BUSINESS_LINE", BusinessLineDto::getId);
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

    @Override
    @Cacheable(value = "catalogs-risk-classifications", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "riskClassificationsFallback")
    public List<RiskClassificationDto> getRiskClassifications() {
        List<RiskClassificationDto> filtered = filterValidRiskClassifications(catalogsClient.getRiskClassifications());
        return applyValidation(filtered, "RISK_CLASSIFICATION", RiskClassificationDto::getId);
    }

    @Override
    @Cacheable(value = "catalogs-guarantees", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "guaranteesFallback")
    public List<GuaranteeDto> getGuarantees() {
        List<GuaranteeDto> filtered = filterValidGuarantees(catalogsClient.getGuarantees());
        return applyValidation(filtered, "GUARANTEE", GuaranteeDto::getId);
    }

    public List<RiskClassificationDto> riskClassificationsFallback(Exception ex) {
        log.error("CRITICAL: Catalog service unavailable after retries — risk-classifications. Error: {}", ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    public List<GuaranteeDto> guaranteesFallback(Exception ex) {
        log.error("CRITICAL: Catalog service unavailable after retries — guarantees. Error: {}", ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    private <T> List<T> applyValidation(List<T> records, String dataType, Function<T, String> idExtractor) {
        if (records.isEmpty()) return records;
        try {
            List<Map<String, Object>> maps = records.stream()
                    .map(r -> OBJECT_MAPPER.convertValue(r, MAP_TYPE))
                    .toList();
            ValidationResult result = dataValidationService.validateBatch(dataType, maps, null);
            if (result == null || result.getResults() == null) return records;

            Set<String> inconsistentIds = result.getResults().stream()
                    .filter(r -> "INCONSISTENT".equals(r.getStatus()))
                    .map(ValidationResult.RecordValidationResult::getId)
                    .collect(Collectors.toSet());

            if (!inconsistentIds.isEmpty()) {
                log.warn("DataValidation: {} inconsistent record(s) filtered for dataType={}", inconsistentIds.size(), dataType);
            }
            return records.stream()
                    .filter(r -> !inconsistentIds.contains(idExtractor.apply(r)))
                    .toList();
        } catch (Exception ex) {
            log.warn("DataValidationService unavailable; skipping validation for dataType={}: {}", dataType, ex.getMessage());
            return records;
        }
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

    private List<RiskClassificationDto> filterValidRiskClassifications(List<RiskClassificationDto> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(r -> {
                    if (r.getId() == null || r.getId().isBlank()) {
                        log.warn("RiskClassification record dropped: missing required field 'id'");
                        return false;
                    }
                    if (r.getNombre() == null || r.getNombre().isBlank()) {
                        log.warn("RiskClassification record dropped: missing required field 'nombre', id={}", r.getId());
                        return false;
                    }
                    return true;
                })
                .toList();
    }

    private List<GuaranteeDto> filterValidGuarantees(List<GuaranteeDto> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(g -> {
                    if (g.getId() == null || g.getId().isBlank()) {
                        log.warn("Guarantee record dropped: missing required field 'id'");
                        return false;
                    }
                    if (g.getNombre() == null || g.getNombre().isBlank()) {
                        log.warn("Guarantee record dropped: missing required field 'nombre', id={}", g.getId());
                        return false;
                    }
                    return true;
                })
                .toList();
    }
}
