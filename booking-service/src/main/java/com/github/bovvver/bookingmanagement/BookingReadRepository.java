package com.github.bovvver.bookingmanagement;

import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BookingReadRepository extends Repository<BookingEntity, UUID> {

    Optional<BookingEntity> findById(UUID id);

    boolean existsByOfferIdAndUserId(UUID offerId, UUID userId);

    List<BookingEntity> findAllByOfferIdAndIdIsNot(UUID offerId, UUID bookingId);

    List<BookingEntity> findAllByOfferId(UUID offerId);

    Optional<BookingEntity> findByOfferIdAndUserId(UUID offerId, UUID userId);
}
