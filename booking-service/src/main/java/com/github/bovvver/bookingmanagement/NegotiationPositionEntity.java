package com.github.bovvver.bookingmanagement;

import com.github.bovvver.bookingmanagement.vo.NegotiationParty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "negotiation_positions")
@AllArgsConstructor
@NoArgsConstructor
class NegotiationPositionEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negotiation_id", nullable = false)
    private NegotiationEntity negotiation;

    @Column(name = "proposed_salary", precision = 10, scale = 2, nullable = false)
    private BigDecimal proposedSalary;

    @Column(name = "proposed_by", nullable = false)
    @Enumerated(EnumType.STRING)
    private NegotiationParty proposedBy;

    @Column(name = "proposed_at", nullable = false)
    private LocalDateTime proposedAt;
}
