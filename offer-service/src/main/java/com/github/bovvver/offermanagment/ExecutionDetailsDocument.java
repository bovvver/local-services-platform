package com.github.bovvver.offermanagment;

import com.github.bovvver.offermanagment.workproofupload.WorkProof;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ExecutionDetailsDocument {

    private String completionDescription;
    private String rejectionReason;
    private LocalDateTime completionRequestedAt;
    private Set<WorkProof> workProofs;
}
