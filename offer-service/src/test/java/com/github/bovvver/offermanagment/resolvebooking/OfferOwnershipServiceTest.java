package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class OfferOwnershipServiceTest {

    private final static UUID OFFER_ID = UUID.randomUUID();
    private final static UUID USER_ID = UUID.randomUUID();

    private static OfferDocument offerDocument;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private OfferOwnershipService offerOwnershipService;

    @BeforeAll
    static void setUp() {
        offerDocument = createOfferDocument();
    }

    @Test
    void shouldReturnOwnershipConfirmedWhenOfferIsOwnedByUser() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(offerDocument));

        OfferOwnershipCheckResponse response = offerOwnershipService.checkOfferOwnership(USER_ID, OFFER_ID);

        assertThat(response.isOwner()).isTrue();
        assertThat(response.message()).isEqualTo("User is the owner of this offer.");
        assertThat(response.httpStatusCode()).isEqualTo(200);
        assertThat(response.checkedAt()).isNotNull();
    }

    @Test
    void shouldReturnOwnershipDeniedWhenOfferIsNotOwnedByUser() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.of(offerDocument));

        OfferOwnershipCheckResponse response = offerOwnershipService.checkOfferOwnership(UUID.randomUUID(), OFFER_ID);

        assertThat(response.isOwner()).isFalse();
        assertThat(response.message()).isEqualTo("User isn't the owner of this offer.");
        assertThat(response.httpStatusCode()).isEqualTo(403);
        assertThat(response.checkedAt()).isNotNull();
    }

    @Test
    void shouldReturnNotFoundWhenOfferDoesNotExist() {
        when(offerRepository.findById(OFFER_ID)).thenReturn(Optional.empty());

        OfferOwnershipCheckResponse response = offerOwnershipService.checkOfferOwnership(USER_ID, OFFER_ID);
        assertThat(response.isOwner()).isFalse();
        assertThat(response.message()).isEqualTo("Offer not found.");
        assertThat(response.httpStatusCode()).isEqualTo(404);
        assertThat(response.checkedAt()).isNotNull();
    }

    private static OfferDocument createOfferDocument() {
        return new OfferDocument(
                OFFER_ID,
                "Test Offer",
                "Test Description",
                null,
                USER_ID,
                null,
                new Location(
                        40.7128,
                        -74.0060
                ),
                null,
                new BigDecimal("1000.0"),
                null,
                null,
                null,
                null
        );
    }
}
