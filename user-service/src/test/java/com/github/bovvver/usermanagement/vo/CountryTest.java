package com.github.bovvver.usermanagement.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CountryTest {

    @Test
    void shouldCreateCountryWithValidCode() {
        Country country = new Country("PL");
        assertThat(country.code()).isEqualTo("PL");
    }

    @Test
    void shouldCreateCountryWithNullValueWhenCountryNameIsNull() {
        Country country = new Country(null);
        assertThat(country.code()).isEqualTo(null);
    }

    @Test
    void shouldThrowExceptionWhenCodeIsTooShort() {
        assertThatThrownBy(() -> new Country("P"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ISO alpha-2 country code: P");
    }

    @Test
    void shouldThrowExceptionWhenCodeIsTooLong() {
        assertThatThrownBy(() -> new Country("POL"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ISO alpha-2 country code: POL");
    }

    @Test
    void shouldThrowExceptionWhenCodeContainsLowercaseLetters() {
        assertThatThrownBy(() -> new Country("pl"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ISO alpha-2 country code: pl");
    }

    @Test
    void shouldThrowExceptionWhenCodeContainsNonAlphabeticCharacters() {
        assertThatThrownBy(() -> new Country("P1"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid ISO alpha-2 country code: P1");
    }

    @Test
    void shouldReturnNullWhenUsingFactoryMethodWithNullCode() {
        assertThat(Country.of(null)).isNull();
    }

    @Test
    void shouldCreateCountryUsingFactoryMethodWithValidCode() {
        Country country = Country.of("US");
        assertThat(country.code()).isEqualTo("US");
    }
}