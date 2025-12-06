package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.results.BeginNegotiationResult;
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
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        assertThrows(IllegalArgumentException.class, () -> Booking.create(bookingId, null, offerId, salary));
    }

    @Test
    void shouldThrowExceptionWhenOfferIdIsNull() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);

        assertThrows(IllegalArgumentException.class, () -> Booking.create(bookingId, userId, null, salary));
    }

    @Test
    void shouldBeginNegotiationWhenStatusIsPending() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(bookingId, userId, offerId, salary);

        BeginNegotiationResult result = booking.beginNegotiation(salary);

        assertThat(result).isNotNull();
        assertThat(booking.getStatus()).isEqualTo(BookingStatus.IN_NEGOTIATION);
        assertThat(booking.getNegotiationId()).isNotNull();
    }

    @Test
    void shouldThrowExceptionWhenBeginNegotiationWithInvalidStatus() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(bookingId, userId, offerId, salary);
        booking.accept();

        assertThrows(IllegalStateException.class, () -> booking.beginNegotiation(salary));
    }

    @Test
    void shouldThrowExceptionWhenSalaryIsNull() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(bookingId, userId, offerId, salary);
        booking.accept();

        assertThrows(IllegalStateException.class, () -> booking.beginNegotiation(null));
    }

    @Test
    void shouldAcceptBookingWhenStatusIsPendingOrInNegotiation() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(bookingId, userId, offerId, salary);

        booking.accept();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.ACCEPTED);
    }

    @Test
    void shouldRejectBookingWhenStatusIsPendingOrInNegotiation() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(bookingId, userId, offerId, salary);

        booking.reject();

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.REJECTED);
    }

    @Test
    void shouldThrowExceptionWhenAcceptWithInvalidStatus() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(bookingId, userId, offerId, salary);
        booking.reject();

        assertThrows(IllegalStateException.class, booking::accept);
    }

    @Test
    void shouldThrowExceptionWhenRejectWithInvalidStatus() {
        BookingId bookingId = BookingId.of(UUID.randomUUID());
        UserId userId = UserId.of(UUID.randomUUID());
        OfferId offerId = OfferId.of(UUID.randomUUID());
        Salary salary = Salary.of(50000.0);
        Booking booking = Booking.create(bookingId, userId, offerId, salary);
        booking.accept();

        assertThrows(IllegalStateException.class, booking::reject);
    }
    
}
