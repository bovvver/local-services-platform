package com.github.bovvver.offermanagment.vo;

public record Rating(int value) {

    public Rating {
        if (value < 1 || value > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
    }

    public static Rating of(int value) {
        return new Rating(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
