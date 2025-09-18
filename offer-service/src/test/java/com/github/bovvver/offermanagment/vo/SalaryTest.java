package com.github.bovvver.offermanagment.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SalaryTest {

    @Test
    void shouldCreateSalaryWhenValueIsPositive() {
        Salary salary = new Salary(5000.0);
        assertThat(salary.value()).isEqualTo(5000.0);
    }

    @Test
    void shouldCreateSalaryWhenValueIsZero() {
        Salary salary = new Salary(0.0);
        assertThat(salary.value()).isEqualTo(0.0);
    }

    @Test
    void shouldThrowExceptionWhenValueIsNegative() {
        assertThatThrownBy(() -> new Salary(-1000.0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Salary cannot be negative");
    }
}
