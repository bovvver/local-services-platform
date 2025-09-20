package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.vo.Location;

import java.util.stream.Collectors;

class OfferTransportationMapper {

    static CreateOfferCommand toCreateOfferCommand(CreateOfferRequest createOfferRequest) {
        return new CreateOfferCommand(
                createOfferRequest.title(),
                createOfferRequest.description(),
                createOfferRequest.salary(),
                new Location(
                        createOfferRequest.location().latitude(),
                        createOfferRequest.location().longitude()
                ),
                createOfferRequest.serviceCategories()
        );
    }

    static OfferCreatedResponse toOfferCreatedResponse(Offer offer) {
        return new OfferCreatedResponse(
                offer.getId().value(),
                offer.getTitle().value(),
                offer.getDescription().value(),
                offer.getStatus().name(),
                new LocationDTO(
                        offer.getLocation().latitude(),
                        offer.getLocation().longitude()
                ),
                offer.getServiceCategories().stream().map(Enum::name).collect(Collectors.toSet()),
                offer.getSalary().value(),
                offer.getCreatedAt()
        );
    }
}
