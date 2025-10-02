package com.github.bovvver.bookingmanagement.vo;

import jakarta.validation.constraints.Min;

public record Salary(double value) {

    public Salary {
        if (value < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
    }

    public static Salary of(final @Min(value = 0) Double salary) {
        return new Salary(salary);
    }
}
