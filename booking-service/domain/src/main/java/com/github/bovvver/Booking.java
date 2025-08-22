package com.github.bovvver;

import com.github.bovvver.vo.BookingId;
import com.github.bovvver.vo.BookingStatus;
import com.github.bovvver.vo.OfferId;
import com.github.bovvver.vo.UserId;

import java.time.LocalDateTime;

class Booking {

    private final BookingId id;
    private final UserId userId;
    private final OfferId offerId;
    private final BookingStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    Booking(BookingId id,
            UserId userId,
            OfferId offerId
    ) {
        this.id = id;
        this.userId = userId;
        this.offerId = offerId;
        this.status = BookingStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    static Booking create(
            BookingId id,
            UserId userId,
            OfferId offerId
    ) {
        return new Booking(id, userId, offerId);
    }
}