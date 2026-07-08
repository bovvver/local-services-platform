package com.github.bovvver.vo;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UserIdTest {

    @Test
    void shouldCreateUserIdWithValidUUID() {
        UUID uuid = UUID.randomUUID();
        UserId userId = new UserId(uuid);

        assertThat(userId.value()).isEqualTo(uuid);
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new UserId(null));
    }
}
