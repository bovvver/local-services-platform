package com.github.bovvver.offermanagment.offerexecution;

import com.github.bovvver.BaseIntegrationTest;
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
import java.util.Set;
import java.util.UUID;

import static com.github.bovvver.offermanagment.offerexecution.OfferExecutionREST.START_EXECUTION_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfferExecutionRESTIT extends BaseIntegrationTest {

    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID EXECUTOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private OfferRepository offerRepository;

    @BeforeEach
    void setUp() {
        offerRepository.deleteAll();

        OfferDocument offer = new OfferDocument(
                OFFER_ID,
                "Test Offer",
                "Test Description",
                null,
                UUID.randomUUID(),
                EXECUTOR_ID,
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(1000.0),
                OfferStatus.ASSIGNED,
                null,
                null,
                null
        );

        offerRepository.save(offer);
    }

    @Test
    void shouldStartExecution() throws Exception {
        mockMvc.perform(post(START_EXECUTION_URL, OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerStatus").value("IN_PROGRESS"))
                .andExpect(jsonPath("$.startedAt").isNotEmpty());
    }

    @Test
    void shouldReturnNotFoundWhenOfferDoesNotExist() throws Exception {
        mockMvc.perform(post(START_EXECUTION_URL, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}

