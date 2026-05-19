package com.github.bovvver.offermanagment.workproofupload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

record CompletionRequest(
        @NotBlank
        @Size(max = 2000)
        String description,

        @NotEmpty
        @Size(max = 20)
        List<@NotBlank String> proofUrls,

        @NotNull
        UUID offerId
) {
}
