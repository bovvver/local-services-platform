package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.*;
import com.github.bovvver.offermanagment.workproofupload.WorkProof;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OfferMapperTest {

    @Test
    void toDomainHandlesNullExecutorId() {
        OfferDocument document = new OfferDocument(
                "Sample Title",
                "Sample Description",
                UUID.randomUUID(),
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0)
        );

        Offer offer = OfferMapper.toDomain(document);

        assertThat(offer).isNotNull();
        assertThat(offer.getExecutorId()).isNull();
    }

    @Test
    void toDocumentHandlesNullExecutorId() {
        Offer offer = Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                UserId.of(UUID.randomUUID()),
                Location.of(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                Salary.of(5000.0)
        );

        OfferDocument document = OfferMapper.toDocument(offer);

        assertThat(document).isNotNull();
        assertThat(document.getExecutorId()).isNull();
    }

    @Test
    void toDomainMapsExecutorIdWhenPresent() {
        UUID executorUuid = UUID.randomUUID();
        OfferDocument document = new OfferDocument(
                UUID.randomUUID(),
                "Sample Title",
                "Sample Description",
                null,
                UUID.randomUUID(),
                executorUuid,
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.ASSIGNED,
                null,
                null,
                null
        );

        Offer offer = OfferMapper.toDomain(document);

        assertThat(offer).isNotNull();
        assertThat(offer.getExecutorId()).isNotNull();
        assertThat(offer.getExecutorId().value()).isEqualTo(executorUuid);
    }

    @Test
    void toDocumentMapsExecutorIdWhenPresent() {
        UUID executorUuid = UUID.randomUUID();
        OfferDocument source = new OfferDocument(
                UUID.randomUUID(),
                "Sample Title",
                "Sample Description",
                null,
                UUID.randomUUID(),
                executorUuid,
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.ASSIGNED,
                null,
                null,
                null
        );

        Offer offer = OfferMapper.toDomain(source);
        OfferDocument document = OfferMapper.toDocument(offer);

        assertThat(document).isNotNull();
        assertThat(document.getExecutorId()).isEqualTo(executorUuid);
    }

    @Test
    void toDomainMapsCompletionDescriptionWhenPresent() {
        Set<WorkProof> workProofs = new HashSet<>(Set.of(
                new WorkProof("https://example.com/proof1", LocalDateTime.now()),
                new WorkProof("https://example.com/proof2", LocalDateTime.now())
        ));
        OfferDocument document = new OfferDocument(
                UUID.randomUUID(),
                "Sample Title",
                "Sample Description",
                "Completion description",
                UUID.randomUUID(),
                null,
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.IN_PROGRESS,
                workProofs,
                null,
                null
        );

        Offer offer = OfferMapper.toDomain(document);

        assertThat(offer.getCompletionDescription()).isNotNull();
        assertThat(offer.getCompletionDescription().value()).isEqualTo("Completion description");
        assertThat(offer.getWorkProofs()).hasSize(2);
        assertThat(offer.getWorkProofs().stream().map(WorkProof::url))
                .containsExactlyInAnyOrder("https://example.com/proof1", "https://example.com/proof2");
    }

    @Test
    void toDocumentMapsCompletionDescriptionWhenPresent() {
        Offer offer = Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                UserId.of(UUID.randomUUID()),
                Location.of(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                Salary.of(5000.0)
        );
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);
        offer.startExecution(executor);
        offer.requestCompletion("Completion description", List.of("https://example.com/proof1", "https://example.com/proof2"), executor);

        OfferDocument document = OfferMapper.toDocument(offer);

        assertThat(document.getCompletionDescription()).isEqualTo("Completion description");
        assertThat(document.getWorkProofs()).hasSize(2);
        assertThat(document.getWorkProofs().stream().map(WorkProof::url))
                .containsExactlyInAnyOrder("https://example.com/proof1", "https://example.com/proof2");
    }
}