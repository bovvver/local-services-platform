package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingEntity;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingFailureServiceTest {

    @Mock
    private BookingReadRepository bookingReadRepository;

    @Mock
    private BookingRepository bookingWriteRepository;

    @InjectMocks
    private BookingFailureService bookingFailureService;

    @Test
    void shouldRejectBookingAndPersist_whenHandlingAcceptedFailure() {
        UUID bookingId = UUID.randomUUID();
        BookingEntity entity = new BookingEntity(
                bookingId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                null,
                BookingStatus.PENDING,
                BigDecimal.valueOf(1000),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        when(bookingReadRepository.findById(bookingId)).thenReturn(Optional.of(entity));

        bookingFailureService.handleBookingAcceptedFailure(bookingId);

        verify(bookingReadRepository).findById(bookingId);
        verify(bookingWriteRepository).save(any(Booking.class));
    }

    @Test
    void shouldPropagateException_whenBookingNotFound() {
        UUID bookingId = UUID.randomUUID();
        when(bookingReadRepository.findById(bookingId)).thenThrow(new IllegalStateException("Booking not found"));

        assertThrows(IllegalStateException.class,
                () -> bookingFailureService.handleBookingAcceptedFailure(bookingId));

        verify(bookingWriteRepository, never()).save(any());
    }
}
