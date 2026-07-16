package com.github.bovvver.verificationmanagement.upload;

import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.infrastructure.URLGenerationFailedException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.verificationmanagement.VerificationEntity;
import com.github.bovvver.verificationmanagement.VerificationReadRepository;
import com.github.bovvver.vo.VerificationStatus;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
class VerificationProofUploadService {

    private static final int URL_EXPIRY_MINUTES = 5;
    private static final String OBJECT_KEY_TEMPLATE = "verification/%s/%s-%s";

    @Value("${MINIO_BUCKET_NAME}")
    private String bucket;

    private final CurrentUser currentUser;
    private final MinioClient minioClient;
    private final VerificationReadRepository verificationReadRepository;

    PresignedUploadUrlResponse getPresignedUploadURL(final PresignedUploadUrlRequest request) {
        if (isVerified()) {
            throw new AlreadyVerifiedException();
        }
        String objectKey = getObjectKey(request);
        String uploadUrl = getPresignedUrl(Http.Method.PUT, objectKey);
        return new PresignedUploadUrlResponse(uploadUrl, objectKey);
    }

    PresignedGetUrlResponse getPresignedGetURLs(final UUID userId) {
        Set<String> proofUrls = verificationReadRepository.findByUserId(userId)
                .map(VerificationEntity::getProofUrl)
                .filter(url -> !url.isBlank())
                .map(Set::of)
                .orElse(Set.of());

        return new PresignedGetUrlResponse(proofUrls.stream()
                .map(key -> getPresignedUrl(Http.Method.GET, key))
                .toList());
    }

    private String getPresignedUrl(Http.Method method, String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(method)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(URL_EXPIRY_MINUTES, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            throw new URLGenerationFailedException();
        }
    }

    private boolean isVerified() {
        return verificationReadRepository.existsByUserIdAndIdentityStatus(
                currentUser.getId().value(),
                VerificationStatus.VERIFIED
        );
    }

    private String getObjectKey(PresignedUploadUrlRequest request) {
        return String.format(
                OBJECT_KEY_TEMPLATE,
                currentUser.getId().value(),
                UUID.randomUUID(),
                request.fileName()
        );
    }
}
