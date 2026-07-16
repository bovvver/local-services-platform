package com.github.bovvver.verificationmanagement.upload;

import java.util.List;

public record PresignedGetUrlResponse(List<String> proofUrls) {
}
