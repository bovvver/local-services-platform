package com.github.bovvver.verificationmanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class VerificationRepositoryImpl implements VerificationRepository {

    private final SqlVerificationRepository repository;

    @Override
    public Verification save(final Verification verification) {
        VerificationEntity entity = repository.save(VerificationMapper.toEntity(verification));
        return VerificationMapper.toDomain(entity);
    }
}
