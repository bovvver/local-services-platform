package com.github.bovvver.vo;

public record Bio(String value) {

    public Bio {
        if (value != null) {
            if (value.isBlank()) {
                throw new IllegalArgumentException("Bio cannot be blank");
            }
            if (value.length() > 1000) {
                throw new IllegalArgumentException("Bio cannot exceed 1000 characters");
            }
        }
    }

    public static Bio of(String value) {
        if (value == null) return null;
        return new Bio(value);
    }
}
