package com.github.bovvver;

import com.github.bovvver.vo.*;

class OfferMapper {

    static Offer toDomain(OfferDocument entity) {
        return Offer.create(
                Title.of(entity.getTitle()),
                Description.of(entity.getDescription()),
                UserId.of(entity.getAuthorId()),
                Location.of(
                        entity.getLocation().latitude(),
                        entity.getLocation().longitude()
                ),
                entity.getServiceCategories(),
                Salary.of(entity.getSalary())
        );
    }

    static OfferDocument toDocument(Offer offer) {
        return new OfferDocument(
                offer.getTitle().value(),
                offer.getDescription().value(),
                offer.getAuthorId().value(),
                Location.of(
                        offer.getLocation().latitude(),
                        offer.getLocation().longitude()
                ),
                offer.getServiceCategories(),
                offer.getSalary().value()
        );
    }
}
