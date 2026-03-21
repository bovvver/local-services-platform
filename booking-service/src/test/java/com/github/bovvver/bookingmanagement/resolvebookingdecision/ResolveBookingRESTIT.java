package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.stream.Stream;

import static com.github.bovvver.bookingmanagement.resolvebookingdecision.ResolveBookingREST.RESOLVE_BOOKING_ENDPOINT;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResolveBookingRESTIT extends BaseIntegrationTest {

    private static final UUID USER_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
    private static final UUID BOOKING_ID = UUID.randomUUID();
    private static final UUID OFFER_ID = UUID.randomUUID();

    private static final String TESTED_ENDPOINT = RESOLVE_BOOKING_ENDPOINT.replace("{bookingId}", BOOKING_ID.toString());

    @MockitoBean
    private OfferOwnershipClient offerRestClient;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookingRepository bookingRepository;

    @BeforeEach
    void setup() {
        Booking booking = Booking.create(
                BookingId.of(BOOKING_ID),
                UserId.of(USER_ID),
                OfferId.of(OFFER_ID),
                Salary.of(1000.0)
        );
        bookingRepository.save(booking);
    }

    @Transactional
    @ParameterizedTest(name = "Should resolve booking decision successfully for status {0} and salary {1}")
    @MethodSource("provideStatusesForSuccessfulDecisions")
    void shouldResolveDecisionSuccessfully(BookingDecisionStatus status, BigDecimal salary) throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                status,
                salary
        );

        when(offerRestClient.isUserAnOwner(USER_ID, OFFER_ID)).thenReturn(true);

        mockMvc.perform(post(TESTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    BookingDecisionResponse response = objectMapper.readValue(responseContent, BookingDecisionResponse.class);

                    assertThat(response.status()).isEqualTo(202);
                    assertThat(response.message()).isEqualTo("Booking decision for offerId %s is being processed".formatted(BOOKING_ID));
                });
    }

    @Test
    void shouldThrowValidationExceptionForSalaryMissingWhenStatusIsNegotiate() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.NEGOTIATE,
                null
        );

        mockMvc.perform(post(TESTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).isEqualTo("Salary must be provided when status is NEGOTIATE");
                });
    }

    @Test
    void shouldThrowValidationExceptionForSalaryPresentWhenStatusIsNotNegotiate() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                BigDecimal.valueOf(1000.0)
        );

        mockMvc.perform(post(TESTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).isEqualTo("Salary can't be provided when status is not NEGOTIATE");
                });
    }

    @Test
    void shouldThrowBookingNotFoundException() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                null
        );

        String notValidBookingId = UUID.randomUUID().toString();

        mockMvc.perform(post(RESOLVE_BOOKING_ENDPOINT.replace("{bookingId}", notValidBookingId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isNotFound())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).isEqualTo("Booking not found: %s".formatted(notValidBookingId));
                });
    }

    @Test
    void shouldThrowOfferOwnershipValidationException() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                null
        );

        when(offerRestClient.isUserAnOwner(USER_ID, OFFER_ID)).thenReturn(false);

        mockMvc.perform(post(TESTED_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden())
                .andExpect(result -> {
                    String responseContent = result.getResponse().getContentAsString();
                    assertThat(responseContent).isEqualTo("Current user is not the owner of the offer");
                });
    }

    private static Stream<Arguments> provideStatusesForSuccessfulDecisions() {
        return Stream.of(
                Arguments.of(BookingDecisionStatus.ACCEPTED, null),
                Arguments.of(BookingDecisionStatus.REJECTED, null),
                Arguments.of(BookingDecisionStatus.NEGOTIATE, BigDecimal.valueOf(1000.0))
        );
    }
}

