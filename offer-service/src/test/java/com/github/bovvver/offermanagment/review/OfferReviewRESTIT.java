package com.github.bovvver.offermanagment.review;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.offermanagment.ExecutionDetailsDocument;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfferReviewRESTIT extends BaseIntegrationTest {

    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID AUTHOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID EXECUTOR_ID = UUID.randomUUID();

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        offerRepository.deleteAll();
    }

    @Test
    void shouldSubmitReviewSuccessfully() throws Exception {
        OfferDocument offer = new OfferDocument(
                OFFER_ID,
                "Test Offer",
                "Test Description",
                new ExecutionDetailsDocument(null, null, null, new HashSet<>()),
                AUTHOR_ID,
                EXECUTOR_ID,
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(1000.0),
                OfferStatus.COMPLETED,
                null,
                null,
                null
        );
        offerRepository.save(offer);

        SubmitReviewRequest request = new SubmitReviewRequest(5);

        mockMvc.perform(post("/offers/{offerId}/review", OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(OFFER_ID.toString()))
                .andExpect(jsonPath("$.rating").value(5));

        OfferDocument updatedOffer = offerRepository.findById(OFFER_ID).orElseThrow();
        assertThat(updatedOffer.getRating()).isEqualTo(5);
    }

    @Test
    void shouldReturnBadRequestWhenRatingIsOutOfRange() throws Exception {
        SubmitReviewRequest request = new SubmitReviewRequest(6);

        mockMvc.perform(post("/offers/{offerId}/review", OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
