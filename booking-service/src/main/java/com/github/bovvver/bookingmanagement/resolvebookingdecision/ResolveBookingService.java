package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingMapper;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.contracts.BookingDecisionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private final BookingRepository bookingRepository;
    private final BookingReadRepository bookingReadRepository;

    void resolveBooking(BookingDecisionCommand cmd) {

        Booking booking = BookingMapper.toDomain(bookingReadRepository.findById(cmd.bookingId()));

        switch (cmd.status()) {
            case ACCEPTED -> booking.accept();
            case REJECTED -> booking.reject();
            default -> throw new IllegalArgumentException(
                    "Status %s is not supported.".formatted(cmd.status())
            );
        }
        bookingRepository.save(booking);
    }
}
