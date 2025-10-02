package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.contracts.BookOfferCommand;
import com.github.bovvver.contracts.BookingAcceptedEvent;
import com.github.bovvver.shared.CurrentUser;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class BookingCreationService {

    private final CurrentUser currentUser;
    private final BookingDraftRepository bookingDraftRepository;
    private final BookingRepository bookingRepository;

    BookOfferCommand createBookingCommand(final BookOfferRequest request) {

        UUID currentUserId = currentUser.getId().value();
        UUID bookingId = UUID.randomUUID();

        return new BookOfferCommand(
                request.offerId(),
                currentUserId,
                bookingId
        );
    }

    @Transactional
    void createDraftBooking(final BookOfferCommand cmd, final Double salary) {

        BookingDraft bookingDraft = BookingDraft.create(
                BookingId.of(cmd.bookingId()),
                OfferId.of(cmd.offerId()),
                UserId.of(cmd.userId()),
                Salary.of(salary)
        );
        bookingDraftRepository.save(bookingDraft);
    }

    @Transactional
    void deleteDraftBooking(final UUID bookingId) {
        bookingDraftRepository.delete(BookingId.of(bookingId));
    }

    @Transactional
    void createBooking(final BookingAcceptedEvent event) {
        Salary salary = bookingDraftRepository.findSalaryByBookingId(BookingId.of(event.bookingId()));
        bookingDraftRepository.delete(BookingId.of(event.bookingId()));

        Booking booking = Booking.create(
                BookingId.of(event.bookingId()),
                UserId.of(event.userId()),
                OfferId.of(event.offerId()),
                salary
        );

        bookingRepository.save(booking);
    }
}
