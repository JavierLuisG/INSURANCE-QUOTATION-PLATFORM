package com.plataformas_danos_back.client;

/**
 * Cliente HTTP para el Servicio de Folios de Plataforma-core-ohs.
 * Proporciona folios únicos en formato COT-YYYY-NNNNNN.
 */
public interface FoliosClient {

    /**
     * Solicita un nuevo folio único al servicio externo.
     *
     * @return folio en formato COT-YYYY-NNNNNN
     */
    String generarFolio();
}
