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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NegotiationRESTIT extends BaseIntegrationTest {

    private static final UUID EXECUTOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID AUTHOR_ID = UUID.randomUUID();
    private static final UUID BOOKING_ID = UUID.randomUUID();
    private static final UUID OFFER_ID = UUID.randomUUID();

    private static final String TESTED_ENDPOINT = "/{bookingId}/negotiation/proposal".replace("{bookingId}", BOOKING_ID.toString());
    private static final String ACCEPT_ENDPOINT_TEMPLATE = "/{bookingId}/negotiation/{positionId}/accept";
    private static final String REJECT_ENDPOINT_TEMPLATE = "/{bookingId}/negotiation/{positionId}/reject";

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

    @Test
    void shouldReturnBadRequestWhenProposalSalaryIsNegative() throws Exception {
        NegotiationProposalRequest request = new NegotiationProposalRequest(BigDecimal.valueOf(-1));

        mockMvc.perform(post(TESTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldAcceptLatestProposalAndUpdateBookingSalaryAndStatus() throws Exception {
        UUID authorPositionId = proposeSalaryAsAuthor(BigDecimal.valueOf(1300));

        String acceptEndpoint = ACCEPT_ENDPOINT_TEMPLATE
                .replace("{bookingId}", BOOKING_ID.toString())
                .replace("{positionId}", authorPositionId.toString());

        mockMvc.perform(post(acceptEndpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var persistedBooking = bookingReadRepository.findById(BOOKING_ID).orElseThrow();
        assertThat(persistedBooking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
        assertThat(persistedBooking.getSalary()).isNotNull();
        assertThat(persistedBooking.getSalary()).isEqualByComparingTo("1300");
        assertThat(persistedBooking.getNegotiation()).isNotNull();
        assertThat(persistedBooking.getNegotiation().getStatus()).isEqualTo(NegotiationStatus.ACCEPTED);
    }

    @Test
    void shouldRejectLatestProposalAndResetBookingStatusToPending() throws Exception {
        UUID authorPositionId = proposeSalaryAsAuthor(BigDecimal.valueOf(1400));

        String rejectEndpoint = REJECT_ENDPOINT_TEMPLATE
                .replace("{bookingId}", BOOKING_ID.toString())
                .replace("{positionId}", authorPositionId.toString());

        mockMvc.perform(post(rejectEndpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        var persistedBooking = bookingReadRepository.findById(BOOKING_ID).orElseThrow();
        assertThat(persistedBooking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(persistedBooking.getNegotiation()).isNotNull();
        assertThat(persistedBooking.getNegotiation().getStatus()).isEqualTo(NegotiationStatus.REJECTED);
    }

    @Test
    void shouldReturnNotFoundWhenAcceptingNotExistingPosition() throws Exception {
        UUID notExistingPositionId = UUID.randomUUID();
        String acceptEndpoint = ACCEPT_ENDPOINT_TEMPLATE
                .replace("{bookingId}", BOOKING_ID.toString())
                .replace("{positionId}", notExistingPositionId.toString());

        mockMvc.perform(post(acceptEndpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualTo("Position not found: %s".formatted(notExistingPositionId)));
    }

    @Test
    void shouldReturnConflictWhenAcceptingOutdatedPosition() throws Exception {
        UUID olderAuthorPositionId = proposeSalaryAsAuthor(BigDecimal.valueOf(1250));
        proposeSalaryAsExecutor(BigDecimal.valueOf(1280));

        String acceptEndpoint = ACCEPT_ENDPOINT_TEMPLATE
                .replace("{bookingId}", BOOKING_ID.toString())
                .replace("{positionId}", olderAuthorPositionId.toString());

        mockMvc.perform(post(acceptEndpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertThat(result.getResponse().getContentAsString())
                        .isEqualTo("Position with id %s is outdated. Please check latest position.".formatted(olderAuthorPositionId)));
    }

    @Test
    void shouldReturnInternalServerErrorWhenDecidingOnOwnProposal() throws Exception {
        proposeSalaryAsAuthor(BigDecimal.valueOf(1200));
        UUID executorPositionId = proposeSalaryAsExecutor(BigDecimal.valueOf(1500));

        String acceptEndpoint = ACCEPT_ENDPOINT_TEMPLATE
                .replace("{bookingId}", BOOKING_ID.toString())
                .replace("{positionId}", executorPositionId.toString());

        assertThatThrownBy(() -> mockMvc.perform(post(acceptEndpoint)
                        .contentType(MediaType.APPLICATION_JSON))
                .andReturn())
                .isInstanceOf(jakarta.servlet.ServletException.class)
                .hasRootCauseInstanceOf(com.github.bovvver.bookingmanagement.infrastructure.OwnNegotiationProposalDecisionException.class)
                .rootCause()
                .hasMessage("Cannot decide on own proposal");
    }

    private UUID proposeSalaryAsAuthor(BigDecimal proposedSalary) throws Exception {
        // W testach CurrentUser jest stały (EXECUTOR_ID), więc propozycję AUTORA symulujemy na poziomie domeny.
        var bookingEntity = bookingReadRepository.findById(BOOKING_ID).orElseThrow();
        var booking = com.github.bovvver.bookingmanagement.BookingMapper.toDomain(bookingEntity);
        booking.addPositionToNegotiation(new Salary(proposedSalary), UserId.of(AUTHOR_ID));
        bookingRepository.save(booking);

        int lastIndex = booking.getNegotiation().getPositions().size() - 1;
        return booking.getNegotiation().getPositions().get(lastIndex).getId().value();
    }

    private UUID proposeSalaryAsExecutor(BigDecimal proposedSalary) throws Exception {
        // Executor działa przez REST (CurrentUser = EXECUTOR_ID)
        return proposeSalary(BOOKING_ID, proposedSalary);
    }

    private UUID proposeSalary(UUID bookingId, BigDecimal proposedSalary) throws Exception {
        NegotiationProposalRequest request = new NegotiationProposalRequest(proposedSalary);
        mockMvc.perform(post("/{bookingId}/negotiation/proposal".replace("{bookingId}", bookingId.toString()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        var bookingEntity = bookingReadRepository.findById(bookingId).orElseThrow();
        var booking = com.github.bovvver.bookingmanagement.BookingMapper.toDomain(bookingEntity);
        assertThat(booking.getNegotiation()).isNotNull();
        assertThat(booking.getNegotiation().getPositions()).isNotEmpty();

        int lastIndex = booking.getNegotiation().getPositions().size() - 1;
        return booking.getNegotiation().getPositions().get(lastIndex).getId().value();
    }
}
