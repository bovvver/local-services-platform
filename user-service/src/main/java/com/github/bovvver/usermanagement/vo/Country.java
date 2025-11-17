package com.github.bovvver.usermanagement.vo;

public record Country(String code) {

    public Country {
        if (code != null && !code.matches("^[A-Z]{2}$")) {
            throw new IllegalArgumentException("Invalid ISO alpha-2 country code: " + code);
        }
    }

    public static Country of(String code) {
        if (code == null) return null;
        return new Country(code);
    }
}
