package com.github.bovvver.profilemanagement;

import com.github.bovvver.vo.ServiceCategory;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * JPA entity for the {@code provider_profiles} table.
 */
@Entity
@Table(name = "provider_profiles")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class ProviderProfileEntity {

    @Id
    private UUID id;

    @Column(name = "user_id", nullable = false, unique = true)
    private UUID userId;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Column(length = 100)
    private String city;

    @Column(length = 2)
    private String country;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "provider_categories",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private Set<ServiceCategory> categories = new HashSet<>();
}
