package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.CatalogoCPZonasResponse;
import com.plataformas_danos_back.model.dto.ParametrosStatusResponse;
import com.plataformas_danos_back.model.dto.TarifaCATResponse;
import com.plataformas_danos_back.model.dto.TarifaFHMResponse;
import com.plataformas_danos_back.model.dto.TarifaIncendioResponse;

import java.util.List;
import java.util.Optional;

public interface ParametroCalculoService {

    List<TarifaIncendioResponse> obtenerTarifasIncendioVigentes();

    List<TarifaCATResponse> obtenerTarifasCATVigentes();

    Optional<TarifaFHMResponse> obtenerTarifaFHMVigente();

    Optional<CatalogoCPZonasResponse> obtenerZonaPorCP(String codigoPostal);

    ParametrosStatusResponse obtenerEstado();
}
