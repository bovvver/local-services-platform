package com.github.bovvver.usermanagement.vo;

public record City(String value) {

    public City {
        if (value != null) {
            if (value.isBlank()) {
                throw new IllegalArgumentException("City cannot be blank");
            }
            if (!value.matches("^[\\p{L} .'-]{1,100}$")) {
                throw new IllegalArgumentException("Invalid city name: " + value);
            }
        }
    }

    public static City of(String value) {
        if (value == null) return null;
        return new City(value);
    }
}