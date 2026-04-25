package com.plataformas_danos_back.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class CatalogsClientConfig {

    @Value("${plataforma-core-ohs.timeout-ms:5000}")
    private int timeoutMs;

    @Bean("catalogsRestTemplate")
    public RestTemplate catalogsRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);
        return new RestTemplate(factory);
    }
}
