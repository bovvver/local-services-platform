package com.github.bovvver.config;

import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;

/**
 * Spring configuration class for MongoDB connection.
 * <p>
 * Reads connection parameters from environment variables and exposes
 * a {@link MongoTemplate} bean for database operations.
 * </p>
 *
 * <p>Environment variables used:</p>
 * <ul>
 *     <li>{@code OFFER_MONGO_INITDB_ROOT_USERNAME} - MongoDB root username</li>
 *     <li>{@code OFFER_MONGO_INITDB_ROOT_PASSWORD} - MongoDB root password</li>
 *     <li>{@code OFFER_MONGO_INITDB_HOST} - hostname or IP address of MongoDB server</li>
 *     <li>{@code OFFER_MONGO_INITDB_DATABASE} - database name</li>
 * </ul>
 *
 * <p>Connection string format:</p>
 * <pre>
 * mongodb://username:password@host:27017/database?authSource=admin
 * </pre>
 */
@Configuration
class MongoConfig {

    @Value("${OFFER_MONGO_INITDB_ROOT_USERNAME}")
    private String DB_USERNAME;

    @Value("${OFFER_MONGO_INITDB_ROOT_PASSWORD}")
    private String DB_PASSWORD;

    @Value("${OFFER_MONGO_INITDB_HOST}")
    private String DB_HOST;

    @Value("${OFFER_MONGO_INITDB_DATABASE}")
    private String DB_NAME;

    @Bean
    public MongoTemplate mongoTemplate() {
        String connectionString = String.format(
                "mongodb://%s:%s@%s:27017/%s?authSource=admin",
                DB_USERNAME, DB_PASSWORD, DB_HOST, DB_NAME
        );
        return new MongoTemplate(MongoClients.create(connectionString), DB_NAME);
    }
}