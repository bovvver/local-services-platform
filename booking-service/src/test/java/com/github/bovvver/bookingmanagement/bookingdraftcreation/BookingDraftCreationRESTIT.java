package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static com.github.bovvver.bookingmanagement.bookingdraftcreation.BookingDraftCreationREST.CREATE_BOOKING_ENDPOINT;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BookingDraftCreationRESTIT extends BaseIntegrationTest {

    private final UUID OFFER_ID = UUID.randomUUID();

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateBookingDraft() throws Exception {
        BookOfferRequest request = new BookOfferRequest(
                OFFER_ID,
                BigDecimal.valueOf(60000.0)
        );

        mockMvc.perform(post(CREATE_BOOKING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldThrowErrorIfBookingExists() throws Exception {
        BookOfferRequest request = new BookOfferRequest(
                OFFER_ID,
                BigDecimal.valueOf(60000.0)
        );

        mockMvc.perform(post(CREATE_BOOKING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post(CREATE_BOOKING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldThrowErrorIfSalaryIsNegative() throws Exception {
        BookOfferRequest request = new BookOfferRequest(
                OFFER_ID,
                BigDecimal.valueOf(-1.0)
        );

        mockMvc.perform(post(CREATE_BOOKING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldThrowErrorIfOfferIdIsNull() throws Exception {
        BookOfferRequest request = new BookOfferRequest(
                null,
                BigDecimal.valueOf(-1.0)
        );

        mockMvc.perform(post(CREATE_BOOKING_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
