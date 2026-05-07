package com.github.bovvver.offermanagment.workproofupload;

import io.minio.GetPresignedObjectUrlArgs;
import io.minio.Http;
import io.minio.MinioClient;
import io.minio.errors.MinioException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
class WorkProofUploadService {

    private static final int URL_EXPIRY_MINUTES = 5;
    private static final String OBJECT_KEY_TEMPLATE = "offers/%s/%s-%s";
    private static final String FILE_URL_TEMPLATE = "%s/%s/%s";

    @Value("${MINIO_BUCKET_NAME}")
    private String bucket;

    @Value("${MINIO_URL}")
    private String url;

    private final MinioClient minioClient;

    PresignedUrlResponse getPresignedURL(final PresignedUrlRequest request) {

        try {
            String objectKey = getObjectKey(request);
            String uploadUrl = getUploadUrl(objectKey);
            String fileUrl = String.format(FILE_URL_TEMPLATE, url, bucket, objectKey);
            return new PresignedUrlResponse(uploadUrl, fileUrl);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }

    private String getUploadUrl(String objectKey) throws MinioException {
        return minioClient.getPresignedObjectUrl(
                GetPresignedObjectUrlArgs.builder()
                        .method(Http.Method.PUT)
                        .bucket(bucket)
                        .object(objectKey)
                        .expiry(URL_EXPIRY_MINUTES, TimeUnit.MINUTES)
                        .build()
        );
    }

    private String getObjectKey(PresignedUrlRequest request) {
        return String.format(
                OBJECT_KEY_TEMPLATE,
                request.offerId(),
                UUID.randomUUID(),
                request.fileName()
        );
    }
}
