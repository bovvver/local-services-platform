package com.github.bovvver.infrastructure;

public class CompletionProofRequiredException extends RuntimeException {

    public CompletionProofRequiredException() {
        super("At least one proof URL must be provided.");
    }
}
