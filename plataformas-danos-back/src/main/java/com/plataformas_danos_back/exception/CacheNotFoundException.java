package com.plataformas_danos_back.exception;

public class CacheNotFoundException extends RuntimeException {
    public CacheNotFoundException(String message) {
        super(message);
    }
}
