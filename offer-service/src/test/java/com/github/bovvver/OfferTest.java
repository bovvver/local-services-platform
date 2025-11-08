package com.github.bovvver;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.vo.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

class OfferTest {

    @Test
    void shouldBookOffer() {
        Offer offer = createOffer();


    }

    private Offer createOffer() {
        return Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                UserId.of(UUID.randomUUID()),
                new Location(
                        40.7128,
                        -74.0060
                ),
                Set.of(ServiceCategory.AUTOMOTIVE),
                Salary.of(1000.0)
        );
    }
}
