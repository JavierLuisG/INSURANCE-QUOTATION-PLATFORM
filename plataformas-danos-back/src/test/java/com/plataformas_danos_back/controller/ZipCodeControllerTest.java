package com.plataformas_danos_back.controller;

import com.plataformas_danos_back.exception.InvalidZipCodeFormatException;
import com.plataformas_danos_back.exception.ZipCodeNotFoundException;
import com.plataformas_danos_back.model.dto.ZipCodeDto;
import com.plataformas_danos_back.service.ZipCodeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZipCodeControllerTest {

    @Mock
    private ZipCodeService zipCodeService;

    @InjectMocks
    private ZipCodeController controller;

    @Test
    void getByZipCode_serviceReturnsDto_returns200() {
        // GIVEN
        var dto = new ZipCodeDto("06600", "ZONA_A", "ALTO", "Ciudad de México", "Cuauhtémoc", "Ciudad de México");
        when(zipCodeService.getByZipCode("06600")).thenReturn(dto);

        // WHEN
        var response = controller.getByZipCode("06600");

        // THEN
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getCodigoPostal()).isEqualTo("06600");
        assertThat(response.getBody().getZonaCAT()).isEqualTo("ZONA_A");
    }

    @Test
    void getByZipCode_serviceThrowsZipCodeNotFoundException_propagatesException() {
        // GIVEN — GlobalExceptionHandler transforma en 404 en contexto HTTP;
        // aquí verificamos que el controlador no la suprime
        when(zipCodeService.getByZipCode("99999"))
                .thenThrow(new ZipCodeNotFoundException("Código postal no encontrado"));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getByZipCode("99999"))
                .isInstanceOf(ZipCodeNotFoundException.class)
                .hasMessage("Código postal no encontrado");
    }

    @Test
    void getByZipCode_serviceThrowsInvalidZipCodeFormatException_propagatesException() {
        // GIVEN — GlobalExceptionHandler transforma en 400 en contexto HTTP
        when(zipCodeService.getByZipCode("ABCDE"))
                .thenThrow(new InvalidZipCodeFormatException(
                        "Formato de código postal inválido. Debe ser exactamente 5 dígitos numéricos."));

        // WHEN / THEN
        assertThatThrownBy(() -> controller.getByZipCode("ABCDE"))
                .isInstanceOf(InvalidZipCodeFormatException.class)
                .hasMessageContaining("5 dígitos numéricos");
    }
}
