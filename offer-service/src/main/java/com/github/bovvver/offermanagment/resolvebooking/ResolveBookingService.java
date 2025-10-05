package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.BookingDecisionCommand;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.OfferId;
import com.github.bovvver.shared.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private static final String OFFER_COMMANDS_TOPIC = "offer.commands";

    private final KafkaTemplate<String, BookingDecisionCommand> kafka;
    private final OfferRepository offerRepository;
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

        kafka.send(OFFER_COMMANDS_TOPIC, bookingId.toString(),
                createBookingDecisionCommand(
                        bookingId,
                        offerId,
                        request
                ));
    }

    private boolean checkOwnership(final UUID offerId) {
        return offerRepository.existsByIdAndOwnerId(
                OfferId.of(offerId),
                currentUser.getId()
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
