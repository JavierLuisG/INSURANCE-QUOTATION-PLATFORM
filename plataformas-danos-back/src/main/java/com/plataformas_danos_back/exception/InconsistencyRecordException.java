package com.plataformas_danos_back.exception;

public class InconsistencyRecordException extends RuntimeException {

    public InconsistencyRecordException(String message) {
        super(message);
    }

    public InconsistencyRecordException(String message, Throwable cause) {
        super(message, cause);
    }
}
