package com.github.bovvver.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TitleTest {

    @Test
    void shouldCreateTitleWhenValueIsValid() {
        Title title = new Title("Valid Title");
        assertThat(title.toString()).isEqualTo("Valid Title");
    }

    @Test
    void shouldThrowExceptionWhenValueIsNull() {
        assertThatThrownBy(() -> new Title(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenValueIsBlank() {
        assertThatThrownBy(() -> new Title("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be null or blank");
    }

    @Test
    void shouldThrowExceptionWhenValueExceedsMaxLength() {
        String longValue = "a".repeat(101);
        assertThatThrownBy(() -> new Title(longValue))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Title cannot be longer than 100 characters");
    }

    @Test
    void shouldCreateTitleWhenValueIsMaxLength() {
        String maxLengthValue = "a".repeat(100);
        Title title = new Title(maxLengthValue);
        assertThat(title.toString()).isEqualTo(maxLengthValue);
    }
}