package com.github.bovvver.migrations;

import com.mongodb.client.model.Updates;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.Collections;

import static com.mongodb.client.model.Filters.*;

@AllArgsConstructor
@ChangeUnit(id = "offers-workproofs-field-initializer", order = "003", author = "bovvver")
public class WorkProofsFieldInitializer {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void changeSet() {
        mongoTemplate.getCollection("offers")
                .updateMany(
                        or(exists("executionDetails.workProofs", false), eq("executionDetails.workProofs", null)),
                        Updates.set("executionDetails.workProofs", Collections.emptyList())
                );
    }

    @RollbackExecution
    public void rollback() {
        mongoTemplate.getCollection("offers")
                .updateMany(
                        eq("executionDetails.workProofs", Collections.emptyList()),
                        new Document("$unset", new Document("executionDetails.workProofs", ""))
                );
    }
}
