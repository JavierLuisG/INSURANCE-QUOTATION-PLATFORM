package com.plataformas_danos_back.service;

import com.plataformas_danos_back.exception.IngestEnProgresoException;
import com.plataformas_danos_back.model.dto.CargarTarifasRequest;
import com.plataformas_danos_back.model.dto.IngestStatusResponse;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import com.plataformas_danos_back.model.entity.CatalogoCPZonas;
import com.plataformas_danos_back.model.entity.TarifaCAT;
import com.plataformas_danos_back.model.entity.TarifaFHM;
import com.plataformas_danos_back.model.entity.TarifaIncendio;
import com.plataformas_danos_back.repository.CatalogoCPZonasRepository;
import com.plataformas_danos_back.repository.TarifaCATRepository;
import com.plataformas_danos_back.repository.TarifaFHMRepository;
import com.plataformas_danos_back.repository.TarifaIncendioRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngestorParametrosServiceImplTest {

    @Mock
    private TarifaIncendioRepository tarifaIncendioRepository;

    @Mock
    private TarifaCATRepository tarifaCATRepository;

    @Mock
    private TarifaFHMRepository tarifaFHMRepository;

    @Mock
    private CatalogoCPZonasRepository catalogoCPZonasRepository;

    @Mock
    private TariffsService tariffsService;

    @Mock
    private ZipCodeService zipCodeService;

    @InjectMocks
    private IngestorParametrosServiceImpl service;

    // ─── HU-044: Ingestión Tarifas Incendio ──────────────────────────────────

    @Test
    void ingestarTarifasIncendio_modoSimulacion_almacenaYRetornaCompletada() {
        // GIVEN
        CargarTarifasRequest request = new CargarTarifasRequest("SIMULACION");
        when(tarifaIncendioRepository.saveAll(anyList())).thenReturn(List.of());

        // WHEN
        IngestStatusResponse resultado = service.ingestarTarifasIncendio(request);

        // THEN
        assertThat(resultado.getStatus()).isEqualTo("COMPLETADA");
        assertThat(resultado.getRequestId()).isNotBlank();
        assertThat(resultado.getTimestamp()).isNotNull();
        verify(tarifaIncendioRepository).saveAll(anyList());
    }

    @Test
    void ingestarTarifasIncendio_enProgreso_lanzaIngestEnProgresoException() {
        // GIVEN — forzar AtomicBoolean a true para simular ingestión en progreso
        ReflectionTestUtils.setField(service, "ingestIncendioEnProgreso", new AtomicBoolean(true));
        CargarTarifasRequest request = new CargarTarifasRequest("SIMULACION");

        // WHEN / THEN
        assertThatThrownBy(() -> service.ingestarTarifasIncendio(request))
                .isInstanceOf(IngestEnProgresoException.class)
                .hasMessageContaining("Ya hay una ingestión de tarifas de incendio en progreso");
        verify(tarifaIncendioRepository, never()).saveAll(anyList());
    }

    @Test
    @SuppressWarnings("unchecked")
    void ingestarTarifasIncendio_desdeServicioExterno_descartaTarifasConTarifaBaseInvalida() {
        // GIVEN — BR-003: tasaBase=0.0 debe descartarse; tasaBase=0.001 debe guardarse
        CargarTarifasRequest request = new CargarTarifasRequest(null);
        TariffFireDto dtoInvalido = new TariffFireDto("ZONA_B", "Tabique", 0.0, 1.1);
        TariffFireDto dtoValido = new TariffFireDto("ZONA_A", "Concreto", 0.001, 1.15);
        when(tariffsService.getTariffsFire()).thenReturn(List.of(dtoInvalido, dtoValido));
        when(tarifaIncendioRepository.saveAll(anyList())).thenReturn(List.of());

        // WHEN
        IngestStatusResponse resultado = service.ingestarTarifasIncendio(request);

        // THEN
        assertThat(resultado.getStatus()).isEqualTo("COMPLETADA");
        ArgumentCaptor<List<TarifaIncendio>> captor = ArgumentCaptor.forClass(List.class);
        verify(tarifaIncendioRepository).saveAll(captor.capture());
        List<TarifaIncendio> guardadas = captor.getValue();
        assertThat(guardadas).hasSize(1);
        assertThat(guardadas.get(0).getZonaGeografica()).isEqualTo("ZONA_A");
    }

    @Test
    void ingestarTarifasIncendio_exceptionEnServicioExterno_retornaStatusFallida() {
        // GIVEN
        CargarTarifasRequest request = new CargarTarifasRequest(null);
        when(tariffsService.getTariffsFire()).thenThrow(new RuntimeException("Servicio externo no disponible"));

        // WHEN
        IngestStatusResponse resultado = service.ingestarTarifasIncendio(request);

        // THEN
        assertThat(resultado.getStatus()).isEqualTo("FALLIDA");
        assertThat(resultado.getMensaje()).contains("Servicio externo no disponible");
        assertThat(resultado.getRequestId()).isNotBlank();
        verify(tarifaIncendioRepository, never()).saveAll(anyList());
    }

    // ─── HU-045: Ingestión Tarifas CAT ───────────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void ingestarTarifasCAT_modoSimulacion_almacenaCuatroTarifas() {
        // GIVEN
        CargarTarifasRequest request = new CargarTarifasRequest("SIMULACION");
        when(tarifaCATRepository.saveAll(anyList())).thenReturn(List.of());

        // WHEN
        IngestStatusResponse resultado = service.ingestarTarifasCAT(request);

        // THEN
        assertThat(resultado.getStatus()).isEqualTo("COMPLETADA");
        ArgumentCaptor<List<TarifaCAT>> captor = ArgumentCaptor.forClass(List.class);
        verify(tarifaCATRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(4);
    }

    @Test
    @SuppressWarnings("unchecked")
    void ingestarTarifasCAT_desdeServicio_descartaZonasNoEnCatalogo() {
        // GIVEN — BR-004: zonaCAT no existe en catálogo → descarta aunque factor sea válido
        CargarTarifasRequest request = new CargarTarifasRequest(null);
        TariffCatDto dtoValido = new TariffCatDto("ZONA_A", 1.10, 0.0008);
        when(tariffsService.getTariffCat(anyString())).thenReturn(dtoValido);
        when(catalogoCPZonasRepository.findByZonaCAT(anyString())).thenReturn(List.of());
        when(tarifaCATRepository.saveAll(anyList())).thenReturn(List.of());

        // WHEN
        IngestStatusResponse resultado = service.ingestarTarifasCAT(request);

        // THEN
        assertThat(resultado.getStatus()).isEqualTo("COMPLETADA");
        ArgumentCaptor<List<TarifaCAT>> captor = ArgumentCaptor.forClass(List.class);
        verify(tarifaCATRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).isEmpty();
    }

    // ─── HU-046: Ingestión Tarifa FHM ────────────────────────────────────────

    @Test
    void ingestarTarifasFHM_factorNuloOInvalido_usaValorPorDefecto() {
        // GIVEN — BR-005: factor=0.0 → debe usar DEFAULT_FACTOR (0.001)
        CargarTarifasRequest request = new CargarTarifasRequest(null);
        TariffElectronicEquipmentDto dtoInvalido = new TariffElectronicEquipmentDto("A", "ALTO", 0.0);
        when(tariffsService.getTariffsElectronicEquipment()).thenReturn(List.of(dtoInvalido));
        when(tarifaFHMRepository.save(any(TarifaFHM.class))).thenReturn(null);

        // WHEN
        IngestStatusResponse resultado = service.ingestarTarifasFHM(request);

        // THEN
        assertThat(resultado.getStatus()).isEqualTo("COMPLETADA");
        ArgumentCaptor<TarifaFHM> captor = ArgumentCaptor.forClass(TarifaFHM.class);
        verify(tarifaFHMRepository).save(captor.capture());
        assertThat(captor.getValue().getFactorEquipoElectronico())
                .isEqualByComparingTo(BigDecimal.valueOf(0.001));
    }

    // ─── HU-047: Ingestión Catálogo CP-Zonas ─────────────────────────────────

    @Test
    @SuppressWarnings("unchecked")
    void ingestarCatalogoCPZonas_modoSimulacion_almacenaSieteRegistros() {
        // GIVEN
        CargarTarifasRequest request = new CargarTarifasRequest("SIMULACION");
        when(catalogoCPZonasRepository.saveAll(anyList())).thenReturn(List.of());

        // WHEN
        IngestStatusResponse resultado = service.ingestarCatalogoCPZonas(request);

        // THEN
        assertThat(resultado.getStatus()).isEqualTo("COMPLETADA");
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<CatalogoCPZonas>> captor = ArgumentCaptor.forClass(List.class);
        verify(catalogoCPZonasRepository).saveAll(captor.capture());
        assertThat(captor.getValue()).hasSize(7);
    }
}
