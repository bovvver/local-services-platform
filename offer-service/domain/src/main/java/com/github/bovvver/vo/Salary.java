package com.github.bovvver.vo;

public record Salary(double value) {

    public Salary {
        if (value < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }

    public static Salary of(double value) {
        return new Salary(value);
    }
}
