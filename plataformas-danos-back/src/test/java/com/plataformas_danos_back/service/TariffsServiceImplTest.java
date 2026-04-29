package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.TariffsClient;
import com.plataformas_danos_back.exception.TariffNotFoundException;
import com.plataformas_danos_back.exception.TariffServiceUnavailableException;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
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
class TariffsServiceImplTest {

    @Mock
    private TariffsClient tariffsClient;

    @Mock
    private DataValidationService dataValidationService;

    @InjectMocks
    private TariffsServiceImpl service;

    // ── Tariffs Fire ─────────────────────────────────────────────────────────

    @Test
    void getTariffsFire_validList_returnsListWithData() {
        // GIVEN
        var tariff = new TariffFireDto("ZONA_A", "Concreto", 0.0012, 1.15);
        when(tariffsClient.getTariffsFire()).thenReturn(List.of(tariff));

        // WHEN
        var result = service.getTariffsFire();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getZonaRiesgo()).isEqualTo("ZONA_A");
    }

    @Test
    void getTariffsFire_emptyList_returnsEmptyList() {
        // GIVEN
        when(tariffsClient.getTariffsFire()).thenReturn(List.of());

        // WHEN
        var result = service.getTariffsFire();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void tariffFireFallback_whenCalled_throwsTariffServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Connection refused");

        // WHEN / THEN — tariff fallback throws TariffServiceUnavailableException (not CatalogServiceUnavailableException)
        assertThatThrownBy(() -> service.tariffFireFallback(cause))
                .isInstanceOf(TariffServiceUnavailableException.class)
                .hasMessageContaining("tarifas no disponible");
    }

    @Test
    void getTariffsFire_recordMissingZonaRiesgo_isDropped() {
        // GIVEN
        var valid = new TariffFireDto("ZONA_A", "Concreto", 0.0012, 1.15);
        var missingZona = new TariffFireDto(null, "Tabique", 0.0018, 1.25);
        when(tariffsClient.getTariffsFire()).thenReturn(List.of(valid, missingZona));

        // WHEN
        var result = service.getTariffsFire();

        // THEN — solo el registro válido se retorna
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getZonaRiesgo()).isEqualTo("ZONA_A");
    }

    @Test
    void getTariffsFire_mapsAllFields() {
        // GIVEN
        var tariff = new TariffFireDto("ZONA_A", "Concreto", 0.0012, 1.15);
        when(tariffsClient.getTariffsFire()).thenReturn(List.of(tariff));

        // WHEN
        var result = service.getTariffsFire();

        // THEN
        var dto = result.get(0);
        assertThat(dto.getZonaRiesgo()).isEqualTo("ZONA_A");
        assertThat(dto.getTipoConstructivo()).isEqualTo("Concreto");
        assertThat(dto.getTasaBase()).isEqualTo(0.0012);
        assertThat(dto.getFactorRecargo()).isEqualTo(1.15);
    }

    // ── Tariff CAT ───────────────────────────────────────────────────────────

    @Test
    void getTariffCat_validZona_returnsDto() {
        // GIVEN
        var catDto = new TariffCatDto("ZONA_A", 0.0015, 0.0008);
        when(tariffsClient.getTariffCat("ZONA_A")).thenReturn(catDto);

        // WHEN
        var result = service.getTariffCat("ZONA_A");

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getZona()).isEqualTo("ZONA_A");
        assertThat(result.getFactorTEV()).isEqualTo(0.0015);
        assertThat(result.getFactorFHM()).isEqualTo(0.0008);
    }

    @Test
    void getTariffCat_zonaNotFound_throwsTariffNotFoundException() {
        // GIVEN
        when(tariffsClient.getTariffCat("ZONA_INEXISTENTE"))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // WHEN / THEN
        assertThatThrownBy(() -> service.getTariffCat("ZONA_INEXISTENTE"))
                .isInstanceOf(TariffNotFoundException.class)
                .hasMessage("Tarifa CAT no encontrada para la zona indicada");
    }

    @Test
    void tariffCatFallback_whenCalled_throwsTariffServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Timeout");

        // WHEN / THEN
        assertThatThrownBy(() -> service.tariffCatFallback("ZONA_A", cause))
                .isInstanceOf(TariffServiceUnavailableException.class)
                .hasMessageContaining("tarifas no disponible");
    }

    @Test
    void getTariffCat_mapsAllFields() {
        // GIVEN
        var catDto = new TariffCatDto("ZONA_A", 0.0015, 0.0008);
        when(tariffsClient.getTariffCat("ZONA_A")).thenReturn(catDto);

        // WHEN
        var result = service.getTariffCat("ZONA_A");

        // THEN
        assertThat(result.getZona()).isEqualTo("ZONA_A");
        assertThat(result.getFactorTEV()).isEqualTo(0.0015);
        assertThat(result.getFactorFHM()).isEqualTo(0.0008);
    }

    // ── Tariffs Electronic Equipment ─────────────────────────────────────────

    @Test
    void getTariffsElectronicEquipment_validList_returnsListWithData() {
        // GIVEN
        var ee = new TariffElectronicEquipmentDto("A", "ALTO", 0.0025);
        when(tariffsClient.getTariffsElectronicEquipment()).thenReturn(List.of(ee));

        // WHEN
        var result = service.getTariffsElectronicEquipment();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClase()).isEqualTo("A");
    }

    @Test
    void getTariffsElectronicEquipment_emptyList_returnsEmptyList() {
        // GIVEN
        when(tariffsClient.getTariffsElectronicEquipment()).thenReturn(List.of());

        // WHEN
        var result = service.getTariffsElectronicEquipment();

        // THEN
        assertThat(result).isEmpty();
    }

    @Test
    void tariffElectronicEquipmentFallback_whenCalled_throwsTariffServiceUnavailableException() {
        // GIVEN
        var cause = new RuntimeException("Service down");

        // WHEN / THEN
        assertThatThrownBy(() -> service.tariffElectronicEquipmentFallback(cause))
                .isInstanceOf(TariffServiceUnavailableException.class)
                .hasMessageContaining("tarifas no disponible");
    }

    @Test
    void getTariffsElectronicEquipment_recordMissingClase_isDropped() {
        // GIVEN
        var valid = new TariffElectronicEquipmentDto("A", "ALTO", 0.0025);
        var missingClase = new TariffElectronicEquipmentDto("", "MEDIO", 0.0018);
        when(tariffsClient.getTariffsElectronicEquipment()).thenReturn(List.of(valid, missingClase));

        // WHEN
        var result = service.getTariffsElectronicEquipment();

        // THEN — solo el registro válido se retorna
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClase()).isEqualTo("A");
    }

    @Test
    void getTariffsElectronicEquipment_mapsAllFields() {
        // GIVEN
        var ee = new TariffElectronicEquipmentDto("A", "ALTO", 0.0025);
        when(tariffsClient.getTariffsElectronicEquipment()).thenReturn(List.of(ee));

        // WHEN
        var result = service.getTariffsElectronicEquipment();

        // THEN
        var dto = result.get(0);
        assertThat(dto.getClase()).isEqualTo("A");
        assertThat(dto.getNivelZona()).isEqualTo("ALTO");
        assertThat(dto.getFactor()).isEqualTo(0.0025);
    }

    // ── SPEC-009 mandatory cases ─────────────────────────────────────────────

    // TC-12: getTariffsFire_happyPath_returnsTariffList
    @Test
    void getTariffsFire_happyPath_returnsTariffList() {
        // GIVEN
        var tariff = new TariffFireDto("ZONA_A", "Concreto", 0.0012, 1.15);
        when(tariffsClient.getTariffsFire()).thenReturn(List.of(tariff));

        // WHEN
        var result = service.getTariffsFire();

        // THEN
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getZonaRiesgo()).isEqualTo("ZONA_A");
        verify(tariffsClient).getTariffsFire();
    }

    // TC-13: getTariffCat_validZone_returnsTariffCatDto
    @Test
    void getTariffCat_validZone_returnsTariffCatDto() {
        // GIVEN
        var catDto = new TariffCatDto("ZONA_B", 0.0020, 0.0010);
        when(tariffsClient.getTariffCat("ZONA_B")).thenReturn(catDto);

        // WHEN
        var result = service.getTariffCat("ZONA_B");

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getZona()).isEqualTo("ZONA_B");
        assertThat(result.getFactorTEV()).isEqualTo(0.0020);
        assertThat(result.getFactorFHM()).isEqualTo(0.0010);
        verify(tariffsClient).getTariffCat("ZONA_B");
    }

    // TC-14: getTariffCat_notFound_throwsTariffNotFoundException_noRetry
    @Test
    void getTariffCat_notFound_throwsTariffNotFoundException_noRetry() {
        // GIVEN — 404 must throw TariffNotFoundException, NOT TariffServiceUnavailableException
        when(tariffsClient.getTariffCat("ZONA_INEXISTENTE"))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // WHEN / THEN — client called exactly once (no retry for 4xx)
        assertThatThrownBy(() -> service.getTariffCat("ZONA_INEXISTENTE"))
                .isInstanceOf(TariffNotFoundException.class)
                .isNotInstanceOf(TariffServiceUnavailableException.class)
                .hasMessage("Tarifa CAT no encontrada para la zona indicada");
        verify(tariffsClient).getTariffCat("ZONA_INEXISTENTE");
    }

    // TC-15: getTariffsElectronicEquipment_happyPath_returnsList
    @Test
    void getTariffsElectronicEquipment_happyPath_returnsList() {
        // GIVEN
        var ee1 = new TariffElectronicEquipmentDto("A", "ALTO", 0.0025);
        var ee2 = new TariffElectronicEquipmentDto("B", "MEDIO", 0.0018);
        when(tariffsClient.getTariffsElectronicEquipment()).thenReturn(List.of(ee1, ee2));

        // WHEN
        var result = service.getTariffsElectronicEquipment();

        // THEN
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getClase()).isEqualTo("A");
        assertThat(result.get(1).getClase()).isEqualTo("B");
        verify(tariffsClient).getTariffsElectronicEquipment();
    }

    // TC-18: tariffsService_serviceUnavailableAfterRetries_throwsTariffServiceUnavailableException
    @Test
    void tariffsService_serviceUnavailableAfterRetries_throwsTariffServiceUnavailableException() {
        // GIVEN — simulate retries exhausted; invoke fallback directly
        var cause = new RuntimeException("Timeout after max retries");

        // WHEN / THEN
        assertThatThrownBy(() -> service.tariffFireFallback(cause))
                .isInstanceOf(TariffServiceUnavailableException.class)
                .hasMessageContaining("tarifas no disponible");
    }

    // TC-19 (tariffs slice): httpClientError4xx_notRetried_throwsImmediately
    @Test
    void getTariffCat_httpClientError400_notRetried_throwsImmediately() {
        // GIVEN — 400 other than 404 propagates directly without wrapping
        when(tariffsClient.getTariffCat("ZONA_A"))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // WHEN / THEN — client called exactly once
        assertThatThrownBy(() -> service.getTariffCat("ZONA_A"))
                .isInstanceOf(HttpClientErrorException.class);
        verify(tariffsClient).getTariffCat("ZONA_A");
    }
}
