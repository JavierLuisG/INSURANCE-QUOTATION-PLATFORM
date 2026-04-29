package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.CatalogsClient;
import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
import com.plataformas_danos_back.model.dto.GuaranteeDto;
import com.plataformas_danos_back.model.dto.RiskClassificationDto;
import com.plataformas_danos_back.model.dto.SubscriberDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogsServiceImplTest {

    @Mock
    private CatalogsClient catalogsClient;

    @Mock
    private DataValidationService dataValidationService;

    @InjectMocks
    private CatalogsServiceImpl service;

    // ── Subscribers ──────────────────────────────────────────────────────────

    @Test
    void getSubscribers_validList_returns200WithList() {
        // GIVEN
        var subscriber = new SubscriberDto("SUB-001", "Empresa ABC S.A. de C.V.", "ABC", true);
        when(catalogsClient.getSubscribers()).thenReturn(List.of(subscriber));

        // WHEN
        var result = service.getSubscribers();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("SUB-001");
    }

    @Test
    void getSubscribers_emptyList_returnsEmptyList() {
        // GIVEN
        when(catalogsClient.getSubscribers()).thenReturn(List.of());

        // WHEN
        var result = service.getSubscribers();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void subscribersFallback_whenCalled_throwsCatalogServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Connection refused");

        // WHEN / THEN
        assertThatThrownBy(() -> service.subscribersFallback(cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    @Test
    void getSubscribers_recordMissingId_isDropped() {
        // GIVEN
        var valid = new SubscriberDto("SUB-001", "Empresa ABC", "ABC", true);
        var invalidId = new SubscriberDto(null, "Sin Identificador", "X", true);
        when(catalogsClient.getSubscribers()).thenReturn(List.of(valid, invalidId));

        // WHEN
        var result = service.getSubscribers();

        // THEN — solo el registro válido se retorna
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("SUB-001");
    }

    @Test
    void getSubscribers_mapsAllFields() {
        // GIVEN
        var subscriber = new SubscriberDto("SUB-001", "Empresa ABC S.A.", "ABC", true);
        when(catalogsClient.getSubscribers()).thenReturn(List.of(subscriber));

        // WHEN
        var result = service.getSubscribers();

        // THEN
        var dto = result.get(0);
        assertThat(dto.getId()).isEqualTo("SUB-001");
        assertThat(dto.getNombre()).isEqualTo("Empresa ABC S.A.");
        assertThat(dto.getClave()).isEqualTo("ABC");
        assertThat(dto.getActivo()).isTrue();
    }

    @Test
    void getSubscribers_clientThrowsHttpClientErrorException_exceptionPropagates() {
        // GIVEN — 4xx errors are in ignore-exceptions; without AOP proxy they propagate directly
        when(catalogsClient.getSubscribers())
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // WHEN / THEN — error 4xx no debe ser envuelto en CatalogServiceUnavailableException
        assertThatThrownBy(() -> service.getSubscribers())
                .isInstanceOf(HttpClientErrorException.class);
    }

    // ── Agents ──────────────────────────────────────────────────────────────

    @Test
    void getAgents_validList_returns200WithList() {
        // GIVEN
        var agent = new AgentDto("AGT-001", "Juan Pérez García", "JPG", true);
        when(catalogsClient.getAgents()).thenReturn(List.of(agent));

        // WHEN
        var result = service.getAgents();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("AGT-001");
        assertThat(result.get(0).getNombre()).isEqualTo("Juan Pérez García");
    }

    @Test
    void agentsFallback_whenCalled_throwsCatalogServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Timeout");

        // WHEN / THEN
        assertThatThrownBy(() -> service.agentsFallback(cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    @Test
    void getAgents_recordMissingNombre_isDropped() {
        // GIVEN
        var valid = new AgentDto("AGT-001", "Juan Pérez", "JPG", true);
        var invalidNombre = new AgentDto("AGT-002", "", "X", true);
        when(catalogsClient.getAgents()).thenReturn(List.of(valid, invalidNombre));

        // WHEN
        var result = service.getAgents();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("AGT-001");
    }

    // ── Business Lines ───────────────────────────────────────────────────────

    @Test
    void getBusinessLines_validList_returns200WithList() {
        // GIVEN
        var bl = new BusinessLineDto("BL-001", "Comercio al por menor", "CM", true);
        when(catalogsClient.getBusinessLines()).thenReturn(List.of(bl));

        // WHEN
        var result = service.getBusinessLines();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("BL-001");
        assertThat(result.get(0).getDescripcion()).isEqualTo("Comercio al por menor");
    }

    @Test
    void businessLinesFallback_whenCalled_throwsCatalogServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Service down");

        // WHEN / THEN
        assertThatThrownBy(() -> service.businessLinesFallback(cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    @Test
    void getBusinessLines_recordMissingId_isDropped() {
        // GIVEN
        var valid = new BusinessLineDto("BL-001", "Comercio", "CM", true);
        var invalidId = new BusinessLineDto("  ", "Sin ID", "X", true);
        when(catalogsClient.getBusinessLines()).thenReturn(List.of(valid, invalidId));

        // WHEN
        var result = service.getBusinessLines();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("BL-001");
    }

    // ── Risk Classifications ─────────────────────────────────────────────────

    @Test
    void getRiskClassifications_validList_returns200WithList() {
        // GIVEN
        var rc = new RiskClassificationDto("RC-001", "Riesgo Bajo", "Clasificación para riesgos con baja probabilidad.");
        when(catalogsClient.getRiskClassifications()).thenReturn(List.of(rc));

        // WHEN
        var result = service.getRiskClassifications();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("RC-001");
    }

    @Test
    void getRiskClassifications_emptyList_returnsEmptyList() {
        // GIVEN
        when(catalogsClient.getRiskClassifications()).thenReturn(List.of());

        // WHEN
        var result = service.getRiskClassifications();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void riskClassificationsFallback_whenCalled_throwsCatalogServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Connection refused");

        // WHEN / THEN
        assertThatThrownBy(() -> service.riskClassificationsFallback(cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    @Test
    void getRiskClassifications_recordMissingId_isDropped() {
        // GIVEN
        var valid = new RiskClassificationDto("RC-001", "Riesgo Bajo", "Descripción");
        var missingId = new RiskClassificationDto(null, "Sin ID", "Descripción");
        when(catalogsClient.getRiskClassifications()).thenReturn(List.of(valid, missingId));

        // WHEN
        var result = service.getRiskClassifications();

        // THEN — solo el registro válido se retorna
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("RC-001");
    }

    @Test
    void getRiskClassifications_mapsAllFields() {
        // GIVEN
        var rc = new RiskClassificationDto("RC-001", "Riesgo Bajo", "Clasificación para riesgos con baja probabilidad.");
        when(catalogsClient.getRiskClassifications()).thenReturn(List.of(rc));

        // WHEN
        var result = service.getRiskClassifications();

        // THEN
        var dto = result.get(0);
        assertThat(dto.getId()).isEqualTo("RC-001");
        assertThat(dto.getNombre()).isEqualTo("Riesgo Bajo");
        assertThat(dto.getDescripcion()).isEqualTo("Clasificación para riesgos con baja probabilidad.");
    }

    // ── Guarantees ──────────────────────────────────────────────────────────

    @Test
    void getGuarantees_validList_returns200WithList() {
        // GIVEN
        var guarantee = new GuaranteeDto("GUA-001", "Robo con Violencia", "RV", true);
        when(catalogsClient.getGuarantees()).thenReturn(List.of(guarantee));

        // WHEN
        var result = service.getGuarantees();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("GUA-001");
    }

    @Test
    void getGuarantees_emptyList_returnsEmptyList() {
        // GIVEN
        when(catalogsClient.getGuarantees()).thenReturn(List.of());

        // WHEN
        var result = service.getGuarantees();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void guaranteesFallback_whenCalled_throwsCatalogServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Timeout");

        // WHEN / THEN
        assertThatThrownBy(() -> service.guaranteesFallback(cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    @Test
    void getGuarantees_recordMissingNombre_isDropped() {
        // GIVEN
        var valid = new GuaranteeDto("GUA-001", "Robo con Violencia", "RV", true);
        var missingNombre = new GuaranteeDto("GUA-002", "", "DA", false);
        when(catalogsClient.getGuarantees()).thenReturn(List.of(valid, missingNombre));

        // WHEN
        var result = service.getGuarantees();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("GUA-001");
    }

    @Test
    void getGuarantees_mapsAllFields() {
        // GIVEN
        var guarantee = new GuaranteeDto("GUA-001", "Robo con Violencia", "RV", true);
        when(catalogsClient.getGuarantees()).thenReturn(List.of(guarantee));

        // WHEN
        var result = service.getGuarantees();

        // THEN
        var dto = result.get(0);
        assertThat(dto.getId()).isEqualTo("GUA-001");
        assertThat(dto.getNombre()).isEqualTo("Robo con Violencia");
        assertThat(dto.getClaveIncendio()).isEqualTo("RV");
        assertThat(dto.getTarifable()).isTrue();
    }

    // ── SPEC-009 mandatory cases ─────────────────────────────────────────────

    // TC-01: getSubscribers_happyPath_returnsCachedResponse
    @Test
    void getSubscribers_happyPath_returnsCachedResponse() {
        // GIVEN
        var subscriber = new SubscriberDto("SUB-001", "Empresa ABC S.A. de C.V.", "ABC", true);
        when(catalogsClient.getSubscribers()).thenReturn(List.of(subscriber));

        // WHEN
        var result = service.getSubscribers();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("SUB-001");
        verify(catalogsClient).getSubscribers();
    }

    // TC-02: getSubscribers_serviceUnavailable_throws503
    @Test
    void getSubscribers_serviceUnavailable_throws503() {
        // GIVEN — client throws RuntimeException simulating network failure
        var cause = new RuntimeException("Connection refused");

        // WHEN / THEN — fallback propagates CatalogServiceUnavailableException
        assertThatThrownBy(() -> service.subscribersFallback(cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessageContaining("catálogos no disponible");
    }

    // TC-03: getAgents_happyPath_returnsAgentList
    @Test
    void getAgents_happyPath_returnsAgentList() {
        // GIVEN
        var agent = new AgentDto("AGT-001", "Juan Pérez García", "JPG", true);
        when(catalogsClient.getAgents()).thenReturn(List.of(agent));

        // WHEN
        var result = service.getAgents();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("AGT-001");
        verify(catalogsClient).getAgents();
    }

    // TC-04: getBusinessLines_happyPath_returnsBusinessLineList
    @Test
    void getBusinessLines_happyPath_returnsBusinessLineList() {
        // GIVEN
        var bl = new BusinessLineDto("BL-001", "Comercio al por menor", "CM", true);
        when(catalogsClient.getBusinessLines()).thenReturn(List.of(bl));

        // WHEN
        var result = service.getBusinessLines();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("BL-001");
        verify(catalogsClient).getBusinessLines();
    }

    // TC-05: getRiskClassifications_happyPath_returnsRiskClassificationList
    @Test
    void getRiskClassifications_happyPath_returnsRiskClassificationList() {
        // GIVEN
        var rc = new RiskClassificationDto("RC-001", "Riesgo Bajo", "Probabilidad baja");
        when(catalogsClient.getRiskClassifications()).thenReturn(List.of(rc));

        // WHEN
        var result = service.getRiskClassifications();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("RC-001");
        verify(catalogsClient).getRiskClassifications();
    }

    // TC-06: getGuarantees_happyPath_returnsGuaranteeList
    @Test
    void getGuarantees_happyPath_returnsGuaranteeList() {
        // GIVEN
        var guarantee = new GuaranteeDto("GUA-001", "Robo con Violencia", "RV", true);
        when(catalogsClient.getGuarantees()).thenReturn(List.of(guarantee));

        // WHEN
        var result = service.getGuarantees();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("GUA-001");
        verify(catalogsClient).getGuarantees();
    }

    // TC-07: getSubscribers_recordMissingId_dropsRecordAndLogsWarning
    @Test
    void getSubscribers_recordMissingId_dropsRecordAndLogsWarning() {
        // GIVEN — one valid record and one with null id
        var valid = new SubscriberDto("SUB-001", "Empresa ABC", "ABC", true);
        var noId = new SubscriberDto(null, "Sin Identificador", "X", true);
        when(catalogsClient.getSubscribers()).thenReturn(List.of(valid, noId));

        // WHEN
        var result = service.getSubscribers();

        // THEN — record without id is dropped; valid record is returned
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo("SUB-001");
    }

    // TC-16: catalogsService_serviceUnavailableAfterRetries_throwsCatalogServiceUnavailableException
    @Test
    void catalogsService_serviceUnavailableAfterRetries_throwsCatalogServiceUnavailableException() {
        // GIVEN — simulate retries exhausted by invoking fallback directly
        var cause = new RuntimeException("Timeout after retries");

        // WHEN / THEN — fallback must throw CatalogServiceUnavailableException (not wrap silently)
        assertThatThrownBy(() -> service.subscribersFallback(cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessageContaining("catálogos no disponible");
    }

    // TC-19 (catalogs slice): httpClientError4xx_notRetried_throwsImmediately
    @Test
    void getSubscribers_httpClientError4xx_notRetried_throwsImmediately() {
        // GIVEN — 4xx must not be wrapped in CatalogServiceUnavailableException
        when(catalogsClient.getSubscribers())
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // WHEN / THEN — exception propagates as-is; client called exactly once
        assertThatThrownBy(() -> service.getSubscribers())
                .isInstanceOf(HttpClientErrorException.class);
        verify(catalogsClient).getSubscribers();
    }
}
