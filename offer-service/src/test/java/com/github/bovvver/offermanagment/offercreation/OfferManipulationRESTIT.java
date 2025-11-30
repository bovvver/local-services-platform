package com.github.bovvver.offermanagment.offercreation;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class OfferManipulationRESTIT extends BaseIntegrationTest {

    private static final String CREATE_OFFER_ENDPOINT = "/create";
    private static final String TITLE = "Test Offer Title";
    private static final String DESCRIPTION = "This is a test offer description.";
    private static final double SALARY = 50000.0;
    private static final LocationDTO LOCATION = new LocationDTO(10, -170);
    private static final String[] SERVICE_CATEGORIES = {"HOME_SERVICES", "TECH_SUPPORT"};

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldCreateOffer() throws Exception {
        CreateOfferRequest request = new CreateOfferRequest(
                TITLE,
                DESCRIPTION,
                SALARY,
                LOCATION,
                Set.of(SERVICE_CATEGORIES)
        );

        mockMvc.perform(post(CREATE_OFFER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.salary").value(SALARY))
                .andExpect(jsonPath("$.location.latitude").value(LOCATION.latitude()))
                .andExpect(jsonPath("$.location.longitude").value(LOCATION.longitude()))
                .andExpect(jsonPath("$.serviceCategories").isArray())
                .andExpect(jsonPath("$.offerId").isNotEmpty());
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForValidationCheck")
    void shouldThrowValidationError(CreateOfferRequest request) throws Exception {
        mockMvc.perform(post(CREATE_OFFER_ENDPOINT)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private static Stream<Arguments> provideArgumentsForValidationCheck() {
        return Stream.of(
                Arguments.of(
                        new CreateOfferRequest(
                                "A".repeat(101),
                                DESCRIPTION,
                                SALARY,
                                LOCATION,
                                Set.of(SERVICE_CATEGORIES)
                        )),
                Arguments.of(
                        new CreateOfferRequest(
                                TITLE,
                                "A".repeat(1001),
                                SALARY,
                                LOCATION,
                                Set.of(SERVICE_CATEGORIES)
                        )),
                Arguments.of(
                        new CreateOfferRequest(
                                TITLE,
                                DESCRIPTION,
                                -1000.0,
                                LOCATION,
                                Set.of(SERVICE_CATEGORIES)
                        )),
                Arguments.of(
                        new CreateOfferRequest(
                                TITLE,
                                DESCRIPTION,
                                SALARY,
                                new LocationDTO(-500, 0),
                                Set.of(SERVICE_CATEGORIES)
                        )),
                Arguments.of(
                        new CreateOfferRequest(
                                TITLE,
                                DESCRIPTION,
                                SALARY,
                                LOCATION,
                                Set.of("INVALID_CATEGORY")
                        ))
        );
    }
}
