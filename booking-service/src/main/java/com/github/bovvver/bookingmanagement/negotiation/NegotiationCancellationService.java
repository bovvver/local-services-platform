package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.infrastructure.BookingNotFoundException;
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
        BookingEntity bookingEntity = bookingReadRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        Booking booking = BookingMapper.toDomain(bookingEntity);
        booking.cancelNegotiation();
        bookingWriteRepository.save(booking);
    }
}
