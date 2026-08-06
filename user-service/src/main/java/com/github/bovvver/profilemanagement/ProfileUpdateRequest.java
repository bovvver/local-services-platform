package com.github.bovvver.profilemanagement;

import com.github.bovvver.vo.ServiceCategory;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Set;
import java.util.UUID;

record ProfileUpdateRequest(
        @NotNull UUID userId,
        @Size(max = 1000, message = "Bio cannot exceed 1000 characters") String bio,
        @Size(max = 100, message = "City name is too long") String city,
        @Size(max = 2, message = "Country code must be 2 characters") String country,
        Set<ServiceCategory> categories
) {
}
