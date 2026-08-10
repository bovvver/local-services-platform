package com.github.bovvver.vo;

public record Rating(double value) {
    public Rating {
        if (value < 0.0 || value > 5.0) {
            throw new IllegalArgumentException("Rating must be between 0.0 and 5.0");
        }
    }

    public static Rating of(double value) {
        return new Rating(value);
    }

    public static Rating newAverage(double average) {
        return Rating.of(Math.round(average * 100.0) / 100.0);
    }
}
