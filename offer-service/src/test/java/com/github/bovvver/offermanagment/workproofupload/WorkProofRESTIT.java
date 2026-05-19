package com.github.bovvver.offermanagment.workproofupload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.offermanagment.OfferDocument;
import com.github.bovvver.offermanagment.OfferRepository;
import com.github.bovvver.offermanagment.vo.Location;
import com.github.bovvver.offermanagment.vo.OfferStatus;
import com.github.bovvver.offermanagment.vo.ServiceCategory;
import io.minio.MinioClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static com.github.bovvver.offermanagment.workproofupload.WorkProofREST.*;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class WorkProofRESTIT extends BaseIntegrationTest {

    @Autowired
    private OfferRepository offerRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private MinioClient minioClient;

    @BeforeEach
    void setUp() {
        offerRepository.deleteAll();
    }

    @Test
    void shouldReturnPresignedUploadUrl() throws Exception {
        UUID offerId = UUID.randomUUID();
        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest(
                "proof.png",
                "image/png",
                offerId
        );

        doReturn("upload-url").when(minioClient).getPresignedObjectUrl(any());

        mockMvc.perform(post(GET_PRESIGNED_UPLOAD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").value("upload-url"))
                .andExpect(jsonPath("$.fileId").value(startsWith("offers/" + offerId + "/")))
                .andExpect(jsonPath("$.fileId").value(endsWith("-proof.png")));
    }

    @Test
    void shouldReturnPresignedGetUrls() throws Exception {
        UUID offerId = UUID.randomUUID();
        Set<WorkProof> workProofs = new LinkedHashSet<>(List.of(
                new WorkProof("offers/%s/proof-1.png".formatted(offerId), LocalDateTime.now()),
                new WorkProof("offers/%s/proof-2.png".formatted(offerId), LocalDateTime.now())
        ));

        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                "Completed",
                UUID.randomUUID(),
                UUID.randomUUID(),
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.COMPLETED_REQUESTED,
                workProofs,
                LocalDateTime.now().minusDays(1),
                null
        );
        offerRepository.save(offerDocument);

        doReturn("get-url-1", "get-url-2").when(minioClient).getPresignedObjectUrl(any());

        mockMvc.perform(get(GET_PRESIGNED_GET_URLS, offerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proofUrls")
                        .value(containsInAnyOrder("get-url-1", "get-url-2")));
    }

    @Test
    void shouldReturnNotFoundWhenOfferMissingForGetUrls() throws Exception {
        UUID offerId = UUID.randomUUID();

        mockMvc.perform(get(GET_PRESIGNED_GET_URLS, offerId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSendCompletionRequest() throws Exception {
        UUID offerId = UUID.randomUUID();
        UUID authorId = UUID.randomUUID();
        UUID executorId = UUID.randomUUID();
        OfferDocument offerDocument = new OfferDocument(
                offerId,
                "Sample Title",
                "Sample Description",
                null,
                authorId,
                executorId,
                new Location(52.2297, 21.0122),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(5000.0),
                OfferStatus.IN_PROGRESS,
                new LinkedHashSet<>(),
                LocalDateTime.now().minusDays(1),
                null
        );
        offerRepository.save(offerDocument);

        CompletionRequest request = new CompletionRequest(
                "Job done",
                List.of("offers/%s/proof-1.png".formatted(offerId), "offers/%s/proof-2.png".formatted(offerId)),
                offerId
        );

        mockMvc.perform(post(REQUEST_COMPLETION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(offerId.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED_REQUESTED"))
                .andExpect(jsonPath("$.completionDescription").value("Job done"))
                .andExpect(jsonPath("$.proofs[*].url")
                        .value(containsInAnyOrder(
                                "offers/%s/proof-1.png".formatted(offerId),
                                "offers/%s/proof-2.png".formatted(offerId)
                        )));
    }

    @Test
    void shouldReturnBadRequestForInvalidCompletionRequest() throws Exception {
        CompletionRequest request = new CompletionRequest(
                "",
                List.of(),
                UUID.randomUUID()
        );

        mockMvc.perform(post(REQUEST_COMPLETION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}

