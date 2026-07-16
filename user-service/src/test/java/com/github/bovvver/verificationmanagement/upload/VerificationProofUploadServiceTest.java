package com.github.bovvver.verificationmanagement.upload;

import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.infrastructure.URLGenerationFailedException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.verificationmanagement.VerificationEntity;
import com.github.bovvver.verificationmanagement.VerificationReadRepository;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationProofUploadServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private MinioClient minioClient;

    @Mock
    private VerificationReadRepository readRepository;

    @InjectMocks
    private VerificationProofUploadService service;

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldReturnPresignedUploadUrl() throws Exception {
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(readRepository.existsByUserIdAndIdentityStatus(USER_UUID, VerificationStatus.VERIFIED)).thenReturn(false);
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://minio/upload-url");

        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest("proof.pdf", "application/pdf");

        PresignedUploadUrlResponse response = service.getPresignedUploadURL(request);

        assertThat(response).isNotNull();
        assertThat(response.uploadUrl()).isEqualTo("http://minio/upload-url");
        assertThat(response.fileId()).startsWith("verification/" + USER_UUID + "/");
        assertThat(response.fileId()).endsWith("-proof.pdf");

        verify(readRepository).existsByUserIdAndIdentityStatus(USER_UUID, VerificationStatus.VERIFIED);
        verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void shouldThrowExceptionWhenGeneratingPresignedUploadUrlForAlreadyVerifiedUser() {
        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(readRepository.existsByUserIdAndIdentityStatus(USER_UUID, VerificationStatus.VERIFIED)).thenReturn(true);

        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest("proof.pdf", "application/pdf");

        assertThrows(AlreadyVerifiedException.class, () -> service.getPresignedUploadURL(request));

        verify(readRepository).existsByUserIdAndIdentityStatus(USER_UUID, VerificationStatus.VERIFIED);
        verifyNoInteractions(minioClient);
    }

    @Test
    void shouldThrowExceptionWhenMinioClientFailsToGenerateUploadUrl() throws Exception {
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(readRepository.existsByUserIdAndIdentityStatus(USER_UUID, VerificationStatus.VERIFIED)).thenReturn(false);
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenThrow(new RuntimeException("Minio error"));

        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest("proof.pdf", "application/pdf");

        assertThrows(URLGenerationFailedException.class, () -> service.getPresignedUploadURL(request));
    }

    @Test
    void shouldReturnPresignedGetUrls() throws Exception {
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");

        VerificationEntity entity = new VerificationEntity(
                USER_UUID,
                VerificationStatus.PENDING,
                "verification/" + USER_UUID + "/proof.pdf",
                java.time.LocalDateTime.now()
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(entity));
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class))).thenReturn("http://minio/download-url");

        PresignedGetUrlResponse response = service.getPresignedGetURLs(USER_UUID);

        assertThat(response).isNotNull();
        assertThat(response.proofUrls()).containsExactly("http://minio/download-url");

        verify(readRepository).findByUserId(USER_UUID);
        verify(minioClient).getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class));
    }

    @Test
    void shouldReturnEmptyUrlsWhenNoVerificationRecordExists() {
        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.empty());

        PresignedGetUrlResponse response = service.getPresignedGetURLs(USER_UUID);

        assertThat(response).isNotNull();
        assertThat(response.proofUrls()).isEmpty();

        verify(readRepository).findByUserId(USER_UUID);
        verifyNoInteractions(minioClient);
    }
}
