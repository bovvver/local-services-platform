package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.Instant;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
class BookingDraftEntity {

    @Id
    private UUID bookingId;

    @Column(nullable = false)
    private UUID offerId;

    @Column(nullable = false)
    private UUID userId;

    @Min(value = 0)
    @Column(precision = 10, scale = 2)
    private Double salary;

    @CreatedDate
    private Instant createdAt;

    BookingDraftEntity(final UUID bookingId,
                       final UUID offerId,
                       final UUID userId,
                       final Double salary) {
        this(bookingId, offerId, userId, salary, Instant.now());
    }
}
