package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class OfferTransportationMapperTest {

    @Test
    void toCreateOfferCommand_mapsAllFields_fromValidRequest() {
        CreateOfferRequest request = new CreateOfferRequest(
                "Sample title",
                "Sample description",
                BigDecimal.valueOf(1500.0),
                new LocationDTO(12.345, 67.89),
                Set.of("CLEANING", "REPAIR")
        );

        CreateOfferCommand command = OfferTransportationMapper.toCreateOfferCommand(request);

        assertThat(command.title()).isEqualTo("Sample title");
        assertThat(command.description()).isEqualTo("Sample description");
        assertThat(command.salary()).isEqualTo(BigDecimal.valueOf(1500.0));
        assertThat(command.location()).isNotNull();
        assertThat(command.location().latitude()).isEqualTo(12.345);
        assertThat(command.location().longitude()).isEqualTo(67.89);
        assertThat(command.serviceCategories()).isEqualTo(Set.of("CLEANING", "REPAIR"));
    }

    @Test
    void toCreateOfferCommand_throwsIllegalArgumentException_forOutOfRangeLatitude() {
        CreateOfferRequest request = new CreateOfferRequest(
                "Title",
                "Desc",
                BigDecimal.valueOf(0.0),
                new LocationDTO(-91.0, 0.0),
                Set.of("CLEANING")
        );

        assertThrows(IllegalArgumentException.class, () -> OfferTransportationMapper.toCreateOfferCommand(request));
    }

    @Test
    void toCreateOfferCommand_throwsIllegalArgumentException_forOutOfRangeLongitude() {
        CreateOfferRequest request = new CreateOfferRequest(
                "Title",
                "Desc",
                BigDecimal.valueOf(0.0),
                new LocationDTO(0.0, 181.0),
                Set.of("CLEANING")
        );

        assertThrows(IllegalArgumentException.class, () -> OfferTransportationMapper.toCreateOfferCommand(request));
    }

    @Test
    void toCreateOfferCommand_throwsNullPointerException_whenLocationIsNull() {
        CreateOfferRequest request = new CreateOfferRequest(
                "Title",
                "Desc",
                BigDecimal.valueOf(0.0),
                null,
                Set.of("CLEANING")
        );

        assertThrows(NullPointerException.class, () -> OfferTransportationMapper.toCreateOfferCommand(request));
    }

    @Test
    void toOfferCreatedResponse_mapsAllFields_fromOffer() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusHours(1);
        Location location = new Location(50.123, 19.456);
        Set<ServiceCategory> categories = EnumSet.of(ServiceCategory.CLEANING, ServiceCategory.REPAIR);

        Offer offer = mock(Offer.class, Mockito.RETURNS_DEEP_STUBS);
        when(offer.getId().value()).thenReturn(id);
        when(offer.getTitle().value()).thenReturn("Mapped title");
        when(offer.getDescription().value()).thenReturn("Mapped description");
        when(offer.getStatus()).thenReturn(OfferStatus.OPEN);
        when(offer.getLocation()).thenReturn(location);
        when(offer.getServiceCategories()).thenReturn(categories);
        when(offer.getSalary().value()).thenReturn(BigDecimal.valueOf(999.99));
        when(offer.getCreatedAt()).thenReturn(createdAt);

        OfferCreatedResponse response = OfferTransportationMapper.toOfferCreatedResponse(offer);

        assertThat(response.offerId()).isEqualTo(id);
        assertThat(response.title()).isEqualTo("Mapped title");
        assertThat(response.description()).isEqualTo("Mapped description");
        assertThat(response.status()).isEqualTo("OPEN");
        assertThat(response.location()).isNotNull();
        assertThat(response.location().latitude()).isEqualTo(50.123);
        assertThat(response.location().longitude()).isEqualTo(19.456);
        assertThat(response.serviceCategories()).isEqualTo(Set.of("CLEANING", "REPAIR"));
        assertThat(response.salary()).isEqualTo(BigDecimal.valueOf(999.99));
        assertThat(response.createdAt()).isEqualTo(createdAt);
    }

    @Test
    void toOfferCreatedResponse_mapsEmptyServiceCategories_toEmptySet() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Location location = new Location(0.0, 0.0);

        Offer offer = mock(Offer.class, Mockito.RETURNS_DEEP_STUBS);
        when(offer.getId().value()).thenReturn(id);
        when(offer.getTitle().value()).thenReturn("T");
        when(offer.getDescription().value()).thenReturn("D");
        when(offer.getStatus()).thenReturn(OfferStatus.ASSIGNED);
        when(offer.getLocation()).thenReturn(location);
        when(offer.getServiceCategories()).thenReturn(Set.of());
        when(offer.getSalary().value()).thenReturn(BigDecimal.valueOf(0.0));
        when(offer.getCreatedAt()).thenReturn(createdAt);

        OfferCreatedResponse response = OfferTransportationMapper.toOfferCreatedResponse(offer);

        assertThat(response.serviceCategories()).isNotNull();
        assertThat(response.serviceCategories()).isEmpty();
        assertThat(response.status()).isEqualTo("ASSIGNED");
    }
}
