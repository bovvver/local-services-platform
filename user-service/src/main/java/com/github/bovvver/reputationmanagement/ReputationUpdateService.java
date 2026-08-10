package com.github.bovvver.reputationmanagement;

import com.github.bovvver.event.DomainEventPublisher;
import com.github.bovvver.infrastructure.UserNotFoundException;
import com.github.bovvver.vo.Rating;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
class ReputationUpdateService {

    private final ReputationReadRepository reputationReadRepository;
    private final ReputationRepository reputationRepository;
    private final DomainEventPublisher domainEventPublisher;

    void incrementCancelledBookingsByExecutor(UUID executorId) {
        ReputationEntity reputationEntity = reputationReadRepository.findByUserId(executorId)
                .orElseThrow(() -> new UserNotFoundException("Reputation for user not found"));
        Reputation reputation = ReputationMapper.toDomain(reputationEntity);
        reputation.incrementCancelledBookings();

        reputationRepository.save(reputation);
        domainEventPublisher.publish(reputation.pullDomainEvents());
    }

    void incrementCompletedBookingsByExecutor(UUID executorId) {
        ReputationEntity reputationEntity = reputationReadRepository.findByUserId(executorId)
                .orElseThrow(() -> new UserNotFoundException("Reputation for user not found"));
        Reputation reputation = ReputationMapper.toDomain(reputationEntity);
        reputation.incrementCompletedBookings();

        reputationRepository.save(reputation);
        domainEventPublisher.publish(reputation.pullDomainEvents());
    }

    void addRatingToExecutor(UUID executorId, double ratingValue) {
        ReputationEntity reputationEntity = reputationReadRepository.findByUserId(executorId)
                .orElseThrow(() -> new UserNotFoundException("Reputation for user not found"));
        Reputation reputation = ReputationMapper.toDomain(reputationEntity);
        reputation.addRating(Rating.of(ratingValue));

        reputationRepository.save(reputation);
        domainEventPublisher.publish(reputation.pullDomainEvents());
    }
}
