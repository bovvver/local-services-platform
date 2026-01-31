package com.github.bovvver.migrations;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import lombok.AllArgsConstructor;
import org.bson.Document;
import org.springframework.data.mongodb.core.MongoTemplate;

@AllArgsConstructor
@ChangeUnit(id = "outbox-initializer", order = "002", author = "bovvver")
public class OutboxInitializer {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void changeSet(MongoDatabase mongoDatabase) {
        mongoTemplate.createCollection("outbox_events");

        MongoCollection<Document> collection = mongoDatabase.getCollection("outbox_events");

        collection.createIndex(Indexes.ascending("authorId"));

        IndexOptions partialIndexOptions = new IndexOptions()
                .name("idx_outbox_status")
                .partialFilterExpression(Filters.eq("processed", false));
        collection.createIndex(Indexes.ascending("status"), partialIndexOptions);

        collection.createIndex(
                Indexes.ascending("occurredAt"),
                new IndexOptions().name("idx_outbox_occurred_at")
        );
    }

    @RollbackExecution
    public void rollback(MongoTemplate mongoTemplate) {
        mongoTemplate.dropCollection("outbox_events");
    }
}
