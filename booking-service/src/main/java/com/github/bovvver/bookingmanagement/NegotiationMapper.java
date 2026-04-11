package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingId;
import com.github.bovvver.bookingmanagement.vo.NegotiationId;
import com.github.bovvver.bookingmanagement.vo.UserId;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

class NegotiationMapper {

    static NegotiationEntity toEntity(Negotiation negotiation, BookingEntity bookingEntity) {
        if (negotiation == null) {
            return null;
        }

        NegotiationEntity entity = new NegotiationEntity();
        entity.setId(negotiation.getId().value());
        entity.setBooking(bookingEntity);
        entity.setOfferAuthorId(negotiation.getOfferAuthorId() != null ? negotiation.getOfferAuthorId().value() : null);
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
                UserId.of(entity.getOfferAuthorId()),
                entity.getPositions().stream()
                        .map(NegotiationPositionMapper::toDomain)
                        .collect(Collectors.toCollection(ArrayList::new)),
                entity.getStatus(),
                entity.getStartedAt(),
                entity.getLastUpdatedAt()
        );
    }
}
