package com.github.bovvver;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.vo.*;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
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
                UUID.randomUUID(),
                executorUuid,
                Set.of(),
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.ACTIVE,
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
                UUID.randomUUID(),
                executorUuid,
                Set.of(),
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.ACTIVE,
                null,
                null
        );

        Offer offer = OfferMapper.toDomain(source);
        OfferDocument document = OfferMapper.toDocument(offer);

        assertThat(document).isNotNull();
        assertThat(document.getExecutorId()).isEqualTo(executorUuid);
    }

}