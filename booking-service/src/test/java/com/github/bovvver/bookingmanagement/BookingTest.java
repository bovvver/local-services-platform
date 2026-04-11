package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.bookingcreation.BookingCreated;
import com.github.bovvver.bookingmanagement.event.DomainEvent;
import com.github.bovvver.bookingmanagement.infrastructure.BookingOwnershipException;
import com.github.bovvver.bookingmanagement.infrastructure.InvalidBookingStatusException;
import com.github.bovvver.bookingmanagement.negotiation.NegotiationStarted;
import com.github.bovvver.bookingmanagement.resolvebookingdecision.BookingAccepted;
import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.Test;

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

        assertThrows(InvalidBookingStatusException.class, () -> booking.beginNegotiation(salary, userId));
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNull() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(InvalidBookingStatusException.class, () -> booking.beginNegotiation(null, userId));
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

        assertThrows(InvalidBookingStatusException.class, booking::accept);
    }

    @Test
    void shouldThrowExceptionWhenRejectWithInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(InvalidBookingStatusException.class, booking::reject);
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
    void shouldThrowExceptionWhenCancelNegotiationWithInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);

        assertThrows(InvalidBookingStatusException.class, booking::cancelNegotiation);

        booking.beginNegotiation(salary, userId);
        booking.accept();

        assertThrows(InvalidBookingStatusException.class, booking::cancelNegotiation);
    }

    @Test
    void shouldResolveNegotiationPartyForExecutor() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);

        assertThat(booking.negotiationPartyFor(executorId)).isEqualTo(NegotiationParty.EXECUTOR);
    }

    @Test
    void shouldResolveNegotiationPartyForAuthor() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);

        assertThat(booking.negotiationPartyFor(authorId)).isEqualTo(NegotiationParty.AUTHOR);
    }

    @Test
    void shouldThrowWhenResolvingNegotiationPartyIfNotInNegotiation() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(executorId, offerId, salary);

        assertThrows(InvalidBookingStatusException.class, () -> booking.negotiationPartyFor(executorId));
    }

    @Test
    void shouldThrowWhenResolvingNegotiationPartyForNonPartyUser() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        UserId strangerId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);

        assertThrows(BookingOwnershipException.class, () -> booking.negotiationPartyFor(strangerId));
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
        assertThat(events.get(0)).isInstanceOf(BookingCreated.class);
        BookingCreated created = (BookingCreated) events.get(0);
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
        assertThat(pulled.get(0)).isInstanceOf(BookingCreated.class);
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
    void shouldRegisterBookingAcceptedEventWhenAcceptFromInNegotiation() {
        UserId userId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);
        booking.beginNegotiation(salary, authorId);

        booking.accept();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
        List<DomainEvent> events = booking.getDomainEvents();
        assertThat(events).hasSize(3);
        assertThat(events.get(0)).isInstanceOf(BookingCreated.class);
        assertThat(events.get(1)).isInstanceOf(NegotiationStarted.class);
        assertThat(events.get(2)).isInstanceOf(BookingAccepted.class);
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

        booking.addPositionToNegotiation(counterSalary, NegotiationParty.EXECUTOR);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.IN_NEGOTIATION);
        assertThat(booking.getNegotiation()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenAddPositionToNegotiationWithoutOngoingNegotiation() {
        UserId executorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);

        assertThrows(InvalidBookingStatusException.class,
                () -> booking.addPositionToNegotiation(Salary.of(55000.0), NegotiationParty.EXECUTOR));
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

        assertThrows(InvalidBookingStatusException.class,
                () -> booking.addPositionToNegotiation(Salary.of(55000.0), NegotiationParty.EXECUTOR));
    }

    @Test
    void shouldThrowExceptionWhenAddPositionToNegotiationAfterBookingAccepted() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);
        booking.accept();

        assertThrows(InvalidBookingStatusException.class,
                () -> booking.addPositionToNegotiation(Salary.of(55000.0), NegotiationParty.EXECUTOR));
    }

    @Test
    void shouldAcceptBookingWhenStatusIsInNegotiation() {
        UserId userId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);
        booking.beginNegotiation(salary, authorId);

        booking.accept();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
    }

    @Test
    void shouldRejectBookingWhenStatusIsInNegotiation() {
        UserId userId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);
        booking.beginNegotiation(salary, authorId);

        booking.reject();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void shouldThrowInvalidBookingStatusExceptionWhenResolvingNegotiationPartyAfterNegotiationCanceled() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);
        booking.cancelNegotiation();

        assertThrows(InvalidBookingStatusException.class,
                () -> booking.negotiationPartyFor(executorId));
    }

    @Test
    void shouldThrowInvalidBookingStatusExceptionWhenResolvingNegotiationPartyAfterBookingAccepted() {
        UserId executorId = UserId.of(UUID.randomUUID());
        UserId authorId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(executorId, offerId, salary);
        booking.beginNegotiation(salary, authorId);
        booking.accept();

        assertThrows(InvalidBookingStatusException.class,
                () -> booking.negotiationPartyFor(authorId));
    }

    @Test
    void shouldNotChangeStatusWhenBeginNegotiationFailsDueToInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(InvalidBookingStatusException.class,
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

        assertThrows(InvalidBookingStatusException.class, booking::cancelNegotiation);

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
        assertThat(eventsAfterAccept.get(0)).isInstanceOf(BookingAccepted.class);

        assertThrows(InvalidBookingStatusException.class, booking::reject);

        assertThat(booking.getDomainEvents()).isEmpty();
    }
}
