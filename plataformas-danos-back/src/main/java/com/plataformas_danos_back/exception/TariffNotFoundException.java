package com.plataformas_danos_back.exception;

public class TariffNotFoundException extends RuntimeException {

    public TariffNotFoundException(String message) {
        super(message);
    }

    public TariffNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
