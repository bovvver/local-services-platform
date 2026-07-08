package com.github.bovvver.usermanagement;

import com.github.bovvver.vo.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * JPA entity for the {@code users} table.
 *
 * <p>Contains only identity and account lifecycle fields.
 * Location, experience, service categories, awards, and ID collections
 * have been moved to dedicated aggregate tables.</p>
 */
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
    @Column(nullable = false)
    private UserStatus status;

    /**
     * Convenience constructor that defaults {@link UserStatus} to {@code UNVERIFIED}.
     * Used when creating a brand-new entity from a Keycloak registration event.
     */
    UserEntity(UUID id, String email, String firstName, String lastName) {
        this(id, email, firstName, lastName, UserStatus.UNVERIFIED);
    }
}
