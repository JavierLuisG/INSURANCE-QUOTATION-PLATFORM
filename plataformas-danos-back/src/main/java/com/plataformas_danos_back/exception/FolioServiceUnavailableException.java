package com.plataformas_danos_back.exception;

public class FolioServiceUnavailableException extends RuntimeException {

    public FolioServiceUnavailableException(String message) {
        super(message);
    }

    public FolioServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
