package com.github.bovvver.offermanagment;

import com.github.bovvver.infrastructure.CompletionProofRequiredException;
import com.github.bovvver.infrastructure.OperationNotAllowedInCurrentStateException;
import com.github.bovvver.infrastructure.UnauthorizedParticipantException;
import com.github.bovvver.offermanagment.events.DomainEvent;
import com.github.bovvver.offermanagment.events.ExecutorAssigned;
import com.github.bovvver.offermanagment.events.ExecutorAssignmentFailed;
import com.github.bovvver.offermanagment.offercancellation.OfferCancelledByAuthor;
import com.github.bovvver.offermanagment.offercancellation.OfferCancelledByExecutor;
import com.github.bovvver.offermanagment.vo.*;
import com.github.bovvver.offermanagment.workproofupload.WorkProof;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an offer in the system.
 * <p>
 * An offer is created by a user (author) and may be assigned to an executor.
 * It contains details such as title, description, location, service categories,
 * salary, status, and associated bookings.
 * </p>
 */
public class Offer {

    private final OfferId id;
    private final Title title;
    private final Description description;
    private final OfferExecutionDetails executionDetails;
    private final UserId authorId;
    private UserId executorId;
    private final Location location;
    private final Set<ServiceCategory> serviceCategories;
    private final Salary salary;
    private OfferStatus status;
    private final LocalDateTime createdAt;
    private final List<DomainEvent> domainEvents;

    Offer(final OfferId id,
          final Title title,
          final Description description,
          final OfferExecutionDetails executionDetails,
          final UserId authorId,
          final UserId executorId,
          final Location location,
          final Set<ServiceCategory> serviceCategories,
          final Salary salary,
          final OfferStatus status,
          final LocalDateTime createdAt,
          final List<DomainEvent> domainEvents
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.executionDetails = executionDetails == null ? OfferExecutionDetails.empty() : executionDetails;
        this.authorId = authorId;
        this.executorId = executorId;
        this.location = location;
        this.serviceCategories = serviceCategories;
        this.salary = salary;
        this.status = status;
        this.createdAt = createdAt;
        this.domainEvents = domainEvents;
    }

    /**
     * Constructs a new offer with the required fields.
     * <p>Default values:</p>
     * <ul>
     *     <li>{@link #executorId} = {@code null}</li>
     *     <li>{@link #status} = {@link OfferStatus#OPEN}</li>
     * </ul>
     *
     * @param id                unique identifier of the offer
     * @param title             title of the offer
     * @param description       detailed description
     * @param authorId          identifier of the user who created the offer
     * @param location          location of the offer
     * @param serviceCategories categories of services
     * @param salary            salary or payment information
     */
    Offer(OfferId id,
          Title title,
          Description description,
          UserId authorId,
          Location location,
          Set<ServiceCategory> serviceCategories,
          Salary salary) {

        this(id, title, description, OfferExecutionDetails.empty(), authorId, null,
                location, serviceCategories, salary,
                OfferStatus.OPEN, LocalDateTime.now(),
                new ArrayList<>());
    }

    /**
     * Factory method for creating a new {@code Offer} instance.
     *
     * @param title             title of the offer
     * @param description       detailed description
     * @param authorId          identifier of the user who created the offer
     * @param location          location of the offer
     * @param serviceCategories categories of services
     * @param salary            salary or payment information
     * @return newly created {@code Offer}
     */
    public static Offer create(
            Title title,
            Description description,
            UserId authorId,
            Location location,
            Set<ServiceCategory> serviceCategories,
            Salary salary
    ) {
        if (authorId == null) {
            throw new IllegalArgumentException("UserId cannot be null");
        }
        return new Offer(new OfferId(UUID.randomUUID()), title, description, authorId, location, serviceCategories, salary);
    }

    public void accept(UserId executorId) {
        if (isClosedForBooking()) {
            addIntegrationEvent(new ExecutorAssignmentFailed(this.id.value(), executorId.value()));
            return;
        }
        this.executorId = executorId;
        this.status = OfferStatus.ASSIGNED;
        addIntegrationEvent(new ExecutorAssigned(this.id.value(), executorId.value()));
    }

    public boolean isOwnedBy(UserId userId) {
        return this.authorId.equals(userId);
    }

    public boolean isExecutedBy(UserId userId) {
        return this.executorId.equals(userId);
    }

    public void isParticipant(UserId userId) {
        if (!isOwnedBy(userId) && !isExecutedBy(userId)) {
            throw new UnauthorizedParticipantException();
        }
    }

    public void startExecution(UserId userId) {
        if (!isExecutedBy(userId)) {
            throw new UnauthorizedParticipantException();
        }
        if (this.status != OfferStatus.ASSIGNED) {
            throw new OperationNotAllowedInCurrentStateException(this.status);
        }
        changeStatus(OfferStatus.IN_PROGRESS);
    }

    public void requestCompletion(final String description, final List<String> proofUrls, UserId executorId) {
        if (!isExecutedBy(executorId)) {
            throw new UnauthorizedParticipantException();
        }
        if (this.status != OfferStatus.IN_PROGRESS) {
            throw new OperationNotAllowedInCurrentStateException(this.status);
        }
        if (proofUrls.isEmpty()) {
            throw new CompletionProofRequiredException();
        }

        executionDetails.requestCompletion(Description.of(description));
        executionDetails.addWorkProofs(
                proofUrls.stream().map(el -> new WorkProof(el, LocalDateTime.now())).toList()
        );
        changeStatus(OfferStatus.COMPLETED_REQUESTED);
    }

    public void rejectCompletion(final UserId id, final String reason) {
        if (!isOwnedBy(id)) {
            throw new UnauthorizedParticipantException();
        }
        if (this.status != OfferStatus.COMPLETED_REQUESTED) {
            throw new OperationNotAllowedInCurrentStateException(this.status);
        }
        executionDetails.setRejectionReason(reason);
        changeStatus(OfferStatus.IN_PROGRESS);
    }

    public void acceptCompletion(final UserId id) {
        if (!isOwnedBy(id)) {
            throw new UnauthorizedParticipantException();
        }
        if (this.status != OfferStatus.COMPLETED_REQUESTED) {
            throw new OperationNotAllowedInCurrentStateException(this.status);
        }
        changeStatus(OfferStatus.COMPLETED);
    }

    public void cancel(final UserId userId) {
        if (isClosedForCancellation()) {
            throw new OperationNotAllowedInCurrentStateException(this.status);
        } else if (isOwnedBy(userId)) {
            changeStatus(OfferStatus.CANCELLED);
            addIntegrationEvent(new OfferCancelledByAuthor(this.id.value()));
        } else if (isExecutedBy(userId)) {
            changeStatus(OfferStatus.OPEN);
            addIntegrationEvent(new OfferCancelledByExecutor(this.id.value(), userId.value()));
        } else {
            throw new UnauthorizedParticipantException();
        }
    }

    public void changeStatus(OfferStatus newStatus) {
        if (this.status == newStatus) {
            return;
        }
        this.status = newStatus;
    }

    public List<DomainEvent> pullEvents() {
        List<DomainEvent> copy = List.copyOf(domainEvents);
        domainEvents.clear();
        return copy;
    }

    private boolean isClosedForCancellation() {
        return isClosedForStatus(OfferStatus.OPEN, OfferStatus.IN_NEGOTIATION, OfferStatus.ASSIGNED);
    }

    private boolean isClosedForBooking() {
        return isClosedForStatus(OfferStatus.OPEN, OfferStatus.IN_NEGOTIATION);
    }

    private boolean isClosedForStatus(OfferStatus... allowedStatuses) {
        return !Arrays.asList(allowedStatuses).contains(this.status);
    }

    private void addIntegrationEvent(DomainEvent domainEvent) {
        this.domainEvents.add(domainEvent);
    }

    public OfferId getId() {
        return id;
    }

    public Title getTitle() {
        return title;
    }

    public Description getDescription() {
        return description;
    }

    public OfferExecutionDetails getExecutionDetails() {
        return executionDetails;
    }

    public UserId getAuthorId() {
        return authorId;
    }

    public UserId getExecutorId() {
        return executorId;
    }

    public Location getLocation() {
        return location;
    }

    public Set<ServiceCategory> getServiceCategories() {
        return serviceCategories;
    }

    public Salary getSalary() {
        return salary;
    }

    public OfferStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
