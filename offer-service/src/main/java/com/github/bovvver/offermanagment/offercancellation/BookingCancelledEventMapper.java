package com.github.bovvver.offermanagment.offercancellation;

import com.github.bovvver.contracts.BookingCancelledByAuthorIntegrationEvent;
import com.github.bovvver.contracts.BookingCancelledByExecutorIntegrationEvent;

public class BookingCancelledEventMapper {

    public static BookingCancelledByAuthorIntegrationEvent authorToIntegrationEvent(final OfferCancelledByAuthor offerCancelledByAuthor) {
        return new BookingCancelledByAuthorIntegrationEvent(
                offerCancelledByAuthor.offerId()
        );
    }

    public static BookingCancelledByExecutorIntegrationEvent executorToIntegrationEvent(final OfferCancelledByExecutor offerCancelledByExecutor) {
        return new BookingCancelledByExecutorIntegrationEvent(
                offerCancelledByExecutor.offerId(),
                offerCancelledByExecutor.executorId()
        );
    }
}
