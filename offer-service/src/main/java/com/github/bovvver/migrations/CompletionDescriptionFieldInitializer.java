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
 * Backwards-compatible migration: ensures field `completionDescription` exists in `offers` collection.
 *
 * We keep it as null by default for existing offers.
 */
@AllArgsConstructor
@ChangeUnit(id = "offers-completion-description-field-initializer", order = "004", author = "bovvver")
public class CompletionDescriptionFieldInitializer {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void changeSet() {
        // set to null for documents where the field does not exist
        mongoTemplate.getCollection("offers")
                .updateMany(
                        exists("completionDescription", false),
                        Updates.set("completionDescription", null)
                );
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getCollection("offers")
                .updateMany(
                        exists("completionDescription", true),
                        new Document("$unset", new Document("completionDescription", ""))
                );
    }
}

