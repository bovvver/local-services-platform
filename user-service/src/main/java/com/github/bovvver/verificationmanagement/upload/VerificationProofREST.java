package com.github.bovvver.verificationmanagement.upload;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
class VerificationProofREST {

    static final String GET_PRESIGNED_UPLOAD_URL = "/files/presigned-upload";
    static final String GET_PRESIGNED_GET_URLS = "/files/{userId}/urls";
    static final String SEND_VERIFICATION_DATA_URL = "/files/verify";
    static final String VERIFY_USER_URL = "/files/verify/{userId}";
    static final String REJECT_USER_URL = "/files/reject/{userId}";


    private final VerificationProofUploadService verificationProofUploadService;
    private final VerificationProcessingService verificationProcessingService;

    @PostMapping(path = GET_PRESIGNED_UPLOAD_URL)
    ResponseEntity<PresignedUploadUrlResponse> getPresignedUploadURL(@Valid @RequestBody PresignedUploadUrlRequest request) {
        return ResponseEntity.ok(verificationProofUploadService.getPresignedUploadURL(request));
    }

    @GetMapping(path = GET_PRESIGNED_GET_URLS)
    ResponseEntity<PresignedGetUrlResponse> getPresignedGetURL(@PathVariable UUID userId) {
        return ResponseEntity.ok(verificationProofUploadService.getPresignedGetURLs(userId));
    }

    @PostMapping(path = SEND_VERIFICATION_DATA_URL)
    ResponseEntity<VerificationDataResponse> sendVerificationData(@Valid @RequestBody VerificationDataRequest request) {
        return ResponseEntity.ok(verificationProcessingService.sendVerificationData(request));
    }

    @PreAuthorize("hasRole('VERIFIER')")
    @PostMapping(path = VERIFY_USER_URL)
    ResponseEntity<Void> verify(@PathVariable UUID userId) {
        verificationProcessingService.verify(userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('VERIFIER')")
    @PostMapping(path = REJECT_USER_URL)
    ResponseEntity<Void> reject(@PathVariable UUID userId) {
        verificationProcessingService.reject(userId);
        return ResponseEntity.ok().build();
    }
}
