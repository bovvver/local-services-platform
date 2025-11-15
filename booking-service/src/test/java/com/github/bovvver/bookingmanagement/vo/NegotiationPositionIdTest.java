package com.github.bovvver.bookingmanagement.vo;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NegotiationPositionIdTest {

    @Test
    void shouldCreateNegotiationPositionIdWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        NegotiationPositionId negotiationPositionId = new NegotiationPositionId(uuid);

        assertThat(negotiationPositionId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenNegotiationPositionIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new NegotiationPositionId(null));
    }
}
