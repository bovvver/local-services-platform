package com.github.bovvver.bookingmanagement.bookingcreation;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

interface SqlBookingDraftRepository extends Repository<BookingDraftEntity, UUID> {

    void save(BookingDraftEntity entity);

    void deleteByBookingId(UUID id);

    @Query("SELECT b.salary from BookingDraftEntity b WHERE b.bookingId = :bookingId")
    Double findSalaryByBookingId(@Param("bookingId") UUID bookingId);
}
