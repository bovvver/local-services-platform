package com.github.bovvver.offermanagment.vo;

public record Title(String value) {

    public Title {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank");
        }
        if (value.length() > 100) {
            throw new IllegalArgumentException("Title cannot be longer than 100 characters");
        }
    }

    public static Title of(String value) {
        return new Title(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
