package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.vo.Description;
import com.github.bovvver.offermanagment.workproofupload.WorkProof;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class OfferExecutionDetails {

    private Description completionDescription;
    private String rejectionReason;
    private LocalDateTime completionRequestedAt;
    private final Set<WorkProof> workProofs;

    OfferExecutionDetails(
            Description completionDescription,
            String rejectionReason,
            LocalDateTime completionRequestedAt,
            Set<WorkProof> workProofs
    ) {
        this.completionDescription = completionDescription;
        this.rejectionReason = rejectionReason;
        this.completionRequestedAt = completionRequestedAt;
        this.workProofs = workProofs == null ? new HashSet<>() : new HashSet<>(workProofs);
    }

    public static OfferExecutionDetails empty() {
        return new OfferExecutionDetails(null, null, null, new HashSet<>());
    }

    void requestCompletion(Description completionDescription) {
        this.completionDescription = completionDescription;
        this.completionRequestedAt = LocalDateTime.now();
        this.rejectionReason = null;
    }

    void addWorkProofs(Collection<WorkProof> proofs) {
        if (proofs == null || proofs.isEmpty()) {
            return;
        }
        this.workProofs.addAll(proofs);
    }

    public Description getCompletionDescription() {
        return completionDescription;
    }

    public String getRejectionReason() {
        return rejectionReason;
    }

    public void setRejectionReason(String rejectionReason) {
        this.rejectionReason = rejectionReason;
    }

    public LocalDateTime getCompletionRequestedAt() {
        return completionRequestedAt;
    }

    public Set<WorkProof> getWorkProofs() {
        return workProofs;
    }
}
