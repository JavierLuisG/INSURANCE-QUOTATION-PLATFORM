package com.plataformas_danos_back.service;

import com.plataformas_danos_back.exception.ParametroNoDisponibleException;
import com.plataformas_danos_back.model.dto.CatalogoCPZonasResponse;
import com.plataformas_danos_back.model.dto.ParametrosStatusResponse;
import com.plataformas_danos_back.model.dto.TarifaCATResponse;
import com.plataformas_danos_back.model.dto.TarifaFHMResponse;
import com.plataformas_danos_back.model.dto.TarifaIncendioResponse;
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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ParametroCalculoServiceImplTest {

    @Mock
    private TarifaIncendioRepository tarifaIncendioRepository;

    @Mock
    private TarifaCATRepository tarifaCATRepository;

    @Mock
    private TarifaFHMRepository tarifaFHMRepository;

    @Mock
    private CatalogoCPZonasRepository catalogoCPZonasRepository;

    @InjectMocks
    private ParametroCalculoServiceImpl service;

    // ─── HU-044: Tarifas Incendio ─────────────────────────────────────────────

    @Test
    void obtenerTarifasIncendioVigentes_conTarifasDisponibles_retornaLista() {
        // GIVEN
        Instant ahora = Instant.now();
        TarifaIncendio entidad = TarifaIncendio.builder()
                .id("uuid-001")
                .zonaGeografica("Zona_A")
                .tipoInmueble("Residencial Urbano")
                .tarifaBase(new BigDecimal("0.0050"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen("SIMULACION")
                .build();
        when(tarifaIncendioRepository.findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class)))
                .thenReturn(List.of(entidad));

        // WHEN
        List<TarifaIncendioResponse> resultado = service.obtenerTarifasIncendioVigentes();

        // THEN
        assertThat(resultado).hasSize(1);
        TarifaIncendioResponse respuesta = resultado.get(0);
        assertThat(respuesta.getId()).isEqualTo("uuid-001");
        assertThat(respuesta.getZonaGeografica()).isEqualTo("Zona_A");
        assertThat(respuesta.getTipoInmueble()).isEqualTo("Residencial Urbano");
        assertThat(respuesta.getTarifaBase()).isEqualByComparingTo(new BigDecimal("0.0050"));
        assertThat(respuesta.getOrigen()).isEqualTo("SIMULACION");
        verify(tarifaIncendioRepository).findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class));
    }

    @Test
    void obtenerTarifasIncendioVigentes_sinTarifasVigentes_lanzaParametroNoDisponibleException() {
        // GIVEN
        when(tarifaIncendioRepository.findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class)))
                .thenReturn(List.of());

        // WHEN / THEN
        assertThatThrownBy(() -> service.obtenerTarifasIncendioVigentes())
                .isInstanceOf(ParametroNoDisponibleException.class)
                .hasMessageContaining("No hay tarifas de incendio vigentes disponibles");
        verify(tarifaIncendioRepository).findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class));
    }

    // ─── HU-045: Tarifas CAT ──────────────────────────────────────────────────

    @Test
    void obtenerTarifasCATVigentes_conTarifasDisponibles_retornaLista() {
        // GIVEN
        Instant ahora = Instant.now();
        TarifaCAT entidad = TarifaCAT.builder()
                .id("uuid-cat-001")
                .zonaCAT("ZONA_A")
                .factorCAT(new BigDecimal("1.10"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen("SIMULACION")
                .build();
        when(tarifaCATRepository.findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class)))
                .thenReturn(List.of(entidad));

        // WHEN
        List<TarifaCATResponse> resultado = service.obtenerTarifasCATVigentes();

        // THEN
        assertThat(resultado).hasSize(1);
        TarifaCATResponse respuesta = resultado.get(0);
        assertThat(respuesta.getId()).isEqualTo("uuid-cat-001");
        assertThat(respuesta.getZonaCAT()).isEqualTo("ZONA_A");
        assertThat(respuesta.getFactorCAT()).isEqualByComparingTo(new BigDecimal("1.10"));
        verify(tarifaCATRepository).findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class));
    }

    @Test
    void obtenerTarifasCATVigentes_sinTarifasVigentes_lanzaParametroNoDisponibleException() {
        // GIVEN
        when(tarifaCATRepository.findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class)))
                .thenReturn(List.of());

        // WHEN / THEN
        assertThatThrownBy(() -> service.obtenerTarifasCATVigentes())
                .isInstanceOf(ParametroNoDisponibleException.class)
                .hasMessageContaining("No hay tarifas CAT vigentes disponibles");
        verify(tarifaCATRepository).findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class));
    }

    // ─── HU-046: Tarifa FHM ───────────────────────────────────────────────────

    @Test
    void obtenerTarifaFHMVigente_conTarifaDisponible_retornaOptional() {
        // GIVEN
        Instant ahora = Instant.now();
        TarifaFHM entidad = TarifaFHM.builder()
                .id("uuid-fhm-001")
                .tarifaFHM(new BigDecimal("0.0500"))
                .factorEquipoElectronico(new BigDecimal("1.2500"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen("SIMULACION")
                .build();
        when(tarifaFHMRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(entidad));

        // WHEN
        Optional<TarifaFHMResponse> resultado = service.obtenerTarifaFHMVigente();

        // THEN
        assertThat(resultado).isPresent();
        TarifaFHMResponse respuesta = resultado.get();
        assertThat(respuesta.getId()).isEqualTo("uuid-fhm-001");
        assertThat(respuesta.getTarifaFHM()).isEqualByComparingTo(new BigDecimal("0.0500"));
        assertThat(respuesta.getFactorEquipoElectronico()).isEqualByComparingTo(new BigDecimal("1.2500"));
        verify(tarifaFHMRepository).findFirstByOrderByCreatedAtDesc();
    }

    @Test
    void obtenerTarifaFHMVigente_sinTarifa_lanzaParametroNoDisponibleException() {
        // GIVEN
        when(tarifaFHMRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.empty());

        // WHEN / THEN
        assertThatThrownBy(() -> service.obtenerTarifaFHMVigente())
                .isInstanceOf(ParametroNoDisponibleException.class)
                .hasMessageContaining("No hay tarifa FHM disponible");
        verify(tarifaFHMRepository).findFirstByOrderByCreatedAtDesc();
    }

    // ─── HU-047: Catálogo CP-Zonas ────────────────────────────────────────────

    @Test
    void obtenerZonaPorCP_cpExistente_retornaOptionalConZona() {
        // GIVEN
        Instant ahora = Instant.now();
        CatalogoCPZonas entidad = CatalogoCPZonas.builder()
                .id("uuid-cp-001")
                .codigoPostal("28001")
                .zonaCAT("ZONA_A")
                .nivelTecnico("ALTO")
                .fechaCarga(ahora)
                .origen("SIMULACION")
                .build();
        when(catalogoCPZonasRepository.findByCodigoPostal("28001")).thenReturn(Optional.of(entidad));

        // WHEN
        Optional<CatalogoCPZonasResponse> resultado = service.obtenerZonaPorCP("28001");

        // THEN
        assertThat(resultado).isPresent();
        CatalogoCPZonasResponse respuesta = resultado.get();
        assertThat(respuesta.getCodigoPostal()).isEqualTo("28001");
        assertThat(respuesta.getZonaCAT()).isEqualTo("ZONA_A");
        assertThat(respuesta.getNivelTecnico()).isEqualTo("ALTO");
        verify(catalogoCPZonasRepository).findByCodigoPostal("28001");
    }

    @Test
    void obtenerZonaPorCP_cpNoExistente_retornaOptionalVacio() {
        // GIVEN
        when(catalogoCPZonasRepository.findByCodigoPostal("99999")).thenReturn(Optional.empty());

        // WHEN
        Optional<CatalogoCPZonasResponse> resultado = service.obtenerZonaPorCP("99999");

        // THEN — no lanza excepción, retorna Optional.empty()
        assertThat(resultado).isEmpty();
        verify(catalogoCPZonasRepository).findByCodigoPostal("99999");
    }

    // ─── HU-048: Estado global de parámetros ─────────────────────────────────

    @Test
    void obtenerEstado_conDatosDisponibles_retornaStatusCompleto() {
        // GIVEN
        Instant ahora = Instant.now();
        TarifaIncendio incendio = TarifaIncendio.builder()
                .id("uuid-i-001")
                .zonaGeografica("Zona_A")
                .tipoInmueble("Residencial")
                .tarifaBase(new BigDecimal("0.0050"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen("SIMULACION")
                .build();
        TarifaCAT cat = TarifaCAT.builder()
                .id("uuid-c-001")
                .zonaCAT("ZONA_A")
                .factorCAT(new BigDecimal("1.10"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen("SIMULACION")
                .build();
        TarifaFHM fhm = TarifaFHM.builder()
                .id("uuid-f-001")
                .tarifaFHM(new BigDecimal("0.0500"))
                .factorEquipoElectronico(new BigDecimal("1.2500"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen("SIMULACION")
                .build();
        CatalogoCPZonas cp = CatalogoCPZonas.builder()
                .id("uuid-cp-001")
                .codigoPostal("28001")
                .zonaCAT("ZONA_A")
                .nivelTecnico("ALTO")
                .fechaCarga(ahora)
                .origen("SIMULACION")
                .build();

        when(tarifaIncendioRepository.findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class)))
                .thenReturn(List.of(incendio));
        when(tarifaCATRepository.findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(any(LocalDate.class)))
                .thenReturn(List.of(cat));
        when(tarifaFHMRepository.findFirstByOrderByCreatedAtDesc()).thenReturn(Optional.of(fhm));
        when(catalogoCPZonasRepository.count()).thenReturn(1L);
        when(catalogoCPZonasRepository.findAll()).thenReturn(List.of(cp));

        // WHEN
        ParametrosStatusResponse resultado = service.obtenerEstado();

        // THEN
        assertThat(resultado).isNotNull();
        assertThat(resultado.getTarifasIncendio().isDisponible()).isTrue();
        assertThat(resultado.getTarifasIncendio().getCantidad()).isEqualTo(1L);
        assertThat(resultado.getTarifasCAT().isDisponible()).isTrue();
        assertThat(resultado.getTarifasCAT().getCantidad()).isEqualTo(1L);
        assertThat(resultado.getTarifasFHM().isDisponible()).isTrue();
        assertThat(resultado.getCatalogoCPZonas().isDisponible()).isTrue();
        assertThat(resultado.getCircuitBreakerStatus()).isEqualTo("CLOSED");
    }
}
