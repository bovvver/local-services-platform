package com.github.bovvver.offermanagment.workproofupload;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CompletionProcessingServiceTest {

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private CompletionProcessingService completionProcessingService;

    @Test
    void shouldRequestCompletionAndReturnResponse() {
        UUID offerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();
        String description = "Job done";
        List<String> proofUrls = List.of("https://proofs.local/1", "https://proofs.local/2");

        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                null,
                authorId,
                executorId,
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.IN_PROGRESS,
                new HashSet<>(),
                LocalDateTime.now().minusDays(1),
                null
        );

        CompletionRequest request = new CompletionRequest(description, proofUrls, offerId);

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));
        when(offerRepository.save(any(OfferDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0, OfferDocument.class));

        OfferExecutionResponse response = completionProcessingService.sendCompletionRequest(request);

        assertThat(response.offerId()).isEqualTo(offerId);
        assertThat(response.status()).isEqualTo(OfferStatus.COMPLETED_REQUESTED);
        assertThat(response.completionDescription()).isEqualTo(description);
        assertThat(response.proofs()).hasSize(2);
        assertThat(response.proofs().stream().map(WorkProof::url))
                .containsExactlyInAnyOrderElementsOf(proofUrls);
        assertThat(response.completionRequestedAt()).isNotNull();

        verify(offerRepository).save(any(OfferDocument.class));
    }

    @Test
    void shouldThrowWhenOfferDoesNotExist() {
        UUID offerId = UUID.randomUUID();
        CompletionRequest request = new CompletionRequest("Done", List.of("proof"), offerId);

        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> completionProcessingService.sendCompletionRequest(request))
                .isInstanceOf(OfferNotFoundException.class);
    }
}

