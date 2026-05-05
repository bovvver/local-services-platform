package com.github.bovvver.offermanagment.offerexecution;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferExecutionServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferExecutionService offerExecutionService;

    @Test
    void shouldStartExecutionAndReturnInProgressStatus() {
        UUID offerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID executorUuid = UUID.randomUUID();

        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                authorId,
                executorUuid,
                new Location(52.2297, 21.0122),
                Set.of(),
                BigDecimal.valueOf(5000.0),
                OfferStatus.ASSIGNED,
                null,
                null
        );

        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));
        when(currentUser.getId()).thenReturn(UserId.of(executorUuid));

        StartExecutionResponse response = offerExecutionService.startExecution(offerId);

        assertThat(response.offerStatus()).isEqualTo(OfferStatus.IN_PROGRESS);
        assertThat(response.startedAt()).isNotNull();
    }

    @Test
    void shouldThrowWhenOfferDoesNotExist() {
        UUID offerId = UUID.randomUUID();
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> offerExecutionService.startExecution(offerId))
                .isInstanceOf(OfferNotFoundException.class);
    }
}


