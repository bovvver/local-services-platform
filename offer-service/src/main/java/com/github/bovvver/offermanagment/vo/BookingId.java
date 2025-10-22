package com.github.bovvver.offermanagment.vo;

import java.util.*;
import java.util.stream.Collectors;

public record BookingId(UUID value) {

    public BookingId {
        if (value == null) {
            throw new IllegalArgumentException("BookingId cannot be null");
        }
    }

    public static BookingId of(UUID value) {
        return new BookingId(value);
    }

    public static Set<BookingId> fromAll(Collection<UUID> ids) {
        Objects.requireNonNull(ids, "Ids cannot be null");
        return ids.stream()
                .map(BookingId::new)
                .collect(Collectors.toSet());
    }
}
