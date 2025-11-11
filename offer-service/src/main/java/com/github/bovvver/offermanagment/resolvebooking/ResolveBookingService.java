package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.AssignExecutorCommand;
import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.contracts.OtherBookingsRejectedEvent;
import com.github.bovvver.offermanagment.*;
import com.github.bovvver.offermanagment.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    static final String OFFER_BOOKING_DECISION = "offer.booking.decision";
    static final String OFFER_BOOKING_NEGOTIATE = "offer.booking.negotiate";
    static final String OFFER_BOOKING_REJECT_OTHERS = "offer.booking.reject.others";

    private final KafkaTemplate<String, Object> kafka;
    private final OfferReadRepository offerReadRepository;
    private final OfferWriteRepository offerWriteRepository;
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
        sendBookingDecisionMadeEvent(createBookingDecisionMadeEvent(
                bookingId,
                offerId,
                request
        ));
    }

    @Transactional
    void completeBookingAssignment(final AssignExecutorCommand cmd) {
        OfferDocument offerDocument = offerReadRepository.findById(cmd.offerId())
                .orElseThrow(() -> new IllegalStateException(
                        "Offer with id %s not found during booking decision.".formatted(cmd.offerId())
                ));
        Offer offer = OfferMapper.toDomain(offerDocument);
        offer.accept(UserId.of(cmd.userId()));
        offerWriteRepository.save(offer);

        kafka.send(OFFER_BOOKING_REJECT_OTHERS, cmd.offerId().toString(), new OtherBookingsRejectedEvent(cmd.offerId()));
    }

    private void sendBookingDecisionMadeEvent(BookingDecisionMadeEvent cmd) {

        OfferDocument offerDocument = offerReadRepository.findById(cmd.offerId())
                .orElseThrow(() -> new IllegalStateException(
                        "Offer with id %s not found during booking decision.".formatted(cmd.offerId())
                ));
        Offer offer = OfferMapper.toDomain(offerDocument);

        if (cmd.status() == BookingDecisionStatus.NEGOTIATE) {
            kafka.send(OFFER_BOOKING_NEGOTIATE, cmd.bookingId().toString(), cmd);
            offer.negotiate();
            return;
        }
        kafka.send(OFFER_BOOKING_DECISION, cmd.bookingId().toString(), cmd);
    }

    private boolean checkOwnership(final UUID offerId) {

        UserId currentUserId = currentUser.getId();
        if (currentUserId == null || currentUserId.value() == null) {
            throw new IllegalStateException("No user logged in.");
        }

        return offerReadRepository.existsByIdAndAuthorId(
                offerId,
                currentUserId.value()
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

    private BookingDecisionMadeEvent createBookingDecisionMadeEvent(
            UUID bookingId,
            UUID offerId,
            BookingDecisionRequest request
    ) {
        return new BookingDecisionMadeEvent(
                bookingId,
                offerId,
                request.status(),
                request.salary()
        );
    }
}
