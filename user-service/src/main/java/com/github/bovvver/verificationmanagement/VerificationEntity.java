package com.github.bovvver.verificationmanagement;

import com.github.bovvver.vo.VerificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA entity for the {@code verification} table.
 * The userId is both the primary key and a logical FK to users.id —
 * exactly one verification record exists per user.
 */
@Entity
@Table(name = "verification")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class VerificationEntity {

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_status", nullable = false, length = 20)
    private VerificationStatus identityStatus;
}
