package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class BookingFailureService {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingWriteRepository;

    void handleBookingAcceptedFailure(UUID bookingId) {
        BookingEntity bookingEntity = bookingReadRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalStateException("Booking with id " + bookingId + " not found"));

        Booking booking = BookingMapper.toDomain(bookingEntity);
        booking.reject();
        bookingWriteRepository.save(booking);
    }
}
