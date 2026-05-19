package com.github.bovvver.offermanagment.workproofupload;

import java.time.LocalDateTime;

public record WorkProof(
        String url,
        LocalDateTime uploadedAt
) {
}
