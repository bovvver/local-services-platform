package com.github.bovvver.bookingmanagement.negotiation;

import com.github.bovvver.bookingmanagement.*;
import com.github.bovvver.bookingmanagement.outbox.OutboxRepository;
import com.github.bovvver.bookingmanagement.vo.Salary;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
class ResolveNegotiationDecisionService {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingRepository;
    private final OutboxRepository outboxRepository;
    private final NegotiationEventMapper negotiationEventMapper;

    @Transactional
    void beginNegotiation(UUID bookingId, BigDecimal salary) {

        BookingEntity bookingEntity = bookingReadRepository.findById(bookingId);
        Booking booking = BookingMapper.toDomain(bookingEntity);

        booking.beginNegotiation(new Salary(salary));
        bookingRepository.save(booking);

        booking.pullDomainEvents().stream()
                .map(negotiationEventMapper::toOutboxEvent)
                .filter(Objects::nonNull)
                .forEach(outboxRepository::save);
    }
}
