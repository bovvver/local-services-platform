package com.github.bovvver;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BookingRepositoryImpl implements BookingRepository {
    private final SqlBookingRepository repository;
}
