package com.plataformas_danos_back.exception;

public class InvalidRuleException extends RuntimeException {

    public InvalidRuleException(String message) {
        super(message);
    }

    public InvalidRuleException(String message, Throwable cause) {
        super(message, cause);
    }
}
