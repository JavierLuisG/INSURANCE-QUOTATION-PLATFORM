package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.exception.TariffNotFoundException;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import com.plataformas_danos_back.service.TariffsService;
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
class TariffsControllerTest {

    @Mock
    private TariffsService tariffsService;

    @InjectMocks
    private TariffsController controller;

    // ── Fire ─────────────────────────────────────────────────────────────────

    @Test
    void getTariffsController_getFire_returns200WithList() {
        // GIVEN
        var tariffs = List.of(
                new TariffFireDto("ZONA_A", "Concreto", 0.0012, 1.15),
                new TariffFireDto("ZONA_B", "Tabique", 0.0018, 1.25)
        );
        when(tariffsService.getTariffsFire()).thenReturn(tariffs);

        // WHEN
        var response = controller.getTariffsFire();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getZonaRiesgo()).isEqualTo("ZONA_A");
        assertThat(response.getBody().get(0).getTipoConstructivo()).isEqualTo("Concreto");
    }

    @Test
    void getTariffsController_getFire_serviceUnavailable_propagatesException() {
        // GIVEN — GlobalExceptionHandler transforma la excepción en 503 en el contexto HTTP;
        // aquí verificamos que el controlador no la suprime
        when(tariffsService.getTariffsFire())
                .thenThrow(new CatalogServiceUnavailableException("Servicio de catálogos no disponible"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getTariffsFire())
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    // ── CAT ──────────────────────────────────────────────────────────────────

    @Test
    void getTariffsController_getCat_returns200WithDto() {
        // GIVEN
        var catDto = new TariffCatDto("ZONA_A", 0.0015, 0.0008);
        when(tariffsService.getTariffCat("ZONA_A")).thenReturn(catDto);

        // WHEN
        var response = controller.getTariffCat("ZONA_A");

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getZona()).isEqualTo("ZONA_A");
        assertThat(response.getBody().getFactorTEV()).isEqualTo(0.0015);
        assertThat(response.getBody().getFactorFHM()).isEqualTo(0.0008);
    }

    @Test
    void getTariffsController_getCat_tariffNotFound_propagatesException() {
        // GIVEN — GlobalExceptionHandler transforma la excepción en 404 en el contexto HTTP;
        // aquí verificamos que el controlador no la suprime
        when(tariffsService.getTariffCat("ZONA_INEXISTENTE"))
                .thenThrow(new TariffNotFoundException("Tarifa CAT no encontrada para la zona indicada"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getTariffCat("ZONA_INEXISTENTE"))
                .isInstanceOf(TariffNotFoundException.class)
                .hasMessage("Tarifa CAT no encontrada para la zona indicada");
    }

    @Test
    void getTariffsController_getCat_serviceUnavailable_propagatesException() {
        // GIVEN
        when(tariffsService.getTariffCat("ZONA_A"))
                .thenThrow(new CatalogServiceUnavailableException("Servicio de catálogos no disponible"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getTariffCat("ZONA_A"))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }

    // ── Electronic Equipment ─────────────────────────────────────────────────

    @Test
    void getTariffsController_getElectronicEquipment_returns200WithList() {
        // GIVEN
        var factors = List.of(
                new TariffElectronicEquipmentDto("A", "ALTO", 0.0025),
                new TariffElectronicEquipmentDto("B", "MEDIO", 0.0018)
        );
        when(tariffsService.getTariffsElectronicEquipment()).thenReturn(factors);

        // WHEN
        var response = controller.getTariffsElectronicEquipment();

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody().get(0).getClase()).isEqualTo("A");
        assertThat(response.getBody().get(0).getNivelZona()).isEqualTo("ALTO");
        assertThat(response.getBody().get(0).getFactor()).isEqualTo(0.0025);
    }

    @Test
    void getTariffsController_getElectronicEquipment_serviceUnavailable_propagatesException() {
        // GIVEN — GlobalExceptionHandler transforma la excepción en 503 en el contexto HTTP;
        // aquí verificamos que el controlador no la suprime
        when(tariffsService.getTariffsElectronicEquipment())
                .thenThrow(new CatalogServiceUnavailableException("Servicio de catálogos no disponible"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getTariffsElectronicEquipment())
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }
}
