package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.infrastructure.BookingNotFoundException;
import com.github.bovvver.bookingmanagement.vo.NegotiationPositionId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
class NegotiationProcessService {

    private final CurrentUser currentUser;
    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingWriteRepository;

    @Transactional
    void makeProposal(final UUID bookingId, final NegotiationProposalRequest request) {
        updateBookingAsCurrentUser(bookingId, booking ->
                booking.addPositionToNegotiation(new Salary(request.proposedSalary()), currentUserId())
        );
    }

    @Transactional
    void acceptProposal(final UUID bookingId, final UUID positionId) {
        updateBookingAsCurrentUser(bookingId, booking -> {
            NegotiationPositionId negotiationPositionId = NegotiationPositionId.of(positionId);
            booking.acceptNegotiationProposal(currentUserId(), negotiationPositionId);
        });
    }

    @Transactional
    void rejectProposal(final UUID bookingId, final UUID positionId) {
        updateBookingAsCurrentUser(bookingId, booking -> {
            NegotiationPositionId negotiationPositionId = NegotiationPositionId.of(positionId);
            booking.rejectNegotiationProposal(currentUserId(), negotiationPositionId);
        });
    }

    private void updateBookingAsCurrentUser(UUID bookingId, Consumer<Booking> update) {
        BookingEntity bookingEntity = bookingReadRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        Booking booking = BookingMapper.toDomain(bookingEntity);
        update.accept(booking);
        bookingWriteRepository.save(booking);
    }

    private UserId currentUserId() {
        return UserId.of(currentUser.getId().value());
    }
}
