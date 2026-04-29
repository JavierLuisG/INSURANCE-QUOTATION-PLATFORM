package com.plataformas_danos_back.service;

import com.plataformas_danos_back.exception.IngestEnProgresoException;
import com.plataformas_danos_back.model.dto.CargarTarifasRequest;
import com.plataformas_danos_back.model.dto.IngestStatusResponse;
import com.plataformas_danos_back.model.dto.TariffCatDto;
import com.plataformas_danos_back.model.dto.TariffElectronicEquipmentDto;
import com.plataformas_danos_back.model.dto.TariffFireDto;
import com.plataformas_danos_back.model.dto.ZipCodeDto;
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
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Service
@RequiredArgsConstructor
public class IngestorParametrosServiceImpl implements IngestorParametrosService {

    private static final String ORIGEN_SIMULACION = "SIMULACION";
    private static final String ORIGEN_PLATAFORMA = "Plataforma-core-ohs";
    private static final BigDecimal DEFAULT_FACTOR = BigDecimal.valueOf(0.001);

    private final TarifaIncendioRepository tarifaIncendioRepository;
    private final TarifaCATRepository tarifaCATRepository;
    private final TarifaFHMRepository tarifaFHMRepository;
    private final CatalogoCPZonasRepository catalogoCPZonasRepository;
    private final TariffsService tariffsService;
    private final ZipCodeService zipCodeService;

    private final AtomicBoolean ingestIncendioEnProgreso = new AtomicBoolean(false);
    private final AtomicBoolean ingestCATEnProgreso = new AtomicBoolean(false);
    private final AtomicBoolean ingestFHMEnProgreso = new AtomicBoolean(false);
    private final AtomicBoolean ingestCPZonasEnProgreso = new AtomicBoolean(false);

    @Override
    @CacheEvict(value = "parameters-tarifas-incendio", allEntries = true)
    public IngestStatusResponse ingestarTarifasIncendio(CargarTarifasRequest request) {
        if (!ingestIncendioEnProgreso.compareAndSet(false, true)) {
            throw new IngestEnProgresoException("Ya hay una ingestión de tarifas de incendio en progreso");
        }
        String requestId = UUID.randomUUID().toString();
        try {
            String origen = resolverOrigen(request);
            log.info("Iniciando ingestión de tarifas de incendio. requestId={}, origen={}", requestId, origen);

            List<TarifaIncendio> entidades;
            if (ORIGEN_SIMULACION.equalsIgnoreCase(origen)) {
                entidades = crearTarifasIncendioSimuladas();
            } else {
                entidades = obtenerTarifasIncendioDesdeServicio();
            }

            tarifaIncendioRepository.saveAll(entidades);
            log.info("Ingestión de tarifas de incendio completada. requestId={}, cantidad={}", requestId, entidades.size());

            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("COMPLETADA")
                    .mensaje("Carga de tarifas de incendio completada. Registros procesados: " + entidades.size())
                    .timestamp(Instant.now())
                    .build();
        } catch (IngestEnProgresoException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("CRITICAL: Error durante ingestión de tarifas de incendio. requestId={}, error={}", requestId, ex.getMessage(), ex);
            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("FALLIDA")
                    .mensaje("Error durante la ingestión de tarifas de incendio: " + ex.getMessage())
                    .timestamp(Instant.now())
                    .build();
        } finally {
            ingestIncendioEnProgreso.set(false);
        }
    }

    @Override
    @CacheEvict(value = "parameters-tarifas-cat", allEntries = true)
    public IngestStatusResponse ingestarTarifasCAT(CargarTarifasRequest request) {
        if (!ingestCATEnProgreso.compareAndSet(false, true)) {
            throw new IngestEnProgresoException("Ya hay una ingestión de tarifas CAT en progreso");
        }
        String requestId = UUID.randomUUID().toString();
        try {
            String origen = resolverOrigen(request);
            log.info("Iniciando ingestión de tarifas CAT. requestId={}, origen={}", requestId, origen);

            List<TarifaCAT> entidades;
            if (ORIGEN_SIMULACION.equalsIgnoreCase(origen)) {
                entidades = crearTarifasCATSimuladas();
            } else {
                entidades = obtenerTarifasCATDesdeServicio();
            }

            tarifaCATRepository.saveAll(entidades);
            log.info("Ingestión de tarifas CAT completada. requestId={}, cantidad={}", requestId, entidades.size());

            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("COMPLETADA")
                    .mensaje("Carga de tarifas CAT completada. Registros procesados: " + entidades.size())
                    .timestamp(Instant.now())
                    .build();
        } catch (IngestEnProgresoException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("CRITICAL: Error durante ingestión de tarifas CAT. requestId={}, error={}", requestId, ex.getMessage(), ex);
            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("FALLIDA")
                    .mensaje("Error durante la ingestión de tarifas CAT: " + ex.getMessage())
                    .timestamp(Instant.now())
                    .build();
        } finally {
            ingestCATEnProgreso.set(false);
        }
    }

    @Override
    @CacheEvict(value = "parameters-tarifas-fhm", allEntries = true)
    public IngestStatusResponse ingestarTarifasFHM(CargarTarifasRequest request) {
        if (!ingestFHMEnProgreso.compareAndSet(false, true)) {
            throw new IngestEnProgresoException("Ya hay una ingestión de tarifas FHM en progreso");
        }
        String requestId = UUID.randomUUID().toString();
        try {
            String origen = resolverOrigen(request);
            log.info("Iniciando ingestión de tarifa FHM. requestId={}, origen={}", requestId, origen);

            TarifaFHM entidad;
            if (ORIGEN_SIMULACION.equalsIgnoreCase(origen)) {
                entidad = crearTarifaFHMSimulada();
            } else {
                entidad = obtenerTarifaFHMDesdeServicio();
            }

            tarifaFHMRepository.save(entidad);
            log.info("Ingestión de tarifa FHM completada. requestId={}", requestId);

            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("COMPLETADA")
                    .mensaje("Carga de tarifa FHM completada exitosamente")
                    .timestamp(Instant.now())
                    .build();
        } catch (IngestEnProgresoException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("CRITICAL: Error durante ingestión de tarifa FHM. requestId={}, error={}", requestId, ex.getMessage(), ex);
            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("FALLIDA")
                    .mensaje("Error durante la ingestión de tarifa FHM: " + ex.getMessage())
                    .timestamp(Instant.now())
                    .build();
        } finally {
            ingestFHMEnProgreso.set(false);
        }
    }

    @Override
    @CacheEvict(value = "parameters-cp-zonas", allEntries = true)
    public IngestStatusResponse ingestarCatalogoCPZonas(CargarTarifasRequest request) {
        if (!ingestCPZonasEnProgreso.compareAndSet(false, true)) {
            throw new IngestEnProgresoException("Ya hay una ingestión del catálogo CP-Zonas en progreso");
        }
        String requestId = UUID.randomUUID().toString();
        try {
            String origen = resolverOrigen(request);
            log.info("Iniciando ingestión del catálogo CP-Zonas. requestId={}, origen={}", requestId, origen);

            List<CatalogoCPZonas> entidades;
            if (ORIGEN_SIMULACION.equalsIgnoreCase(origen)) {
                entidades = crearCatalogoCPZonasSimulado();
            } else {
                entidades = obtenerCatalogoCPZonasDesdeServicio();
            }

            catalogoCPZonasRepository.saveAll(entidades);
            log.info("Ingestión del catálogo CP-Zonas completada. requestId={}, cantidad={}", requestId, entidades.size());

            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("COMPLETADA")
                    .mensaje("Carga del catálogo CP-Zonas completada. Registros procesados: " + entidades.size())
                    .timestamp(Instant.now())
                    .build();
        } catch (IngestEnProgresoException ex) {
            throw ex;
        } catch (Exception ex) {
            log.error("CRITICAL: Error durante ingestión del catálogo CP-Zonas. requestId={}, error={}", requestId, ex.getMessage(), ex);
            return IngestStatusResponse.builder()
                    .requestId(requestId)
                    .status("FALLIDA")
                    .mensaje("Error durante la ingestión del catálogo CP-Zonas: " + ex.getMessage())
                    .timestamp(Instant.now())
                    .build();
        } finally {
            ingestCPZonasEnProgreso.set(false);
        }
    }

    // ─── Helpers — resolución de origen ───────────────────────────────────────

    private String resolverOrigen(CargarTarifasRequest request) {
        if (request != null && request.getOrigenForzado() != null && !request.getOrigenForzado().isBlank()) {
            return request.getOrigenForzado().trim().toUpperCase();
        }
        return ORIGEN_PLATAFORMA;
    }

    // ─── Obtención desde servicio externo ─────────────────────────────────────

    private List<TarifaIncendio> obtenerTarifasIncendioDesdeServicio() {
        List<TariffFireDto> dtos = tariffsService.getTariffsFire();
        if (dtos == null || dtos.isEmpty()) {
            log.warn("El servicio externo no retornó tarifas de incendio");
            return List.of();
        }
        Instant ahora = Instant.now();
        LocalDate hoy = LocalDate.now();
        List<TarifaIncendio> resultado = new ArrayList<>();

        for (TariffFireDto dto : dtos) {
            BigDecimal tarifaBase = dto.getTasaBase() != null ? BigDecimal.valueOf(dto.getTasaBase()) : null;
            if (tarifaBase == null || tarifaBase.compareTo(BigDecimal.ZERO) <= 0) {
                log.warn("TariffFire descartada por tarifaBase inválida: zonaRiesgo={}", dto.getZonaRiesgo());
                continue;
            }
            resultado.add(TarifaIncendio.builder()
                    .id(UUID.randomUUID().toString())
                    .zonaGeografica(dto.getZonaRiesgo())
                    .tipoInmueble(dto.getTipoConstructivo())
                    .tarifaBase(tarifaBase)
                    .fechaVigenciaInicio(hoy)
                    .fechaVigenciaFin(hoy.plusYears(1))
                    .createdAt(ahora)
                    .updatedAt(ahora)
                    .origen(ORIGEN_PLATAFORMA)
                    .build());
        }
        return resultado;
    }

    private List<TarifaCAT> obtenerTarifasCATDesdeServicio() {
        List<String> zonas = List.of("ZONA_A", "ZONA_B", "ZONA_C", "ZONA_D");
        Instant ahora = Instant.now();
        LocalDate hoy = LocalDate.now();
        List<TarifaCAT> resultado = new ArrayList<>();

        for (String zona : zonas) {
            try {
                TariffCatDto dto = tariffsService.getTariffCat(zona);
                if (dto == null) continue;

                boolean zonaExiste = !catalogoCPZonasRepository.findByZonaCAT(zona).isEmpty();
                if (!zonaExiste) {
                    log.warn("TariffCAT descartada: zonaCAT='{}' no existe en catalogo_cp_zonas", zona);
                    continue;
                }

                BigDecimal factorCAT = dto.getFactorTEV() != null ? BigDecimal.valueOf(dto.getFactorTEV()) : null;
                if (factorCAT == null || factorCAT.compareTo(BigDecimal.ZERO) <= 0) {
                    log.warn("TariffCAT descartada por factorCAT inválido: zona={}", zona);
                    continue;
                }

                resultado.add(TarifaCAT.builder()
                        .id(UUID.randomUUID().toString())
                        .zonaCAT(zona)
                        .factorCAT(factorCAT)
                        .fechaVigenciaInicio(hoy)
                        .fechaVigenciaFin(hoy.plusYears(1))
                        .createdAt(ahora)
                        .updatedAt(ahora)
                        .origen(ORIGEN_PLATAFORMA)
                        .build());
            } catch (Exception ex) {
                log.warn("Error obteniendo tarifa CAT para zona='{}': {}", zona, ex.getMessage());
            }
        }
        return resultado;
    }

    private TarifaFHM obtenerTarifaFHMDesdeServicio() {
        List<TariffElectronicEquipmentDto> dtos = tariffsService.getTariffsElectronicEquipment();
        Instant ahora = Instant.now();
        LocalDate hoy = LocalDate.now();

        BigDecimal factorEquipo = DEFAULT_FACTOR;
        if (dtos != null && !dtos.isEmpty()) {
            TariffElectronicEquipmentDto primero = dtos.get(0);
            if (primero.getFactor() != null && primero.getFactor() > 0) {
                factorEquipo = BigDecimal.valueOf(primero.getFactor());
            } else {
                log.warn("Factor de equipo electrónico nulo o inválido; usando valor por defecto={}", DEFAULT_FACTOR);
            }
        }

        return TarifaFHM.builder()
                .id(UUID.randomUUID().toString())
                .tarifaFHM(BigDecimal.valueOf(0.05))
                .factorEquipoElectronico(factorEquipo)
                .fechaVigenciaInicio(hoy)
                .fechaVigenciaFin(hoy.plusYears(1))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen(ORIGEN_PLATAFORMA)
                .build();
    }

    private List<CatalogoCPZonas> obtenerCatalogoCPZonasDesdeServicio() {
        List<String> codigosPostales = List.of("28001", "28002", "28003", "06600", "64000", "44100", "45010");
        Instant ahora = Instant.now();
        List<CatalogoCPZonas> resultado = new ArrayList<>();

        for (String cp : codigosPostales) {
            try {
                ZipCodeDto dto = zipCodeService.getByZipCode(cp);
                if (dto == null) continue;

                if (!cp.matches("\\d{5}")) {
                    log.warn("Código postal mal formado descartado: '{}'", cp);
                    continue;
                }

                resultado.add(CatalogoCPZonas.builder()
                        .id(UUID.randomUUID().toString())
                        .codigoPostal(dto.getCodigoPostal() != null ? dto.getCodigoPostal() : cp)
                        .zonaCAT(dto.getZonaCAT())
                        .nivelTecnico(dto.getNivelTecnico())
                        .fechaCarga(ahora)
                        .origen(ORIGEN_PLATAFORMA)
                        .build());
            } catch (Exception ex) {
                log.warn("Error obteniendo datos de CP='{}' desde servicio externo: {}", cp, ex.getMessage());
            }
        }
        return resultado;
    }

    // ─── Datos simulados ──────────────────────────────────────────────────────

    private List<TarifaIncendio> crearTarifasIncendioSimuladas() {
        Instant ahora = Instant.now();
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 12, 31);

        return List.of(
                buildTarifaIncendio("Zona_A", "Residencial Urbano", "0.0050", inicio, fin, ahora),
                buildTarifaIncendio("Zona_A", "Residencial Rural", "0.0065", inicio, fin, ahora),
                buildTarifaIncendio("Zona_B", "Residencial Urbano", "0.0045", inicio, fin, ahora),
                buildTarifaIncendio("Zona_B", "Comercial", "0.0080", inicio, fin, ahora),
                buildTarifaIncendio("Zona_C", "Residencial Urbano", "0.0055", inicio, fin, ahora),
                buildTarifaIncendio("Zona_C", "Industrial", "0.0120", inicio, fin, ahora)
        );
    }

    private TarifaIncendio buildTarifaIncendio(String zona, String tipo, String tarifa,
                                                LocalDate inicio, LocalDate fin, Instant ahora) {
        return TarifaIncendio.builder()
                .id(UUID.randomUUID().toString())
                .zonaGeografica(zona)
                .tipoInmueble(tipo)
                .tarifaBase(new BigDecimal(tarifa))
                .fechaVigenciaInicio(inicio)
                .fechaVigenciaFin(fin)
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen(ORIGEN_SIMULACION)
                .build();
    }

    private List<TarifaCAT> crearTarifasCATSimuladas() {
        Instant ahora = Instant.now();
        LocalDate inicio = LocalDate.of(2026, 1, 1);
        LocalDate fin = LocalDate.of(2026, 12, 31);

        return List.of(
                buildTarifaCAT("ZONA_A", "1.10", inicio, fin, ahora),
                buildTarifaCAT("ZONA_B", "1.25", inicio, fin, ahora),
                buildTarifaCAT("ZONA_C", "1.40", inicio, fin, ahora),
                buildTarifaCAT("ZONA_D", "1.60", inicio, fin, ahora)
        );
    }

    private TarifaCAT buildTarifaCAT(String zona, String factor,
                                      LocalDate inicio, LocalDate fin, Instant ahora) {
        return TarifaCAT.builder()
                .id(UUID.randomUUID().toString())
                .zonaCAT(zona)
                .factorCAT(new BigDecimal(factor))
                .fechaVigenciaInicio(inicio)
                .fechaVigenciaFin(fin)
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen(ORIGEN_SIMULACION)
                .build();
    }

    private TarifaFHM crearTarifaFHMSimulada() {
        Instant ahora = Instant.now();
        return TarifaFHM.builder()
                .id(UUID.randomUUID().toString())
                .tarifaFHM(new BigDecimal("0.0500"))
                .factorEquipoElectronico(new BigDecimal("1.2500"))
                .fechaVigenciaInicio(LocalDate.of(2026, 1, 1))
                .fechaVigenciaFin(LocalDate.of(2026, 12, 31))
                .createdAt(ahora)
                .updatedAt(ahora)
                .origen(ORIGEN_SIMULACION)
                .build();
    }

    private List<CatalogoCPZonas> crearCatalogoCPZonasSimulado() {
        Instant ahora = Instant.now();
        return List.of(
                buildCatalogoCPZona("28001", "ZONA_A", "ALTO", ahora),
                buildCatalogoCPZona("28002", "ZONA_A", "ALTO", ahora),
                buildCatalogoCPZona("28003", "ZONA_B", "MEDIO", ahora),
                buildCatalogoCPZona("06600", "ZONA_B", "MEDIO", ahora),
                buildCatalogoCPZona("64000", "ZONA_C", "BAJO", ahora),
                buildCatalogoCPZona("44100", "ZONA_C", "BAJO", ahora),
                buildCatalogoCPZona("45010", "ZONA_D", "BAJO", ahora)
        );
    }

    private CatalogoCPZonas buildCatalogoCPZona(String cp, String zona, String nivel, Instant ahora) {
        return CatalogoCPZonas.builder()
                .id(UUID.randomUUID().toString())
                .codigoPostal(cp)
                .zonaCAT(zona)
                .nivelTecnico(nivel)
                .fechaCarga(ahora)
                .origen(ORIGEN_SIMULACION)
                .build();
    }
}
