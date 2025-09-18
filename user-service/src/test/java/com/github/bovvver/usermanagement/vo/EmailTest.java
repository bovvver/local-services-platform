package com.github.bovvver.usermanagement.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class EmailTest {

    @Test
    void shouldCreateEmailWithValidFormat() {
        Email email = new Email("test@example.com");

        assertThat(email.value()).isEqualTo("test@example.com");
    }

    @Test
    void shouldThrowExceptionWhenEmailIsNull() {
        assertThrows(IllegalArgumentException.class, () -> new Email(null));
    }

    @Test
    void shouldThrowExceptionWhenEmailIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> new Email("   "));
    }

    @Test
    void shouldThrowExceptionWhenEmailHasInvalidFormat() {
        assertThrows(IllegalArgumentException.class, () -> new Email("invalid-email"));
        assertThrows(IllegalArgumentException.class, () -> new Email("test@"));
        assertThrows(IllegalArgumentException.class, () -> new Email("@example.com"));
    }

    @Test
    void shouldAcceptComplexValidEmailFormats() {
        assertDoesNotThrow(() -> new Email("user.name+tag@example.co.uk"));
        assertDoesNotThrow(() -> new Email("test123@subdomain.example.com"));
        assertDoesNotThrow(() -> new Email("user_name@example.com"));
        assertDoesNotThrow(() -> new Email("user-name@example-domain.org"));
        assertDoesNotThrow(() -> new Email("user%tag@example.co"));
        assertDoesNotThrow(() -> new Email("u.ser+1_tag@sub.subdomain.example.io"));
    }
}
