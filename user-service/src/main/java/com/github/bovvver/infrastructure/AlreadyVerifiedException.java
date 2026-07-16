package com.github.bovvver.infrastructure;

public class AlreadyVerifiedException extends RuntimeException {

    public AlreadyVerifiedException() {
        super("User is already verified.");
    }
}
