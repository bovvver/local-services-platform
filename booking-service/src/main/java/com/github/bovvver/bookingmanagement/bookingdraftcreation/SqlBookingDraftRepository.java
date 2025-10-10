package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlBookingDraftRepository extends Repository<BookingDraftEntity, UUID> {

    void save(BookingDraftEntity entity);

    void deleteByBookingId(UUID id);
}
