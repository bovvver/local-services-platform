package com.github.bovvver.bookingmanagement.vo;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record Salary(BigDecimal value) {

    public Salary {
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        if(value.scale() > 2) {
            throw new IllegalArgumentException("Salary cannot have more than 2 decimal places");
        }
    }

    public static Salary of(final @Min(value = 0) Double salary) {
        return new Salary(BigDecimal.valueOf(salary));
    }
}
