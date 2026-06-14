package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.BookingStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
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
 *     <li>{@link #salary} – final salary</li>
 *     <li>{@link #createdAt}, {@link #createdAt} – managed automatically via auditing</li>
 *     <li>{@link #updatedAt}, {@link #updatedAt} – managed automatically via auditing</li>
 *     <li>{@link #expiresAt}, {@link #expiresAt} – date of potential booking expiration</li>
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
@EntityListeners(AuditingEntityListener.class)
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private UUID offerId;

    @Setter
    @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private NegotiationEntity negotiation;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}
