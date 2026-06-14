package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.bookingcreation.BookingCreated;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.infrastructure.BookingNotExpiredYetException;
import com.github.bovvver.bookingmanagement.infrastructure.OperationNotAllowedInCurrentStateException;
import com.github.bovvver.bookingmanagement.infrastructure.OutdatedNegotiationPositionException;
import com.github.bovvver.bookingmanagement.infrastructure.OwnNegotiationProposalDecisionException;
import com.github.bovvver.bookingmanagement.negotiation.NegotiationStarted;
import com.github.bovvver.bookingmanagement.resolvebookingdecision.BookingAccepted;
import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class BookingTest {

    @Test
    void shouldCreateBookingWithDefaultValues() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(bookingId, userId, offerId, salary);

        assertThat(booking).isNotNull();
        assertThat(booking.getId()).isEqualTo(bookingId);
        assertThat(booking.getUserId()).isEqualTo(userId);
        assertThat(booking.getOfferId()).isEqualTo(offerId);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldCreateBookingWithGeneratedIdWhenUsingFactoryWithoutId() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);

        assertThat(booking.getId()).isNotNull();
        assertThat(booking.getUserId()).isEqualTo(userId);
        assertThat(booking.getOfferId()).isEqualTo(offerId);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getCreatedAt()).isNotNull();
        assertThat(booking.getSalary()).isEqualTo(salary);
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        assertThrows(IllegalArgumentException.class, () -> Booking.create(bookingId, null, offerId, salary));
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNullInFactoryWithoutId() {
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        assertThrows(IllegalArgumentException.class, () -> Booking.create(null, offerId, salary));
    }

    @Test
    void shouldThrowExceptionWhenOfferIdIsNull() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        assertThrows(IllegalArgumentException.class, () -> Booking.create(bookingId, userId, null, salary));
    }

    @Test
    void shouldThrowExceptionWhenOfferIdIsNullInFactoryWithoutId() {
        UserId userId = UserId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        assertThrows(IllegalArgumentException.class, () -> Booking.create(userId, null, salary));
    }

    @Test
    void shouldBeginNegotiationWhenStatusIsPending() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);

        booking.beginNegotiation(salary, userId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.IN_NEGOTIATION);
        assertThat(booking.getNegotiation()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenBeginNegotiationWithInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(OperationNotAllowedInCurrentStateException.class, () -> booking.beginNegotiation(salary, userId));
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNull() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(OperationNotAllowedInCurrentStateException.class, () -> booking.beginNegotiation(null, userId));
    }

    @Test
    void shouldAcceptBookingWhenStatusIsPendingOrInNegotiation() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);

        booking.accept();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
    }

    @Test
    void shouldRejectBookingWhenStatusIsPendingOrInNegotiation() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);

        booking.reject();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void shouldThrowExceptionWhenAcceptWithInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.reject();

        assertThrows(OperationNotAllowedInCurrentStateException.class, booking::accept);
    }

    @Test
    void shouldThrowExceptionWhenRejectWithInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(OperationNotAllowedInCurrentStateException.class, booking::reject);
    }

    @Test
    void shouldCancelNegotiationWhenStatusIsInNegotiation() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);

        booking.beginNegotiation(salary, userId);
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.IN_NEGOTIATION);
        assertThat(booking.getNegotiation()).isNotNull();

        booking.cancelNegotiation();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getNegotiation()).isNull();
    }

    @Test
    void shouldRegisterBookingCreatedEventOnCreation() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(bookingId, userId, offerId, salary);

        List<DomainEvent> events = booking.getDomainEvents();
        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(BookingCreated.class);
        BookingCreated created = (BookingCreated) events.getFirst();
        assertThat(created.bookingId()).isEqualTo(bookingId);
        assertThat(created.userId()).isEqualTo(userId);
        assertThat(created.offerId()).isEqualTo(offerId);
        assertThat(created.salary()).isEqualTo(salary);
    }

    @Test
    void pullDomainEventsShouldReturnBookingCreatedEventAndClearList() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(bookingId, userId, offerId, salary);

        List<DomainEvent> pulled = booking.pullDomainEvents();

        assertThat(pulled).hasSize(1);
        assertThat(pulled.getFirst()).isInstanceOf(BookingCreated.class);
        assertThat(booking.getDomainEvents()).isEmpty();
    }

    @Test
    void pullDomainEventsShouldReturnEmptyListWhenNoEvents() {
        Booking booking = Booking.create(UserId.of(UUID.randomUUID()), OfferId.of(UUID.randomUUID()), Salary.of(50000.0));

        // first pull clears BookingCreated
        booking.pullDomainEvents();

        List<DomainEvent> pulledAgain = booking.pullDomainEvents();

        assertThat(pulledAgain).isEmpty();
        assertThat(booking.getDomainEvents()).isEmpty();
    }

    @Test
    void shouldRegisterNegotiationStartedEventWhenBeginNegotiation() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);

        booking.beginNegotiation(salary, authorId);

        List<DomainEvent> events = booking.getDomainEvents();
        assertThat(events).hasSize(2);
        assertThat(events.get(0)).isInstanceOf(BookingCreated.class);
        assertThat(events.get(1)).isInstanceOf(NegotiationStarted.class);
        NegotiationStarted started = (NegotiationStarted) events.get(1);
        assertThat(started.offerId()).isEqualTo(offerId);
        assertThat(started.bookingId()).isEqualTo(booking.getId());
    }

    @Test
    void pullDomainEventsShouldReturnBookingCreatedAndNegotiationStartedInOrder() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);

        List<DomainEvent> pulled = booking.pullDomainEvents();

        assertThat(pulled).hasSize(2);
        assertThat(pulled.get(0)).isInstanceOf(BookingCreated.class);
        assertThat(pulled.get(1)).isInstanceOf(NegotiationStarted.class);
        assertThat(booking.getDomainEvents()).isEmpty();
    }

    @Test
    void shouldRegisterBookingAcceptedEventWhenAcceptFromPending() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);

        booking.accept();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
        List<DomainEvent> events = booking.getDomainEvents();
        assertThat(events).hasSize(2);
        assertThat(events.get(0)).isInstanceOf(BookingCreated.class);
        assertThat(events.get(1)).isInstanceOf(BookingAccepted.class);
        BookingAccepted accepted = (BookingAccepted) events.get(1);
        assertThat(accepted.offerId()).isEqualTo(offerId);
        assertThat(accepted.userId()).isEqualTo(userId);
        assertThat(accepted.bookingId()).isEqualTo(booking.getId());
    }

    @Test
    void shouldAddPositionToNegotiationWhenStatusIsInNegotiation() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary initialSalary = Salary.of(50000.0);
        Salary counterSalary = Salary.of(55000.0);

        Booking booking = Booking.create(executorId, offerId, initialSalary);
        booking.beginNegotiation(initialSalary, authorId);

        booking.addPositionToNegotiation(counterSalary, executorId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.IN_NEGOTIATION);
        assertThat(booking.getNegotiation()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenAddPositionToNegotiationWithoutOngoingNegotiation() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);

        assertThrows(OperationNotAllowedInCurrentStateException.class,
                () -> booking.addPositionToNegotiation(Salary.of(55000.0), executorId));
    }

    @Test
    void shouldThrowExceptionWhenAddPositionToNegotiationAfterNegotiationCanceled() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);
        booking.cancelNegotiation();

        assertThrows(OperationNotAllowedInCurrentStateException.class,
                () -> booking.addPositionToNegotiation(Salary.of(55000.0), executorId));
    }

    @Test
    void shouldNotChangeStatusWhenBeginNegotiationFailsDueToInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(OperationNotAllowedInCurrentStateException.class,
                () -> booking.beginNegotiation(salary, UserId.of(UUID.randomUUID())));

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
        assertThat(booking.getNegotiation()).isNull();
    }

    @Test
    void shouldNotChangeStatusOrNegotiationWhenCancelNegotiationFails() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);

        assertThrows(OperationNotAllowedInCurrentStateException.class, booking::cancelNegotiation);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
        assertThat(booking.getNegotiation()).isNull();
    }

    @Test
    void shouldNotRegisterAdditionalEventsWhenActionFailsOnInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);

        // clear initial BookingCreated event
        booking.pullDomainEvents();

        booking.accept();
        List<DomainEvent> eventsAfterAccept = booking.pullDomainEvents();
        assertThat(eventsAfterAccept).hasSize(1);
        assertThat(eventsAfterAccept.getFirst()).isInstanceOf(BookingAccepted.class);

        assertThrows(OperationNotAllowedInCurrentStateException.class, booking::reject);

        assertThat(booking.getDomainEvents()).isEmpty();
    }

    @Test
    void shouldAcceptLatestNegotiationProposalWhenDecidedByOtherParty() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Salary initialSalary = Salary.of(50000.0);
        Salary counterSalary = Salary.of(55000.0);

        Booking booking = Booking.create(executorId, offerId, initialSalary);
        booking.beginNegotiation(initialSalary, authorId);

        sleepMillis(2);

        booking.addPositionToNegotiation(counterSalary, authorId);

        NegotiationPositionId latestPositionId = booking.getNegotiation().getPositions().getLast().getId();

        booking.acceptNegotiationProposal(executorId, latestPositionId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
        assertThat(booking.getSalary()).isEqualTo(counterSalary);
    }

    @Test
    void shouldRejectLatestNegotiationProposalWhenDecidedByOtherPartyAndReturnToPending() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Salary initialSalary = Salary.of(50000.0);
        Salary counterSalary = Salary.of(55000.0);

        Booking booking = Booking.create(executorId, offerId, initialSalary);
        booking.beginNegotiation(initialSalary, authorId);

        sleepMillis(2);
        booking.addPositionToNegotiation(counterSalary, authorId);

        NegotiationPositionId latestPositionId = booking.getNegotiation().getPositions().getLast().getId();

        booking.rejectNegotiationProposal(executorId, latestPositionId);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.PENDING);
    }

    @Test
    void shouldThrowExceptionWhenAcceptingOutdatedNegotiationProposal() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Salary initialSalary = Salary.of(50000.0);
        Salary counterSalary = Salary.of(55000.0);

        Booking booking = Booking.create(executorId, offerId, initialSalary);
        booking.beginNegotiation(initialSalary, authorId);

        sleepMillis(2);

        booking.addPositionToNegotiation(counterSalary, executorId);
        NegotiationPositionId executorsPositionId = booking.getNegotiation().getPositions().get(1).getId();

        sleepMillis(2);

        booking.addPositionToNegotiation(Salary.of(60000.0), authorId);

        assertThrows(OutdatedNegotiationPositionException.class,
                () -> booking.acceptNegotiationProposal(authorId, executorsPositionId));
    }

    @Test
    void shouldThrowExceptionWhenDecidingOnOwnLatestNegotiationProposal() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Salary initialSalary = Salary.of(50000.0);
        Salary counterSalary = Salary.of(55000.0);

        Booking booking = Booking.create(executorId, offerId, initialSalary);
        booking.beginNegotiation(initialSalary, authorId);

        sleepMillis(2);

        booking.addPositionToNegotiation(counterSalary, executorId);

        NegotiationPositionId latestPositionId = booking.getNegotiation().getPositions().getLast().getId();

        assertThrows(OwnNegotiationProposalDecisionException.class,
                () -> booking.acceptNegotiationProposal(executorId, latestPositionId));
    }

    @Test
    void shouldCancelBookingByAuthorFromPendingState() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));

        booking.cancelByAuthor();

        assertThat(booking.getStatus())
                .isEqualTo(BookingStatus.CANCELED_BY_AUTHOR);
    }

    @Test
    void shouldCancelBookingByExecutorFromPendingState() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));

        booking.cancelByExecutor();

        assertThat(booking.getStatus())
                .isEqualTo(BookingStatus.CANCELED_BY_EXECUTOR);
    }

    @Test
    void shouldCancelBookingWithActiveNegotiationAndClearIt() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));
        booking.beginNegotiation(Salary.of(50000.0), authorId);

        booking.cancelByAuthor();

        assertThat(booking.getStatus())
                .isEqualTo(BookingStatus.CANCELED_BY_AUTHOR);

        assertThat(booking.getNegotiation()).isNull();
    }

    @Test
    void shouldThrowExceptionWhenAuthorCancelsRejectedBooking() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));
        booking.reject();

        assertThrows(OperationNotAllowedInCurrentStateException.class,
                booking::cancelByAuthor);
    }

    @Test
    void shouldThrowExceptionWhenExecutorCancelsRejectedBooking() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));
        booking.reject();

        assertThrows(OperationNotAllowedInCurrentStateException.class,
                booking::cancelByExecutor);
    }

    @Test
    void shouldExpireBookingWhenExpireCalled() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));

        booking.expire(booking.getExpiresAt().plusDays(1));

        assertThat(booking.getStatus())
                .isEqualTo(BookingStatus.EXPIRED);
    }

    @Test
    void shouldThrowExceptionWhenTryingToExpireNonPendingOrInNegotiationBooking() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));
        booking.accept();

        assertThrows(OperationNotAllowedInCurrentStateException.class,
                () -> booking.expire(LocalDateTime.now().plusDays(1)));
    }

    @Test
    void shouldThrowExceptionWhenTryingToExpireBookingThatIsNotExpiredYet() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());

        Booking booking = Booking.create(executorId, offerId, Salary.of(50000.0));

        assertThrows(BookingNotExpiredYetException.class,
                () -> booking.expire(LocalDateTime.now().minusDays(1)));
    }

    private static void sleepMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
    }
}
