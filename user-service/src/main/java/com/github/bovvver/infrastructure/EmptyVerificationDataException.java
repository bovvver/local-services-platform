package com.github.bovvver.infrastructure;

public class EmptyVerificationDataException extends RuntimeException {
    public EmptyVerificationDataException() {
        super("Verification proof cannot be null or empty.");
    }
}
