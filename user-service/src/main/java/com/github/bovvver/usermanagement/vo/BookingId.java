package com.github.bovvver.usermanagement.vo;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record BookingId(UUID value) {

    public BookingId {
        if (value == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }
    }

    public static List<BookingId> fromAll(Collection<UUID> ids) {
        Objects.requireNonNull(ids, "Ids cannot be null");
        return ids.stream()
                .map(BookingId::new)
                .toList();
    }
}