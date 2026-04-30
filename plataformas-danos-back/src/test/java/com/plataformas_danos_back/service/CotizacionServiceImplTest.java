package com.plataformas_danos_back.service;

import com.plataformas_danos_back.client.CotizadorCatalogosClient;
import com.plataformas_danos_back.client.FoliosClient;
import com.plataformas_danos_back.exception.CotizacionConflictException;
import com.plataformas_danos_back.exception.CotizacionNotFoundException;
import com.plataformas_danos_back.exception.FolioServiceUnavailableException;
import com.plataformas_danos_back.exception.ValidationException;
import com.plataformas_danos_back.model.dto.CotizacionRequest;
import com.plataformas_danos_back.model.dto.CotizacionResponse;
import com.plataformas_danos_back.model.entity.Cotizacion;
import com.plataformas_danos_back.repository.CotizacionRepository;
import com.plataformas_danos_back.validator.RfcValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;

import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

// SPEC-011 — Tests unitarios del servicio de cotizaciones (HU-001 a HU-005)
@ExtendWith(MockitoExtension.class)
class CotizacionServiceImplTest {

    @Mock
    private CotizacionRepository cotizacionRepository;

    @Mock
    private FoliosClient foliosClient;

    @Mock
    private CotizadorCatalogosClient cotizadorCatalogosClient;

    @Mock
    private RfcValidator rfcValidator;

    @InjectMocks
    private CotizacionServiceImpl service;

    // ─── HU-001: Iniciar cotización ───────────────────────────────────────────

    @Test
    void iniciar_cuandoServicioFoliosDisponible_creaDocumentoConFolioYEstadoIncompleta() {
        // GIVEN
        String folioEsperado = "COT-2026-000001";
        Cotizacion cotizacionGuardada = Cotizacion.builder()
                .id("mongo-id-001")
                .folio(folioEsperado)
                .estadoValidacion("INCOMPLETA")
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .version(0L)
                .build();

        when(foliosClient.generarFolio()).thenReturn(folioEsperado);
        when(cotizacionRepository.save(any(Cotizacion.class))).thenReturn(cotizacionGuardada);

        // WHEN
        CotizacionResponse response = service.iniciar();

        // THEN
        assertThat(response.getFolio()).isEqualTo(folioEsperado);
        assertThat(response.getEstadoValidacion()).isEqualTo("INCOMPLETA");
        assertThat(response.getVersion()).isEqualTo(0L);
        verify(foliosClient).generarFolio();
        verify(cotizacionRepository).save(any(Cotizacion.class));
    }

    @Test
    void iniciar_cuandoServicioFoliosFalla_lanzaServiceUnavailableException() {
        // GIVEN
        when(foliosClient.generarFolio())
                .thenThrow(new RuntimeException("Connection refused"));

        // WHEN / THEN
        assertThatThrownBy(() -> service.iniciar())
                .isInstanceOf(FolioServiceUnavailableException.class)
                .hasMessageContaining("No se pudo iniciar la cotización");

        verify(cotizacionRepository, never()).save(any());
    }

    // ─── HU-003: Validación de RFC ────────────────────────────────────────────

    @Test
    void actualizar_cuandoRfcInvalido_lanzaValidationException() {
        // GIVEN
        String folio = "COT-2026-000001";
        Cotizacion existente = Cotizacion.builder()
                .folio(folio)
                .estadoValidacion("INCOMPLETA")
                .version(0L)
                .build();
        CotizacionRequest request = CotizacionRequest.builder()
                .rfcAsegurado("RFCINVALIDO123")
                .version(0L)
                .build();

        when(cotizacionRepository.findByFolio(folio)).thenReturn(Optional.of(existente));
        when(rfcValidator.isValid("RFCINVALIDO123")).thenReturn(false);

        // WHEN / THEN
        assertThatThrownBy(() -> service.actualizar(folio, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("RFC");

        verify(cotizacionRepository, never()).save(any());
    }

    // ─── HU-005: Validación de rango de fechas ────────────────────────────────

    @Test
    void actualizar_cuandoFechaFinAnteriorAInicio_lanzaValidationException() {
        // GIVEN
        String folio = "COT-2026-000002";
        Cotizacion existente = Cotizacion.builder()
                .folio(folio)
                .estadoValidacion("INCOMPLETA")
                .version(1L)
                .build();
        CotizacionRequest request = CotizacionRequest.builder()
                .fechaInicioVigencia("2026-05-31")
                .fechaFinVigencia("2026-05-01")
                .version(1L)
                .build();

        when(cotizacionRepository.findByFolio(folio)).thenReturn(Optional.of(existente));

        // WHEN / THEN
        assertThatThrownBy(() -> service.actualizar(folio, request))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("fecha de fin de vigencia no puede ser anterior");

        verify(cotizacionRepository, never()).save(any());
    }

    // ─── HU-002: Obtener por folio ────────────────────────────────────────────

    @Test
    void obtenerPorFolio_cuandoFolioNoExiste_lanzaNotFoundException() {
        // GIVEN
        String folioInexistente = "COT-2026-999999";
        when(cotizacionRepository.findByFolio(folioInexistente)).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> service.obtenerPorFolio(folioInexistente))
                .isInstanceOf(CotizacionNotFoundException.class)
                .hasMessageContaining(folioInexistente);
    }

    // ─── HU-002: Concurrencia optimista ──────────────────────────────────────

    @Test
    void actualizar_cuandoConflictoDeVersion_lanzaOptimisticLockException() {
        // GIVEN — la versión en la DB es 2, el request trae version=1 (desactualizado)
        String folio = "COT-2026-000003";
        Cotizacion existente = Cotizacion.builder()
                .folio(folio)
                .estadoValidacion("INCOMPLETA")
                .version(2L)
                .build();
        CotizacionRequest request = CotizacionRequest.builder()
                .nombreAsegurado("Juan Perez")
                .version(1L)
                .build();

        when(cotizacionRepository.findByFolio(folio)).thenReturn(Optional.of(existente));

        // WHEN / THEN
        assertThatThrownBy(() -> service.actualizar(folio, request))
                .isInstanceOf(CotizacionConflictException.class)
                .hasMessageContaining("modificada por otro usuario");

        verify(cotizacionRepository, never()).save(any());
    }
}
