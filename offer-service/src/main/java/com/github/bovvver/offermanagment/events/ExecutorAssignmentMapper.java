package com.github.bovvver.offermanagment.events;

import com.github.bovvver.contracts.ExecutorAssignedIntegrationEvent;
import com.github.bovvver.contracts.ExecutorAssignmentFailedIntegrationEvent;

public class ExecutorAssignmentMapper {

    public static ExecutorAssignedIntegrationEvent successToIntegrationEvent(ExecutorAssigned event) {
        return new ExecutorAssignedIntegrationEvent(
                event.offerId(),
                event.executorId()
        );
    }

    public static ExecutorAssignmentFailedIntegrationEvent failureToIntegrationEvent(ExecutorAssignmentFailed event) {
        return new ExecutorAssignmentFailedIntegrationEvent(
                event.offerId(),
                event.executorId()
        );
    }
}
