package com.github.bovvver;

import com.github.bovvver.offermanagment.vo.BookingStatus;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA entity representing a booking record in the relational database.
 * <p>
 * This entity persists data for the domain-level {@link Booking}.
 * It ensures a user cannot create multiple bookings for the same offer
 * by enforcing a unique constraint on {@code (user_id, offer_id)}.
 * </p>
 *
 * <p>Key properties:</p>
 * <ul>
 *     <li>{@link #id} – primary key (UUID)</li>
 *     <li>{@link #userId} – identifier of the booking user</li>
 *     <li>{@link #offerId} – identifier of the booked offer</li>
 *     <li>{@link #status} – current booking state</li>
 *     <li>{@link #createdAt}, {@link #updatedAt} – managed automatically via auditing</li>
 * </ul>
 *
 * <p>Usage:</p>
 * <ul>
 *     <li>Created through constructors or mapping from the domain model.</li>
 *     <li>Updated only through repository methods; timestamps handled by Spring Data.</li>
 * </ul>
 */
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

    /**
     * Creates a new booking entity with default {@link BookingStatus#PENDING} state.
     *
     * @param id      unique identifier for the booking
     * @param userId  identifier of the user making the booking
     * @param offerId identifier of the offer being booked
     */
    BookingEntity(UUID id,
                  UUID userId,
                  UUID offerId) {
        this.id = id;
        this.userId = userId;
        this.offerId = offerId;
        this.status = BookingStatus.PENDING;
    }
}
