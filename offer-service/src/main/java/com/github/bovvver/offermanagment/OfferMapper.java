package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.*;

import java.util.stream.Collectors;

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
                BookingId.fromAll(document.getBookingIds()),
                Location.of(
                        document.getLocation().latitude(),
                        document.getLocation().longitude()
                ),
                document.getServiceCategories(),
                Salary.of(document.getSalary()),
                document.getStatus(),
                document.getCreatedAt(),
                document.getUpdatedAt()
        );
    }

    public static OfferDocument toDocument(Offer offer) {
        return new OfferDocument(
                offer.getId().value(),
                offer.getTitle().value(),
                offer.getDescription().value(),
                offer.getAuthorId().value(),
                offer.getExecutorId() != null ? offer.getExecutorId().value() : null,
                offer.getBookingIds().stream().map(BookingId::value).collect(Collectors.toSet()),
                Location.of(
                        offer.getLocation().latitude(),
                        offer.getLocation().longitude()
                ),
                offer.getServiceCategories(),
                offer.getSalary().value(),
                offer.getStatus(),
                offer.getCreatedAt(),
                offer.getUpdatedAt()
        );
    }
}
