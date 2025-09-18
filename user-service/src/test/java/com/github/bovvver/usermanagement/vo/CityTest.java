package com.github.bovvver.usermanagement.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CityTest {

    @Test
    void shouldCreateCityWithValidName() {
        City city = new City("Warsaw");
        assertThat(city.value()).isEqualTo("Warsaw");
    }

    @Test
    void shouldThrowExceptionWhenCityNameIsNull() {
        assertThatThrownBy(() -> new City(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("City cannot be blank");
    }

    @Test
    void shouldThrowExceptionWhenCityNameIsBlank() {
        assertThatThrownBy(() -> new City("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("City cannot be blank");
    }

    @Test
    void shouldThrowExceptionWhenCityNameExceedsMaxLength() {
        String longName = "A".repeat(101);
        assertThatThrownBy(() -> new City(longName))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid city name: " + longName);
    }

    @Test
    void shouldThrowExceptionWhenCityNameContainsInvalidCharacters() {
        assertThatThrownBy(() -> new City("City@123"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Invalid city name: City@123");
    }

    @Test
    void shouldAllowCityNameWithSpecialCharacters() {
        City city = new City("Saint-Pierre");
        assertThat(city.value()).isEqualTo("Saint-Pierre");
    }

    @Test
    void shouldAllowCityNameWithSpaces() {
        City city = new City("New York");
        assertThat(city.value()).isEqualTo("New York");
    }

    @Test
    void shouldAllowCityNameWithCountryCharacters() {
        City city = new City("Łódź");
        assertThat(city.value()).isEqualTo("Łódź");

        City city2 = new City("München");
        assertThat(city2.value()).isEqualTo("München");
    }
}