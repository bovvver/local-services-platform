package com.github.bovvver.bookingmanagement;

public interface BookingRepository {

    void save(Booking booking);

    void saveNegotiation(Booking booking, Negotiation negotiation, NegotiationPosition position);
}
