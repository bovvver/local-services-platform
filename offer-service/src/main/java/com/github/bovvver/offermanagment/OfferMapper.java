package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.*;

import java.util.ArrayList;

public class OfferMapper {

    public static Offer toDomain(OfferDocument document) {

        if (document == null) {
            return null;
        }

        return new Offer(
                OfferId.of(document.getId()),
                Title.of(document.getTitle()),
                Description.of(document.getDescription()),
                UserId.of(document.getAuthorId()),
                document.getExecutorId() == null ? null : UserId.of(document.getExecutorId()),
                Location.of(
                        document.getLocation().latitude(),
                        document.getLocation().longitude()
                ),
                document.getServiceCategories(),
                new Salary(document.getSalary()),
                document.getStatus(),
                document.getCreatedAt(),
                new ArrayList<>()
        );
    }

    public static OfferDocument toDocument(Offer offer) {
        return new OfferDocument(
                offer.getId().value(),
                offer.getTitle().value(),
                offer.getDescription().value(),
                offer.getAuthorId().value(),
                offer.getExecutorId() != null ? offer.getExecutorId().value() : null,
                offer.getLocation(),
                offer.getServiceCategories(),
                offer.getSalary().value(),
                offer.getStatus(),
                offer.getCreatedAt(),
                null    // managed by MongoDB
        );
    }
}
