package com.github.bovvver.verificationmanagement.upload;

import com.github.bovvver.infrastructure.VerificationNotFoundException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.verificationmanagement.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class VerificationProcessingService {

    private final CurrentUser currentUser;
    private final VerificationReadRepository verificationReadRepository;
    private final VerificationRepository verificationWriteRepository;

    @Transactional
    public VerificationDataResponse sendVerificationData(final @Valid VerificationDataRequest request) {
        VerificationEntity verificationEntity = verificationReadRepository.findByUserId(currentUser.getId().value())
                .orElseThrow(() -> new VerificationNotFoundException(currentUser.getId().value()));
        Verification verification = VerificationMapper.toDomain(verificationEntity);
        verification.addVerificationProof(VerificationProof.of(request.proofUrls().getFirst()));
        verificationWriteRepository.save(verification);

        return VerificationDataResponse.of(currentUser.getId().value());
    }

    @Transactional
    public void verify(UUID userId) {
        VerificationEntity verificationEntity = verificationReadRepository.findByUserId(userId)
                .orElseThrow(() -> new VerificationNotFoundException(userId));

        Verification verification = VerificationMapper.toDomain(verificationEntity);
        verification.verify();
        verificationWriteRepository.save(verification);
    }

    @Transactional
    public void reject(UUID userId) {
        VerificationEntity verificationEntity = verificationReadRepository.findByUserId(userId)
                .orElseThrow(() -> new VerificationNotFoundException(userId));

        Verification verification = VerificationMapper.toDomain(verificationEntity);
        verification.reject();
        verificationWriteRepository.save(verification);
    }
}
