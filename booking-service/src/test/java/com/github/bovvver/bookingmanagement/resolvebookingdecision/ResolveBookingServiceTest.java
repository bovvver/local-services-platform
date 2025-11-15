package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingEntity;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import com.github.bovvver.contracts.AssignExecutorCommand;
import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.contracts.OtherBookingsRejectedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.StreamSupport;

import static com.github.bovvver.bookingmanagement.resolvebookingdecision.ResolveBookingService.OFFER_BOOKING_DECISION_RESPONSE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveBookingServiceTest {

    @Mock
    private KafkaTemplate<String, AssignExecutorCommand> kafka;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private BookingReadRepository bookingReadRepository;

    @InjectMocks
    private ResolveBookingService resolveBookingService;

    private final UUID offerId = UUID.randomUUID();
    private final UUID bookingId = UUID.randomUUID();

    @Test
    void shouldNotSendAnEventWhenStatusRejected() {

        BookingDecisionMadeEvent command = new BookingDecisionMadeEvent(
                bookingId,
                offerId,
                BookingDecisionStatus.REJECTED,
                null
        );
        BookingEntity bookingEntity = getTestBookingEntity();
        when(bookingReadRepository.findById(command.bookingId()))
                .thenReturn(bookingEntity);

        resolveBookingService.resolveBooking(command);

        verifyNoInteractions(bookingRepository);
        verifyNoInteractions(kafka);
    }

    @Test
    void shouldSendAnEventWhenStatusAccepted() {

        BookingDecisionMadeEvent command = new BookingDecisionMadeEvent(
                bookingId,
                offerId,
                BookingDecisionStatus.ACCEPTED,
                null
        );
        BookingEntity bookingEntity = getTestBookingEntity();
        when(bookingReadRepository.findById(command.bookingId()))
                .thenReturn(bookingEntity);

        resolveBookingService.resolveBooking(command);

        verify(bookingRepository).save(any(Booking.class));
        verify(kafka).send(
                eq(OFFER_BOOKING_DECISION_RESPONSE),
                eq(bookingId.toString()),
                argThat(event ->
                        event.userId().equals(bookingEntity.getUserId()) &&
                                event.offerId().equals(bookingEntity.getOfferId())
                )
        );
    }

    @Test
    void shouldRejectAllPendingBookingsForOffer() {
        UUID offerId = UUID.randomUUID();
        OtherBookingsRejectedEvent event = new OtherBookingsRejectedEvent(offerId);

        List<BookingEntity> pendingBookings = List.of(
                getTestBookingEntity(),
                getTestBookingEntity()
        );

        when(bookingReadRepository.findAllByOfferIdAndStatusNotIn(
                offerId,
                List.of(BookingStatus.ACCEPTED, BookingStatus.REJECTED)
        )).thenReturn(pendingBookings);

        resolveBookingService.rejectOtherBookings(event);

        verify(bookingRepository).saveAll(argThat(bookings -> {
            List<Booking> bookingList = StreamSupport.stream(bookings.spliterator(), false)
                    .toList();
            return bookingList.size() == 2 &&
                    bookingList.stream().allMatch(b -> b.getStatus().equals(BookingStatus.REJECTED));
        }));
    }

    @Test
    void shouldNotRejectAlreadyAcceptedOrRejectedBookings() {
        UUID offerId = UUID.randomUUID();
        OtherBookingsRejectedEvent event = new OtherBookingsRejectedEvent(offerId);

        when(bookingReadRepository.findAllByOfferIdAndStatusNotIn(
                offerId,
                List.of(BookingStatus.ACCEPTED, BookingStatus.REJECTED)
        )).thenReturn(List.of());

        resolveBookingService.rejectOtherBookings(event);

        verify(bookingRepository).saveAll(argThat(bookings ->
                !bookings.iterator().hasNext()
        ));
    }

    @Test
    void shouldHandleEmptyBookingsList() {
        UUID offerId = UUID.randomUUID();
        OtherBookingsRejectedEvent event = new OtherBookingsRejectedEvent(offerId);

        when(bookingReadRepository.findAllByOfferIdAndStatusNotIn(
                offerId,
                List.of(BookingStatus.ACCEPTED, BookingStatus.REJECTED)
        )).thenReturn(List.of());

        resolveBookingService.rejectOtherBookings(event);

        verify(bookingRepository).saveAll(argThat(bookings ->
                !bookings.iterator().hasNext()
        ));
    }

    private BookingEntity getTestBookingEntity() {
        return new BookingEntity(
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                BookingStatus.PENDING,
                BigDecimal.valueOf(60000),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
