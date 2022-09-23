package com.joje.untitled.exception;

public class ExpiredTokenException extends RuntimeException {
    public ExpiredTokenException() {}
    public ExpiredTokenException(String message) {
        super(message);
    }
    public ExpiredTokenException(String message, Throwable cause) {
        super(message, cause);
    }
}
