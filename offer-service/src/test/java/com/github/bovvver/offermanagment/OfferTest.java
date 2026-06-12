package com.github.bovvver.offermanagment;

import com.github.bovvver.infrastructure.CompletionProofRequiredException;
import com.github.bovvver.infrastructure.OperationNotAllowedInCurrentStateException;
import com.github.bovvver.infrastructure.UnauthorizedParticipantException;
import com.github.bovvver.offermanagment.events.ExecutorAssigned;
import com.github.bovvver.offermanagment.events.ExecutorAssignmentFailed;
import com.github.bovvver.offermanagment.vo.*;
import com.github.bovvver.offermanagment.workproofupload.WorkProof;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferTest {

    private static final Location LOCATION = new Location(40.7128, -74.0060);

    @Test
    void shouldCreateOfferWithOpenStatus() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.OPEN);
    }

    @Test
    void shouldCreateOfferWithNoExecutor() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));

        assertThat(offer.getExecutorId()).isNull();
    }

    @Test
    void shouldCreateOfferWithNoDomainEvents() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));

        assertThat(offer.pullEvents()).isEmpty();
    }

    @Test
    void shouldThrowWhenCreatingOfferWithNullAuthorId() {
        assertThatThrownBy(() -> createValidOffer(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId cannot be null");
    }

    @Test
    void shouldAssignExecutorAndSetStatusToAssignedWhenOfferIsOpen() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());

        offer.accept(executor);

        assertThat(offer.getExecutorId()).isEqualTo(executor);
        assertThat(offer.getStatus()).isEqualTo(OfferStatus.ASSIGNED);
    }

    @Test
    void shouldPublishExecutorAssignedEventWhenOfferIsOpen() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());

        offer.accept(executor);

        assertThat(offer.pullEvents())
                .hasSize(1)
                .first()
                .isInstanceOf(ExecutorAssigned.class);
    }

    @Test
    void shouldPublishExecutorAssignedEventWhenOfferIsInNegotiation() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        offer.changeStatus(OfferStatus.IN_NEGOTIATION);
        UserId executor = UserId.of(UUID.randomUUID());

        offer.accept(executor);

        assertThat(offer.pullEvents())
                .hasSize(1)
                .first()
                .isInstanceOf(ExecutorAssigned.class);
    }

    @Test
    void shouldPublishExecutorAssignmentFailedEventWhenOfferIsAlreadyAssigned() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        offer.accept(UserId.of(UUID.randomUUID()));
        offer.pullEvents();

        offer.accept(UserId.of(UUID.randomUUID()));

        assertThat(offer.pullEvents())
                .hasSize(1)
                .first()
                .isInstanceOf(ExecutorAssignmentFailed.class);
    }

    @Test
    void shouldNotChangeExecutorWhenOfferIsClosedForBooking() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId firstExecutor = UserId.of(UUID.randomUUID());
        offer.accept(firstExecutor);
        offer.pullEvents();

        offer.accept(UserId.of(UUID.randomUUID()));

        assertThat(offer.getExecutorId()).isEqualTo(firstExecutor);
    }

    @Test
    void shouldReturnTrueWhenOfferIsOwnedByAuthor() {
        UserId authorId = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(authorId);

        assertThat(offer.isOwnedBy(authorId)).isTrue();
    }

    @Test
    void shouldReturnFalseWhenOfferIsNotOwnedByGivenUser() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));

        assertThat(offer.isOwnedBy(UserId.of(UUID.randomUUID()))).isFalse();
    }

    @Test
    void shouldChangeStatus() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));

        offer.changeStatus(OfferStatus.IN_NEGOTIATION);

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.IN_NEGOTIATION);
    }

    @Test
    void shouldNotChangeStatusWhenNewStatusIsSameAsCurrent() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));

        offer.changeStatus(OfferStatus.OPEN);

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.OPEN);
    }

    @Test
    void shouldClearEventsAfterPulling() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        offer.accept(UserId.of(UUID.randomUUID()));

        offer.pullEvents();

        assertThat(offer.pullEvents()).isEmpty();
    }

    @Test
    void shouldReturnAllEventsOnPull() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        offer.accept(UserId.of(UUID.randomUUID()));

        List<?> events = offer.pullEvents();

        assertThat(events).hasSize(1);
    }

    @Test
    void shouldStartExecutionAndChangeStatusToInProgressWhenExecutorIsAssigned() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);
        offer.pullEvents();

        offer.startExecution(executor);

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.IN_PROGRESS);
    }

    @Test
    void shouldThrowWhenStartingExecutionByNonExecutor() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);

        assertThatThrownBy(() -> offer.startExecution(UserId.of(UUID.randomUUID())))
                .isInstanceOf(UnauthorizedParticipantException.class);
    }

    @Test
    void shouldThrowWhenStartingExecutionInStatusOtherThanAssigned() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);
        offer.changeStatus(OfferStatus.IN_NEGOTIATION);

        assertThatThrownBy(() -> offer.startExecution(executor))
                .isInstanceOf(OperationNotAllowedInCurrentStateException.class);
    }

    @Test
    void shouldRequestCompletionAndStoreProofs() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);
        offer.startExecution(executor);

        List<String> proofUrls = List.of("https://proofs.local/1", "https://proofs.local/2");
        String completionDescription = "Job done";

        offer.requestCompletion(completionDescription, proofUrls, executor);

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.COMPLETED_REQUESTED);
        assertThat(offer.getExecutionDetails().getCompletionDescription().value()).isEqualTo(completionDescription);
        assertThat(offer.getExecutionDetails().getWorkProofs()).hasSize(2);
        assertThat(offer.getExecutionDetails().getWorkProofs().stream().map(WorkProof::url))
                .containsExactlyInAnyOrderElementsOf(proofUrls);
    }

    @Test
    void shouldThrowWhenRequestingCompletionInWrongStatus() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);

        assertThatThrownBy(() -> offer.requestCompletion("Done", List.of("proof"), executor))
                .isInstanceOf(OperationNotAllowedInCurrentStateException.class);
    }

    @Test
    void shouldThrowWhenRequestingCompletionWithoutProofs() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);
        offer.startExecution(executor);

        assertThatThrownBy(() -> offer.requestCompletion("Done", List.of(), executor))
                .isInstanceOf(CompletionProofRequiredException.class);
    }

    @Test
    void shouldAcceptCompletionRequest() {
        UserId author = UserId.of(UUID.randomUUID());
        UserId executor = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        offer.accept(executor);
        offer.startExecution(executor);
        offer.changeStatus(OfferStatus.COMPLETED_REQUESTED);

        offer.acceptCompletion(author);

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.COMPLETED);
    }

    @Test
    void shouldRejectCompletionRequest() {
        UserId author = UserId.of(UUID.randomUUID());
        UserId executor = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        offer.accept(executor);
        offer.startExecution(executor);
        offer.changeStatus(OfferStatus.COMPLETED_REQUESTED);

        offer.rejectCompletion(author, "Work is not finished");

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.IN_PROGRESS);
        assertThat(offer.getExecutionDetails().getRejectionReason())
                .isEqualTo("Work is not finished");
    }

    @Test
    void shouldThrowWhenNonOwnerAcceptsCompletion() {
        UserId author = UserId.of(UUID.randomUUID());
        UserId executor = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        offer.accept(executor);
        offer.startExecution(executor);
        offer.changeStatus(OfferStatus.COMPLETED_REQUESTED);

        assertThatThrownBy(() -> offer.acceptCompletion(executor))
                .isInstanceOf(UnauthorizedParticipantException.class);
    }

    @Test
    void shouldThrowWhenNonOwnerRejectsCompletion() {
        UserId author = UserId.of(UUID.randomUUID());
        UserId executor = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        offer.accept(executor);
        offer.startExecution(executor);
        offer.changeStatus(OfferStatus.COMPLETED_REQUESTED);

        assertThatThrownBy(() -> offer.rejectCompletion(executor, "Reason"))
                .isInstanceOf(UnauthorizedParticipantException.class);
    }

    @Test
    void shouldThrowWhenAcceptingCompletionInInvalidState() {
        UserId author = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        assertThatThrownBy(() -> offer.acceptCompletion(author))
                .isInstanceOf(OperationNotAllowedInCurrentStateException.class);
    }

    @Test
    void shouldThrowWhenRejectingCompletionInInvalidState() {
        UserId author = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        assertThatThrownBy(() -> offer.rejectCompletion(author, "Reason"))
                .isInstanceOf(OperationNotAllowedInCurrentStateException.class);
    }

    @Test
    void shouldCancelAnOfferByAuthor() {
        UserId author = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        offer.cancel(author);
        assertThat(offer.getStatus()).isEqualTo(OfferStatus.CANCELLED);
    }

    @Test
    void shouldCancelAnOfferByExecutor() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());

        offer.accept(executor);
        offer.startExecution(executor);

        offer.changeStatus(OfferStatus.ASSIGNED);
        offer.cancel(executor);

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.OPEN);
    }

    @Test
    void shouldThrowWhenOfferIsClosedForCancellation() {
        UserId author = UserId.of(UUID.randomUUID());
        UserId executor = UserId.of(UUID.randomUUID());
        Offer offer = createValidOffer(author);

        offer.accept(executor);
        offer.startExecution(executor);
        offer.changeStatus(OfferStatus.IN_PROGRESS);

        assertThatThrownBy(() -> offer.cancel(author))
                .isInstanceOf(OperationNotAllowedInCurrentStateException.class);
    }

    @Test
    void shouldThrowWhenUnauthorizedParticipant() {
        Offer offer = createValidOffer(UserId.of(UUID.randomUUID()));
        UserId executor = UserId.of(UUID.randomUUID());
        offer.accept(executor);
        offer.startExecution(executor);
        offer.changeStatus(OfferStatus.OPEN);

        assertThatThrownBy(() -> offer.cancel(UserId.of(UUID.randomUUID())))
                .isInstanceOf(UnauthorizedParticipantException.class);
    }

    private Offer createValidOffer(UserId authorId) {
        return Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                authorId,
                LOCATION,
                Set.of(ServiceCategory.AUTOMOTIVE),
                Salary.of(1000.0)
        );
    }
}
