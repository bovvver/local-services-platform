package com.github.bovvver.bookingmanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlBookingRepository extends Repository<BookingEntity, UUID> {

    void save(BookingEntity bookingEntity);

    BookingEntity findById(UUID id);
}
