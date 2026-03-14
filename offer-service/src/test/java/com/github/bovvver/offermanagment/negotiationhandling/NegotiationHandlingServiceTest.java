package com.github.bovvver.offermanagment.negotiationhandling;

import com.github.bovvver.offermanagment.*;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NegotiationHandlingServiceTest {

    @Mock
    private OfferRepository offerRepository;
    @Mock
    private OfferWriteRepository offerWriteRepository;
    @InjectMocks
    private NegotiationHandlingService negotiationHandlingService;

    @Test
    void shouldThrowExceptionWhenOfferNotFound() {
        UUID nonExistentOfferId = UUID.randomUUID();

        when(offerRepository.findById(nonExistentOfferId))
                .thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            negotiationHandlingService.handleNegotiationStarted(nonExistentOfferId);
        });
    }

    @Test
    void shouldChangeOfferStatusToInNegotiation() {
        UUID offerId = UUID.randomUUID();
        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.OPEN,
                null,
                null
        );

        when(offerRepository.findById(offerId))
                .thenReturn(Optional.of(offerDocument));

        negotiationHandlingService.handleNegotiationStarted(offerId);

        ArgumentCaptor<Offer> offerCaptor = ArgumentCaptor.forClass(Offer.class);
        verify(offerWriteRepository).save(offerCaptor.capture());

        Offer savedOffer = offerCaptor.getValue();
        assertThat(savedOffer.getStatus()).isEqualTo(OfferStatus.IN_NEGOTIATION);
    }
}
