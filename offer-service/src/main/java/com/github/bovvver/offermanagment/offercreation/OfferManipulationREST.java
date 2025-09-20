package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.Offer;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class OfferManipulationREST {

    private static final String CREATE_OFFER_ENDPOINT = "/create";

    private final OfferManipulationFacade offerManipulationFacade;

    @PostMapping(path = CREATE_OFFER_ENDPOINT)
    ResponseEntity<OfferCreatedResponse> createOffer(
            @Valid @RequestBody CreateOfferRequest createOfferRequest
    ) {
        Offer createOffer = offerManipulationFacade.createOffer(OfferTransportationMapper.toCreateOfferCommand(createOfferRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(OfferTransportationMapper.toOfferCreatedResponse(createOffer));
    }
}
