package com.github.bovvver.vo;

import java.util.UUID;

public record ProviderProfileId(UUID value) {

    public ProviderProfileId {
        if (value == null) {
            throw new IllegalArgumentException("ProviderProfileId cannot be null");
        }
    }

    public static ProviderProfileId of(UUID value) {
        return new ProviderProfileId(value);
    }

    public static ProviderProfileId generate() {
        return new ProviderProfileId(UUID.randomUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
