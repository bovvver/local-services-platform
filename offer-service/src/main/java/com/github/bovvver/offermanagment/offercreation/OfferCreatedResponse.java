package com.github.bovvver.offermanagment.offercreation;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public record OfferCreatedResponse(

        UUID offerId,
        String title,
        String description,
        String status,
        LocationDTO location,
        Set<String> serviceCategories,
        Double salary,
        LocalDateTime createdAt
) {
}
