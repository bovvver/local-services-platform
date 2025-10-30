package com.github.bovvver.bookingmanagement.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class SalaryTest {

    @Test
    void shouldCreateSalaryWhenValueIsPositive() {
        Salary salary = Salary.of(1000.00);
        assertThat(salary.value()).isEqualTo(1000.0);
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Salary.of(-500.0));
        assertEquals("Salary cannot be negative", exception.getMessage());
        assertThat(exception.getMessage()).isEqualTo("Salary cannot be negative");
    }

    @Test
    void shouldCreateSalaryUsingFactoryMethodWhenValueIsPositive() {
        Salary salary = Salary.of(2000.0);
        assertThat(salary.value()).isEqualTo(2000.0);
    }

    @Test
    void shouldThrowExceptionUsingFactoryMethodWhenValueIsNegative() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> Salary.of(-1000.0));
        assertThat(exception.getMessage()).isEqualTo("Salary cannot be negative");
    }

    @Test
    void shouldThrowExceptionUsingFactoryMethodWhenValueIsNull() {
        assertThrows(NullPointerException.class, () -> Salary.of(null));
    }
}