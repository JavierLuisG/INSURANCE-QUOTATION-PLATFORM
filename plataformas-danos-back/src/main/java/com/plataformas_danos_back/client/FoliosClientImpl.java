package com.plataformas_danos_back.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Component
public class FoliosClientImpl implements FoliosClient {

    private final RestTemplate restTemplate;

    @Value("${plataforma-core-ohs.url:http://localhost:3001}")
    private String baseUrl;

    public FoliosClientImpl(@Qualifier("catalogsRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @CircuitBreaker(name = "foliosCircuitBreaker")
    @Retry(name = "foliosRetry")
    @SuppressWarnings("unchecked")
    public String generarFolio() {
        String url = baseUrl + "/v1/folios";
        log.debug("Calling external service: GET {}", url);
        Map<String, Object> response = restTemplate.getForObject(url, Map.class);
        if (response == null || !response.containsKey("folio")) {
            throw new IllegalStateException("Respuesta del servicio de folios no contiene el campo 'folio'");
        }
        String folio = (String) response.get("folio");
        log.info("Folio obtenido del servicio externo: {}", folio);
        return folio;
    }
}
