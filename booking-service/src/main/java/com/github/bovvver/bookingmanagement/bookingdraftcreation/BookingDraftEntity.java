package com.github.bovvver.bookingmanagement.bookingdraftcreation;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(
        name = "booking_drafts",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "booking_drafts_booking_offer_unique",
                        columnNames = {"booking_id", "offer_id"}
                )
        })
class BookingDraftEntity {

    @Id
    private UUID bookingId;

    @Column(nullable = false)
    private UUID offerId;

    @Column(nullable = false)
    private UUID userId;

    @Min(value = 0)
    @Column(precision = 10, scale = 2)
    private BigDecimal salary;

    @CreatedDate
    private LocalDateTime createdAt;
}
