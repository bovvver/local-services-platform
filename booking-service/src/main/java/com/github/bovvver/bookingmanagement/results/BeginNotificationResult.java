package com.github.bovvver.bookingmanagement.results;

import com.github.bovvver.bookingmanagement.Booking;
import com.github.bovvver.bookingmanagement.Negotiation;
import com.github.bovvver.bookingmanagement.NegotiationPosition;

public record BeginNotificationResult(Booking booking, Negotiation negotiation, NegotiationPosition position) {
}
