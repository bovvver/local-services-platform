package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.NegotiationId;
import com.github.bovvver.bookingmanagement.vo.NegotiationPositionId;
import com.github.bovvver.bookingmanagement.vo.Salary;

class NegotiationPositionMapper {

    static NegotiationPositionEntity toEntity(NegotiationPosition position) {

        NegotiationPositionEntity entity = new NegotiationPositionEntity();
        entity.setId(position.getId().value());
        entity.setProposedSalary(position.getProposedSalary().value());
        entity.setProposedBy(position.getProposedBy());
        entity.setProposedAt(position.getProposedAt());

        return entity;
    }

    static NegotiationPosition toDomain(NegotiationPositionEntity entity) {
        return new NegotiationPosition(
                new NegotiationPositionId(entity.getId()),
                NegotiationId.of(entity.getNegotiation().getId()),
                new Salary(entity.getProposedSalary()),
                entity.getProposedBy(),
                entity.getProposedAt()
        );
    }
}
