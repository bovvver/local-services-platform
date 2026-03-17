package com.github.bovvver.bookingmanagement.bookingcreation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
    private OutboxRepository outboxRepository;

    @Mock
    private BookingEventMapper bookingEventMapper;

    @InjectMocks
    private BookingCreationService bookingCreationService;

    private final UUID offerId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void processBookingCreation_shouldThrowConflictWhenBookingAlreadyExistsForOfferAndUser() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(10_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(offerId, userId)).thenReturn(true);

        assertThatThrownBy(() -> bookingCreationService.processBookingCreation(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bookingRepository, never()).save(any());
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void processBookingCreation_shouldUseCurrentUserIdWhenCheckingConflicts() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(5_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(any(), any())).thenReturn(false);

        bookingCreationService.processBookingCreation(request);

        ArgumentCaptor<UUID> userIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(bookingReadRepository).existsByOfferIdAndUserId(eq(offerId), userIdCaptor.capture());
        assertThat(userIdCaptor.getValue()).isEqualTo(userId);
    }

    @Test
    void processBookingCreation_shouldCreateBookingAndPersistOutboxEventsWhenNoConflict() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(5_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(any(), any())).thenReturn(false);

        OutboxEvent outboxEvent = mock(OutboxEvent.class);
        when(bookingEventMapper.toOutboxEvent(any(DomainEvent.class))).thenReturn(outboxEvent);

        bookingCreationService.processBookingCreation(request);

        verify(bookingRepository).save(any(Booking.class));
        verify(outboxRepository, atLeastOnce()).save(outboxEvent);
    }

    @Test
    void createBooking_shouldCreateBookingWithCorrectDataAndPersistOutboxEvents() {
        UUID bookingId = UUID.randomUUID();
        BigDecimal salaryValue = BigDecimal.valueOf(7_500.0);
        BookOfferCommand command = new BookOfferCommand(
                offerId,
                userId,
                bookingId,
                salaryValue
        );

        OutboxEvent outboxEvent = mock(OutboxEvent.class);
        when(bookingEventMapper.toOutboxEvent(any(DomainEvent.class))).thenReturn(outboxEvent);

        bookingCreationService.createBooking(command);

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertThat(savedBooking.getUserId()).isEqualTo(UserId.of(userId));
        assertThat(savedBooking.getOfferId()).isEqualTo(OfferId.of(offerId));
        assertThat(savedBooking).isNotNull();

        verify(outboxRepository, atLeastOnce()).save(outboxEvent);
    }
}
