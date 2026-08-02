package com.github.bovvver.reputationmanagement;

import com.github.bovvver.infrastructure.UserNotFoundException;
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

    void incrementCancelledBookingsByExecutor(UUID executorId) {
        ReputationEntity reputationEntity = reputationReadRepository.findByUserId(executorId)
                .orElseThrow(() -> new UserNotFoundException("Reputation for user not found"));
        Reputation reputation = ReputationMapper.toDomain(reputationEntity);
        reputation.incrementCancelledBookings();

        reputationRepository.save(reputation);
    }

    void incrementCompletedBookingsByExecutor(UUID executorId) {
        ReputationEntity reputationEntity = reputationReadRepository.findByUserId(executorId)
                .orElseThrow(() -> new UserNotFoundException("Reputation for user not found"));
        Reputation reputation = ReputationMapper.toDomain(reputationEntity);
        reputation.incrementCompletedBookings();

        reputationRepository.save(reputation);
    }
}
