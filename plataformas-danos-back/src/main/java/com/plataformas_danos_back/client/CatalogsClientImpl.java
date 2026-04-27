package com.plataformas_danos_back.client;

import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
import com.plataformas_danos_back.model.dto.GuaranteeDto;
import com.plataformas_danos_back.model.dto.RiskClassificationDto;
import com.plataformas_danos_back.model.dto.SubscriberDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
@Component
public class CatalogsClientImpl implements CatalogsClient {

    private final RestTemplate restTemplate;

    @Value("${plataforma-core-ohs.url:http://localhost:3001}")
    private String baseUrl;

    public CatalogsClientImpl(@Qualifier("catalogsRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<SubscriberDto> getSubscribers() {
        String url = baseUrl + "/v1/subscribers";
        log.debug("Calling external service: GET {}", url);
        ResponseEntity<List<SubscriberDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public List<AgentDto> getAgents() {
        String url = baseUrl + "/v1/agents";
        log.debug("Calling external service: GET {}", url);
        ResponseEntity<List<AgentDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public List<BusinessLineDto> getBusinessLines() {
        String url = baseUrl + "/v1/business-lines";
        log.debug("Calling external service: GET {}", url);
        ResponseEntity<List<BusinessLineDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public List<RiskClassificationDto> getRiskClassifications() {
        String url = baseUrl + "/v1/catalogs/risk-classification";
        log.debug("Calling external service: GET {}", url);
        ResponseEntity<List<RiskClassificationDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }

    @Override
    public List<GuaranteeDto> getGuarantees() {
        String url = baseUrl + "/v1/catalogs/guarantees";
        log.debug("Calling external service: GET {}", url);
        ResponseEntity<List<GuaranteeDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null,
                new ParameterizedTypeReference<>() {}
        );
        return response.getBody();
    }
}
