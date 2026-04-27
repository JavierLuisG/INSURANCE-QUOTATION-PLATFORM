package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.ZipCodeClient;
import com.plataformas_danos_back.exception.CatalogServiceUnavailableException;
import com.plataformas_danos_back.exception.InvalidZipCodeFormatException;
import com.plataformas_danos_back.exception.ZipCodeNotFoundException;
import com.plataformas_danos_back.model.dto.ZipCodeDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ZipCodeServiceImplTest {

    @Mock
    private ZipCodeClient zipCodeClient;

    @InjectMocks
    private ZipCodeServiceImpl service;

    // ── Happy Path ────────────────────────────────────────────────────────────

    @Test
    void getByZipCode_validCp_returnsDto() {
        // GIVEN
        var dto = new ZipCodeDto("06600", "ZONA_A", "ALTO", "Ciudad de México", "Cuauhtémoc", "Ciudad de México");
        when(zipCodeClient.getByZipCode("06600")).thenReturn(dto);

        // WHEN
        var result = service.getByZipCode("06600");

        // THEN
        assertThat(result.getCodigoPostal()).isEqualTo("06600");
        assertThat(result.getZonaCAT()).isEqualTo("ZONA_A");
        assertThat(result.getNivelTecnico()).isEqualTo("ALTO");
    }

    @Test
    void getByZipCode_mapsAllFields() {
        // GIVEN
        var dto = new ZipCodeDto("01000", "ZONA_B", "MEDIO", "Estado de México", "Naucalpan", "Naucalpan");
        when(zipCodeClient.getByZipCode("01000")).thenReturn(dto);

        // WHEN
        var result = service.getByZipCode("01000");

        // THEN
        assertThat(result.getCodigoPostal()).isEqualTo("01000");
        assertThat(result.getZonaCAT()).isEqualTo("ZONA_B");
        assertThat(result.getNivelTecnico()).isEqualTo("MEDIO");
        assertThat(result.getEstado()).isEqualTo("Estado de México");
        assertThat(result.getMunicipio()).isEqualTo("Naucalpan");
        assertThat(result.getCiudad()).isEqualTo("Naucalpan");
    }

    // ── Default Values ────────────────────────────────────────────────────────

    @Test
    void getByZipCode_zonaCATNull_appliesDefaultZona() {
        // GIVEN — zonaCAT ausente en respuesta del mock
        var dto = new ZipCodeDto("06600", null, "ALTO", "Ciudad de México", "Cuauhtémoc", null);
        when(zipCodeClient.getByZipCode("06600")).thenReturn(dto);

        // WHEN
        var result = service.getByZipCode("06600");

        // THEN
        assertThat(result.getZonaCAT()).isEqualTo("ZONA_INDEFINIDA");
        assertThat(result.getNivelTecnico()).isEqualTo("ALTO");
    }

    @Test
    void getByZipCode_nivelTecnicoNull_appliesDefaultNivel() {
        // GIVEN — nivelTecnico ausente en respuesta del mock
        var dto = new ZipCodeDto("06600", "ZONA_A", null, "Ciudad de México", "Cuauhtémoc", null);
        when(zipCodeClient.getByZipCode("06600")).thenReturn(dto);

        // WHEN
        var result = service.getByZipCode("06600");

        // THEN
        assertThat(result.getZonaCAT()).isEqualTo("ZONA_A");
        assertThat(result.getNivelTecnico()).isEqualTo("NIVEL_INDEFINIDO");
    }

    // ── Format Validation ────────────────────────────────────────────────────

    @Test
    void getByZipCode_invalidFormat_throwsInvalidZipCodeFormatException() {
        // GIVEN — formato con letras
        // WHEN / THEN
        assertThatThrownBy(() -> service.getByZipCode("ABCDE"))
                .isInstanceOf(InvalidZipCodeFormatException.class)
                .hasMessageContaining("5 dígitos numéricos");
    }

    @Test
    void getByZipCode_tooShort_throwsInvalidZipCodeFormatException() {
        // GIVEN — solo 4 dígitos
        // WHEN / THEN
        assertThatThrownBy(() -> service.getByZipCode("1234"))
                .isInstanceOf(InvalidZipCodeFormatException.class);
    }

    @Test
    void getByZipCode_tooLong_throwsInvalidZipCodeFormatException() {
        // GIVEN — 6 dígitos
        // WHEN / THEN
        assertThatThrownBy(() -> service.getByZipCode("123456"))
                .isInstanceOf(InvalidZipCodeFormatException.class);
    }

    @Test
    void getByZipCode_invalidFormat_clientNeverCalled() {
        // GIVEN — formato inválido no debe llamar al servicio externo
        // WHEN
        try {
            service.getByZipCode("ABCDE");
        } catch (InvalidZipCodeFormatException ignored) {
        }

        // THEN — el cliente no fue invocado
        verify(zipCodeClient, never()).getByZipCode("ABCDE");
    }

    // ── Error Path ────────────────────────────────────────────────────────────

    @Test
    void getByZipCode_clientReturns404_throwsZipCodeNotFoundException() {
        // GIVEN — el servicio externo devuelve 404
        when(zipCodeClient.getByZipCode("99999"))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // WHEN / THEN
        assertThatThrownBy(() -> service.getByZipCode("99999"))
                .isInstanceOf(ZipCodeNotFoundException.class)
                .hasMessage("Código postal no encontrado");
    }

    // ── Fallback ──────────────────────────────────────────────────────────────

    @Test
    void zipCodeFallback_whenCalled_throwsCatalogServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Connection refused");

        // WHEN / THEN
        assertThatThrownBy(() -> service.zipCodeFallback("06600", cause))
                .isInstanceOf(CatalogServiceUnavailableException.class)
                .hasMessage("Servicio de catálogos no disponible");
    }
}
