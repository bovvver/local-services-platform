package com.github.bovvver.config;

import java.time.LocalDateTime;

record ErrorResponse(
        int status,
        String message,
        LocalDateTime timestamp
) {
}
