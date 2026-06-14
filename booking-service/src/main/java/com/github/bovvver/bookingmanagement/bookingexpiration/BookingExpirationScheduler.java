package com.github.bovvver.bookingmanagement.bookingexpiration;

import com.github.bovvver.bookingmanagement.*;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
class BookingExpirationScheduler {

    private final BookingReadRepository bookingReadRepository;
    private final BookingRepository bookingRepository;

    @Scheduled(cron = "0 */5 * * * *")
    @Transactional
    void run() {
        LocalDateTime now = LocalDateTime.now();
        List<BookingEntity> expiredBookingEntities = bookingReadRepository.findExpiredBookings(now);
        List<Booking> expiredBookings = BookingMapper.toDomainList(expiredBookingEntities);

        expiredBookings.forEach(el -> el.expire(now));
        bookingRepository.saveAll(expiredBookings);
    }
}
