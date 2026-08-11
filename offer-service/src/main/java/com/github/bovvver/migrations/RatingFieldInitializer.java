package com.github.bovvver.migrations;

import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import static com.mongodb.client.model.Filters.exists;

/**
 * Backwards-compatible migration: ensures field `rating` exists in `offers` collection.
 * <p>
 * We keep it as null by default for existing offers.
 */
@AllArgsConstructor
@ChangeUnit(id = "offers-rating-field-initializer", order = "005", author = "bovvver")
public class RatingFieldInitializer {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void changeSet() {
        // set to null for documents where the field does not exist
        mongoTemplate.getCollection("offers")
                .updateMany(
                        exists("rating", false),
                        Updates.set("rating", null)
                );
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getCollection("offers")
                .updateMany(
                        exists("rating", true),
                        new Document("$unset", new Document("rating", ""))
                );
    }
}
