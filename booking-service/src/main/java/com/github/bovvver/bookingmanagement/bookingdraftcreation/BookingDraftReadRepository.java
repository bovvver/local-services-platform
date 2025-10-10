package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

interface BookingDraftReadRepository extends Repository<BookingDraftEntity, UUID> {

    @Query("SELECT b.salary from BookingDraftEntity b WHERE b.bookingId = :bookingId")
    Double findSalaryByBookingId(@Param("bookingId") UUID bookingId);
}
