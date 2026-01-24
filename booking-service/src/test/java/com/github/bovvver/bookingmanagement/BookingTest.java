package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.*;
import org.junit.jupiter.api.Test;

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
        assertThat(booking.getUpdatedAt()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenUserIdIsNull() {
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        assertThrows(IllegalArgumentException.class, () -> Booking.create(null, offerId, salary));
    }

    @Test
    void shouldThrowExceptionWhenOfferIdIsNull() {
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

        booking.beginNegotiation(salary);

        assertThat(result).isNotNull();
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

        assertThrows(IllegalStateException.class, () -> booking.beginNegotiation(salary));
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNull() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(IllegalStateException.class, () -> booking.beginNegotiation(null));
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

        assertThrows(IllegalStateException.class, booking::accept);
    }

    @Test
    void shouldThrowExceptionWhenRejectWithInvalidStatus() {
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(userId, offerId, salary);
        booking.accept();

        assertThrows(IllegalStateException.class, booking::reject);
    }
}
