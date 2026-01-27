package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.contracts.BookOfferCommand;
import com.github.bovvver.shared.CurrentUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class BookingCreationService {

    private final CurrentUser currentUser;
    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingRepository;
    private final OutboxRepository outboxRepository;
    private final BookingEventMapper bookingEventMapper;

    @Transactional
    public void processBookingCreation(@Valid BookOfferRequest request) {

        checkForExistingBookings(request.offerId());
        createBooking(createBookingCommand(request));
    }

    void createBooking(BookOfferCommand command) {
        Booking booking = Booking.create(
                UserId.of(command.userId()),
                OfferId.of(command.offerId()),
                new Salary(command.salary())
        );
        bookingRepository.save(booking);
        booking.pullDomainEvents().stream()
                .map(bookingEventMapper::toOutboxEvent)
                .filter(Objects::nonNull)
                .forEach(outboxRepository::save);
    }

    private BookOfferCommand createBookingCommand(final BookOfferRequest request) {

        UUID currentUserId = currentUser.getId().value();
        UUID bookingId = UUID.randomUUID();

        return new BookOfferCommand(
                request.offerId(),
                currentUserId,
                bookingId,
                request.salary()
        );
    }

    private void checkForExistingBookings(UUID offerId) {
        UUID currentUserId = currentUser.getId().value();

        if (bookingReadRepository.existsByOfferIdAndUserId(offerId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A booking for this offer already exists for the current user.");
        }
    }
}
