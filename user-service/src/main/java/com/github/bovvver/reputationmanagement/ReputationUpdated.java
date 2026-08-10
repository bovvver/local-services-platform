package com.github.bovvver.reputationmanagement;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.vo.Rating;
import com.github.bovvver.vo.UserId;

public record ReputationUpdated(
        UserId userId,
        Rating averageRating
) implements DomainEvent {
}
