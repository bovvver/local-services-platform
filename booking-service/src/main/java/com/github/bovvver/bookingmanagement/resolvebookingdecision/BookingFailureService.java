package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingMapper;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class BookingFailureService {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingWriteRepository;

    void handleBookingAcceptedFailure(UUID bookingId) {
        Booking booking = BookingMapper.toDomain(bookingReadRepository.findById(bookingId));
        booking.reject();
        bookingWriteRepository.save(booking);
    }
}
