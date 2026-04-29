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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParametroCalculoServiceImpl implements ParametroCalculoService {

    private final TarifaIncendioRepository tarifaIncendioRepository;
    private final TarifaCATRepository tarifaCATRepository;
    private final TarifaFHMRepository tarifaFHMRepository;
    private final CatalogoCPZonasRepository catalogoCPZonasRepository;

    @Override
    @Cacheable(value = "parameters-tarifas-incendio", key = "'all'")
    public List<TarifaIncendioResponse> obtenerTarifasIncendioVigentes() {
        log.debug("Consultando tarifas de incendio vigentes desde repositorio");
        LocalDate hoy = LocalDate.now();
        List<TarifaIncendio> tarifas = tarifaIncendioRepository
                .findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(hoy);
        if (tarifas.isEmpty()) {
            log.warn("No hay tarifas de incendio vigentes disponibles");
            throw new ParametroNoDisponibleException("No hay tarifas de incendio vigentes disponibles");
        }
        return tarifas.stream().map(this::toTarifaIncendioResponse).toList();
    }

    @Override
    @Cacheable(value = "parameters-tarifas-cat", key = "'all'")
    public List<TarifaCATResponse> obtenerTarifasCATVigentes() {
        log.debug("Consultando tarifas CAT vigentes desde repositorio");
        LocalDate hoy = LocalDate.now();
        List<TarifaCAT> tarifas = tarifaCATRepository
                .findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(hoy);
        if (tarifas.isEmpty()) {
            log.warn("No hay tarifas CAT vigentes disponibles");
            throw new ParametroNoDisponibleException("No hay tarifas CAT vigentes disponibles");
        }
        return tarifas.stream().map(this::toTarifaCATResponse).toList();
    }

    @Override
    @Cacheable(value = "parameters-tarifas-fhm", key = "'current'")
    public Optional<TarifaFHMResponse> obtenerTarifaFHMVigente() {
        log.debug("Consultando tarifa FHM vigente desde repositorio");
        Optional<TarifaFHM> tarifaOpt = tarifaFHMRepository.findFirstByOrderByCreatedAtDesc();
        if (tarifaOpt.isEmpty()) {
            log.warn("No hay tarifa FHM disponible");
            throw new ParametroNoDisponibleException("No hay tarifa FHM disponible");
        }
        return tarifaOpt.map(this::toTarifaFHMResponse);
    }

    @Override
    @Cacheable(value = "parameters-cp-zonas", key = "#codigoPostal")
    public Optional<CatalogoCPZonasResponse> obtenerZonaPorCP(String codigoPostal) {
        log.debug("Consultando zona para código postal: {}", codigoPostal);
        return catalogoCPZonasRepository.findByCodigoPostal(codigoPostal)
                .map(this::toCatalogoCPZonasResponse);
    }

    @Override
    public ParametrosStatusResponse obtenerEstado() {
        log.debug("Consultando estado global de parámetros de cálculo");
        LocalDate hoy = LocalDate.now();

        List<TarifaIncendio> incendios = tarifaIncendioRepository
                .findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(hoy);
        List<TarifaCAT> cats = tarifaCATRepository
                .findByFechaVigenciaFinAfterOrFechaVigenciaFinIsNull(hoy);
        Optional<TarifaFHM> fhmOpt = tarifaFHMRepository.findFirstByOrderByCreatedAtDesc();
        long totalCP = catalogoCPZonasRepository.count();

        ParametrosStatusResponse.TarifasStatus statusIncendio = ParametrosStatusResponse.TarifasStatus.builder()
                .disponible(!incendios.isEmpty())
                .ultimaActualizacion(incendios.isEmpty() ? null
                        : incendios.stream()
                                .map(TarifaIncendio::getUpdatedAt)
                                .max(java.time.Instant::compareTo)
                                .orElse(null))
                .cantidad(incendios.size())
                .build();

        ParametrosStatusResponse.TarifasStatus statusCAT = ParametrosStatusResponse.TarifasStatus.builder()
                .disponible(!cats.isEmpty())
                .ultimaActualizacion(cats.isEmpty() ? null
                        : cats.stream()
                                .map(TarifaCAT::getUpdatedAt)
                                .max(java.time.Instant::compareTo)
                                .orElse(null))
                .cantidad(cats.size())
                .build();

        ParametrosStatusResponse.TarifaFHMStatus statusFHM = ParametrosStatusResponse.TarifaFHMStatus.builder()
                .disponible(fhmOpt.isPresent())
                .ultimaActualizacion(fhmOpt.map(TarifaFHM::getUpdatedAt).orElse(null))
                .build();

        ParametrosStatusResponse.TarifasStatus statusCP = ParametrosStatusResponse.TarifasStatus.builder()
                .disponible(totalCP > 0)
                .ultimaActualizacion(totalCP > 0
                        ? catalogoCPZonasRepository.findAll().stream()
                                .map(CatalogoCPZonas::getFechaCarga)
                                .max(java.time.Instant::compareTo)
                                .orElse(null)
                        : null)
                .cantidad(totalCP)
                .build();

        return ParametrosStatusResponse.builder()
                .tarifasIncendio(statusIncendio)
                .tarifasCAT(statusCAT)
                .tarifasFHM(statusFHM)
                .catalogoCPZonas(statusCP)
                .circuitBreakerStatus("CLOSED")
                .build();
    }

    private TarifaIncendioResponse toTarifaIncendioResponse(TarifaIncendio entity) {
        return TarifaIncendioResponse.builder()
                .id(entity.getId())
                .zonaGeografica(entity.getZonaGeografica())
                .tipoInmueble(entity.getTipoInmueble())
                .tarifaBase(entity.getTarifaBase())
                .fechaVigenciaInicio(entity.getFechaVigenciaInicio())
                .fechaVigenciaFin(entity.getFechaVigenciaFin())
                .origen(entity.getOrigen())
                .build();
    }

    private TarifaCATResponse toTarifaCATResponse(TarifaCAT entity) {
        return TarifaCATResponse.builder()
                .id(entity.getId())
                .zonaCAT(entity.getZonaCAT())
                .factorCAT(entity.getFactorCAT())
                .fechaVigenciaInicio(entity.getFechaVigenciaInicio())
                .fechaVigenciaFin(entity.getFechaVigenciaFin())
                .origen(entity.getOrigen())
                .build();
    }

    private TarifaFHMResponse toTarifaFHMResponse(TarifaFHM entity) {
        return TarifaFHMResponse.builder()
                .id(entity.getId())
                .tarifaFHM(entity.getTarifaFHM())
                .factorEquipoElectronico(entity.getFactorEquipoElectronico())
                .fechaVigenciaInicio(entity.getFechaVigenciaInicio())
                .fechaVigenciaFin(entity.getFechaVigenciaFin())
                .origen(entity.getOrigen())
                .build();
    }

    private CatalogoCPZonasResponse toCatalogoCPZonasResponse(CatalogoCPZonas entity) {
        return CatalogoCPZonasResponse.builder()
                .codigoPostal(entity.getCodigoPostal())
                .zonaCAT(entity.getZonaCAT())
                .nivelTecnico(entity.getNivelTecnico())
                .fechaCarga(entity.getFechaCarga())
                .build();
    }
}
