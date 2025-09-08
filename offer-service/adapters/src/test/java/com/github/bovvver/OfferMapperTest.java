package com.github.bovvver;

import com.github.bovvver.vo.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class OfferMapperTest {

    @Test
    void toDomainMapsAllFieldsCorrectly() {
        OfferDocument document = new OfferDocument(
                "Sample Title",
                "Sample Description",
                UUID.randomUUID(),
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES, ServiceCategory.CLEANING),
                5000.0
        );

        Offer offer = OfferMapper.toDomain(document);

        assertThat(offer.getTitle().value()).isEqualTo(document.getTitle());
        assertThat(offer.getDescription().value()).isEqualTo(document.getDescription());
        assertThat(offer.getAuthorId().value()).isEqualTo(document.getAuthorId());
        assertThat(offer.getLocation().latitude()).isEqualTo(document.getLocation().latitude());
        assertThat(offer.getLocation().longitude()).isEqualTo(document.getLocation().longitude());
        assertThat(offer.getServiceCategories()).isEqualTo(document.getServiceCategories());
        assertThat(offer.getSalary().value()).isEqualTo(document.getSalary());
    }

    @Test
    void toDocumentMapsAllFieldsCorrectly() {
        Offer offer = Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                UserId.of(UUID.randomUUID()),
                Location.of(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES, ServiceCategory.CLEANING),
                Salary.of(5000.0)
        );

        OfferDocument document = OfferMapper.toDocument(offer);

        assertThat(document.getTitle()).isEqualTo(offer.getTitle().value());
        assertThat(document.getDescription()).isEqualTo(offer.getDescription().value());
        assertThat(document.getAuthorId()).isEqualTo(offer.getAuthorId().value());
        assertThat(document.getLocation().latitude()).isEqualTo(offer.getLocation().latitude());
        assertThat(document.getLocation().longitude()).isEqualTo(offer.getLocation().longitude());
        assertThat(document.getServiceCategories()).isEqualTo(offer.getServiceCategories());
        assertThat(document.getSalary()).isEqualTo(offer.getSalary().value());
    }

    @Test
    void toDomainHandlesNullServiceCategories() {
        OfferDocument document = new OfferDocument(
                "Sample Title",
                "Sample Description",
                UUID.randomUUID(),
                new Location(52.2297, 21.0122),
                null,
                5000.0
        );

        Offer offer = OfferMapper.toDomain(document);

        assertThat(offer).isNotNull();
        assertThat(offer.getServiceCategories()).isNull();
    }

    @Test
    void toDocumentHandlesEmptyServiceCategories() {
        Offer offer = Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                UserId.of(UUID.randomUUID()),
                Location.of(52.2297, 21.0122),
                Set.of(),
                Salary.of(5000.0)
        );

        OfferDocument document = OfferMapper.toDocument(offer);

        assertThat(document).isNotNull();
        assertThat(document.getServiceCategories()).isEmpty();
    }
}
