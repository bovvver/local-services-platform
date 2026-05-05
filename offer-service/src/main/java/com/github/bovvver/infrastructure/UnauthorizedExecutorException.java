package com.github.bovvver.infrastructure;

public class UnauthorizedExecutorException extends RuntimeException {

    public UnauthorizedExecutorException() {
        super("User is not authorized to execute this offer.");
    }
}
