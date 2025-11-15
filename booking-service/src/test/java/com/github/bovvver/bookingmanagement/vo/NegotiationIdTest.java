package com.github.bovvver.bookingmanagement.vo;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class NegotiationIdTest {

    @Test
    void shouldCreateNegotiationIdWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        NegotiationId negotiationId = new NegotiationId(uuid);

        assertThat(negotiationId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenNegotiationIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new NegotiationId(null));
    }
}
