package com.github.bovvver.bookingmanagement;

class NegotiationPositionMapper {

    static NegotiationPositionEntity toEntity(NegotiationPosition position) {
        return new NegotiationPositionEntity(
                position.getId().value(),
                position.getNegotiationId().value(),
                position.getProposedSalary().value(),
                position.getProposedBy(),
                position.getProposedAt()
        );
    }
}
