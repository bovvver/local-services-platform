package com.github.bovvver.bookingmanagement.negotiation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NegotiationRESTIT extends BaseIntegrationTest {

    private static final UUID EXECUTOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final UUID BOOKING_ID = UUID.randomUUID();
    private static final UUID OFFER_ID = UUID.randomUUID();

    private static final String TESTED_ENDPOINT = "/{bookingId}/negotiation/proposal".replace("{bookingId}", BOOKING_ID.toString());

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private BookingReadRepository bookingReadRepository;

    @BeforeEach
    void setup() {
        Salary initialSalary = Salary.of(1000.0);

        Booking booking = Booking.create(
                BookingId.of(BOOKING_ID),
                UserId.of(EXECUTOR_ID),
                OfferId.of(OFFER_ID),
                initialSalary
        );

        booking.beginNegotiation(initialSalary, UserId.of(AUTHOR_ID));

        bookingRepository.save(booking);
    }

    @Test
    void shouldAddNegotiationPositionWhenProposalIsValid() throws Exception {
        BigDecimal newProposedSalary = BigDecimal.valueOf(1200.0);
        NegotiationProposalRequest request = new NegotiationProposalRequest(newProposedSalary);

        int initialPositionsCount = bookingReadRepository.findById(BOOKING_ID)
                .map(bookingEntity -> bookingEntity.getNegotiation().getPositions().size())
                .orElseThrow();

        mockMvc.perform(post(TESTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var persistedBooking = bookingReadRepository.findById(BOOKING_ID).orElseThrow();

        assertThat(persistedBooking.getNegotiation()).isNotNull();
        assertThat(persistedBooking.getNegotiation().getPositions()).hasSize(initialPositionsCount + 1);
    }

    @Test
    void shouldReturnNotFoundWhenBookingDoesNotExist() throws Exception {
        UUID notExistingBookingId = UUID.randomUUID();
        String endpoint = "/{bookingId}/negotiation/proposal".replace("{bookingId}", notExistingBookingId.toString());

        NegotiationProposalRequest request = new NegotiationProposalRequest(BigDecimal.valueOf(1200.0));

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).isEqualTo("Booking not found: %s".formatted(notExistingBookingId));
                });
    }

    @Test
    void shouldReturnForbiddenWhenCurrentUserIsNotPartyOfNegotiation() throws Exception {
        UUID otherExecutorId = UUID.randomUUID();
        UUID bookingId = UUID.randomUUID();
        UUID offerId = UUID.randomUUID();

        Salary initialSalary = Salary.of(1000.0);

        Booking booking = Booking.create(
                BookingId.of(bookingId),
                UserId.of(otherExecutorId),
                OfferId.of(offerId),
                initialSalary
        );
        booking.beginNegotiation(initialSalary, UserId.of(UUID.randomUUID()));
        bookingRepository.save(booking);

        String endpoint = "/{bookingId}/negotiation/proposal".replace("{bookingId}", bookingId.toString());
        NegotiationProposalRequest request = new NegotiationProposalRequest(BigDecimal.valueOf(1200.0));

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).isEqualTo("Current user is not a party of the negotiation");
                });
    }
}
