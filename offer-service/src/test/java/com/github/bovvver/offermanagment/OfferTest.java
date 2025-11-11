package com.github.bovvver.offermanagment;

import com.github.bovvver.event.DomainEvent;
import com.github.bovvver.offermanagment.vo.*;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OfferTest {

    @Test
    void shouldAcceptOfferWhenOpen() {
        Offer offer = createOffer();
        UserId executorId = UserId.of(UUID.randomUUID());

        offer.accept(executorId);

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.ASSIGNED);
        assertThat(offer.getExecutorId()).isEqualTo(executorId);
    }

    @Test
    void shouldThrowWhenAcceptingOfferThatIsClosed() {
        Offer offer = createOffer();
        offer.accept(UserId.of(UUID.randomUUID()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, () ->
                offer.accept(UserId.of(UUID.randomUUID()))
        );

        assertThat(exception.getMessage()).isEqualTo("Offer %s is not open for booking.".formatted(offer.getId().value()));
    }

    @Test
    void shouldBookOfferWhenOpen() {
        Offer offer = createOffer();
        UserId userId = UserId.of(UUID.randomUUID());
        BookingId bookingId = BookingId.of(UUID.randomUUID());

        DomainEvent event = offer.book(userId, bookingId);

        assertThat(event).isInstanceOf(BookingDraftAccepted.class);
        assertThat(offer.getBookingIds()).contains(bookingId);
    }

    @Test
    void shouldRejectBookingWhenOfferIsClosed() {
        Offer offer = createOffer();
        offer.accept(UserId.of(UUID.randomUUID()));
        UserId userId = UserId.of(UUID.randomUUID());
        BookingId bookingId = BookingId.of(UUID.randomUUID());

        DomainEvent event = offer.book(userId, bookingId);

        assertThat(event).isInstanceOf(BookingDraftRejected.class);
        assertThat(offer.getBookingIds()).doesNotContain(bookingId);
    }

    @Test
    void shouldNegotiateOfferWhenOpen() {
        Offer offer = createOffer();

        offer.negotiate();

        assertThat(offer.getStatus()).isEqualTo(OfferStatus.IN_NEGOTIATION);
    }

    @Test
    void shouldThrowWhenNegotiatingOfferThatIsClosed() {
        Offer offer = createOffer();
        offer.accept(UserId.of(UUID.randomUUID()));

        IllegalStateException exception = assertThrows(IllegalStateException.class, offer::negotiate);

        assertThat(exception.getMessage()).isEqualTo("Offer %s is not open for booking.".formatted(offer.getId().value()));
    }

    private Offer createOffer() {
        return Offer.create(
                new Title("Sample Offer"),
                new Description("This is a sample offer description."),
                UserId.of(UUID.randomUUID()),
                new Location(10, 30),
                Set.of(ServiceCategory.CLEANING),
                new Salary(100.0)
        );
    }
}
