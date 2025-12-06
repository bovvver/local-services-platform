package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.contracts.AssignExecutorCommand;
import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.contracts.OtherBookingsRejectedEvent;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferReadRepository;
import com.github.bovvver.offermanagment.OfferWriteRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.bovvver.offermanagment.resolvebooking.ResolveBookingService.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ResolveBookingServiceTest {

    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();
    private static final UUID BOOKING_ID = UUID.randomUUID();

    @Mock
    private KafkaTemplate<String, Object> kafka;

    @Mock
    private OfferReadRepository offerReadRepository;

    @Mock
    private OfferWriteRepository offerWriteRepository;

    @Mock
    private CurrentUser currentUser;

    @InjectMocks
    private ResolveBookingService resolveBookingService;

    @ParameterizedTest
    @MethodSource("provideBookingDecisionScenarios")
    void shouldProcessBookingDecision(
            BookingDecisionStatus status,
            BigDecimal salary,
            String expectedTopic
    ) {
        BookingDecisionRequest request = new BookingDecisionRequest(status, salary);

        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));
        when(offerReadRepository.existsByIdAndAuthorId(OFFER_ID, USER_ID))
                .thenReturn(true);
        when(offerReadRepository.findById(OFFER_ID))
                .thenReturn(Optional.of(createOfferDocument()));

        resolveBookingService.processBookingDecision(
                BOOKING_ID,
                OFFER_ID,
                request
        );

        verify(kafka).send(
                eq(expectedTopic),
                eq(BOOKING_ID.toString()),
                argThat(event -> event instanceof BookingDecisionMadeEvent &&
                        ((BookingDecisionMadeEvent) event).bookingId().equals(BOOKING_ID) &&
                        ((BookingDecisionMadeEvent) event).offerId().equals(OFFER_ID) &&
                        ((BookingDecisionMadeEvent) event).status() == status &&
                        ((BookingDecisionMadeEvent) event).salary() == salary
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenUserIsNotOwner() {
        BookingDecisionRequest request = new BookingDecisionRequest(BookingDecisionStatus.ACCEPTED, null);

        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));
        when(offerReadRepository.existsByIdAndAuthorId(OFFER_ID, USER_ID))
                .thenReturn(false);

        assertThrows(IllegalStateException.class, () ->
                resolveBookingService.processBookingDecision(BOOKING_ID, OFFER_ID, request)
        );
    }

    @Test
    void shouldThrowExceptionWhenSalaryProvidedForAcceptedStatus() {
        BookingDecisionRequest request = new BookingDecisionRequest(BookingDecisionStatus.ACCEPTED, BigDecimal.valueOf(10.0));

        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));
        when(offerReadRepository.existsByIdAndAuthorId(OFFER_ID, USER_ID))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                resolveBookingService.processBookingDecision(BOOKING_ID, OFFER_ID, request)
        );
    }

    @Test
    void shouldThrowExceptionWhenSalaryNotProvidedForNegotiateStatus() {
        BookingDecisionRequest request = new BookingDecisionRequest(BookingDecisionStatus.NEGOTIATE, null);

        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));
        when(offerReadRepository.existsByIdAndAuthorId(OFFER_ID, USER_ID))
                .thenReturn(true);

        assertThrows(IllegalArgumentException.class, () ->
                resolveBookingService.processBookingDecision(BOOKING_ID, OFFER_ID, request)
        );
    }

    @Test
    void shouldThrowExceptionWhenUserNotLoggedIn() {
        BookingDecisionRequest request = new BookingDecisionRequest(BookingDecisionStatus.ACCEPTED, null);

        when(currentUser.getId()).thenReturn(null);

        assertThrows(IllegalStateException.class, () ->
                resolveBookingService.processBookingDecision(BOOKING_ID, OFFER_ID, request)
        );
    }

    @Test
    void shouldCompleteBookingAssignmentAndSaveOffer() {
        AssignExecutorCommand cmd = new AssignExecutorCommand(OFFER_ID, USER_ID);

        when(offerReadRepository.findById(OFFER_ID))
                .thenReturn(Optional.of(createOfferDocument()));

        resolveBookingService.completeBookingAssignment(cmd);

        verify(offerWriteRepository).save(argThat(offer ->
                offer.getId().value().equals(OFFER_ID)
        ));
        verify(kafka).send(
                eq(OFFER_BOOKING_REJECT_OTHERS),
                eq(OFFER_ID.toString()),
                argThat(event -> event instanceof OtherBookingsRejectedEvent &&
                        ((OtherBookingsRejectedEvent) event).offerId().equals(OFFER_ID)
                )
        );
    }

    @Test
    void shouldThrowExceptionWhenOfferNotFoundDuringAssignment() {
        AssignExecutorCommand cmd = new AssignExecutorCommand(OFFER_ID, USER_ID);

        when(offerReadRepository.findById(OFFER_ID))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                resolveBookingService.completeBookingAssignment(cmd)
        );
    }

    @Test
    void shouldThrowExceptionWhenOfferNotFoundDuringDecision() {
        BookingDecisionRequest request = new BookingDecisionRequest(BookingDecisionStatus.ACCEPTED, null);

        when(currentUser.getId()).thenReturn(UserId.of(USER_ID));
        when(offerReadRepository.existsByIdAndAuthorId(OFFER_ID, USER_ID))
                .thenReturn(true);
        when(offerReadRepository.findById(OFFER_ID))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () ->
                resolveBookingService.processBookingDecision(BOOKING_ID, OFFER_ID, request)
        );
    }

    private static Stream<Arguments> provideBookingDecisionScenarios() {
        return Stream.of(
                Arguments.of(BookingDecisionStatus.ACCEPTED, null, OFFER_BOOKING_DECISION),
                Arguments.of(BookingDecisionStatus.NEGOTIATE, BigDecimal.valueOf(10.0), OFFER_BOOKING_NEGOTIATE)
        );
    }

    private OfferDocument createOfferDocument() {
        return new OfferDocument(
                OFFER_ID,
                "Test Title",
                "Test Description",
                USER_ID,
                null,
                Set.of(),
                new Location(0.0, 0.0),
                Set.of(),
                BigDecimal.valueOf(100.0),
                OfferStatus.OPEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
