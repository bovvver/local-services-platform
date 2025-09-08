package com.github.bovvver.requests;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record LocationDTO (
        @Min(value = -90, message = "Latitude must be >= -90")
        @Max(value = 90, message = "Latitude must be <= 90")
        double latitude,

        @Min(value = -180, message = "Longitude must be >= -180")
        @Max(value = 180, message = "Longitude must be <= 180")
        double longitude
) {}
