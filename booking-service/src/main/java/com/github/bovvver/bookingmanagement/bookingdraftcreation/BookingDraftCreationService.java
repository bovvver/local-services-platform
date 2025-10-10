package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.contracts.BookOfferCommand;
import com.github.bovvver.contracts.BookingDraftAcceptedEvent;
import com.github.bovvver.shared.CurrentUser;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class BookingDraftCreationService {

    private static final String BOOKING_COMMANDS_TOPIC = "booking.commands";

    private final CurrentUser currentUser;
    private final BookingDraftWriteRepository bookingDraftWriteRepository;
    private final BookingDraftReadRepository bookingDraftReadRepository;
    private final BookingRepository bookingRepository;
    private final KafkaTemplate<String, BookOfferCommand> kafka;

    public void processBookingCreation(@Valid BookOfferRequest request) {

        BookOfferCommand cmd = createBookingCommand(request);
        createDraftBooking(cmd, request.salary());
        kafka.send(BOOKING_COMMANDS_TOPIC, cmd.offerId().toString(), cmd);
    }

    @Transactional
    void deleteDraftBooking(final UUID bookingId) {
        bookingDraftWriteRepository.delete(BookingId.of(bookingId));
    }

    @Transactional
    void createBooking(final BookingDraftAcceptedEvent event) {
        Salary salary = Salary.of(bookingDraftReadRepository.findSalaryByBookingId(event.bookingId()));
        bookingDraftWriteRepository.delete(BookingId.of(event.bookingId()));

        Booking booking = Booking.create(
                BookingId.of(event.bookingId()),
                UserId.of(event.userId()),
                OfferId.of(event.offerId()),
                salary
        );

        bookingRepository.save(booking);
    }

    private BookOfferCommand createBookingCommand(final BookOfferRequest request) {

        UUID currentUserId = currentUser.getId().value();
        UUID bookingId = UUID.randomUUID();

        return new BookOfferCommand(
                request.offerId(),
                currentUserId,
                bookingId
        );
    }

    private void createDraftBooking(final BookOfferCommand cmd, final Double salary) {

        BookingDraft bookingDraft = BookingDraft.create(
                BookingId.of(cmd.bookingId()),
                OfferId.of(cmd.offerId()),
                UserId.of(cmd.userId()),
                Salary.of(salary)
        );
        bookingDraftWriteRepository.save(bookingDraft);
    }
}
