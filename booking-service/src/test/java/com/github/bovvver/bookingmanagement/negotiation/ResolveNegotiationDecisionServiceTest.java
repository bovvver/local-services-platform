package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxEvent;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveNegotiationDecisionServiceTest {

    @Mock
    private BookingReadRepository bookingReadRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OutboxRepository outboxRepository;

    @Mock
    private NegotiationEventMapper negotiationEventMapper;

    @InjectMocks
    private ResolveNegotiationDecisionService resolveNegotiationDecisionService;

    private UUID bookingId;
    private BookingEntity bookingEntity;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        bookingEntity = new BookingEntity(
                bookingId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                BookingStatus.PENDING,
                null,
                LocalDateTime.now(),
                null
        );
    }

    @Test
    void beginNegotiation_shouldBeginNegotiationAndPersistBookingAndOutboxEvents() {
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.ofNullable(bookingEntity));

        Salary proposedSalary = new Salary(BigDecimal.valueOf(1000));
        UUID offerAuthorId = UUID.randomUUID();

        Booking bookingFromMapper = BookingMapper.toDomain(bookingEntity);
        assertThat(bookingFromMapper.getStatus()).isEqualTo(BookingStatus.PENDING);

        when(negotiationEventMapper.toOutboxEvent(any(DomainEvent.class))).thenReturn(mock(OutboxEvent.class));

        resolveNegotiationDecisionService.beginNegotiation(bookingId, offerAuthorId, proposedSalary.value());
        verify(bookingReadRepository).findById(bookingId);
        verify(bookingRepository).save(any(Booking.class));
        verify(negotiationEventMapper, atLeastOnce()).toOutboxEvent(any(DomainEvent.class));
        verify(outboxRepository, atLeastOnce()).save(any(OutboxEvent.class));
    }

    @Test
    void beginNegotiation_shouldPropagateExceptionWhenBookingNotFound() {
        when(bookingReadRepository.findById(bookingId)).thenReturn(null);

        UUID offerAuthorId = UUID.randomUUID();

        assertThatThrownBy(() -> resolveNegotiationDecisionService.beginNegotiation(bookingId, offerAuthorId, BigDecimal.valueOf(1000)))
                .isInstanceOf(NullPointerException.class);
        verify(bookingRepository, never()).save(any());
        verify(outboxRepository, never()).save(any());
    }

    @Test
    void beginNegotiation_shouldNotSaveOutboxEventsWhenMapperReturnsNull() {
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.ofNullable(bookingEntity));

        Salary proposedSalary = new Salary(BigDecimal.valueOf(1000));
        UUID offerAuthorId = UUID.randomUUID();

        when(negotiationEventMapper.toOutboxEvent(any(DomainEvent.class))).thenReturn(null);

        resolveNegotiationDecisionService.beginNegotiation(bookingId, offerAuthorId, proposedSalary.value());
        verify(bookingRepository).save(any(Booking.class));
        verify(outboxRepository, never()).save(any());
    }
}
