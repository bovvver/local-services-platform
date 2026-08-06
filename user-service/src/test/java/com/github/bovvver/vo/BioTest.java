package com.github.bovvver.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class BioTest {

    @Test
    void shouldCreateBioWithValidText() {
        Bio bio = new Bio("Hello, I am a professional plumber.");
        assertThat(bio.value()).isEqualTo("Hello, I am a professional plumber.");
    }

    @Test
    void shouldCreateBioWithNullValueWhenTextIsNull() {
        Bio bio = new Bio(null);
        assertThat(bio.value()).isNull();
    }

    @Test
    void shouldThrowExceptionWhenBioIsBlank() {
        assertThatThrownBy(() -> new Bio("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bio cannot be blank");
    }

    @Test
    void shouldThrowExceptionWhenBioExceedsMaxLength() {
        String longText = "A".repeat(1001);
        assertThatThrownBy(() -> new Bio(longText))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Bio cannot exceed 1000 characters");
    }

    @Test
    void shouldReturnNullWhenUsingFactoryMethodWithNullValue() {
        assertThat(Bio.of(null)).isNull();
    }

    @Test
    void shouldCreateBioUsingFactoryMethodWithValidValue() {
        Bio bio = Bio.of("Some text");
        assertThat(bio.value()).isEqualTo("Some text");
    }
}
