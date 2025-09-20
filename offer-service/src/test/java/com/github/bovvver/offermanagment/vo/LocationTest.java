package com.github.bovvver.offermanagment.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LocationTest {

    @Test
    void shouldCreateLocationWithValidCoordinates() {
        Location location = new Location(52.2297, 21.0122);

        assertThat(location.latitude()).isEqualTo(52.2297);
        assertThat(location.longitude()).isEqualTo(21.0122);
    }

    @Test
    void shouldThrowExceptionWhenLatitudeIsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Location(91.0, 21.0122));
        assertThrows(IllegalArgumentException.class, () -> new Location(-91.0, 21.0122));
    }

    @Test
    void shouldThrowExceptionWhenLongitudeIsOutOfRange() {
        assertThrows(IllegalArgumentException.class, () -> new Location(52.2297, 181.0));
        assertThrows(IllegalArgumentException.class, () -> new Location(52.2297, -181.0));
    }
}
