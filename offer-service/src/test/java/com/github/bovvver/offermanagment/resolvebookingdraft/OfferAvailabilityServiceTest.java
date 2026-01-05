package com.github.bovvver.offermanagment.resolvebookingdraft;

import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferReadRepository;
import com.github.bovvver.offermanagment.OfferWriteRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OfferAvailabilityServiceTest {

    @Mock
    private OfferReadRepository offerReadRepository;

    @Mock
    private OfferWriteRepository offerWriteRepository;

    @InjectMocks
    private OfferAvailabilityService offerAvailabilityService;

    private UUID offerId;
    private UUID userId;
    private UUID bookingId;

    @BeforeEach
    void setUp() {
        offerId = UUID.randomUUID();
        userId = UUID.randomUUID();
        bookingId = UUID.randomUUID();
    }

    @Test
    void shouldReturnOfferAvailableStatus() {
        OfferDocument offerDocument = createOfferDocument(OfferStatus.OPEN);

        when(offerReadRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));

        OfferAvailabilityCheckResponse response = offerAvailabilityService.checkOfferAvailability(offerId, userId, bookingId);

        assertThat(response.httpStatus()).isEqualTo(HttpStatus.OK);
        assertThat(response.isAvailable()).isTrue();
    }

    @Test
    void shouldReturnOfferUnavailableStatus() {
        OfferDocument offerDocument = createOfferDocument(OfferStatus.ASSIGNED);

        when(offerReadRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));

        OfferAvailabilityCheckResponse response = offerAvailabilityService.checkOfferAvailability(offerId, userId, bookingId);

        assertThat(response.httpStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.isAvailable()).isFalse();
    }

    @Test
    void shouldReturnOfferNotFoundStatus() {
        when(offerReadRepository.findById(offerId)).thenReturn(Optional.empty());

        OfferAvailabilityCheckResponse response = offerAvailabilityService.checkOfferAvailability(offerId, userId, bookingId);

        assertThat(response.httpStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.isAvailable()).isFalse();
    }

    private OfferDocument createOfferDocument(OfferStatus status) {
        return new OfferDocument(
                offerId,
                "Test Title",
                "Test Description",
                userId,
                null,
                Set.of(),
                new Location(0.0, 0.0),
                Set.of(),
                BigDecimal.valueOf(100.0),
                status,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }
}
