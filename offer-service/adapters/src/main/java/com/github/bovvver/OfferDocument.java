package com.github.bovvver;

import com.github.bovvver.vo.OfferStatus;
import com.github.bovvver.vo.ServiceCategory;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@NoArgsConstructor
@Document(collection = "offers")
class OfferDocument {

    @Id
    private UUID id;
    private String title;
    private String description;
    private UUID authorId;
    private UUID executorId;
    private Set<UUID> bookingIds;
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
                double salary) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.authorId = authorId;
        this.executorId = null;
        this.bookingIds = new HashSet<>();
        this.location = location;
        this.serviceCategories = new HashSet<>();
        this.salary = salary;
        this.status = OfferStatus.OPEN;
    }
}
