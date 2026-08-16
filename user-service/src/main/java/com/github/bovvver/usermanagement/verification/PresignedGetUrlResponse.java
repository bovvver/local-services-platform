package com.github.bovvver.usermanagement.verification;

import java.util.List;

public record PresignedGetUrlResponse(List<String> proofUrls) {
}
