package com.github.bovvver.offermanagment.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DescriptionTest {

    @Test
    void shouldCreateDescriptionWhenValueIsValid() {
        Description description = new Description("Valid description");
        assertThat(description.toString()).isEqualTo("Valid description");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new Description(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Description cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThatThrownBy(() -> new Description("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Description cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longValue = "a".repeat(1001);
        assertThatThrownBy(() -> new Description(longValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Description cannot exceed 1000 characters");
    }

    @Test
    void shouldCreateDescriptionWhenValueIsMaxLength() {
        String maxLengthValue = "a".repeat(1000);
        Description description = new Description(maxLengthValue);
        assertThat(description.toString()).isEqualTo(maxLengthValue);
    }
}
