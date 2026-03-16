package com.github.bovvver.bookingmanagement.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class NegotiationPartyTest {

    @Test
    void shouldContainAllExpectedValues() {
        assertThat(NegotiationParty.values())
                .containsExactly(
                        NegotiationParty.EXECUTOR,
                        NegotiationParty.AUTHOR
                );
    }

    @Test
    void shouldParseFromString() {
        assertThat(NegotiationParty.valueOf("EXECUTOR")).isEqualTo(NegotiationParty.EXECUTOR);
        assertThat(NegotiationParty.valueOf("AUTHOR")).isEqualTo(NegotiationParty.AUTHOR);
    }
}
