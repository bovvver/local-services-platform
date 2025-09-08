package com.github.bovvver.vo;

public record City(String value) {

    public City {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("City cannot be blank");
        }
        if (!value.matches("^[\\p{L} .'-]{1,100}$")) {
            throw new IllegalArgumentException("Invalid city name: " + value);
        }
    }
}