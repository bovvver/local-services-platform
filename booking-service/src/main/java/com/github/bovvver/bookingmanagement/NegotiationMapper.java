package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.NegotiationId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

class NegotiationMapper {

    static NegotiationEntity toEntity(Negotiation negotiation) {
        if (negotiation == null) {
            return null;
        }

        NegotiationEntity entity = new NegotiationEntity();
        entity.setId(negotiation.getId().value());
        entity.setStatus(negotiation.getStatus());
        entity.setStartedAt(negotiation.getStartedAt());
        entity.setLastUpdatedAt(negotiation.getLastUpdatedAt());

        List<NegotiationPositionEntity> positionEntities = new ArrayList<>();

        for (NegotiationPosition position : negotiation.getPositions()) {
            NegotiationPositionEntity positionEntity =
                    NegotiationPositionMapper.toEntity(position);
            positionEntity.setNegotiation(entity);
            positionEntities.add(positionEntity);
        }

        entity.setPositions(positionEntities);
        return entity;
    }

    static Negotiation toDomain(NegotiationEntity entity, UUID bookingId) {
        if (entity == null) {
            return null;
        }
        return new Negotiation(
                new NegotiationId(entity.getId()),
                BookingId.of(bookingId),
                entity.getPositions().stream()
                        .map(NegotiationPositionMapper::toDomain)
                        .toList(),
                entity.getStatus(),
                entity.getStartedAt(),
                entity.getLastUpdatedAt()
        );
    }
}
