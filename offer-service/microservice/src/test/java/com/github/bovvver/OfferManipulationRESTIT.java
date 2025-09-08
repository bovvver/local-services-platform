package com.github.bovvver;

import com.github.bovvver.requests.CreateOfferRequest;
import com.github.bovvver.requests.LocationDTO;
import com.github.bovvver.responses.OfferCreatedResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

class OfferManipulationRESTIT extends BaseIntegrationTest {

    private static final String CREATE_OFFER_ENDPOINT = "/create";
    private static final String TITLE = "Test Offer Title";
    private static final String DESCRIPTION = "This is a test offer description.";
    private static final double SALARY = 50000.0;
    private static final LocationDTO LOCATION = new LocationDTO(10, -170);
    private static final String[] SERVICE_CATEGORIES = {"HOME_SERVICES", "TECH_SUPPORT"};

    @Test
    void shouldCreateOffer() {
        CreateOfferRequest request = new CreateOfferRequest(
                TITLE,
                DESCRIPTION,
                SALARY,
                LOCATION,
                Set.of(SERVICE_CATEGORIES)
        );

        ResponseEntity<OfferCreatedResponse> response = restTemplate.postForEntity(
                CREATE_OFFER_ENDPOINT,
                request,
                OfferCreatedResponse.class
        );
        OfferCreatedResponse offer = response.getBody();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(offer).isNotNull();
        assertThat(offer.title()).isEqualTo(TITLE);
        assertThat(offer.description()).isEqualTo(DESCRIPTION);
        assertThat(offer.salary()).isEqualTo(SALARY);
        assertThat(offer.location()).isEqualTo(LOCATION);
        assertThat(offer.serviceCategories()).containsExactlyInAnyOrder(SERVICE_CATEGORIES);
        assertThat(offer.offerId()).isNotNull();
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsForValidationCheck")
    void shouldThrowValidationError(CreateOfferRequest request) {

        ResponseEntity<OfferCreatedResponse> response = restTemplate.postForEntity(
                CREATE_OFFER_ENDPOINT,
                request,
                OfferCreatedResponse.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
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