package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;

public interface BookingRepository {

    void save(Booking booking);

    Booking findById(BookingId bookingId);
}
