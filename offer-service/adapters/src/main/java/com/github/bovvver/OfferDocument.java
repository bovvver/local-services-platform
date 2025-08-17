package com.github.bovvver;

import com.github.bovvver.vo.OfferStatus;
import com.github.bovvver.vo.ServiceCategory;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Document(collection = "offers")
class OfferDocument {

    @Id
    private UUID id;
    private String title;
    private String description;
    private UUID authorId;
    private UUID executorId;
    private Set<UUID> bookingIds = new HashSet<>();
    private LocationEmbeddable location;
    private Set<ServiceCategory> serviceCategories;
    private double salary;
    private OfferStatus status;

    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate

    private LocalDateTime updatedAt;

    OfferDocument(UUID id,
                String title,
                String description,
                UUID authorId,
                LocationEmbeddable location,
                Set<ServiceCategory> serviceCategories,
                double salary) {
        this.id = id != null ? id : UUID.randomUUID();
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.executorId = null;
        this.bookingIds = new HashSet<>();
        this.location = location;
        this.serviceCategories = serviceCategories;
        this.salary = salary;
        this.status = OfferStatus.OPEN;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }
}
