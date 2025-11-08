package com.github.bovvver.offermanagment.vo;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ServiceCategoryTest {

    @Test
    void testFromString_ValidCategory() {
        assertThat(ServiceCategory.fromString("HOME_SERVICES")).isEqualTo(ServiceCategory.HOME_SERVICES);
        assertThat(ServiceCategory.fromString("automotive")).isEqualTo(ServiceCategory.AUTOMOTIVE);
        assertThat(ServiceCategory.fromString("Beauty_Wellness")).isEqualTo(ServiceCategory.BEAUTY_WELLNESS);
    }

    @Test
    void testFromString_InvalidCategory() {
        assertThrows(IllegalArgumentException.class, () -> ServiceCategory.fromString("INVALID_CATEGORY"));
    }
}
