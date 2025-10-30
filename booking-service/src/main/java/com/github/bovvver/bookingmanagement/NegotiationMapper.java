package com.github.bovvver.bookingmanagement;

class NegotiationMapper {

    static NegotiationEntity toEntity(Negotiation negotiation) {
        return new NegotiationEntity(
                negotiation.getId().value(),
                negotiation.getBookingId().value(),
                negotiation.getStatus(),
                negotiation.getStartedAt(),
                negotiation.getLastUpdatedAt()
        );
    }
}
