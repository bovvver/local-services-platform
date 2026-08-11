package com.github.bovvver.offermanagment.review;

import com.github.bovvver.contracts.ReviewAddedIntegrationEvent;
import com.github.bovvver.offermanagment.events.ReviewAdded;

public class ReviewAddedEventMapper {

    public static ReviewAddedIntegrationEvent toIntegrationEvent(final ReviewAdded reviewAdded) {
        return new ReviewAddedIntegrationEvent(
                reviewAdded.offerId(),
                reviewAdded.executorId(),
                reviewAdded.rating()
        );
    }
}
