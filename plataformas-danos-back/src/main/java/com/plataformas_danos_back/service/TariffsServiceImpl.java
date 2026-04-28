package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.TariffsClient;
import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.exception.TariffNotFoundException;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TariffsServiceImpl implements TariffsService {

    private final TariffsClient tariffsClient;

    @Override
    @Cacheable(value = "tariffs-fire", key = "'all'")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "tariffFireFallback")
    public List<TariffFireDto> getTariffsFire() {
        return filterValidTariffsFire(tariffsClient.getTariffsFire());
    }

    @Override
    @Cacheable(value = "tariffs-cat", key = "#zona")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "tariffCatFallback")
    public TariffCatDto getTariffCat(String zona) {
        try {
            return tariffsClient.getTariffCat(zona);
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
        return filterValidTariffsElectronicEquipment(tariffsClient.getTariffsElectronicEquipment());
    }

    public List<TariffFireDto> tariffFireFallback(Exception ex) {
        log.error("CRITICAL: Tariffs service unavailable after retries — fire. Error: {}", ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    public TariffCatDto tariffCatFallback(String zona, Exception ex) {
        log.error("CRITICAL: Tariffs service unavailable after retries — cat. zona={}. Error: {}", zona, ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    public List<TariffElectronicEquipmentDto> tariffElectronicEquipmentFallback(Exception ex) {
        log.error("CRITICAL: Tariffs service unavailable after retries — electronic-equipment. Error: {}", ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
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
