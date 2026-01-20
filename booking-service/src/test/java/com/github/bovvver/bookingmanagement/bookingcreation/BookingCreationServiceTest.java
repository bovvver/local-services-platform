package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingCreationServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private BookingReadRepository bookingReadRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OfferAvailabilityClient offerAvailabilityClient;

    @InjectMocks
    private BookingCreationService bookingCreationService;

    private final UUID offerId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldThrowConflictWhenBookingAlreadyExistsForOfferAndUser() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(10_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(offerId, userId)).thenReturn(true);

        assertThatThrownBy(() -> bookingCreationService.processBookingCreation(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(offerAvailabilityClient, never()).isOfferAvailable(any(), any(), any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldCheckConflictsUsingCurrentUserId() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(5_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(any(), any())).thenReturn(false);
        when(offerAvailabilityClient.isOfferAvailable(any(), any(), any())).thenReturn(true);

        bookingCreationService.processBookingCreation(request);

        ArgumentCaptor<UUID> userIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(bookingReadRepository).existsByOfferIdAndUserId(eq(offerId), userIdCaptor.capture());
        assertThat(userIdCaptor.getValue()).isEqualTo(userId);
    }

    @Test
    void shouldCreateBookingWhenOfferIsAvailable() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(5_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(any(), any())).thenReturn(false);
        when(offerAvailabilityClient.isOfferAvailable(any(), eq(offerId), eq(userId))).thenReturn(true);

        bookingCreationService.processBookingCreation(request);

        ArgumentCaptor<UUID> bookingIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(offerAvailabilityClient).isOfferAvailable(bookingIdCaptor.capture(), eq(offerId), eq(userId));
        verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldNotCreateBookingWhenOfferIsNotAvailable() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(5_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(any(), any())).thenReturn(false);
        when(offerAvailabilityClient.isOfferAvailable(any(), eq(offerId), eq(userId))).thenReturn(false);

        bookingCreationService.processBookingCreation(request);

        verify(offerAvailabilityClient).isOfferAvailable(any(), eq(offerId), eq(userId));
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldCreateBookingWithCorrectData() {
        UUID bookingId = UUID.randomUUID();
        UUID acceptedUserId = UUID.randomUUID();
        UUID eventOfferId = UUID.randomUUID();

        bookingCreationService.createBooking(bookingId, acceptedUserId, eventOfferId);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertThat(savedBooking.getUserId()).isEqualTo(UserId.of(acceptedUserId));
        assertThat(savedBooking.getOfferId()).isEqualTo(OfferId.of(eventOfferId));
    }
}
