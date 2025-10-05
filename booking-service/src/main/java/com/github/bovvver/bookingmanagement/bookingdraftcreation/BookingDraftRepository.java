package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.Salary;

interface BookingDraftRepository {

    void save(BookingDraft draft);

    void delete(BookingId bookingId);

    Salary findSalaryByBookingId(BookingId bookingId);
}
