package com.github.bovvver.bookingmanagement.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class BookingStatusTest {

    @Test
    void shouldContainAllExpectedValues() {
        assertThat(BookingStatus.values())
                .containsExactly(
                        BookingStatus.PENDING,
                        BookingStatus.IN_NEGOTIATION,
                        BookingStatus.ACCEPTED,
                        BookingStatus.REJECTED,
                        BookingStatus.CANCELED_BY_EXECUTOR,
                        BookingStatus.CANCELED_BY_AUTHOR,
                        BookingStatus.EXPIRED
                );
    }

    @Test
    void shouldParseFromString() {
        assertThat(BookingStatus.valueOf("PENDING")).isEqualTo(BookingStatus.PENDING);
        assertThat(BookingStatus.valueOf("ACCEPTED")).isEqualTo(BookingStatus.ACCEPTED);
    }
}
