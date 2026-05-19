package com.github.bovvver.offermanagment.workproofupload;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class WorkProofREST {

    static final String GET_PRESIGNED_UPLOAD_URL = "/files/presigned-upload";
    static final String GET_PRESIGNED_GET_URLS = "/files/{offerId}/urls";
    static final String REQUEST_COMPLETION_URL = "/offers/completion-request";

    private final WorkProofUploadService workProofUploadService;
    private final CompletionProcessingService completionProcessingService;

    @PostMapping(path = GET_PRESIGNED_UPLOAD_URL)
    ResponseEntity<PresignedUploadUrlResponse> getPresignedUploadURL(@Valid @RequestBody PresignedUploadUrlRequest request) {
        return ResponseEntity.ok(workProofUploadService.getPresignedUploadURL(request));
    }

    @GetMapping(path = GET_PRESIGNED_GET_URLS)
    ResponseEntity<PresignedGetUrlResponse> getPresignedGetURL(@PathVariable UUID offerId) {
        return ResponseEntity.ok(workProofUploadService.getPresignedGetURLs(offerId));
    }

    @PostMapping(path = REQUEST_COMPLETION_URL)
    ResponseEntity<OfferExecutionResponse> sendCompletionRequest(@Valid @RequestBody CompletionRequest request) {
        return ResponseEntity.ok(completionProcessingService.sendCompletionRequest(request));
    }
}
