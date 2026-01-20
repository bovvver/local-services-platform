package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingId;
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

import java.util.UUID;

@Service
@RequiredArgsConstructor
class BookingCreationService {

    private final CurrentUser currentUser;
    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingRepository;
    private final OfferAvailabilityClient offerAvailabilityClient;

    @Transactional
    public void processBookingCreation(@Valid BookOfferRequest request) {

        checkForExistingBookings(request.offerId());
        BookOfferCommand cmd = createBookingCommand(request);

        boolean isOfferAvailable = offerAvailabilityClient.isOfferAvailable(
                cmd.bookingId(),
                cmd.offerId(),
                cmd.userId()
        );

        if (isOfferAvailable) {
            createBooking(cmd.bookingId(), cmd.userId(), cmd.offerId());
        }
    }

    void createBooking(final UUID bookingId, final UUID userId, final UUID offerId) {
        // Salary salary = new Salary(bookingDraftReadRepository.findSalaryByBookingId(bookingId)); // FIXME: will be covered by saga in later PR

        Booking booking = Booking.create(
                BookingId.of(bookingId),
                UserId.of(userId),
                OfferId.of(offerId),
                Salary.of(0.0)
                // salary
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

    private void checkForExistingBookings(UUID offerId) {
        UUID currentUserId = currentUser.getId().value();

        if (bookingReadRepository.existsByOfferIdAndUserId(offerId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A booking for this offer already exists for the current user.");
        }
    }
}
