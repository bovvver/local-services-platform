package com.github.bovvver.profilemanagement;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
class ProfileModificationREST {

    static final String UPDATE_PROFILE_PATH = "/update/profile";

    private final ProfileModificationService profileModificationService;

    @PostMapping(path = UPDATE_PROFILE_PATH)
    ResponseEntity<ProfileUpdateResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(profileModificationService.updateProfile(request));
    }
}
