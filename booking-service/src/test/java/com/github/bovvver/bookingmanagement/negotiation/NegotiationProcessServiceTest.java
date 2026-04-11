package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.infrastructure.BookingNotFoundException;
import com.github.bovvver.bookingmanagement.infrastructure.BookingOwnershipException;
import com.github.bovvver.bookingmanagement.infrastructure.InvalidBookingStatusException;
import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import com.github.bovvver.bookingmanagement.vo.NegotiationStatus;
import com.github.bovvver.bookingmanagement.vo.UserId;
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

import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NegotiationProcessServiceTest {

    private static final UUID BOOKING_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private CurrentUser currentUser;

    @Mock
    private BookingReadRepository bookingReadRepository;

    @Mock
    private BookingRepository bookingWriteRepository;

    @InjectMocks
    private NegotiationProcessService negotiationProcessService;

    @Test
    void shouldMakeProposalSuccessfully() {
        BookingEntity booking = createBookingEntity(USER_ID, BookingStatus.IN_NEGOTIATION);

        when(bookingReadRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));

        negotiationProcessService.makeProposal(BOOKING_ID, new NegotiationProposalRequest(new BigDecimal(50000)));
        verify(bookingWriteRepository).save(any(Booking.class));
    }

    @Test
    void shouldThrowExceptionWhenBookingNotFound() {
        assertThrows(BookingNotFoundException.class, () -> {
            when(bookingReadRepository.findById(BOOKING_ID)).thenReturn(Optional.empty());
            negotiationProcessService.makeProposal(BOOKING_ID, new NegotiationProposalRequest(new BigDecimal(50000)));
        });
    }

    @Test
    void shouldThrowExceptionWhenCurrentUserNotInvolvedInBooking() {
        BookingEntity booking = createBookingEntity(UUID.randomUUID(), BookingStatus.IN_NEGOTIATION);

        when(bookingReadRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));

        assertThrows(BookingOwnershipException.class, () ->
                negotiationProcessService.makeProposal(BOOKING_ID, new NegotiationProposalRequest(new BigDecimal(50000)))
        );
    }

    @Test
    void shouldThrowExceptionWhenBookingNotInNegotiation() {
        BookingEntity booking = createBookingEntity(USER_ID, BookingStatus.PENDING);

        when(bookingReadRepository.findById(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));

        assertThrows(InvalidBookingStatusException.class, () ->
                negotiationProcessService.makeProposal(BOOKING_ID, new NegotiationProposalRequest(new BigDecimal(50000)))
        );
    }

    private BookingEntity createBookingEntity(UUID userId, BookingStatus status) {
        NegotiationEntity negotiation = new NegotiationEntity();
        negotiation.setId(UUID.randomUUID());
        negotiation.setOfferAuthorId(UUID.randomUUID());
        negotiation.setPositions(List.of());
        negotiation.setStatus(NegotiationStatus.ACTIVE);

        return new BookingEntity(
                BOOKING_ID,
                userId,
                UUID.randomUUID(),
                negotiation,
                status,
                new BigDecimal(1000),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
