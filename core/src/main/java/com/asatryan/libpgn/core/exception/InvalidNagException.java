package com.asatryan.libpgn.core.exception;

public class InvalidNagException extends PgnException {
    public InvalidNagException(String message) {
        super(message);
    }

    public InvalidNagException(String message, Throwable cause) {
        super(message, cause);
    }
}
