package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.ZipCodeClient;
import com.plataformas_danos_back.exception.InvalidZipCodeFormatException;
import com.plataformas_danos_back.exception.ZipCodeNotFoundException;
import com.plataformas_danos_back.exception.ZipCodeServiceUnavailableException;
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

    @Mock
    private DataValidationService dataValidationService;

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
    void zipCodeFallback_whenCalled_throwsZipCodeServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Connection refused");

        // WHEN / THEN — fallback must throw ZipCodeServiceUnavailableException specifically
        assertThatThrownBy(() -> service.zipCodeFallback("06600", cause))
                .isInstanceOf(ZipCodeServiceUnavailableException.class)
                .hasMessageContaining("CP no disponible");
    }

    // ── SPEC-009 mandatory cases ─────────────────────────────────────────────

    // TC-08: getByZipCode_validZipCode_returns200WithZone
    @Test
    void getByZipCode_validZipCode_returns200WithZone() {
        // GIVEN
        var dto = new ZipCodeDto("64000", "ZONA_B", "MEDIO", "Nuevo León", "Monterrey", "Monterrey");
        when(zipCodeClient.getByZipCode("64000")).thenReturn(dto);

        // WHEN
        var result = service.getByZipCode("64000");

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getCodigoPostal()).isEqualTo("64000");
        assertThat(result.getZonaCAT()).isEqualTo("ZONA_B");
        verify(zipCodeClient).getByZipCode("64000");
    }

    // TC-09: getByZipCode_invalidFormat_throwsInvalidZipCodeFormatException (both formats)
    @Test
    void getByZipCode_fourDigits_throwsInvalidZipCodeFormatExceptionWithoutCallingClient() {
        // GIVEN — "1234" has only 4 digits, format invalid
        // WHEN / THEN — exception thrown before client is called
        assertThatThrownBy(() -> service.getByZipCode("1234"))
                .isInstanceOf(InvalidZipCodeFormatException.class)
                .hasMessageContaining("5 dígitos numéricos");
        verify(zipCodeClient, never()).getByZipCode("1234");
    }

    // TC-10: getByZipCode_notFound_throwsZipCodeNotFoundException
    @Test
    void getByZipCode_notFound_throwsZipCodeNotFoundException() {
        // GIVEN — external service returns 404
        when(zipCodeClient.getByZipCode("00000"))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // WHEN / THEN
        assertThatThrownBy(() -> service.getByZipCode("00000"))
                .isInstanceOf(ZipCodeNotFoundException.class)
                .hasMessage("Código postal no encontrado");
    }

    // TC-11: getByZipCode_missingZona_appliesDefaultZona
    @Test
    void getByZipCode_missingZona_appliesDefaultZona() {
        // GIVEN — client returns dto with null zonaCAT
        var dto = new ZipCodeDto("12345", null, "ALTO", "Jalisco", "Guadalajara", "Guadalajara");
        when(zipCodeClient.getByZipCode("12345")).thenReturn(dto);

        // WHEN
        var result = service.getByZipCode("12345");

        // THEN — DEFAULT_ZONA applied
        assertThat(result.getZonaCAT()).isEqualTo("ZONA_INDEFINIDA");
    }

    // TC-17: zipCodeService_serviceUnavailableAfterRetries_throwsZipCodeServiceUnavailableException
    @Test
    void zipCodeService_serviceUnavailableAfterRetries_throwsZipCodeServiceUnavailableException() {
        // GIVEN — simulate retries exhausted; call fallback directly
        var cause = new RuntimeException("Connection timeout after max retries");

        // WHEN / THEN
        assertThatThrownBy(() -> service.zipCodeFallback("06600", cause))
                .isInstanceOf(ZipCodeServiceUnavailableException.class)
                .hasMessageContaining("CP no disponible");
    }

    // TC-19 (zip-code slice): httpClientError4xx_notRetried_throwsImmediately
    @Test
    void getByZipCode_httpClientError400_notRetried_throwsImmediately() {
        // GIVEN — 4xx other than 404 propagates without wrapping
        when(zipCodeClient.getByZipCode("06600"))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // WHEN / THEN — client called exactly once; exception propagates as HttpClientErrorException
        assertThatThrownBy(() -> service.getByZipCode("06600"))
                .isInstanceOf(HttpClientErrorException.class);
        verify(zipCodeClient).getByZipCode("06600");
    }
}
