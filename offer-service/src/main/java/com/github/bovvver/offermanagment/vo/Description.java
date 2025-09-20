package com.github.bovvver.offermanagment.vo;

public record Description(String value) {

    public Description {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Description cannot be null or blank");
        }
        if (value.length() > 1000) {
            throw new IllegalArgumentException("Description cannot exceed 1000 characters");
        }
    }

    public static Description of(String value) {
        return new Description(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
