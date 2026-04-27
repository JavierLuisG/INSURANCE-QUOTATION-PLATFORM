package com.plataformas_danos_back.client;

import com.plataformas_danos_back.model.dto.ZipCodeDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
public class ZipCodeClientImpl implements ZipCodeClient {

    private final RestTemplate restTemplate;

    @Value("${plataforma-core-ohs.url:http://localhost:3001}")
    private String baseUrl;

    public ZipCodeClientImpl(@Qualifier("catalogsRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ZipCodeDto getByZipCode(String zipCode) {
        String url = baseUrl + "/v1/zip-codes/" + zipCode;
        log.debug("Calling external service: GET {}", url);
        return restTemplate.getForObject(url, ZipCodeDto.class);
    }
}
