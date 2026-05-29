package com.github.bovvver.offermanagment.workproofupload;

import com.github.bovvver.infrastructure.OfferNotFoundException;
import com.github.bovvver.offermanagment.ExecutionDetailsDocument;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import com.github.bovvver.offermanagment.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WorkProofUploadServiceTest {

    @Mock
    private MinioClient minioClient;

    @Mock
    private CurrentUser currentUser;

    @Mock
    private OfferRepository offerRepository;

    @InjectMocks
    private WorkProofUploadService workProofUploadService;

    @Test
    void shouldReturnPresignedUploadUrlAndFileId() throws Exception {
        UUID offerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest(
                "proof.png",
                "image/png",
                offerId
        );

        ExecutionDetailsDocument executionDetails = new ExecutionDetailsDocument(
                null,
                null,
                null,
                new LinkedHashSet<>()

        );
        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                executionDetails,
                authorId,
                null,
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.OPEN,
                LocalDateTime.now().minusDays(1),
                null
        );

        setBucket(workProofUploadService, "offer-bucket");
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));
        when(currentUser.getId()).thenReturn(UserId.of(authorId));
        doReturn("upload-url").when(minioClient).getPresignedObjectUrl(any());

        PresignedUploadUrlResponse response = workProofUploadService.getPresignedUploadURL(request);

        assertThat(response.uploadUrl()).isEqualTo("upload-url");
        assertThat(response.fileId()).startsWith("offers/" + offerId + "/");
        assertThat(response.fileId()).endsWith("-proof.png");
        verify(minioClient).getPresignedObjectUrl(any());
    }

    @Test
    void shouldReturnPresignedGetUrlsForWorkProofs() throws Exception {
        UUID offerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        Set<WorkProof> workProofs = new LinkedHashSet<>(List.of(
                new WorkProof("offers/%s/proof-1.png".formatted(offerId), LocalDateTime.now()),
                new WorkProof("offers/%s/proof-2.png".formatted(offerId), LocalDateTime.now())
        ));

        ExecutionDetailsDocument executionDetails = new ExecutionDetailsDocument(
                "Completed",
                null,
                LocalDateTime.now().minusDays(1),
                workProofs
        );
        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                executionDetails,
                authorId,
                null,
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.COMPLETED_REQUESTED,
                LocalDateTime.now().minusDays(1),
                null
        );

        setBucket(workProofUploadService, "offer-bucket");
        when(offerRepository.findById(offerId)).thenReturn(Optional.of(offerDocument));
        when(currentUser.getId()).thenReturn(UserId.of(authorId));
        doReturn("get-url-1", "get-url-2").when(minioClient).getPresignedObjectUrl(any());

        PresignedGetUrlResponse response = workProofUploadService.getPresignedGetURLs(offerId);

        assertThat(response.proofUrls())
                .containsExactlyInAnyOrder("get-url-1", "get-url-2");
        verify(minioClient, times(2)).getPresignedObjectUrl(any());
    }

    @Test
    void shouldThrowWhenOfferDoesNotExist() {
        UUID offerId = UUID.randomUUID();
        when(offerRepository.findById(offerId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> workProofUploadService.getPresignedGetURLs(offerId))
                .isInstanceOf(OfferNotFoundException.class);
        verifyNoInteractions(minioClient);
    }

    private static void setBucket(WorkProofUploadService service, String bucketName) {
        try {
            Field field = WorkProofUploadService.class.getDeclaredField("bucket");
            field.setAccessible(true);
            field.set(service, bucketName);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new IllegalStateException("Failed to set bucket", e);
        }
    }
}
