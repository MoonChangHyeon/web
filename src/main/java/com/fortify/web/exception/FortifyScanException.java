package com.fortify.web.exception;

public class FortifyScanException extends RuntimeException {
    public FortifyScanException(String message) {
        super(message);
    }

    public FortifyScanException(String message, Throwable cause) {
        super(message, cause);
    }
}
