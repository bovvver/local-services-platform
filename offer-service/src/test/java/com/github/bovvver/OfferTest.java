package com.github.bovvver;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.offermanagment.Offer;
import com.github.bovvver.offermanagment.vo.*;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OfferTest {

    @Test
    void shouldBookOfferWhenOfferIsOpen() {
        Offer offer = createOffer();
        UserId userId = UserId.of(UUID.randomUUID());
        BookingId bookingId = new BookingId(UUID.randomUUID());

        offer.book(UserId.of(UUID.randomUUID()), bookingId);

        assertThat(offer.getDomainEvents())
                .anyMatch(e -> e instanceof BookingDraftAccepted);
        assertThat(offer.getBookingIds()).contains(bookingId);
    }

    @Test
    void shouldRejectBookingWhenOfferIsAssigned() {
        Offer offer = createOffer();
        UserId executorId = UserId.of(UUID.randomUUID());
        offer.accept(executorId);
        UserId userId = UserId.of(UUID.randomUUID());
        BookingId bookingId = new BookingId(UUID.randomUUID());

        offer.book(userId, bookingId);

        assertThat(offer.getDomainEvents())
                .anyMatch(e -> e instanceof BookingDraftRejected);
        assertThat(offer.getBookingIds()).doesNotContain(bookingId);
    }

    @Test
    void shouldAllowMultipleBookingsWhenOfferIsOpen() {
        Offer offer = createOffer();
        BookingId firstBookingId = new BookingId(UUID.randomUUID());
        BookingId secondBookingId = new BookingId(UUID.randomUUID());

        offer.book(UserId.of(UUID.randomUUID()), firstBookingId);
        offer.book(UserId.of(UUID.randomUUID()), secondBookingId);

        assertThat(offer.getBookingIds()).containsExactlyInAnyOrder(firstBookingId, secondBookingId);
    }

    @Test
    void shouldChangeStatusToInNegotiationWhenNegotiating() {
        Offer offer = createOffer();

        offer.negotiate();

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.IN_NEGOTIATION);
    }

    @Test
    void shouldAllowBookingWhenOfferIsInNegotiation() {
        Offer offer = createOffer();
        offer.negotiate();
        BookingId bookingId = new BookingId(UUID.randomUUID());

        offer.book(UserId.of(UUID.randomUUID()), bookingId);

        assertThat(offer.getDomainEvents())
                .anyMatch(e -> e instanceof BookingDraftAccepted);
        assertThat(offer.getBookingIds()).contains(bookingId);
    }

    @Test
    void shouldThrowExceptionWhenNegotiatingAssignedOffer() {
        Offer offer = createOffer();
        offer.accept(UserId.of(UUID.randomUUID()));

        assertThatThrownBy(offer::negotiate)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is not open for booking");
    }

    @Test
    void shouldAssignExecutorWhenAccepting() {
        Offer offer = createOffer();
        UserId executorId = UserId.of(UUID.randomUUID());

        offer.accept(executorId);

        assertThat(offer.getExecutorId()).isEqualTo(executorId);
        assertThat(offer.getStatus()).isEqualTo(OfferStatus.ASSIGNED);
    }

    @Test
    void shouldThrowExceptionWhenAcceptingAssignedOffer() {
        Offer offer = createOffer();
        offer.accept(UserId.of(UUID.randomUUID()));

        assertThatThrownBy(() -> offer.accept(UserId.of(UUID.randomUUID())))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("is not open for booking");
    }

    @Test
    void shouldThrowExceptionWhenCreatingOfferWithNullAuthorId() {
        assertThatThrownBy(() -> Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                null,
                new Location(40.7128, -74.0060),
                Set.of(ServiceCategory.AUTOMOTIVE),
                Salary.of(1000.0)
        ))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("UserId cannot be null");
    }

    @Test
    void shouldCreateOfferWithOpenStatus() {
        Offer offer = createOffer();

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.OPEN);
        assertThat(offer.getExecutorId()).isNull();
        assertThat(offer.getBookingIds()).isEmpty();
    }

    @Test
    void shouldUpdateTimestampWhenBooking() {
        Offer offer = createOffer();
        LocalDateTime beforeBooking = offer.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        offer.book(UserId.of(UUID.randomUUID()), new BookingId(UUID.randomUUID()));

        assertThat(offer.getUpdatedAt()).isAfter(beforeBooking);
    }

    @Test
    void shouldUpdateTimestampWhenNegotiating() {
        Offer offer = createOffer();
        LocalDateTime beforeNegotiation = offer.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        offer.negotiate();

        assertThat(offer.getUpdatedAt()).isAfter(beforeNegotiation);
    }

    @Test
    void shouldUpdateTimestampWhenAccepting() {
        Offer offer = createOffer();
        LocalDateTime beforeAccepting = offer.getUpdatedAt();

        try {
            Thread.sleep(1);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        offer.accept(UserId.of(UUID.randomUUID()));

        assertThat(offer.getUpdatedAt()).isAfter(beforeAccepting);
    }

    @Test
    void shouldReturnAllDomainEventsAndClearTheList() {
        Offer offer = createOffer();
        UserId userId = UserId.of(UUID.randomUUID());
        BookingId bookingId = new BookingId(UUID.randomUUID());
        offer.book(userId, bookingId);

        List<DomainEvent> events = offer.pullDomainEvents();

        assertThat(events).hasSize(1);
        assertThat(events.getFirst()).isInstanceOf(BookingDraftAccepted.class);
        assertThat(offer.getDomainEvents()).isEmpty();
    }

    @Test
    void shouldReturnEmptyListWhenNoDomainEventsExist() {
        Offer offer = createOffer();

        List<DomainEvent> events = offer.pullDomainEvents();

        assertThat(events).isEmpty();
        assertThat(offer.getDomainEvents()).isEmpty();
    }

    private Offer createOffer() {
        return Offer.create(
                Title.of("Sample Title"),
                Description.of("Sample Description"),
                UserId.of(UUID.randomUUID()),
                new Location(
                        40.7128,
                        -74.0060
                ),
                Set.of(ServiceCategory.AUTOMOTIVE),
                Salary.of(1000.0)
        );
    }
}
