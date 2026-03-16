package com.github.bovvver.bookingmanagement.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationStatusTest {

    @Test
    void shouldContainAllExpectedValues() {
        assertThat(NegotiationStatus.values())
                .containsExactly(
                        NegotiationStatus.ACTIVE,
                        NegotiationStatus.ACCEPTED,
                        NegotiationStatus.REJECTED,
                        NegotiationStatus.EXPIRED
                );
    }

    @Test
    void shouldParseFromString() {
        assertThat(NegotiationStatus.valueOf("ACTIVE")).isEqualTo(NegotiationStatus.ACTIVE);
        assertThat(NegotiationStatus.valueOf("REJECTED")).isEqualTo(NegotiationStatus.REJECTED);
    }
}
