package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.infrastructure.BookingNotFoundException;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
import com.github.bovvver.bookingmanagement.resolvebookingdecision.BookingDecisionMapper;
import com.github.bovvver.bookingmanagement.vo.NegotiationPositionId;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.bookingmanagement.vo.UserId;
import com.github.bovvver.shared.CurrentUser;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
class NegotiationProcessService {

    private final CurrentUser currentUser;
    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingWriteRepository;
    private final BookingDecisionMapper bookingDecisionMapper;
    private final OutboxRepository outboxRepository;

    @Transactional
    void makeProposal(final UUID bookingId, final NegotiationProposalRequest request) {
        withBooking(bookingId, booking -> booking.addPositionToNegotiation(new Salary(request.proposedSalary()), currentUserId()));
    }

    @Transactional
    void acceptProposal(final UUID bookingId, final UUID positionId) {
        Booking booking = withBookingAndPositionId(bookingId, positionId,
                (b, negotiationPositionId) -> b.acceptNegotiationProposal(currentUserId(), negotiationPositionId));

        booking.pullDomainEvents().stream()
                .map(bookingDecisionMapper::toOutboxEvent)
                .filter(Objects::nonNull)
                .forEach(outboxRepository::save);
    }

    @Transactional
    void rejectProposal(final UUID bookingId, final UUID positionId) {
        withBookingAndPositionId(bookingId, positionId,
                (b, negotiationPositionId) -> b.rejectNegotiationProposal(currentUserId(), negotiationPositionId));
    }

    private void withBooking(UUID bookingId, Consumer<Booking> action) {
        Booking booking = getBookingById(bookingId);
        action.accept(booking);
        bookingWriteRepository.save(booking);
    }

    private Booking withBookingAndPositionId(UUID bookingId, UUID positionId, BiConsumer<Booking, NegotiationPositionId> action) {
        Booking booking = getBookingById(bookingId);
        NegotiationPositionId negotiationPositionId = NegotiationPositionId.of(positionId);
        action.accept(booking, negotiationPositionId);
        bookingWriteRepository.save(booking);
        return booking;
    }

    private Booking getBookingById(UUID bookingId) {
        BookingEntity bookingEntity = bookingReadRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));
        return BookingMapper.toDomain(bookingEntity);
    }

    private UserId currentUserId() {
        return UserId.of(currentUser.getId().value());
    }
}
