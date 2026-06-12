package com.github.bovvver.offermanagment.offercancellation;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.OfferWriteRepository;
import com.github.bovvver.offermanagment.outbox.OutboxService;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import com.github.bovvver.offermanagment.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OfferCancellationServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OfferWriteRepository offerWriteRepository;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private OfferCancellationService offerCancellationService;

    @Test
    void shouldCancelOfferSuccessfully() {
        UUID offerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        OfferDocument offerDocument = createOfferDocument(offerId, authorId);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));
        when(currentUser.getId()).thenReturn(UserId.of(authorId));

        OfferCancellationResponse response = offerCancellationService.cancelOffer(offerId);

        verify(offerWriteRepository).save(any());
        verify(outboxService).passToOutbox(
                any(),
                eq(offerId),
                eq("Offer")
        );

        assertThat(response.offerId()).isEqualTo(offerId);
        assertThat(response.status()).isEqualTo(OfferStatus.CANCELLED);
    }

    @Test
    void shouldThrowExceptionWhenOfferNotFound() {
        UUID offerId = UUID.randomUUID();

        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerCancellationService.cancelOffer(offerId))
                .isInstanceOf(OfferNotFoundException.class);

        verifyNoInteractions(offerWriteRepository);
        verifyNoInteractions(outboxService);
    }

    private OfferDocument createOfferDocument(UUID offerId, UUID authorId) {
        return new OfferDocument(
                offerId,
                "Test Offer",
                "This is a test offer.",
                null,
                authorId,
                UUID.randomUUID(),
                new Location(10, 10),
                Set.of(ServiceCategory.AUTOMOTIVE),
                BigDecimal.TEN,
                OfferStatus.OPEN,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
