package com.plataformas_danos_back.service;

import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;
import com.plataformas_danos_back.client.ZipCodeClient;
import com.plataformas_danos_back.exception.InvalidZipCodeFormatException;
import com.plataformas_danos_back.exception.ZipCodeServiceUnavailableException;
import com.plataformas_danos_back.exception.ZipCodeNotFoundException;
import com.plataformas_danos_back.model.dto.ValidationResult;
import com.plataformas_danos_back.model.dto.ZipCodeDto;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class ZipCodeServiceImpl implements ZipCodeService {

    private static final Pattern ZIP_FORMAT = Pattern.compile("^\\d{5}$");
    private static final String DEFAULT_ZONA = "ZONA_INDEFINIDA";
    private static final String DEFAULT_NIVEL = "NIVEL_INDEFINIDO";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ZipCodeClient zipCodeClient;
    private final DataValidationService dataValidationService;

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
            ZipCodeDto result = applyDefaults(dto, zipCode);
            validateSingleRecord(result, "ZIP_CODE", zipCode);
            return result;
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new ZipCodeNotFoundException("Código postal no encontrado", ex);
            }
            throw ex;
        }
    }

    public ZipCodeDto zipCodeFallback(String zipCode, Exception ex) {
        log.error("CRITICAL: ZipCode service unavailable after retries. CP={}. Error: {}",
                zipCode, ex.getMessage());
        throw new ZipCodeServiceUnavailableException("Servicio de validación de CP no disponible", ex);
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
}
