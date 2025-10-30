package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.results.BeginNotificationResult;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.contracts.BookingDecisionCommand;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
class ResolveNegotiationDecisionService {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingRepository;

    @Transactional
    void beginNegotiation(BookingDecisionCommand cmd) {

        BookingEntity bookingEntity = bookingReadRepository.findById(cmd.bookingId());
        Booking booking = BookingMapper.toDomain(bookingEntity);

        BeginNotificationResult negotiationData = booking.beginNegotiation(Salary.of(cmd.salary()));

        bookingRepository.saveNegotiation(
                negotiationData.booking(),
                negotiationData.negotiation(),
                negotiationData.position()
        );
    }
}
