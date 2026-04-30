package com.plataformas_danos_back.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class CotizadorCatalogosClientImpl implements CotizadorCatalogosClient {

    private final RestTemplate restTemplate;

    @Value("${plataforma-core-ohs.url:http://localhost:3001}")
    private String baseUrl;

    public CotizadorCatalogosClientImpl(@Qualifier("catalogsRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = "foliosCircuitBreaker")
    @Retry(name = "foliosRetry")
    public boolean existsInCatalog(String tipoCatalogo, String id) {
        String url = baseUrl + "/v1/catalogos/" + tipoCatalogo + "/" + id;
        log.debug("Validating catalog entry: GET {}", url);
        try {
            ResponseEntity<Void> response = restTemplate.getForEntity(url, Void.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound e) {
            log.debug("Catalog entry not found: tipoCatalogo={}, id={}", tipoCatalogo, id);
            return false;
        } catch (HttpClientErrorException e) {
            log.warn("HTTP client error validating catalog: tipoCatalogo={}, id={}, status={}", tipoCatalogo, id, e.getStatusCode());
            return false;
        } catch (Exception e) {
            log.error("Error connecting to catalog service: tipoCatalogo={}, id={}, error={}", tipoCatalogo, id, e.getMessage());
            throw new RuntimeException("Error al conectar con el servicio de catálogos: " + e.getMessage(), e);
        }
    }
}
