package com.github.bovvver;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlBookingRepository extends Repository<BookingEntity, UUID> {
}
