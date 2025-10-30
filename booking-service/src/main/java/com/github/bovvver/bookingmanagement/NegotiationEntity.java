package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.NegotiationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Table(
        name = "negotiations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "negotiations_bookings_unique",
                        columnNames = {"id", "booking_id"}
                )
        }
)
@AllArgsConstructor
@NoArgsConstructor
class NegotiationEntity {

    @Id
    private UUID id;

    @Column(name = "booking_id", nullable = false)
    private UUID bookingId;

    @Enumerated(EnumType.STRING)
    private NegotiationStatus status;

    @CreatedDate
    @Column(name = "started_at", nullable = false, updatable = false)
    private LocalDateTime startedAt;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime lastUpdatedAt;
}
