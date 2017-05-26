package com.asatryan.libpgn.core.exception;

public class PgnException extends RuntimeException {
    public PgnException(String message) {
        super(message);
    }

    public PgnException(String message, Throwable cause) {
        super(message, cause);
    }
}
