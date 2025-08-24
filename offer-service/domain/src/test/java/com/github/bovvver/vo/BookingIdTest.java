package com.github.bovvver.vo;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingIdTest {

    @Test
    void shouldCreateBookingIdWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        BookingId bookingId = new BookingId(uuid);

        assertThat(bookingId).isNotNull();
        assertThat(bookingId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenBookingIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new BookingId(null));
    }

    @Test
    void shouldBeEqualWhenSameUUID() {
        UUID uuid = UUID.randomUUID();
        BookingId bookingId1 = new BookingId(uuid);
        BookingId bookingId2 = new BookingId(uuid);

        assertThat(bookingId1).isEqualTo(bookingId2);
        assertThat(bookingId1.hashCode()).isEqualTo(bookingId2.hashCode());
    }
}
