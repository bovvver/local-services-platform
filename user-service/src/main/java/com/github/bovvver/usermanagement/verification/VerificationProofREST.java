package com.github.bovvver.usermanagement.verification;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class VerificationProofREST {

    public static final String GET_PRESIGNED_UPLOAD_URL = "/files/presigned-upload";
    public static final String GET_PRESIGNED_GET_URLS = "/files/{userId}/urls";
    public static final String SEND_VERIFICATION_DATA_URL = "/files/verify";
    public static final String VERIFY_USER_URL = "/files/verify/{userId}";
    public static final String REJECT_USER_URL = "/files/reject/{userId}";

    private final VerificationProofUploadService verificationProofUploadService;
    private final VerificationProcessingService verificationProcessingService;

    @PostMapping(path = GET_PRESIGNED_UPLOAD_URL)
    public ResponseEntity<PresignedUploadUrlResponse> getPresignedUploadURL(@Valid @RequestBody PresignedUploadUrlRequest request) {
        return ResponseEntity.ok(verificationProofUploadService.getPresignedUploadURL(request));
    }

    @GetMapping(path = GET_PRESIGNED_GET_URLS)
    public ResponseEntity<PresignedGetUrlResponse> getPresignedGetURL(@PathVariable UUID userId) {
        return ResponseEntity.ok(verificationProofUploadService.getPresignedGetURLs(userId));
    }

    @PostMapping(path = SEND_VERIFICATION_DATA_URL)
    public ResponseEntity<VerificationDataResponse> sendVerificationData(@Valid @RequestBody VerificationDataRequest request) {
        return ResponseEntity.ok(verificationProcessingService.sendVerificationData(request));
    }

    @PreAuthorize("hasRole('VERIFIER')")
    @PostMapping(path = VERIFY_USER_URL)
    public ResponseEntity<Void> verify(@PathVariable UUID userId) {
        verificationProcessingService.verify(userId);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasRole('VERIFIER')")
    @PostMapping(path = REJECT_USER_URL)
    public ResponseEntity<Void> reject(@PathVariable UUID userId) {
        verificationProcessingService.reject(userId);
        return ResponseEntity.ok().build();
    }
}
