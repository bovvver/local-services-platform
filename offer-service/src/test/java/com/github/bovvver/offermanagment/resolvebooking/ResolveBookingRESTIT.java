package com.github.bovvver.offermanagment.resolvebooking;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import com.github.bovvver.contracts.BookingDecisionStatus;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferReadRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import com.github.bovvver.shared.CurrentUser;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.math.BigDecimal;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.UUID;

import static com.github.bovvver.offermanagment.resolvebooking.ResolveBookingService.OFFER_BOOKING_DECISION;
import static com.github.bovvver.offermanagment.resolvebooking.ResolveBookingService.OFFER_BOOKING_NEGOTIATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ResolveBookingRESTIT extends BaseIntegrationTest {

    private static final String RESOLVE_BOOKING_ENDPOINT = "/%s/bookings/%s/decision";

    @Autowired
    private OfferReadRepository offerReadRepository;

    @Autowired
    private CurrentUser currentUser;

    @Autowired
    private ObjectMapper objectMapper;

    private KafkaConsumer<String, BookingDecisionMadeEvent> kafkaConsumer;
    private UUID testOfferId;
    private UUID testBookingId;

    @BeforeEach
    void setUp() {
        testOfferId = UUID.randomUUID();
        testBookingId = UUID.randomUUID();

        OfferDocument testOffer = new OfferDocument(
                "Test Offer",
                "Test Description",
                currentUser.getId().value(),
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(1000.0)
        );

        OfferDocument savedOffer = new OfferDocument(
                testOfferId,
                testOffer.getTitle(),
                testOffer.getDescription(),
                testOffer.getAuthorId(),
                testOffer.getExecutorId(),
                testOffer.getBookingIds(),
                testOffer.getLocation(),
                testOffer.getServiceCategories(),
                testOffer.getSalary(),
                testOffer.getStatus(),
                testOffer.getCreatedAt(),
                testOffer.getUpdatedAt()
        );
        offerReadRepository.save(savedOffer);
        setupKafkaConsumerForTopic(OFFER_BOOKING_DECISION);
    }

    @Test
    void shouldAcceptBookingDecisionAndPublishToKafka() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                null
        );

        String endpoint = String.format(RESOLVE_BOOKING_ENDPOINT, testOfferId, testBookingId);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.status").value(202))
                .andExpect(jsonPath("$.message").value(Matchers.containsString(testBookingId.toString())))
                .andExpect(jsonPath("$.message").value(Matchers.containsString(testOfferId.toString())));

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    ConsumerRecords<String, BookingDecisionMadeEvent> records =
                            kafkaConsumer.poll(Duration.ofMillis(100));

                    assertThat(records).isNotEmpty();

                    ConsumerRecord<String, BookingDecisionMadeEvent> record = records.iterator().next();
                    assertThat(record.key()).isEqualTo(testBookingId.toString());

                    BookingDecisionMadeEvent event = record.value();
                    assertThat(event.bookingId()).isEqualTo(testBookingId);
                    assertThat(event.offerId()).isEqualTo(testOfferId);
                    assertThat(event.status()).isEqualTo(BookingDecisionStatus.ACCEPTED);
                    assertThat(event.salary()).isNull();
                });
    }

    @Test
    void shouldRejectBookingDecisionAndPublishToKafka() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.REJECTED,
                null
        );

        String endpoint = String.format(RESOLVE_BOOKING_ENDPOINT, testOfferId, testBookingId);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());
    }

    @Test
    void shouldNegotiateBookingWithSalaryAndPublishToKafka() throws Exception {
        BigDecimal negotiatedSalary = BigDecimal.valueOf(1500.0);
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.NEGOTIATE,
                negotiatedSalary
        );

        String endpoint = String.format(RESOLVE_BOOKING_ENDPOINT, testOfferId, testBookingId);

        kafkaConsumer.close();
        setupKafkaConsumerForTopic(OFFER_BOOKING_NEGOTIATE);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() -> {
                    ConsumerRecords<String, BookingDecisionMadeEvent> records =
                            kafkaConsumer.poll(Duration.ofMillis(100));

                    assertThat(records).isNotEmpty();

                    BookingDecisionMadeEvent event = records.iterator().next().value();
                    assertThat(event.bookingId()).isEqualTo(testBookingId);
                    assertThat(event.offerId()).isEqualTo(testOfferId);
                    assertThat(event.status()).isEqualTo(BookingDecisionStatus.NEGOTIATE);
                    assertThat(event.salary()).isEqualTo(negotiatedSalary);
                });
    }

    @Test
    void shouldReturnErrorWhenUserIsNotOwnerOfOffer() throws Exception {
        UUID differentOwnerId = UUID.randomUUID();
        OfferDocument offerWithDifferentOwner = new OfferDocument(
                UUID.randomUUID(),
                "Different Owner Offer",
                "Description",
                differentOwnerId,
                null,
                Set.of(),
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(1000.0),
                OfferStatus.OPEN,
                null,
                null
        );
        offerReadRepository.save(offerWithDifferentOwner);

        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                null
        );

        String endpoint = String.format(
                RESOLVE_BOOKING_ENDPOINT,
                offerWithDifferentOwner.getId(),
                testBookingId
        );

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Current user is not the owner of the offer with id");
                });
    }

    @Test
    void shouldReturnErrorWhenNegotiatingWithoutSalary() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.NEGOTIATE,
                null
        );

        String endpoint = String.format(RESOLVE_BOOKING_ENDPOINT, testOfferId, testBookingId);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Salary must be provided when status is NEGOTIATE");
                });
    }

    @Test
    void shouldReturnErrorWhenProvidingSalaryForNonNegotiateStatus() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                BigDecimal.valueOf(1500.0)
        );

        String endpoint = String.format(RESOLVE_BOOKING_ENDPOINT, testOfferId, testBookingId);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    String response = result.getResponse().getContentAsString();
                    assertThat(response).contains("Salary can't be provided when status is not NEGOTIATE");
                });
    }

    @Test
    void shouldReturnErrorWhenOfferDoesNotExist() throws Exception {
        UUID nonExistentOfferId = UUID.randomUUID();
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.ACCEPTED,
                null
        );

        String endpoint = String.format(RESOLVE_BOOKING_ENDPOINT, nonExistentOfferId, testBookingId);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnErrorWhenNegativeSalaryProvided() throws Exception {
        BookingDecisionRequest request = new BookingDecisionRequest(
                BookingDecisionStatus.NEGOTIATE,
                BigDecimal.valueOf(-100.0)
        );

        String endpoint = String.format(RESOLVE_BOOKING_ENDPOINT, testOfferId, testBookingId);

        mockMvc.perform(post(endpoint)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    private void setupKafkaConsumerForTopic(String topic) {
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafka.getBootstrapServers());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "test-consumer-" + UUID.randomUUID());
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class.getName());
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
        props.put(JsonDeserializer.VALUE_DEFAULT_TYPE, BookingDecisionMadeEvent.class.getName());

        kafkaConsumer = new KafkaConsumer<>(props);
        kafkaConsumer.subscribe(List.of(topic));
    }
}
