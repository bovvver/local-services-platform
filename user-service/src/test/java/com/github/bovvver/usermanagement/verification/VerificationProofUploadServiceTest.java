package com.github.bovvver.usermanagement.verification;

import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.usermanagement.User;
import com.github.bovvver.usermanagement.UserRepository;
import com.github.bovvver.vo.Email;
import com.github.bovvver.vo.UserId;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
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
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationProofUploadServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private MinioClient minioClient;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private VerificationProofUploadService service;

    private static final UUID USER_UUID = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(service, "bucket", "test-bucket");
    }

    @Test
    void shouldGetPresignedUploadUrlSuccessfully() throws Exception {
        User user = User.create(UserId.of(USER_UUID), new Email("test@example.com"), "John", "Doe");

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(userRepository.findById(UserId.of(USER_UUID))).thenReturn(Optional.of(user));
        when(minioClient.getPresignedObjectUrl(any())).thenReturn("http://minio/upload");

        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest("proof.png", "image/png");
        PresignedUploadUrlResponse response = service.getPresignedUploadURL(request);

        assertThat(response.uploadUrl()).isEqualTo("http://minio/upload");
        assertThat(response.fileId()).contains("verification/" + USER_UUID);
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyVerified() {
        User user = User.create(UserId.of(USER_UUID), new Email("test@example.com"), "John", "Doe");
        user.addVerificationProof(VerificationProof.of("http://example.com/proof"));
        user.verify();

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(userRepository.findById(UserId.of(USER_UUID))).thenReturn(Optional.of(user));

        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest("proof.png", "image/png");

        assertThrows(AlreadyVerifiedException.class, () -> service.getPresignedUploadURL(request));
    }

    @Test
    void shouldGetPresignedGetUrlsSuccessfully() throws Exception {
        User user = User.create(UserId.of(USER_UUID), new Email("test@example.com"), "John", "Doe");
        user.addVerificationProof(VerificationProof.of("http://example.com/proof"));

        when(userRepository.findById(UserId.of(USER_UUID))).thenReturn(Optional.of(user));
        when(minioClient.getPresignedObjectUrl(any())).thenReturn("http://minio/get-url");

        PresignedGetUrlResponse response = service.getPresignedGetURLs(USER_UUID);

        assertThat(response.proofUrls()).hasSize(1);
        assertThat(response.proofUrls().getFirst()).isEqualTo("http://minio/get-url");
    }
}
