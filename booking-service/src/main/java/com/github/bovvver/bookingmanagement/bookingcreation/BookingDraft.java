package com.github.bovvver.bookingmanagement.bookingcreation;

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

    BookingDraft(final BookingId bookingId,
                 final OfferId offerId,
                 final UserId userId,
                 final Salary salary) {
        this.bookingId = bookingId;
        this.offerId = offerId;
        this.userId = userId;
        this.salary = salary;
        this.createdAt = Instant.now();
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

    Salary getSalary() {
        return salary;
    }

    UserId getUserId() {
        return userId;
    }

    OfferId getOfferId() {
        return offerId;
    }

    BookingId getBookingId() {
        return bookingId;
    }
}
