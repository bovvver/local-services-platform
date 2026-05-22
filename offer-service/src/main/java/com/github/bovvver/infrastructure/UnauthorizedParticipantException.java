package com.github.bovvver.infrastructure;

public class UnauthorizedParticipantException extends RuntimeException {

    public UnauthorizedParticipantException() {
        super("User is not authorized to execute this operation.");
    }
}
