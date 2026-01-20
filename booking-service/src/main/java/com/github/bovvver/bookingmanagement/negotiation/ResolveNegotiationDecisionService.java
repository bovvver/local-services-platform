package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
import com.github.bovvver.bookingmanagement.vo.Salary;
import com.github.bovvver.contracts.BookingDecisionMadeEvent;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@RequiredArgsConstructor
class ResolveNegotiationDecisionService {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingRepository;
    private final OutboxRepository outboxRepository;
    private final NegotiationEventMapper negotiationEventMapper;

    @Transactional
    void beginNegotiation(BookingDecisionMadeEvent cmd) {

        BookingEntity bookingEntity = bookingReadRepository.findById(cmd.bookingId());
        Booking booking = BookingMapper.toDomain(bookingEntity);

        booking.beginNegotiation(new Salary(cmd.salary()));
        bookingRepository.save(booking);

        booking.pullDomainEvents().stream()
                .map(negotiationEventMapper::toOutboxEvent)
                .filter(Objects::nonNull)
                .forEach(outboxRepository::save);
    }
}
