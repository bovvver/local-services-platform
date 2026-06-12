package com.github.bovvver.offermanagment.offercancellation;

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
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.bovvver.offermanagment.offercancellation.OfferCancellationREST.CANCEL_OFFER_URL;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class OfferCancellationRESTIT extends BaseIntegrationTest {

    private static final UUID AUTHOR_ASSIGNED_OFFER_ID = UUID.randomUUID();
    private static final UUID EXECUTOR_ASSIGNED_OFFER_ID = UUID.randomUUID();
    private static final UUID CANCELLED_OFFER_ID = UUID.randomUUID();
    private static final UUID UNAUTHORIZED_OFFER_ID = UUID.randomUUID();
    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

    @Autowired
    private OfferRepository offerRepository;

    @BeforeEach
    void setUp() {

        offerRepository.deleteAll();

        offerRepository.saveAll(List.of(
                offerAuthor(AUTHOR_ASSIGNED_OFFER_ID, USER_ID),
                offerExecutor(EXECUTOR_ASSIGNED_OFFER_ID, USER_ID),
                offerCancelled(CANCELLED_OFFER_ID, USER_ID),
                offerUnauthorized(UNAUTHORIZED_OFFER_ID)
        ));
    }

    // =========================
    // TEST CASES
    // =========================

    @Test
    void shouldCancelSuccessfullyForAuthor() throws Exception {
        mockMvc.perform(post(CANCEL_OFFER_URL, AUTHOR_ASSIGNED_OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(AUTHOR_ASSIGNED_OFFER_ID.toString()))
                .andExpect(jsonPath("$.status").value(OfferStatus.CANCELLED.name()));
    }

    @Test
    void shouldCancelSuccessfullyForExecutor() throws Exception {
        mockMvc.perform(post(CANCEL_OFFER_URL, EXECUTOR_ASSIGNED_OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(EXECUTOR_ASSIGNED_OFFER_ID.toString()))
                .andExpect(jsonPath("$.status").value(OfferStatus.OPEN.name()));
    }

    @Test
    void shouldReturn404WhenOfferDoesNotExist() throws Exception {
        mockMvc.perform(post(CANCEL_OFFER_URL, UUID.randomUUID())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldThrowAnErrorIfOfferIsNotOpenForCancellation() throws Exception {
        mockMvc.perform(post(CANCEL_OFFER_URL, CANCELLED_OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldThrowAnErrorIfUserIsUnauthorized() throws Exception {
        mockMvc.perform(post(CANCEL_OFFER_URL, UNAUTHORIZED_OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }

    // =========================
    // TEST FACTORY (LOCAL)
    // =========================

    private static OfferDocument offerAuthor(UUID id, UUID authorId) {
        return base(id, authorId, UUID.randomUUID(), OfferStatus.ASSIGNED);
    }

    private static OfferDocument offerExecutor(UUID id, UUID executorId) {
        return base(id, UUID.randomUUID(), executorId, OfferStatus.ASSIGNED);
    }

    private static OfferDocument offerCancelled(UUID id, UUID userId) {
        return base(id, userId, userId, OfferStatus.CANCELLED);
    }

    private static OfferDocument offerUnauthorized(UUID id) {
        return base(id, UUID.randomUUID(), UUID.randomUUID(), OfferStatus.ASSIGNED);
    }

    private static OfferDocument base(
            UUID id,
            UUID authorId,
            UUID executorId,
            OfferStatus status
    ) {
        return new OfferDocument(
                id,
                "Test Offer",
                "Test Description",
                new ExecutionDetailsDocument(null, null, null, null),
                authorId,
                executorId,
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(1000.0),
                status,
                null,
                null
        );
    }
}