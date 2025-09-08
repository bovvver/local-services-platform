package com.github.bovvver.requests;

import com.github.bovvver.validation.ValidServiceCategory;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.util.Set;

public record CreateOfferRequest(
        @NotBlank(message = "Title cannot be blank")
        @Size(max = 100, message = "Title cannot exceed 100 characters")
        String title,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 1000, message = "Description cannot exceed 1000 characters")
        String description,

        @PositiveOrZero(message = "Salary must be non-negative")
        double salary,

        @Valid
        LocationDTO location,

        @Size(max = 5, message = "A maximum of 5 service categories are allowed")
        @ValidServiceCategory(message = "Service category must be one of: " +
                "HOME_SERVICES, AUTOMOTIVE, BEAUTY_WELLNESS, EDUCATION, " +
                "TECH_SUPPORT, DELIVERY, CLEANING, REPAIR, GARDENING, PET_SERVICES")
        Set<@NotBlank(message = "Service category cannot be blank") String> serviceCategories
) {
}