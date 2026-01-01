package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.contracts.BookOfferCommand;
import com.github.bovvver.contracts.BookingDraftRejectedEvent;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferReadRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferAvailabilityServiceTest {

    @Mock
    private KafkaTemplate<String, BookingDraftRejectedEvent> kafka;

    @Mock
    private OfferReadRepository offerReadRepository;

    @InjectMocks
    private OfferAvailabilityService listener;

    private UUID offerId;
    private UUID userId;
    private UUID bookingId;
    private BookOfferCommand command;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        userId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
        command = new BookOfferCommand(offerId, userId, bookingId);
    }

    @Test
    void shouldPublishDomainEventWhenOfferExists() {
        OfferDocument offerDocument = createOfferDocument();

        when(offerReadRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));

        listener.checkOfferAvailability(command);

        verifyNoInteractions(kafka);
    }

    @Test
    void shouldSendRejectionEventWhenOfferNotFound() {
        when(offerReadRepository.findById(offerId)).thenReturn(Optional.empty());

        listener.checkOfferAvailability(command);

        verify(kafka).send(
                eq(OfferAvailabilityService.BOOKING_OFFER_AVAILABILITY_REJECTED),
                eq(bookingId.toString()),
                argThat(event ->
                        event.error().equals("NOT_FOUND") &&
                                event.offerId().equals(offerId) &&
                                event.userId().equals(userId) &&
                                event.bookingId().equals(bookingId) &&
                                event.reason().contains("Offer with id")
                )
        );
        verifyNoInteractions(domainEventPublisher);
    }

    private OfferDocument createOfferDocument() {
        return new OfferDocument(
                offerId,
                "Test Title",
                "Test Description",
                userId,
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

