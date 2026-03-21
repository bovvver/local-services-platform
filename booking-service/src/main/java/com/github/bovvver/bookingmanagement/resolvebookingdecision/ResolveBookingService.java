package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.infrastructure.BookingDecisionValidationException;
import com.github.bovvver.bookingmanagement.infrastructure.BookingNotFoundException;
import com.github.bovvver.bookingmanagement.negotiation.NegotiationFacade;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
import com.github.bovvver.shared.CurrentUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private final BookingRepository bookingRepository;
    private final BookingReadRepository bookingReadRepository;
    private final OutboxRepository outboxRepository;
    private final BookingDecisionMapper bookingDecisionMapper;
    private final NegotiationFacade negotiationFacade;
    private final OfferOwnershipValidator offerOwnershipValidator;
    private final CurrentUser currentUser;

    void processBookingDecision(
            UUID bookingId,
            @Valid BookingDecisionRequest request
    ) {
        validateRequest(request);
        BookingEntity entity = bookingReadRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        Booking booking = BookingMapper.toDomain(entity);
        offerOwnershipValidator.validate(currentUser.getId().value(), booking.getOfferId().value());
        handleBookingDecision(booking, request);
    }

    private void handleBookingDecision(Booking booking, @Valid BookingDecisionRequest request) {

        BookingDecisionStatus status = request.status();
        if (status == BookingDecisionStatus.NEGOTIATE) {
            negotiationFacade.beginNegotiation(booking.getId().value(), request.salary());
        } else if (status == BookingDecisionStatus.ACCEPTED) {
            acceptBooking(booking);
        } else {
            booking.reject();
            bookingRepository.save(booking);
        }
    }

    private void acceptBooking(Booking booking) {

        List<BookingEntity> bookingsEntitiesToReject = bookingReadRepository.findAllByOfferIdAndIdIsNot(
                booking.getOfferId().value(),
                booking.getId().value()
        );
        List<Booking> bookingsToReject = BookingMapper.toDomainList(bookingsEntitiesToReject);
        bookingsToReject.forEach(Booking::reject);
        booking.accept();

        bookingsToReject.addFirst(booking);
        bookingRepository.saveAll(bookingsToReject);

        booking.pullDomainEvents().stream()
                .map(bookingDecisionMapper::toOutboxEvent)
                .filter(Objects::nonNull)
                .forEach(outboxRepository::save);
    }

    private void validateRequest(@Valid BookingDecisionRequest request) {
        if (request.status() == BookingDecisionStatus.NEGOTIATE && request.salary() == null) {
            throw new BookingDecisionValidationException("Salary must be provided when status is NEGOTIATE");
        }
        if (request.status() != BookingDecisionStatus.NEGOTIATE && request.salary() != null) {
            throw new BookingDecisionValidationException("Salary can't be provided when status is not NEGOTIATE");
        }
    }
}
