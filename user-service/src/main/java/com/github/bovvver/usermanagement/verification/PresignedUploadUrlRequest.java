package com.github.bovvver.usermanagement.verification;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PresignedUploadUrlRequest(
        @NotBlank
        @Size(max = 255)
        String fileName,

        @NotBlank
        @Size(max = 255)
        @Pattern(
                regexp = "^[a-zA-Z0-9!#$&^_.+-]+/[a-zA-Z0-9!#$&^_.+-]+$",
                message = "contentType must be a valid MIME type, e.g. image/png"
        )
        String contentType
) {
}
