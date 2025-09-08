package com.github.bovvver.vo;

import java.util.Arrays;

public enum ServiceCategory {
    HOME_SERVICES,
    AUTOMOTIVE,
    BEAUTY_WELLNESS,
    EDUCATION,
    TECH_SUPPORT,
    DELIVERY,
    CLEANING,
    REPAIR,
    GARDENING,
    PET_SERVICES;

    public static ServiceCategory fromString(String category) {
        return Arrays.stream(ServiceCategory.values())
                .filter(c -> c.name().equalsIgnoreCase(category))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Invalid service category: " + category));
    }
}
