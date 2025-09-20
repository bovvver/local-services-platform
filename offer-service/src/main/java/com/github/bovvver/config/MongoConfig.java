package com.github.bovvver.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import org.bson.UuidRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
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
 *     <li>{@code OFFER_MONGO_INITDB_PORT} - database port</li>
 * </ul>
 *
 * <p>Connection string format:</p>
 * <pre>
 * mongodb://username:password@host:port/database?authSource=admin
 * </pre>
 */
@Configuration
@Profile("!test")
class MongoConfig {

    @Value("${OFFER_MONGO_INITDB_ROOT_USERNAME}")
    private String DB_USERNAME;

    @Value("${OFFER_MONGO_INITDB_ROOT_PASSWORD}")
    private String DB_PASSWORD;

    @Value("${OFFER_MONGO_INITDB_HOST}")
    private String DB_HOST;

    @Value("${OFFER_MONGO_INITDB_DATABASE}")
    private String DB_NAME;

    @Value("${OFFER_MONGO_INITDB_PORT}")
    private String DB_PORT;

    @Bean
    public MongoTemplate mongoTemplate() {
        String connectionString = String.format(
                "mongodb://%s:%s@%s:%s/%s?authSource=admin",
                DB_USERNAME, DB_PASSWORD, DB_HOST, DB_PORT, DB_NAME
        );

        MongoClientSettings settings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(connectionString))
                .uuidRepresentation(UuidRepresentation.STANDARD)
                .build();

        return new MongoTemplate(MongoClients.create(settings), DB_NAME);
    }
}