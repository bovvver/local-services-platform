package com.github.bovvver.bookingmanagement.offercancellation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingEntity;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferCancellationServiceTest {

    @Captor
    private ArgumentCaptor<List<Booking>> bookingListCaptor;

    @Captor
    private ArgumentCaptor<Booking> bookingCaptor;

    @Mock
    private BookingReadRepository bookingReadRepository;

    @Mock
    private BookingRepository bookingWriteRepository;

    @InjectMocks
    private OfferCancellationService offerCancellationService;

    @Test
    void shouldCancelAllBookingsByAuthor() {

        UUID offerId = UUID.randomUUID();

        BookingEntity booking1 = createBookingEntity(offerId);
        BookingEntity booking2 = createBookingEntity(offerId);

        when(bookingReadRepository.findAllByOfferId(offerId))
                .thenReturn(List.of(booking1, booking2));

        offerCancellationService.cancelByAuthor(offerId);

        verify(bookingWriteRepository).saveAll(bookingListCaptor.capture());

        assertThat(bookingListCaptor.getValue())
                .extracting(Booking::getStatus)
                .containsOnly(BookingStatus.CANCELED_BY_AUTHOR);
    }

    @Test
    void shouldDoNothingWhenNoBookingsFoundForAuthorCancellation() {
        UUID offerId = UUID.randomUUID();

        when(bookingReadRepository.findAllByOfferId(offerId))
                .thenReturn(List.of());

        offerCancellationService.cancelByAuthor(offerId);

        verifyNoInteractions(bookingWriteRepository);
    }

    @Test
    void shouldCancelBookingByExecutor() {
        UUID offerId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();

        BookingEntity booking = createBookingEntity(offerId);

        when(bookingReadRepository.findByOfferIdAndUserId(offerId, executorId))
                .thenReturn(Optional.of(booking));

        offerCancellationService.cancelByExecutor(offerId, executorId);

        verify(bookingWriteRepository).save(bookingCaptor.capture());

        assertThat(bookingCaptor.getValue())
                .extracting(Booking::getStatus)
                .isEqualTo(BookingStatus.CANCELED_BY_EXECUTOR);
    }

    @Test
    void shouldDoNothingWhenBookingNotFoundForExecutorCancellation() {
        UUID offerId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();

        when(bookingReadRepository.findByOfferIdAndUserId(offerId, executorId))
                .thenReturn(Optional.empty());

        offerCancellationService.cancelByExecutor(offerId, executorId);

        verifyNoInteractions(bookingWriteRepository);
    }

    private BookingEntity createBookingEntity(UUID offerId) {
        return new BookingEntity(
                UUID.randomUUID(),
                offerId,
                UUID.randomUUID(),
                null,
                BookingStatus.PENDING,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                LocalDateTime.now().plusDays(14)
        );
    }
}
