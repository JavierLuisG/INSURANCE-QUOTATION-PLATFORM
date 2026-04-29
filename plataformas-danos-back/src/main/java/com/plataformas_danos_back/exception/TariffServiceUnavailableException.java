package com.plataformas_danos_back.exception;

public class TariffServiceUnavailableException extends RuntimeException {

    public TariffServiceUnavailableException(String message) {
        super(message);
    }

    public TariffServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
