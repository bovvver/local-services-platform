package com.github.bovvver.offermanagment.workproofupload;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.infrastructure.URLGenerationFailedException;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferMapper;
import com.github.bovvver.offermanagment.OfferRepository;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
class WorkProofUploadService {

    private static final int URL_EXPIRY_MINUTES = 5;
    private static final String OBJECT_KEY_TEMPLATE = "offers/%s/%s-%s";

    @Value("${MINIO_BUCKET_NAME}")
    private String bucket;

    private final MinioClient minioClient;
    private final OfferRepository offerRepository;

    PresignedUploadUrlResponse getPresignedUploadURL(final PresignedUploadUrlRequest request) {
        String objectKey = getObjectKey(request);
        String uploadUrl = getUploadUrl(objectKey);
        return new PresignedUploadUrlResponse(uploadUrl, objectKey);
    }

    PresignedGetUrlResponse getPresignedGetURLs(final UUID offerId) {
        OfferDocument offerDocument = offerRepository.findById(offerId)
                .orElseThrow(() -> new OfferNotFoundException(offerId));
        Offer offer = OfferMapper.toDomain(offerDocument);
        Set<WorkProof> workProofs = offer.getWorkProofs();

        List<String> getUrls = workProofs.stream()
                .map(WorkProof::url)
                .map(this::getGetUrl)
                .toList();

        return new PresignedGetUrlResponse(getUrls);
    }

    private String getGetUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(URL_EXPIRY_MINUTES, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            throw new URLGenerationFailedException();
        }
    }

    private String getUploadUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Http.Method.PUT)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(URL_EXPIRY_MINUTES, TimeUnit.MINUTES)
                            .build()
            );
        } catch (Exception e) {
            throw new URLGenerationFailedException();
        }
    }

    private String getObjectKey(PresignedUploadUrlRequest request) {
        return String.format(
                OBJECT_KEY_TEMPLATE,
                request.offerId(),
                UUID.randomUUID(),
                request.fileName()
        );
    }
}
