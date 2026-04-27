package com.plataformas_danos_back.exception;

public class ZipCodeNotFoundException extends RuntimeException {

    public ZipCodeNotFoundException(String message) {
        super(message);
    }

    public ZipCodeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
