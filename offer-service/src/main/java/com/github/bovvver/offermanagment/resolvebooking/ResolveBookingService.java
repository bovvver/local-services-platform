package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingDecisionCommand;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.offermanagment.OfferReadRepository;
import com.github.bovvver.shared.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private static final String OFFER_BOOKING_DECISION = "offer.booking.decision";
    private static final String OFFER_BOOKING_NEGOTIATE = "offer.booking.negotiate";

    private final KafkaTemplate<String, BookingDecisionCommand> kafka;
    private final OfferReadRepository offerReadRepository;
    private final CurrentUser currentUser;

    void processBookingDecision(
            UUID bookingId,
            UUID offerId,
            @Valid BookingDecisionRequest request
    ) {
        if (!checkOwnership(offerId)) {
            throw new IllegalStateException(
                    "Current user is not the owner of the offer with id %s".formatted(offerId)
            );
        }
        validateRequest(request);
        sendBookingDecisionCommand(createBookingDecisionCommand(
                bookingId,
                offerId,
                request
        ));
    }

    private void sendBookingDecisionCommand(BookingDecisionCommand cmd) {

        if (cmd.status() == BookingDecisionStatus.NEGOTIATE) {
            kafka.send(OFFER_BOOKING_NEGOTIATE, cmd.bookingId().toString(), cmd);
            return;
        }
        kafka.send(OFFER_BOOKING_DECISION, cmd.bookingId().toString(), cmd);
    }

    private boolean checkOwnership(final UUID offerId) {

        UUID currentUserId = currentUser.getId().value();
        if (currentUserId == null) {
            throw new IllegalStateException("No user logged in.");
        }

        return offerReadRepository.existsByIdAndAuthorId(
                offerId,
                currentUserId
        );
    }

    private void validateRequest(@Valid BookingDecisionRequest request) {
        if (request.status() == BookingDecisionStatus.NEGOTIATE && request.salary() == null) {
            throw new IllegalArgumentException("Salary must be provided when status is NEGOTIATE");
        }
        if (request.status() != BookingDecisionStatus.NEGOTIATE && request.salary() != null) {
            throw new IllegalArgumentException("Salary can't be provided when status is not NEGOTIATE");
        }
    }

    private BookingDecisionCommand createBookingDecisionCommand(
            UUID bookingId,
            UUID offerId,
            BookingDecisionRequest request
    ) {
        return new BookingDecisionCommand(
                bookingId,
                offerId,
                request.status(),
                request.salary()
        );
    }
}
