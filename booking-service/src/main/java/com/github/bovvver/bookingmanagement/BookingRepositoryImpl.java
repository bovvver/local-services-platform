package com.github.bovvver.bookingmanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BookingRepositoryImpl implements BookingRepository {
    private final SqlBookingRepository bookingRepository;

    @Override
    public void save(final Booking booking) {
        bookingRepository.save(BookingMapper.toEntity(booking));
    }

    @Override
    public void saveAll(final Iterable<Booking> bookings) {
        bookingRepository.saveAll(BookingMapper.toEntityList(bookings));
    }
}
