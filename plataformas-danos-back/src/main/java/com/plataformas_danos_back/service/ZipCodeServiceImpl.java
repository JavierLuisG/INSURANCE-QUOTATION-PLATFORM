package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.ZipCodeClient;
import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.exception.InvalidZipCodeFormatException;
import com.plataformas_danos_back.exception.ZipCodeNotFoundException;
import com.plataformas_danos_back.model.dto.ZipCodeDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipCodeServiceImpl implements ZipCodeService {

    private static final Pattern ZIP_FORMAT = Pattern.compile("^\\d{5}$");
    private static final String DEFAULT_ZONA = "ZONA_INDEFINIDA";
    private static final String DEFAULT_NIVEL = "NIVEL_INDEFINIDO";

    private final ZipCodeClient zipCodeClient;

    @Override
    @Cacheable(value = "zip-codes", key = "#zipCode")
    @Retry(name = "plataforma-core-ohs", fallbackMethod = "zipCodeFallback")
    public ZipCodeDto getByZipCode(String zipCode) {
        if (zipCode == null || !ZIP_FORMAT.matcher(zipCode).matches()) {
            throw new InvalidZipCodeFormatException(
                    "Formato de código postal inválido. Debe ser exactamente 5 dígitos numéricos.");
        }
        try {
            ZipCodeDto dto = zipCodeClient.getByZipCode(zipCode);
            return applyDefaults(dto, zipCode);
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ZipCodeNotFoundException("Código postal no encontrado", ex);
            }
            throw ex;
        }
    }

    public ZipCodeDto zipCodeFallback(String zipCode, Exception ex) {
        log.error("CRITICAL: Catalog service unavailable after retries — zip-codes. CP={}. Error: {}",
                zipCode, ex.getMessage());
        throw new CatalogServiceUnavailableException("Servicio de catálogos no disponible", ex);
    }

    private ZipCodeDto applyDefaults(ZipCodeDto dto, String zipCode) {
        if (dto.getZonaCAT() == null || dto.getZonaCAT().isBlank()) {
            log.warn("zonaCAT missing for codigoPostal={}; applying default '{}'", zipCode, DEFAULT_ZONA);
            dto.setZonaCAT(DEFAULT_ZONA);
        }
        if (dto.getNivelTecnico() == null || dto.getNivelTecnico().isBlank()) {
            log.warn("nivelTecnico missing for codigoPostal={}; applying default '{}'", zipCode, DEFAULT_NIVEL);
            dto.setNivelTecnico(DEFAULT_NIVEL);
        }
        return dto;
    }
}
