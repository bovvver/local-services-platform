package com.github.bovvver.usermanagement;

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
}
