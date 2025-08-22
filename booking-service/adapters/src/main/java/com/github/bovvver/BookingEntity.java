package com.github.bovvver;

import com.github.bovvver.vo.BookingStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "bookings",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "bookings_user_offer_unique",
                        columnNames = {"user_id", "offer_id"}
                )
        }
)
@NoArgsConstructor
class BookingEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID offerId;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    BookingEntity(UUID id,
                  UUID userId,
                  UUID offerId) {
        this.id = id;
        this.userId = userId;
        this.offerId = offerId;
        this.status = BookingStatus.PENDING;
    }
}
