package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.contracts.OtherBookingsRejectedEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private final BookingRepository bookingRepository;
    private final BookingReadRepository bookingReadRepository;
    private final OutboxRepository outboxRepository;
    private final BookingDecisionMapper bookingDecisionMapper;

    @Transactional
    void resolveBooking(BookingDecisionMadeEvent cmd) {

        Booking booking = BookingMapper.toDomain(bookingReadRepository.findById(cmd.bookingId()));

        if (cmd.status() == BookingDecisionStatus.REJECTED) {
            booking.reject();
            bookingRepository.save(booking);
            return;
        }
        booking.accept();
        bookingRepository.save(booking);

        booking.pullDomainEvents().stream()
                .map(bookingDecisionMapper::toOutboxEvent)
                .filter(Objects::nonNull)
                .forEach(outboxRepository::save);
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
