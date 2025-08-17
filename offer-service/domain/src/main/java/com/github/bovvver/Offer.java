package com.github.bovvver;

import com.github.bovvver.vo.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

class Offer {

    private final OfferId id;
    private final Title title;
    private final Description description;
    private final UserId authorId;
    private final UserId executorId;
    private final Set<BookingId> bookingIds;
    private final Location location;
    private final Set<ServiceCategory> serviceCategories;
    private final Salary salary;
    private final OfferStatus status;
    private final LocalDateTime createdAt;
    private final LocalDateTime updatedAt;

    Offer(OfferId id,
          Title title,
          Description description,
          UserId authorId,
          Location location,
          Set<ServiceCategory> serviceCategories,
          Salary salary) {
        this.id = id;
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

    static Offer create(
            OfferId id,
            Title title,
            Description description,
            UserId authorId,
            Location location,
            Set<ServiceCategory> serviceCategories,
            Salary salary
    ) {
        return new Offer(id, title, description, authorId, location, serviceCategories, salary);
    }
}
