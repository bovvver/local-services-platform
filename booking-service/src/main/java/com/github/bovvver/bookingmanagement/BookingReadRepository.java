package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.UUID;

public interface BookingReadRepository extends Repository<BookingEntity, UUID> {

    BookingEntity findById(UUID id);

    boolean existsByOfferIdAndUserId(UUID offerId, UUID userId);

    List<BookingEntity> findAllByOfferIdAndStatusNotIn(UUID offerId, List<BookingStatus> statuses);
}
