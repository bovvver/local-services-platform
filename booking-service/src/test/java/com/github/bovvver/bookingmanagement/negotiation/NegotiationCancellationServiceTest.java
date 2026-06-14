package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingEntity;
import com.github.bovvver.bookingmanagement.BookingMapper;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NegotiationCancellationServiceTest {

    @Mock
    private BookingReadRepository bookingReadRepository;

    @Mock
    private BookingRepository bookingWriteRepository;

    @InjectMocks
    private NegotiationCancellationService negotiationCancellationService;

    private UUID bookingId;
    private BookingEntity bookingEntityInNegotiation;

    @BeforeEach
    void setUp() {
        bookingId = UUID.randomUUID();
        bookingEntityInNegotiation = new BookingEntity(
                bookingId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                BookingStatus.IN_NEGOTIATION,
                null,
                LocalDateTime.now(),
                null,
                LocalDateTime.now().plusDays(14)
        );
    }

    @Test
    void cancelNegotiation_shouldCancelNegotiationAndPersistBooking() {
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.ofNullable(bookingEntityInNegotiation));

        Booking booking = BookingMapper.toDomain(bookingEntityInNegotiation);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.IN_NEGOTIATION);

        negotiationCancellationService.cancelNegotiation(bookingId);

        verify(bookingReadRepository).findById(bookingId);
        verify(bookingWriteRepository).save(any(Booking.class));
    }

    @Test
    void cancelNegotiation_shouldPropagateExceptionWhenBookingNotFound() {
        when(bookingReadRepository.findById(bookingId)).thenReturn(null);

        assertThatThrownBy(() -> negotiationCancellationService.cancelNegotiation(bookingId))
                .isInstanceOf(NullPointerException.class);

        verify(bookingWriteRepository, never()).save(any());
    }
}
