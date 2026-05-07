package com.github.bovvver.offermanagment.workproofupload;

import java.util.UUID;

record PresignedUrlRequest(
        String fileName,
        String contentType,
        UUID offerId
) {
}
