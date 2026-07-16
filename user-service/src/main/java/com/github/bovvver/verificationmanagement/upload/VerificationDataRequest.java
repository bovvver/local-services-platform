package com.github.bovvver.verificationmanagement.upload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record VerificationDataRequest(
        @NotEmpty
        @Size(max = 10)
        List<@NotBlank String> proofUrls
) {
}
