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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingDraftCreationServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private BookingDraftWriteRepository bookingDraftWriteRepository;

    @Mock
    private BookingDraftReadRepository bookingDraftReadRepository;

    @Mock
    private BookingReadRepository bookingReadRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private OfferAvailabilityClient offerAvailabilityClient;

    @Mock
    private KafkaTemplate<String, BookOfferCommand> kafka;

    @InjectMocks
    private BookingDraftCreationService bookingDraftCreationService;

    private final UUID offerId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @Test
    void shouldCreateDraftWhenNoExistingBookingsOrDrafts() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(10_000.0));

        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(offerId, userId))
                .thenReturn(false);
        when(bookingDraftReadRepository.existsByOfferIdAndUserId(offerId, userId))
                .thenReturn(false);
        when(offerAvailabilityClient.isOfferAvailable(any(), eq(offerId), eq(userId)))
                .thenReturn(true);

        bookingDraftCreationService.processBookingCreation(request);

        ArgumentCaptor<BookingDraft> captor = ArgumentCaptor.forClass(BookingDraft.class);
        verify(bookingDraftWriteRepository).save(captor.capture());
        BookingDraft savedDraft = captor.getValue();

        assertThat(savedDraft.getOfferId().value()).isEqualTo(offerId);
        assertThat(savedDraft.getUserId().value()).isEqualTo(userId);
        assertThat(savedDraft.getSalary()).isEqualTo(Salary.of(10_000.0));
        assertThat(savedDraft.getBookingId().value()).isNotNull();
    }

    @Test
    void shouldThrowConflictWhenFinalBookingAlreadyExistsForOfferAndUser() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(10_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(offerId, userId)).thenReturn(true);

        assertThatThrownBy(() -> bookingDraftCreationService.processBookingCreation(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verify(bookingDraftReadRepository, never()).existsByOfferIdAndUserId(any(), any());
        verifyNoInteractions(bookingDraftWriteRepository);
    }

    @Test
    void shouldThrowConflictWhenDraftBookingAlreadyExistsForOfferAndUser() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(10_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(offerId, userId)).thenReturn(false);
        when(bookingDraftReadRepository.existsByOfferIdAndUserId(offerId, userId)).thenReturn(true);

        assertThatThrownBy(() -> bookingDraftCreationService.processBookingCreation(request))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.CONFLICT);

        verifyNoInteractions(bookingDraftWriteRepository);
    }

    @Test
    void shouldCheckConflictsUsingCurrentUserId() {
        BookOfferRequest request = new BookOfferRequest(offerId, BigDecimal.valueOf(5_000.0));
        when(currentUser.getId()).thenReturn(UserId.of(userId));
        when(bookingReadRepository.existsByOfferIdAndUserId(any(), any())).thenReturn(false);
        when(bookingDraftReadRepository.existsByOfferIdAndUserId(any(), any())).thenReturn(false);

        bookingDraftCreationService.processBookingCreation(request);

        ArgumentCaptor<UUID> userIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(bookingReadRepository).existsByOfferIdAndUserId(eq(offerId), userIdCaptor.capture());
        assertThat(userIdCaptor.getValue()).isEqualTo(userId);

        verify(bookingDraftReadRepository).existsByOfferIdAndUserId(eq(offerId), eq(userId));
    }

    @Test
    void shouldDeleteDraftBookingById() {
        UUID bookingId = UUID.randomUUID();

        bookingDraftCreationService.deleteDraftBooking(bookingId);

        verify(bookingDraftWriteRepository).delete(BookingId.of(bookingId));
        verifyNoMoreInteractions(bookingDraftWriteRepository);
        verifyNoInteractions(bookingDraftReadRepository, bookingReadRepository, bookingRepository, kafka);
    }

    @Test
    void shouldCreateBookingFromAcceptedDraftEventAndDeleteDraft() {
        UUID bookingId = UUID.randomUUID();
        UUID acceptedUserId = UUID.randomUUID();
        UUID eventOfferId = UUID.randomUUID();

        when(bookingDraftReadRepository.findSalaryByBookingId(bookingId)).thenReturn(12_345.0);

        bookingDraftCreationService.createBooking(bookingId, acceptedUserId, eventOfferId);

        verify(bookingDraftReadRepository).findSalaryByBookingId(bookingId);
        verify(bookingDraftWriteRepository).delete(BookingId.of(bookingId));

        ArgumentCaptor<Booking> bookingCaptor = ArgumentCaptor.forClass(Booking.class);
        verify(bookingRepository).save(bookingCaptor.capture());
        Booking savedBooking = bookingCaptor.getValue();

        assertThat(savedBooking.getUserId()).isEqualTo(UserId.of(acceptedUserId));
        assertThat(savedBooking.getOfferId()).isEqualTo(OfferId.of(eventOfferId));

        InOrder inOrder = inOrder(bookingDraftReadRepository, bookingDraftWriteRepository, bookingRepository);
        inOrder.verify(bookingDraftReadRepository).findSalaryByBookingId(bookingId);
        inOrder.verify(bookingDraftWriteRepository).delete(BookingId.of(bookingId));
        inOrder.verify(bookingRepository).save(any(Booking.class));
    }

    @Test
    void shouldFailWhenSalaryForDraftIsNull() {
        UUID bookingId = UUID.randomUUID();
        UUID acceptedUserId = UUID.randomUUID();
        UUID eventOfferId = UUID.randomUUID();

        when(bookingDraftReadRepository.findSalaryByBookingId(bookingId)).thenReturn(null);

        assertThatThrownBy(() -> bookingDraftCreationService.createBooking(bookingId, acceptedUserId, eventOfferId));
    }
}
