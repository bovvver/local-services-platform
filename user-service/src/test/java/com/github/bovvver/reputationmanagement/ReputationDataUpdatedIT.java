package com.github.bovvver.reputationmanagement;

import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.contracts.BookingCancelledByExecutorIntegrationEvent;
import com.github.bovvver.contracts.OfferCompletedIntegrationEvent;
import com.github.bovvver.contracts.ReviewAddedIntegrationEvent;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

class ReputationDataUpdatedIT extends BaseIntegrationTest {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Autowired
    private ReputationReadRepository reputationReadRepository;

    @Autowired
    private ReputationRepository reputationRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @AfterEach
    void tearDown() {
        jdbcTemplate.update("DELETE FROM reputation");
        jdbcTemplate.update("DELETE FROM users");
    }

    @Test
    void shouldIncrementCancelledBookingsCountOnBookingCancelledByExecutorEvent() {
        UUID userId = UUID.randomUUID();
        User user = User.create(UserId.of(userId), new Email("test@example.com"), "John", "Doe");
        userRepository.save(user);

        Reputation reputation = Reputation.initialize(UserId.of(userId));
        reputationRepository.save(reputation);

        BookingCancelledByExecutorIntegrationEvent event = new BookingCancelledByExecutorIntegrationEvent(
                UUID.randomUUID(),
                userId
        );

        kafkaTemplate.send("booking.cancelled.by-executor", userId.toString(), event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var entity = reputationReadRepository.findByUserId(userId);
            assertThat(entity.orElseThrow().getCancelledBookings()).isOne();
        });
    }

    @Test
    void shouldIncrementCompletedBookingsCountOnOfferCompletedEvent() {
        UUID userId = UUID.randomUUID();
        User user = User.create(UserId.of(userId), new Email("test@example.com"), "John", "Doe");
        userRepository.save(user);

        Reputation reputation = Reputation.initialize(UserId.of(userId));
        reputationRepository.save(reputation);

        OfferCompletedIntegrationEvent event = new OfferCompletedIntegrationEvent(
                UUID.randomUUID(),
                userId
        );

        kafkaTemplate.send("offer.completed", userId.toString(), event);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var entity = reputationReadRepository.findByUserId(userId);
            assertThat(entity.orElseThrow().getCompletedBookings()).isOne();
        });
    }

    @Test
    void shouldUpdateAverageRatingAndTotalRatingsOnReviewAddedEvent() {
        UUID userId = UUID.randomUUID();
        User user = User.create(UserId.of(userId), new Email("test@example.com"), "John", "Doe");
        userRepository.save(user);

        Reputation reputation = Reputation.initialize(UserId.of(userId));
        reputationRepository.save(reputation);

        ReviewAddedIntegrationEvent event1 = new ReviewAddedIntegrationEvent(
                UUID.randomUUID(),
                userId,
                4.5
        );

        kafkaTemplate.send("review.added", userId.toString(), event1);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var entity = reputationReadRepository.findByUserId(userId);
            assertThat(entity.orElseThrow().getAverageRating()).isEqualTo(4.5);
            assertThat(entity.orElseThrow().getTotalRatings()).isOne();
        });

        ReviewAddedIntegrationEvent event2 = new ReviewAddedIntegrationEvent(
                UUID.randomUUID(),
                userId,
                3.5
        );

        kafkaTemplate.send("review.added", userId.toString(), event2);

        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            var entity = reputationReadRepository.findByUserId(userId);
            assertThat(entity.orElseThrow().getAverageRating()).isEqualTo(4.0);
            assertThat(entity.orElseThrow().getTotalRatings()).isEqualTo(2);
        });
    }
}
