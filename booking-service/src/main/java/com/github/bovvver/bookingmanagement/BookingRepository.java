package com.github.bovvver.bookingmanagement;

public interface BookingRepository {

    void save(Booking booking);

    void saveAll(Iterable<Booking> bookings);

    void saveNegotiation(Booking booking, Negotiation negotiation, NegotiationPosition position);
}
