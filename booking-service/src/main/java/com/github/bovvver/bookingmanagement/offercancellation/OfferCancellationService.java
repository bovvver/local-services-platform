package com.github.bovvver.bookingmanagement.offercancellation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingMapper;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class OfferCancellationService {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingWriteRepository;

    void cancelByAuthor(UUID offerId) {
        List<Booking> bookings = BookingMapper.toDomainList(
                bookingReadRepository.findAllByOfferId(offerId)
        );

        if (bookings.isEmpty()) {
            return;
        }
        bookings.forEach(Booking::cancelByAuthor);
        bookingWriteRepository.saveAll(bookings);
    }

    void cancelByExecutor(UUID offerId, UUID executorId) {
        Optional<Booking> booking = bookingReadRepository.findByOfferIdAndUserId(offerId, executorId)
                .map(BookingMapper::toDomain);

        if (booking.isEmpty()) {
            return;
        }
        Booking resolved = booking.get();
        resolved.cancelByExecutor();
        bookingWriteRepository.save(resolved);
    }
}
