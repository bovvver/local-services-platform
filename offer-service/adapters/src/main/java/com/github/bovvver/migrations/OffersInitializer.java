package com.github.bovvver.migrations;

import com.mongodb.client.model.Indexes;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;

@AllArgsConstructor
@ChangeUnit(id="offers-initializer", order = "001", author = "bovvver")
class OffersInitializer {

    private final MongoTemplate mongoTemplate;

    @Execution
    public void changeSet() {
        mongoTemplate.createCollection("offers");
        mongoTemplate.getCollection("offers")
                .createIndex(Indexes.ascending("authorId"));
    }
}