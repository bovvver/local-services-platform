package com.github.bovvver.bookingmanagement;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
class BookingRepositoryImpl implements BookingRepository {
    private final SqlBookingRepository bookingRepository;
    private final SqlNegotiationRepository negotiationRepository;
    private final SqlNegotiationPositionRepository negotiationPositionRepository;

    @Override
    public void save(final Booking booking) {
        bookingRepository.save(BookingMapper.toEntity(booking));
    }

    @Override
    public void saveAll(final Iterable<Booking> bookings) {
        bookingRepository.saveAll(BookingMapper.toEntityList(bookings));
    }

    @Override
    public void saveNegotiation(final Booking booking, final Negotiation negotiation, final NegotiationPosition position) {
        bookingRepository.save(BookingMapper.toEntity(booking));
        negotiationRepository.save(NegotiationMapper.toEntity(negotiation));
        negotiationPositionRepository.save(NegotiationPositionMapper.toEntity(position));
    }
}
