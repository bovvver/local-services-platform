package com.github.bovvver.verificationmanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlVerificationRepository extends Repository<VerificationEntity, UUID> {

    VerificationEntity save(VerificationEntity entity);
}
