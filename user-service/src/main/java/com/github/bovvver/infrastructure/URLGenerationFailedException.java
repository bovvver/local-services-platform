package com.github.bovvver.infrastructure;

public class URLGenerationFailedException extends RuntimeException {

    public URLGenerationFailedException() {
        super("Failed to generate presigned URL.");
    }
}
