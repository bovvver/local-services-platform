package com.github.bovvver.usermanagement;

import com.github.bovvver.vo.VerificationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class UserEntity {

    @Id
    private UUID id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String firstName;

    @Column(nullable = false)
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "identity_status", nullable = false, length = 20)
    private VerificationStatus identityStatus;

    @Column(name = "proof_url")
    private String proofUrl;

    @Column(name = "proof_uploaded_at")
    private java.time.LocalDateTime proofUploadedAt;
}
