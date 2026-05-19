package com.github.bovvver.offermanagment.workproofupload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.UUID;

record PresignedUploadUrlRequest(
        @NotBlank
        @Size(max = 255)
        String fileName,

        @NotBlank
        @Size(max = 255)
        @Pattern(
                regexp = "^[a-zA-Z0-9!#$&^_.+-]+/[a-zA-Z0-9!#$&^_.+-]+$",
                message = "contentType must be a valid MIME type, e.g. image/png"
        )
        String contentType,

        @NotNull
        UUID offerId
) {
}
