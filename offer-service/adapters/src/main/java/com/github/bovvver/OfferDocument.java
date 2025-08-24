package com.github.bovvver;

import com.github.bovvver.vo.OfferStatus;
import com.github.bovvver.vo.ServiceCategory;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * MongoDB document representing an offer stored in the "offers" collection.
 * <p>
 * Contains information about the offer including title, description, author,
 * executor, associated bookings, location, service categories, salary, status,
 * and timestamps for creation and last modification.
 * </p>
 *
 * <p>Collections such as {@link #bookingIds} and {@link #serviceCategories} are
 * stored as embedded arrays in the document.</p>
 */
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

    /**
     * Constructs a new {@code OfferDocument} with the required fields.
     * <p>Default values:</p>
     * <ul>
     *     <li>{@link #executorId} = {@code null}</li>
     *     <li>{@link #bookingIds} initialized as empty set</li>
     *     <li>{@link #serviceCategories} initialized as empty set</li>
     *     <li>{@link #status} = {@link OfferStatus#OPEN}</li>
     * </ul>
     *
     * @param id          unique identifier of the offer
     * @param title       title of the offer
     * @param description detailed description
     * @param authorId    identifier of the user who created the offer
     * @param location    location of the offer
     * @param salary      salary associated with the offer
     */
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
