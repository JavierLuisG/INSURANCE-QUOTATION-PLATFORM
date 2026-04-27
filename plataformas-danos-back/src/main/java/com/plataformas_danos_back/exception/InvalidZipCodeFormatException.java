package com.plataformas_danos_back.exception;

public class InvalidZipCodeFormatException extends RuntimeException {

    public InvalidZipCodeFormatException(String message) {
        super(message);
    }
}
