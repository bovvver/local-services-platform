package com.github.bovvver.offermanagment.vo;

import jakarta.validation.constraints.Min;

import java.math.BigDecimal;

public record Salary(BigDecimal value) {

    public Salary {
        if (value == null) {
            throw new IllegalArgumentException("Salary value cannot be null");
        }
        if (value.signum() < 0) {
            throw new IllegalArgumentException("Salary cannot be negative");
        }
        if (value.scale() > 2) {
            throw new IllegalArgumentException("Salary cannot have more than 2 decimal places");
        }
    }

    public static Salary of(final @Min(value = 0) Double salary) {
        if (salary == null) throw new IllegalArgumentException("Salary cannot be null");
        return new Salary(BigDecimal.valueOf(salary));
    }
}
