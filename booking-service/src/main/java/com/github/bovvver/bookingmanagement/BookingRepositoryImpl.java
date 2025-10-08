package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BookingRepositoryImpl implements BookingRepository {
    private final SqlBookingRepository repository;

    @Override
    public void save(final Booking booking) {
        repository.save(BookingMapper.toEntity(booking));
    }

    @Override
    public Booking findById(BookingId bookingId) {
        BookingEntity bookingEntity = repository.findById(bookingId.value());
        return BookingMapper.toDomain(bookingEntity);
    }
}
