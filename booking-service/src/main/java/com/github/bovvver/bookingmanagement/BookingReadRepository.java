package com.github.bovvver.bookingmanagement;

import org.springframework.data.repository.Repository;

import java.util.UUID;

public interface BookingReadRepository extends Repository<BookingEntity, UUID> {

    BookingEntity findById(UUID id);

    boolean existsByOfferIdAndUserId(UUID offerId, UUID userId);
}
