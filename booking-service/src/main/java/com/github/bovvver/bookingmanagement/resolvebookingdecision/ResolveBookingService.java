package com.github.bovvver.bookingmanagement.resolvebookingdecision;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.contracts.BookingDecisionCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ResolveBookingService {

    private final BookingRepository bookingRepository;

    void resolveBooking(BookingDecisionCommand cmd) {

        Booking booking = bookingRepository.findById(BookingId.of(cmd.bookingId()));

        switch (cmd.status()) {
            case ACCEPTED -> booking.accept();
            case REJECTED -> booking.reject();
            case NEGOTIATE -> {

            }
        }
    }
}
