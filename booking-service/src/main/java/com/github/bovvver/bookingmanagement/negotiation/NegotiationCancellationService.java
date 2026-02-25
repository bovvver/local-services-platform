package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingMapper;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class NegotiationCancellationService {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingWriteRepository;

    @Transactional
    void cancelNegotiation(UUID bookingId) {
        Booking booking = BookingMapper.toDomain(bookingReadRepository.findById(bookingId));
        booking.cancelNegotiation();
        bookingWriteRepository.save(booking);
    }
}
