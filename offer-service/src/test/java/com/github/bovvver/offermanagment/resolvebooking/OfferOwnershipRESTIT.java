package com.github.bovvver.offermanagment.resolvebooking;

import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static com.github.bovvver.offermanagment.resolvebooking.OfferOwnershipCheckResponse.*;
import static com.github.bovvver.offermanagment.resolvebooking.OfferOwnershipREST.OFFER_OWNERSHIP_ENDPOINT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfferOwnershipRESTIT extends BaseIntegrationTest {

    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.randomUUID();

    @Autowired
    private OfferRepository offerRepository;

    @BeforeEach
    void setUp() {
        OfferDocument testOffer = new OfferDocument(
                "Test Offer",
                "Test Description",
                USER_ID,
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(1000.0)
        );

        OfferDocument savedOffer = new OfferDocument(
                OFFER_ID,
                testOffer.getTitle(),
                testOffer.getDescription(),
                testOffer.getAuthorId(),
                testOffer.getExecutorId(),
                testOffer.getLocation(),
                testOffer.getServiceCategories(),
                testOffer.getSalary(),
                testOffer.getStatus(),
                testOffer.getCreatedAt(),
                testOffer.getUpdatedAt()
        );
        offerRepository.save(savedOffer);
    }

    @Test
    void shouldReturnOfferNotFound() throws Exception {
        mockMvc.perform(post(OFFER_OWNERSHIP_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", USER_ID.toString())
                        .param("offerId", UUID.randomUUID().toString())
                )
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(OFFER_NOT_FOUND_MESSAGE));
    }

    @Test
    void shouldConfirmOwnership() throws Exception {
        mockMvc.perform(post(OFFER_OWNERSHIP_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", USER_ID.toString())
                        .param("offerId", OFFER_ID.toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(OWNERSHIP_CONFIRMED_MESSAGE));
    }

    @Test
    void shouldDenyOwnership() throws Exception {
        mockMvc.perform(post(OFFER_OWNERSHIP_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("userId", UUID.randomUUID().toString())
                        .param("offerId", OFFER_ID.toString())
                )
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(OWNERSHIP_DENIED_MESSAGE));
    }
}
