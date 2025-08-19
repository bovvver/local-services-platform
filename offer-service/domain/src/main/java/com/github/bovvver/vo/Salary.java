package com.github.bovvver.vo;

public record Salary(double value) {

    public Salary {
        if (value < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }
}
