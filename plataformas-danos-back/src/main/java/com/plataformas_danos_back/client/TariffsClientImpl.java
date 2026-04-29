package com.plataformas_danos_back.client;

import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import java.util.List;

@Slf4j
@Component
public class TariffsClientImpl implements TariffsClient {

    private final RestTemplate restTemplate;

    @Value("${plataforma-core-ohs.url:http://localhost:3001}")
    private String baseUrl;

    public TariffsClientImpl(@Qualifier("catalogsRestTemplate") RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public List<TariffFireDto> getTariffsFire() {
        String url = baseUrl + "/v1/tariffs/fire";
        log.debug("Calling external service: GET {}", url);
        ResponseEntity<List<TariffFireDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }

    @Override
    public TariffCatDto getTariffCat(String zona) {
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/v1/tariffs/cat")
                .queryParam("zona", zona)
                .toUriString();
        log.debug("Calling external service: GET {}", url);
        return restTemplate.getForObject(url, TariffCatDto.class);
    }

    @Override
    public List<TariffElectronicEquipmentDto> getTariffsElectronicEquipment() {
        String url = baseUrl + "/v1/tariffs/electronic-equipment";
        log.debug("Calling external service: GET {}", url);
        ResponseEntity<List<TariffElectronicEquipmentDto>> response = restTemplate.exchange(
                url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {});
        return response.getBody();
    }
}
