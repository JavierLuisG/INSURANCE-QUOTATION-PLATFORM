package com.plataformas_danos_back.client;

/**
 * Cliente HTTP para validar IDs de catálogos del cotizador contra Plataforma-core-ohs.
 * Soporta: tipoSeguro, moneda y canalVenta.
 */
public interface CotizadorCatalogosClient {

    /**
     * Verifica si un ID existe en el catálogo especificado.
     *
     * @param tipoCatalogo tipo de catálogo: "tipoSeguro", "moneda", "canalVenta"
     * @param id           ID a validar
     * @return true si existe y está activo en el catálogo
     */
    boolean existsInCatalog(String tipoCatalogo, String id);
}
