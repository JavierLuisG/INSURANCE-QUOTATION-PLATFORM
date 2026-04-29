package com.plataformas_danos_back.exception;

public class ZipCodeServiceUnavailableException extends RuntimeException {

    public ZipCodeServiceUnavailableException(String message) {
        super(message);
    }

    public ZipCodeServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
