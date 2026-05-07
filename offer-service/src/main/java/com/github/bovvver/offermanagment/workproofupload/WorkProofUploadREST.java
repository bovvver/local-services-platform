package com.github.bovvver.offermanagment.workproofupload;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class WorkProofUploadREST {

    static final String GET_PRESIGNED_UPLOAD_URL = "/files/presigned-upload";

    private final WorkProofUploadService workProofUploadService;

    @PostMapping(path = GET_PRESIGNED_UPLOAD_URL)
    ResponseEntity<PresignedUrlResponse> getPresignedURL(@RequestBody PresignedUrlRequest request) {
        return ResponseEntity.ok(workProofUploadService.getPresignedURL(request));
    }
}
