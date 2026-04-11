package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.BookingEntity;
import com.github.bovvver.bookingmanagement.BookingMapper;
import com.github.bovvver.bookingmanagement.BookingReadRepository;
import com.github.bovvver.bookingmanagement.BookingRepository;
import com.github.bovvver.bookingmanagement.infrastructure.BookingNotFoundException;
import com.github.bovvver.bookingmanagement.vo.NegotiationParty;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
class NegotiationProcessService {

    private final CurrentUser currentUser;
    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingWriteRepository;

    @Transactional
    void makeProposal(final UUID bookingId, final NegotiationProposalRequest request) {
        BookingEntity bookingEntity = bookingReadRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        Booking booking = BookingMapper.toDomain(bookingEntity);
        UserId currentUserId = UserId.of(currentUser.getId().value());
        NegotiationParty negotiationParty = booking.negotiationPartyFor(currentUserId);

        booking.addPositionToNegotiation(new Salary(request.proposedSalary()), negotiationParty);
        bookingWriteRepository.save(booking);
    }
}
