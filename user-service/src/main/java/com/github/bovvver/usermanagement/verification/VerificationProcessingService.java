package com.github.bovvver.usermanagement.verification;

import com.github.bovvver.infrastructure.UserNotFoundException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.UserId;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VerificationProcessingService {

    private final CurrentUser currentUser;
    private final UserRepository userRepository;

    @Transactional
    public VerificationDataResponse sendVerificationData(final @Valid VerificationDataRequest request) {
        User user = userRepository.findById(currentUser.getId())
                .orElseThrow(() -> new UserNotFoundException("User not found: " + currentUser.getId()));
        user.addVerificationProof(VerificationProof.of(request.proofUrls().getFirst()));
        userRepository.save(user);

        return VerificationDataResponse.of(currentUser.getId().value());
    }

    @Transactional
    public void verify(UUID userId) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.verify();
        userRepository.save(user);
    }

    @Transactional
    public void reject(UUID userId) {
        User user = userRepository.findById(UserId.of(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));

        user.reject();
        userRepository.save(user);
    }
}
