package com.github.bovvver.contracts;

import java.util.UUID;

public record AssignExecutorCommand(
        UUID offerId,
        UUID userId) {
}
