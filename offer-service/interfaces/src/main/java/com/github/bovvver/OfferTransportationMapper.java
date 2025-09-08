package com.github.bovvver;

import com.github.bovvver.commands.CreateOfferCommand;
import com.github.bovvver.requests.CreateOfferRequest;
import com.github.bovvver.requests.LocationDTO;
import com.github.bovvver.responses.OfferCreatedResponse;
import com.github.bovvver.vo.Location;

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
