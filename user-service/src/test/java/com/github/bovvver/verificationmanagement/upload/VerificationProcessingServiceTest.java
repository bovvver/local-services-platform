package com.github.bovvver.verificationmanagement.upload;

import com.github.bovvver.infrastructure.AlreadyVerifiedException;
import com.github.bovvver.infrastructure.VerificationNotFoundException;
import com.github.bovvver.shared.CurrentUser;
import com.github.bovvver.verificationmanagement.VerificationEntity;
import com.github.bovvver.verificationmanagement.VerificationReadRepository;
import com.github.bovvver.verificationmanagement.VerificationRepository;
import com.github.bovvver.vo.UserId;
import com.github.bovvver.vo.VerificationStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VerificationProcessingServiceTest {

    @Mock
    private CurrentUser currentUser;

    @Mock
    private VerificationReadRepository readRepository;

    @Mock
    private VerificationRepository writeRepository;

    @InjectMocks
    private VerificationProcessingService service;

    private static final UUID USER_UUID = UUID.randomUUID();

    @Test
    void shouldSendVerificationDataSuccessfully() {
        VerificationEntity entity = new VerificationEntity(
                USER_UUID,
                VerificationStatus.PENDING,
                null,
                null
        );

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(entity));

        VerificationDataRequest request = new VerificationDataRequest(List.of("http://example.com/proof"));

        VerificationDataResponse response = service.sendVerificationData(request);

        assertThat(response).isNotNull();
        assertThat(response.userId()).isEqualTo(USER_UUID);
        assertThat(response.message()).isEqualTo("Verification data uploaded successfully.");

        verify(currentUser, atLeastOnce()).getId();
        verify(readRepository).findByUserId(USER_UUID);
        verify(writeRepository).save(argThat(v -> 
                v.getIdentityStatus() == VerificationStatus.PENDING &&
                v.getVerificationProof() != null &&
                "http://example.com/proof".equals(v.getVerificationProof().url())
        ));
    }

    @Test
    void shouldThrowExceptionWhenSendingVerificationDataAndVerificationNotFound() {
        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.empty());

        VerificationDataRequest request = new VerificationDataRequest(List.of("http://example.com/proof"));

        assertThrows(VerificationNotFoundException.class, () -> service.sendVerificationData(request));

        verify(currentUser, atLeastOnce()).getId();
        verify(readRepository).findByUserId(USER_UUID);
        verifyNoInteractions(writeRepository);
    }

    @Test
    void shouldThrowExceptionWhenSendingVerificationDataAndUserAlreadyVerified() {
        VerificationEntity entity = new VerificationEntity(
                USER_UUID,
                VerificationStatus.VERIFIED,
                "http://example.com/old-proof",
                java.time.LocalDateTime.now()
        );

        when(currentUser.getId()).thenReturn(UserId.of(USER_UUID));
        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(entity));

        VerificationDataRequest request = new VerificationDataRequest(List.of("http://example.com/new-proof"));

        assertThrows(AlreadyVerifiedException.class, () -> service.sendVerificationData(request));

        verify(currentUser, atLeastOnce()).getId();
        verify(readRepository).findByUserId(USER_UUID);
        verifyNoInteractions(writeRepository);
    }

    @Test
    void shouldVerifySuccessfully() {
        VerificationEntity entity = new VerificationEntity(
                USER_UUID,
                VerificationStatus.PENDING,
                "http://example.com/proof",
                java.time.LocalDateTime.now()
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(entity));

        service.verify(USER_UUID);

        verify(readRepository).findByUserId(USER_UUID);
        verify(writeRepository).save(argThat(v -> v.getIdentityStatus() == VerificationStatus.VERIFIED));
    }

    @Test
    void shouldRejectSuccessfully() {
        VerificationEntity entity = new VerificationEntity(
                USER_UUID,
                VerificationStatus.PENDING,
                null,
                null
        );

        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.of(entity));

        service.reject(USER_UUID);

        verify(readRepository).findByUserId(USER_UUID);
        verify(writeRepository).save(argThat(v -> v.getIdentityStatus() == VerificationStatus.REJECTED));
    }

    @Test
    void shouldThrowExceptionWhenVerificationNotFoundOnVerify() {
        when(readRepository.findByUserId(USER_UUID)).thenReturn(Optional.empty());

        assertThrows(VerificationNotFoundException.class, () -> service.verify(USER_UUID));

        verify(readRepository).findByUserId(USER_UUID);
        verifyNoInteractions(writeRepository);
    }
}
