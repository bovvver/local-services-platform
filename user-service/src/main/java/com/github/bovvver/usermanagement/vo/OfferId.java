package com.github.bovvver.usermanagement.vo;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record OfferId(UUID value) {

    public OfferId {
        if (value == null) {
            throw new IllegalArgumentException("OfferId cannot be null");
        }
    }

    public static List<OfferId> fromAll(Collection<UUID> ids) {
        Objects.requireNonNull(ids, "Ids cannot be null");
        return ids.stream()
                .map(OfferId::new)
                .toList();
    }
}