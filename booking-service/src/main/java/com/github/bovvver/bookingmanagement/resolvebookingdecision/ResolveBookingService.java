package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import com.github.bovvver.contracts.AssignExecutorCommand;
import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.contracts.OtherBookingsRejectedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    static final String OFFER_BOOKING_DECISION_RESPONSE = "offer.booking.decision.response";

    private final BookingRepository bookingRepository;
    private final BookingReadRepository bookingReadRepository;
    private final KafkaTemplate<String, AssignExecutorCommand> kafka;

    @Transactional
    void resolveBooking(BookingDecisionMadeEvent cmd) {

        Booking booking = BookingMapper.toDomain(bookingReadRepository.findById(cmd.bookingId()));

        if (cmd.status() == BookingDecisionStatus.REJECTED) {
            booking.reject();
            return;
        }
        booking.accept();
        bookingRepository.save(booking);

        kafka.send(OFFER_BOOKING_DECISION_RESPONSE, cmd.bookingId().toString(),
                new AssignExecutorCommand(
                        booking.getOfferId().value(),
                        booking.getUserId().value()
                ));
    }

    @Transactional
    void rejectOtherBookings(final OtherBookingsRejectedEvent event) {
        List<BookingEntity> bookingEntities = bookingReadRepository.findAllByOfferIdAndStatusNotIn(
                event.offerId(),
                List.of(BookingStatus.ACCEPTED, BookingStatus.REJECTED)
        );
        List<Booking> bookings = BookingMapper.toDomainList(bookingEntities);
        bookings.forEach(Booking::reject);
        bookingRepository.saveAll(bookings);
    }
}
