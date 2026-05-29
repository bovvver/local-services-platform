package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.*;

import java.util.ArrayList;
import java.util.HashSet;

public class OfferMapper {

    public static Offer toDomain(OfferDocument document) {

        if (document == null) {
            return null;
        }

        ExecutionDetailsDocument executionDetails = document.getExecutionDetails();
        OfferExecutionDetails execution = new OfferExecutionDetails(
                executionDetails == null || executionDetails.getCompletionDescription() == null
                        ? null
                        : Description.of(executionDetails.getCompletionDescription()),
                executionDetails == null ? null : executionDetails.getRejectionReason(),
                executionDetails == null ? null : executionDetails.getCompletionRequestedAt(),
                executionDetails == null ? new HashSet<>() : executionDetails.getWorkProofs()
        );

        return new Offer(
                OfferId.of(document.getId()),
                Title.of(document.getTitle()),
                Description.of(document.getDescription()),
                execution,
                UserId.of(document.getAuthorId()),
                document.getExecutorId() == null ? null : UserId.of(document.getExecutorId()),
                Location.of(
                        document.getLocation().latitude(),
                        document.getLocation().longitude()
                ),
                document.getServiceCategories(),
                new Salary(document.getSalary()),
                document.getStatus(),
                document.getCreatedAt(),
                new ArrayList<>()
        );
    }

    public static OfferDocument toDocument(Offer offer) {
        ExecutionDetailsDocument executionDetails = new ExecutionDetailsDocument(
                offer.getExecutionDetails().getCompletionDescription() == null
                        ? null
                        : offer.getExecutionDetails().getCompletionDescription().value(),
                offer.getExecutionDetails().getRejectionReason(),
                offer.getExecutionDetails().getCompletionRequestedAt(),
                offer.getExecutionDetails().getWorkProofs()
        );

        return new OfferDocument(
                offer.getId().value(),
                offer.getTitle().value(),
                offer.getDescription().value(),
                executionDetails,
                offer.getAuthorId().value(),
                offer.getExecutorId() != null ? offer.getExecutorId().value() : null,
                offer.getLocation(),
                offer.getServiceCategories(),
                offer.getSalary().value(),
                offer.getStatus(),
                offer.getCreatedAt(),
                null    // managed by MongoDB
        );
    }
}
