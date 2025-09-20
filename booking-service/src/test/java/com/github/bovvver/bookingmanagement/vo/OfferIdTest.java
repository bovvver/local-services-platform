package com.github.bovvver.bookingmanagement.vo;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfferIdTest {

    @Test
    void shouldCreateOfferIdWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        OfferId offerId = new OfferId(uuid);

        assertThat(offerId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenOfferIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new OfferId(null));
    }
}
