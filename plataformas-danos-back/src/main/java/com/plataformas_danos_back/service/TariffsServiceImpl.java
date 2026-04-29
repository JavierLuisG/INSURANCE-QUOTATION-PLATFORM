package com.plataformas_danos_back.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.plataformas_danos_back.client.TariffsClient;
import com.plataformas_danos_back.exception.TariffNotFoundException;
import com.plataformas_danos_back.exception.TariffServiceUnavailableException;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import com.plataformas_danos_back.model.dto.ValidationResult;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffsServiceImpl implements TariffsService {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final TariffsClient tariffsClient;
    private final DataValidationService dataValidationService;

    @Override
    @Cacheable(value = "tariffs-fire", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "tariffFireFallback")
    public List<TariffFireDto> getTariffsFire() {
        List<TariffFireDto> filtered = filterValidTariffsFire(tariffsClient.getTariffsFire());
        return applyValidation(filtered, "TARIFF_FIRE", TariffFireDto::getZonaRiesgo);
    }

    @Override
    @Cacheable(value = "tariffs-cat", key = "#zona")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "tariffCatFallback")
    public TariffCatDto getTariffCat(String zona) {
        try {
            TariffCatDto dto = tariffsClient.getTariffCat(zona);
            validateSingleRecord(dto, "TARIFF_CAT", zona);
            return dto;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new TariffNotFoundException("Tarifa CAT no encontrada para la zona indicada", ex);
            }
            throw ex;
        }
    }

    @Override
    @Cacheable(value = "tariffs-electronic-equipment", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "tariffElectronicEquipmentFallback")
    public List<TariffElectronicEquipmentDto> getTariffsElectronicEquipment() {
        List<TariffElectronicEquipmentDto> filtered = filterValidTariffsElectronicEquipment(tariffsClient.getTariffsElectronicEquipment());
        return applyValidation(filtered, "TARIFF_ELECTRONIC_EQUIPMENT", TariffElectronicEquipmentDto::getClase);
    }

    public List<TariffFireDto> tariffFireFallback(Exception ex) {
        log.error("CRITICAL: Tariffs service unavailable after retries — fire. Error: {}", ex.getMessage());
        throw new TariffServiceUnavailableException("Servicio de tarifas no disponible", ex);
    }

    public TariffCatDto tariffCatFallback(String zona, Exception ex) {
        log.error("CRITICAL: Tariffs service unavailable after retries — cat. zona={}. Error: {}", zona, ex.getMessage());
        throw new TariffServiceUnavailableException("Servicio de tarifas no disponible", ex);
    }

    public List<TariffElectronicEquipmentDto> tariffElectronicEquipmentFallback(Exception ex) {
        log.error("CRITICAL: Tariffs service unavailable after retries — electronic-equipment. Error: {}", ex.getMessage());
        throw new TariffServiceUnavailableException("Servicio de tarifas no disponible", ex);
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

    private void validateSingleRecord(Object record, String dataType, String recordId) {
        try {
            Map<String, Object> map = OBJECT_MAPPER.convertValue(record, MAP_TYPE);
            ValidationResult result = dataValidationService.validateBatch(dataType, List.of(map), null);
            if (result != null && result.getResults() != null) {
                result.getResults().stream()
                        .filter(r -> "INCONSISTENT".equals(r.getStatus()))
                        .findFirst()
                        .ifPresent(r -> log.warn("DataValidation inconsistency for {}={}: {}", dataType, recordId, r.getErrors()));
            }
        } catch (Exception ex) {
            log.warn("DataValidationService unavailable for dataType={}: {}", dataType, ex.getMessage());
        }
    }

    private List<TariffFireDto> filterValidTariffsFire(List<TariffFireDto> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(t -> {
                    if (t.getZonaRiesgo() == null || t.getZonaRiesgo().isBlank()) {
                        log.warn("TariffFire record dropped: missing required field 'zonaRiesgo'");
                        return false;
                    }
                    return true;
                })
                .toList();
    }

    private List<TariffElectronicEquipmentDto> filterValidTariffsElectronicEquipment(List<TariffElectronicEquipmentDto> list) {
        if (list == null) return List.of();
        return list.stream()
                .filter(t -> {
                    if (t.getClase() == null || t.getClase().isBlank()) {
                        log.warn("TariffElectronicEquipment record dropped: missing required field 'clase'");
                        return false;
                    }
                    return true;
                })
                .toList();
    }
}
