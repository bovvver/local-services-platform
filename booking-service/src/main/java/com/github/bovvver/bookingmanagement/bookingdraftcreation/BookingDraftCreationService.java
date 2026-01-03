package com.github.bovvver.bookingmanagement.bookingdraftcreation;

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
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class BookingDraftCreationService {

    private static final String RESOLVE_BOOKING_DRAFT_ENDPOINT = "/internal/offer/availability";

    private final CurrentUser currentUser;
    private final BookingDraftWriteRepository bookingDraftWriteRepository;
    private final BookingDraftReadRepository bookingDraftReadRepository;
    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    public void processBookingCreation(@Valid BookOfferRequest request) {

        checkForExistingBookings(request.offerId());
        BookOfferCommand cmd = createBookingCommand(request);
        createDraftBooking(cmd, request.salary());

        RestClient restClient = RestClient.builder()
                .baseUrl("http://offer-service")
                .build();

        ResponseEntity<OfferAvailabilityCheckResponse> response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(RESOLVE_BOOKING_DRAFT_ENDPOINT)
                        .queryParam("offerId", cmd.offerId())
                        .queryParam("userId", cmd.userId())
                        .queryParam("bookingId", cmd.bookingId())
                        .build())
                .retrieve()
                .toEntity(OfferAvailabilityCheckResponse.class);

        if (response.getBody() != null && response.getBody().isAvailable()) {
            createBooking(cmd.bookingId(), cmd.userId(), cmd.offerId());
        } else if (response.getBody() != null && !response.getBody().isAvailable()) {
            deleteDraftBooking(cmd.bookingId());
        }
    }

    void deleteDraftBooking(final UUID bookingId) {
        bookingDraftWriteRepository.delete(BookingId.of(bookingId));
    }

    void createBooking(final UUID bookingId, final UUID userId, final UUID offerId) {
        Salary salary = Salary.of(bookingDraftReadRepository.findSalaryByBookingId(bookingId));
        bookingDraftWriteRepository.delete(BookingId.of(bookingId));

        Booking booking = Booking.create(
                BookingId.of(bookingId),
                UserId.of(userId),
                OfferId.of(offerId),
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

    private void createDraftBooking(final BookOfferCommand cmd, final BigDecimal salary) {

        BookingDraft bookingDraft = BookingDraft.create(
                BookingId.of(cmd.bookingId()),
                OfferId.of(cmd.offerId()),
                UserId.of(cmd.userId()),
                new Salary(salary)
        );
        bookingDraftWriteRepository.save(bookingDraft);
    }

    private void checkForExistingBookings(UUID offerId) {
        UUID currentUserId = currentUser.getId().value();

        if (bookingReadRepository.existsByOfferIdAndUserId(offerId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A booking for this offer already exists for the current user.");
        }

        if (bookingDraftReadRepository.existsByOfferIdAndUserId(offerId, currentUserId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "A booking draft for this offer already exists for the current user.");
        }
    }
}
