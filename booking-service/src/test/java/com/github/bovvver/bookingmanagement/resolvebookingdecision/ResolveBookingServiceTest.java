package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.infrastructure.BookingDecisionValidationException;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import com.github.bovvver.bookingmanagement.vo.*;
import com.github.bovvver.shared.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveBookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private BookingReadRepository bookingReadRepository;
    @Mock
    private com.github.bovvver.bookingmanagement.outbox.OutboxRepository outboxRepository;
    @Mock
    private BookingDecisionMapper bookingDecisionMapper;
    @Mock
    private com.github.bovvver.bookingmanagement.negotiation.NegotiationFacade negotiationFacade;
    @Mock
    private OfferOwnershipValidator offerOwnershipValidator;
    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ResolveBookingService resolveBookingService;

    @Test
    void shouldAcceptBookingAndRejectOthers_andPersistAndPublishOutboxEvents() {
        UUID bookingId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BookingEntity mainEntity = new BookingEntity(
                bookingId,
                userId,
                offerId,
                null,
                BookingStatus.PENDING,
                BigDecimal.valueOf(1000),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.of(mainEntity));

        UserId currentUserId = new UserId(userId);
        when(currentUser.getId()).thenReturn(currentUserId);

        BookingEntity otherEntity = new BookingEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                offerId,
                null,
                BookingStatus.PENDING,
                BigDecimal.valueOf(900),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(bookingReadRepository.findAllByOfferIdAndIdIsNot(offerId, bookingId)).thenReturn(List.of(otherEntity));

        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                null
        );

        doNothing().when(offerOwnershipValidator).validate(userId, offerId);

        OutboxEvent outboxEvent = OutboxEvent.create(
                offerId,
                "Booking",
                "BookingAccepted",
                new com.fasterxml.jackson.databind.ObjectMapper().createObjectNode(),
                LocalDateTime.now()
        );
        when(bookingDecisionMapper.toOutboxEvent(any())).thenReturn(outboxEvent);

        resolveBookingService.processBookingDecision(bookingId, request);

        verify(offerOwnershipValidator).validate(userId, offerId);
        verify(bookingRepository).saveAll(anyIterable());
        verify(outboxRepository, atLeastOnce()).save(any(OutboxEvent.class));
        verify(negotiationFacade, never()).beginNegotiation(any(), any(), any());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void shouldBeginNegotiation_whenStatusIsNegotiate() {
        UUID bookingId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BookingEntity entity = new BookingEntity(
                bookingId,
                userId,
                offerId,
                null,
                BookingStatus.PENDING,
                BigDecimal.valueOf(1000),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.of(entity));
        when(currentUser.getId()).thenReturn(new UserId(userId));
        doNothing().when(offerOwnershipValidator).validate(userId, offerId);

        BigDecimal salary = BigDecimal.valueOf(1200);
        BookingDecisionRequest request = new BookingDecisionRequest(
            BookingDecisionStatus.NEGOTIATE,
            salary
        );

        resolveBookingService.processBookingDecision(bookingId, request);

        verify(negotiationFacade).beginNegotiation(bookingId, userId, salary);
        verifyNoInteractions(outboxRepository);
        verify(bookingRepository, never()).save(any());
        verify(bookingRepository, never()).saveAll(anyIterable());
    }

    @Test
    void shouldRejectBooking_whenStatusIsRejected() {
        UUID bookingId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BookingEntity entity = new BookingEntity(
            bookingId,
            userId,
            offerId,
            null,
            BookingStatus.PENDING,
            BigDecimal.valueOf(1000),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.of(entity));
        when(currentUser.getId()).thenReturn(new UserId(userId));
        doNothing().when(offerOwnershipValidator).validate(userId, offerId);

        BookingDecisionRequest request = new BookingDecisionRequest(
            BookingDecisionStatus.REJECTED,
            null
        );

        resolveBookingService.processBookingDecision(bookingId, request);

        verify(bookingRepository).save(any(Booking.class));
        verifyNoInteractions(outboxRepository);
        verify(negotiationFacade, never()).beginNegotiation(any(), any(), any());
    }

    @Test
    void shouldThrowWhenSalaryMissingForNegotiate() {
        UUID bookingId = UUID.randomUUID();
        BookingDecisionRequest request = new BookingDecisionRequest(
            BookingDecisionStatus.NEGOTIATE,
            null
        );

        assertThrows(BookingDecisionValidationException.class,
            () -> resolveBookingService.processBookingDecision(bookingId, request));

        verifyNoInteractions(bookingReadRepository, offerOwnershipValidator, negotiationFacade, bookingRepository, outboxRepository);
    }

    @Test
    void shouldThrowWhenSalaryProvidedForNonNegotiate() {
        UUID bookingId = UUID.randomUUID();
        BookingDecisionRequest request = new BookingDecisionRequest(
            BookingDecisionStatus.ACCEPTED,
            BigDecimal.TEN
        );

        assertThrows(BookingDecisionValidationException.class,
            () -> resolveBookingService.processBookingDecision(bookingId, request));

        verifyNoInteractions(bookingReadRepository, offerOwnershipValidator, negotiationFacade, bookingRepository, outboxRepository);
    }

    @Test
    void shouldPropagateWhenOfferOwnershipInvalid() {
        UUID bookingId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        BookingEntity entity = new BookingEntity(
            bookingId,
            userId,
            offerId,
            null,
            BookingStatus.PENDING,
            BigDecimal.valueOf(1000),
            LocalDateTime.now(),
            LocalDateTime.now()
        );
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.of(entity));
        when(currentUser.getId()).thenReturn(new UserId(userId));

        doThrow(new IllegalStateException("Current user is not the owner of the offer"))
            .when(offerOwnershipValidator).validate(userId, offerId);

        BookingDecisionRequest request = new BookingDecisionRequest(
            BookingDecisionStatus.ACCEPTED,
            null
        );

        assertThrows(IllegalStateException.class,
            () -> resolveBookingService.processBookingDecision(bookingId, request));

        verify(bookingRepository, never()).save(any());
        verify(bookingRepository, never()).saveAll(anyIterable());
        verifyNoInteractions(outboxRepository, negotiationFacade);
    }
}
