package com.github.bovvver.offermanagment.workproofupload;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.bovvver.BaseIntegrationTest;
import com.github.bovvver.offermanagment.ExecutionDetailsDocument;
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

    private static final UUID OFFER_ID = UUID.randomUUID();
    private static final UUID EXECUTOR_ID = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

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
        createOfferWithStatus(OfferStatus.ASSIGNED);

        PresignedUploadUrlRequest request = new PresignedUploadUrlRequest(
                "proof.png",
                "image/png",
                OFFER_ID
        );

        doReturn("upload-url").when(minioClient).getPresignedObjectUrl(any());

        mockMvc.perform(post(GET_PRESIGNED_UPLOAD_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.uploadUrl").value("upload-url"))
                .andExpect(jsonPath("$.fileId").value(startsWith("offers/" + OFFER_ID + "/")))
                .andExpect(jsonPath("$.fileId").value(endsWith("-proof.png")));
    }

    @Test
    void shouldReturnPresignedGetUrls() throws Exception {
        Set<WorkProof> workProofs = new LinkedHashSet<>(List.of(
                new WorkProof("offers/%s/proof-1.png".formatted(OFFER_ID), LocalDateTime.now()),
                new WorkProof("offers/%s/proof-2.png".formatted(OFFER_ID), LocalDateTime.now())
        ));

        ExecutionDetailsDocument executionDetails = new ExecutionDetailsDocument(
                "Completed",
                null,
                LocalDateTime.now().minusDays(1),
                workProofs
        );
        OfferDocument offerDocument = createOfferWithExecutionDetails(
                executionDetails,
                OfferStatus.COMPLETED_REQUESTED
        );
        offerRepository.save(offerDocument);

        doReturn("get-url-1", "get-url-2").when(minioClient).getPresignedObjectUrl(any());

        mockMvc.perform(get(GET_PRESIGNED_GET_URLS, OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.proofUrls")
                        .value(containsInAnyOrder("get-url-1", "get-url-2")));
    }

    @Test
    void shouldReturnNotFoundWhenOfferMissingForGetUrls() throws Exception {
        mockMvc.perform(get(GET_PRESIGNED_GET_URLS, OFFER_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldSendCompletionRequest() throws Exception {
        ExecutionDetailsDocument executionDetails = new ExecutionDetailsDocument(
                null,
                null,
                null,
                new LinkedHashSet<>()
        );
        OfferDocument offerDocument = createOfferWithExecutionDetails(
                executionDetails,
                OfferStatus.IN_PROGRESS
        );
        offerRepository.save(offerDocument);

        CompletionRequest request = new CompletionRequest(
                "Job done",
                List.of("offers/%s/proof-1.png".formatted(OFFER_ID), "offers/%s/proof-2.png".formatted(OFFER_ID)),
                OFFER_ID
        );

        mockMvc.perform(post(REQUEST_COMPLETION_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.offerId").value(OFFER_ID.toString()))
                .andExpect(jsonPath("$.status").value("COMPLETED_REQUESTED"))
                .andExpect(jsonPath("$.completionDescription").value("Job done"))
                .andExpect(jsonPath("$.proofs[*].url")
                        .value(containsInAnyOrder(
                                "offers/%s/proof-1.png".formatted(OFFER_ID),
                                "offers/%s/proof-2.png".formatted(OFFER_ID)
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

    private OfferDocument createOfferWithStatus(OfferStatus status) {
        return createOfferWithExecutionDetails(
                new ExecutionDetailsDocument(null, null, null, null),
                status
        );
    }

    private OfferDocument createOfferWithExecutionDetails(
            ExecutionDetailsDocument executionDetails,
            OfferStatus status
    ) {
        OfferDocument offer = new OfferDocument(
                OFFER_ID,
                "Test Offer",
                "Test Description",
                executionDetails,
                UUID.randomUUID(),
                EXECUTOR_ID,
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.HOME_SERVICES),
                BigDecimal.valueOf(1000.0),
                status,
                null,
                null
        );

        offerRepository.save(offer);
        return offer;
    }
}
