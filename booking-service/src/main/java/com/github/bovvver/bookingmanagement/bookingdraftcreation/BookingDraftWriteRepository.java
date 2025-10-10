package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.bookingmanagement.vo.BookingId;

interface BookingDraftWriteRepository {

    void save(BookingDraft draft);

    void delete(BookingId bookingId);
}
