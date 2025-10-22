package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.OfferId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;

import java.time.Instant;

class BookingDraft {

    private final BookingId bookingId;
    private final OfferId offerId;
    private final UserId userId;
    private final Salary salary;
    private final Instant createdAt;

    BookingDraft(final BookingId bookingId, final OfferId offerId, final UserId userId, final Salary salary, final Instant createdAt) {
        this.bookingId = bookingId;
        this.offerId = offerId;
        this.userId = userId;
        this.salary = salary;
        this.createdAt = createdAt;
    }

    BookingDraft(final BookingId bookingId,
                 final OfferId offerId,
                 final UserId userId,
                 final Salary salary) {
        this(bookingId, offerId, userId, salary, Instant.now());
    }

    static BookingDraft create(
            BookingId bookingId,
            OfferId offerId,
            UserId userId,
            Salary salary
    ) {
        return new BookingDraft(
                bookingId,
                offerId,
                userId,
                salary
        );
    }

    BookingId getBookingId() {
        return bookingId;
    }

    OfferId getOfferId() {
        return offerId;
    }

    UserId getUserId() {
        return userId;
    }

    Salary getSalary() {
        return salary;
    }

    Instant getCreatedAt() {
        return createdAt;
    }
}
