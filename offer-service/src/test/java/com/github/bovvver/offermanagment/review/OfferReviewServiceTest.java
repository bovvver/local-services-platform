package com.github.bovvver.offermanagment.review;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.ExecutionDetailsDocument;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
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
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferReviewServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private OfferRepository offerRepository;

    @Mock
    private OutboxService outboxService;

    @InjectMocks
    private OfferReviewService offerReviewService;

    @Test
    void shouldSubmitReviewAndReturnResponse() {
        UUID offerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();

        ExecutionDetailsDocument executionDetails = new ExecutionDetailsDocument(
                "Completed work",
                null,
                LocalDateTime.now(),
                new HashSet<>()
        );
        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                executionDetails,
                authorId,
                executorId,
                Location.of(0.0, 0.0),
                Set.of(ServiceCategory.AUTOMOTIVE),
                BigDecimal.valueOf(100.0),
                OfferStatus.COMPLETED,
                null,
                LocalDateTime.now(),
                null
        );

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));
        when(currentUser.getId()).thenReturn(UserId.of(authorId));
        when(offerRepository.save(any(OfferDocument.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OfferReviewResponse response = offerReviewService.submitReview(offerId, 5);

        assertThat(response.offerId()).isEqualTo(offerId);
        assertThat(response.rating()).isEqualTo(5);

        verify(offerRepository).findById(offerId);
        verify(offerRepository).save(any(OfferDocument.class));
        verify(outboxService).passToOutbox(any(), any(), any());
    }

    @Test
    void shouldThrowWhenOfferNotFound() {
        UUID offerId = UUID.randomUUID();
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerReviewService.submitReview(offerId, 5))
                .isInstanceOf(OfferNotFoundException.class);
    }
}
