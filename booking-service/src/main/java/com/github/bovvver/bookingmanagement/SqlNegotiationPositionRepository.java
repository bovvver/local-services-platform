package com.github.bovvver.bookingmanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlNegotiationPositionRepository extends Repository<NegotiationPositionEntity, UUID> {

    void save(NegotiationPositionEntity negotiationPositionEntity);
}
