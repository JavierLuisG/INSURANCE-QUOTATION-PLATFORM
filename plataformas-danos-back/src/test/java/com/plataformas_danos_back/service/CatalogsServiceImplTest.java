package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.CatalogsClient;
import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogsServiceImplTest {

    @Mock
    private CatalogsClient catalogsClient;

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
}
