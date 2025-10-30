package com.github.bovvver.bookingmanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

interface SqlNegotiationRepository extends Repository<NegotiationEntity, UUID>  {

    void save(NegotiationEntity negotiationEntity);
}
