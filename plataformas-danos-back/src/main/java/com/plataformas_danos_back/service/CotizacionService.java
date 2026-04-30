package com.plataformas_danos_back.service;

import com.plataformas_danos_back.model.dto.CotizacionRequest;
import com.plataformas_danos_back.model.dto.CotizacionResponse;

public interface CotizacionService {

    /**
     * Inicia una nueva cotización obteniendo un folio único del Servicio de Folios
     * (Plataforma-core-ohs). La cotización se crea con estadoValidacion=INCOMPLETA.
     *
     * @return cotización recién creada con folio asignado
     */
    CotizacionResponse iniciar();

    /**
     * Obtiene los datos de una cotización por su folio de negocio.
     *
     * @param folio folio en formato COT-YYYY-NNNNNN
     * @return cotización encontrada
     */
    CotizacionResponse obtenerPorFolio(String folio);

    /**
     * Actualiza los datos generales de una cotización existente.
     * Aplica validación de RFC, rango de fechas, IDs de catálogos y versionado optimista.
     *
     * @param folio   folio en formato COT-YYYY-NNNNNN
     * @param request datos a actualizar (todos opcionales excepto version)
     * @return cotización actualizada
     */
    CotizacionResponse actualizar(String folio, CotizacionRequest request);
}
