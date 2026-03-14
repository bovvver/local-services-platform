package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.OfferWriteRepository;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import com.github.bovvver.offermanagment.vo.Location;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ResolveBookingServiceTest {

    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private OfferWriteRepository offerWriteRepository;
    @Mock
    private OutboxService outboxService;
    @InjectMocks
    private ResolveBookingService resolveBookingService;

    @Test
    void shouldCompleteSuccessfullyWhenOfferExists() {
        OfferDocument offerDocument = createOfferDocument();

        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(offerDocument));

        resolveBookingService.completeBookingAssignment(OFFER_ID, USER_ID);

        verify(offerWriteRepository).save(any(Offer.class));
        verify(outboxService).passToOutbox(anyList(), eq(OFFER_ID), eq("Offer"));
    }

    @Test
    void completeBookingAssignmentShouldThrowExceptionWhenOfferDoesNotExist() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> resolveBookingService.completeBookingAssignment(OFFER_ID, USER_ID));

        verifyNoInteractions(offerWriteRepository, outboxService);
    }

    private OfferDocument createOfferDocument() {
        return new OfferDocument(
                OFFER_ID,
                "Test Offer",
                "Test Description",
                USER_ID,
                null,
                new Location(
                        40.7128,
                        -74.0060
                ),
                null,
                new BigDecimal("1000.0"),
                null,
                null,
                null
        );
    }
}