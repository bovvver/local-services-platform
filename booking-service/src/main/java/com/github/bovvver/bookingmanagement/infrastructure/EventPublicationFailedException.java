package com.github.bovvver.bookingmanagement.infrastructure;

public class EventPublicationFailedException extends RuntimeException {

    public EventPublicationFailedException(String eventType, Throwable cause) {
        super("Failed to publish event: " + eventType, cause);
    }
}
