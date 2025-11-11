package com.github.bovvver.offermanagment.offercreation;

import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class OfferTransportationMapperTest {

    @Test
    void toCreateOfferCommand_mapsAllFields_fromValidRequest() {
        CreateOfferRequest request = new CreateOfferRequest(
                "Sample title",
                "Sample description",
                1500.0,
                new LocationDTO(12.345, 67.89),
                Set.of("CLEANING", "REPAIR")
        );

        CreateOfferCommand command = OfferTransportationMapper.toCreateOfferCommand(request);

        assertEquals("Sample title", command.title());
        assertEquals("Sample description", command.description());
        assertEquals(1500.0, command.salary(), 1e-9);
        assertNotNull(command.location());
        assertEquals(12.345, command.location().latitude(), 1e-9);
        assertEquals(67.89, command.location().longitude(), 1e-9);
        assertEquals(Set.of("CLEANING", "REPAIR"), command.serviceCategories());
    }

    @Test
    void toCreateOfferCommand_throwsIllegalArgumentException_forOutOfRangeLatitude() {
        CreateOfferRequest request = new CreateOfferRequest(
                "Title",
                "Desc",
                0.0,
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
                0.0,
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
                0.0,
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

        Offer offer = Mockito.mock(Offer.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(offer.getId().value()).thenReturn(id);
        Mockito.when(offer.getTitle().value()).thenReturn("Mapped title");
        Mockito.when(offer.getDescription().value()).thenReturn("Mapped description");
        Mockito.when(offer.getStatus()).thenReturn(OfferStatus.OPEN);
        Mockito.when(offer.getLocation()).thenReturn(location);
        Mockito.when(offer.getServiceCategories()).thenReturn(categories);
        Mockito.when(offer.getSalary().value()).thenReturn(999.99);
        Mockito.when(offer.getCreatedAt()).thenReturn(createdAt);

        OfferCreatedResponse response = OfferTransportationMapper.toOfferCreatedResponse(offer);

        assertEquals(id, response.offerId());
        assertEquals("Mapped title", response.title());
        assertEquals("Mapped description", response.description());
        assertEquals("OPEN", response.status());
        assertNotNull(response.location());
        assertEquals(50.123, response.location().latitude(), 1e-9);
        assertEquals(19.456, response.location().longitude(), 1e-9);
        assertEquals(Set.of("CLEANING", "REPAIR"), response.serviceCategories());
        assertEquals(999.99, response.salary(), 1e-9);
        assertEquals(createdAt, response.createdAt());
    }

    @Test
    void toOfferCreatedResponse_mapsEmptyServiceCategories_toEmptySet() {
        UUID id = UUID.randomUUID();
        LocalDateTime createdAt = LocalDateTime.now().minusDays(1);
        Location location = new Location(0.0, 0.0);

        Offer offer = Mockito.mock(Offer.class, Mockito.RETURNS_DEEP_STUBS);
        Mockito.when(offer.getId().value()).thenReturn(id);
        Mockito.when(offer.getTitle().value()).thenReturn("T");
        Mockito.when(offer.getDescription().value()).thenReturn("D");
        Mockito.when(offer.getStatus()).thenReturn(OfferStatus.ASSIGNED);
        Mockito.when(offer.getLocation()).thenReturn(location);
        Mockito.when(offer.getServiceCategories()).thenReturn(Set.of());
        Mockito.when(offer.getSalary().value()).thenReturn(0.0);
        Mockito.when(offer.getCreatedAt()).thenReturn(createdAt);

        OfferCreatedResponse response = OfferTransportationMapper.toOfferCreatedResponse(offer);

        assertNotNull(response.serviceCategories());
        assertTrue(response.serviceCategories().isEmpty());
        assertEquals("ASSIGNED", response.status());
    }
}
