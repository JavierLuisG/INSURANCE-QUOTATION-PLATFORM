package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.model.dto.AgentDto;
import com.plataformas_danos_back.model.dto.BusinessLineDto;
import com.plataformas_danos_back.model.dto.SubscriberDto;
import com.plataformas_danos_back.service.CatalogsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogsControllerTest {

    @Mock
    private CatalogsService catalogsService;

    @InjectMocks
    private CatalogsController controller;

    // ── Subscribers ──────────────────────────────────────────────────────────

    @Test
    void getSubscribers_serviceReturnsData_returns200WithList() {
        // GIVEN
        var subscribers = List.of(
                new SubscriberDto("SUB-001", "Empresa ABC S.A. de C.V.", "ABC", true),
                new SubscriberDto("SUB-002", "Corporativo XYZ", "XYZ", true)
        );
        when(catalogsService.getSubscribers()).thenReturn(subscribers);

        // WHEN
        var response = controller.getSubscribers();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getId()).isEqualTo("SUB-001");
        assertThat(response.getBody().get(0).getNombre()).isEqualTo("Empresa ABC S.A. de C.V.");
    }

    @Test
    void getSubscribers_emptyList_returns200WithEmptyBody() {
        // GIVEN
        when(catalogsService.getSubscribers()).thenReturn(List.of());

        // WHEN
        var response = controller.getSubscribers();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getSubscribers_serviceUnavailable_propagatesException() {
        // GIVEN — GlobalExceptionHandler transforma la excepción en 503 en el contexto HTTP;
        // aquí verificamos que el controlador no la suprime
        when(catalogsService.getSubscribers())
                .thenThrow(new CatalogServiceUnavailableException("Servicio de catálogos no disponible"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getSubscribers())
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    // ── Agents ──────────────────────────────────────────────────────────────

    @Test
    void getAgents_serviceReturnsData_returns200WithList() {
        // GIVEN
        var agents = List.of(new AgentDto("AGT-001", "Juan Pérez García", "JPG", true));
        when(catalogsService.getAgents()).thenReturn(agents);

        // WHEN
        var response = controller.getAgents();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo("AGT-001");
    }

    @Test
    void getAgents_serviceUnavailable_propagatesException() {
        // GIVEN
        when(catalogsService.getAgents())
                .thenThrow(new CatalogServiceUnavailableException("Servicio de catálogos no disponible"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getAgents())
                .isInstanceOf(CatalogServiceUnavailableException.class);
    }

    // ── Business Lines ───────────────────────────────────────────────────────

    @Test
    void getBusinessLines_serviceReturnsData_returns200WithList() {
        // GIVEN
        var lines = List.of(new BusinessLineDto("BL-001", "Comercio al por menor", "CM", true));
        when(catalogsService.getBusinessLines()).thenReturn(lines);

        // WHEN
        var response = controller.getBusinessLines();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getId()).isEqualTo("BL-001");
        assertThat(response.getBody().get(0).getDescripcion()).isEqualTo("Comercio al por menor");
    }

    @Test
    void getBusinessLines_serviceUnavailable_propagatesException() {
        // GIVEN
        when(catalogsService.getBusinessLines())
                .thenThrow(new CatalogServiceUnavailableException("Servicio de catálogos no disponible"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getBusinessLines())
                .isInstanceOf(CatalogServiceUnavailableException.class);
    }
}
