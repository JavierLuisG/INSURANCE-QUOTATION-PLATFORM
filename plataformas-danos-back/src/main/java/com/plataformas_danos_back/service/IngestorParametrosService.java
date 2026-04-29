package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.CargarTarifasRequest;
import com.plataformas_danos_back.model.dto.IngestStatusResponse;

public interface IngestorParametrosService {

    IngestStatusResponse ingestarTarifasIncendio(CargarTarifasRequest request);

    IngestStatusResponse ingestarTarifasCAT(CargarTarifasRequest request);

    IngestStatusResponse ingestarTarifasFHM(CargarTarifasRequest request);

    IngestStatusResponse ingestarCatalogoCPZonas(CargarTarifasRequest request);
}
